package org.xg.project.core.navigation

import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.window.core.layout.WindowSizeClass

enum class AppAdaptiveLayout {
    Compact,
    Medium,
    Expanded,
}

val AppAdaptiveLayout.isMediumOrExpanded: Boolean
    get() = this == AppAdaptiveLayout.Medium || this == AppAdaptiveLayout.Expanded

@Composable
fun currentAppAdaptiveLayout(): AppAdaptiveLayout {
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    return when {
        windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND) ->
            AppAdaptiveLayout.Expanded
        windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND) ->
            AppAdaptiveLayout.Medium
        else -> AppAdaptiveLayout.Compact
    }
}
