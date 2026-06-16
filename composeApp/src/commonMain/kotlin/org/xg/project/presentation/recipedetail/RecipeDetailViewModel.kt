package org.xg.project.presentation.recipedetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.xg.project.data.repository.FoodRepository
import org.xg.project.domain.Result
import org.xg.project.domain.model.RecipeMenu

data class RecipeDetailState(
    val isLoading: Boolean = true,
    val recipe: RecipeMenu? = null,
    val error: String? = null,
)

sealed class RecipeDetailIntent {
    data class LoadRecipe(val recipeId: String) : RecipeDetailIntent()
}

class RecipeDetailViewModel(
    private val repository: FoodRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(RecipeDetailState())
    val state: StateFlow<RecipeDetailState> = _state.asStateFlow()

    fun handleIntent(intent: RecipeDetailIntent) {
        if (intent is RecipeDetailIntent.LoadRecipe) loadRecipe(intent.recipeId)
    }

    private fun loadRecipe(recipeId: String) {
        val id = recipeId.toIntOrNull()
        if (id == null) {
            _state.value = RecipeDetailState(isLoading = false, error = "无效的食谱 ID")
            return
        }

        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            when (val result = repository.fetchRecipes()) {
                is Result.Success -> {
                    val recipe = result.data.firstOrNull { it.id == id }
                    _state.value = RecipeDetailState(
                        isLoading = false,
                        recipe = recipe,
                        error = if (recipe == null) "未找到该食谱" else null,
                    )
                }
                is Result.Error -> {
                    _state.value = RecipeDetailState(isLoading = false, error = result.message)
                }
            }
        }
    }
}
