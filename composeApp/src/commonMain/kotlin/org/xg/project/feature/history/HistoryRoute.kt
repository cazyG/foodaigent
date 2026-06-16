package org.xg.project.feature.history

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import org.koin.compose.viewmodel.koinViewModel
import org.xg.project.domain.model.DailyMenuRecord
import org.xg.project.domain.model.MenuItemData
import org.xg.project.core.navigation.isTabletLandscape

private val HistoryBg = Color(0xFFF3F5FB)
private val HistoryCard = Color.White
private val HistoryBorder = Color(0xFFE7EAF0)
private val HistoryPrimary = Color(0xFF2B2B2B)
private val HistorySecondary = Color(0xFF7B8190)
private val HistoryAccent = Color(0xFFF0A142)

private data class HistoryMealUi(
    val label: String,
    val time: String,
    val title: String,
    val desc: String,
    val imageUrl: String?,
)

private data class HistoryDayUi(
    val dateTitle: String,
    val rawDate: String,
    val meals: List<HistoryMealUi>,
    val comment: String,
    val dayBadge: String? = null,
)

private data class HistoryContentUi(
    val totalMeals: Int,
    val activeDays: Int,
    val monthTitle: String,
    val days: List<HistoryDayUi>,
)

@Composable
fun HistoryScreen(viewModel: HistoryViewModel = koinViewModel()) {
    val state by viewModel.uiState.collectAsState()
    val content = remember(state.dailyRecords) { state.dailyRecords.toHistoryContentUi() }

    BoxWithConstraints(modifier = Modifier.fillMaxSize().background(HistoryBg)) {
        // History 页面运行在应用主内容区（左侧有导航栏），可用宽度会小于整窗宽度；
        // 使用更贴近主内容区的阈值，避免在 PC 上误判为移动布局。
        val desktopMode = isTabletLandscape(maxWidth, maxHeight) && maxWidth >= 900.dp
        when {
            state.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = HistoryAccent)
            }

            desktopMode -> DesktopHistoryLayout(content = content)
            else -> MobileHistoryLayout(content = content)
        }
    }
}

@Composable
private fun MobileHistoryLayout(content: HistoryContentUi) {
    Scaffold(
        containerColor = HistoryBg,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { },
                containerColor = Color(0xFFB5761B),
                contentColor = Color.White,
            ) { Icon(Icons.Default.Add, contentDescription = "添加") }
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item { MobileHeader(content = content) }
            items(content.days.size) { idx ->
                MobileDayCard(content.days[idx], isLatest = idx == 0)
            }
        }
    }
}

