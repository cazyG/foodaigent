package org.xg.project.screen.auth

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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhoneIphone
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource

internal val AuthWideBreakpoint = 720.dp
internal val AuthCardShape = RoundedCornerShape(24.dp)
internal val AuthPillShape = RoundedCornerShape(26.dp)
internal val AuthFieldMinHeight = 52.dp
internal val AuthCardShadowColor = Color.Black.copy(alpha = 0.01f)
internal const val AuthImageWeight = 16f
internal const val AuthFormWeight = 9f
private const val AuthBrandingHoverScale = 1.06f
internal val AuthCompactScreenPadding = 16.dp

internal object AuthColors {
    val PageBackground = Color(0xFFF3F4F6)
    val BrandOrange = Color(0xFFF0883A)
    val BrandBrown = Color(0xFF8B5E3C)
    val CardWhite = Color.White
    val TextPrimary = Color(0xFF1F2937)
    val TextSecondary = Color(0xFF6B7280)
    val InputBackground = Color(0xFFF3F4F6)
    val InputBorder = Color(0xFFE8D4C4)
    val Link = BrandOrange
    val FooterBackground = Color(0xFF2C2C2C)
    val FooterText = Color(0xFF9CA3AF)
}

internal enum class AuthFormVariant { Wide, Compact }

internal enum class AuthBrandingVariant { Login, Register }

@Composable
internal fun AuthBrandingPanel(
    variant: AuthBrandingVariant,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    val imageScale by animateFloatAsState(
        targetValue = if (isHovered) AuthBrandingHoverScale else 1f,
        animationSpec = tween(durationMillis = 450, easing = FastOutSlowInEasing),
        label = "authBrandingImageScale",
    )

    Box(
        modifier = modifier
            .fillMaxHeight()
            .clip(RectangleShape)
            .background(AuthColors.BrandOrange)
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
                            0f to Color.Black.copy(alpha = if (variant == AuthBrandingVariant.Register) 0.18f else 0f),
                            0.35f to Color.Transparent,
                            0.5f to Color.Transparent,
                            0.78f to Color.Black.copy(alpha = 0.22f),
                            1f to Color.Black.copy(alpha = 0.48f),
                        ),
                    ),
                ),
        )
        if (variant == AuthBrandingVariant.Register) {
            Row(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(horizontal = 28.dp, vertical = 28.dp),
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
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(horizontal = 28.dp, vertical = 32.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            if (variant == AuthBrandingVariant.Login) {
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
            }
            Text(
                text = if (variant == AuthBrandingVariant.Register) {
                    "开启您的美味食光"
                } else {
                    "发现生活的美味，记录厨间的温情。每一道菜都是一份心意。"
                },
                color = Color.White.copy(alpha = if (variant == AuthBrandingVariant.Register) 1f else 0.92f),
                fontSize = if (variant == AuthBrandingVariant.Register) 26.sp else 13.sp,
                fontWeight = if (variant == AuthBrandingVariant.Register) FontWeight.Bold else FontWeight.Normal,
                lineHeight = if (variant == AuthBrandingVariant.Register) 34.sp else 20.sp,
            )
            if (variant == AuthBrandingVariant.Register) {
                Text(
                    text = "在这里，每一个锅铲的翻动都是对生活的热爱。与千万同好一起，在充满温情的厨房中探索无限可能。",
                    color = Color.White.copy(alpha = 0.92f),
                    fontSize = 13.sp,
                    lineHeight = 20.sp,
                )
            }
        }
    }
}

@Composable
internal fun AuthCompactHeader(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Default.Restaurant,
            contentDescription = null,
            tint = AuthColors.BrandBrown,
            modifier = Modifier.size(24.dp),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "锅铲黄小厨",
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = AuthColors.BrandBrown,
        )
    }
}

@Composable
internal fun AuthCompactFooter(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "锅铲黄小厨",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = AuthColors.TextSecondary,
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
                    color = AuthColors.TextSecondary,
                    modifier = Modifier.clickable { },
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "© 2024 锅铲黄小厨. All rights reserved.",
            fontSize = 11.sp,
            color = AuthColors.TextSecondary.copy(alpha = 0.8f),
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
internal fun AuthRegisterWideFooter(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(AuthColors.FooterBackground)
            .padding(horizontal = 40.dp, vertical = 28.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(modifier = Modifier.weight(1.2f)) {
            Text(
                text = "锅铲黄小厨",
                color = AuthColors.BrandOrange,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "让每一餐都充满温度，在锅铲之间感受烹饪的艺术。",
                color = AuthColors.FooterText,
                fontSize = 12.sp,
                lineHeight = 18.sp,
            )
        }
        AuthFooterLinkColumn(
            title = "关于我们",
            links = listOf("品牌故事", "加入我们", "商业合作"),
            modifier = Modifier.weight(0.8f),
        )
        AuthFooterLinkColumn(
            title = "帮助中心",
            links = listOf("常见问题", "用户协议", "隐私政策"),
            modifier = Modifier.weight(0.8f),
        )
        Column(modifier = Modifier.weight(0.8f)) {
            Text(
                text = "联系我们",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                AuthFooterIcon(Icons.Default.Email)
                AuthFooterIcon(Icons.Default.Share)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "© 2024 Culinary Warmth. 版权所有",
                color = AuthColors.FooterText,
                fontSize = 11.sp,
            )
        }
    }
}

@Composable
private fun AuthFooterLinkColumn(
    title: String,
    links: List<String>,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = title,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(modifier = Modifier.height(12.dp))
        links.forEach { link ->
            Text(
                text = link,
                color = AuthColors.FooterText,
                fontSize = 12.sp,
                modifier = Modifier
                    .padding(vertical = 4.dp)
                    .clickable { },
            )
        }
    }
}

