package org.xg.project.feature.manualrecipe.usecase

import org.xg.project.data.repository.FoodRepository
import org.xg.project.domain.Result
import org.xg.project.domain.model.CreateRecipeData
import org.xg.project.domain.model.RecipeDraft

class CreateRecipeUseCase(
    private val repository: FoodRepository
) {
    suspend operator fun invoke(recipeDraft: RecipeDraft): Result<CreateRecipeData> {
        return repository.createRecipe(recipeDraft)
    }
}
