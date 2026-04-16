package org.xg.project.domain.model

data class RecipeDraftIngredient(
    val name: String,
    val number: String
)

data class RecipeDraft(
    val name: String,
    val ingredients: List<RecipeDraftIngredient>,
    val steps: List<String>,
    val duration: String,
    val difficulty: String,
    val tag: String,
    val mealType: MealType,
    val imageUrl: String?
)
