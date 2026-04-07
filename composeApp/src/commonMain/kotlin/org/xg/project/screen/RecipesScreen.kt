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

// 食谱数据类，增加 mealType 字段表示所属分类
data class Recipe(
    val id: Int,
    val name: String,
    val duration: String,
    val difficulty: String,
    val tag: String,
    val mealType: MealType,
    val img: String? = null
)

enum class MealType(val title: String) {
    BREAKFAST("早餐"),
    LUNCH("午餐"),
    DINNER("晚餐"),
    SNACK("宵夜")
}

// 示例数据，按分类整理
val sampleRecipes = listOf(
    Recipe(1, "经典红烧肉", "45分钟", "中等难度", "老公爱吃", MealType.LUNCH,"https://modao.cc/agent-py/media/generated_images/2026-03-30/8801c842ff4c4601b0eaef5a8bb46f63.jpg"),
    Recipe(2, "牛油果大虾沙拉", "15分钟", "新手入门", "老婆最爱", MealType.LUNCH),
    Recipe(3, "西红柿炒鸡蛋", "10分钟", "必点基础", "", MealType.DINNER),
    Recipe(4, "秘制宫保鸡丁", "25分钟", "挑战厨艺", "", MealType.DINNER),
    Recipe(5, "孔雀开屏清蒸鱼", "20分钟", "颜值极高", "低脂健康", MealType.DINNER),
    Recipe(6, "正宗麻婆豆腐", "15分钟", "下饭神器", "", MealType.LUNCH),
    Recipe(7, "冬瓜薏米排骨汤", "90分钟", "滋补养生", "", MealType.DINNER),
    Recipe(8, "蒜蓉粉丝蒸大虾", "20分钟", "宴客之选", "", MealType.DINNER),
    Recipe(9, "全麦三明治", "10分钟", "新手入门", "减脂", MealType.BREAKFAST),
    Recipe(10, "燕麦水果酸奶碗", "5分钟", "简单", "快手", MealType.BREAKFAST),
    Recipe(11, "煎饺", "15分钟", "中等难度", "中式", MealType.BREAKFAST),
    Recipe(12, "煎饺", "15分钟", "中等难度", "中式", MealType.BREAKFAST),
    Recipe(13, "煎饺", "15分钟", "中等难度", "中式", MealType.BREAKFAST),
    Recipe(14, "煎饺", "15分钟", "中等难度", "中式", MealType.BREAKFAST),
    Recipe(15, "煎饺", "15分钟", "中等难度", "中式", MealType.BREAKFAST),
    Recipe(16, "煎饺", "15分钟", "中等难度", "中式", MealType.BREAKFAST),
    Recipe(17, "煎饺", "15分钟", "中等难度", "中式", MealType.BREAKFAST),
    Recipe(18, "煎饺", "15分钟", "中等难度", "中式", MealType.BREAKFAST),
    Recipe(19, "煎饺", "15分钟", "中等难度", "中式", MealType.BREAKFAST),
    Recipe(20, "煎饺", "15分钟", "中等难度", "中式", MealType.BREAKFAST),

    Recipe(12, "烤鸡翅", "25分钟", "简单", "宵夜最爱", MealType.SNACK),
    Recipe(13, "芝士焗红薯", "20分钟", "简单", "甜品", MealType.SNACK)
)

@Composable
fun RecipesScreen() {
    // 当前选中的分类
    var selectedMealType by remember { mutableStateOf(MealType.BREAKFAST) }
    // 选中的食谱 id 集合
    var selectedRecipeIds by remember { mutableStateOf(setOf<Int>()) }

    // 当前分类下的食谱列表
    val currentRecipes = remember(selectedMealType) {
        sampleRecipes.filter { it.mealType == selectedMealType }
    }

    // 处理卡片点击（多选）
    fun toggleSelection(recipeId: Int) {
        selectedRecipeIds = if (selectedRecipeIds.contains(recipeId)) {
            selectedRecipeIds - recipeId
        } else {
            selectedRecipeIds + recipeId
        }
    }

    // 保存选中的食谱
    fun saveSelections() {
        val selectedRecipes = sampleRecipes.filter { it.id in selectedRecipeIds }
        // 示例：打印选中的食谱，实际可替换为添加计划等逻辑
        println("保存选中食谱: ${selectedRecipes.joinToString { it.name }}")
        // 清空选中状态
        selectedRecipeIds = emptySet()
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
            val buttonText = if (selectedRecipeIds.isNotEmpty()) "保存" else "+ 手动录入"
            Button(
                onClick = {
                    if (selectedRecipeIds.isNotEmpty()) {
                        saveSelections()
                    } else {
                        // 手动录入逻辑（可根据需要实现）
                        println("手动录入")
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
                    val isSelected = selectedMealType == mealType
                    Button(
                        onClick = {
                            selectedMealType = mealType
                            // 切换分类时清空选中状态
                            selectedRecipeIds = emptySet()
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
                items(currentRecipes) { recipe ->
                    RecipeCard(
                        recipe = recipe,
                        isSelected = recipe.id in selectedRecipeIds,
                        onToggle = { toggleSelection(recipe.id) }
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
        Column {
            // 图片加载区
            Box(
                modifier = Modifier
                    .height(96.dp)
                    .fillMaxWidth()
                    .background(Color.LightGray)
            ) {
                if (recipe.img != null) {
                    AsyncImage(
                        model = recipe.img,
                        contentDescription = recipe.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                if (recipe.tag.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .padding(6.dp)
                            .background(Color.White.copy(alpha = 0.8f), RoundedCornerShape(8.dp))
                            .align(Alignment.TopEnd)
                    ) {
                        Text(recipe.tag, fontSize = 10.sp, color = Color(0xFFF97316))
                    }
                }
            }
            Column(Modifier.padding(8.dp)) {
                Text(
                    recipe.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    maxLines = 1
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(recipe.duration, fontSize = 10.sp, color = Color.Gray)
                    Text(" | ", fontSize = 10.sp, color = Color.LightGray)
                    Text(recipe.difficulty, fontSize = 10.sp, color = Color(0xFFF97316))
                }
            }
        }
    }
}