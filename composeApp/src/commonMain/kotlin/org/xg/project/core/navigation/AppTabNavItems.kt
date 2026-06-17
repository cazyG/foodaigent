package org.xg.project.core.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

data class AppTabNavItem(
    val label: String,
    val icon: ImageVector,
    val route: BottomTabRoute,
)

val appTabNavItems = listOf(
    AppTabNavItem("首页", Icons.Default.Home, BottomTabRoute.Home),
    AppTabNavItem("食谱库", Icons.Default.Book, BottomTabRoute.Recipes),
    AppTabNavItem("历史", Icons.Default.History, BottomTabRoute.History),
    AppTabNavItem("我的", Icons.Default.Person, BottomTabRoute.Profile),
)
