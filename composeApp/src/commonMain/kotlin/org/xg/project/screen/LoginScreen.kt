package org.xg.project.screen

import aigent.composeapp.generated.resources.Res
import aigent.composeapp.generated.resources.login_left_bg
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhoneIphone
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import org.jetbrains.compose.resources.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.viewmodel.koinViewModel
import org.xg.project.presentation.login.LoginEffect
import org.xg.project.presentation.login.LoginIntent
import org.xg.project.presentation.login.LoginUiState
import org.xg.project.presentation.login.LoginViewModel

private val LoginWideBreakpoint = 720.dp
private val LoginCardShape = RoundedCornerShape(24.dp)
private val LoginPillShape = RoundedCornerShape(26.dp)
private val LoginFieldMinHeight = 52.dp
private val LoginCardShadowColor = Color.Black.copy(alpha = 0.01f)
private const val LoginImageWeight = 16f
private const val LoginFormWeight = 9f
private const val LoginBrandingHoverScale = 1.06f
private val LoginCompactScreenPadding = 16.dp

private object LoginColors {
    val PageBackground = Color(0xFFF3F4F6)
    val BrandOrange = Color(0xFFF0883A)
    val BrandOrangeDark = Color(0xFFE07A2F)
    val BrandBrown = Color(0xFF8B5E3C)
    val CardWhite = Color.White
    val TextPrimary = Color(0xFF1F2937)
    val TextSecondary = Color(0xFF6B7280)
    val InputBackground = Color(0xFFF3F4F6)
    val InputBorder = Color(0xFFE8D4C4)
    val Link = BrandOrange
}

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

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .imePadding(),
    ) {
        if (maxWidth >= LoginWideBreakpoint) {
            LoginWideLayout(
                uiState = uiState,
                onIntent = viewModel::handleIntent,
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(LoginColors.PageBackground),
            ) {
                LoginCompactLayout(
                    uiState = uiState,
                    onIntent = viewModel::handleIntent,
                )
            }
        }
    }
}

@Composable
private fun LoginWideLayout(
    uiState: LoginUiState,
    onIntent: (LoginIntent) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(LoginColors.CardWhite),
    ) {
        LoginBrandingPanel(modifier = Modifier.weight(LoginImageWeight).fillMaxHeight())
        LoginFormPanel(
            modifier = Modifier.weight(LoginFormWeight),
            uiState = uiState,
            onIntent = onIntent,
            variant = LoginFormVariant.Wide,
        )
    }
}

@Composable
private fun LoginCompactLayout(
    uiState: LoginUiState,
    onIntent: (LoginIntent) -> Unit,
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
        LoginCompactHeader(
            modifier = Modifier.padding(
                horizontal = LoginCompactScreenPadding,
                vertical = 12.dp,
            ),
        )
        Spacer(modifier = Modifier.height(16.dp))
        LoginFormPanel(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = LoginCompactScreenPadding),
            uiState = uiState,
            onIntent = onIntent,
            variant = LoginFormVariant.Compact,
        )
        Spacer(modifier = Modifier.height(24.dp))
        LoginCompactFooter(
            modifier = Modifier.padding(
                horizontal = LoginCompactScreenPadding,
                vertical = 16.dp,
            ),
        )
    }
}

@Composable
private fun LoginCompactHeader(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Default.Restaurant,
            contentDescription = null,
            tint = LoginColors.BrandBrown,
            modifier = Modifier.size(24.dp),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "锅铲黄小厨",
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = LoginColors.BrandBrown,
        )
    }
}

@Composable
private fun LoginCompactFooter(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "锅铲黄小厨",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = LoginColors.TextSecondary,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            listOf("关于我们", "服务条款", "隐私政策", "联系厨师").forEach { label ->
                Text(
                    text = label,
                    fontSize = 12.sp,
                    color = LoginColors.TextSecondary,
                    modifier = Modifier.clickable { },
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "© 2024 锅铲黄小厨. All rights reserved.",
            fontSize = 11.sp,
            color = LoginColors.TextSecondary.copy(alpha = 0.8f),
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun LoginBrandingPanel(modifier: Modifier = Modifier) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    val imageScale by animateFloatAsState(
        targetValue = if (isHovered) LoginBrandingHoverScale else 1f,
        animationSpec = tween(
            durationMillis = 450,
            easing = FastOutSlowInEasing,
        ),
        label = "loginBrandingImageScale",
    )

    Box(
        modifier = modifier
            .fillMaxHeight()
            .clip(RectangleShape)
            .background(LoginColors.BrandOrange)
            .hoverable(interactionSource = interactionSource),
    ) {
        Image(
            painter = painterResource(Res.drawable.login_left_bg),
            contentDescription = "新鲜食材与厨房场景",
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = imageScale
                    scaleY = imageScale
                    transformOrigin = TransformOrigin(0.5f, 1f)
                },
            contentScale = ContentScale.Crop,
            alignment = Alignment.BottomCenter,
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colorStops = arrayOf(
                            0f to Color.Transparent,
                            0.5f to Color.Transparent,
                            0.78f to Color.Black.copy(alpha = 0.22f),
                            1f to Color.Black.copy(alpha = 0.48f),
                        ),
                    ),
                ),
        )
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(horizontal = 28.dp, vertical = 32.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.Restaurant,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(22.dp),
                )
                Text(
                    text = "锅铲黄小厨",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
            Text(
                text = "发现生活的美味，记录厨间的温情。每一道菜都是一份心意。",
                color = Color.White.copy(alpha = 0.92f),
                fontSize = 13.sp,
                lineHeight = 20.sp,
            )
        }
    }
}

