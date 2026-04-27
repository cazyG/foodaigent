import androidx.compose.ui.mediaQuery
import androidx.compose.ui.UiMediaScope
import androidx.compose.ui.ExperimentalMediaQueryApi

@OptIn(ExperimentalMediaQueryApi::class)
fun test() {
    mediaQuery {
        val a = windowSize
    }
}
