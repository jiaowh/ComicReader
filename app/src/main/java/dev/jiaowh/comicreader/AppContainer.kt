package dev.jiaowh.comicreader

import android.content.Context
import android.webkit.WebSettings
import dev.jiaowh.comicreader.data.ComicRepository
import dev.jiaowh.comicreader.data.StarterSeriesSeeder
import dev.jiaowh.comicreader.data.db.AppDatabase
import dev.jiaowh.comicreader.data.net.ChallengeSolver
import dev.jiaowh.comicreader.data.net.SiteHeadersInterceptor
import dev.jiaowh.comicreader.data.net.SiteProtectionInterceptor
import dev.jiaowh.comicreader.data.net.WebViewCookieJar
import dev.jiaowh.comicreader.data.remote.BatcaveApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

/** Hand-rolled dependency container; one instance lives on [ComicApp]. */
class AppContainer(context: Context) {
    private val appContext = context.applicationContext

    /** The WebView's own UA, so clearance cookies earned in a WebView stay valid for OkHttp. */
    val userAgent: String by lazy { WebSettings.getDefaultUserAgent(appContext) }

    val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .cookieJar(WebViewCookieJar())
            .addInterceptor(SiteHeadersInterceptor(userAgent))
            .addInterceptor(SiteProtectionInterceptor(ChallengeSolver(appContext, userAgent)))
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    private val database by lazy { AppDatabase.create(appContext) }

    val repository by lazy { ComicRepository(BatcaveApi(okHttpClient), database) }

    val starterSeriesSeeder by lazy { StarterSeriesSeeder(appContext, repository) }

    private val _siteVerified = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    /** Emits after the user completes the manual verification page, so failed screens can retry. */
    val siteVerified: SharedFlow<Unit> = _siteVerified

    fun notifySiteVerified() {
        _siteVerified.tryEmit(Unit)
    }
}
