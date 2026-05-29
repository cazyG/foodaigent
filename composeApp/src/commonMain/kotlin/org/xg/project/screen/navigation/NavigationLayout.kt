package org.xg.project.screen.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.xg.project.Routes.BottomTabRoute

fun isTabletLandscape(maxWidth: Dp, maxHeight: Dp): Boolean =
    maxWidth > maxHeight && maxWidth >= 840.dp

fun isDesktopWidth(width: Dp): Boolean = width >= 1200.dp

data class AppTabNavItem(
    val route: BottomTabRoute,
    val label: String,
    val icon: ImageVector,
)

val appTabNavItems: List<AppTabNavItem> = listOf(
    AppTabNavItem(BottomTabRoute.Home, "首页", Icons.Default.Home),
    AppTabNavItem(BottomTabRoute.Recipes, "菜谱", Icons.Default.Book),
    AppTabNavItem(BottomTabRoute.History, "历史", Icons.Default.History),
    AppTabNavItem(BottomTabRoute.Profile, "我的", Icons.Default.Person),
)

@Composable
fun AppDesktopSidebar(
    activeTab: BottomTabRoute,
    onTabClick: (BottomTabRoute) -> Unit,
    modifier: Modifier = Modifier,
    footer: @Composable ColumnScope.() -> Unit = {},
) {
    Column(
        modifier = modifier
            .width(220.dp)
            .fillMaxHeight()
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 24.dp),
    ) {
        Text(
            text = "黄小厨",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF8B5E3C),
            modifier = Modifier.padding(bottom = 24.dp),
        )
        appTabNavItems.forEach { tab ->
            val selected = tab.route == activeTab
            Column(
                modifier = Modifier
                    .padding(vertical = 4.dp)
                    .clickable { onTabClick(tab.route) }
                    .padding(vertical = 10.dp, horizontal = 12.dp),
                horizontalAlignment = Alignment.Start,
            ) {
                Icon(
                    imageVector = tab.icon,
                    contentDescription = tab.label,
                    tint = if (selected) Color(0xFFF97316) else Color(0xFF9CA3AF),
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = tab.label,
                    fontSize = 13.sp,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                    color = if (selected) Color(0xFFF97316) else Color(0xFF6B7280),
                )
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        footer()
    }
}
