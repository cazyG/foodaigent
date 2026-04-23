package org.xg.project.presentation.manualrecipeinput.usecase

import org.xg.project.data.repository.FoodRepository
import org.xg.project.domain.Result
import org.xg.project.domain.model.RecipeDraft

class CreateRecipeUseCase(
    private val repository: FoodRepository
) {
    suspend operator fun invoke(recipeDraft: RecipeDraft): Result<FoodRepository.CreateRecipeResponse> {
        return repository.createRecipe(recipeDraft)
    }
}
