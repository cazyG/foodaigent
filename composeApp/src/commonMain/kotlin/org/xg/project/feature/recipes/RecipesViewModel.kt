package org.xg.project.feature.recipes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.xg.project.data.repository.FoodRepository
import org.xg.project.domain.Result

class RecipesViewModel(
    private val repository: FoodRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(RecipesState())
    val uiState: StateFlow<RecipesState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<RecipesEffect>()
    val effect: SharedFlow<RecipesEffect> = _effect.asSharedFlow()

    fun onIntent(intent: RecipesIntent) {
        when (intent) {
            is RecipesIntent.LoadRecipes -> loadRecipes()
            is RecipesIntent.ChangeMealFilter -> changeMealFilter(intent)
            is RecipesIntent.UpdateSearchQuery -> updateSearchQuery(intent)
            is RecipesIntent.ToggleSelection -> toggleSelection(intent)
            is RecipesIntent.SaveSelections -> saveSelections()
        }
    }

    private fun loadRecipes() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            when (val result = repository.fetchRecipes()) {
                is Result.Success -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            allRecipes = result.data
                        )
                    }
                }
                is Result.Error -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = result.message
                        )
                    }
                    _effect.emit(RecipesEffect.ShowError(result.message))
                }
            }
        }
    }

    private fun changeMealFilter(intent: RecipesIntent.ChangeMealFilter) {
        _state.update {
            it.copy(
                selectedMealFilter = intent.mealType,
                selectedRecipeIds = emptySet(),
            )
        }
    }

    private fun updateSearchQuery(intent: RecipesIntent.UpdateSearchQuery) {
        _state.update { it.copy(searchQuery = intent.query) }
    }

    private fun toggleSelection(intent: RecipesIntent.ToggleSelection) {
        _state.update { currentState ->
            val currentSelected = currentState.selectedRecipeIds
            val newSelected = if (currentSelected.contains(intent.recipeId)) {
                currentSelected - intent.recipeId
            } else {
                currentSelected + intent.recipeId
            }
            currentState.copy(selectedRecipeIds = newSelected)
        }
    }

    private fun saveSelections() {
        _state.update { it.copy(selectedRecipeIds = emptySet()) }
        viewModelScope.launch {
            _effect.emit(RecipesEffect.SaveSuccess)
        }
    }
}
