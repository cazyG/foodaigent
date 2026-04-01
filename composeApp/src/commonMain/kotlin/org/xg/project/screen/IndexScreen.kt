package org.xg.project.screen

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.compose.ui.Alignment

@Composable
fun IndexScreen() {
    Column(
        modifier = Modifier.fillMaxSize().background(Color(0xFFFCFAF2)).verticalScroll(rememberScrollState())
    ) {
        // 顶部导航栏
        Row(
            Modifier.fillMaxWidth().background(Color.White).padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier
                        .background(Color(0xFFFBBF24), RoundedCornerShape(12.dp))
                        .padding(8.dp)
                ) {
                    Text("🍴")
                }
                Spacer(Modifier.width(8.dp))
                Text("心动厨房", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
            Text("2026年03月30日 星期一", color = Color(0xFFF59E42))
        }

        // 今日菜单
        SectionTitle("今日菜单", "修改计划 >")
        Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            MenuCard("早餐", "全麦欧包 & 煎蛋", "配料：黑咖啡、蓝莓、无糖酸奶", chef = "丈夫掌勺")
            MenuCard("午餐", "清蒸鲈鱼 & 蚝油生菜", "配料：糙米饭、排骨海带汤", chef = "妻子掌勺", labelColor = Color(0xFFFF9720))
            MenuCard("晚餐", "待定", "", chef = "夫妻", isPending = true)
        }

        // 点菜日历
        CalendarSection()

        // 最近动态
        SectionTitle("爱的评价")
        ReviewCard("老婆大人", "昨天 19:45", "今天的红烧排骨简直绝了！汤汁拌饭我可以吃三碗，五星好评~ ⭐⭐⭐⭐⭐", "红烧排骨计划")
        ReviewCard("老公大人", "前天 12:30", "番茄炒蛋永远是我的神，就是稍微有点点咸了，下次少放点盐哦。 ❤️", "午餐家常菜")
        Spacer(Modifier.height(96.dp))
    }

    // 底部TabBar
    TabBar(active = "首页")
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
        Modifier.weight(1f),
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
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFF59E42))
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
        Modifier.padding(16.dp), shape = RoundedCornerShape(32.dp), backgroundColor = Color.White
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
                    Icon(Icons.Default.ArrowBack, contentDescription = null)
                    Text("2026年3月", Modifier.padding(horizontal = 8.dp), fontWeight = FontWeight.Bold)
                    Icon(Icons.Default.ArrowForward, contentDescription = null)
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