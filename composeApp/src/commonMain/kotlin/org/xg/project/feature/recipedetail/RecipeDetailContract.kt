package org.xg.project.feature.recipedetail

import org.xg.project.domain.model.RecipeMenu

data class RecipeDetailState(
    val isLoading: Boolean = false,
    val recipe: RecipeMenu? = null,
    val error: String? = null,
)

sealed class RecipeDetailIntent {
    data class LoadRecipe(val recipeId: String) : RecipeDetailIntent()
}

sealed interface RecipeDetailEffect
