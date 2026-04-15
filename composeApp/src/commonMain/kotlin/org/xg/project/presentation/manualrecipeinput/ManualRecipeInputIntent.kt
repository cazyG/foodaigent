package org.xg.project.presentation.manualrecipeinput

import org.xg.project.domain.model.Ingredient
import org.xg.project.domain.model.MealType

sealed class ManualRecipeInputIntent {
    data class UpdateRecipeName(val value: String) : ManualRecipeInputIntent()
    data class AddIngredient(val ingredient: Ingredient) : ManualRecipeInputIntent()
    data class UpdateIngredient(val ingredient: Ingredient) : ManualRecipeInputIntent()
    data class RemoveIngredient(val ingredient: Ingredient) : ManualRecipeInputIntent()
    data class UpdateSteps(val value: String) : ManualRecipeInputIntent()
    data class UpdateDuration(val value: String) : ManualRecipeInputIntent()
    data class UpdateDifficulty(val value: String) : ManualRecipeInputIntent()
    data class UpdateTag(val value: String) : ManualRecipeInputIntent()
    data class SelectMealType(val mealType: String) : ManualRecipeInputIntent()
    data class SelectImage(val uri: String?) : ManualRecipeInputIntent()
    data class UploadImage(val imageBytes: ByteArray?) : ManualRecipeInputIntent()
    object SaveRecipe : ManualRecipeInputIntent()
    object ResetForm : ManualRecipeInputIntent()
}