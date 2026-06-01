package org.xg.project.screen.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Restaurant
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
import org.xg.project.domain.model.MealType

private val CompactHorizontalPadding = 16.dp

@Composable
fun HomeCompactContent(
    content: HomeContentUi,
    onAddPlan: (MealType) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .background(HomeColors.PageBackground)
            .padding(bottom = 16.dp),
    ) {
        HomeCompactTopBar(
            modifier = Modifier.padding(
                horizontal = CompactHorizontalPadding,
                vertical = 12.dp,
            ),
        )
        HomeGreetingSection(
            content = content,
            modifier = Modifier.padding(horizontal = CompactHorizontalPadding),
        )
        Spacer(modifier = Modifier.height(16.dp))
        HomeFeaturedImageCard(
            title = content.heroTitle,
            subtitle = "今日推荐",
            imageUrl = content.heroImageUrl,
            enableHoverZoom = false,
            modifier = Modifier.padding(horizontal = CompactHorizontalPadding),
        )
        Spacer(modifier = Modifier.height(20.dp))
        HomeCompactTimeline(
            slots = content.mealSlots,
            onAddPlan = onAddPlan,
            modifier = Modifier.padding(horizontal = CompactHorizontalPadding),
        )
        Spacer(modifier = Modifier.height(24.dp))
        HomeSeasonalSection(
            enableHoverZoom = false,
            modifier = Modifier.padding(horizontal = CompactHorizontalPadding),
        )
    }
}

@Composable
fun HomeMediumContent(
    content: HomeContentUi,
    onAddPlan: (MealType) -> Unit,
    modifier: Modifier = Modifier,
) {
    HomeDesktopLayout(
        content = content,
        onAddPlan = onAddPlan,
        showAppChrome = false,
        modifier = modifier,
    )
}

@Composable
fun HomeWideContent(
    content: HomeContentUi,
    onAddPlan: (MealType) -> Unit,
    modifier: Modifier = Modifier,
) {
    HomeDesktopLayout(
        content = content,
        onAddPlan = onAddPlan,
        showAppChrome = true,
        showSidebar = false,
        modifier = modifier,
    )
}

@Composable
private fun HomeCompactTopBar(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Icon(
            imageVector = Icons.Default.Menu,
            contentDescription = null,
            tint = HomeColors.BrandBrown,
            modifier = Modifier.size(24.dp),
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Restaurant,
                contentDescription = null,
                tint = HomeColors.BrandBrown,
                modifier = Modifier.size(20.dp),
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "锅铲黄小厨",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = HomeColors.BrandBrown,
            )
        }
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(HomeColors.BrandOrange.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = HomeColors.BrandBrown,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

@Composable
private fun HomeGreetingSection(
    content: HomeContentUi,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = content.dateLine,
            fontSize = 13.sp,
            color = HomeColors.BrandBrown,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "哈喽，小厨神！",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = HomeColors.TextPrimary,
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = content.greetingSubtitle,
            fontSize = 14.sp,
            color = HomeColors.TextSecondary,
            lineHeight = 20.sp,
        )
    }
}

@Composable
private fun HomeCompactTimeline(
    slots: List<MealSlotUi>,
    onAddPlan: (MealType) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        slots.forEachIndexed { index, slot ->
            Row(modifier = Modifier.fillMaxWidth()) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.width(36.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(slot.accent.copy(alpha = 0.18f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Restaurant,
                            contentDescription = null,
                            tint = slot.accent,
                            modifier = Modifier.size(16.dp),
                        )
                    }
                    if (index < slots.lastIndex) {
                        Box(
                            modifier = Modifier
                                .width(2.dp)
                                .height(120.dp)
                                .background(HomeColors.TimelineLine),
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "${slot.title} ${slot.timeRange}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = HomeColors.BrandBrown,
                        )
                        slot.badge?.let { badge ->
                            Text(
                                text = badge,
                                fontSize = 11.sp,
                                color = HomeColors.TextSecondary,
                                modifier = Modifier
                                    .background(Color(0xFFF3F4F6), RoundedCornerShape(12.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    HomeTimelineMealCard(
                        slot = slot,
                        enableHoverZoom = false,
                        onAddPlan = { onAddPlan(slot.mealType) },
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}

@Composable
private fun HomeSeasonalSection(
    enableHoverZoom: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "当季灵感",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = HomeColors.TextPrimary,
            )
            Text(
                text = "查看全部",
                fontSize = 13.sp,
                color = HomeColors.BrandOrange,
                fontWeight = FontWeight.Medium,
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        HomeSeasonalInspirationCard(enableHoverZoom = enableHoverZoom)
    }
}
