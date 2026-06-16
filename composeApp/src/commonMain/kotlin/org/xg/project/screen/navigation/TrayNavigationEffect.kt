package org.xg.project.screen.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import org.koin.compose.koinInject
import org.xg.project.Routes.AppRoute
import org.xg.project.TrayNavigationBridge
import org.xg.project.data.session.UserSessionRepository
import org.xg.project.presentation.navigation.AppNavigationCoordinator

@Composable
internal fun TrayNavigationEffect(rootBackStack: NavBackStack<NavKey>) {
    val navigationCoordinator = koinInject<AppNavigationCoordinator>()
    val userSessionRepository = koinInject<UserSessionRepository>()
    val currentUser by userSessionRepository.currentUser.collectAsState()

    LaunchedEffect(rootBackStack, navigationCoordinator, currentUser) {
        TrayNavigationBridge.openSettingsRequests.collect {
            navigateToProfileFromTray(
                rootBackStack = rootBackStack,
                navigationCoordinator = navigationCoordinator,
                isLoggedIn = currentUser != null,
            )
        }
    }
}

private fun navigateToProfileFromTray(
    rootBackStack: NavBackStack<NavKey>,
    navigationCoordinator: AppNavigationCoordinator,
    isLoggedIn: Boolean,
) {
    if (!isLoggedIn) return

    when (rootBackStack.lastOrNull()) {
        is AppRoute.ManualRecipeInput, is AppRoute.RecipeDetail -> {
            while (rootBackStack.size > 1) {
                rootBackStack.removeAt(rootBackStack.lastIndex)
            }
        }
        is AppRoute.Login -> {
            rootBackStack.clear()
            rootBackStack.add(AppRoute.Home)
        }
        else -> Unit
    }

    if (rootBackStack.lastOrNull() !is AppRoute.Home) {
        rootBackStack.clear()
        rootBackStack.add(AppRoute.Home)
    }

    navigationCoordinator.openProfileTab()
}
