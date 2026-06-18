package org.xg.project.feature.greeting

import aigent.composeapp.generated.resources.Res
import aigent.composeapp.generated.resources.compose_multiplatform
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.collectLatest
import org.jetbrains.compose.resources.painterResource
import org.xg.project.Greeting

@Composable
fun GreetingScreen(
    vm: GreetingViewModel = viewModel { GreetingViewModel(greetingProvider = Greeting()) },
) {
    val uiState by vm.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(vm) {
        vm.effect.collectLatest { effect ->
            when (effect) {
                is GreetingEffect.ShowErrorMessage -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.primaryContainer)
            .safeContentPadding()
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Button(
            onClick = { vm.onIntent(GreetingIntent.ToggleGreeting) },
            enabled = !uiState.isLoading,
        ) {
            Text(
                if (uiState.isGreetingVisible) {
                    "Hide greeting"
                } else {
                    "Show greeting"
                },
            )
        }

        if (uiState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(28.dp),
            )
        }

        AnimatedVisibility(uiState.isGreetingVisible && uiState.greetingMessage != null) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Image(painterResource(Res.drawable.compose_multiplatform), null)
                Text("Compose: ${uiState.greetingMessage}")
            }
        }

        val errorMessage = uiState.errorMessage
        if (errorMessage != null) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.widthIn(max = 320.dp),
            )
            TextButton(onClick = { vm.onIntent(GreetingIntent.ClearError) }) {
                Text("Dismiss")
            }
        }

        SnackbarHost(hostState = snackbarHostState)
    }
}
