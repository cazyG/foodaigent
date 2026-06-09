package org.xg.project.screen.auth

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
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
import org.xg.project.presentation.register.RegisterEffect
import org.xg.project.presentation.register.RegisterIntent
import org.xg.project.presentation.register.RegisterUiState
import org.xg.project.presentation.register.RegisterViewModel
import org.xg.project.presentation.register.fieldErrors
import org.xg.project.presentation.register.formCredentials
import org.xg.project.presentation.register.globalError
import org.xg.project.presentation.register.isSubmitting

private enum class AuthPage { Login, Register }

@Composable
fun AuthScreen(
    onAuthSuccess: () -> Unit,
    loginViewModel: LoginViewModel = koinViewModel(),
    registerViewModel: RegisterViewModel = koinViewModel(),
) {
    var authPage by rememberSaveable { mutableStateOf(AuthPage.Login) }
    val loginUiState by loginViewModel.uiState.collectAsState()
    val registerUiState by registerViewModel.uiState.collectAsState()

    LaunchedEffect(loginViewModel) {
        loginViewModel.effect.collectLatest { effect ->
            when (effect) {
                LoginEffect.NavigateHome -> onAuthSuccess()
            }
        }
    }

    LaunchedEffect(registerViewModel) {
        registerViewModel.effect.collectLatest { effect ->
            when (effect) {
                RegisterEffect.NavigateHome -> onAuthSuccess()
            }
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .imePadding(),
    ) {
        if (maxWidth >= AuthWideBreakpoint) {
            AuthWideAnimatedLayout(
                page = authPage,
                loginUiState = loginUiState,
                registerUiState = registerUiState,
                onLoginIntent = loginViewModel::handleIntent,
                onRegisterIntent = registerViewModel::handleIntent,
                onNavigateToLogin = { authPage = AuthPage.Login },
                onNavigateToRegister = { authPage = AuthPage.Register },
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(AuthColors.PageBackground),
            ) {
                AuthCompactLayout(
                    page = authPage,
                    loginUiState = loginUiState,
                    registerUiState = registerUiState,
                    onLoginIntent = loginViewModel::handleIntent,
                    onRegisterIntent = registerViewModel::handleIntent,
                    onNavigateToLogin = { authPage = AuthPage.Login },
                    onNavigateToRegister = { authPage = AuthPage.Register },
                )
            }
        }
    }
}

@Composable
private fun AuthWideAnimatedLayout(
    page: AuthPage,
    loginUiState: LoginUiState,
    registerUiState: RegisterUiState,
    onLoginIntent: (LoginIntent) -> Unit,
    onRegisterIntent: (RegisterIntent) -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToRegister: () -> Unit,
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(AuthColors.CardWhite),
    ) {
        val imageFraction = AuthImageWeight / (AuthImageWeight + AuthFormWeight)
        val imageWidth = maxWidth * imageFraction
        val formWidth = maxWidth - imageWidth
        val isLogin = page == AuthPage.Login

        val imageOffsetX by animateDpAsState(
            targetValue = if (isLogin) 0.dp else formWidth,
            animationSpec = tween(durationMillis = 550, easing = FastOutSlowInEasing),
            label = "authImageSlide",
        )
        val formOffsetX by animateDpAsState(
            targetValue = if (isLogin) imageWidth else 0.dp,
            animationSpec = tween(durationMillis = 550, easing = FastOutSlowInEasing),
            label = "authFormSlide",
        )

        Box(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .offset(x = formOffsetX)
                        .width(formWidth)
                        .fillMaxHeight(),
                ) {
                    AnimatedContent(
                        targetState = page,
                        transitionSpec = {
                            fadeIn(tween(280)) togetherWith fadeOut(tween(220))
                        },
                        label = "authWideFormContent",
                    ) { currentPage ->
                        when (currentPage) {
                            AuthPage.Login -> LoginWideFormContent(
                                uiState = loginUiState,
                                onIntent = onLoginIntent,
                                onNavigateToRegister = onNavigateToRegister,
                                useCenteredCard = false,
                            )
                            AuthPage.Register -> RegisterWideFormContent(
                                uiState = registerUiState,
                                onIntent = onRegisterIntent,
                                onNavigateToLogin = onNavigateToLogin,
                                useCenteredCard = true,
                            )
                        }
                    }
                }

                AuthBrandingPanel(
                    variant = if (isLogin) AuthBrandingVariant.Login else AuthBrandingVariant.Register,
                    modifier = Modifier
                        .offset(x = imageOffsetX)
                        .width(imageWidth)
                        .fillMaxHeight(),
                )
        }
    }
}

