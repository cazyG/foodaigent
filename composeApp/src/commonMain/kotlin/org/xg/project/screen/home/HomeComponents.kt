package org.xg.project.screen.home

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import org.xg.project.domain.model.MenuItemData

private val CardShape = RoundedCornerShape(20.dp)
private const val ImageHoverScale = 1.05f

@Composable
fun HomeFeaturedImageCard(
    title: String,
    subtitle: String,
    imageUrl: String?,
    enableHoverZoom: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    val scale by animateFloatAsState(
        targetValue = if (enableHoverZoom && isHovered) ImageHoverScale else 1f,
        animationSpec = tween(400, easing = FastOutSlowInEasing),
        label = "homeFeaturedScale",
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp)
            .then(if (enableHoverZoom) Modifier.hoverable(interactionSource) else Modifier)
            .clickable(onClick = onClick),
        shape = CardShape,
        colors = CardDefaults.cardColors(containerColor = HomeColors.CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(CardShape),
        ) {
            HomeMealImage(
                imageUrl = imageUrl,
                scale = scale,
                modifier = Modifier.fillMaxSize(),
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colorStops = arrayOf(
                                0f to Color.Transparent,
                                0.45f to Color.Transparent,
                                1f to Color.Black.copy(alpha = 0.55f),
                            ),
                        ),
                    ),
            )
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp),
            ) {
                Text(
                    text = subtitle,
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 12.sp,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
fun HomeHoverableMealThumbnail(
    imageUrl: String?,
    enableHoverZoom: Boolean,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    val scale by animateFloatAsState(
        targetValue = if (enableHoverZoom && isHovered) ImageHoverScale else 1f,
        animationSpec = tween(400, easing = FastOutSlowInEasing),
        label = "homeThumbScale",
    )
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .then(if (enableHoverZoom) Modifier.hoverable(interactionSource) else Modifier),
    ) {
        HomeMealImage(
            imageUrl = imageUrl,
            scale = scale,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Composable
fun HomeMealImage(
    imageUrl: String?,
    scale: Float,
    modifier: Modifier = Modifier,
) {
    if (!imageUrl.isNullOrBlank()) {
        AsyncImage(
            model = imageUrl,
            contentDescription = null,
            modifier = modifier.graphicsLayer {
                scaleX = scale
                scaleY = scale
                transformOrigin = TransformOrigin(0.5f, 0.5f)
            },
            contentScale = ContentScale.Crop,
        )
    } else {
        Box(
            modifier = modifier
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    transformOrigin = TransformOrigin(0.5f, 0.5f)
                }
                .background(
                    Brush.linearGradient(
                        listOf(
                            Color(0xFFFFE0C2),
                            Color(0xFFF0883A),
                        ),
                    ),
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Default.Restaurant,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.85f),
                modifier = Modifier.size(48.dp),
            )
        }
    }
}

@Composable
fun HomeAddPlanCard(
    mealLabel: String,
    timeRange: String,
    accent: Color,
    modifier: Modifier = Modifier,
    onAddPlan: () -> Unit,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onAddPlan),
        shape = CardShape,
        colors = CardDefaults.cardColors(containerColor = HomeColors.CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(accent.copy(alpha = 0.15f), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Default.Restaurant,
                        contentDescription = null,
                        tint = accent,
                        modifier = Modifier.size(22.dp),
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = mealLabel,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = HomeColors.TextPrimary,
                    )
                    Text(
                        text = timeRange,
                        fontSize = 12.sp,
                        color = HomeColors.TextSecondary,
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .border(1.dp, HomeColors.DashedBorder, RoundedCornerShape(12.dp))
                    .clickable(onClick = onAddPlan),
                contentAlignment = Alignment.Center,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = HomeColors.BrandOrange,
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "+ 去添加计划",
                        color = HomeColors.BrandOrange,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }
        }
    }
}

