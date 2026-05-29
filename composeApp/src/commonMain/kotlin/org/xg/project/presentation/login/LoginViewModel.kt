package org.xg.project.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _effect = MutableSharedFlow<LoginEffect>()
    val effect: SharedFlow<LoginEffect> = _effect.asSharedFlow()

    fun handleIntent(intent: LoginIntent) {
        when (intent) {
            is LoginIntent.UpdateUsername -> _uiState.update {
                it.copy(
                    formCredentials = it.formCredentials.copy(username = intent.value),
                    fieldErrors = it.fieldErrors.copy(username = null),
                    globalError = null,
                )
            }
            is LoginIntent.UpdatePassword -> _uiState.update {
                it.copy(
                    formCredentials = it.formCredentials.copy(password = intent.value),
                    fieldErrors = it.fieldErrors.copy(password = null),
                    globalError = null,
                )
            }
            LoginIntent.Submit -> submit()
        }
    }

    private fun submit() {
        val credentials = _uiState.value.formCredentials
        val usernameError = credentials.username.trim().takeIf { it.isEmpty() }?.let { "请输入用户名" }
        val passwordError = credentials.password.takeIf { it.isEmpty() }?.let { "请输入密码" }
        if (usernameError != null || passwordError != null) {
            _uiState.update {
                it.copy(
                    fieldErrors = LoginFieldErrors(
                        username = usernameError,
                        password = passwordError,
                    ),
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, globalError = null) }
            delay(400)
            _uiState.update { it.copy(isSubmitting = false) }
            _effect.emit(LoginEffect.NavigateHome)
        }
    }
}
