package org.xg.project.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PlanningScreen() {
    var selectedRole by remember { mutableStateOf("丈夫") }
    var selectedMeal by remember { mutableStateOf("午餐") }
    val state = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GlassStyle.BgGradient)
            .verticalScroll(state),
    ) {
        // 步骤1：选择日期
        SectionTitle("第一步：选择日期")
        Card(
            Modifier
                .padding(horizontal = 16.dp)
                .glassPanelStrong(RoundedCornerShape(40.dp)),
            shape = RoundedCornerShape(40.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
        ) {
            GlassHighlight {
                Row(Modifier.padding(20.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("2026年03月30日", fontWeight = FontWeight.Bold, color = GlassStyle.TextPrimary)
                    Icon(Icons.Default.CalendarToday, contentDescription = null, tint = GlassStyle.AccentStrong)
                }
            }
        }

        // 步骤2：选择餐次
        SectionTitle("第二步：选择餐次")
        Row(Modifier.padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            ToggleButtonGroup(
                options = listOf("午餐", "晚餐"),
                selected = selectedMeal,
                onSelect = { selectedMeal = it }
            )
        }

        // 步骤3：挑选菜品
        SectionTitle("第三步：挑选菜品")
        TextField(
            value = "",
            onValueChange = {},
            placeholder = { Text("从食谱库快速添加...") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .glassPanel(RoundedCornerShape(16.dp)),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )
        Spacer(Modifier.height(16.dp))
        // 推荐菜品（省略实际搜索）
        DishOptionCard("清炖排骨汤", "适合感冒/滋补，约1.5小时", selected = false)
        DishOptionCard("地三鲜", "家常热门，下饭菜", selected = false)
        DishOptionCard("红烧肉", "老公爱吃，已选择", selected = true)

        // 生成计划
        Spacer(Modifier.height(32.dp))
        Button(
            onClick = {},
            Modifier.fillMaxWidth().padding(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.24f))
        ) {
            Text("生成计划（由${selectedRole}发起)", fontWeight = FontWeight.Bold, color = GlassStyle.TextPrimary)
        }
        Spacer(Modifier.height(96.dp))
    }
//    TabBar(active = "点菜")
}

@Composable
fun ToggleButtonGroup(options: List<String>, selected: String, onSelect: (String) -> Unit) {
    Row(
        Modifier
            .glassPanel(RoundedCornerShape(16.dp))
            .padding(4.dp)
    ) {
        options.forEach {
            val isSelected = it == selected
            Box(
                Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isSelected) Color.White.copy(alpha = 0.30f) else Color.Transparent)
                    .clickable { onSelect(it) }
                    .padding(vertical = 6.dp, horizontal = 20.dp)
            ) {
                Text(it, color = if (isSelected) GlassStyle.TextPrimary else GlassStyle.TextSecondary, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun DishOptionCard(name: String, tip: String, selected: Boolean) {
    Card(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .border(
                if (selected) BorderStroke(1.5.dp, GlassStyle.Stroke) else BorderStroke(1.dp, GlassStyle.Stroke.copy(alpha = 0.8f)),
                shape = RoundedCornerShape(24.dp)
            )
            .glassPanel(RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        GlassHighlight {
            Row(Modifier.padding(12.dp)) {
            Box(
                Modifier.size(48.dp).background(Color.White.copy(alpha = 0.18f), RoundedCornerShape(16.dp))
            ) { /* 图片占位 */ }
            Column(Modifier.padding(start = 12.dp)) {
                Text(name, fontWeight = FontWeight.Bold, color = GlassStyle.TextPrimary)
                Text(tip, fontSize = 10.sp, color = GlassStyle.TextSecondary)
            }
            Spacer(Modifier.weight(1f))
            Icon(
                if (selected) Icons.Default.Check else Icons.Default.Add, contentDescription = null,
                tint = GlassStyle.TextPrimary,
                modifier = Modifier
                    .background(Color.White.copy(alpha = 0.22f), RoundedCornerShape(50)).padding(6.dp)
            )
        }
        }
    }
}