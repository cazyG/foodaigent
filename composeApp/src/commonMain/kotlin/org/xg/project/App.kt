package org.xg.project

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.entry
import androidx.navigation3.ui.NavDisplay
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import coil3.network.ktor3.KtorNetworkFetcherFactory
import org.koin.compose.KoinApplication
import org.koin.dsl.koinConfiguration
import org.xg.project.Routes.Routes
import org.xg.project.di.appModule
import org.xg.project.screen.HistoryScreen
import org.xg.project.screen.IndexScreen
import org.xg.project.screen.ManualRecipeInputScreen
import org.xg.project.screen.PlanningScreen
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
                val navBackStack = rememberNavBackStack(initialDestination = Routes.Home)
                val currentTab = navBackStack.firstOrNull() ?: Routes.Home
                val recipesRefreshKey = remember { androidx.compose.runtime.mutableStateOf(0) }
                val popBackStack = {
                    if (navBackStack.size > 1) {
                        navBackStack.removeAt(navBackStack.lastIndex)
                    } else if (currentTab != Routes.Home) {
                        navBackStack.clear()
                        navBackStack.add(Routes.Home)
                    }
                }
                val switchTab: (Routes) -> Unit = { route ->
                    if (navBackStack.firstOrNull() == route) {
                        navBackStack.clear()
                        navBackStack.add(route)
                    } else {
                        navBackStack.clear()
                        navBackStack.add(route)
                    }
                }
                NavDisplay(
                    backStack = navBackStack,
                    onBack = popBackStack,
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
                        entry<Routes.Home> {
                            IndexScreen(
                                activeTab = currentTab,
                                onTabClick = switchTab,
                                onAddPlan = { switchTab(Routes.Recipes) }
                            )
                        }
                        entry<Routes.Recipes> {
                            RecipesScreen(
                                activeTab = currentTab,
                                onTabClick = switchTab,
                                refreshTrigger = recipesRefreshKey.value,
                                onNavigateToManualInput = { navBackStack.add(Routes.ManualRecipeInput) }
                            )
                        }
                        entry<Routes.History> { HistoryScreen(activeTab = currentTab, onTabClick = switchTab) }
                        entry<Routes.Profile> { ProfileScreen(activeTab = currentTab, onTabClick = switchTab) }
                        entry<Routes.Plan> { PlanningScreen(activeTab = currentTab, onTabClick = switchTab) }
                        entry<Routes.ManualRecipeInput> {
                            ManualRecipeInputScreen(
                                onBack = popBackStack,
                                onSave = {
                                    recipesRefreshKey.value += 1
                                    popBackStack()
                                }
                            )
                        }
                    }
                )
            }
        })
}
