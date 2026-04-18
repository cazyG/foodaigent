package org.xg.project.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import org.xg.project.domain.model.Ingredient
import org.xg.project.domain.model.MealType

private val GlassBgTop = Color(0xFFF2F7FF)
private val GlassBgBottom = Color(0xFFFAF4FF)
private val GlassText = Color(0xFF475569)
private val GlassSurface = Color.White.copy(alpha = 0.14f)
private val GlassSurfaceStrong = Color.White.copy(alpha = 0.2f)
private val GlassStroke = Color.White.copy(alpha = 0.58f)
private val FieldShape = RoundedCornerShape(14.dp)
private val AppBarTextSize = 15.sp
private val SectionTitleSize = 16.sp
private val BodyTextSize = 14.sp

data class RecipeDetailState(
    val id: Int,
    val recipeName: String,
    val ingredients: List<Ingredient>,
    val steps: List<String>,
    val duration: String,
    val difficulty: String,
    val tag: String,
    val mealType: String,
    val imageUrl: String?
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailScreen(
    recipeId: Int,
    onBack: () -> Unit
) {
    // 模拟数据加载。实际项目中应该从 ViewModel 中通过 recipeId 获取
    val recipe = remember(recipeId) {
        RecipeDetailState(
            id = recipeId,
            recipeName = "美味食谱 $recipeId",
            ingredients = listOf(
                Ingredient("1", "西红柿", "2个"),
                Ingredient("2", "鸡蛋", "3个"),
                Ingredient("3", "葱花", "适量"),
                Ingredient("4", "盐", "1勺")
            ),
            steps = listOf(
                "将西红柿洗净切块，鸡蛋打散备用",
                "热锅凉油，倒入鸡蛋炒熟盛出",
                "底油炒香葱花，下西红柿炒出汁",
                "加入炒好的鸡蛋，加盐调味，翻炒均匀即可出锅"
            ),
            duration = "15分钟",
            difficulty = "3星",
            tag = "家常菜",
            mealType = MealType.LUNCH.title,
            imageUrl = null
        )
    }

    Scaffold(
        containerColor = Color.Transparent,
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(GlassBgTop, GlassBgBottom)
                )
            ),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "食谱详情",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = GlassText
                    )
                },
                navigationIcon = {
                    TextButton(
                        onClick = onBack,
                        modifier = Modifier.sizeIn(minHeight = 36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "返回",
                            tint = GlassText,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            "返回",
                            color = GlassText,
                            fontWeight = FontWeight.Medium,
                            fontSize = AppBarTextSize
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = GlassSurfaceStrong,
                    titleContentColor = GlassText,
                    navigationIconContentColor = GlassText,
                    actionIconContentColor = GlassText
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 600.dp)
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .shadow(
                            6.dp,
                            RoundedCornerShape(20.dp),
                            ambientColor = Color.White.copy(alpha = 0.24f),
                            spotColor = Color.Black.copy(alpha = 0.07f)
                        )
                        .background(
                            color = GlassSurface,
                            shape = RoundedCornerShape(20.dp)
                        )
                        .border(
                            width = 1.dp,
                            color = GlassStroke,
                            shape = RoundedCornerShape(20.dp)
                        )
                        .padding(16.dp)
                ) {
                    // 图片展示
                    if (recipe.imageUrl != null) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .padding(bottom = 16.dp),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            AsyncImage(
                                model = recipe.imageUrl,
                                contentDescription = recipe.recipeName,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }

                    // 标题与标签
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = recipe.recipeName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp,
                            color = GlassText,
                            modifier = Modifier.weight(1f)
                        )
                        if (recipe.tag.isNotEmpty()) {
                            Box(
                                modifier = Modifier
                                    .background(Color.White.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = recipe.tag,
                                    fontSize = 12.sp,
                                    color = GlassText,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    // 基础信息 (用餐类型, 时长, 难度)
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        InfoBadge("类型", recipe.mealType)
                        InfoBadge("时长", recipe.duration)
                        InfoBadge("难度", recipe.difficulty)
                    }

                    // 原材料
                    if (recipe.ingredients.isNotEmpty()) {
                        Text(
                            text = "原材料",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = SectionTitleSize,
                            color = GlassText,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                                .border(1.dp, GlassStroke, RoundedCornerShape(12.dp))
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            recipe.ingredients.forEach { ingredient ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(ingredient.name, color = GlassText, fontSize = BodyTextSize)
                                    Text(ingredient.quantity, color = GlassText, fontSize = BodyTextSize, fontWeight = FontWeight.Medium)
                                }
                                Divider(color = GlassStroke.copy(alpha = 0.3f), thickness = 1.dp)
                            }
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                    }

                    // 制作步骤
                    if (recipe.steps.isNotEmpty()) {
                        Text(
                            text = "制作过程",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = SectionTitleSize,
                            color = GlassText,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            recipe.steps.forEachIndexed { index, step ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                                        .border(1.dp, GlassStroke, RoundedCornerShape(12.dp))
                                        .padding(12.dp),
                                    crossAlignment = Alignment.Top
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .background(Color(0xFFF59E42).copy(alpha = 0.8f), RoundedCornerShape(12.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("${index + 1}", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(step, color = GlassText, fontSize = BodyTextSize, lineHeight = 20.sp)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoBadge(label: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
            .border(1.dp, GlassStroke, RoundedCornerShape(12.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(label, fontSize = 12.sp, color = GlassText.copy(alpha = 0.7f))
        Spacer(modifier = Modifier.height(4.dp))
        Text(value, fontSize = 14.sp, color = GlassText, fontWeight = FontWeight.SemiBold)
    }
}
