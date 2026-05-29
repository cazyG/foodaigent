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
        width = 1280.dp,
        height = 800.dp,
        position = WindowPosition.Aligned(androidx.compose.ui.Alignment.Center),
    )

    Window(
        onCloseRequest = ::exitApplication,
        title = "锅铲黄炒",
        icon = painterResource(Res.drawable.app_icon),
        state = windowState,
//        alwaysOnTop = true,//是否成为悬浮窗
        undecorated = true,   // 移除系统标题栏和边框
        transparent = true,   // 窗口背景透明，才能显示我们自己绘制的圆角
    ) {
        App()
    }
}