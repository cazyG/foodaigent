package org.xg.project.presentation.login

data class LoginFormCredentials(
    val username: String = "",
    val password: String = "",
)

data class LoginFieldErrors(
    val username: String? = null,
    val password: String? = null,
)

data class LoginUiState(
    val formCredentials: LoginFormCredentials = LoginFormCredentials(),
    val fieldErrors: LoginFieldErrors = LoginFieldErrors(),
    val isSubmitting: Boolean = false,
    val globalError: String? = null,
)

sealed class LoginIntent {
    data class UpdateUsername(val value: String) : LoginIntent()
    data class UpdatePassword(val value: String) : LoginIntent()
    data object Submit : LoginIntent()
}

sealed interface LoginEffect {
    data object NavigateHome : LoginEffect
}
