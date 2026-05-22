package org.xg.project.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.viewmodel.koinViewModel
import org.xg.project.presentation.login.LoginEffect
import org.xg.project.presentation.login.LoginIntent
import org.xg.project.presentation.login.LoginUiState
import org.xg.project.presentation.login.LoginViewModel
import org.xg.project.presentation.login.fieldErrors
import org.xg.project.presentation.login.formCredentials
import org.xg.project.presentation.login.globalError
import org.xg.project.presentation.login.isSubmitting

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    viewModel: LoginViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(viewModel) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                LoginEffect.NavigateHome -> onLoginSuccess()
            }
        }
    }

    LoginScreenContent(
        uiState = uiState,
        onUsernameChange = { viewModel.handleIntent(LoginIntent.UpdateUsername(it)) },
        onPasswordChange = { viewModel.handleIntent(LoginIntent.UpdatePassword(it)) },
        onSubmit = { viewModel.handleIntent(LoginIntent.Submit) },
    )
}

@Composable
private fun LoginScreenContent(
    uiState: LoginUiState,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onSubmit: () -> Unit,
) {
    val credentials = uiState.formCredentials
    val fieldErrors = uiState.fieldErrors
    val isSubmitting = uiState.isSubmitting
    val globalError = uiState.globalError

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFFD9CA8F), Color(0xFFD7ECF6)),
                    start = Offset(0f, 0f),
                    end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY),
                ),
            ),
        containerColor = Color.Transparent,
    ) { innerPadding ->
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 400.dp)
                    .fillMaxSize()
                    .padding(innerPadding)
                    .imePadding()
                    .verticalScroll(rememberScrollState())
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Spacer(modifier = Modifier.height(64.dp))

                Text(
                    text = "欢迎来到黄小厨",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = GlassStyle.TextPrimary,
                    modifier = Modifier.padding(bottom = 32.dp),
                )

                globalError?.let { message ->
                    Text(
                        text = message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                    )
                }

                OutlinedTextField(
                    value = credentials.username,
                    onValueChange = onUsernameChange,
                    label = { Text("用户名") },
                    enabled = !isSubmitting,
                    isError = fieldErrors.username != null,
                    supportingText = fieldErrors.username?.let { { Text(it) } },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = loginTextFieldColors(),
                )

                OutlinedTextField(
                    value = credentials.password,
                    onValueChange = onPasswordChange,
                    label = { Text("密码") },
                    visualTransformation = PasswordVisualTransformation(),
                    enabled = !isSubmitting,
                    isError = fieldErrors.password != null,
                    supportingText = fieldErrors.password?.let { { Text(it) } },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = loginTextFieldColors(),
                )

                Button(
                    onClick = onSubmit,
                    enabled = !isSubmitting,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(25.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFF59E42),
                    ),
                ) {
                    if (isSubmitting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 2.dp,
                        )
                    } else {
                        Text("登录", fontSize = 18.sp, color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(64.dp))
            }
        }
    }
}

@Composable
private fun loginTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = Color.White.copy(alpha = 0.3f),
    unfocusedContainerColor = Color.White.copy(alpha = 0.1f),
)
