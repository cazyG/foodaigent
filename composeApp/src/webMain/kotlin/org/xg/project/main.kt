package org.xg.project

import aigent.composeapp.generated.resources.ChineseFontPreloadGate
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    ComposeViewport {
        ChineseFontPreloadGate {
            App()
        }
    }
}
