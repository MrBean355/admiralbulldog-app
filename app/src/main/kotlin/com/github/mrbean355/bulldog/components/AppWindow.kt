package com.github.mrbean355.bulldog.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberWindowState

val DefaultWindowSize: DpSize get() = DpSize(600.dp, 400.dp)

@Composable
@OptIn(ExperimentalComposeUiApi::class)
fun AppWindow(
    title: String,
    size: DpSize = DefaultWindowSize,
    escapeClosesWindow: Boolean = true,
    onCloseRequest: () -> Unit,
    content: @Composable FrameWindowScope.() -> Unit
) {
    Window(
        icon = painterResource("images/bulldog.jpg"),
        title = title,
        resizable = false,
        state = rememberWindowState(
            position = WindowPosition(Alignment.Center),
            size = size
        ),
        onCloseRequest = onCloseRequest,
        onPreviewKeyEvent = {
            if (escapeClosesWindow && it.key == Key.Escape && it.type == KeyEventType.KeyDown) {
                onCloseRequest()
                true
            } else {
                false
            }
        }
    ) {
        MaterialTheme {
            Surface(modifier = Modifier.size(size)) {
                content()
            }
        }
    }
}