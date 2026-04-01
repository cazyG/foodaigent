package org.xg.project.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RecipesScreen() {
    Column(Modifier.fillMaxSize().background(Color(0xFFFCFAF2)).verticalScroll(rememberScrollState())) {
        // 顶部标题和新建按钮
        Row(
            Modifier.fillMaxWidth().background(Color.White).padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically
        ) {
            Text("食谱灵感库", fontWeight = FontWeight.Bold, fontSize = 22.sp)
            Button(onClick = {}, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFEDD5))) {
                Text("+ 手动录入", color = Color(0xFFF59E42))
            }
        }
        TextField(
            value = "", onValueChange = {}, modifier = Modifier
                .fillMaxWidth().padding(horizontal = 16.dp),
            placeholder = { Text("想吃什么？搜索菜名或食材") }
        )
        Row(Modifier.horizontalScroll(rememberScrollState()).padding(12.dp)) {
            CategoryButton("全部", true)
            CategoryButton("快手菜")
            CategoryButton("硬菜/大餐")
            CategoryButton("汤粥")
            CategoryButton("减脂餐")
            CategoryButton("甜品")
        }

        // 食谱卡片区域
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 160.dp),
            contentPadding = PaddingValues(all = 12.dp)
        ) {
            items(sampleRecipes) { r -> RecipeCard(r) }
        }
        Spacer(Modifier.height(96.dp))
    }
    TabBar(active = "食谱库")
}

@Composable
fun CategoryButton(label: String, selected: Boolean = false) {
    Button(
        onClick = {}, Modifier.padding(end = 8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) Color(0xFFF59E42) else Color.White,
            contentColor = if (selected) Color.White else Color(0xFF4B5563)
        )
    ) { Text(label, fontSize = 14.sp) }
}

data class Recipe(val name: String, val duration: String, val difficulty: String, val tag: String, val img: String? = null)
val sampleRecipes = listOf(
    Recipe("经典红烧肉", "45分钟", "中等难度", "老公爱吃"),
    Recipe("牛油果大虾沙拉", "15分钟", "新手入门", "老婆最爱"),
    Recipe("西红柿炒鸡蛋", "10分钟", "必点基础", ""),
    Recipe("秘制宫保鸡丁", "25分钟", "挑战厨艺", ""),
    Recipe("孔雀开屏清蒸鱼", "20分钟", "颜值极高", "低脂健康"),
    Recipe("正宗麻婆豆腐", "15分钟", "下饭神器", ""),
    Recipe("冬瓜薏米排骨汤", "90分钟", "滋补养生", ""),
    Recipe("蒜蓉粉丝蒸大虾", "20分钟", "宴客之选", "")
)

@Composable
fun RecipeCard(recipe: Recipe) {
    Card(
        Modifier
            .padding(8.dp)
            .width(160.dp), shape = RoundedCornerShape(24.dp)
    ) {
        Column {
            Box(Modifier.height(96.dp).fillMaxWidth().background(Color.Gray)) {
                if (recipe.tag.isNotEmpty()) {
                    Box(
                        Modifier
                            .padding(6.dp)
                            .background(Color.White.copy(alpha = 0.8f), RoundedCornerShape(8.dp))
                            .align(Alignment.TopEnd)
                    ) {
                        Text(recipe.tag, fontSize = 10.sp, color = Color(0xFFF97316))
                    }
                }
            }
            Column(Modifier.padding(8.dp)) {
                Text(recipe.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, maxLines = 1)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(recipe.duration, fontSize = 10.sp, color = Color.Gray)
                    Text(" | ", fontSize = 10.sp, color = Color.LightGray)
                    Text(recipe.difficulty, fontSize = 10.sp, color = Color(0xFFF97316))
                }
            }
        }
    }
}