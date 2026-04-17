package org.xg.project.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import org.xg.project.Routes.BottomTabRoute

private data class TabItem(val label: String, val icon: ImageVector, val route: BottomTabRoute)

@Composable
fun BottomTabBar(
    activeTab: BottomTabRoute,
    onTabClick: (BottomTabRoute) -> Unit
) {

    val items = listOf(
        TabItem("首页", Icons.Default.Home, BottomTabRoute.Home),
        TabItem("食谱库", Icons.Default.Book, BottomTabRoute.Recipes),
        TabItem("历史", Icons.Default.History, BottomTabRoute.History),
        TabItem("我的", Icons.Default.Person, BottomTabRoute.Profile),
    )

    Card(
        Modifier
            .padding(horizontal = 8.dp, vertical = 8.dp)
            .fillMaxWidth()
            .height(70.dp)
            .glassPanelStrong(RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(0.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
    ) {
        Row(
            Modifier.fillMaxSize().padding(horizontal = 14.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            items.forEach { tab ->
                val isActive = tab.route == activeTab
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                        .clickable {
                            onTabClick(tab.route)
                        }
                ) {
                    Icon(
                        imageVector = tab.icon,
                        contentDescription = tab.label,
                        modifier = Modifier.size(32.dp),
                        tint = if (isActive) Color(0xFFF97316) else Color(0xFFC2C2C2)
                    )
                    Text(
                        text = tab.label,
                        fontSize = 11.sp,
                        fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                        color = if (isActive) Color(0xFFF97316) else Color(0xFFC2C2C2),
                    )
                }
            }
        }
    }
}
