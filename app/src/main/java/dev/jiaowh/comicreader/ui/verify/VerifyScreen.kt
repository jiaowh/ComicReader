package dev.jiaowh.comicreader.ui.verify

import android.webkit.CookieManager
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import dev.jiaowh.comicreader.ComicApp
import dev.jiaowh.comicreader.data.net.Site
import dev.jiaowh.comicreader.data.net.SiteWebView
import kotlinx.coroutines.delay

/** Shows the site in a real WebView so the user can complete a challenge the hidden solver couldn't. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerifyScreen(onDone: () -> Unit) {
    val container = (LocalContext.current.applicationContext as ComicApp).container
    var verified by remember { mutableStateOf(false) }
    var finished by remember { mutableStateOf(false) }
    val finish = {
        if (!finished) {
            finished = true
            CookieManager.getInstance().flush()
            container.notifySiteVerified()
            onDone()
        }
    }

    LaunchedEffect(verified) {
        if (verified) {
            delay(1_000)
            finish()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (verified) "Verified ✓" else "Verify access") },
                navigationIcon = {
                    IconButton(onClick = onDone) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") }
                },
                actions = { TextButton(onClick = finish) { Text("Done") } },
            )
        },
    ) { padding ->
        Column(Modifier.padding(padding).fillMaxSize()) {
            Text(
                "Complete the check below if one appears. This page closes on its own once BatCave loads.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            )
            AndroidView(
                factory = { context ->
                    WebView(context).apply {
                        SiteWebView.configure(this, container.userAgent)
                        webViewClient = object : WebViewClient() {
                            override fun onPageFinished(view: WebView, url: String?) {
                                view.evaluateJavascript(SiteWebView.STATE_JS) { result ->
                                    if (SiteWebView.isPastChallenge(result, requireCookie = false)) verified = true
                                }
                            }
                        }
                        loadUrl("${Site.BASE_URL}/")
                    }
                },
                onRelease = { it.destroy() },
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}
