package org.xg.project.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.xg.project.domain.model.MealType

private val DesktopRightRailWidth = 300.dp
private val DesktopCardShape = RoundedCornerShape(10.dp)
private val DesktopPanelShape = RoundedCornerShape(10.dp)
private val DesktopToggleShape = RoundedCornerShape(8.dp)
enum class HomeDesktopDayTab(val label: String) {
    Today("今日"),
    Tomorrow("明日"),
}

@Composable
fun HomeDesktopLayout(
    content: HomeContentUi,
    onAddPlan: (MealType) -> Unit,
    showAppChrome: Boolean,
    onProfileClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()

    if (showAppChrome) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(HomeColors.CardWhite),
        ) {
            Row(modifier = Modifier.weight(1f)) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                ) {
                    HomeDesktopTopBar(onProfileClick = onProfileClick)
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(scrollState)
                            .background(Color(0xFFF7F7F7))
                            .padding(horizontal = 24.dp, vertical = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(24.dp),
                    ) {
                        HomeDesktopMainSection(
                            content = content,
                            onAddPlan = onAddPlan,
                            modifier = Modifier.weight(1f),
                        )
                        HomeDesktopRightRail(
                            heroImageUrl = content.heroImageUrl,
                            modifier = Modifier.width(DesktopRightRailWidth),
                        )
                    }
                }
            }
            HorizontalDivider(color = Color(0xFFE5E7EB))
            HomeDesktopFooter()
        }
    } else {
        Row(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .background(HomeColors.PageBackground)
                .padding(24.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            HomeDesktopMainSection(
                content = content,
                onAddPlan = onAddPlan,
                modifier = Modifier.weight(1f),
            )
            HomeDesktopRightRail(
                heroImageUrl = content.heroImageUrl,
                modifier = Modifier.width(DesktopRightRailWidth),
            )
        }
    }
}

@Composable
fun HomeDesktopMainSection(
    content: HomeContentUi,
    onAddPlan: (MealType) -> Unit,
    modifier: Modifier = Modifier,
) {
    var dayTab by remember { mutableStateOf(HomeDesktopDayTab.Today) }

    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(
                    text = "今日餐单",
                    fontSize = HomeDesktopFonts.pageTitle,
                    fontWeight = FontWeight.Bold,
                    color = HomeColors.TextPrimary,
                )
                Text(
                    text = content.menuDateLabel.ifBlank { content.dateLine },
                    fontSize = HomeDesktopFonts.pageSubtitle,
                    color = HomeColors.TextSecondary,
                    modifier = Modifier.padding(top = 6.dp),
                )
            }
            HomeDesktopDayToggle(
                selected = dayTab,
                onSelected = { dayTab = it },
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
            val breakfast = content.mealSlots.firstOrNull { it.mealType == MealType.BREAKFAST }
            val lunch = content.mealSlots.firstOrNull { it.mealType == MealType.LUNCH }
            val dinner = content.mealSlots.firstOrNull { it.mealType == MealType.DINNER }
            val snack = content.mealSlots.firstOrNull { it.mealType == MealType.SNACK }

            if (breakfast != null && lunch != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    HomeDesktopMealCard(
                        slot = breakfast,
                        onAddPlan = { onAddPlan(breakfast.mealType) },
                        modifier = Modifier.weight(1f).height(200.dp),
                    )
                    HomeDesktopMealCard(
                        slot = lunch,
                        onAddPlan = { onAddPlan(lunch.mealType) },
                        modifier = Modifier.weight(1f).height(200.dp),
                    )
                }
            }
            dinner?.let { slot ->
                HomeDesktopMealCard(
                    slot = slot,
                    onAddPlan = { onAddPlan(slot.mealType) },
                    modifier = Modifier.fillMaxWidth().heightIn(min = 180.dp),
                )
            }
            snack?.let { slot ->
                if (slot.items.isEmpty()) {
                    HomeDesktopEmptySnackCard(
                        slot = slot,
                        onAddPlan = { onAddPlan(slot.mealType) },
                    )
                } else {
                    HomeDesktopMealCard(
                        slot = slot,
                        onAddPlan = { onAddPlan(slot.mealType) },
                        modifier = Modifier.fillMaxWidth().heightIn(min = 160.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeDesktopTopBar(onProfileClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .background(HomeColors.CardWhite)
            .padding(horizontal = 24.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            listOf("周计划", "菜谱库", "食材管家", "社区").forEach { label ->
                Text(
                    text = label,
                    fontSize = HomeDesktopFonts.topNav,
                    fontWeight = FontWeight.Medium,
                    color = HomeColors.TextSecondary,
                    modifier = Modifier
                        .clickable { }
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                )
            }
        }
        Spacer(modifier = Modifier.width(24.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            HomeDesktopSearchField(modifier = Modifier.width(280.dp))
            Icon(
                Icons.Default.Notifications,
                contentDescription = null,
                tint = HomeColors.TextSecondary,
                modifier = Modifier.size(22.dp),
            )
            Icon(
                Icons.Default.Settings,
                contentDescription = null,
                tint = HomeColors.TextSecondary,
                modifier = Modifier.size(22.dp),
            )
            Button(
                onClick = { },
                modifier = Modifier.heightIn(min = 38.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = HomeColors.BrandBrown),
            ) {
                Text("开始烹饪", fontSize = HomeDesktopFonts.actionButton)
            }
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(HomeColors.BrandOrange.copy(alpha = 0.25f))
                    .clickable(onClick = onProfileClick),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    tint = HomeColors.BrandBrown,
                    modifier = Modifier.size(20.dp),
                )
            }
        }
    }
}

@Composable
private fun HomeDesktopSearchField(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .height(40.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFFF3F4F6))
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            Icons.Default.Search,
            contentDescription = null,
            tint = HomeColors.TextSecondary,
            modifier = Modifier.size(20.dp),
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = "搜索食谱...",
            fontSize = HomeDesktopFonts.searchPlaceholder,
            color = HomeColors.TextSecondary,
        )
    }
}

