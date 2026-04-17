package org.xg.project.screen

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.xg.project.Routes.Routes

@Composable
fun AppScaffold(
    activeTab: Routes,
    onTabClick: (Routes) -> Unit,
    showBottomBar: Boolean = true,
    applyStatusBarsPadding: Boolean = true,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        modifier = Modifier.then(
            if (applyStatusBarsPadding) {
                Modifier.statusBarsPadding()
            } else {
                Modifier
            }
        ),
        bottomBar = {
            if (showBottomBar) {
                TabBar(
                    activeRoute = activeTab,
                    onTabClick = onTabClick
                )
            }
        },
        content = content
    )
}
