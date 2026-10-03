package dev.jiaowh.comicreader.data.net

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.webkit.CookieManager
import android.webkit.WebView
import android.webkit.WebViewClient
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Thrown when batcave.biz's bot protection (Cloudflare or its "DLE Guard") couldn't be
 * passed automatically. The UI then offers a visible WebView so the user can complete it.
 */
class SiteProtectionException : IOException("BatCave wants to check you're human. Tap Verify to continue.")

/** Shares cookies between OkHttp and WebView, so clearance earned in a WebView applies to app requests. */
class WebViewCookieJar : CookieJar {
    private val manager get() = CookieManager.getInstance()

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        val target = url.toString()
        cookies.forEach { manager.setCookie(target, it.toString()) }
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        val raw = manager.getCookie(url.toString()) ?: return emptyList()
        return raw.split(';').mapNotNull { Cookie.parse(url, it.trim()) }
    }
}

/** Adds browser-like headers. The UA must match the WebView's or clearance cookies are rejected. */
class SiteHeadersInterceptor(private val userAgent: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val request = original.newBuilder()
            .header("User-Agent", userAgent)
            .header("Accept-Language", "en-US,en;q=0.9")
            .apply { if (original.header("Referer") == null) header("Referer", "${Site.BASE_URL}/") }
            .build()
        return chain.proceed(request)
    }
}

/** Detects a protection page, tries to clear it with a hidden WebView, then retries the request once. */
class SiteProtectionInterceptor(private val solver: ChallengeSolver) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)
        if (!isChallenge(response)) return response
        response.close()

        val target = if (request.method == "GET" && request.url.host == Site.HOST) {
            request.url.toString()
        } else {
            "${Site.BASE_URL}/"
        }
        if (!solver.solve(target)) throw SiteProtectionException()

        val retry = chain.proceed(request)
        if (isChallenge(retry)) {
            retry.close()
            throw SiteProtectionException()
        }
        return retry
    }

    private fun isChallenge(response: Response): Boolean {
        // DLE Guard redirects to /_c/... until a trust cookie is set.
        if (response.request.url.pathSegments.firstOrNull() == "_c") return true
        if (response.code != 403 && response.code != 503) return false
        if (response.header("cf-mitigated") == "challenge") return true
        if (response.header("Server")?.contains("cloudflare", ignoreCase = true) != true) return false
        val body = response.peekBody(64 * 1024).string()
        return "_cf_chl_opt" in body || "Just a moment" in body
    }
}

object SiteWebView {
    private val CLEARANCE_COOKIES = listOf("cf_clearance", "__guard_trust")

    /** JS returning "readyState|host|path|title", consumed by [isPastChallenge]. */
    const val STATE_JS =
        "(function(){return [document.readyState, location.hostname, location.pathname, document.title].join('|');})()"

    @SuppressLint("SetJavaScriptEnabled")
    fun configure(webView: WebView, userAgent: String) {
        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            userAgentString = userAgent
        }
        CookieManager.getInstance().apply {
            setAcceptCookie(true)
            setAcceptThirdPartyCookies(webView, true)
        }
    }

    /**
     * True once the WebView shows a real site page rather than a challenge.
     * [requireCookie] guards against false positives while solving headlessly.
     */
    fun isPastChallenge(jsResult: String?, requireCookie: Boolean): Boolean {
        val parts = jsResult?.trim('"')?.split('|', limit = 4) ?: return false
        if (parts.size < 4) return false
        val (readyState, host, path, title) = parts
        val onRealPage = readyState == "complete" &&
            host.endsWith(Site.HOST) &&
            !path.startsWith("/_c") &&
            !title.contains("Just a moment", ignoreCase = true) &&
            !title.contains("Attention Required", ignoreCase = true)
        if (!onRealPage) return false
        if (!requireCookie) return true
        val cookies = CookieManager.getInstance().getCookie(Site.BASE_URL).orEmpty().split(';')
        return CLEARANCE_COOKIES.any { name -> cookies.any { it.trim().startsWith("$name=") } }
    }
}

/** Loads the page in an off-screen WebView so the challenge's JavaScript can run and set clearance cookies. */
class ChallengeSolver(private val context: Context, private val userAgent: String) {
    private val mainHandler = Handler(Looper.getMainLooper())

    @Volatile
    private var lastSolvedAt = 0L

    /** Blocks the calling background thread for up to [TIMEOUT_SECONDS]. */
    @Synchronized
    fun solve(url: String): Boolean {
        check(Looper.myLooper() != Looper.getMainLooper()) { "solve() must not run on the main thread" }
        // A parallel request just solved it; the retry will pick up the fresh cookies.
        if (System.currentTimeMillis() - lastSolvedAt < RECENT_SOLVE_MS) return true

        val latch = CountDownLatch(1)
        val solved = AtomicBoolean(false)
        val finished = AtomicBoolean(false)
        var webView: WebView? = null

        mainHandler.post {
            val view = WebView(context.applicationContext)
            webView = view
            SiteWebView.configure(view, userAgent)
            view.settings.blockNetworkImage = true
            view.webViewClient = WebViewClient()
            view.loadUrl(url)

            val poll = object : Runnable {
                override fun run() {
                    if (finished.get()) return
                    view.evaluateJavascript(SiteWebView.STATE_JS) { result ->
                        if (finished.get()) return@evaluateJavascript
                        if (SiteWebView.isPastChallenge(result, requireCookie = true)) {
                            solved.set(true)
                            latch.countDown()
                        } else {
                            mainHandler.postDelayed(this, POLL_MS)
                        }
                    }
                }
            }
            mainHandler.postDelayed(poll, POLL_MS)
        }

        latch.await(TIMEOUT_SECONDS, TimeUnit.SECONDS)
        finished.set(true)
        mainHandler.post {
            webView?.apply {
                stopLoading()
                destroy()
            }
        }

        if (solved.get()) {
            CookieManager.getInstance().flush()
            lastSolvedAt = System.currentTimeMillis()
        }
        return solved.get()
    }

    private companion object {
        const val TIMEOUT_SECONDS = 30L
        const val POLL_MS = 500L
        const val RECENT_SOLVE_MS = 10_000L
    }
}
