package org.xg.project.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.IosShare
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import org.xg.project.data.model.ResponseResult
import org.xg.project.data.repository.FoodRepository
import org.xg.project.domain.model.RecipeMenu
import org.xg.project.screen.home.HomeColors
import kotlin.math.max

private val DetailPageBg = Color(0xFFF4F5FA)
private val DetailCardBg = Color.White
private val DetailCardBorder = Color(0xFFE7EAF0)
private val DetailTextPrimary = Color(0xFF1F2937)
private val DetailTextSecondary = Color(0xFF6B7280)
private val DetailAccent = Color(0xFFF0A33E)

@Composable
fun RecipeDetailScreen(
    recipeId: String,
    onBack: () -> Unit,
) {
    val recipeState by produceState<RecipeMenu?>(initialValue = null, key1 = recipeId) {
        val id = recipeId.toIntOrNull()
        if (id == null) {
            value = null
            return@produceState
        }
        val result = FoodRepository().getAllRecipe()
        value = when (result) {
            is ResponseResult.Success -> result.data.firstOrNull { it.id == id }
            is ResponseResult.Error -> null
            is ResponseResult.Loading -> null
        }
    }

    Scaffold(
        containerColor = DetailPageBg,
        topBar = {
            RecipeDetailTopBar(onBack = onBack)
        },
    ) { innerPadding ->
        val recipe = recipeState
        when {
            recipe == null -> RecipeDetailLoadingOrEmpty(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            )

            else -> RecipeDetailContent(
                recipe = recipe,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            )
        }
    }
}

@Composable
private fun RecipeDetailTopBar(onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "返回",
            tint = DetailTextPrimary,
            modifier = Modifier
                .size(20.dp)
                .clickable(onClick = onBack),
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = "食谱详情",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = DetailTextPrimary,
        )
        Spacer(modifier = Modifier.weight(1f))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { }) {
                Icon(Icons.Outlined.IosShare, contentDescription = null, tint = HomeColors.BrandBrown, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("分享", color = HomeColors.BrandBrown, fontSize = 13.sp)
            }
            Button(
                onClick = { },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = HomeColors.BrandBrown,
                    contentColor = Color.White,
                ),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp),
            ) {
                Icon(Icons.Outlined.FavoriteBorder, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("收藏食谱", fontSize = 13.sp)
            }
        }
    }
    HorizontalDivider(color = DetailCardBorder)
}

@Composable
private fun RecipeDetailLoadingOrEmpty(modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = HomeColors.BrandBrown)
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "正在加载食谱详情...",
                color = DetailTextSecondary,
                fontSize = 14.sp,
            )
        }
    }
}

