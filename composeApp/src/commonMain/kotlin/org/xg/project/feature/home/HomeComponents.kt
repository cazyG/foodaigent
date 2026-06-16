package org.xg.project.feature.home

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
    slot: MealSlotUi,
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
            HomeMealSlotCardHeader(
                slot = slot,
                periodIconColor = slot.accent,
                periodIconBg = slot.accent.copy(alpha = 0.15f),
            )
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
                        text = "去添加计划",
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
            slot = slot,
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
fun HomeMealSlotCardHeader(
    slot: MealSlotUi,
    periodIconColor: Color,
    periodIconBg: Color,
    largeIndex: Boolean = false,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top,
    ) {
        Column {
            Text(
                text = slot.mealType.slotIndex(),
                fontSize = if (largeIndex) 28.sp else 13.sp,
                fontWeight = if (largeIndex) FontWeight.Bold else FontWeight.Normal,
                color = if (largeIndex) {
                    periodIconColor.copy(alpha = 0.85f)
                } else {
                    HomeColors.TextSecondary
                },
            )
            Text(
                text = slot.title,
                fontSize = if (largeIndex) HomeDesktopFonts.mealTitle else 18.sp,
                fontWeight = FontWeight.Bold,
                color = HomeColors.TextPrimary,
            )
        }
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(if (largeIndex) RoundedCornerShape(8.dp) else CircleShape)
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "今日营养目标",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = HomeColors.TextPrimary,
                )
                Text(
                    text = "总完成度 85%",
                    fontSize = 13.sp,
                    color = HomeColors.BrandOrange,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
            NutritionBar("热量", 0.77f, HomeColors.BrandBrown, "1540/2000 kcal")
            Spacer(modifier = Modifier.height(10.dp))
            NutritionBar("蛋白质", 0.73f, Color(0xFF60A5FA), "88/120 g")
            Spacer(modifier = Modifier.height(10.dp))
            NutritionBar("膳食纤维", 0.93f, Color(0xFF34D399), "28/30 g")
        }
    }
}

@Composable
private fun NutritionBar(label: String, progress: Float, color: Color, detail: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = label,
                fontSize = 12.sp,
                color = HomeColors.TextSecondary,
            )
            Text(
                text = detail,
                fontSize = 11.sp,
                color = HomeColors.TextSecondary,
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
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
                text = "小厨提示",
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
                text = "季节限定",
                fontSize = 12.sp,
                color = HomeColors.BrandOrange,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "暖冬养生：板栗排骨",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = HomeColors.TextPrimary,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "查看今日推荐 >",
                fontSize = 13.sp,
                color = HomeColors.BrandBrown,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

