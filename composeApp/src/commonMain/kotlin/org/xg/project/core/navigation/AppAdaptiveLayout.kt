package org.xg.project.core.navigation

import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.ExperimentalMaterial3AdaptiveNavigationSuiteApi
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.window.core.layout.WindowSizeClass

enum class AppAdaptiveLayout {
    Compact,
    Medium,
    Expanded,
}

val AppAdaptiveLayout.isMediumOrExpanded: Boolean
    get() = this == AppAdaptiveLayout.Medium || this == AppAdaptiveLayout.Expanded

@OptIn(ExperimentalMaterial3AdaptiveNavigationSuiteApi::class)
fun AppAdaptiveLayout.navigationSuiteType(): NavigationSuiteType = when (this) {
    AppAdaptiveLayout.Compact -> NavigationSuiteType.NavigationBar
    AppAdaptiveLayout.Medium -> NavigationSuiteType.NavigationRail
    AppAdaptiveLayout.Expanded -> NavigationSuiteType.NavigationDrawer
}

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
