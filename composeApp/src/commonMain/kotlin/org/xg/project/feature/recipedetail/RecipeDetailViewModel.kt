package org.xg.project.feature.recipedetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.xg.project.data.repository.FoodRepository
import org.xg.project.domain.Result

class RecipeDetailViewModel(
    private val repository: FoodRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(RecipeDetailState())
    val uiState: StateFlow<RecipeDetailState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<RecipeDetailEffect>()
    val effect: SharedFlow<RecipeDetailEffect> = _effect.asSharedFlow()

    fun onIntent(intent: RecipeDetailIntent) {
        when (intent) {
            is RecipeDetailIntent.LoadRecipe -> loadRecipe(intent.recipeId)
        }
    }

    private fun loadRecipe(recipeId: String) {
        val id = recipeId.toIntOrNull()
        if (id == null) {
            _state.value = RecipeDetailState(error = "无效的食谱 ID")
            return
        }

        viewModelScope.launch {
            _state.value = RecipeDetailState(isLoading = true)
            _state.value = when (val result = repository.fetchRecipes()) {
                is Result.Success -> {
                    val recipe = result.data.firstOrNull { it.id == id }
                    RecipeDetailState(
                        recipe = recipe,
                        error = if (recipe == null) "未找到该食谱" else null,
                    )
                }
                is Result.Error -> RecipeDetailState(error = result.message)
            }
        }
    }
}
