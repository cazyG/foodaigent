package org.xg.project.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HistoryScreen() {
    Column(
        modifier = Modifier.fillMaxSize().background(Color(0xFFFCFAF2)).verticalScroll(rememberScrollState())
    ) {
        // 顶部栏
        Box(Modifier.fillMaxWidth().background(Color.White).padding(24.dp)) {
            Column {
                Text("饮食记忆", fontWeight = FontWeight.Bold, fontSize = 22.sp)
                Text("2026年累计点餐 86 次", fontSize = 12.sp, color = Color.Gray)
            }
        }

        Spacer(Modifier.height(16.dp))

        // 口味分布
        Card(Modifier.padding(horizontal = 16.dp), shape = RoundedCornerShape(40.dp)) {
            Column(Modifier.padding(24.dp)) {
                Text("口味偏好分布", color = Color.Gray, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Box(Modifier.fillMaxWidth().height(120.dp).background(Color.LightGray)) {
                    Text("雷达图区域（可用Canvas或图片占位）", Modifier.align(Alignment.Center))
                }
            }
        }

        // 历史记录
        Spacer(Modifier.height(16.dp))
        Text("饮食记录流", fontWeight = FontWeight.Bold, color = Color.Gray, modifier = Modifier.padding(16.dp, 4.dp, 0.dp, 4.dp))

        Column {
            MealRecord(
                date = "2026.03.29 (昨天)",
                stars = 5f,
                title = "晚餐：麻辣香锅",
                comment = "“老公大展身手的一次，辣度刚刚好，藕片很清脆！”",
                imgUrl = "https://modao.cc/agent-py/media/generated_images/2026-03-30/8801c842ff4c4601b0eaef5a8bb46f63.jpg"
            )
            MealRecord(
                date = "2026.03.28",
                stars = 4.5f,
                title = "中餐：酸汤肥牛",
                comment = "“老婆辛苦了，肥牛稍微煮老了一点点，但汤汁拌饭无敌。”",
                buttonLabel = "再吃一次"
            )
            MealRecord(
                date = "2026.03.27",
                stars = 4f,
                title = "晚餐：外卖打卡（披萨）",
                comment = "“加班太累了，今天不做饭。这家榴莲披萨料好足。”"
            )
        }
        Spacer(Modifier.height(96.dp))
    }
//    TabBar(active = "历史")
}

@Composable
fun MealRecord(
    date: String,
    stars: Float,
    title: String,
    comment: String = "",
    imgUrl: String? = null,
    buttonLabel: String? = null
) {
    Card(Modifier.padding(start = 32.dp, end = 16.dp, bottom = 12.dp), shape = RoundedCornerShape(32.dp)) {
        Column(Modifier.padding(16.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(date, color = Color.Gray, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Row {
                    val fullStars = stars.toInt()
                    repeat(fullStars) { Text("⭐", fontSize = 14.sp, color = Color(0xFFFBBF24)) }
                    if (stars - fullStars >= 0.5f) Text("⭐", fontSize = 14.sp, color = Color(0x80FBBF24))
                }
            }
            Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp, modifier = Modifier.padding(top = 8.dp))
            if (comment.isNotEmpty()) Text(comment, fontSize = 13.sp, color = Color.Gray, modifier = Modifier.padding(top = 6.dp))
            if (imgUrl != null) {
                Spacer(Modifier.height(12.dp))
                // TODO: 图片加载需要配合compose-image-loader（Web/桌面需要不同loader），这里只写图片占位
                Box(Modifier.size(64.dp).background(Color.Gray).clip(RoundedCornerShape(16.dp))) {
                    Text("图", Modifier.align(Alignment.Center), color = Color.White)
                }
            }
            if (buttonLabel != null) {
                Button(
                    onClick = { /*TODO*/ },
                    Modifier.padding(top = 8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E42))
                ) {
                    Text(buttonLabel, fontSize = 12.sp)
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
            Box(Modifier.size(40.dp).background(Color(0xFFFFEDD5), RoundedCornerShape(16.dp)))
            Column(Modifier.padding(start = 8.dp)) {
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
}