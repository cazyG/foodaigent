package org.xg.project.screen.recipes

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.SignalCellularAlt
import androidx.compose.material.icons.outlined.Whatshot
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import org.xg.project.domain.model.MealType
import org.xg.project.domain.model.RecipeMenu
import org.xg.project.presentation.recipes.RecipesIntent

@Composable
fun RecipesLoadingState(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = RecipesColors.BrandOrange)
    }
}

@Composable
fun RecipesErrorState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "加载失败",
            fontSize = RecipesFonts.pageSubtitle,
            fontWeight = FontWeight.Bold,
            color = RecipesColors.TextPrimary,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = message, fontSize = RecipesFonts.cardMeta, color = RecipesColors.TextSecondary)
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(containerColor = RecipesColors.BrandBrown),
        ) {
            Text("重试", color = Color.White)
        }
    }
}

@Composable
fun RecipesSearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "搜索食谱、食材或标签...",
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(44.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(RecipesColors.SearchBg)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            Icons.Default.Search,
            contentDescription = null,
            tint = RecipesColors.TextSecondary,
            modifier = Modifier.size(20.dp),
        )
        Spacer(modifier = Modifier.width(10.dp))
        androidx.compose.foundation.text.BasicTextField(
            value = query,
            onValueChange = onQueryChange,
            singleLine = true,
            textStyle = androidx.compose.ui.text.TextStyle(
                fontSize = RecipesFonts.searchPlaceholder,
                color = RecipesColors.TextPrimary,
            ),
            modifier = Modifier.weight(1f),
            decorationBox = { inner ->
                Box(contentAlignment = Alignment.CenterStart) {
                    if (query.isEmpty()) {
                        Text(
                            text = placeholder,
                            fontSize = RecipesFonts.searchPlaceholder,
                            color = RecipesColors.TextSecondary,
                        )
                    }
                    inner()
                }
            },
        )
    }
}

@Composable
fun RecipesFilterChipsRow(
    selectedFilter: MealType?,
    onFilterSelected: (MealType?) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()
    Row(
        modifier = modifier.horizontalScroll(scrollState),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        RecipesFilterChip(
            label = "全部",
            selected = selectedFilter == null,
            onClick = { onFilterSelected(null) },
        )
        MealType.entries.forEach { mealType ->
            RecipesFilterChip(
                label = mealType.title,
                selected = selectedFilter == mealType,
                onClick = { onFilterSelected(mealType) },
            )
        }
    }
}

@Composable
private fun RecipesFilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val background = if (selected) RecipesColors.BrandBrown else RecipesColors.ChipInactiveBg
    val borderColor = if (selected) RecipesColors.BrandBrown else RecipesColors.ChipInactiveBorder
    val textColor = if (selected) Color.White else RecipesColors.TextPrimary
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(background)
            .border(1.dp, borderColor, RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 8.dp),
    ) {
        Text(
            text = label,
            fontSize = RecipesFonts.filterChip,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            color = textColor,
        )
    }
}

@Composable
fun RecipesRecipeGrid(
    recipes: List<RecipeMenu>,
    gridColumns: Int,
    isFromHome: Boolean,
    selectedRecipeIds: Set<Int>,
    onRecipeClick: (RecipeMenu) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: androidx.compose.foundation.layout.PaddingValues =
        androidx.compose.foundation.layout.PaddingValues(0.dp),
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(gridColumns.coerceAtLeast(1)),
        modifier = modifier.fillMaxSize(),
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        items(recipes, key = { it.id }) { recipe ->
            RecipesGridCard(
                recipe = recipe,
                isSelected = recipe.id in selectedRecipeIds,
                showSelectionBorder = isFromHome,
                onClick = { onRecipeClick(recipe) },
            )
        }
    }
}

@Composable
fun RecipesRecipeList(
    recipes: List<RecipeMenu>,
    isFromHome: Boolean,
    selectedRecipeIds: Set<Int>,
    onRecipeClick: (RecipeMenu) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: androidx.compose.foundation.layout.PaddingValues =
        androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 8.dp),
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        items(recipes, key = { it.id }) { recipe ->
            RecipesCompactCard(
                recipe = recipe,
                isSelected = recipe.id in selectedRecipeIds,
                showSelectionBorder = isFromHome,
                onClick = { onRecipeClick(recipe) },
            )
        }
    }
}

@Composable
fun RecipesContentBody(
    content: RecipesContentUi,
    isFromHome: Boolean,
    gridColumns: Int,
    onIntent: (RecipesIntent) -> Unit,
    onRecipeOpen: (RecipeMenu) -> Unit,
    modifier: Modifier = Modifier,
    showPageHeader: Boolean = true,
    searchMaxWidth: androidx.compose.ui.unit.Dp? = null,
) {
    Column(modifier = modifier.fillMaxSize()) {
        if (showPageHeader) {
            RecipesPageHeader(
                recommendedCount = content.recommendedCount,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            )
        }
        val searchModifier = if (searchMaxWidth != null) {
            Modifier.width(searchMaxWidth).padding(horizontal = 16.dp)
        } else {
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        }
        RecipesSearchField(
            query = content.searchQuery,
            onQueryChange = { onIntent(RecipesIntent.UpdateSearchQuery(it)) },
            modifier = searchModifier,
            placeholder = if (gridColumns <= 1) "搜索食谱、食材..." else "搜索食谱、食材或标签...",
        )
        Spacer(modifier = Modifier.height(12.dp))
        RecipesFilterChipsRow(
            selectedFilter = content.selectedMealFilter,
            onFilterSelected = { onIntent(RecipesIntent.ChangeMealFilter(it)) },
            modifier = Modifier.padding(horizontal = 16.dp),
        )
        Spacer(modifier = Modifier.height(16.dp))
        if (content.error != null && content.recipes.isEmpty()) {
            RecipesErrorState(
                message = content.error,
                onRetry = { onIntent(RecipesIntent.LoadRecipes) },
            )
        } else if (gridColumns <= 1) {
            RecipesRecipeList(
                recipes = content.recipes,
                isFromHome = isFromHome,
                selectedRecipeIds = content.selectedRecipeIds,
                onRecipeClick = onRecipeOpen,
            )
        } else {
            RecipesRecipeGrid(
                recipes = content.recipes,
                gridColumns = gridColumns,
                isFromHome = isFromHome,
                selectedRecipeIds = content.selectedRecipeIds,
                onRecipeClick = onRecipeOpen,
                modifier = Modifier.padding(horizontal = 16.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 24.dp),
            )
        }
    }
}