@Composable
private fun AuthCompactLayout(
    page: AuthPage,
    loginUiState: LoginUiState,
    registerUiState: RegisterUiState,
    onLoginIntent: (LoginIntent) -> Unit,
    onRegisterIntent: (RegisterIntent) -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToRegister: () -> Unit,
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .statusBarsPadding()
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (page == AuthPage.Register) {
            AuthCompactRegisterHeader(
                onBack = onNavigateToLogin,
                modifier = Modifier.padding(
                    horizontal = AuthCompactScreenPadding,
                    vertical = 12.dp,
                ),
            )
        } else {
            AuthCompactHeader(
                modifier = Modifier.padding(
                    horizontal = AuthCompactScreenPadding,
                    vertical = 12.dp,
                ),
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        AnimatedContent(
            targetState = page,
            transitionSpec = {
                fadeIn(tween(250)) togetherWith fadeOut(tween(200))
            },
            label = "authCompactForm",
        ) { currentPage ->
            when (currentPage) {
                AuthPage.Login -> LoginCompactFormCard(
                    uiState = loginUiState,
                    onIntent = onLoginIntent,
                    onNavigateToRegister = onNavigateToRegister,
                )
                AuthPage.Register -> RegisterCompactFormCard(
                    uiState = registerUiState,
                    onIntent = onRegisterIntent,
                    onNavigateToLogin = onNavigateToLogin,
                )
            }
        }
        if (page == AuthPage.Login) {
            Spacer(modifier = Modifier.height(24.dp))
            AuthCompactFooter(
                modifier = Modifier.padding(
                    horizontal = AuthCompactScreenPadding,
                    vertical = 16.dp,
                ),
            )
        } else {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun LoginWideFormContent(
    uiState: LoginUiState,
    onIntent: (LoginIntent) -> Unit,
    onNavigateToRegister: () -> Unit,
    useCenteredCard: Boolean,
) {
    val scrollState = rememberScrollState()
    val content: @Composable () -> Unit = {
        LoginFormBody(
            uiState = uiState,
            onIntent = onIntent,
            onNavigateToRegister = onNavigateToRegister,
            variant = AuthFormVariant.Wide,
        )
    }

    if (useCenteredCard) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 40.dp, vertical = 36.dp),
            contentAlignment = Alignment.Center,
        ) {
            AuthFormCard(modifier = Modifier.width(420.dp), content = content)
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 40.dp, vertical = 36.dp),
        ) {
            content()
        }
    }
}

@Composable
private fun RegisterWideFormContent(
    uiState: RegisterUiState,
    onIntent: (RegisterIntent) -> Unit,
    onNavigateToLogin: () -> Unit,
    useCenteredCard: Boolean,
) {
    val scrollState = rememberScrollState()
    val content: @Composable () -> Unit = {
        RegisterFormBody(
            uiState = uiState,
            onIntent = onIntent,
            onNavigateToLogin = onNavigateToLogin,
            variant = AuthFormVariant.Wide,
        )
    }

    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        AuthBackButton(
            onClick = onNavigateToLogin,
            modifier = Modifier.padding(start = 16.dp, top = 16.dp),
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = if (useCenteredCard) 48.dp else 40.dp)
                .padding(bottom = 36.dp),
            contentAlignment = if (useCenteredCard) Alignment.Center else Alignment.TopStart,
        ) {
            if (useCenteredCard) {
                AuthFormCard(modifier = Modifier.width(440.dp), content = content)
            } else {
                Column { content() }
            }
        }
    }
}

@Composable
private fun LoginCompactFormCard(
    uiState: LoginUiState,
    onIntent: (LoginIntent) -> Unit,
    onNavigateToRegister: () -> Unit,
) {
    AuthFormCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = AuthCompactScreenPadding),
    ) {
        LoginFormBody(
            uiState = uiState,
            onIntent = onIntent,
            onNavigateToRegister = onNavigateToRegister,
            variant = AuthFormVariant.Compact,
        )
    }
}

@Composable
private fun RegisterCompactFormCard(
    uiState: RegisterUiState,
    onIntent: (RegisterIntent) -> Unit,
    onNavigateToLogin: () -> Unit,
) {
    AuthFormCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = AuthCompactScreenPadding),
    ) {
        RegisterFormBody(
            uiState = uiState,
            onIntent = onIntent,
            onNavigateToLogin = onNavigateToLogin,
            variant = AuthFormVariant.Compact,
        )
    }
}

@Composable
private fun AuthFormCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = modifier
            .shadow(
                elevation = 16.dp,
                shape = AuthCardShape,
                clip = false,
                ambientColor = AuthCardShadowColor,
                spotColor = AuthCardShadowColor,
            )
            .clip(AuthCardShape)
            .background(AuthColors.CardWhite)
            .border(1.dp, AuthColors.InputBorder.copy(alpha = 0.35f), AuthCardShape)
            .padding(horizontal = 20.dp, vertical = 24.dp),
    ) {
        content()
    }
}

