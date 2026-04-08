package org.xg.project.presentation.index

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.xg.project.data.repository.FoodRepository
import org.xg.project.domain.model.MealType
import org.xg.project.domain.usecase.CheckMealReviewEligibilityUseCase

class IndexViewModel(
    private val repository: FoodRepository = FoodRepository(),
    private val checkReviewEligibility: CheckMealReviewEligibilityUseCase = CheckMealReviewEligibilityUseCase()
) : ViewModel() {

    private val _state = MutableStateFlow(IndexState())
    val state: StateFlow<IndexState> = _state.asStateFlow()

    init {
        handleIntent(IndexIntent.LoadTodayMenu)
    }

    fun handleIntent(intent: IndexIntent) {
        when (intent) {
            is IndexIntent.LoadTodayMenu -> loadTodayMenu()
        }
    }

    private fun loadTodayMenu() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                // 模拟网络获取今天的菜单
                val records = repository.fetchDailyRecords()
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
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Unknown error"
                )
            }
        }
    }
}