@Composable
fun RecipesPageHeader(
    recommendedCount: Int,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = "食谱灵感库",
            fontSize = RecipesFonts.pageTitle,
            fontWeight = FontWeight.Bold,
            color = RecipesColors.TextPrimary,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "发现您的下一次美味创作，今日推荐 $recommendedCount 道精品菜谱",
            fontSize = RecipesFonts.pageSubtitle,
            color = RecipesColors.TextSecondary,
        )
    }
}

@Composable
fun RecipesGridCard(
    recipe: RecipeMenu,
    isSelected: Boolean,
    onClick: () -> Unit,
    showSelectionBorder: Boolean = false,
) {
    var favorited by remember(recipe.id) { mutableStateOf(false) }
    val shape = RoundedCornerShape(16.dp)
    val borderModifier =
        if (isSelected && showSelectionBorder) {
            Modifier.border(2.dp, RecipesColors.BrandOrange, shape)
        } else {
            Modifier
        }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .then(borderModifier)
            .background(RecipesColors.CardWhite)
            .clickable(onClick = onClick),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.15f)
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
        ) {
            RecipeImage(recipe = recipe)
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(10.dp)
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.92f))
                    .clickable { favorited = !favorited },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = if (favorited) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = null,
                    tint = RecipesColors.BrandBrown,
                    modifier = Modifier.size(18.dp),
                )
            }
        }
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = recipe.name,
                fontSize = RecipesFonts.cardTitle,
                fontWeight = FontWeight.Bold,
                color = RecipesColors.TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (recipe.displayTags().isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    recipe.displayTags().take(2).forEach { tag ->
                        RecipeTagChip(text = tag)
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            RecipeMetaRow(recipe = recipe)
        }
    }
}

@Composable
fun RecipesCompactCard(
    recipe: RecipeMenu,
    isSelected: Boolean,
    onClick: () -> Unit,
    showSelectionBorder: Boolean = false,
) {
    var favorited by remember(recipe.id) { mutableStateOf(false) }
    val shape = RoundedCornerShape(18.dp)
    val borderModifier =
        if (isSelected && showSelectionBorder) {
            Modifier.border(2.dp, RecipesColors.BrandOrange, shape)
        } else {
            Modifier
        }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .then(borderModifier)
            .background(RecipesColors.CardWhite)
            .clickable(onClick = onClick),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(210.dp),
        ) {
            RecipeImage(recipe = recipe)
            Row(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                recipe.displayTags().take(2).forEach { tag ->
                    RecipeTagChip(text = tag, onImage = true)
                }
            }
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.92f))
                    .clickable { favorited = !favorited },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = if (favorited) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = null,
                    tint = RecipesColors.BrandBrown,
                    modifier = Modifier.size(18.dp),
                )
            }
        }
        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)) {
            Text(
                text = recipe.name,
                fontSize = RecipesFonts.cardTitleCompact,
                fontWeight = FontWeight.Bold,
                color = RecipesColors.TextPrimary,
            )
            Spacer(modifier = Modifier.height(8.dp))
            RecipeMetaRow(recipe = recipe)
        }
    }
}

@Composable
private fun RecipeImage(recipe: RecipeMenu) {
    val displayImg = recipe.img ?: recipe.imageUrl
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(RecipesColors.ChipInactiveBg),
    ) {
        if (displayImg != null) {
            AsyncImage(
                model = displayImg,
                contentDescription = recipe.name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
        }
    }
}

@Composable
private fun RecipeTagChip(text: String, onImage: Boolean = false) {
    val bg = if (onImage) Color.White.copy(alpha = 0.88f) else RecipesColors.TagBreakfast
    Text(
        text = text,
        fontSize = RecipesFonts.cardTag,
        color = RecipesColors.BrandBrown,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bg)
            .padding(horizontal = 8.dp, vertical = 4.dp),
    )
}

@Composable
private fun RecipeMetaRow(recipe: RecipeMenu) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RecipeMetaItem(icon = { Icon(Icons.Default.Schedule, null, Modifier.size(14.dp), tint = RecipesColors.TextSecondary) }, text = recipe.duration)
        RecipeMetaItem(icon = { Icon(Icons.Outlined.Whatshot, null, Modifier.size(14.dp), tint = RecipesColors.TextSecondary) }, text = metaCalories(recipe))
        RecipeMetaItem(icon = { Icon(Icons.Outlined.SignalCellularAlt, null, Modifier.size(14.dp), tint = RecipesColors.TextSecondary) }, text = recipe.difficulty)
    }
}

@Composable
private fun RecipeMetaItem(
    icon: @Composable () -> Unit,
    text: String,
) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        icon()
        Text(text = text, fontSize = RecipesFonts.cardMeta, color = RecipesColors.TextSecondary)
    }
}

private fun metaCalories(recipe: RecipeMenu): String {
    val digits = recipe.duration.filter { it.isDigit() }
    val minutes = digits.toIntOrNull() ?: 30
    return "${minutes * 8} kcal"
}