@Composable
fun HomeTimelineMealCard(
    slot: MealSlotUi,
    enableHoverZoom: Boolean,
    modifier: Modifier = Modifier,
    onAddPlan: () -> Unit,
    onReviewClick: () -> Unit = {},
) {
    val item = slot.items.firstOrNull()
    if (item == null) {
        HomeAddPlanCard(
            mealLabel = slot.title,
            timeRange = slot.timeRange,
            accent = slot.accent,
            modifier = modifier,
            onAddPlan = onAddPlan,
        )
        return
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = CardShape,
        colors = CardDefaults.cardColors(containerColor = HomeColors.CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            HomeHoverableMealThumbnail(
                imageUrl = null,
                enableHoverZoom = enableHoverZoom,
                modifier = Modifier.size(88.dp),
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = HomeColors.TextPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                if (item.desc.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = item.desc,
                        fontSize = 12.sp,
                        color = HomeColors.TextSecondary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (slot.canReview) {
                        Text(
                            text = "去评价 >",
                            fontSize = 13.sp,
                            color = HomeColors.BrandOrange,
                            modifier = Modifier.clickable(onClick = onReviewClick),
                        )
                    } else {
                        Text(
                            text = "详情 >",
                            fontSize = 13.sp,
                            color = HomeColors.BrandBrown,
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(
                            imageVector = Icons.Default.FavoriteBorder,
                            contentDescription = null,
                            tint = HomeColors.TextSecondary,
                            modifier = Modifier.size(18.dp),
                        )
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = null,
                            tint = HomeColors.TextSecondary,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HomeWideMealSlotCard(
    slot: MealSlotUi,
    modifier: Modifier = Modifier,
    onAddPlan: () -> Unit,
) {
    val item = slot.items.firstOrNull()
    Card(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1.05f)
            .clickable(enabled = item == null, onClick = onAddPlan),
        shape = CardShape,
        colors = CardDefaults.cardColors(containerColor = HomeColors.CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        if (item == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = slot.mealType.slotIndex(),
                        fontSize = 13.sp,
                        color = HomeColors.TextSecondary,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = slot.title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = HomeColors.TextPrimary,
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(vertical = 12.dp)
                        .border(1.dp, HomeColors.DashedBorder, RoundedCornerShape(14.dp))
                        .clickable(onClick = onAddPlan),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "+ 去添加计划",
                        color = HomeColors.BrandOrange,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }
        } else {
            Column(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(RectangleShape),
                ) {
                    HomeMealImage(imageUrl = null, scale = 1f, modifier = Modifier.fillMaxSize())
                    Text(
                        text = "${slot.title} ${slot.timeRange.take(5)}",
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(12.dp)
                            .background(Color.Black.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        color = Color.White,
                        fontSize = 11.sp,
                    )
                }
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = item.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = HomeColors.TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    if (item.desc.isNotBlank()) {
                        Text(
                            text = item.desc,
                            fontSize = 12.sp,
                            color = HomeColors.TextSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HomeSeasonalInspirationCard(
    enableHoverZoom: Boolean,
    modifier: Modifier = Modifier,
) {
    HomeFeaturedImageCard(
        title = "有机生活，从今天开始",
        subtitle = "当季灵感",
        imageUrl = null,
        enableHoverZoom = enableHoverZoom,
        modifier = modifier.height(220.dp),
    )
}

@Composable
fun HomeNutritionGoalsPanel(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = CardShape,
        colors = CardDefaults.cardColors(containerColor = HomeColors.CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "今日营养目标",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = HomeColors.TextPrimary,
            )
            Text(
                text = "已完成 85%",
                fontSize = 13.sp,
                color = HomeColors.BrandOrange,
                modifier = Modifier.padding(top = 4.dp, bottom = 14.dp),
            )
            NutritionBar("热量", 0.85f, HomeColors.BrandBrown)
            Spacer(modifier = Modifier.height(10.dp))
            NutritionBar("蛋白质", 0.72f, Color(0xFF60A5FA))
            Spacer(modifier = Modifier.height(10.dp))
            NutritionBar("膳食纤维", 0.58f, Color(0xFF34D399))
        }
    }
}

@Composable
private fun NutritionBar(label: String, progress: Float, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = HomeColors.TextSecondary,
            modifier = Modifier.width(52.dp),
        )
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .weight(1f)
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = color,
            trackColor = Color(0xFFE5E7EB),
        )
    }
}

@Composable
fun HomeChefTipCard(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = CardShape,
        colors = CardDefaults.cardColors(containerColor = HomeColors.TipPanel),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "小厨贴士",
                fontWeight = FontWeight.Bold,
                fontSize = HomeDesktopFonts.tipTitle,
                color = HomeColors.BrandBrown,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "清蒸鱼建议最后 8 分钟再淋热油，能更好锁住鲜味。",
                fontSize = HomeDesktopFonts.tipBody,
                color = HomeColors.TextSecondary,
                lineHeight = 22.sp,
            )
        }
    }
}

@Composable
fun HomePromoCard(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = CardShape,
        colors = CardDefaults.cardColors(containerColor = HomeColors.PeachPanel),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "时令限定",
                fontSize = 12.sp,
                color = HomeColors.BrandOrange,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "冬日养生：栗子烧排骨",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = HomeColors.TextPrimary,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "查看灵感食谱 >",
                fontSize = 13.sp,
                color = HomeColors.BrandBrown,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

@Composable
fun HomeDayPlanTabs(
    selected: HomeDayPlanTab,
    onSelected: (HomeDayPlanTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .background(Color(0xFFF3F4F6), RoundedCornerShape(24.dp))
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        HomeDayPlanTab.entries.forEach { tab ->
            val active = tab == selected
            Text(
                text = tab.label,
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (active) HomeColors.CardWhite else Color.Transparent)
                    .clickable { onSelected(tab) }
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                fontSize = 13.sp,
                fontWeight = if (active) FontWeight.SemiBold else FontWeight.Normal,
                color = if (active) HomeColors.BrandBrown else HomeColors.TextSecondary,
            )
        }
    }
}

enum class HomeDayPlanTab(val label: String) {
    Today("本日"),
    Tomorrow("明日"),
    Week("本周"),
}
