package org.xg.project.screen

import androidx.compose.runtime.Composable
import org.xg.project.presentation.login.LoginViewModel
import org.xg.project.presentation.register.RegisterViewModel
import org.xg.project.screen.auth.AuthScreen
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    loginViewModel: LoginViewModel = koinViewModel(),
    registerViewModel: RegisterViewModel = koinViewModel(),
) {
    AuthScreen(
        onAuthSuccess = onLoginSuccess,
        loginViewModel = loginViewModel,
        registerViewModel = registerViewModel,
    )
}
