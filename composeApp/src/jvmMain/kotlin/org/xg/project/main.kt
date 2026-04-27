package org.xg.project

import aigent.composeapp.generated.resources.Res
import aigent.composeapp.generated.resources.app_icon
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource

fun main() = application {
    val windowState = rememberWindowState(
        width = 800.dp,
        height = 600.dp,
        position = WindowPosition.Aligned(androidx.compose.ui.Alignment.Center)
    )

    Window(
        onCloseRequest = ::exitApplication,
        title = "锅铲黄炒",
        icon = painterResource(Res.drawable.app_icon),
        state = windowState
    ) {
        App()
    }
}