@Composable
private fun HomeDesktopDayToggle(
    selected: HomeDesktopDayTab,
    onSelected: (HomeDesktopDayTab) -> Unit,
) {
    Row(
        modifier = Modifier
            .background(Color(0xFFEFEFEF), DesktopToggleShape)
            .padding(3.dp),
    ) {
        HomeDesktopDayTab.entries.forEach { tab ->
            val active = tab == selected
            Text(
                text = tab.label,
                modifier = Modifier
                    .clip(DesktopToggleShape)
                    .background(if (active) HomeColors.CardWhite else Color.Transparent)
                    .clickable { onSelected(tab) }
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                fontSize = HomeDesktopFonts.dayTab,
                fontWeight = if (active) FontWeight.SemiBold else FontWeight.Normal,
                color = if (active) HomeColors.TextPrimary else HomeColors.TextSecondary,
            )
        }
    }
}

@Composable
fun HomeDesktopMealCard(
    slot: MealSlotUi,
    onAddPlan: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val item = slot.items.firstOrNull()
    val isDayMeal = slot.mealType == MealType.BREAKFAST || slot.mealType == MealType.LUNCH
    val periodIconColor = if (isDayMeal) Color(0xFFF5A623) else Color(0xFF7EB6D4)
    val periodIconBg = periodIconColor.copy(alpha = 0.15f)

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = DesktopCardShape,
        colors = CardDefaults.cardColors(containerColor = HomeColors.CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        if (item == null) {
            HomeDesktopEmptyMealCardContent(
                slot = slot,
                periodIconColor = periodIconColor,
                periodIconBg = periodIconBg,
                onAddPlan = onAddPlan,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
            ) {
                HomeMealSlotCardHeader(
                    slot = slot,
                    periodIconColor = periodIconColor,
                    periodIconBg = periodIconBg,
                    largeIndex = true,
                )
                Spacer(modifier = Modifier.weight(1f))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFF9FAFB))
                        .padding(12.dp),
                ) {
                    Text(
                        text = item.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = HomeDesktopFonts.mealMeta,
                        color = HomeColors.TextPrimary,
                    )
                    if (item.desc.isNotBlank()) {
                        Text(
                            text = item.desc,
                            fontSize = HomeDesktopFonts.mealMetaSmall,
                            color = HomeColors.TextSecondary,
                            modifier = Modifier.padding(top = 4.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeDesktopEmptyMealCardContent(
    slot: MealSlotUi,
    periodIconColor: Color,
    periodIconBg: Color,
    onAddPlan: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        HomeMealSlotCardHeader(
            slot = slot,
            periodIconColor = periodIconColor,
            periodIconBg = periodIconBg,
            largeIndex = true,
        )
        Spacer(modifier = Modifier.weight(1f))
        HomeDesktopAddPlanButton(
            onClick = onAddPlan,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun HomeDesktopEmptySnackCard(
    slot: MealSlotUi,
    onAddPlan: () -> Unit,
) {
    val periodIconColor = Color(0xFF7EB6D4)
    val periodIconBg = periodIconColor.copy(alpha = 0.15f)
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = DesktopCardShape,
        colors = CardDefaults.cardColors(containerColor = HomeColors.CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            HomeMealSlotCardHeader(
                slot = slot,
                periodIconColor = periodIconColor,
                periodIconBg = periodIconBg,
                largeIndex = true,
            )
            Spacer(modifier = Modifier.height(12.dp))
            HomeDesktopAddPlanButton(
                onClick = onAddPlan,
                label = "添加深夜食堂",
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun HomeDesktopAddPlanButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    label: String = "去添加计划",
) {
    Box(
        modifier = modifier
            .height(52.dp)
            .border(1.dp, HomeColors.DashedBorder, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = null,
                tint = HomeColors.BrandOrange,
                modifier = Modifier.size(18.dp),
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                color = HomeColors.BrandOrange,
                fontWeight = FontWeight.Medium,
                fontSize = HomeDesktopFonts.mealAction,
            )
        }
    }
}

@Composable
private fun HomeDesktopRightRail(
    @Suppress("UNUSED_PARAMETER") heroImageUrl: String?,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        HomePromoCard()
        HomeNutritionGoalsPanel()
        HomeChefTipCard()
    }
}

@Composable
private fun HomeDesktopFooter(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .padding(horizontal = 24.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "© 2024 锅铲黄小厨 | 让每一顿饭都充满温度",
            fontSize = HomeDesktopFonts.footer,
            color = HomeColors.TextSecondary,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
            listOf("关于我们", "隐私协议", "使用帮助", "联系客服").forEach { label ->
                Text(
                    text = label,
                    fontSize = HomeDesktopFonts.footer,
                    color = HomeColors.TextSecondary,
                    modifier = Modifier.clickable { },
                )
            }
        }
    }
}
