package org.xg.project.ui

import aigent.composeapp.generated.resources.ChineseFontFamily
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily

@Composable
fun appTypography(): Typography {
    val fontFamily = ChineseFontFamily
    val defaults = Typography()
    return Typography(
        displayLarge = defaults.displayLarge.withFontFamily(fontFamily),
        displayMedium = defaults.displayMedium.withFontFamily(fontFamily),
        displaySmall = defaults.displaySmall.withFontFamily(fontFamily),
        headlineLarge = defaults.headlineLarge.withFontFamily(fontFamily),
        headlineMedium = defaults.headlineMedium.withFontFamily(fontFamily),
        headlineSmall = defaults.headlineSmall.withFontFamily(fontFamily),
        titleLarge = defaults.titleLarge.withFontFamily(fontFamily),
        titleMedium = defaults.titleMedium.withFontFamily(fontFamily),
        titleSmall = defaults.titleSmall.withFontFamily(fontFamily),
        bodyLarge = defaults.bodyLarge.withFontFamily(fontFamily),
        bodyMedium = defaults.bodyMedium.withFontFamily(fontFamily),
        bodySmall = defaults.bodySmall.withFontFamily(fontFamily),
        labelLarge = defaults.labelLarge.withFontFamily(fontFamily),
        labelMedium = defaults.labelMedium.withFontFamily(fontFamily),
        labelSmall = defaults.labelSmall.withFontFamily(fontFamily),
    )
}

private fun TextStyle.withFontFamily(fontFamily: FontFamily): TextStyle =
    copy(fontFamily = fontFamily)