@Composable
private fun RecipeDetailContent(recipe: RecipeMenu, modifier: Modifier = Modifier) {
    val imageUrl = recipe.img ?: recipe.imageUrl
    val nutrition = rememberNutrition(recipe)

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 18.dp),
    ) {
        Column(modifier = Modifier.widthIn(max = 1080.dp).align(Alignment.CenterHorizontally)) {
            Row(horizontalArrangement = Arrangement.spacedBy(18.dp)) {
                Box(
                    modifier = Modifier
                        .weight(1.2f)
                        .height(280.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, DetailCardBorder, RoundedCornerShape(12.dp))
                        .background(DetailCardBg),
                ) {
                    if (!imageUrl.isNullOrBlank()) {
                        AsyncImage(
                            model = imageUrl,
                            contentDescription = recipe.name,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,
                        )
                    } else {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("暂无图片", color = DetailTextSecondary)
                        }
                    }
                }
                Column(
                    modifier = Modifier.weight(0.8f),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text(
                        text = recipe.name,
                        fontSize = 44.sp,
                        fontWeight = FontWeight.Bold,
                        color = DetailTextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = "经典家常做法，适配 ${recipe.mealType.title} 场景，口味标签：${recipe.tag.ifBlank { "家常" }}。",
                        color = DetailTextSecondary,
                        fontSize = 18.sp,
                        lineHeight = 28.sp,
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        DetailPill(text = recipe.mealType.title)
                        DetailPill(icon = { Icon(Icons.Outlined.Schedule, contentDescription = null, modifier = Modifier.size(14.dp), tint = DetailTextSecondary) }, text = recipe.duration)
                        DetailPill(text = recipe.difficulty)
                    }
                    DetailPill(text = nutrition.calories)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, DetailCardBorder, RoundedCornerShape(12.dp))
                            .background(Color.White)
                            .padding(vertical = 12.dp, horizontal = 8.dp),
                    ) {
                        NutritionCell("蛋白质", nutrition.protein, Modifier.weight(1f))
                        NutritionCell("碳水", nutrition.carbs, Modifier.weight(1f))
                        NutritionCell("脂肪", nutrition.fat, Modifier.weight(1f))
                    }
                }
            }

            Spacer(modifier = Modifier.height(26.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(18.dp),
                verticalAlignment = Alignment.Top,
            ) {
                Column(modifier = Modifier.weight(0.9f), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text("原材料", fontSize = 36.sp, fontWeight = FontWeight.Bold, color = DetailTextPrimary)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("2 人份", color = DetailTextSecondary, fontSize = 14.sp)
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White)
                            .border(1.dp, DetailCardBorder, RoundedCornerShape(12.dp))
                            .padding(horizontal = 18.dp, vertical = 14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        recipe.ingredients.forEach { ingredient ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(DetailAccent),
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(text = ingredient.name, color = DetailTextPrimary, fontSize = 15.sp)
                                }
                                Text(text = ingredient.number, color = DetailTextSecondary, fontSize = 15.sp)
                            }
                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFFFFBF3))
                            .border(1.dp, Color(0xFFF4E1BD), RoundedCornerShape(12.dp))
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                    ) {
                        Text("火候提醒", color = HomeColors.BrandBrown, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            "食材翻炒时建议中火，避免糊底；蔬菜类食材保留少量脆感更佳。",
                            color = DetailTextSecondary,
                            fontSize = 13.sp,
                            lineHeight = 20.sp,
                        )
                    }
                }

                Column(modifier = Modifier.weight(1.1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("制作过程", fontSize = 36.sp, fontWeight = FontWeight.Bold, color = DetailTextPrimary)
                    recipe.steps.forEachIndexed { index, step ->
                        Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .clip(CircleShape)
                                    .background(DetailAccent),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text("${index + 1}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                            Column {
                                Text(
                                    text = step,
                                    color = DetailTextPrimary,
                                    fontSize = 15.sp,
                                    lineHeight = 24.sp,
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                HorizontalDivider(color = DetailCardBorder)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(22.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                Button(
                    onClick = { },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE8EBF1),
                        contentColor = DetailTextPrimary,
                    ),
                    contentPadding = PaddingValues(horizontal = 30.dp, vertical = 12.dp),
                ) {
                    Text("下载 PDF")
                }
                Spacer(modifier = Modifier.width(12.dp))
                Button(
                    onClick = { },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DetailAccent,
                        contentColor = Color.White,
                    ),
                    contentPadding = PaddingValues(horizontal = 32.dp, vertical = 12.dp),
                ) {
                    Text("开始烹饪指导")
                }
            }
        }
    }
}

@Composable
private fun DetailPill(
    text: String,
    icon: (@Composable () -> Unit)? = null,
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(Color(0xFFF2F4F8))
            .padding(horizontal = 10.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (icon != null) {
            icon()
            Spacer(modifier = Modifier.width(4.dp))
        }
        Text(text = text, color = DetailTextPrimary, fontSize = 13.sp)
    }
}

@Composable
private fun NutritionCell(title: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(title, color = DetailTextSecondary, fontSize = 13.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(value, color = DetailTextPrimary, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
    }
}

private data class NutritionSummary(
    val calories: String,
    val protein: String,
    val carbs: String,
    val fat: String,
)

private fun rememberNutrition(recipe: RecipeMenu): NutritionSummary {
    val minutes = recipe.duration.filter { it.isDigit() }.toIntOrNull() ?: 20
    val base = max(180, minutes * 14 + (recipe.id % 40))
    return NutritionSummary(
        calories = "$base 卡",
        protein = "${max(10, base / 18)}克",
        carbs = "${max(8, base / 12)}克",
        fat = "${max(6, base / 20)}克",
    )
}
