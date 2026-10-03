package dev.jiaowh.comicreader.ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import dev.jiaowh.comicreader.AppContainer
import dev.jiaowh.comicreader.ComicApp
import dev.jiaowh.comicreader.data.net.SiteProtectionException
import java.io.IOException

/** Builds a ViewModel factory with access to the app container and nav arguments. */
inline fun <reified VM : ViewModel> appViewModelFactory(
    crossinline create: (container: AppContainer, handle: SavedStateHandle) -> VM,
): ViewModelProvider.Factory = viewModelFactory {
    initializer {
        val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as ComicApp
        create(app.container, createSavedStateHandle())
    }
}

fun Throwable.userMessage(): String = when (this) {
    is SiteProtectionException -> message.orEmpty()
    is IOException -> message ?: "Network error. Check your connection."
    else -> message ?: "Something went wrong."
}

@Composable
fun ErrorView(
    error: Throwable,
    onRetry: () -> Unit,
    onVerify: () -> Unit,
    modifier: Modifier = Modifier,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
) {
    Column(
        modifier = modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(error.userMessage(), textAlign = TextAlign.Center, color = contentColor)
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            if (error is SiteProtectionException) {
                Button(onClick = onVerify) { Text("Verify") }
                OutlinedButton(onClick = onRetry) { Text("Retry") }
            } else {
                Button(onClick = onRetry) { Text("Retry") }
            }
        }
    }
}

@Composable
fun CenteredLoading(modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
}

fun pluralize(count: Int, word: String) = "$count $word${if (count == 1) "" else "s"}"
