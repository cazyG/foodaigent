package org.xg.project.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import org.koin.compose.viewmodel.koinViewModel
import org.xg.project.domain.model.DailyMenuRecord
import org.xg.project.presentation.history.HistoryViewModel
import org.xg.project.presentation.history.HistoryIntent

@Composable
fun HistoryScreen(
    bottomBar: @Composable () -> Unit = {},
    viewModel: HistoryViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val scrollState = rememberScrollState()

    Scaffold(
        bottomBar = bottomBar,
        containerColor = Color.Transparent,
        modifier = Modifier.fillMaxSize().background(GlassStyle.BgGradient)
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding).verticalScroll(scrollState)
        ) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .background(GlassStyle.SurfaceStrong)
                    .border(1.dp, GlassStyle.Stroke, RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp))
                    .padding(24.dp)
            ) {
                Column {
                    Text("饮食记忆", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = GlassStyle.TextPrimary)
                    Text("2026年累计点餐 86 次", fontSize = 12.sp, color = GlassStyle.TextSecondary)
                }
            }

            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = GlassStyle.AccentStrong)
                }
            } else {
                Spacer(Modifier.height(16.dp))
                Text("饮食记录流", fontWeight = FontWeight.Bold, color = GlassStyle.TextSecondary, modifier = Modifier.padding(16.dp, 4.dp, 0.dp, 4.dp))

                Column {
                    state.dailyRecords.forEach { record ->
                        MealRecord(record = record)
                    }
                }
            }
        }
    }
}

@Composable
fun MealRecord(
    record: DailyMenuRecord
) {
    Card(
        Modifier
            .padding(start = 32.dp, end = 16.dp, bottom = 12.dp)
            .glassPanel(RoundedCornerShape(32.dp)),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        GlassHighlight {
            Column(Modifier.padding(16.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(record.date, color = GlassStyle.TextSecondary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                if (record.stars > 0) {
                    Row {
                        val fullStars = record.stars.toInt()
                        repeat(fullStars) { Text("⭐", fontSize = 14.sp, color = Color(0xFFFBBF24)) }
                        if (record.stars - fullStars >= 0.5f) Text("⭐", fontSize = 14.sp, color = Color(0x80FBBF24))
                    }
                }
            }
            Spacer(Modifier.height(8.dp))

            // 展示各个餐段的数据
            if (record.breakfast.isNotEmpty()) {
                Text("早餐：${record.breakfast.joinToString("，") { it.name }}", fontSize = 14.sp, color = GlassStyle.TextPrimary, fontWeight = FontWeight.Medium)
            }
            if (record.lunch.isNotEmpty()) {
                Text("午餐：${record.lunch.joinToString("，") { it.name }}", fontSize = 14.sp, color = GlassStyle.TextPrimary, fontWeight = FontWeight.Medium)
            }
            if (record.dinner.isNotEmpty()) {
                Text("晚餐：${record.dinner.joinToString("，") { it.name }}", fontSize = 14.sp, color = GlassStyle.TextPrimary, fontWeight = FontWeight.Medium)
            }
            if (record.snack.isNotEmpty()) {
                Text("宵夜：${record.snack.joinToString("，") { it.name }}", fontSize = 14.sp, color = GlassStyle.TextPrimary, fontWeight = FontWeight.Medium)
            }

            if (record.comment.isNotEmpty()) Text(record.comment, fontSize = 13.sp, color = GlassStyle.TextSecondary, modifier = Modifier.padding(top = 6.dp))
            if (record.imgUrl != null) {
                Spacer(Modifier.height(12.dp))
                AsyncImage(
                    model = record.imgUrl,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp).clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop
                )
            }
        }
        }
    }
}


@Composable
fun SectionTitle(title: String, action: String? = null) {
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        if (action != null) Text(action, color = Color(0xFFF59E42), fontSize = 14.sp)
    }
}

@Composable
fun MenuCard(meal: String, name: String, desc: String, chef: String, isPending: Boolean = false, labelColor: Color = Color(0xFF60A5FA)) {
    Card(
        modifier = Modifier, // 正确用法
        shape = RoundedCornerShape(24.dp),
        border = if (isPending) BorderStroke(2.dp, Color(0xFF6366F1)) else null
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
            if (isPending) {
                Button(
                    onClick = { /*TODO*/ },
                    Modifier.fillMaxWidth().padding(top = 8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E42))
                ) {
                    Text("去点菜", color = Color.White)
                }
            }
        }
    }
}

@Composable
fun CalendarSection() {
    Card(
        Modifier.padding(16.dp), shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("点菜日历", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text("本月已打卡 22 天", fontSize = 12.sp, color = Color.Gray)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    Text("2026年3月", Modifier.padding(horizontal = 8.dp), fontWeight = FontWeight.Bold)
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                }
            }
            CalendarGrid() // 用自定义的 Compose 日历组件实现
        }
    }
}

@Composable
fun CalendarGrid() {
    // 实际可用 LazyVerticalGrid 或 Row/Column 设置点菜打卡标记和样式
    Text("实现略（模拟日历 grid，可用 LazyVerticalGrid或自己实现）", color = Color.LightGray, fontSize = 12.sp)
}

@Composable
fun ReviewCard(user: String, time: String, content: String, target: String) {
    Card(Modifier.padding(horizontal = 16.dp, vertical = 4.dp), shape = RoundedCornerShape(24.dp)) {
        Row(Modifier.padding(12.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(user, fontWeight = FontWeight.Bold)
                Text(time, fontSize = 10.sp, color = Color.Gray)
            }
            Text(content, fontSize = 14.sp, modifier = Modifier.padding(top = 2.dp))
            Box(Modifier.background(Color(0xFFF9FAFB), RoundedCornerShape(8.dp)).padding(4.dp).padding(top = 4.dp)) {
                Text("针对：$target", fontSize = 10.sp, color = Color.Gray)
            }
        }
    }
}
