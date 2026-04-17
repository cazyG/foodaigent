package org.xg.project

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import org.jetbrains.androidx.navigation3.runtime.entryProvider
import org.jetbrains.androidx.navigation3.runtime.entry
import org.jetbrains.androidx.navigation3.ui.NavDisplay
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import coil3.network.ktor3.KtorNetworkFetcherFactory
import org.koin.compose.KoinApplication
import org.koin.dsl.koinConfiguration
import org.jetbrains.androidx.navigation3.runtime.rememberNavBackStack
import org.xg.project.Routes.AppRoute
import org.xg.project.Routes.BottomTabRoute
import org.xg.project.Routes.RecipesInternalRoute
import org.xg.project.di.appModule
import org.xg.project.screen.BottomTabBar
import org.xg.project.screen.HistoryScreen
import org.xg.project.screen.IndexScreen
import org.xg.project.screen.ManualRecipeInputScreen
import org.xg.project.screen.ProfileScreen
import org.xg.project.screen.RecipesScreen

@Composable
fun App() {
    // Coil3 初始化网络请求组件
    setSingletonImageLoaderFactory { context ->
        ImageLoader.Builder(context)
//            .crossfade(true)
//            .logger(DebugLogger())
            .components {
                add(KtorNetworkFetcherFactory())
            }
            .build()
    }
    KoinApplication(
        configuration = koinConfiguration(declaration = { modules(appModule) }),
        content = {
            MaterialTheme {
                NavDisplay(
                    backStack = rememberAppNavBackStack(AppRoute.Main),
                    transitionSpec = {
                        slideInHorizontally {
                            it
                        } + fadeIn() togetherWith slideOutHorizontally {
                            -it
                        } + fadeOut()
                    },
                    popTransitionSpec = {
                        slideInHorizontally {
                            -it
                        } + fadeIn() togetherWith slideOutHorizontally {
                            it
                        } + fadeOut()
                    },
                    entryProvider = entryProvider {
                        entry<AppRoute.Main> { BottomNavDisplay() }
                    }
                )
            }
        })
}

@Composable
private fun BottomNavDisplay() {
    val selectedTab = rememberSaveable { mutableStateOf<BottomTabRoute>(BottomTabRoute.Home) }
    val homeBackStack = rememberAppNavBackStack(BottomTabRoute.Home)
    val recipesBackStack = rememberAppNavBackStack(BottomTabRoute.Recipes)
    val historyBackStack = rememberAppNavBackStack(BottomTabRoute.History)
    val profileBackStack = rememberAppNavBackStack(BottomTabRoute.Profile)
    val activeBackStack = when (selectedTab.value) {
        BottomTabRoute.Home -> homeBackStack
        BottomTabRoute.Recipes -> recipesBackStack
        BottomTabRoute.History -> historyBackStack
        BottomTabRoute.Profile -> profileBackStack
    }
    val recipesRefreshKey = remember { mutableStateOf(0) }
    val popActiveBackStack = {
        if (activeBackStack.size > 1) {
            activeBackStack.removeAt(activeBackStack.lastIndex)
        }
        Unit
    }

    Scaffold(
        bottomBar = {
            BottomTabBar(
                activeTab = selectedTab.value,
                onTabClick = { selectedTab.value = it }
            )
        }
    ) { innerPadding ->
        NavDisplay(
            backStack = activeBackStack,
            modifier = Modifier.padding(innerPadding),
            onBack = popActiveBackStack,
            entryProvider = entryProvider {
                entry<BottomTabRoute.Home> {
                    IndexScreen(
                        onAddPlan = {
                            selectedTab.value = BottomTabRoute.Recipes
                            recipesBackStack.clear()
                            recipesBackStack.add(BottomTabRoute.Recipes)
                        }
                    )
                }
                entry<BottomTabRoute.Recipes> {
                    RecipesScreen(
                        refreshTrigger = recipesRefreshKey.value,
                        onNavigateToManualInput = { recipesBackStack.add(RecipesInternalRoute.ManualRecipeInput) }
                    )
                }
                entry<RecipesInternalRoute.ManualRecipeInput> {
                    ManualRecipeInputScreen(
                        onBack = popActiveBackStack,
                        onSave = {
                            recipesRefreshKey.value += 1
                            popActiveBackStack()
                        }
                    )
                }
                entry<BottomTabRoute.History> { HistoryScreen() }
                entry<BottomTabRoute.Profile> {
                    ProfileScreen()
                }
            }
        )
    }
}