private enum class LoginFormVariant { Wide, Compact }

@Composable
private fun LoginFormPanel(
    modifier: Modifier,
    uiState: LoginUiState,
    onIntent: (LoginIntent) -> Unit,
    variant: LoginFormVariant,
) {
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var rememberMe by rememberSaveable { mutableStateOf(false) }

    val scrollState = rememberScrollState()
    val shape = if (variant == LoginFormVariant.Compact) {
        LoginCardShape
    } else {
        RoundedCornerShape(0.dp)
    }
    val horizontalPadding = if (variant == LoginFormVariant.Wide) 40.dp else 20.dp
    val verticalPadding = if (variant == LoginFormVariant.Wide) 36.dp else 24.dp
    val horizontalAlignment = if (variant == LoginFormVariant.Wide) {
        Alignment.Start
    } else {
        Alignment.CenterHorizontally
    }

    val panelModifier = modifier.then(
        if (variant == LoginFormVariant.Compact) {
            Modifier
                .shadow(
                    elevation = 16.dp,
                    shape = shape,
                    clip = false,
                    ambientColor = LoginCardShadowColor,
                    spotColor = LoginCardShadowColor,
                )
                .clip(shape)
                .background(LoginColors.CardWhite)
                .border(1.dp, LoginColors.InputBorder.copy(alpha = 0.35f), shape)
        } else {
            Modifier
                .fillMaxHeight()
                .background(LoginColors.CardWhite)
        },
    )

    if (variant == LoginFormVariant.Wide) {
        Box(modifier = panelModifier) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = horizontalPadding, vertical = verticalPadding),
                horizontalAlignment = horizontalAlignment,
            ) {
                LoginFormFields(
                    uiState = uiState,
                    onIntent = onIntent,
                    variant = variant,
                    passwordVisible = passwordVisible,
                    onPasswordVisibleChange = { passwordVisible = it },
                    rememberMe = rememberMe,
                    onRememberMeChange = { rememberMe = it },
                )
            }
        }
    } else {
        Column(
            modifier = panelModifier.padding(
                horizontal = horizontalPadding,
                vertical = verticalPadding,
            ),
            horizontalAlignment = horizontalAlignment,
        ) {
            LoginFormFields(
                uiState = uiState,
                onIntent = onIntent,
                variant = variant,
                passwordVisible = passwordVisible,
                onPasswordVisibleChange = { passwordVisible = it },
                rememberMe = rememberMe,
                onRememberMeChange = { rememberMe = it },
            )
        }
    }
}

