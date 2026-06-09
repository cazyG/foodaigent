package org.xg.project.presentation.register

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
import org.xg.project.data.repository.UserRepository
import org.xg.project.data.session.UserSessionRepository
import org.xg.project.domain.Result
import org.xg.project.domain.model.RegisterRequest

class RegisterViewModel(
    private val userRepository: UserRepository,
    private val userSessionRepository: UserSessionRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<RegisterUiState>(RegisterUiState.Ready())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    private val _effect = MutableSharedFlow<RegisterEffect>()
    val effect: SharedFlow<RegisterEffect> = _effect.asSharedFlow()

    fun handleIntent(intent: RegisterIntent) {
        when (intent) {
            is RegisterIntent.UpdateUsername -> updateUsername(intent.value)
            is RegisterIntent.UpdateContact -> updateContact(intent.value)
            is RegisterIntent.UpdatePassword -> updatePassword(intent.value)
            is RegisterIntent.UpdateConfirmPassword -> updateConfirmPassword(intent.value)
            is RegisterIntent.UpdateAgreeToTerms -> updateAgreeToTerms(intent.value)
            RegisterIntent.Submit -> submit()
        }
    }

    private fun updateUsername(value: String) {
        _uiState.update { state ->
            when (state) {
                is RegisterUiState.Ready -> state.copy(
                    credentials = state.credentials.copy(username = value),
                    fieldErrors = RegisterFieldErrors(),
                )
                is RegisterUiState.Failed -> RegisterUiState.Ready(
                    credentials = state.credentials.copy(username = value),
                )
                is RegisterUiState.Submitting -> state
            }
        }
    }

    private fun updateContact(value: String) {
        _uiState.update { state ->
            when (state) {
                is RegisterUiState.Ready -> state.copy(
                    credentials = state.credentials.copy(contact = value),
                    fieldErrors = RegisterFieldErrors(),
                )
                is RegisterUiState.Failed -> RegisterUiState.Ready(
                    credentials = state.credentials.copy(contact = value),
                )
                is RegisterUiState.Submitting -> state
            }
        }
    }

    private fun updatePassword(value: String) {
        _uiState.update { state ->
            when (state) {
                is RegisterUiState.Ready -> state.copy(
                    credentials = state.credentials.copy(password = value),
                    fieldErrors = RegisterFieldErrors(),
                )
                is RegisterUiState.Failed -> RegisterUiState.Ready(
                    credentials = state.credentials.copy(password = value),
                )
                is RegisterUiState.Submitting -> state
            }
        }
    }

    private fun updateConfirmPassword(value: String) {
        _uiState.update { state ->
            when (state) {
                is RegisterUiState.Ready -> state.copy(
                    credentials = state.credentials.copy(confirmPassword = value),
                    fieldErrors = RegisterFieldErrors(),
                )
                is RegisterUiState.Failed -> RegisterUiState.Ready(
                    credentials = state.credentials.copy(confirmPassword = value),
                )
                is RegisterUiState.Submitting -> state
            }
        }
    }

    private fun updateAgreeToTerms(value: Boolean) {
        _uiState.update { state ->
            when (state) {
                is RegisterUiState.Ready -> state.copy(
                    credentials = state.credentials.copy(agreeToTerms = value),
                    fieldErrors = RegisterFieldErrors(),
                )
                is RegisterUiState.Failed -> RegisterUiState.Ready(
                    credentials = state.credentials.copy(agreeToTerms = value),
                )
                is RegisterUiState.Submitting -> state
            }
        }
    }

    private fun submit() {
        val credentials = when (val state = _uiState.value) {
            is RegisterUiState.Ready -> state.credentials
            is RegisterUiState.Failed -> state.credentials
            is RegisterUiState.Submitting -> return
        }

        val fieldErrors = validate(credentials)
        if (fieldErrors.hasErrors) {
            _uiState.value = RegisterUiState.Ready(credentials, fieldErrors)
            return
        }

        viewModelScope.launch {
            _uiState.value = RegisterUiState.Submitting(credentials)
            when (
                val result = userRepository.createUser(
                    RegisterRequest(
                        username = credentials.username.trim(),
                        email = resolveEmail(credentials.contact),
                        password = credentials.password,
                    ),
                )
            ) {
                is Result.Success -> {
                    userSessionRepository.setLoggedInUser(result.data)
                    _uiState.value = RegisterUiState.Ready()
                    _effect.emit(RegisterEffect.NavigateHome)
                }
                is Result.Error -> {
                    _uiState.value = RegisterUiState.Failed(
                        credentials = credentials,
                        message = result.message,
                    )
                }
            }
        }
    }

    private fun validate(credentials: RegisterCredentials): RegisterFieldErrors {
        val usernameError = when {
            credentials.username.isBlank() -> "请输入用户名"
            credentials.username.length < 2 -> "用户名至少 2 个字符"
            else -> null
        }
        val contactError = when {
            credentials.contact.isBlank() -> "请输入手机号或邮箱"
            !isValidContact(credentials.contact) -> "请输入有效的手机号或邮箱"
            else -> null
        }
        val passwordError = when {
            credentials.password.isBlank() -> "请设置登录密码"
            credentials.password.length < 6 -> "密码至少 6 位"
            else -> null
        }
        val confirmPasswordError = when {
            credentials.confirmPassword.isBlank() -> "请确认登录密码"
            credentials.confirmPassword != credentials.password -> "两次输入的密码不一致"
            else -> null
        }
        val agreeError = when {
            !credentials.agreeToTerms -> "请先阅读并同意用户协议和隐私政策"
            else -> null
        }
        return RegisterFieldErrors(
            username = usernameError,
            contact = contactError,
            password = passwordError,
            confirmPassword = confirmPasswordError,
            agreeToTerms = agreeError,
        )
    }

    private fun isValidContact(contact: String): Boolean {
        val trimmed = contact.trim()
        if (trimmed.contains("@")) {
            return trimmed.length >= 5 && trimmed.contains(".")
        }
        val digits = trimmed.filter { it.isDigit() }
        return digits.length in 10..11
    }

    private fun resolveEmail(contact: String): String {
        val trimmed = contact.trim()
        return if (trimmed.contains("@")) {
            trimmed
        } else {
            "${trimmed.filter { it.isDigit() }}@foodaigent.local"
        }
    }
}
