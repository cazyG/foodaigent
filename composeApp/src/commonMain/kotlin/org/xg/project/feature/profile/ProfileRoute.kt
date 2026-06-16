package org.xg.project.feature.profile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.viewmodel.koinViewModel
import org.xg.project.feature.profile.ProfileEffect
import org.xg.project.feature.profile.ProfileIntent
import org.xg.project.feature.profile.ProfileViewModel
import org.xg.project.feature.home.HomeColors
import org.xg.project.feature.profile.ProfileBreakpoints
import org.xg.project.feature.profile.ProfileCompactLayout
import org.xg.project.feature.profile.ProfileDesktopLayout
import org.xg.project.feature.profile.toProfileContentUi

@Composable
fun ProfileScreen(
    refreshTrigger: Int = 0,
    onNavigateLogin: () -> Unit = {},
    viewModel: ProfileViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(viewModel) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                ProfileEffect.NavigateLogin -> onNavigateLogin()
            }
        }
    }

    LaunchedEffect(refreshTrigger) {
        viewModel.onIntent(ProfileIntent.LoadUserProfile)
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(color = HomeColors.BrandOrange)
            }
            return@BoxWithConstraints
        }

        val content = state.toProfileContentUi()
        val callbacks = ProfileScreenCallbacks(
            onSettingsClick = { },
            onInviteClick = { },
            onManageMembersClick = { },
            onViewAllAchievementsClick = { },
            onLogoutClick = { viewModel.onIntent(ProfileIntent.Logout) },
        )

        if (maxWidth >= ProfileBreakpoints.DesktopMin) {
            ProfileDesktopLayout(
                content = content,
                onInviteClick = callbacks.onInviteClick,
                onManageMembersClick = callbacks.onManageMembersClick,
                onViewAllAchievementsClick = callbacks.onViewAllAchievementsClick,
                onLogoutClick = callbacks.onLogoutClick,
                modifier = Modifier.fillMaxSize(),
            )
        } else {
            ProfileCompactLayout(
                content = content,
                onSettingsClick = callbacks.onSettingsClick,
                onInviteClick = callbacks.onInviteClick,
                onManageMembersClick = callbacks.onManageMembersClick,
                onViewAllAchievementsClick = callbacks.onViewAllAchievementsClick,
                onLogoutClick = callbacks.onLogoutClick,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

private data class ProfileScreenCallbacks(
    val onSettingsClick: () -> Unit,
    val onInviteClick: () -> Unit,
    val onManageMembersClick: () -> Unit,
    val onViewAllAchievementsClick: () -> Unit,
    val onLogoutClick: () -> Unit,
)
