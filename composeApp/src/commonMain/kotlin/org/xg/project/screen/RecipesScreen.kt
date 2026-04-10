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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.window.Dialog

import org.koin.compose.viewmodel.koinViewModel
import org.xg.project.domain.model.MealType
import org.xg.project.domain.model.Recipe
import org.xg.project.presentation.recipes.RecipesViewModel
import org.xg.project.presentation.recipes.RecipesIntent
import org.xg.project.screen.ManualRecipeInputScreen

@Composable
fun RecipesScreen(
    viewModel: RecipesViewModel = koinViewModel(),
    onNavigateToManualInput: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    
    if (state.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Color(0xFFF59E42))
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFCFAF2))
    ) {
        // 顶部栏：标题 + 按钮（固定）
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding( 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "食谱灵感库", modifier = Modifier.padding(start = 10.dp), fontWeight = FontWeight.Bold, fontSize = 22.sp)

            // 按钮根据是否有选中项切换文本和功能
            val buttonText = if (state.selectedRecipeIds.isNotEmpty()) "保存" else "+ 手动录入"
            Button(
                onClick = {
                    if (state.selectedRecipeIds.isNotEmpty()) {
                        viewModel.handleIntent(RecipesIntent.SaveSelections)
                    } else {
                        // 使用路由导航到手动录入页面
                        onNavigateToManualInput()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFEDD5))
            ) {
                Text(buttonText, color = Color(0xFFF59E42))
            }
        }

        // 下方主体区域：左侧分类栏 + 右侧食谱网格（比例 2:8）
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)  // 填充剩余高度
        ) {
            // 左侧分类栏 (权重2)
            Column(
                modifier = Modifier
                    .weight(2f)
                    .fillMaxHeight()
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
                            .padding(horizontal = 4.dp, vertical = 6.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected) Color(0xFFF59E42) else Color.White,
                            contentColor = if (isSelected) Color.White else Color(0xFF4B5563)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(mealType.title, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }

            // 右侧食谱网格 (权重8)
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(all = 12.dp),
                modifier = Modifier
                    .weight(8f)
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
                    width = 2.dp,
                    color = Color(0xFFF59E42),
                    shape = RoundedCornerShape(24.dp)
                ) else Modifier
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFFFFF3E6) else Color.White
        )
    ) {
        // 使用 Box 堆叠图片和文字
        Box(
            modifier = Modifier
                .height(140.dp) // 增加高度让图片更大一点
                .fillMaxWidth()
                .background(Color.LightGray)
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
                        .background(Color.White.copy(alpha = 0.8f), RoundedCornerShape(8.dp))
                        .align(Alignment.TopEnd)
                ) {
                    Text(
                        text = recipe.tag,
                        fontSize = 10.sp,
                        color = Color(0xFFF97316),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            // 底部：半透明渐变背景 + 文字信息
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.5f)) // 半透明黑色背景，让白色文字更清晰
                    .padding(8.dp)
            ) {
                Text(
                    recipe.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color.White,
                    maxLines = 1
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(recipe.duration, fontSize = 10.sp, color = Color.White.copy(alpha = 0.8f))
                    Text(" | ", fontSize = 10.sp, color = Color.White.copy(alpha = 0.5f))
                    Text(recipe.difficulty, fontSize = 10.sp, color = Color(0xFFFFB366))
                }
            }
        }
    }
}