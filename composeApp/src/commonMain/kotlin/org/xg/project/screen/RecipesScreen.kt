package org.xg.project.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale

import org.koin.compose.viewmodel.koinViewModel
import org.xg.project.domain.model.MealType
import org.xg.project.domain.model.Recipe
import org.xg.project.presentation.recipes.RecipesIntent
import org.xg.project.presentation.recipes.RecipesViewModel

@Composable
fun RecipesScreen(
    viewModel: RecipesViewModel = koinViewModel(),
    refreshTrigger: Int = 0,
    onNavigateToManualInput: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(refreshTrigger) {
        viewModel.handleIntent(RecipesIntent.LoadRecipes)
    }
    
    Scaffold { innerPadding ->
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFFF59E42))
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(GlassStyle.BgGradient)
                .padding(innerPadding)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(GlassStyle.SurfaceStrong)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "食谱灵感库", modifier = Modifier.padding(start = 10.dp), fontWeight = FontWeight.Bold, fontSize = 22.sp, color = GlassStyle.TextPrimary)

                val buttonText = if (state.selectedRecipeIds.isNotEmpty()) "保存" else "+ 手动录入"
                Button(
                    onClick = {
                        if (state.selectedRecipeIds.isNotEmpty()) {
                            viewModel.handleIntent(RecipesIntent.SaveSelections)
                        } else {
                            onNavigateToManualInput()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.24f))
                ) {
                    Text(buttonText, color = GlassStyle.TextPrimary)
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Column(
                    modifier = Modifier
                        .weight(2.6f)
                        .fillMaxHeight()
                        .glassPanel(RoundedCornerShape(24.dp))
                        .verticalScroll(rememberScrollState())
                        .padding(vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    MealType.values().forEach { mealType ->
                        val isSelected = state.selectedMealType == mealType
                        Button(
                            onClick = {
                                viewModel.handleIntent(RecipesIntent.ChangeMealType(mealType))
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 2.dp, vertical = 6.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSelected) Color.White.copy(alpha = 0.28f) else Color.White.copy(alpha = 0.16f),
                                contentColor = if (isSelected) GlassStyle.TextPrimary else GlassStyle.TextSecondary
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(mealType.title, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(all = 12.dp),
                    modifier = Modifier
                        .weight(7.4f)
                        .glassPanel(RoundedCornerShape(24.dp))
                        .fillMaxHeight()
                ) {
                    items(state.currentRecipes) { recipe ->
                        RecipeCard(
                            recipe = recipe,
                            isSelected = recipe.id in state.selectedRecipeIds,
                            onToggle = { viewModel.handleIntent(RecipesIntent.ToggleSelection(recipe.id)) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RecipeCard(
    recipe: Recipe,
    isSelected: Boolean,
    onToggle: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable { onToggle() }
            .then(
                // 如果选中，添加金色边框
                if (isSelected) Modifier.border(
                    width = 1.5.dp,
                    color = GlassStyle.Stroke,
                    shape = RoundedCornerShape(24.dp)
                ) else Modifier
            )
            .glassPanel(RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        // 使用 Box 堆叠图片和文字
        Box(
            modifier = Modifier
                .height(140.dp) // 增加高度让图片更大一点
                .fillMaxWidth()
                .background(Color.White.copy(alpha = 0.15f))
        ) {
            // 底层：图片
            if (recipe.img != null) {
                AsyncImage(
                    model = recipe.img,
                    contentDescription = recipe.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            // 右上角：标签
            if (recipe.tag.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .padding(6.dp)
                        .background(Color.White.copy(alpha = 0.38f), RoundedCornerShape(8.dp))
                        .align(Alignment.TopEnd)
                ) {
                    Text(
                        text = recipe.tag,
                        fontSize = 10.sp,
                        color = GlassStyle.TextPrimary,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            // 底部：半透明渐变背景 + 文字信息
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.White.copy(alpha = 0.36f)
                            )
                        )
                    )
                    .padding(8.dp)
            ) {
                Text(
                    recipe.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = GlassStyle.TextPrimary,
                    maxLines = 1
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(recipe.duration, fontSize = 10.sp, color = GlassStyle.TextSecondary)
                    Text(" | ", fontSize = 10.sp, color = GlassStyle.TextSecondary.copy(alpha = 0.6f))
                    Text(recipe.difficulty, fontSize = 10.sp, color = GlassStyle.TextPrimary)
                }
            }
        }
    }
}
