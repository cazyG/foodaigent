package org.xg.project.feature.greeting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.xg.project.Greeting

class GreetingViewModel(
    private val greetingProvider: Greeting,
) : ViewModel() {
    private val _uiState = MutableStateFlow(GreetingUiState())
    val uiState: StateFlow<GreetingUiState> = _uiState.asStateFlow()

    private val _effect = MutableSharedFlow<GreetingEffect>()
    val effect: SharedFlow<GreetingEffect> = _effect.asSharedFlow()

    fun onIntent(intent: GreetingIntent) {
        when (intent) {
            GreetingIntent.ToggleGreeting -> toggleGreeting()
            GreetingIntent.ClearError -> clearError()
        }
    }

    private fun toggleGreeting() {
        val currentState = _uiState.value
        if (currentState.isLoading) return

        if (currentState.isGreetingVisible) {
            _uiState.update { it.copy(isGreetingVisible = false) }
            return
        }

        val cachedMessage = currentState.greetingMessage
        if (cachedMessage != null) {
            _uiState.update {
                it.copy(
                    isGreetingVisible = true,
                    errorMessage = null,
                )
            }
            return
        }

        loadGreeting()
    }

    private fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    private fun loadGreeting() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            runCatching { greetingProvider.greet() }
                .onSuccess { greeting ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isGreetingVisible = true,
                            greetingMessage = greeting,
                        )
                    }
                }
                .onFailure { throwable ->
                    val message = throwable.message ?: "Failed to load greeting."
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isGreetingVisible = false,
                            errorMessage = message,
                        )
                    }
                    _effect.emit(GreetingEffect.ShowErrorMessage(message))
                }
        }
    }
}
