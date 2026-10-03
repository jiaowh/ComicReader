package dev.jiaowh.comicreader.ui.reader

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChanged
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.SubcomposeAsyncImage
import dev.jiaowh.comicreader.data.remote.Chapter
import dev.jiaowh.comicreader.ui.common.ErrorView
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

private val OverlayColor = Color.Black.copy(alpha = 0.8f)

@Composable
fun ReaderScreen(
    onBack: () -> Unit,
    onVerify: () -> Unit,
    /** Continues the reading order in a different comic. */
    onOpenOtherComic: (url: String, chapterId: Int, page: Int) -> Unit,
    onOpenReadingOrder: (seriesId: Long) -> Unit,
    viewModel: ReaderViewModel = viewModel(factory = ReaderViewModel.Factory),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var overlayVisible by rememberSaveable { mutableStateOf(true) }

    KeepScreenOn()
    ImmersiveMode(hideSystemBars = !overlayVisible)

    Box(Modifier.fillMaxSize().background(Color.Black)) {
        val chapter = state.chapter
        val error = state.error
        when {
            error != null -> {
                ErrorView(
                    error,
                    onRetry = viewModel::retry,
                    onVerify = onVerify,
                    contentColor = Color.White,
                    modifier = Modifier.align(Alignment.Center),
                )
                TopBar(state.comicTitle, chapter?.title.orEmpty(), onBack)
            }
            state.loading || chapter == null -> {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
                TopBar(state.comicTitle, chapter?.title.orEmpty(), onBack)
            }
            else -> key(chapter.id) {
                ChapterPager(
                    state = state,
                    chapter = chapter,
                    overlayVisible = overlayVisible,
                    onToggleOverlay = { overlayVisible = !overlayVisible },
                    onPageChanged = viewModel::onPageChanged,
                    onOpenChapter = { viewModel.open(it.id) },
                    onOpenOrderNext = onOpenOrderNext@{ next ->
                        val url = next.url
                        val chapterId = next.chapterId
                        if (url == null || chapterId == null) {
                            onOpenReadingOrder(next.seriesId) // not linked yet; let the user find it
                            return@onOpenOrderNext
                        }
                        if (url == viewModel.comicUrl) {
                            viewModel.open(chapterId, next.page)
                        } else {
                            onOpenOtherComic(url, chapterId, next.page)
                        }
                    },
                    onBack = onBack,
                )
            }
        }
    }
}

@Composable
private fun BoxScope.ChapterPager(
    state: ReaderUiState,
    chapter: Chapter,
    overlayVisible: Boolean,
    onToggleOverlay: () -> Unit,
    onPageChanged: (Int) -> Unit,
    onOpenChapter: (Chapter) -> Unit,
    onOpenOrderNext: (OrderNext) -> Unit,
    onBack: () -> Unit,
) {
    val pages = state.pages
    // One extra page at the end for "next chapter".
    val pagerState = rememberPagerState(initialPage = state.startPage) { pages.size + 1 }
    val scope = rememberCoroutineScope()
    var zoomed by remember { mutableStateOf(false) }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect(onPageChanged)
    }

    HorizontalPager(
        state = pagerState,
        beyondViewportPageCount = 2,
        userScrollEnabled = !zoomed,
        key = { it },
        modifier = Modifier.fillMaxSize(),
    ) { page ->
        if (page < pages.size) {
            ZoomableImage(
                url = pages[page],
                onZoomChange = { if (page == pagerState.currentPage) zoomed = it },
                onTap = { offset, size ->
                    when {
                        offset.x < size.width * 0.3f -> scope.launch { pagerState.animateScrollToPage(page - 1) }
                        offset.x > size.width * 0.7f -> scope.launch { pagerState.animateScrollToPage(page + 1) }
                        else -> onToggleOverlay()
                    }
                },
            )
        } else {
            EndOfChapter(chapter, state.next, state.orderNext, state.tracking, onOpenChapter, onOpenOrderNext, onBack)
        }
    }

    AnimatedVisibility(
        visible = overlayVisible,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = Modifier.align(Alignment.TopCenter),
    ) {
        TopBar(state.comicTitle, chapter.title, onBack)
    }
    AnimatedVisibility(
        visible = overlayVisible,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = Modifier.align(Alignment.BottomCenter),
    ) {
        BottomBar(state, pagerState, onOpenChapter)
    }
}

@Composable
private fun TopBar(comicTitle: String, chapterTitle: String, onBack: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .background(OverlayColor)
            .statusBarsPadding()
            .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
        }
        Column {
            Text(comicTitle, color = Color.White, style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
            if (chapterTitle.isNotEmpty()) {
                Text(chapterTitle, color = Color.White.copy(alpha = 0.7f), style = MaterialTheme.typography.bodySmall, maxLines = 1)
            }
        }
    }
}

@Composable
private fun BottomBar(state: ReaderUiState, pagerState: PagerState, onOpenChapter: (Chapter) -> Unit) {
    val scope = rememberCoroutineScope()
    val pageCount = state.pages.size
    val current = pagerState.currentPage.coerceAtMost(pageCount - 1)
    Column(
        Modifier
            .fillMaxWidth()
            .background(OverlayColor)
            .navigationBarsPadding()
            .padding(horizontal = 8.dp, vertical = 4.dp),
    ) {
        if (pageCount > 1) {
            Slider(
                value = current.toFloat(),
                onValueChange = { scope.launch { pagerState.scrollToPage(it.roundToInt()) } },
                valueRange = 0f..(pageCount - 1).toFloat(),
                steps = (pageCount - 2).coerceAtLeast(0),
                modifier = Modifier.padding(horizontal = 8.dp),
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = { state.previous?.let(onOpenChapter) }, enabled = state.previous != null) {
                Text("‹ Prev chapter")
            }
            Text(
                "${current + 1} / $pageCount",
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f),
            )
            TextButton(onClick = { state.next?.let(onOpenChapter) }, enabled = state.next != null) {
                Text("Next chapter ›")
            }
        }
    }
}

