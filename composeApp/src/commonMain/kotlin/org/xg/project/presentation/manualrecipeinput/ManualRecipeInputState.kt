package org.xg.project.presentation.manualrecipeinput

import org.xg.project.domain.model.MealType

data class ManualRecipeInputState(
    val recipeName: String = "",
    val ingredients: String = "",
    val steps: String = "",
    val duration: String = "",
    val difficulty: String = "",
    val tag: String = "",
    val selectedMealType: MealType = MealType.LUNCH,
    val selectedImagePath: String? = null,
    val uploadedImageUrl: String? = null,
    val isUploading: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null,
    val saveSuccess: Boolean = false
)