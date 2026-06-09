package org.xg.project.presentation.register

data class RegisterCredentials(
    val username: String = "",
    val contact: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val agreeToTerms: Boolean = false,
)

data class RegisterFieldErrors(
    val username: String? = null,
    val contact: String? = null,
    val password: String? = null,
    val confirmPassword: String? = null,
    val agreeToTerms: String? = null,
) {
    val hasErrors: Boolean
        get() = username != null ||
            contact != null ||
            password != null ||
            confirmPassword != null ||
            agreeToTerms != null
}

sealed class RegisterUiState {
    data class Ready(
        val credentials: RegisterCredentials = RegisterCredentials(),
        val fieldErrors: RegisterFieldErrors = RegisterFieldErrors(),
    ) : RegisterUiState()

    data class Submitting(
        val credentials: RegisterCredentials,
    ) : RegisterUiState()

    data class Failed(
        val credentials: RegisterCredentials,
        val message: String,
        val fieldErrors: RegisterFieldErrors = RegisterFieldErrors(),
    ) : RegisterUiState()
}

val RegisterUiState.formCredentials: RegisterCredentials
    get() = when (this) {
        is RegisterUiState.Ready -> credentials
        is RegisterUiState.Submitting -> credentials
        is RegisterUiState.Failed -> credentials
    }

val RegisterUiState.isSubmitting: Boolean
    get() = this is RegisterUiState.Submitting

val RegisterUiState.globalError: String?
    get() = (this as? RegisterUiState.Failed)?.message

val RegisterUiState.fieldErrors: RegisterFieldErrors
    get() = when (this) {
        is RegisterUiState.Ready -> fieldErrors
        is RegisterUiState.Failed -> fieldErrors
        is RegisterUiState.Submitting -> RegisterFieldErrors()
    }

sealed class RegisterIntent {
    data class UpdateUsername(val value: String) : RegisterIntent()
    data class UpdateContact(val value: String) : RegisterIntent()
    data class UpdatePassword(val value: String) : RegisterIntent()
    data class UpdateConfirmPassword(val value: String) : RegisterIntent()
    data class UpdateAgreeToTerms(val value: Boolean) : RegisterIntent()
    data object Submit : RegisterIntent()
}

sealed interface RegisterEffect {
    data object NavigateHome : RegisterEffect
}
