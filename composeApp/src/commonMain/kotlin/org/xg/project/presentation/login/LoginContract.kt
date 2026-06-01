package org.xg.project.presentation.login

/**
 * 登录表单字段，在 [LoginUiState] 各交互态之间传递。
 */
data class LoginCredentials(
    val username: String = "",
    val password: String = "",
)

/**
 * 字段级校验错误。
 */
data class LoginFieldErrors(
    val username: String? = null,
    val password: String? = null,
) {
    val hasErrors: Boolean
        get() = username != null || password != null
}

/**
 * 登录页 UI 状态。使用 sealed class 保证 [when] 分支穷尽，避免布尔标志组合。
 */
sealed class LoginUiState {

    /** 可编辑表单（含本地校验错误）。 */
    data class Ready(
        val credentials: LoginCredentials = LoginCredentials(),
        val fieldErrors: LoginFieldErrors = LoginFieldErrors(),
    ) : LoginUiState()

    /** 提交中，保留表单内容。 */
    data class Submitting(
        val credentials: LoginCredentials,
    ) : LoginUiState()

    /** 提交失败，保留表单与全局错误信息。 */
    data class Failed(
        val credentials: LoginCredentials,
        val message: String,
        val cause: Throwable? = null,
        val fieldErrors: LoginFieldErrors = LoginFieldErrors(),
    ) : LoginUiState()
}

/** 从任意表单相关态提取凭据，供 UI 统一绑定。 */
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

sealed class LoginIntent {
    data class UpdateUsername(val value: String) : LoginIntent()
    data class UpdatePassword(val value: String) : LoginIntent()
    data object Submit : LoginIntent()
}

sealed interface LoginEffect {
    data object NavigateHome : LoginEffect
}
