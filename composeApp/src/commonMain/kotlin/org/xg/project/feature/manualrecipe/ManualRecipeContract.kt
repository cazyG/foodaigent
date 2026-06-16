package org.xg.project.feature.manualrecipe

import org.xg.project.domain.model.Ingredient
import org.xg.project.domain.model.MealType
import org.xg.project.data.session.UserAccount

data class ManualRecipeInputState(
    val recipeName: String = "",
    val ingredients: List<Ingredient> = emptyList(),
    val steps: List<String> = emptyList(),
    val durationMinutes: Int = 15,
    val difficultyStars: Int = 3,
    val tag: String = "",
    val selectedMealType: String = MealType.LUNCH.name,
    val uploadedImageUrl: String? = null,
    val isUploading: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null,
    val userAccount: UserAccount? = null,
)

sealed class ManualRecipeInputIntent {
    data class UpdateRecipeName(val value: String) : ManualRecipeInputIntent()
    data class AddIngredient(val ingredient: Ingredient) : ManualRecipeInputIntent()
    data class UpdateIngredient(val ingredient: Ingredient) : ManualRecipeInputIntent()
    data class RemoveIngredient(val ingredient: Ingredient) : ManualRecipeInputIntent()
    data object AddStep : ManualRecipeInputIntent()
    data class UpdateStep(val index: Int, val value: String) : ManualRecipeInputIntent()
    data class RemoveStep(val index: Int) : ManualRecipeInputIntent()
    data class UpdateDuration(val minutes: Int) : ManualRecipeInputIntent()
    data class UpdateDifficulty(val stars: Int) : ManualRecipeInputIntent()
    data class UpdateTag(val value: String) : ManualRecipeInputIntent()
    data class SelectMealType(val mealType: String) : ManualRecipeInputIntent()
    data class UploadImage(val fileName: String, val imageBytes: ByteArray) : ManualRecipeInputIntent()
    data object SaveRecipe : ManualRecipeInputIntent()
    data object ResetForm : ManualRecipeInputIntent()
}

sealed interface ManualRecipeInputEffect {
    data object SaveSuccess : ManualRecipeInputEffect
}
