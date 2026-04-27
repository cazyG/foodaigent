package org.xg.project

import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import androidx.compose.ui.unit.dp

fun main() = application {
    val windowState = rememberWindowState(
        width = 800.dp,
        height = 600.dp,
        position = WindowPosition.Aligned(androidx.compose.ui.Alignment.Center)
    )

    Window(
        onCloseRequest = ::exitApplication,
        title = "锅铲黄炒",
        icon = painterResource("app_icon.png"),
        state = windowState
    ) {
        App()
    }
}