package dev.jiaowh.comicreader

import android.app.Application
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import coil3.request.crossfade
import dev.jiaowh.comicreader.work.UpdateWorker

class ComicApp : Application(), SingletonImageLoader.Factory {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
        UpdateWorker.createNotificationChannel(this)
        UpdateWorker.schedule(this)
    }

    /** Images go through the same client so they get the site's cookies and headers. */
    override fun newImageLoader(context: PlatformContext): ImageLoader =
        ImageLoader.Builder(context)
            .components { add(OkHttpNetworkFetcherFactory(callFactory = { container.okHttpClient })) }
            .crossfade(true)
            .build()
}