@Composable
private fun LoginFormBody(
    uiState: LoginUiState,
    onIntent: (LoginIntent) -> Unit,
    onNavigateToRegister: () -> Unit,
    variant: AuthFormVariant,
) {
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var rememberMe by rememberSaveable { mutableStateOf(false) }
    val credentials = uiState.formCredentials
    val fieldErrors = uiState.fieldErrors
    val isSubmitting = uiState.isSubmitting
    val globalError = uiState.globalError

    Column(
        horizontalAlignment = if (variant == AuthFormVariant.Wide) {
            Alignment.Start
        } else {
            Alignment.CenterHorizontally
        },
    ) {
        Text(
            text = "欢迎来到黄小厨",
            fontSize = if (variant == AuthFormVariant.Wide) 28.sp else 22.sp,
            fontWeight = FontWeight.Bold,
            color = AuthColors.TextPrimary,
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = if (variant == AuthFormVariant.Wide) {
                "登录您的账号以开始您的烹饪之旅"
            } else {
                "开启您的美味烹饪之旅"
            },
            fontSize = if (variant == AuthFormVariant.Wide) 14.sp else 13.sp,
            color = AuthColors.TextSecondary,
            lineHeight = if (variant == AuthFormVariant.Compact) 18.sp else 20.sp,
        )
        Spacer(modifier = Modifier.height(if (variant == AuthFormVariant.Wide) 28.dp else 22.dp))

        if (globalError != null) {
            Text(
                text = globalError,
                color = Color(0xFFB00020),
                fontSize = 13.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
            )
        }

        AuthTextField(
            value = credentials.username,
            onValueChange = { onIntent(LoginIntent.UpdateUsername(it)) },
            label = "用户名",
            placeholder = if (variant == AuthFormVariant.Wide) "输入手机号或邮箱" else "请输入您的用户名",
            leadingIcon = Icons.Default.Person,
            errorText = fieldErrors.username,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
        )
        Spacer(modifier = Modifier.height(16.dp))

        AuthTextField(
            value = credentials.password,
            onValueChange = { onIntent(LoginIntent.UpdatePassword(it)) },
            label = "密码",
            placeholder = if (variant == AuthFormVariant.Wide) "输入您的登录密码" else "请输入您的密码",
            leadingIcon = Icons.Default.Lock,
            errorText = fieldErrors.password,
            isPassword = true,
            passwordVisible = passwordVisible,
            onTogglePasswordVisibility = { passwordVisible = !passwordVisible },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = { onIntent(LoginIntent.Submit) },
            ),
        )

        if (variant == AuthFormVariant.Wide) {
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = rememberMe,
                        onCheckedChange = { rememberMe = it },
                        colors = CheckboxDefaults.colors(checkedColor = AuthColors.BrandOrange),
                    )
                    Text("记住我", fontSize = 13.sp, color = AuthColors.TextSecondary)
                }
                TextButton(onClick = { }) {
                    Text("忘记密码?", color = AuthColors.Link, fontSize = 13.sp)
                }
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.End,
            ) {
                TextButton(onClick = { }) {
                    Text("忘记密码?", color = AuthColors.Link, fontSize = 13.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        AuthPrimaryButton(
            text = "登录",
            isSubmitting = isSubmitting,
            onClick = { onIntent(LoginIntent.Submit) },
        )
        Spacer(modifier = Modifier.height(16.dp))
        AuthSwitchLink(
            prefix = "还没有账号? ",
            linkText = "立即注册",
            onClick = onNavigateToRegister,
        )
        Spacer(modifier = Modifier.height(24.dp))
        AuthSocialDivider(label = if (variant == AuthFormVariant.Wide) "社交账号登录" else "其他登录方式")
        Spacer(modifier = Modifier.height(20.dp))
        if (variant == AuthFormVariant.Wide) {
            AuthWideSocialButtons()
        } else {
            AuthCompactSocialButtons()
        }
    }
}

@Composable
private fun RegisterFormBody(
    uiState: RegisterUiState,
    onIntent: (RegisterIntent) -> Unit,
    onNavigateToLogin: () -> Unit,
    variant: AuthFormVariant,
) {
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var confirmPasswordVisible by rememberSaveable { mutableStateOf(false) }
    val credentials = uiState.formCredentials
    val fieldErrors = uiState.fieldErrors
    val isSubmitting = uiState.isSubmitting
    val globalError = uiState.globalError

    Column(
        horizontalAlignment = if (variant == AuthFormVariant.Wide) {
            Alignment.Start
        } else {
            Alignment.CenterHorizontally
        },
    ) {
        Text(
            text = "欢迎加入黄小厨",
            fontSize = if (variant == AuthFormVariant.Wide) 28.sp else 22.sp,
            fontWeight = FontWeight.Bold,
            color = AuthColors.TextPrimary,
            textAlign = if (variant == AuthFormVariant.Compact) TextAlign.Center else TextAlign.Start,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "注册您的账号以开始您的烹饪之旅",
            fontSize = if (variant == AuthFormVariant.Wide) 14.sp else 13.sp,
            color = AuthColors.TextSecondary,
            lineHeight = if (variant == AuthFormVariant.Compact) 18.sp else 20.sp,
            textAlign = if (variant == AuthFormVariant.Compact) TextAlign.Center else TextAlign.Start,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(if (variant == AuthFormVariant.Wide) 28.dp else 22.dp))

        if (globalError != null) {
            Text(
                text = globalError,
                color = Color(0xFFB00020),
                fontSize = 13.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
            )
        }

        AuthTextField(
            value = credentials.username,
            onValueChange = { onIntent(RegisterIntent.UpdateUsername(it)) },
            label = "用户名",
            placeholder = "输入您的用户名",
            leadingIcon = Icons.Default.Person,
            errorText = fieldErrors.username,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        )
        Spacer(modifier = Modifier.height(16.dp))

        AuthTextField(
            value = credentials.contact,
            onValueChange = { onIntent(RegisterIntent.UpdateContact(it)) },
            label = "手机号 / 邮箱",
            placeholder = "输入手机号或邮箱",
            leadingIcon = Icons.Default.Email,
            errorText = fieldErrors.contact,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
        )
        Spacer(modifier = Modifier.height(16.dp))

        AuthTextField(
            value = credentials.password,
            onValueChange = { onIntent(RegisterIntent.UpdatePassword(it)) },
            label = "设置登录密码",
            placeholder = "设置登录密码",
            leadingIcon = Icons.Default.Lock,
            errorText = fieldErrors.password,
            isPassword = true,
            passwordVisible = passwordVisible,
            onTogglePasswordVisibility = { passwordVisible = !passwordVisible },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        )
        Spacer(modifier = Modifier.height(16.dp))

        AuthTextField(
            value = credentials.confirmPassword,
            onValueChange = { onIntent(RegisterIntent.UpdateConfirmPassword(it)) },
            label = "确认登录密码",
            placeholder = "确认登录密码",
            leadingIcon = Icons.Default.Lock,
            errorText = fieldErrors.confirmPassword,
            isPassword = true,
            passwordVisible = confirmPasswordVisible,
            onTogglePasswordVisibility = { confirmPasswordVisible = !confirmPasswordVisible },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = { onIntent(RegisterIntent.Submit) },
            ),
        )

        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
        ) {
            Checkbox(
                checked = credentials.agreeToTerms,
                onCheckedChange = { onIntent(RegisterIntent.UpdateAgreeToTerms(it)) },
                colors = CheckboxDefaults.colors(checkedColor = AuthColors.BrandOrange),
            )
            Column(modifier = Modifier.padding(top = 10.dp)) {
                Text(
                    text = "我已阅读并同意用户协议和隐私政策",
                    fontSize = 13.sp,
                    color = AuthColors.TextSecondary,
                    lineHeight = 18.sp,
                )
                fieldErrors.agreeToTerms?.let { error ->
                    Text(
                        text = error,
                        color = Color(0xFFB00020),
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 4.dp),
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        AuthPrimaryButton(
            text = "注册",
            isSubmitting = isSubmitting,
            onClick = { onIntent(RegisterIntent.Submit) },
        )
        Spacer(modifier = Modifier.height(16.dp))
        AuthSwitchLink(
            prefix = "已有账号？",
            linkText = "立即登录",
            onClick = onNavigateToLogin,
        )
        Spacer(modifier = Modifier.height(24.dp))
        AuthSocialDivider(label = "社交账号注册")
        Spacer(modifier = Modifier.height(20.dp))
        AuthRegisterSocialButtons()
    }
}

@Composable
private fun AuthPrimaryButton(
    text: String,
    isSubmitting: Boolean,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        enabled = !isSubmitting,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = AuthPillShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = AuthColors.BrandOrange,
            disabledContainerColor = AuthColors.BrandOrange.copy(alpha = 0.6f),
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
    ) {
        if (isSubmitting) {
            CircularProgressIndicator(
                modifier = Modifier.size(22.dp),
                color = Color.White,
                strokeWidth = 2.dp,
            )
        } else {
            Text(text, fontSize = 17.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
        }
    }
}
