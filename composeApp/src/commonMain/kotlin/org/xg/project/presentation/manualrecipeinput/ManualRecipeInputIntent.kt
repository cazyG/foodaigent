package org.xg.project.presentation.manualrecipeinput

import org.xg.project.domain.model.MealType

sealed class ManualRecipeInputIntent {
    data class UpdateRecipeName(val value: String) : ManualRecipeInputIntent()
    data class UpdateIngredients(val value: String) : ManualRecipeInputIntent()
    data class UpdateSteps(val value: String) : ManualRecipeInputIntent()
    data class UpdateDuration(val value: String) : ManualRecipeInputIntent()
    data class UpdateDifficulty(val value: String) : ManualRecipeInputIntent()
    data class UpdateTag(val value: String) : ManualRecipeInputIntent()
    data class SelectMealType(val mealType: MealType) : ManualRecipeInputIntent()
    data class SelectImage(val imagePath: String?) : ManualRecipeInputIntent()
    object UploadImage : ManualRecipeInputIntent()
    object SaveRecipe : ManualRecipeInputIntent()
    object ResetForm : ManualRecipeInputIntent()
}