package org.xg.project.presentation.recipes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.xg.project.data.repository.FoodRepository
import org.xg.project.domain.Result

class RecipesViewModel(
    private val repository: FoodRepository = FoodRepository()
) : ViewModel() {

    private val _state = MutableStateFlow(RecipesState())
    val state: StateFlow<RecipesState> = _state.asStateFlow()

    init {
        handleIntent(RecipesIntent.LoadRecipes)
    }

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
            _state.value = _state.value.copy(isLoading = true, error = null)
            when (val result = repository.getAllRecipe()) {
                is Result.Success -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        allRecipes = result.data
                    )
                }
                is Result.Error -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
            }
        }
    }

    private fun changeMealType(intent: RecipesIntent.ChangeMealType) {
        _state.value = _state.value.copy(
            selectedMealType = intent.mealType,
            selectedRecipeIds = emptySet()
        )
    }

    private fun toggleSelection(intent: RecipesIntent.ToggleSelection) {
        val currentSelected = _state.value.selectedRecipeIds
        val newSelected = if (currentSelected.contains(intent.recipeId)) {
            currentSelected - intent.recipeId
        } else {
            currentSelected + intent.recipeId
        }
        _state.value = _state.value.copy(selectedRecipeIds = newSelected)
    }

    private fun saveSelections() {
        val selectedRecipes = _state.value.allRecipes.filter { it.name in _state.value.selectedRecipeIds }
        println("保存选中食谱: ${selectedRecipes.joinToString { it.name }}")
        _state.value = _state.value.copy(selectedRecipeIds = emptySet())
    }
}