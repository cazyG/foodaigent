package org.xg.project.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import kotlinx.datetime.toLocalDateTime
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

    val scrollState = rememberScrollState()

    val now = remember {
        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    }
    val currentHour = now.hour
    val showBreakfastReview = currentHour >= 9
    val showLunchReview = currentHour >= 12
    val showDinnerReview = currentHour > 19
    val showSnackReview = currentHour >= 21

    Column(
        Modifier
            .fillMaxSize()
            .background(Color(0xFFFCFAF2))
            .safeContentPadding()
            .verticalScroll(scrollState)
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

        // 早餐、午餐、晚餐、宵夜根据内容自适应高度，整体可滚动
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
            modifier = Modifier.wrapContentSize(),
            showReviewButton = showBreakfastReview,
            onReviewClick = { /* TODO */ },
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
            modifier = Modifier.wrapContentSize(),
            showReviewButton = showLunchReview,
            onReviewClick = { /* TODO */ },
            onAddPlan = onAddPlan
        )

        MenuSection(
            title = "晚餐",
            menus = listOf(),
            modifier = Modifier.wrapContentSize(),
            showReviewButton = showDinnerReview,
            onReviewClick = { /* TODO */ },
            onAddPlan = onAddPlan
        )

        MenuSection(
            title = "宵夜",
            titleColor = Color(0xFF9C27B0),
            menus = listOf(),
            modifier = Modifier.wrapContentSize(),
            showReviewButton = showSnackReview,
            onReviewClick = { /* TODO */ },
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
    showReviewButton: Boolean = false,
    onReviewClick: () -> Unit = {},
    onAddPlan: () -> Unit
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 18.dp, top = 18.dp, bottom = 8.dp, end = 18.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                title,
                fontSize = 24.sp,
                color = titleColor,
                fontWeight = FontWeight.Bold
            )

            if (showReviewButton) {
                Text(
                    "去评价 >",
                    fontSize = 14.sp,
                    color = Color(0xFF6366F1),
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .clickable { onReviewClick() }
                        .padding(4.dp)
                )
            }
        }

        if (menus.isEmpty()) {
            // 空状态：固定高度，点击调用 onAddPlan
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .padding(horizontal = 12.dp)
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
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                menus.chunked(2).forEach { rowMenus ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        for (item in rowMenus) {
                            Box(modifier = Modifier.weight(1f)) {
                                MenuCard(title, item.name, item.desc, chef = item.chef)
                            }
                        }
                        if (rowMenus.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
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