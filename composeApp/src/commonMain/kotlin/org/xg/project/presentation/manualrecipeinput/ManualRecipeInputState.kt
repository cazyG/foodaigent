package org.xg.project.presentation.manualrecipeinput

import org.xg.project.domain.model.Ingredient
import org.xg.project.domain.model.MealType

data class ManualRecipeInputState(
    val recipeName: String = "",
    val ingredients: List<Ingredient> = emptyList(),
    val steps: String = "",
    val duration: String = "",
    val difficulty: String = "",
    val tag: String = "",
    val selectedMealType: String = MealType.LUNCH.name,
    val uploadedImageUrl: String? = null,
    val isUploading: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null,
    val saveSuccess: Boolean = false
)