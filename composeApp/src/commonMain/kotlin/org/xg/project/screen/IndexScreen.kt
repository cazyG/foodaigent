package org.xg.project.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.Scaffold
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.koin.compose.viewmodel.koinViewModel
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

import org.xg.project.domain.model.DailyMenuRecord
import org.xg.project.domain.model.MenuItemData
import org.xg.project.presentation.index.IndexViewModel
import org.xg.project.presentation.index.IndexIntent

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun IndexScreen(
    onAddPlan: () -> Unit,
    viewModel: IndexViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val today = remember {
        val timeZone = TimeZone.currentSystemDefault()
        Clock.System.todayIn(timeZone)
    }

    val scrollState = rememberScrollState()

    val todayRecord = state.todayRecord ?: DailyMenuRecord(
        date = "${today.year}.${today.month.number}.${today.day} (今天)",
        breakfast = emptyList(),
        lunch = emptyList(),
        dinner = emptyList(),
        snack = emptyList()
    )

    Box(
        modifier = Modifier.fillMaxSize().background(GlassStyle.BgGradient)
    ) {
        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = GlassStyle.AccentStrong)
            }
            return@Box
        }

        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .background(GlassStyle.SurfaceStrong)
                    .border(1.dp, GlassStyle.Stroke, RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp)),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.padding(top = 6.dp, start = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        Modifier
                            .glassPanelStrong(RoundedCornerShape(12.dp))
                            .padding(8.dp)
                    ) {
                        Text("黄小厨", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = GlassStyle.TextPrimary)
                    }
                }
                Text("${today.year}年${today.month.number}月${today.day}日", color = GlassStyle.TextPrimary)
            }

            val openRecipes = { onAddPlan() }

            MenuSection(
                title = "早餐",
                titleColor = GlassStyle.TextPrimary,
                menus = todayRecord.breakfast,
                modifier = Modifier.wrapContentSize(),
                showReviewButton = state.canReviewBreakfast,
                onReviewClick = { /* TODO */ },
                onAddPlan = openRecipes
            )

            MenuSection(
                title = "午餐",
                menus = todayRecord.lunch,
                modifier = Modifier.wrapContentSize(),
                showReviewButton = state.canReviewLunch,
                onReviewClick = { /* TODO */ },
                onAddPlan = openRecipes
            )

            MenuSection(
                title = "晚餐",
                menus = todayRecord.dinner,
                modifier = Modifier.wrapContentSize(),
                showReviewButton = state.canReviewDinner,
                onReviewClick = { /* TODO */ },
                onAddPlan = openRecipes
            )

            MenuSection(
                title = "宵夜",
                titleColor = GlassStyle.TextPrimary,
                menus = todayRecord.snack,
                modifier = Modifier.wrapContentSize(),
                showReviewButton = state.canReviewSnack,
                onReviewClick = { /* TODO */ },
                onAddPlan = openRecipes
            )
        }
    }
}

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
                    color = GlassStyle.AccentStrong,
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
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                    modifier = Modifier.glassPanelStrong(RoundedCornerShape(16.dp))
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
        modifier = Modifier.glassPanel(RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Column(Modifier.padding(12.dp)) {
            Row(
                Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    Modifier
                        .background(Color.White.copy(alpha = 0.20f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) { Text(meal, color = labelColor, fontSize = 12.sp) }
                Text(chef, fontSize = 12.sp, color = GlassStyle.TextSecondary)
            }
            Text(name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = GlassStyle.TextPrimary, modifier = Modifier.padding(top = 4.dp))
            if (desc.isNotEmpty()) Text(desc, fontSize = 12.sp, color = GlassStyle.TextSecondary, modifier = Modifier.padding(top = 2.dp))
        }
    }
}