@Composable
private fun MobileHeader(content: HistoryContentUi) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("饮食记忆", fontSize = 34.sp, fontWeight = FontWeight.Bold, color = HistoryPrimary)
            Spacer(Modifier.weight(1f))
            Icon(Icons.Default.Settings, contentDescription = null, tint = HistorySecondary)
        }
        Box(
            Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp))
                .background(HistoryAccent).padding(14.dp),
        ) {
            Column {
                Text("2026年餐饮食记", color = Color.White.copy(alpha = 0.9f), fontSize = 12.sp)
                Text("累积 ${content.totalMeals} 餐", color = Color.White, fontSize = 40.sp, fontWeight = FontWeight.ExtraBold)
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    MobileMetricPill("佳餐 ${content.activeDays * 2}")
                    MobileMetricPill("卡食 ${content.totalMeals / 3}")
                }
            }
        }
        Box(
            Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(HistoryCard)
                .border(1.dp, HistoryBorder, RoundedCornerShape(14.dp)).padding(12.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("多端同步", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = HistoryPrimary)
                    Text("扫描右侧二维码，与家人分享今日菜单", fontSize = 13.sp, color = HistorySecondary, lineHeight = 20.sp)
                }
                Box(
                    Modifier.size(66.dp).clip(RoundedCornerShape(8.dp)).background(Color(0xFFF2F4F8)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("QR", color = HistorySecondary, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun MobileMetricPill(text: String) {
    Box(
        Modifier.clip(RoundedCornerShape(999.dp)).background(Color.White.copy(alpha = 0.25f))
            .padding(horizontal = 10.dp, vertical = 5.dp),
    ) {
        Text(text, color = Color.White, fontSize = 12.sp)
    }
}

@Composable
private fun MobileDayCard(day: HistoryDayUi, isLatest: Boolean) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(5.dp).clip(CircleShape).background(HistoryAccent))
            Spacer(Modifier.width(8.dp))
            Text(day.dateTitle, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = HistoryPrimary)
            if (isLatest) {
                Spacer(Modifier.width(8.dp))
                Box(Modifier.clip(RoundedCornerShape(99.dp)).background(Color(0xFFE5ECF9)).padding(horizontal = 8.dp, vertical = 3.dp)) {
                    Text("儿童节", fontSize = 10.sp, color = Color(0xFF5D7DC8))
                }
            }
        }
        day.meals.forEach { meal ->
            Box(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(HistoryCard)
                    .border(1.dp, HistoryBorder, RoundedCornerShape(12.dp)).padding(12.dp),
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = when (meal.label) {
                                "早餐" -> Icons.Default.LightMode
                                "午餐" -> Icons.Default.LightMode
                                else -> Icons.Default.DarkMode
                            },
                            contentDescription = null,
                            tint = Color(0xFF8D6A2F),
                            modifier = Modifier.size(16.dp),
                        )
                        Spacer(Modifier.width(6.dp))
                        Text("${meal.label}·${meal.time}", color = HistorySecondary, fontSize = 12.sp)
                    }
                    Text(meal.title, color = HistoryPrimary, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                    if (meal.imageUrl != null) {
                        AsyncImage(
                            model = meal.imageUrl,
                            contentDescription = meal.title,
                            modifier = Modifier.fillMaxWidth().height(110.dp).clip(RoundedCornerShape(10.dp)),
                            contentScale = ContentScale.Crop,
                        )
                    }
                }
            }
        }
        if (day.comment.isNotBlank()) {
            Box(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(HistoryCard)
                    .border(1.dp, HistoryBorder, RoundedCornerShape(12.dp)).padding(12.dp),
            ) {
                Column {
                    Text(day.comment, color = HistorySecondary, lineHeight = 20.sp, fontSize = 13.sp)
                    Spacer(Modifier.height(4.dp))
                    Text("查看详情 ›", color = Color(0xFF9B6A12), fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
private fun DesktopHistoryLayout(content: HistoryContentUi) {
    Row(
        modifier = Modifier.fillMaxSize().padding(horizontal = 18.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("饮食回忆", fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF5C3816))
                Spacer(Modifier.weight(1f))
                Icon(Icons.Default.Search, contentDescription = null, tint = HistorySecondary, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(12.dp))
                Icon(Icons.Default.Settings, contentDescription = null, tint = HistorySecondary, modifier = Modifier.size(18.dp))
            }
            Text("2026年累计记录 ${content.totalMeals} 顿美餐", color = HistorySecondary, fontSize = 14.sp)
            Spacer(Modifier.height(16.dp))
            Text(content.monthTitle, color = HistoryPrimary, fontSize = 36.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))
            FlowRow(
                maxItemsInEachRow = 2,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                content.days.take(4).forEach { day ->
                    DesktopDayCard(day)
                }
            }
        }
        Column(modifier = Modifier.width(300.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            DesktopSummaryCard(content)
            DesktopReviewCard()
            DesktopCameraCard()
        }
    }
}

@Composable
private fun DesktopDayCard(day: HistoryDayUi) {
    Box(
        Modifier.widthIn(min = 270.dp, max = 360.dp).heightIn(min = 220.dp).clip(RoundedCornerShape(14.dp))
            .background(HistoryCard).border(1.dp, HistoryBorder, RoundedCornerShape(14.dp)).padding(14.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (day.dayBadge != null) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .background(Color(0xFFFFF2DD))
                            .padding(horizontal = 8.dp, vertical = 3.dp),
                    ) {
                        Text(day.dayBadge, color = Color(0xFF9A6A1A), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(day.dateTitle, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = HistoryPrimary)
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = if (day.dayBadge == "今天") Icons.Outlined.FavoriteBorder else Icons.Outlined.MoreVert,
                    contentDescription = null,
                    tint = HistorySecondary,
                    modifier = Modifier.size(16.dp),
                )
            }
            day.meals.take(3).forEach { meal ->
                Box(
                    Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(Color(0xFFF6F8FC)).padding(9.dp),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(meal.label, color = Color(0xFF9B6A12), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Spacer(Modifier.width(6.dp))
                        Column(Modifier.weight(1f)) {
                            Text(meal.title, maxLines = 1, overflow = TextOverflow.Ellipsis, color = HistoryPrimary, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                            Text("${meal.time} · ${meal.desc}", fontSize = 11.sp, color = HistorySecondary, maxLines = 1)
                        }
                        if (meal.imageUrl != null) {
                            AsyncImage(
                                model = meal.imageUrl,
                                contentDescription = null,
                                modifier = Modifier.size(46.dp).clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop,
                            )
                        }
                    }
                }
            }
            if (day.comment.isNotBlank()) {
                Text(day.comment, color = HistorySecondary, maxLines = 2, overflow = TextOverflow.Ellipsis, lineHeight = 20.sp, fontSize = 13.sp)
            }
        }
    }
}

@Composable
private fun DesktopSummaryCard(content: HistoryContentUi) {
    Box(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(HistoryAccent).padding(14.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("本月摘要", color = Color(0xFF5E3A11), fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SummaryMetric("在家餐位", "${content.totalMeals / 2} 次", modifier = Modifier.weight(1f))
                SummaryMetric("点赞热度", "1.8k avg", modifier = Modifier.weight(1f))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SummaryMetric("尝新菜单", "${content.days.size.coerceAtLeast(1)} 道", modifier = Modifier.weight(1f))
                SummaryMetric("健康评分", "92 pts", modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun SummaryMetric(title: String, value: String, modifier: Modifier = Modifier) {
    Box(
        modifier.clip(RoundedCornerShape(10.dp)).background(Color.White.copy(alpha = 0.22f)).padding(8.dp),
    ) {
        Column {
            Text(title, color = Color(0xFF6E4B21), fontSize = 11.sp)
            Text(value, color = Color(0xFF5E3817), fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}

@Composable
private fun DesktopReviewCard() {
    Box(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(HistoryCard)
            .border(1.dp, HistoryBorder, RoundedCornerShape(14.dp)).padding(14.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("家人点评", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = HistoryPrimary)
                Spacer(Modifier.weight(1f))
                Text("查看全部", color = Color(0xFF7C5217), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
            }
            ReviewItem(
                user = "妈妈",
                content = "今天的清蒸鲈鱼火候掌握得刚刚好，肉质鲜嫩，全家都爱吃！",
                likes = 12,
                comments = 2,
            )
            HorizontalDivider(color = HistoryBorder)
            ReviewItem(
                user = "爸爸",
                content = "这道红烧肉肥而不腻，很有小时候的味道，下饭神器！",
                likes = 8,
                comments = 1,
            )
        }
    }
}

@Composable
private fun ReviewItem(user: String, content: String, likes: Int, comments: Int) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(verticalAlignment = Alignment.Top) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE7ECF6)),
                contentAlignment = Alignment.Center,
            ) {
                Text(user.take(1), color = Color(0xFF5F6B82), fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(user, color = HistoryPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Text(content, color = HistorySecondary, lineHeight = 20.sp, fontSize = 13.sp)
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Outlined.FavoriteBorder, contentDescription = null, tint = HistorySecondary, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("$likes", color = HistorySecondary, fontSize = 12.sp)
            Spacer(modifier = Modifier.width(12.dp))
            Icon(Icons.Outlined.ChatBubbleOutline, contentDescription = null, tint = HistorySecondary, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("$comments", color = HistorySecondary, fontSize = 12.sp)
        }
    }
}

@Composable
private fun DesktopCameraCard() {
    Box(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(Color(0xFFEFF4FA))
            .border(1.dp, HistoryBorder, RoundedCornerShape(14.dp)).padding(14.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("时光相机", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF374151))
            Text("为每一餐留下最美的注脚", color = HistorySecondary, fontSize = 13.sp)
            Box(
                Modifier.clip(RoundedCornerShape(999.dp)).background(Color(0xFF64748B)).padding(horizontal = 14.dp, vertical = 8.dp),
            ) {
                Text("开始记录", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
            }
        }
    }
}

private fun List<DailyMenuRecord>.toHistoryContentUi(): HistoryContentUi {
    val days = mapIndexed { index, record ->
        HistoryDayUi(
            dateTitle = record.date.toDisplayDateTitle(),
            rawDate = record.date,
            meals = buildList {
                addMeal("早餐", "08:30", record.breakfast, record.imgUrl)
                addMeal("午餐", "12:15", record.lunch, record.imgUrl)
                addMeal("晚餐", "19:00", record.dinner, record.imgUrl)
                addMeal("宵夜", "22:30", record.snack, record.imgUrl)
            },
            comment = record.comment,
            dayBadge = when (index) {
                0 -> "今天"
                1 -> "昨天"
                else -> null
            },
        )
    }
    val totalMeals = days.sumOf { it.meals.size }
    return HistoryContentUi(
        totalMeals = totalMeals,
        activeDays = days.count { it.meals.isNotEmpty() },
        monthTitle = days.firstOrNull()?.rawDate?.toMonthTitle() ?: "六月 June",
        days = days,
    )
}

private fun MutableList<HistoryMealUi>.addMeal(
    label: String,
    time: String,
    items: List<MenuItemData>,
    imageUrl: String?,
) {
    if (items.isEmpty()) return
    val first = items.first()
    add(
        HistoryMealUi(
            label = label,
            time = time,
            title = first.name,
            desc = first.desc,
            imageUrl = imageUrl,
        ),
    )
}

private fun String.toDisplayDateTitle(): String {
    val cleaned = replace("年", "-").replace("月", "-").replace("日", "")
    val parts = cleaned.split("-").filter { it.isNotBlank() }
    return if (parts.size == 3) "${parts[1]}月${parts[2]}日, ${parts[0]}" else this
}

private fun String.toMonthTitle(): String {
    val cleaned = replace("年", "-").replace("月", "-").replace("日", "")
    val parts = cleaned.split("-").filter { it.isNotBlank() }
    val month = parts.getOrNull(1)?.toIntOrNull()
    val english = when (month) {
        1 -> "January"
        2 -> "February"
        3 -> "March"
        4 -> "April"
        5 -> "May"
        6 -> "June"
        7 -> "July"
        8 -> "August"
        9 -> "September"
        10 -> "October"
        11 -> "November"
        12 -> "December"
        else -> "Month"
    }
    return "${month ?: 6}月 $english"
}
