package org.xg.project.presentation.recipes

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
import org.xg.project.data.model.ResponseResult
import org.xg.project.data.repository.FoodRepository

class RecipesViewModel(
    private val repository: FoodRepository = FoodRepository()
) : ViewModel() {

    private val _state = MutableStateFlow(RecipesState())
    val state: StateFlow<RecipesState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<RecipesEffect>()
    val effect: SharedFlow<RecipesEffect> = _effect.asSharedFlow()

    fun handleIntent(intent: RecipesIntent) {
        when (intent) {
            is RecipesIntent.LoadRecipes -> loadRecipes()
            is RecipesIntent.ChangeMealType -> changeMealType(intent)
            is RecipesIntent.ToggleSelection -> toggleSelection(intent)
            is RecipesIntent.SaveSelections -> saveSelections()
        }
    }

    private fun loadRecipes() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            when (val result = repository.getAllRecipe()) {
                is ResponseResult.Success -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            allRecipes = result.data
                        )
                    }
                }
                is ResponseResult.Error -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = result.message
                        )
                    }
                    _effect.emit(RecipesEffect.ShowError(result.message))
                }
                is ResponseResult.Loading -> {
                    _state.update { it.copy(isLoading = true) }
                }
            }
        }
    }

    private fun changeMealType(intent: RecipesIntent.ChangeMealType) {
        _state.update {
            it.copy(
                selectedMealType = intent.mealType,
                selectedRecipeIds = emptySet()
            )
        }
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
        val selectedRecipes = _state.value.allRecipes.filter { it.id in _state.value.selectedRecipeIds }
        println("保存选中食谱: ${selectedRecipes.joinToString { it.name }}")
        _state.update { it.copy(selectedRecipeIds = emptySet()) }
        viewModelScope.launch {
            _effect.emit(RecipesEffect.SaveSuccess)
        }
    }
}