@Composable
private fun AuthFooterIcon(icon: ImageVector) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.08f))
            .border(1.dp, Color.White.copy(alpha = 0.12f), CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
    }
}

@Composable
internal fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    leadingIcon: ImageVector,
    errorText: String?,
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
            color = AuthColors.TextPrimary,
            modifier = Modifier.padding(bottom = 6.dp),
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    text = placeholder,
                    color = AuthColors.TextSecondary.copy(alpha = 0.7f),
                    fontSize = 14.sp,
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    tint = AuthColors.TextSecondary,
                    modifier = Modifier.size(20.dp),
                )
            },
            trailingIcon = if (isPassword && onTogglePasswordVisibility != null) {
                {
                    IconButton(onClick = onTogglePasswordVisibility, modifier = Modifier.size(40.dp)) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = null,
                            tint = AuthColors.TextSecondary,
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
                .defaultMinSize(minHeight = AuthFieldMinHeight),
            shape = AuthPillShape,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = AuthColors.InputBackground,
                unfocusedContainerColor = AuthColors.InputBackground,
                disabledContainerColor = AuthColors.InputBackground,
                focusedBorderColor = AuthColors.InputBorder,
                unfocusedBorderColor = AuthColors.InputBorder.copy(alpha = 0.6f),
                errorBorderColor = Color(0xFFB00020),
            ),
        )
    }
}

@Composable
internal fun AuthBackButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    IconButton(
        onClick = onClick,
        modifier = modifier.size(40.dp),
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "返回",
            tint = AuthColors.TextPrimary,
            modifier = Modifier.size(22.dp),
        )
    }
}

@Composable
internal fun AuthCompactRegisterHeader(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AuthBackButton(onClick = onBack)
        Spacer(modifier = Modifier.width(4.dp))
        Icon(
            imageVector = Icons.Default.Restaurant,
            contentDescription = null,
            tint = AuthColors.BrandBrown,
            modifier = Modifier.size(24.dp),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "锅铲黄小厨",
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = AuthColors.BrandBrown,
        )
    }
}

@Composable
internal fun AuthSocialDivider(label: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFE5E7EB))
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 12.dp),
            fontSize = 12.sp,
            color = AuthColors.TextSecondary,
        )
        HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFE5E7EB))
    }
}

@Composable
internal fun AuthWideSocialButtons() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
    ) {
        AuthSocialIconButton(Icons.Default.Chat)
        AuthSocialIconButton(Icons.Default.Language)
        AuthSocialIconButton(Icons.Default.PhoneIphone)
    }
}

@Composable
internal fun AuthCompactSocialButtons() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(32.dp, Alignment.CenterHorizontally),
    ) {
        AuthSocialLabelButton(icon = Icons.Default.Chat, label = "微信", iconTint = Color(0xFF07C160))
        AuthSocialLabelButton(icon = Icons.Default.PhoneIphone, label = "手机号", iconTint = AuthColors.BrandBrown)
    }
}

@Composable
internal fun AuthRegisterSocialButtons() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
    ) {
        AuthSocialIconButton(Icons.Default.Chat)
        AuthSocialIconButton(Icons.Default.Language)
        AuthSocialIconButton(Icons.Default.PhoneIphone)
    }
}

@Composable
private fun AuthSocialIconButton(icon: ImageVector) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(AuthColors.InputBackground)
            .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(12.dp))
            .clickable { },
        contentAlignment = Alignment.Center,
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = AuthColors.TextSecondary)
    }
}

@Composable
private fun AuthSocialLabelButton(
    icon: ImageVector,
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
                .background(AuthColors.InputBackground)
                .border(1.dp, Color(0xFFE5E7EB), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(imageVector = icon, contentDescription = label, tint = iconTint, modifier = Modifier.size(26.dp))
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(text = label, fontSize = 12.sp, color = AuthColors.TextSecondary)
    }
}

@Composable
internal fun AuthSwitchLink(
    prefix: String,
    linkText: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
    ) {
        Text(prefix, fontSize = 13.sp, color = AuthColors.TextSecondary)
        Text(
            text = linkText,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = AuthColors.Link,
            modifier = Modifier.clickable(onClick = onClick),
        )
    }
}
