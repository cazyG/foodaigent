package org.xg.project.feature.auth

data class LoginCredentials(
    val username: String = "",
    val password: String = "",
)

data class LoginFieldErrors(
    val username: String? = null,
    val password: String? = null,
) {
    val hasErrors: Boolean
        get() = username != null || password != null
}

sealed class LoginUiState {
    data class Ready(
        val credentials: LoginCredentials = LoginCredentials(),
        val fieldErrors: LoginFieldErrors = LoginFieldErrors(),
    ) : LoginUiState()

    data class Submitting(
        val credentials: LoginCredentials,
    ) : LoginUiState()

    data class Failed(
        val credentials: LoginCredentials,
        val message: String,
        val cause: Throwable? = null,
        val fieldErrors: LoginFieldErrors = LoginFieldErrors(),
    ) : LoginUiState()
}

val LoginUiState.formCredentials: LoginCredentials
    get() = when (this) {
        is LoginUiState.Ready -> credentials
        is LoginUiState.Submitting -> credentials
        is LoginUiState.Failed -> credentials
    }

val LoginUiState.isSubmitting: Boolean
    get() = this is LoginUiState.Submitting

val LoginUiState.globalError: String?
    get() = (this as? LoginUiState.Failed)?.message

val LoginUiState.fieldErrors: LoginFieldErrors
    get() = when (this) {
        is LoginUiState.Ready -> fieldErrors
        is LoginUiState.Failed -> fieldErrors
        is LoginUiState.Submitting -> LoginFieldErrors()
    }

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

enum class AuthPage { Login, Register }

data class AuthState(
    val page: AuthPage = AuthPage.Login,
    val loginUiState: LoginUiState = LoginUiState.Ready(),
    val registerUiState: RegisterUiState = RegisterUiState.Ready(),
)

sealed class AuthIntent {
    data object ShowLogin : AuthIntent()
    data object ShowRegister : AuthIntent()
    data class UpdateLoginUsername(val value: String) : AuthIntent()
    data class UpdateLoginPassword(val value: String) : AuthIntent()
    data object SubmitLogin : AuthIntent()
    data class UpdateRegisterUsername(val value: String) : AuthIntent()
    data class UpdateRegisterContact(val value: String) : AuthIntent()
    data class UpdateRegisterPassword(val value: String) : AuthIntent()
    data class UpdateRegisterConfirmPassword(val value: String) : AuthIntent()
    data class UpdateRegisterAgreeToTerms(val value: Boolean) : AuthIntent()
    data object SubmitRegister : AuthIntent()
}

sealed interface AuthEffect {
    data object NavigateHome : AuthEffect
}
