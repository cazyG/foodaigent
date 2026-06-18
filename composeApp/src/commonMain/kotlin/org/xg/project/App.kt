package org.xg.project

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.xg.project.feature.greeting.GreetingScreen

@Composable
@Preview
fun App() {
    MaterialTheme {
        GreetingScreen()
    }
}