@Composable
private fun LoginFormFields(
    uiState: LoginUiState,
    onIntent: (LoginIntent) -> Unit,
    variant: LoginFormVariant,
    passwordVisible: Boolean,
    onPasswordVisibleChange: (Boolean) -> Unit,
    rememberMe: Boolean,
    onRememberMeChange: (Boolean) -> Unit,
) {
    val credentials = uiState.formCredentials
    val fieldErrors = uiState.fieldErrors
    val isSubmitting = uiState.isSubmitting
    val globalError = uiState.globalError

    Column {
        Text(
            text = "欢迎来到黄小厨",
            fontSize = if (variant == LoginFormVariant.Wide) 28.sp else 22.sp,
            fontWeight = FontWeight.Bold,
            color = LoginColors.TextPrimary,
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = if (variant == LoginFormVariant.Wide) {
                "登录您的账号以开始您的烹饪之旅"
            } else {
                "开启您的美味烹饪之旅"
            },
            fontSize = if (variant == LoginFormVariant.Wide) 14.sp else 13.sp,
            color = LoginColors.TextSecondary,
            lineHeight = if (variant == LoginFormVariant.Compact) 18.sp else 20.sp,
        )
        Spacer(modifier = Modifier.height(if (variant == LoginFormVariant.Wide) 28.dp else 22.dp))

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

        LoginTextField(
            value = credentials.username,
            onValueChange = { onIntent(LoginIntent.UpdateUsername(it)) },
            label = "用户名",
            placeholder = if (variant == LoginFormVariant.Wide) {
                "输入手机号或邮箱"
            } else {
                "请输入您的用户名"
            },
            leadingIcon = Icons.Default.Person,
            errorText = fieldErrors.username,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next,
            ),
            variant = variant,
        )
        Spacer(modifier = Modifier.height(16.dp))

        LoginTextField(
            value = credentials.password,
            onValueChange = { onIntent(LoginIntent.UpdatePassword(it)) },
            label = "密码",
            placeholder = if (variant == LoginFormVariant.Wide) {
                "输入您的登录密码"
            } else {
                "请输入您的密码"
            },
            leadingIcon = Icons.Default.Lock,
            errorText = fieldErrors.password,
            isPassword = true,
            passwordVisible = passwordVisible,
            onTogglePasswordVisibility = { onPasswordVisibleChange(!passwordVisible) },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = { onIntent(LoginIntent.Submit) },
            ),
            variant = variant,
        )

        if (variant == LoginFormVariant.Wide) {
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = rememberMe,
                        onCheckedChange = onRememberMeChange,
                        colors = CheckboxDefaults.colors(
                            checkedColor = LoginColors.BrandOrange,
                        ),
                    )
                    Text("记住我", fontSize = 13.sp, color = LoginColors.TextSecondary)
                }
                TextButton(onClick = { }) {
                    Text("忘记密码?", color = LoginColors.Link, fontSize = 13.sp)
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
                    Text("忘记密码?", color = LoginColors.Link, fontSize = 13.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = { onIntent(LoginIntent.Submit) },
            enabled = !isSubmitting,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = LoginPillShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = LoginColors.BrandOrange,
                disabledContainerColor = LoginColors.BrandOrange.copy(alpha = 0.6f),
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
                Text("登录", fontSize = 17.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            Text("还没有账号? ", fontSize = 13.sp, color = LoginColors.TextSecondary)
            Text(
                text = "立即注册",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = LoginColors.Link,
                modifier = Modifier.clickable { },
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        LoginSocialDivider(
            label = if (variant == LoginFormVariant.Wide) "社交账号登录" else "其他登录方式",
        )
        Spacer(modifier = Modifier.height(20.dp))

        if (variant == LoginFormVariant.Wide) {
            LoginWideSocialButtons()
        } else {
            LoginCompactSocialButtons()
        }
    }
}

@Composable
private fun LoginTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector,
    errorText: String?,
    variant: LoginFormVariant,
    isPassword: Boolean = false,
    passwordVisible: Boolean = false,
    onTogglePasswordVisibility: (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = LoginColors.TextPrimary,
            modifier = Modifier.padding(bottom = 6.dp),
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    text = placeholder,
                    color = LoginColors.TextSecondary.copy(alpha = 0.7f),
                    fontSize = 14.sp,
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    tint = LoginColors.TextSecondary,
                    modifier = Modifier.size(20.dp),
                )
            },
            trailingIcon = if (isPassword && onTogglePasswordVisibility != null) {
                {
                    IconButton(
                        onClick = onTogglePasswordVisibility,
                        modifier = Modifier.size(40.dp),
                    ) {
                        Icon(
                            imageVector = if (passwordVisible) {
                                Icons.Default.VisibilityOff
                            } else {
                                Icons.Default.Visibility
                            },
                            contentDescription = null,
                            tint = LoginColors.TextSecondary,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                }
            } else {
                null
            },
            singleLine = true,
            visualTransformation = if (isPassword && !passwordVisible) {
                PasswordVisualTransformation()
            } else {
                VisualTransformation.None
            },
            isError = errorText != null,
            supportingText = errorText?.let { err ->
                { Text(err, color = Color(0xFFB00020), fontSize = 12.sp) }
            },
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = LoginFieldMinHeight),
            shape = LoginPillShape,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = LoginColors.InputBackground,
                unfocusedContainerColor = LoginColors.InputBackground,
                disabledContainerColor = LoginColors.InputBackground,
                focusedBorderColor = LoginColors.InputBorder,
                unfocusedBorderColor = LoginColors.InputBorder.copy(alpha = 0.6f),
                errorBorderColor = Color(0xFFB00020),
            ),
        )
    }
}

@Composable
private fun LoginSocialDivider(label: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFE5E7EB))
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 12.dp),
            fontSize = 12.sp,
            color = LoginColors.TextSecondary,
        )
        HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFE5E7EB))
    }
}

@Composable
private fun LoginWideSocialButtons() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
    ) {
        LoginSocialIconButton(Icons.Default.Chat)
        LoginSocialIconButton(Icons.Default.Email)
        LoginSocialIconButton(Icons.Default.MoreHoriz)
    }
}

@Composable
private fun LoginCompactSocialButtons() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(32.dp, Alignment.CenterHorizontally),
    ) {
        LoginSocialLabelButton(
            icon = Icons.Default.Chat,
            label = "微信",
            iconTint = Color(0xFF07C160),
        )
        LoginSocialLabelButton(
            icon = Icons.Default.PhoneIphone,
            label = "手机号",
            iconTint = LoginColors.BrandBrown,
        )
    }
}

@Composable
private fun LoginSocialIconButton(icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(LoginColors.InputBackground)
            .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(12.dp))
            .clickable { },
        contentAlignment = Alignment.Center,
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = LoginColors.TextSecondary)
    }
}

@Composable
private fun LoginSocialLabelButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    iconTint: Color,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { },
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(LoginColors.InputBackground)
                .border(1.dp, Color(0xFFE5E7EB), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(imageVector = icon, contentDescription = label, tint = iconTint, modifier = Modifier.size(26.dp))
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(text = label, fontSize = 12.sp, color = LoginColors.TextSecondary)
    }
}
