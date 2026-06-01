package org.xg.project.screen.home

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
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.ui.unit.sp
import org.xg.project.Routes.BottomTabRoute
import org.xg.project.domain.model.MealType
import org.xg.project.screen.navigation.AppDesktopSidebar

private val DesktopRightRailWidth = 260.dp
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
    showSidebar: Boolean = true,
    activeTab: BottomTabRoute = BottomTabRoute.Home,
    onTabClick: (BottomTabRoute) -> Unit = {},
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
                if (showSidebar) {
                    HomeDesktopSidebar(
                        activeTab = activeTab,
                        onTabClick = onTabClick,
                        modifier = Modifier.fillMaxHeight(),
                    )
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                ) {
                    HomeDesktopTopBar()
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
            content.mealSlots.chunked(2).forEach { rowSlots ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                ) {
                    rowSlots.forEach { slot ->
                        HomeDesktopMealCard(
                            slot = slot,
                            onAddPlan = { onAddPlan(slot.mealType) },
                            modifier = Modifier
                                .weight(1f)
                                .height(220.dp),
                        )
                    }
                    if (rowSlots.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeDesktopSidebar(
    activeTab: BottomTabRoute,
    onTabClick: (BottomTabRoute) -> Unit,
    modifier: Modifier = Modifier,
) {
    AppDesktopSidebar(
        activeTab = activeTab,
        onTabClick = onTabClick,
        modifier = modifier,
        footer = {
            Column {
                Button(
                    onClick = { },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = HomeColors.BrandBrown),
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("上传新菜谱", fontSize = HomeDesktopFonts.actionButton, color = Color.White)
                }
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { },
                ) {
                    Icon(
                        Icons.Default.ExitToApp,
                        contentDescription = null,
                        tint = HomeColors.TextSecondary,
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "退出登录",
                        fontSize = HomeDesktopFonts.mealMetaSmall,
                        color = HomeColors.TextSecondary,
                    )
                }
            }
        },
    )
}

@Composable
private fun HomeDesktopTopBar() {
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
                    .background(HomeColors.BrandOrange.copy(alpha = 0.25f)),
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = slot.title,
                    fontSize = HomeDesktopFonts.mealTitle,
                    fontWeight = FontWeight.Bold,
                    color = HomeColors.TextPrimary,
                )
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(periodIconBg),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Default.Restaurant,
                        contentDescription = null,
                        tint = periodIconColor,
                        modifier = Modifier.size(18.dp),
                    )
                }
            }
            Spacer(modifier = Modifier.weight(1f))
                if (item == null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .border(1.dp, HomeColors.DashedBorder, RoundedCornerShape(8.dp))
                            .clickable(onClick = onAddPlan),
                        contentAlignment = Alignment.Center,
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = null,
                                tint = HomeColors.BrandOrange,
                                modifier = Modifier.size(18.dp),
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "去添加计划",
                                color = HomeColors.BrandOrange,
                                fontWeight = FontWeight.Medium,
                                fontSize = HomeDesktopFonts.mealAction,
                            )
                        }
                    }
                } else {
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
private fun HomeDesktopRightRail(
    heroImageUrl: String?,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        HomeDesktopSeasonalPanel(imageUrl = heroImageUrl)
        HomeChefTipCard(modifier = Modifier.width(220.dp))
    }
}

@Composable
private fun HomeDesktopSeasonalPanel(imageUrl: String?) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = "✦", color = HomeColors.BrandOrange, fontSize = HomeDesktopFonts.sectionTitle)
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "当季灵感",
                fontSize = HomeDesktopFonts.sectionTitle,
                fontWeight = FontWeight.Bold,
                color = HomeColors.TextPrimary,
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Card(
            modifier = Modifier.width(220.dp),
            shape = DesktopPanelShape,
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        ) {
            Box(modifier = Modifier.height(280.dp)) {
                HomeMealImage(
                    imageUrl = imageUrl,
                    scale = 1f,
                    modifier = Modifier.fillMaxSize(),
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.35f)),
                )
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(20.dp),
                ) {
                    Text(
                        text = "有机生活，从今天开始",
                        color = Color.White,
                        fontSize = HomeDesktopFonts.inspirationTitle,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "探索 20+ 款健康时令食谱，让厨房充满活力与烟火气。",
                        color = Color.White.copy(alpha = 0.92f),
                        fontSize = HomeDesktopFonts.inspirationBody,
                        lineHeight = 22.sp,
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "探索灵感食谱",
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.White)
                            .clickable { }
                            .padding(horizontal = 18.dp, vertical = 10.dp),
                        color = HomeColors.BrandBrown,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = HomeDesktopFonts.inspirationButton,
                    )
                }
            }
        }
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
