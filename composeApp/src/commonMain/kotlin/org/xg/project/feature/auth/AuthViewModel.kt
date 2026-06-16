package org.xg.project.feature.auth

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
import org.xg.project.domain.model.User

class AuthViewModel(
    private val userSessionRepository: UserSessionRepository,
    private val userRepository: UserRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(AuthState())
    val uiState: StateFlow<AuthState> = _uiState.asStateFlow()

    private val _effect = MutableSharedFlow<AuthEffect>()
    val effect: SharedFlow<AuthEffect> = _effect.asSharedFlow()

    fun onIntent(intent: AuthIntent) {
        when (intent) {
            AuthIntent.ShowLogin -> _uiState.update { it.copy(page = AuthPage.Login) }
            AuthIntent.ShowRegister -> _uiState.update { it.copy(page = AuthPage.Register) }
            is AuthIntent.UpdateLoginUsername -> updateLoginUsername(intent.value)
            is AuthIntent.UpdateLoginPassword -> updateLoginPassword(intent.value)
            AuthIntent.SubmitLogin -> submitLogin()
            is AuthIntent.UpdateRegisterUsername -> updateRegisterUsername(intent.value)
            is AuthIntent.UpdateRegisterContact -> updateRegisterContact(intent.value)
            is AuthIntent.UpdateRegisterPassword -> updateRegisterPassword(intent.value)
            is AuthIntent.UpdateRegisterConfirmPassword -> updateRegisterConfirmPassword(intent.value)
            is AuthIntent.UpdateRegisterAgreeToTerms -> updateRegisterAgreeToTerms(intent.value)
            AuthIntent.SubmitRegister -> submitRegister()
        }
    }

    private fun updateLoginUsername(value: String) {
        _uiState.update { authState ->
            authState.copy(
                loginUiState = when (val state = authState.loginUiState) {
                    is LoginUiState.Ready -> state.copy(
                        credentials = state.credentials.copy(username = value),
                        fieldErrors = LoginFieldErrors(),
                    )
                    is LoginUiState.Failed -> LoginUiState.Ready(
                        credentials = state.credentials.copy(username = value),
                    )
                    is LoginUiState.Submitting -> state
                },
            )
        }
    }

    private fun updateLoginPassword(value: String) {
        _uiState.update { authState ->
            authState.copy(
                loginUiState = when (val state = authState.loginUiState) {
                    is LoginUiState.Ready -> state.copy(
                        credentials = state.credentials.copy(password = value),
                        fieldErrors = LoginFieldErrors(),
                    )
                    is LoginUiState.Failed -> LoginUiState.Ready(
                        credentials = state.credentials.copy(password = value),
                    )
                    is LoginUiState.Submitting -> state
                },
            )
        }
    }

    private fun submitLogin() {
        val credentials = when (val state = _uiState.value.loginUiState) {
            is LoginUiState.Ready -> state.credentials
            is LoginUiState.Failed -> state.credentials
            is LoginUiState.Submitting -> return
        }
        val fieldErrors = validateLogin(credentials)
        if (fieldErrors.hasErrors) {
            _uiState.update {
                it.copy(loginUiState = LoginUiState.Ready(credentials, fieldErrors))
            }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(loginUiState = LoginUiState.Submitting(credentials)) }
            try {
                val user = performLogin(credentials)
                userSessionRepository.setLoggedInUser(user)
                _uiState.value = AuthState()
                _effect.emit(AuthEffect.NavigateHome)
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        loginUiState = LoginUiState.Failed(
                            credentials = credentials,
                            message = e.message ?: "登录失败，请稍后重试",
                            cause = e,
                        ),
                    )
                }
            }
        }
    }

    private fun updateRegisterUsername(value: String) {
        updateRegisterState { state ->
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

    private fun updateRegisterContact(value: String) {
        updateRegisterState { state ->
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

    private fun updateRegisterPassword(value: String) {
        updateRegisterState { state ->
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

    private fun updateRegisterConfirmPassword(value: String) {
        updateRegisterState { state ->
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

    private fun updateRegisterAgreeToTerms(value: Boolean) {
        updateRegisterState { state ->
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

    private fun submitRegister() {
        val credentials = when (val state = _uiState.value.registerUiState) {
            is RegisterUiState.Ready -> state.credentials
            is RegisterUiState.Failed -> state.credentials
            is RegisterUiState.Submitting -> return
        }
        val fieldErrors = validateRegister(credentials)
        if (fieldErrors.hasErrors) {
            _uiState.update {
                it.copy(registerUiState = RegisterUiState.Ready(credentials, fieldErrors))
            }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(registerUiState = RegisterUiState.Submitting(credentials)) }
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
                    _uiState.value = AuthState()
                    _effect.emit(AuthEffect.NavigateHome)
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            registerUiState = RegisterUiState.Failed(
                                credentials = credentials,
                                message = result.message,
                            ),
                        )
                    }
                }
            }
        }
    }

    private fun updateRegisterState(reducer: (RegisterUiState) -> RegisterUiState) {
        _uiState.update { it.copy(registerUiState = reducer(it.registerUiState)) }
    }

    private fun validateLogin(credentials: LoginCredentials): LoginFieldErrors {
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

    private suspend fun performLogin(credentials: LoginCredentials): User {
        return when (val result = userRepository.authenticate(credentials.username, credentials.password)) {
            is Result.Success -> result.data
            is Result.Error -> throw IllegalStateException(result.message)
        }
    }

    private fun validateRegister(credentials: RegisterCredentials): RegisterFieldErrors {
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
