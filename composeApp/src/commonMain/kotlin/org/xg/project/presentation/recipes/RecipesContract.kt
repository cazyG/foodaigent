package org.xg.project.presentation.recipes

import org.xg.project.domain.model.MealType
import org.xg.project.domain.model.Recipe
import org.xg.project.domain.model.RecipeDraft

data class RecipesState(
    val isLoading: Boolean = false,
    val allRecipes: List<RecipeDraft> = emptyList(),
    val selectedMealType: MealType = MealType.BREAKFAST,
    val selectedRecipeIds: Set<String> = emptySet(),
    val error: String? = null
) {
    val currentRecipes: List<RecipeDraft>
        get() = allRecipes.filter { it.mealType == selectedMealType }
}

sealed class RecipesIntent {
    object LoadRecipes : RecipesIntent()
    data class ChangeMealType(val mealType: MealType) : RecipesIntent()
    data class ToggleSelection(val recipeId: String) : RecipesIntent()
    object SaveSelections : RecipesIntent()
}