@Composable
private fun EndOfChapter(
    chapter: Chapter,
    next: Chapter?,
    orderNext: OrderNext?,
    tracking: Boolean,
    onOpenChapter: (Chapter) -> Unit,
    onOpenOrderNext: (OrderNext) -> Unit,
    onBack: () -> Unit,
) {
    Column(
        Modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Finished ${chapter.title}", color = Color.White, style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center)
        if (orderNext != null) {
            // The reading order wins over the comic's own next chapter.
            Text(
                "Up next in ${orderNext.seriesName}",
                color = Color.White.copy(alpha = 0.7f),
                style = MaterialTheme.typography.bodySmall,
            )
            Button(onClick = { onOpenOrderNext(orderNext) }) {
                Text(
                    if (orderNext.url != null) orderNext.label else "${orderNext.label} (find it)",
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                )
            }
            if (next != null) {
                TextButton(onClick = { onOpenChapter(next) }) { Text("Or continue: ${next.title}", maxLines = 1, overflow = TextOverflow.Ellipsis) }
            }
        } else if (next != null) {
            Button(onClick = { onOpenChapter(next) }) { Text("Next: ${next.title}", maxLines = 1, overflow = TextOverflow.Ellipsis) }
        } else {
            Text("You're all caught up!", color = Color.White.copy(alpha = 0.7f))
        }
        OutlinedButton(onClick = onBack) { Text("Back") }
        if (!tracking) {
            Text(
                "This comic isn't in your library, so progress isn't saved.",
                color = Color.White.copy(alpha = 0.6f),
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
            )
        }
    }
}

/** Fit-to-screen page with pinch / double-tap zoom. Single-finger drags pass through to the pager unless zoomed. */
@Composable
private fun ZoomableImage(
    url: String,
    onZoomChange: (Boolean) -> Unit,
    onTap: (Offset, IntSize) -> Unit,
) {
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var size by remember { mutableStateOf(IntSize.Zero) }
    var attempt by remember { mutableIntStateOf(0) }

    fun clamp(value: Offset, forScale: Float): Offset {
        val maxX = size.width * (forScale - 1f) / 2f
        val maxY = size.height * (forScale - 1f) / 2f
        return Offset(value.x.coerceIn(-maxX, maxX), value.y.coerceIn(-maxY, maxY))
    }

    Box(
        Modifier
            .fillMaxSize()
            .onSizeChanged { size = it }
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onTap(it, size) },
                    onDoubleTap = { tap ->
                        if (scale > 1f) {
                            scale = 1f
                            offset = Offset.Zero
                        } else {
                            scale = DOUBLE_TAP_SCALE
                            val center = Offset(size.width / 2f, size.height / 2f)
                            offset = clamp((center - tap) * (DOUBLE_TAP_SCALE - 1f), DOUBLE_TAP_SCALE)
                        }
                        onZoomChange(scale > 1f)
                    },
                )
            }
            .pointerInput(Unit) {
                awaitEachGesture {
                    awaitFirstDown(requireUnconsumed = false)
                    do {
                        val event = awaitPointerEvent()
                        val pinching = event.changes.count { it.pressed } > 1
                        if (pinching || scale > 1f) {
                            val newScale = (scale * event.calculateZoom()).coerceIn(1f, MAX_SCALE)
                            scale = newScale
                            offset = if (newScale == 1f) Offset.Zero else clamp(offset + event.calculatePan(), newScale)
                            onZoomChange(newScale > 1f)
                            event.changes.forEach { if (it.positionChanged()) it.consume() }
                        }
                    } while (event.changes.any { it.pressed })
                }
            },
        contentAlignment = Alignment.Center,
    ) {
        key(attempt) {
            SubcomposeAsyncImage(
                model = url,
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        translationX = offset.x
                        translationY = offset.y
                    },
                loading = {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                },
                error = {
                    Column(
                        Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text("Couldn't load this page", color = Color.White)
                        TextButton(onClick = { attempt++ }) { Text("Retry") }
                    }
                },
            )
        }
    }
}

private const val DOUBLE_TAP_SCALE = 2.5f
private const val MAX_SCALE = 5f

@Composable
private fun KeepScreenOn() {
    val view = LocalView.current
    DisposableEffect(view) {
        view.keepScreenOn = true
        onDispose { view.keepScreenOn = false }
    }
}

@Composable
private fun ImmersiveMode(hideSystemBars: Boolean) {
    val view = LocalView.current
    val window = (view.context as? Activity)?.window ?: return
    val controller = remember(window, view) { WindowCompat.getInsetsController(window, view) }
    LaunchedEffect(hideSystemBars) {
        if (hideSystemBars) {
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            controller.hide(WindowInsetsCompat.Type.systemBars())
        } else {
            controller.show(WindowInsetsCompat.Type.systemBars())
        }
    }
    DisposableEffect(controller) {
        onDispose { controller.show(WindowInsetsCompat.Type.systemBars()) }
    }
}
