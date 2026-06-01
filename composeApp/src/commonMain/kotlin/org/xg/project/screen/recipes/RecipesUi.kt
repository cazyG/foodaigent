package org.xg.project.screen.recipes

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.xg.project.domain.model.MealType
import org.xg.project.domain.model.RecipeMenu
import org.xg.project.presentation.recipes.RecipesState

object RecipesBreakpoints {
    val CompactMax = 720.dp
    val WideMin = 1100.dp
}

object RecipesColors {
    val PageBackground = Color(0xFFF5F5F5)
    val CardWhite = Color.White
    val BrandBrown = Color(0xFF7D4A05)
    val BrandBrownDark = Color(0xFF6B3F04)
    val BrandOrange = Color(0xFFF0883A)
    val TextPrimary = Color(0xFF1F2937)
    val TextSecondary = Color(0xFF6B7280)
    val ChipInactiveBg = Color(0xFFF3F4F6)
    val ChipInactiveBorder = Color(0xFFE5E7EB)
    val SidebarActiveBg = Color(0xFFF5EDE4)
    val SearchBg = Color(0xFFF3F4F6)
    val TagBreakfast = Color(0xFFFFF3E8)
    val TagLunch = Color(0xFFE8F4FC)
    val TagDinner = Color(0xFFEDE9FE)
    val TagSnack = Color(0xFFF3F4F6)
}

object RecipesFonts {
    val brandTitle = 20.sp
    val brandSubtitle = 12.sp
    val sidebarNav = 15.sp
    val pageTitle = 28.sp
    val pageSubtitle = 14.sp
    val searchPlaceholder = 14.sp
    val filterChip = 14.sp
    val cardTitle = 17.sp
    val cardTitleCompact = 18.sp
    val cardMeta = 13.sp
    val cardTag = 12.sp
    val actionButton = 14.sp
    val profileName = 14.sp
    val profileBadge = 12.sp
}

data class RecipesContentUi(
    val recipes: List<RecipeMenu>,
    val selectedMealFilter: MealType?,
    val searchQuery: String,
    val selectedRecipeIds: Set<Int>,
    val error: String?,
    val recommendedCount: Int,
)

fun RecipesState.toRecipesContentUi(): RecipesContentUi =
    RecipesContentUi(
        recipes = currentRecipes,
        selectedMealFilter = selectedMealFilter,
        searchQuery = searchQuery,
        selectedRecipeIds = selectedRecipeIds,
        error = error,
        recommendedCount = allRecipes.size.coerceAtLeast(currentRecipes.size),
    )

fun RecipeMenu.displayTags(): List<String> =
    tag.split(',', '，')
        .map { it.trim() }
        .filter { it.isNotEmpty() }
        .ifEmpty { listOf(tag).filter { it.isNotEmpty() } }
