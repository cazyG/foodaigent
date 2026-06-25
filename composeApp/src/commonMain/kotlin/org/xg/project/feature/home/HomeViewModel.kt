package org.xg.project.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.xg.project.data.repository.FoodRepository
import org.xg.project.domain.Result
import org.xg.project.domain.model.MealType
import org.xg.project.domain.usecase.CheckMealReviewEligibilityUseCase

class HomeViewModel(
    private val repository: FoodRepository,
    private val checkReviewEligibility: CheckMealReviewEligibilityUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val uiState: StateFlow<HomeState> = _state.asStateFlow()

    init {
        onIntent(HomeIntent.LoadTodayMenu)
    }

    fun onIntent(intent: HomeIntent) {
        when (intent) {
            is HomeIntent.LoadTodayMenu -> loadTodayMenu()
        }
    }

    private fun loadTodayMenu() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            when (val result = repository.fetchDailyRecords()) {
                is Result.Success -> {
                    val records = result.data
                    val todayRecord = records.firstOrNull()

                    // 计算各个餐段的评价资格
                    val canBreakfast = checkReviewEligibility(MealType.BREAKFAST)
                    val canLunch = checkReviewEligibility(MealType.LUNCH)
                    val canDinner = checkReviewEligibility(MealType.DINNER)
                    val canSnack = checkReviewEligibility(MealType.SNACK)

                    _state.value = _state.value.copy(
                        isLoading = false,
                        todayRecord = todayRecord,
                        canReviewBreakfast = canBreakfast,
                        canReviewLunch = canLunch,
                        canReviewDinner = canDinner,
                        canReviewSnack = canSnack
                    )
                }
                is Result.Error -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        todayRecord = null,
                        error = result.message,
                    )
                }
            }
        }
    }
}
