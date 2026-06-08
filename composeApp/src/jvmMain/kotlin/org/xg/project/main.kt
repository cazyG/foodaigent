package org.xg.project

import aigent.composeapp.generated.resources.Res
import aigent.composeapp.generated.resources.app_icon
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import java.awt.Desktop
import org.jetbrains.compose.resources.painterResource

private const val APP_TITLE = "锅铲黄炒"

fun main() = application {
    var isWindowVisible by remember { mutableStateOf(true) }
    val windowIcon = painterResource(Res.drawable.app_icon)
    val windowState = rememberWindowState(
        width = 1280.dp,
        height = 800.dp,
        position = WindowPosition.Aligned(Alignment.Center),
    )

    fun showMainWindow() {
        isWindowVisible = true
    }

    fun hideMainWindow() {
        isWindowVisible = false
    }

    val trayController = remember {
        JvmSystemTrayController.install(
            tooltip = APP_TITLE,
            onShowWindow = ::showMainWindow,
            onHideWindow = ::hideMainWindow,
            isWindowVisible = { isWindowVisible },
            onExit = ::exitApplication,
        )
    }

    DisposableEffect(trayController) {
        onDispose {
            trayController?.dispose()
        }
    }

    LaunchedEffect(isWindowVisible) {
        trayController?.updateMenuLabels()
        if (isWindowVisible) {
            runCatching {
                Desktop.getDesktop().requestForeground(true)
            }
        }
    }

    Window(
        onCloseRequest = ::hideMainWindow,
        visible = isWindowVisible,
        title = APP_TITLE,
        icon = windowIcon,
        state = windowState,
    ) {
        App()
    }
}
