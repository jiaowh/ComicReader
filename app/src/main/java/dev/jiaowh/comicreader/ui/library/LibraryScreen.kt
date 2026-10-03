package dev.jiaowh.comicreader.ui.library

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import dev.jiaowh.comicreader.data.SeedStatus
import dev.jiaowh.comicreader.data.db.LibraryItem
import dev.jiaowh.comicreader.data.db.SeriesEntity
import dev.jiaowh.comicreader.ui.common.CenteredLoading
import dev.jiaowh.comicreader.ui.common.DeleteSeriesDialog
import dev.jiaowh.comicreader.ui.common.RemoveComicDialog
import dev.jiaowh.comicreader.ui.common.SeriesNameDialog
import dev.jiaowh.comicreader.ui.common.SeriesPickerDialog
import dev.jiaowh.comicreader.ui.common.pluralize

private sealed interface LibraryDialog {
    data class RemoveComic(val item: LibraryItem) : LibraryDialog
    data class MoveComic(val item: LibraryItem) : LibraryDialog
    data object NewSeries : LibraryDialog
    data class RenameSeries(val series: SeriesEntity) : LibraryDialog
    data class DeleteSeries(val group: SeriesGroup) : LibraryDialog
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    onOpenComic: (url: String) -> Unit,
    onResume: (url: String, chapterId: Int, page: Int) -> Unit,
    onSearch: () -> Unit,
    onVerify: () -> Unit,
    viewModel: LibraryViewModel = viewModel(factory = LibraryViewModel.Factory),
) {
    val content by viewModel.content.collectAsStateWithLifecycle()
    val allSeries by viewModel.series.collectAsStateWithLifecycle()
    val refreshing by viewModel.refreshing.collectAsStateWithLifecycle()
    val message by viewModel.message.collectAsStateWithLifecycle()
    val seedStatus by viewModel.seedStatus.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }
    var dialog by remember { mutableStateOf<LibraryDialog?>(null) }

    LaunchedEffect(message) {
        when (val current = message) {
            null -> Unit
            is LibraryMessage.Info -> {
                viewModel.messageShown()
                snackbar.showSnackbar(current.text)
            }
            LibraryMessage.NeedsVerification -> {
                viewModel.messageShown()
                val result = snackbar.showSnackbar(
                    "BatCave needs a quick human check",
                    actionLabel = "Verify",
                    duration = SnackbarDuration.Long,
                )
                if (result == SnackbarResult.ActionPerformed) onVerify()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Library") },
                actions = {
                    IconButton(onClick = { dialog = LibraryDialog.NewSeries }) {
                        Icon(Icons.Default.Add, contentDescription = "New series")
                    }
                    IconButton(onClick = viewModel::refresh, enabled = !refreshing) {
                        Icon(Icons.Default.Refresh, contentDescription = "Check for new chapters")
                    }
                    IconButton(onClick = onSearch) {
                        Icon(Icons.Default.Search, contentDescription = "Search BatCave")
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbar) },
    ) { padding ->
        val library = content
        when {
            library == null -> CenteredLoading(Modifier.padding(padding))
            library.isEmpty && seedStatus !is SeedStatus.Running -> EmptyLibrary(onSearch, Modifier.padding(padding))
            else -> PullToRefreshBox(
                isRefreshing = refreshing,
                onRefresh = viewModel::refresh,
                modifier = Modifier.padding(padding).fillMaxSize(),
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 110.dp),
                    contentPadding = PaddingValues(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize(),
                ) {
                    (seedStatus as? SeedStatus.Running)?.let { status ->
                        item(key = "seeding", span = { GridItemSpan(maxLineSpan) }) { SeedingBanner(status) }
                    }

                    val comicCard: LazyGridScope.(List<LibraryItem>) -> Unit = { list ->
                        items(list, key = { it.comic.url }) { item ->
                            LibraryCard(
                                item = item,
                                onClick = { onOpenComic(item.comic.url) },
                                onResume = item.resumeTarget()?.let { (chapterId, page) ->
                                    { onResume(item.comic.url, chapterId, page) }
                                },
                                onMove = { dialog = LibraryDialog.MoveComic(item) },
                                onRemove = { dialog = LibraryDialog.RemoveComic(item) },
                            )
                        }
                    }

                    for (group in library.groups) {
                        item(key = "series-${group.series.id}", span = { GridItemSpan(maxLineSpan) }) {
                            SeriesHeader(
                                group = group,
                                onToggle = { viewModel.toggleCollapsed(group.series) },
                                onRename = { dialog = LibraryDialog.RenameSeries(group.series) },
                                onDelete = { dialog = LibraryDialog.DeleteSeries(group) },
                            )
                        }
                        if (!group.series.collapsed) {
                            if (group.items.isEmpty()) {
                                item(key = "series-empty-${group.series.id}", span = { GridItemSpan(maxLineSpan) }) {
                                    Text(
                                        "No comics yet. Add one from its page, or long-press a comic to move it here.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                            }
                            comicCard(group.items)
                        }
                    }

                    if (library.ungrouped.isNotEmpty()) {
                        if (library.groups.isNotEmpty()) {
                            item(key = "ungrouped-header", span = { GridItemSpan(maxLineSpan) }) {
                                SectionTitle("Other comics", pluralize(library.ungrouped.size, "comic"))
                            }
                        }
                        comicCard(library.ungrouped)
                    }
                }
            }
        }
    }

    when (val current = dialog) {
        null -> Unit
        is LibraryDialog.RemoveComic -> RemoveComicDialog(
            title = current.item.comic.title,
            onConfirm = {
                viewModel.removeComic(current.item)
                dialog = null
            },
            onDismiss = { dialog = null },
        )
        is LibraryDialog.MoveComic -> SeriesPickerDialog(
            title = "Move to series",
            confirmLabel = "Move",
            series = allSeries,
            current = current.item.comic.seriesId,
            onConfirm = {
                viewModel.moveToSeries(current.item, it)
                dialog = null
            },
            onDismiss = { dialog = null },
        )
        LibraryDialog.NewSeries -> SeriesNameDialog(
            title = "New series",
            confirmLabel = "Create",
            onConfirm = {
                viewModel.createSeries(it)
                dialog = null
            },
            onDismiss = { dialog = null },
        )
        is LibraryDialog.RenameSeries -> SeriesNameDialog(
            title = "Rename series",
            confirmLabel = "Rename",
            initial = current.series.name,
            onConfirm = {
                viewModel.renameSeries(current.series, it)
                dialog = null
            },
            onDismiss = { dialog = null },
        )
        is LibraryDialog.DeleteSeries -> DeleteSeriesDialog(
            series = current.group.series,
            comicCount = current.group.items.size,
            onConfirm = { deleteComics ->
                viewModel.deleteSeries(current.group.series, deleteComics)
                dialog = null
            },
            onDismiss = { dialog = null },
        )
    }
}

/** Where "resume" should land: the unfinished current chapter, else the next one. */
private fun LibraryItem.resumeTarget(): Pair<Int, Int>? {
    val current = comic.lastReadChapterId
    return when {
        current != null && !comic.lastChapterFinished -> current to comic.lastReadPage
        nextChapterId != null -> nextChapterId to 0
        else -> null
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun SeriesHeader(
    group: SeriesGroup,
    onToggle: () -> Unit,
    onRename: () -> Unit,
    onDelete: () -> Unit,
) {
    var menuOpen by remember { mutableStateOf(false) }
    val haptics = LocalHapticFeedback.current
    Column {
        Row(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .combinedClickable(
                    onClick = onToggle,
                    onLongClick = {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        menuOpen = true
                    },
                )
                .padding(start = 4.dp, top = 4.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                if (group.series.collapsed) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
                contentDescription = if (group.series.collapsed) "Expand" else "Collapse",
            )
            Column(Modifier.weight(1f).padding(start = 8.dp)) {
                Text(group.series.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(
                    pluralize(group.items.size, "comic"),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            if (group.newChapters > 0) Badge("NEW +${group.newChapters}", highlight = true, shape = RoundedCornerShape(8.dp))
            Box {
                IconButton(onClick = { menuOpen = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Series options")
                }
                DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                    DropdownMenuItem(text = { Text("Rename") }, onClick = {
                        menuOpen = false
                        onRename()
                    })
                    DropdownMenuItem(text = { Text("Delete series") }, onClick = {
                        menuOpen = false
                        onDelete()
                    })
                }
            }
        }
        HorizontalDivider()
    }
}

@Composable
private fun SectionTitle(title: String, subtitle: String) {
    Column {
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        HorizontalDivider(Modifier.padding(top = 4.dp))
    }
}

@Composable
private fun SeedingBanner(status: SeedStatus.Running) {
    Surface(color = MaterialTheme.colorScheme.secondaryContainer, shape = RoundedCornerShape(8.dp)) {
        Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp)
            Text(
                "Setting up ${status.seriesName}… (${status.done}/${status.total})",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(start = 12.dp),
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun LibraryCard(
    item: LibraryItem,
    onClick: () -> Unit,
    onResume: (() -> Unit)?,
    onMove: () -> Unit,
    onRemove: () -> Unit,
) {
    val comic = item.comic
    var menuOpen by remember { mutableStateOf(false) }
    val haptics = LocalHapticFeedback.current
    Box {
        Column(
            Modifier.combinedClickable(
                onClick = onClick,
                onLongClick = {
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    menuOpen = true
                },
            ),
        ) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .aspectRatio(2f / 3f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
            ) {
                AsyncImage(
                    model = comic.coverUrl,
                    contentDescription = comic.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
                val started = comic.lastReadChapterId != null
                when {
                    comic.newChapterCount > 0 -> Badge("NEW +${comic.newChapterCount}", highlight = true)
                    started && item.unreadCount > 0 -> Badge("${item.unreadCount} unread", highlight = false)
                }
                if (onResume != null) {
                    FilledIconButton(
                        onClick = onResume,
                        modifier = Modifier.align(Alignment.BottomEnd).padding(6.dp).size(36.dp),
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = "Resume reading")
                    }
                }
            }
            Text(
                comic.title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 6.dp),
            )
            Text(
                progressLabel(item),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
            DropdownMenuItem(text = { Text("Move to series…") }, onClick = {
                menuOpen = false
                onMove()
            })
            DropdownMenuItem(text = { Text("Remove from library") }, onClick = {
                menuOpen = false
                onRemove()
            })
        }
    }
}

@Composable
private fun Badge(text: String, highlight: Boolean, shape: RoundedCornerShape = RoundedCornerShape(bottomEnd = 8.dp)) {
    Surface(
        color = if (highlight) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondaryContainer,
        contentColor = if (highlight) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondaryContainer,
        shape = shape,
    ) {
        Text(
            text,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
        )
    }
}

private fun progressLabel(item: LibraryItem): String {
    val comic = item.comic
    val chapter = item.lastReadChapterTitle ?: return "Not started · ${item.totalChapters} ch"
    return when {
        !comic.lastChapterFinished && comic.lastReadPageCount > 0 ->
            "$chapter · p${comic.lastReadPage + 1}/${comic.lastReadPageCount}"
        item.unreadCount == 0 -> "Caught up · $chapter"
        else -> "Read $chapter"
    }
}

@Composable
private fun EmptyLibrary(onSearch: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Your library is empty", style = MaterialTheme.typography.titleMedium)
        Text(
            "Search BatCave for the comics you're reading and add them here.",
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp, bottom = 20.dp),
        )
        Button(onClick = onSearch) { Text("Search comics") }
    }
}
