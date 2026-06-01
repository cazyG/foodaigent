package org.xg.project.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import org.xg.project.data.session.UserSessionRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val userSessionRepository: UserSessionRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Ready())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _effect = MutableSharedFlow<LoginEffect>()
    val effect: SharedFlow<LoginEffect> = _effect.asSharedFlow()

    fun handleIntent(intent: LoginIntent) {
        when (intent) {
            is LoginIntent.UpdateUsername -> updateUsername(intent.value)
            is LoginIntent.UpdatePassword -> updatePassword(intent.value)
            is LoginIntent.Submit -> submit()
        }
    }

    private fun updateUsername(value: String) {
        _uiState.update { state ->
            when (state) {
                is LoginUiState.Ready -> state.copy(
                    credentials = state.credentials.copy(username = value),
                    fieldErrors = LoginFieldErrors(),
                )
                is LoginUiState.Failed -> LoginUiState.Ready(
                    credentials = state.credentials.copy(username = value),
                )
                is LoginUiState.Submitting -> state
            }
        }
    }

    private fun updatePassword(value: String) {
        _uiState.update { state ->
            when (state) {
                is LoginUiState.Ready -> state.copy(
                    credentials = state.credentials.copy(password = value),
                    fieldErrors = LoginFieldErrors(),
                )
                is LoginUiState.Failed -> LoginUiState.Ready(
                    credentials = state.credentials.copy(password = value),
                )
                is LoginUiState.Submitting -> state
            }
        }
    }

    private fun submit() {
        val credentials = when (val state = _uiState.value) {
            is LoginUiState.Ready -> state.credentials
            is LoginUiState.Failed -> state.credentials
            is LoginUiState.Submitting -> return
        }

        val fieldErrors = validate(credentials)
        if (fieldErrors.hasErrors) {
            _uiState.value = LoginUiState.Ready(credentials, fieldErrors)
            return
        }

        viewModelScope.launch {
            _uiState.value = LoginUiState.Submitting(credentials)
            try {
                performLogin(credentials)
                userSessionRepository.setLoggedInUser(credentials.username)
                _uiState.value = LoginUiState.Ready()
                _effect.emit(LoginEffect.NavigateHome)
            } catch (e: Exception) {
                _uiState.value = LoginUiState.Failed(
                    credentials = credentials,
                    message = e.message ?: "登录失败，请稍后重试",
                    cause = e,
                )
            }
        }
    }

    private fun validate(credentials: LoginCredentials): LoginFieldErrors {
        val usernameError = when {
            credentials.username.isBlank() -> "请输入用户名"
            else -> null
        }
        val passwordError = when {
            credentials.password.isBlank() -> "请输入密码"
            credentials.password.length < 6 -> "密码至少 6 位"
            else -> null
        }
        return LoginFieldErrors(username = usernameError, password = passwordError)
    }

    private suspend fun performLogin(credentials: LoginCredentials) {
        delay(400)
        // TODO: 接入真实认证 API
        if (credentials.username == "error") {
            throw IllegalStateException("用户名或密码错误")
        }
    }
}
