package dev.jiaowh.comicreader.ui.comic

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import dev.jiaowh.comicreader.data.net.Site
import dev.jiaowh.comicreader.data.remote.Chapter
import dev.jiaowh.comicreader.ui.common.CenteredLoading
import dev.jiaowh.comicreader.ui.common.ErrorView
import dev.jiaowh.comicreader.ui.common.RemoveComicDialog
import dev.jiaowh.comicreader.ui.common.SeriesPickerDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComicScreen(
    onBack: () -> Unit,
    onRead: (url: String, chapterId: Int, page: Int) -> Unit,
    onVerify: () -> Unit,
    viewModel: ComicViewModel = viewModel(factory = ComicViewModel.Factory),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var menuOpen by remember { mutableStateOf(false) }
    var confirmRemove by remember { mutableStateOf(false) }
    val series by viewModel.series.collectAsStateWithLifecycle()
    var pickingSeries by remember { mutableStateOf(false) }
    val seriesName = series.firstOrNull { it.id == state.progress?.seriesId }?.name

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.title, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") }
                },
                actions = {
                    IconButton(onClick = { menuOpen = true }) { Icon(Icons.Default.MoreVert, contentDescription = "More") }
                    DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                        DropdownMenuItem(
                            text = { Text("Open on BatCave") },
                            onClick = {
                                menuOpen = false
                                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(Site.absolute(state.url))))
                            },
                        )
                        if (state.inLibrary) {
                            DropdownMenuItem(
                                text = { Text("Remove from library") },
                                onClick = {
                                    menuOpen = false
                                    confirmRemove = true
                                },
                            )
                        }
                    }
                },
            )
        },
    ) { padding ->
        val error = state.error
        when {
            state.title.isEmpty() && state.loading -> CenteredLoading(Modifier.padding(padding))
            state.title.isEmpty() && error != null -> Box(Modifier.padding(padding).fillMaxSize(), Alignment.Center) {
                ErrorView(error, onRetry = viewModel::load, onVerify = onVerify)
            }
            else -> ComicContent(
                state = state,
                onRead = { chapter, page -> onRead(state.url, chapter.id, page) },
                seriesName = seriesName,
                // With no series yet, skip the picker and just add.
                onAdd = { if (series.isEmpty()) viewModel.addToLibrary() else pickingSeries = true },
                onChangeSeries = { pickingSeries = true },
                onMarkReadUpTo = viewModel::markReadUpTo,
                onMarkUnreadFrom = viewModel::markUnreadFrom,
                onVerify = onVerify,
                onRetry = viewModel::load,
                modifier = Modifier.padding(padding),
            )
        }
    }

    if (confirmRemove) {
        RemoveComicDialog(
            title = state.title,
            onConfirm = {
                confirmRemove = false
                viewModel.removeFromLibrary()
            },
            onDismiss = { confirmRemove = false },
        )
    }

    if (pickingSeries) {
        SeriesPickerDialog(
            title = if (state.inLibrary) "Series" else "Add to library",
            confirmLabel = if (state.inLibrary) "Save" else "Add",
            series = series,
            current = state.progress?.seriesId,
            onConfirm = { choice ->
                pickingSeries = false
                if (state.inLibrary) viewModel.setSeries(choice) else viewModel.addToLibrary(choice)
            },
            onDismiss = { pickingSeries = false },
        )
    }
}

