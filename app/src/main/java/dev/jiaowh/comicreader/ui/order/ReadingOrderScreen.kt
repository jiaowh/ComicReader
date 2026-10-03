package dev.jiaowh.comicreader.ui.order

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import dev.jiaowh.comicreader.data.StepState
import dev.jiaowh.comicreader.data.StepStatus
import dev.jiaowh.comicreader.data.TitleMatch
import dev.jiaowh.comicreader.ui.common.CenteredLoading
import dev.jiaowh.comicreader.ui.common.ErrorView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReadingOrderScreen(
    onBack: () -> Unit,
    onRead: (url: String, chapterId: Int, page: Int) -> Unit,
    onLink: (stepId: Long, label: String, query: String) -> Unit,
    onOpenComic: (url: String) -> Unit,
    onVerify: () -> Unit,
    viewModel: ReadingOrderViewModel = viewModel(factory = ReadingOrderViewModel.Factory),
) {
    val order by viewModel.order.collectAsStateWithLifecycle()
    val seriesName by viewModel.seriesName.collectAsStateWithLifecycle()
    val matching by viewModel.matching.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()

    // Tapping a step reads it if linked, otherwise asks the user to pick the comic.
    val open: (StepState) -> Unit = { state ->
        val target = state.openTarget
        val url = state.comic?.url
        if (url != null && target != null) {
            onRead(url, target.first, target.second)
        } else {
            onLink(state.step.id, state.step.label, TitleMatch.queries(state.step.title).first())
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(seriesName, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text("Reading order", style = MaterialTheme.typography.bodySmall)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") }
                },
                actions = {
                    IconButton(onClick = viewModel::rematch, enabled = matching == null) {
                        Icon(Icons.Default.Refresh, contentDescription = "Find missing books")
                    }
                },
            )
        },
    ) { padding ->
        val current = order
        if (current == null) {
            CenteredLoading(Modifier.padding(padding))
            return@Scaffold
        }
        LazyColumn(Modifier.padding(padding).fillMaxSize()) {
            item { ProgressHeader(current.doneCount, current.required.size, current.nextUp, open) }

            matching?.takeIf { it.second > 0 }?.let { (done, total) ->
                item { MatchingBanner(done, total) }
            }
            error?.let { failure ->
                item { ErrorView(failure, onRetry = viewModel::rematch, onVerify = onVerify, modifier = Modifier.fillMaxWidth()) }
            }

            val nextUpId = current.nextUp?.step?.id
            current.steps.forEachIndexed { index, state ->
                if (index == 0 || current.steps[index - 1].step.section != state.step.section) {
                    item(key = "section-$index") { SectionHeader(state.step.section) }
                }
                item(key = state.step.id) {
                    StepRow(
                        number = index + 1,
                        state = state,
                        isNextUp = state.step.id == nextUpId,
                        onClick = { open(state) },
                        onMarkRead = { viewModel.markRead(state) },
                        onMarkUnread = { viewModel.markUnread(state) },
                        onRelink = { onLink(state.step.id, state.step.label, TitleMatch.queries(state.step.title).first()) },
                        onOpenComic = state.comic?.url?.let { url -> { onOpenComic(url) } },
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}

@Composable
private fun ProgressHeader(done: Int, total: Int, nextUp: StepState?, onOpen: (StepState) -> Unit) {
    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("$done of $total read", style = MaterialTheme.typography.titleMedium)
        LinearProgressIndicator(
            progress = { if (total == 0) 0f else done.toFloat() / total },
            modifier = Modifier.fillMaxWidth(),
        )
        if (nextUp != null) {
            Button(onClick = { onOpen(nextUp) }, modifier = Modifier.fillMaxWidth().padding(top = 4.dp)) {
                Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                Text(
                    "Next up: ${nextUp.step.label}",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(start = 6.dp),
                )
            }
        } else {
            Text("All done! 🎉", style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
private fun MatchingBanner(done: Int, total: Int) {
    Surface(
        color = MaterialTheme.colorScheme.secondaryContainer,
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
    ) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp)
            Text(
                if (total == 0) "Finding books on BatCave…" else "Finding books on BatCave… ($done/$total)",
                modifier = Modifier.padding(start = 12.dp),
            )
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        title,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 6.dp),
    )
}

@Composable
private fun StepRow(
    number: Int,
    state: StepState,
    isNextUp: Boolean,
    onClick: () -> Unit,
    onMarkRead: () -> Unit,
    onMarkUnread: () -> Unit,
    onRelink: () -> Unit,
    onOpenComic: (() -> Unit)?,
) {
    var menuOpen by remember { mutableStateOf(false) }
    val done = state.status == StepStatus.Done
    Row(
        Modifier
            .fillMaxWidth()
            .background(if (isNextUp) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f) else MaterialTheme.colorScheme.surface)
            .clickable(onClick = onClick)
            .padding(start = 16.dp, top = 8.dp, bottom = 8.dp, end = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            "$number",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(28.dp),
        )
        Box(
            Modifier
                .width(40.dp)
                .height(60.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center,
        ) {
            if (state.comic != null) {
                AsyncImage(
                    model = state.comic.coverUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize().alpha(if (done) 0.5f else 1f),
                )
            } else {
                Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Column(Modifier.weight(1f).padding(horizontal = 12.dp)) {
            Text(
                state.step.label,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (isNextUp) FontWeight.Bold else FontWeight.Normal,
                color = if (done) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
            )
            Text(
                statusText(state),
                style = MaterialTheme.typography.bodySmall,
                color = when (state.status) {
                    StepStatus.NotFound, StepStatus.IssueMissing -> MaterialTheme.colorScheme.error
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                },
            )
            val extra = listOfNotNull(if (state.step.optional) "Optional" else null, state.step.note).joinToString(" · ")
            if (extra.isNotEmpty()) {
                Text(extra, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        if (done) {
            Icon(Icons.Default.CheckCircle, contentDescription = "Read", tint = MaterialTheme.colorScheme.primary)
        }
        Box {
            IconButton(onClick = { menuOpen = true }) { Icon(Icons.Default.MoreVert, contentDescription = "Step options") }
            DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                if (state.isLinked) {
                    if (!done) {
                        DropdownMenuItem(text = { Text("Mark as read") }, onClick = {
                            menuOpen = false
                            onMarkRead()
                        })
                    }
                    if (state.readCount > 0 || state.status == StepStatus.InProgress) {
                        DropdownMenuItem(text = { Text("Mark as unread") }, onClick = {
                            menuOpen = false
                            onMarkUnread()
                        })
                    }
                }
                DropdownMenuItem(
                    text = { Text(if (state.comic == null) "Find on BatCave…" else "Link a different comic…") },
                    onClick = {
                        menuOpen = false
                        onRelink()
                    },
                )
                if (onOpenComic != null) {
                    DropdownMenuItem(text = { Text("Open comic page") }, onClick = {
                        menuOpen = false
                        onOpenComic()
                    })
                }
            }
        }
    }
}

private fun statusText(state: StepState): String {
    val issues = state.chapters.size
    return when (state.status) {
        StepStatus.Unlinked -> "Not linked yet. Tap to find it"
        StepStatus.NotFound -> "Not found on BatCave. Tap to pick it"
        StepStatus.IssueMissing -> "These issues aren't on BatCave yet"
        StepStatus.NotStarted -> if (issues > 1) "$issues issues" else "Not started"
        StepStatus.InProgress -> when {
            issues > 1 -> "${state.readCount} of $issues issues read"
            state.resumePage > 0 -> "On page ${state.resumePage + 1}"
            else -> "In progress"
        }
        StepStatus.Done -> if (issues > 1) "All $issues issues read" else "Read"
    }
}
