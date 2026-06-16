package org.xg.project.feature.recipes

import org.xg.project.domain.model.MealType
import org.xg.project.domain.model.RecipeMenu

data class RecipesState(
    val isLoading: Boolean = false,
    val allRecipes: List<RecipeMenu> = emptyList(),
    val selectedMealFilter: MealType? = null,
    val searchQuery: String = "",
    val selectedRecipeIds: Set<Int> = emptySet(),
    val error: String? = null,
) {
    val currentRecipes: List<RecipeMenu>
        get() {
            val mealFiltered = selectedMealFilter?.let { filter ->
                allRecipes.filter { it.mealType == filter }
            } ?: allRecipes
            val query = searchQuery.trim()
            if (query.isEmpty()) return mealFiltered
            return mealFiltered.filter { recipe ->
                recipe.name.contains(query, ignoreCase = true) ||
                    recipe.tag.contains(query, ignoreCase = true) ||
                    recipe.difficulty.contains(query, ignoreCase = true)
            }
        }
}

sealed class RecipesIntent {
    data object LoadRecipes : RecipesIntent()
    data class ChangeMealFilter(val mealType: MealType?) : RecipesIntent()
    data class UpdateSearchQuery(val query: String) : RecipesIntent()
    data class ToggleSelection(val recipeId: Int) : RecipesIntent()
    data object SaveSelections : RecipesIntent()
}

sealed interface RecipesEffect {
    data object SaveSuccess : RecipesEffect
    data class ShowError(val message: String) : RecipesEffect
}
