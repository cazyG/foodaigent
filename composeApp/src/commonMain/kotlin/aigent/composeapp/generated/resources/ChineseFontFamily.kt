package aigent.composeapp.generated.resources

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import org.jetbrains.compose.resources.Font as ComposeFont

val ChineseFontFamily: FontFamily
    @Composable get() = FontFamily(
        ComposeFont(Res.font.NotoSansSC_Regular, FontWeight.Normal),
        ComposeFont(Res.font.NotoSansSC_Medium, FontWeight.Medium),
        ComposeFont(Res.font.NotoSansSC_Bold, FontWeight.SemiBold),
        ComposeFont(Res.font.NotoSansSC_Bold, FontWeight.Bold),
    )