@Composable
private fun ComicContent(
    state: ComicUiState,
    onRead: (Chapter, Int) -> Unit,
    seriesName: String?,
    onAdd: () -> Unit,
    onChangeSeries: () -> Unit,
    onMarkReadUpTo: (Chapter) -> Unit,
    onMarkUnreadFrom: (Chapter) -> Unit,
    onVerify: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier.fillMaxSize()) {
        item {
            if (state.loading) LinearProgressIndicator(Modifier.fillMaxWidth())
            Header(state)
        }
        item {
            Actions(state, seriesName, onRead, onAdd, onChangeSeries)
        }
        state.description?.let { description ->
            item { Description(description) }
        }
        state.error?.let { error ->
            item { ErrorView(error, onRetry = onRetry, onVerify = onVerify, modifier = Modifier.fillMaxWidth()) }
        }
        item {
            Text(
                "${state.chapters.size} chapters",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 4.dp),
            )
        }
        // Newest first, like the site.
        items(state.chapters.asReversed(), key = { it.id }) { chapter ->
            ChapterRow(
                chapter = chapter,
                read = state.isRead(chapter),
                currentPage = if (state.isCurrent(chapter)) state.progress?.let { it.lastReadPage to it.lastReadPageCount } else null,
                showMenu = state.inLibrary,
                onClick = { onRead(chapter, if (state.isCurrent(chapter)) state.progress?.lastReadPage ?: 0 else 0) },
                onMarkReadUpTo = { onMarkReadUpTo(chapter) },
                onMarkUnreadFrom = { onMarkUnreadFrom(chapter) },
            )
            HorizontalDivider()
        }
    }
}

@Composable
private fun Header(state: ComicUiState) {
    Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        AsyncImage(
            model = state.coverUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .width(110.dp)
                .height(165.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
        )
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(state.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
            listOfNotNull(state.publisher, state.year).joinToString(" · ").takeIf { it.isNotEmpty() }?.let {
                Text(it, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            state.status?.let {
                Text(it, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun Actions(
    state: ComicUiState,
    seriesName: String?,
    onRead: (Chapter, Int) -> Unit,
    onAdd: () -> Unit,
    onChangeSeries: () -> Unit,
) {
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        val target = state.readTarget
        Button(
            onClick = { target?.let { onRead(it.chapter, it.page) } },
            enabled = target != null,
            modifier = Modifier.weight(1f),
        ) {
            Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
            Text(
                target?.label ?: "Caught up",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(start = 6.dp),
            )
        }
        if (state.inLibrary) {
            // Shows the comic's series; tap to change it.
            OutlinedButton(onClick = onChangeSeries, modifier = Modifier.widthIn(max = 160.dp)) {
                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                Text(
                    seriesName ?: "In library",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(start = 6.dp),
                )
            }
        } else {
            OutlinedButton(onClick = onAdd, enabled = state.canAdd) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Text("Add", modifier = Modifier.padding(start = 6.dp))
            }
        }
    }
    if (!state.inLibrary) {
        Text(
            "Add to your library to track progress and new chapters.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
        )
    }
}

@Composable
private fun Description(text: String) {
    var expanded by remember { mutableStateOf(false) }
    Text(
        text,
        style = MaterialTheme.typography.bodyMedium,
        maxLines = if (expanded) Int.MAX_VALUE else 3,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier
            .clickable { expanded = !expanded }
            .animateContentSize()
            .padding(16.dp),
    )
}

@Composable
private fun ChapterRow(
    chapter: Chapter,
    read: Boolean,
    currentPage: Pair<Int, Int>?,
    showMenu: Boolean,
    onClick: () -> Unit,
    onMarkReadUpTo: () -> Unit,
    onMarkUnreadFrom: () -> Unit,
) {
    var menuOpen by remember { mutableStateOf(false) }
    Row(
        Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(start = 16.dp, top = 10.dp, bottom = 10.dp, end = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f).alpha(if (read) 0.45f else 1f)) {
            Text(
                chapter.title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (currentPage != null) FontWeight.Bold else FontWeight.Normal,
                color = if (currentPage != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
            )
            val subtitle = buildList {
                if (chapter.date.isNotBlank()) add(chapter.date)
                currentPage?.let { (page, count) -> add(if (count > 0) "Page ${page + 1} of $count" else "In progress") }
            }.joinToString(" · ")
            if (subtitle.isNotEmpty()) {
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        if (showMenu) {
            Box {
                IconButton(onClick = { menuOpen = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Chapter options")
                }
                DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                    DropdownMenuItem(
                        text = { Text("Mark read up to here") },
                        onClick = {
                            menuOpen = false
                            onMarkReadUpTo()
                        },
                    )
                    DropdownMenuItem(
                        text = { Text("Mark unread from here") },
                        onClick = {
                            menuOpen = false
                            onMarkUnreadFrom()
                        },
                    )
                }
            }
        }
    }
}
