package org.xg.project.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight

@Composable
fun TabBar(active: String) {
    val items = listOf("首页", "食谱库", "点菜", "历史", "我的")
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(32.dp),
        elevation = CardDefaults.cardElevation(16.dp) // Material3 写法
    ) {
        Row(
            Modifier
                .height(56.dp)
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach {
                val isActive = it == active
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "🔸",
                        Modifier.size(22.dp),
                        color = if (isActive) Color(0xFFF97316) else Color.Gray
                    )
                    Text(
                        it,
                        fontSize = 10.sp,
                        color = if (isActive) Color(0xFFF97316) else Color.Gray,
                        fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}