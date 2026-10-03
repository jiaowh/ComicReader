package dev.jiaowh.comicreader

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.jiaowh.comicreader.ui.ComicRoute
import dev.jiaowh.comicreader.ui.LibraryRoute
import dev.jiaowh.comicreader.ui.ReaderRoute
import dev.jiaowh.comicreader.ui.ReadingOrderRoute
import dev.jiaowh.comicreader.ui.order.ReadingOrderScreen
import dev.jiaowh.comicreader.ui.SearchRoute
import dev.jiaowh.comicreader.ui.VerifyRoute
import dev.jiaowh.comicreader.ui.comic.ComicScreen
import dev.jiaowh.comicreader.ui.library.LibraryScreen
import dev.jiaowh.comicreader.ui.reader.ReaderScreen
import dev.jiaowh.comicreader.ui.search.SearchScreen
import dev.jiaowh.comicreader.ui.theme.ComicReaderTheme
import dev.jiaowh.comicreader.ui.verify.VerifyScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComicReaderTheme {
                RequestNotificationPermission()
                AppNavigation()
            }
        }
    }
}

@Composable
private fun AppNavigation() {
    val nav = rememberNavController()
    val openVerify = { nav.navigate(VerifyRoute) }

    NavHost(navController = nav, startDestination = LibraryRoute) {
        composable<LibraryRoute> {
            LibraryScreen(
                onOpenComic = { nav.navigate(ComicRoute(it)) },
                onResume = { url, chapterId, page -> nav.navigate(ReaderRoute(url, chapterId, page)) },
                onSearch = { nav.navigate(SearchRoute()) },
                onVerify = openVerify,
                onOpenReadingOrder = { nav.navigate(ReadingOrderRoute(it)) },
            )
        }
        composable<ReadingOrderRoute> {
            ReadingOrderScreen(
                onBack = { nav.popBackStack() },
                onRead = { url, chapterId, page -> nav.navigate(ReaderRoute(url, chapterId, page)) },
                onLink = { stepId, label, query -> nav.navigate(SearchRoute(query, stepId, label)) },
                onOpenComic = { nav.navigate(ComicRoute(it)) },
                onVerify = openVerify,
            )
        }
        composable<SearchRoute> {
            SearchScreen(
                onBack = { nav.popBackStack() },
                onOpenComic = { nav.navigate(ComicRoute(it)) },
                onVerify = openVerify,
            )
        }
        composable<ComicRoute> {
            ComicScreen(
                onBack = { nav.popBackStack() },
                onRead = { url, chapterId, page -> nav.navigate(ReaderRoute(url, chapterId, page)) },
                onVerify = openVerify,
            )
        }
        composable<ReaderRoute> {
            ReaderScreen(
                onBack = { nav.popBackStack() },
                onVerify = openVerify,
                // Swap this reader for one on the next comic, so Back still returns to where reading started.
                onOpenOtherComic = { url, chapterId, page ->
                    nav.navigate(ReaderRoute(url, chapterId, page)) {
                        popUpTo<ReaderRoute> { inclusive = true }
                    }
                },
                onOpenReadingOrder = { nav.navigate(ReadingOrderRoute(it)) },
            )
        }
        composable<VerifyRoute> {
            VerifyScreen(onDone = { nav.popBackStack() })
        }
    }
}

@Composable
private fun RequestNotificationPermission() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { }
    LaunchedEffect(Unit) {
        val granted = ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
            PackageManager.PERMISSION_GRANTED
        if (!granted) launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }
}
