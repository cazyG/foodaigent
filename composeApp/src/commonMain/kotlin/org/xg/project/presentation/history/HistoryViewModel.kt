package org.xg.project.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.xg.project.data.repository.FoodRepository

class HistoryViewModel(
    private val repository: FoodRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(HistoryState())
    val state: StateFlow<HistoryState> = _state.asStateFlow()

    init {
        handleIntent(HistoryIntent.LoadHistory)
    }

    fun handleIntent(intent: HistoryIntent) {
        when (intent) {
            is HistoryIntent.LoadHistory -> loadHistoryData()
        }
    }

    private fun loadHistoryData() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            when (val result = repository.fetchDailyRecords()) {
                is org.xg.project.domain.Result.Success -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        dailyRecords = result.data
                    )
                }
                is org.xg.project.domain.Result.Error -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
            }
        }
    }
}