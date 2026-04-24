package org.xg.project.presentation.manualrecipeinput

import org.xg.project.domain.model.Ingredient
import org.xg.project.domain.model.MealType
import org.xg.project.domain.model.RecipeDraft
import org.xg.project.domain.model.RecipeDraftIngredient

fun ManualRecipeInputState.toRecipeDraft(): RecipeDraft {
    val safeMealType = MealType.entries.firstOrNull { it.name == selectedMealType } ?: MealType.LUNCH
    return RecipeDraft(
        name = recipeName.trim(),
        ingredients = ingredients.map {
            RecipeDraftIngredient(
                name = it.name.trim(),
                number = it.quantity.trim()
            )
        },
        steps = steps.map { it.trim() }.filter { it.isNotEmpty() },
        duration = "${durationMinutes}分钟",
        difficulty = "${difficultyStars}星",
        tag = tag.trim(),
        mealType = safeMealType.name,
        imageUrl = uploadedImageUrl,
        submitter = "admin"
    )
}
