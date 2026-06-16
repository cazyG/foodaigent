package org.xg.project.screen.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import org.koin.compose.koinInject
import org.xg.project.Routes.AppRoute
import org.xg.project.screen.GlassStyle
import org.xg.project.screen.ManualRecipeInputScreen
import org.xg.project.screen.RecipeDetailScreen
import org.xg.project.screen.auth.AuthScreen
import org.xg.project.presentation.navigation.AppNavigationCoordinator

@Composable
fun AppRootNavHost() {
    val rootBackStack = rememberAppNavBackStack(AppRoute.Login)
    TrayNavigationEffect(rootBackStack)

    val isLoginScreen = rootBackStack.lastOrNull() is AppRoute.Login

    Box(
        modifier = Modifier
            .fillMaxSize()
            .then(
                if (isLoginScreen) {
                    Modifier.background(Transparent)
                } else {
                    Modifier.background(GlassStyle.BgGradient)
                },
            ),
    ) {
        NavDisplay(
            backStack = rootBackStack,
            onBack = { rootBackStack.popOne() },
            transitionSpec = { appHorizontalTransition() },
            popTransitionSpec = { appHorizontalPopTransition() },
            entryProvider = entryProvider {
                entry<AppRoute.Login> {
                    AuthScreen(
                        onAuthSuccess = {
                            rootBackStack.clear()
                            rootBackStack.add(AppRoute.Home)
                        },
                    )
                }
                entry<AppRoute.Home> {
                    HomeNavDisplay(
                        onNavigateToManualInput = {
                            rootBackStack.add(AppRoute.ManualRecipeInput)
                        },
                        onNavigateToRecipeDetail = { recipeId ->
                            rootBackStack.add(AppRoute.RecipeDetail(recipeId))
                        },
                        onLogout = {
                            rootBackStack.clear()
                            rootBackStack.add(AppRoute.Login)
                        },
                    )
                }
                entry<AppRoute.ManualRecipeInput> {
                    val navigationCoordinator = koinInject<AppNavigationCoordinator>()
                    ManualRecipeInputScreen(
                        onBack = { rootBackStack.popOne() },
                        onSave = {
                            navigationCoordinator.refreshRecipes()
                            rootBackStack.popOne()
                        },
                        onNavigateToProfile = {
                            navigationCoordinator.openProfileTab()
                            rootBackStack.popOne()
                        },
                    )
                }
                entry<AppRoute.RecipeDetail> { route ->
                    RecipeDetailScreen(
                        recipeId = route.id,
                        onBack = { rootBackStack.popOne() },
                    )
                }
            },
        )
    }
}
