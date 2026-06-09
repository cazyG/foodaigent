package org.xg.project

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type

/**
 * 桌面端全局 ESC：将主窗口隐藏到系统托盘。
 */
@Composable
fun DesktopEscapeToTrayHandler(
    enabled: Boolean,
    onHideToTray: () -> Unit,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .onPreviewKeyEvent { event ->
                if (!enabled) return@onPreviewKeyEvent false
                if (event.type == KeyEventType.KeyDown && event.key == Key.Escape) {
                    onHideToTray()
                    true
                } else {
                    false
                }
            },
    ) {
        content()
    }
}
