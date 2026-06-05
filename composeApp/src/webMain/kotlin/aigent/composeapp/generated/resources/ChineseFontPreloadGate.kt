package aigent.composeapp.generated.resources

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.preloadFont

@OptIn(ExperimentalResourceApi::class)
@Composable
fun ChineseFontPreloadGate(content: @Composable () -> Unit) {
    val regularFont by preloadFont(
        resource = Res.font.NotoSansSC_Regular,
        weight = FontWeight.Normal,
        style = FontStyle.Normal,
    )
    var fallbackReady by remember { mutableStateOf(false) }
    val fontFamilyResolver = LocalFontFamilyResolver.current
    val appFontFamily = ChineseFontFamily

    LaunchedEffect(regularFont, fontFamilyResolver, appFontFamily) {
        val font = regularFont ?: return@LaunchedEffect
        fontFamilyResolver.preload(FontFamily(listOf(font)))
        fontFamilyResolver.preload(appFontFamily)
        fallbackReady = true
    }

    if (regularFont != null && fallbackReady) {
        content()
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8F5F0)),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator()
        }
    }
}
