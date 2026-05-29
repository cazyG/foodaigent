package org.xg.project.screen.recipes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import org.xg.project.domain.model.MealType
import org.xg.project.domain.model.RecipeMenu
import org.xg.project.presentation.recipes.RecipesIntent
import org.xg.project.presentation.recipes.RecipesState
import org.xg.project.screen.glassPanel

object RecipesColors {
    val BrandOrange = Color(0xFFF0883A)
    val BrandBrown = Color(0xFF8B5E3C)
    val CardWhite = Color.White
    val PageBackground = Color(0xFFF8FAFC)
    val TextPrimary = Color(0xFF1F2937)
    val TextSecondary = Color(0xFF6B7280)
}

object RecipesFonts {
    val actionButton: TextUnit = 14.sp
}

object RecipesBreakpoints {
    val CompactMax: Dp = 600.dp
}

data class RecipesContentUi(
    val recipes: List<RecipeMenu>,
    val selectedMealFilter: MealType?,
    val searchQuery: String,
    val selectedRecipeIds: Set<Int>,
)

fun RecipesState.toRecipesContentUi(): RecipesContentUi = RecipesContentUi(
    recipes = currentRecipes,
    selectedMealFilter = selectedMealFilter,
    searchQuery = searchQuery,
    selectedRecipeIds = selectedRecipeIds,
)

@Composable
fun RecipesLoadingState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = RecipesColors.BrandOrange)
    }
}

@Composable
fun RecipesCompactContent(
    content: RecipesContentUi,
    isFromHome: Boolean,
    onIntent: (RecipesIntent) -> Unit,
    onNavigateToManualInput: () -> Unit,
    onRecipeOpen: (RecipeMenu) -> Unit,
    onBack: () -> Unit,
    onSaveOrManual: () -> Unit,
    saveButtonLabel: String,
) {
    RecipesScaffold(
        content = content,
        isFromHome = isFromHome,
        onIntent = onIntent,
        onRecipeOpen = onRecipeOpen,
        onBack = if (isFromHome) onBack else null,
        onSaveOrManual = onSaveOrManual,
        saveButtonLabel = saveButtonLabel,
        columns = 2,
    )
}

@Composable
fun RecipesMediumContent(
    content: RecipesContentUi,
    isFromHome: Boolean,
    layoutWidth: Dp,
    onIntent: (RecipesIntent) -> Unit,
    onNavigateToManualInput: () -> Unit,
    onRecipeOpen: (RecipeMenu) -> Unit,
    onBack: () -> Unit,
    onSaveOrManual: () -> Unit,
    saveButtonLabel: String,
) {
    RecipesScaffold(
        content = content,
        isFromHome = isFromHome,
        onIntent = onIntent,
        onRecipeOpen = onRecipeOpen,
        onBack = if (isFromHome) onBack else null,
        onSaveOrManual = onSaveOrManual,
        saveButtonLabel = saveButtonLabel,
        columns = if (layoutWidth >= 900.dp) 3 else 2,
    )
}

@Composable
fun RecipesTabletContent(
    content: RecipesContentUi,
    isFromHome: Boolean,
    layoutWidth: Dp,
    onIntent: (RecipesIntent) -> Unit,
    onNavigateToManualInput: () -> Unit,
    onRecipeOpen: (RecipeMenu) -> Unit,
    onSaveOrManual: () -> Unit,
    saveButtonLabel: String,
) {
    RecipesScaffold(
        content = content,
        isFromHome = isFromHome,
        onIntent = onIntent,
        onRecipeOpen = onRecipeOpen,
        onBack = null,
        onSaveOrManual = onSaveOrManual,
        saveButtonLabel = saveButtonLabel,
        columns = if (layoutWidth >= 1000.dp) 4 else 3,
    )
}

@Composable
fun RecipesWideContent(
    content: RecipesContentUi,
    isFromHome: Boolean,
    onIntent: (RecipesIntent) -> Unit,
    onNavigateToManualInput: () -> Unit,
    onRecipeOpen: (RecipeMenu) -> Unit,
    onSaveOrManual: () -> Unit,
    saveButtonLabel: String,
) {
    RecipesScaffold(
        content = content,
        isFromHome = isFromHome,
        onIntent = onIntent,
        onRecipeOpen = onRecipeOpen,
        onBack = null,
        onSaveOrManual = onSaveOrManual,
        saveButtonLabel = saveButtonLabel,
        columns = 4,
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun RecipesScaffold(
    content: RecipesContentUi,
    isFromHome: Boolean,
    onIntent: (RecipesIntent) -> Unit,
    onRecipeOpen: (RecipeMenu) -> Unit,
    onBack: (() -> Unit)?,
    onSaveOrManual: () -> Unit,
    saveButtonLabel: String,
    columns: Int,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(if (isFromHome) RecipesColors.PageBackground else Color.Transparent)
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (onBack != null) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                }
            }
            Text(
                text = if (isFromHome) "选择菜谱" else "菜谱库",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = RecipesColors.TextPrimary,
                modifier = Modifier.weight(1f),
            )
            Button(
                onClick = onSaveOrManual,
                colors = ButtonDefaults.buttonColors(containerColor = RecipesColors.BrandBrown),
                shape = RoundedCornerShape(10.dp),
            ) {
                Text(saveButtonLabel, color = Color.White, fontSize = RecipesFonts.actionButton)
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = content.searchQuery,
            onValueChange = { onIntent(RecipesIntent.UpdateSearchQuery(it)) },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("搜索菜谱") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
        )
        Spacer(modifier = Modifier.height(10.dp))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
                selected = content.selectedMealFilter == null,
                onClick = { onIntent(RecipesIntent.ChangeMealFilter(null)) },
                label = { Text("全部") },
            )
            MealType.entries.forEach { meal ->
                FilterChip(
                    selected = content.selectedMealFilter == meal,
                    onClick = { onIntent(RecipesIntent.ChangeMealFilter(meal)) },
                    label = { Text(meal.title) },
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        if (content.recipes.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("暂无菜谱", color = RecipesColors.TextSecondary)
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(columns),
                contentPadding = PaddingValues(bottom = 80.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize(),
            ) {
                items(content.recipes, key = { it.id }) { recipe ->
                    RecipeCard(
                        recipe = recipe,
                        selected = recipe.id in content.selectedRecipeIds,
                        selectionMode = isFromHome,
                        onClick = { onRecipeOpen(recipe) },
                    )
                }
            }
        }
    }
}

@Composable
private fun RecipeCard(
    recipe: RecipeMenu,
    selected: Boolean,
    selectionMode: Boolean,
    onClick: () -> Unit,
) {
    val borderColor = if (selected) RecipesColors.BrandOrange else Color.Transparent
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .glassPanel(RoundedCornerShape(14.dp)),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        border = if (selectionMode && selected) {
            androidx.compose.foundation.BorderStroke(2.dp, borderColor)
        } else {
            null
        },
    ) {
        Column {
            val imageUrl = recipe.imageUrl ?: recipe.img
            if (!imageUrl.isNullOrBlank()) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = recipe.name,
                    modifier = Modifier.fillMaxWidth().height(110.dp),
                    contentScale = ContentScale.Crop,
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .background(RecipesColors.PageBackground),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(recipe.name.take(1), fontSize = 28.sp, color = RecipesColors.BrandBrown)
                }
            }
            Column(modifier = Modifier.padding(10.dp)) {
                Text(recipe.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = RecipesColors.TextPrimary)
                Text(
                    "${recipe.difficulty} · ${recipe.duration}",
                    fontSize = 12.sp,
                    color = RecipesColors.TextSecondary,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
        }
    }
}
