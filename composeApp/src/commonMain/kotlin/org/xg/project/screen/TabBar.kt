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
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import org.xg.project.Routes.Routes

data class TabItem(val label: String, val icon: ImageVector, val route: String)

@Composable
fun TabBar(
    activeRoute: String,
    onTabClick: (String) -> Unit
) {
    print("isActive ${activeRoute} ")

    val items = listOf(
        TabItem("首页", Icons.Default.Home, Routes.Home),
        TabItem("食谱库", Icons.Default.Book, Routes.Recipes),
        TabItem("点菜", Icons.Default.AddCircle, Routes.Plan),
        TabItem("历史", Icons.Default.History, Routes.History),
        TabItem("我的", Icons.Default.Person, Routes.Profile),
    )

    Card(
        Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
            .fillMaxWidth()
            .height(70.dp),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.92f)),
        elevation = CardDefaults.cardElevation(16.dp),
    ) {
        Row(
            Modifier.fillMaxSize().padding(horizontal = 28.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            items.forEach { tab ->
                val isActive = tab.route == activeRoute
                print("isActive ${isActive} ${tab.label} ${activeRoute} \n")
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
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
                        modifier = Modifier.size(28.dp),
                        tint = if (isActive) Color(0xFFF97316) else Color(0xFFC2C2C2)
                    )
                    Text(
                        tab.label,
                        fontSize = 11.sp,
                        fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                        color = if (isActive) Color(0xFFF97316) else Color(0xFFC2C2C2),
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        }
    }
}