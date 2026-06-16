package org.xg.project.feature.manualrecipe.usecase

import org.xg.project.domain.Result
import org.xg.project.domain.model.RecipeDraft
import org.xg.project.feature.manualrecipe.ManualRecipeInputState
import org.xg.project.feature.manualrecipe.toRecipeDraft

class BuildRecipeDraftUseCase {
    operator fun invoke(state: ManualRecipeInputState): Result<RecipeDraft> {
        val normalizedSteps = state.steps
            .map { it.trim() }
            .filter { it.isNotEmpty() }

        if (state.recipeName.trim().isEmpty() || state.ingredients.isEmpty() || normalizedSteps.isEmpty()) {
            return Result.Error("食谱名称、原材料和制作过程不能为空")
        }

        val draft = state.copy(steps = normalizedSteps).toRecipeDraft()
        return Result.Success(draft)
    }
}
