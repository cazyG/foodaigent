package org.xg.project.feature.greeting

data class GreetingUiState(
    val isLoading: Boolean = false,
    val isGreetingVisible: Boolean = false,
    val greetingMessage: String? = null,
    val errorMessage: String? = null,
)

sealed interface GreetingIntent {
    data object ToggleGreeting : GreetingIntent
    data object ClearError : GreetingIntent
}

sealed interface GreetingEffect {
    data class ShowErrorMessage(val message: String) : GreetingEffect
}
