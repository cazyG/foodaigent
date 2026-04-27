import androidx.compose.ui.ComposeUiFlags
import androidx.compose.ui.ExperimentalComposeUiApi

@OptIn(ExperimentalComposeUiApi::class)
fun test() {
    ComposeUiFlags.isMediaQueryIntegrationEnabled = true
}
