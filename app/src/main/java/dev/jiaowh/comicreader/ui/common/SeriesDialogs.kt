package dev.jiaowh.comicreader.ui.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp
import dev.jiaowh.comicreader.data.SeriesChoice
import dev.jiaowh.comicreader.data.db.SeriesEntity

/** Text-entry dialog for creating or renaming a series. */
@Composable
fun SeriesNameDialog(
    title: String,
    confirmLabel: String,
    initial: String = "",
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    var name by remember { mutableStateOf(initial) }
    val focus = remember { FocusRequester() }
    LaunchedEffect(Unit) { focus.requestFocus() }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Series name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().focusRequester(focus),
            )
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(name.trim()) }, enabled = name.isNotBlank()) { Text(confirmLabel) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
    )
}

/**
 * Lets the user pick which series a comic belongs to, or create a new one.
 * [current] is the comic's current series (null = none).
 */
@Composable
fun SeriesPickerDialog(
    title: String,
    confirmLabel: String,
    series: List<SeriesEntity>,
    current: Long?,
    onConfirm: (SeriesChoice) -> Unit,
    onDismiss: () -> Unit,
) {
    var selected by remember { mutableStateOf(current) }
    var creatingNew by remember { mutableStateOf(false) }
    var newName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(Modifier.heightIn(max = 420.dp).verticalScroll(rememberScrollState())) {
                PickerRow("No series", selected = !creatingNew && selected == null) {
                    creatingNew = false
                    selected = null
                }
                series.forEach { item ->
                    PickerRow(item.name, selected = !creatingNew && selected == item.id) {
                        creatingNew = false
                        selected = item.id
                    }
                }
                HorizontalDivider(Modifier.padding(vertical = 4.dp))
                PickerRow("New series…", selected = creatingNew) { creatingNew = true }
                if (creatingNew) {
                    OutlinedTextField(
                        value = newName,
                        onValueChange = { newName = it },
                        label = { Text("Series name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(if (creatingNew) SeriesChoice.New(newName.trim()) else SeriesChoice.Existing(selected))
                },
                enabled = !creatingNew || newName.isNotBlank(),
            ) { Text(confirmLabel) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
    )
}

@Composable
private fun PickerRow(label: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(selected = selected, onClick = onClick)
        Text(label, style = MaterialTheme.typography.bodyLarge)
    }
}

/** Confirms deleting a series, optionally taking its comics with it. */
@Composable
fun DeleteSeriesDialog(
    series: SeriesEntity,
    comicCount: Int,
    onConfirm: (deleteComics: Boolean) -> Unit,
    onDismiss: () -> Unit,
) {
    var deleteComics by remember { mutableStateOf(false) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete \"${series.name}\"?") },
        text = {
            Column {
                Text(
                    if (deleteComics) {
                        "The series and its ${pluralize(comicCount, "comic")} will be removed, along with their reading progress."
                    } else {
                        "Its comics stay in your library, just without a series."
                    },
                )
                if (comicCount > 0) {
                    Row(
                        Modifier.fillMaxWidth().clickable { deleteComics = !deleteComics }.padding(top = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Checkbox(checked = deleteComics, onCheckedChange = { deleteComics = it })
                        Text("Also remove its ${pluralize(comicCount, "comic")} from the library")
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = { onConfirm(deleteComics) }) { Text("Delete") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
    )
}

/** Confirms removing a single comic from the library. */
@Composable
fun RemoveComicDialog(title: String, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Remove from library?") },
        text = { Text("Your reading progress for $title will be lost.") },
        confirmButton = { TextButton(onClick = onConfirm) { Text("Remove") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
    )
}
