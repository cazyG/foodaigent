package org.xg.project.screen.home

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.datetime.LocalDate
import kotlinx.datetime.number
import org.xg.project.domain.model.DailyMenuRecord
import org.xg.project.domain.model.MealType
import org.xg.project.domain.model.MenuItemData
import org.xg.project.presentation.index.IndexState
import org.xg.project.screen.RadarChart
import org.xg.project.screen.glassPanel

object HomeColors {
    val BrandOrange = Color(0xFFF0883A)
    val BrandBrown = Color(0xFF8B5E3C)
    val CardWhite = Color.White
    val PageBackground = Color(0xFFF8FAFC)
    val TextPrimary = Color(0xFF1F2937)
    val TextSecondary = Color(0xFF6B7280)
}

object HomeBreakpoints {
    val CompactMax: Dp = 600.dp
}

data class HomeContentUi(
    val dateLabel: String,
    val todayRecord: DailyMenuRecord,
    val canReviewBreakfast: Boolean,
    val canReviewLunch: Boolean,
    val canReviewDinner: Boolean,
    val canReviewSnack: Boolean,
)

fun LocalDate.formatChineseDateOnly(): String =
    "${year}年${month.number}月${day}日"

fun IndexState.toHomeContentUi(today: LocalDate, emptyRecord: DailyMenuRecord): HomeContentUi =
    HomeContentUi(
        dateLabel = today.formatChineseDateOnly(),
        todayRecord = todayRecord ?: emptyRecord,
        canReviewBreakfast = canReviewBreakfast,
        canReviewLunch = canReviewLunch,
        canReviewDinner = canReviewDinner,
        canReviewSnack = canReviewSnack,
    )

@Composable
fun HomeCompactContent(
    content: HomeContentUi,
    onAddPlan: (MealType) -> Unit,
    modifier: Modifier = Modifier,
) {
    HomeMealList(content = content, onAddPlan = onAddPlan, modifier = modifier)
}

@Composable
fun HomeMediumContent(
    content: HomeContentUi,
    onAddPlan: (MealType) -> Unit,
    modifier: Modifier = Modifier,
) {
    HomeMealList(content = content, onAddPlan = onAddPlan, modifier = modifier)
}

@Composable
fun HomeWideContent(
    content: HomeContentUi,
    onAddPlan: (MealType) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier.fillMaxSize().padding(24.dp)) {
        HomeMealList(
            content = content,
            onAddPlan = onAddPlan,
            modifier = Modifier.weight(1f),
        )
        Card(
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp)
                .glassPanel(RoundedCornerShape(20.dp)),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text("口味雷达", fontWeight = FontWeight.Bold, color = HomeColors.TextPrimary)
                Spacer(modifier = Modifier.height(12.dp))
                RadarChart(
                    data = mapOf(
                        "鲜" to 0.8f,
                        "香" to 0.7f,
                        "辣" to 0.4f,
                        "甜" to 0.6f,
                        "酸" to 0.5f,
                    ),
                    modifier = Modifier.fillMaxWidth().height(220.dp),
                )
            }
        }
    }
}

@Composable
private fun HomeMealList(
    content: HomeContentUi,
    onAddPlan: (MealType) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        Text(
            text = content.dateLabel,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = HomeColors.TextPrimary,
        )
        Text(
            text = "今日菜单",
            fontSize = 13.sp,
            color = HomeColors.TextSecondary,
            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp),
        )
        MealType.entries.forEach { mealType ->
            HomeMealSection(
                mealType = mealType,
                items = content.itemsFor(mealType),
                canReview = content.canReviewFor(mealType),
                onAddPlan = { onAddPlan(mealType) },
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun HomeMealSection(
    mealType: MealType,
    items: List<MenuItemData>,
    canReview: Boolean,
    onAddPlan: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth().glassPanel(RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        shape = RoundedCornerShape(16.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(mealType.title, fontWeight = FontWeight.Bold, color = HomeColors.TextPrimary)
                if (canReview) {
                    Text("可评价", fontSize = 12.sp, color = HomeColors.BrandOrange)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            if (items.isEmpty()) {
                Text("暂无菜品", fontSize = 13.sp, color = HomeColors.TextSecondary)
            } else {
                items.forEach { item ->
                    Text(
                        text = "${item.name} · ${item.desc}",
                        fontSize = 13.sp,
                        color = HomeColors.TextPrimary,
                        modifier = Modifier.padding(vertical = 2.dp),
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Button(
                onClick = onAddPlan,
                colors = ButtonDefaults.buttonColors(containerColor = HomeColors.BrandOrange),
                shape = RoundedCornerShape(10.dp),
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                Text("添加${mealType.title}", color = Color.White, modifier = Modifier.padding(start = 4.dp))
            }
        }
    }
}

private fun HomeContentUi.itemsFor(mealType: MealType): List<MenuItemData> = when (mealType) {
    MealType.BREAKFAST -> todayRecord.breakfast
    MealType.LUNCH -> todayRecord.lunch
    MealType.DINNER -> todayRecord.dinner
    MealType.SNACK -> todayRecord.snack
}

private fun HomeContentUi.canReviewFor(mealType: MealType): Boolean = when (mealType) {
    MealType.BREAKFAST -> canReviewBreakfast
    MealType.LUNCH -> canReviewLunch
    MealType.DINNER -> canReviewDinner
    MealType.SNACK -> canReviewSnack
}
