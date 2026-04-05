package org.xg.project.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.todayIn
import kotlin.time.Clock

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun IndexScreen(
    onAddPlan: () -> Unit   // 点击“去添加计划”时触发，用于跳转到点餐页面
) {
    val today = remember {
        val timeZone = TimeZone.currentSystemDefault()
        Clock.System.todayIn(timeZone)
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(Color(0xFFFCFAF2))
            .safeContentPadding()
    ) {
        // 顶部栏
        Row(
            Modifier.fillMaxWidth().background(Color.White),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(modifier = Modifier.padding(top = 6.dp, start = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier
                        .background(Color(0xFFFBBF24), RoundedCornerShape(12.dp))
                        .padding(8.dp)
                ) {
                    Text("黄小厨", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
            Text("${today.year}年${today.month.number}月${today.day}日", color = Color(0xFFF59E42))
        }

        // 早餐、午餐、晚餐均分剩余高度
        MenuSection(
            title = "早餐",
            titleColor = Color(0xFF39EC51),
            menus = listOf(
                MenuItemData(
                    name = "全麦欧包 & 煎蛋",
                    desc = "配料：黑咖啡、蓝莓、无糖酸奶",
                    chef = "丈夫掌勺"
                ),
                MenuItemData(
                    name = "全麦欧包 & 煎蛋1",
                    desc = "配料：黑咖啡、蓝莓、无糖酸奶",
                    chef = "丈夫掌勺"
                ),
                MenuItemData(
                    name = "全麦欧包 & 煎蛋2",
                    desc = "配料：黑咖啡、蓝莓、无糖酸奶",
                    chef = "丈夫掌勺"
                )
            ),
            modifier = Modifier.weight(1f),
            onAddPlan = onAddPlan
        )

        MenuSection(
            title = "午餐",
            menus = listOf(
                MenuItemData(
                    name = "清蒸鲈鱼 & 蚝油生菜",
                    desc = "配料：糙米饭、排骨海带汤",
                    chef = "妻子掌勺"
                ),
                // ... 省略重复数据，实际代码保持不变
            ),
            modifier = Modifier.weight(1f),
            onAddPlan = onAddPlan
        )

        MenuSection(
            title = "晚餐",
            menus = listOf(),
            modifier = Modifier.weight(1f),
            onAddPlan = onAddPlan
        )
    }
}

// 菜单项数据结构
data class MenuItemData(
    val name: String,
    val desc: String,
    val chef: String
)

// 带标题和网格的菜单区块，支持空状态和点击添加计划
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MenuSection(
    title: String,
    titleColor: Color = Color(0xFFF59E42),
    menus: List<MenuItemData>,
    modifier: Modifier = Modifier,
    onAddPlan: () -> Unit
) {
    Column(modifier = modifier) {
        Text(
            title,
            fontSize = 24.sp,
            color = titleColor,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 18.dp, top = 18.dp, bottom = 8.dp)
        )

        if (menus.isEmpty()) {
            // 空状态：可点击区域占满剩余空间，点击调用 onAddPlan
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clickable { onAddPlan() },
                contentAlignment = Alignment.Center
            ) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = titleColor.copy(alpha = 0.1f))
                ) {
                    Text(
                        "➕ 去添加计划",
                        fontSize = 18.sp,
                        color = titleColor,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
                    )
                }
            }
        } else {
            // 有菜单：显示网格列表
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 120.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                items(menus) { item ->
                    MenuCard(title, item.name, item.desc, chef = item.chef)
                }
            }
        }
    }
}

// 菜单卡片（保持不变）
@Composable
fun MenuCard(
    meal: String,
    name: String,
    desc: String,
    chef: String,
    labelColor: Color = Color(0xFF60A5FA)
) {
    Card(
        modifier = Modifier,
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(Modifier.padding(12.dp)) {
            Row(
                Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    Modifier
                        .background(labelColor.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) { Text(meal, color = labelColor, fontSize = 12.sp) }
                Text(chef, fontSize = 12.sp, color = Color.Gray)
            }
            Text(name, fontWeight = FontWeight.Bold, fontSize = 16.sp, modifier = Modifier.padding(top = 4.dp))
            if (desc.isNotEmpty()) Text(desc, fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(top = 2.dp))
        }
    }
}