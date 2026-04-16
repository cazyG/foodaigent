package org.xg.project

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavEntry
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
import org.xg.project.screen.TabBar

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
                val backStack = remember { mutableStateListOf(Routes.Home) }
                val recipesRefreshKey = remember { mutableStateOf(0) }
                val currentRoute = backStack.lastOrNull() ?: Routes.Home
                val popBackStack = {
                    if (backStack.size > 1) {
                        backStack.removeAt(backStack.lastIndex)
                    }
                }
                Scaffold(
                    modifier = Modifier.then(
                        if (currentRoute != Routes.ManualRecipeInput) {
                            Modifier.statusBarsPadding()
                        } else {
                            Modifier
                        }
                    ),
                    bottomBar = {
                        if (currentRoute != Routes.ManualRecipeInput) {
                            TabBar(
                                activeRoute = currentRoute,
                                onTabClick = { route ->
                                    if (currentRoute != route) {
                                        backStack.clear()
                                        backStack.add(route)
                                    }
                                }
                            )
                        }
                    }
                ) { innerPadding ->
                    NavDisplay(
                        backStack = backStack,
//                        modifier = Modifier.padding(innerPadding),
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
                        }
                    ) { key ->
                        when (key) {
                            Routes.Home -> NavEntry(key) {
                                IndexScreen(
                                    onAddPlan = { backStack.add(Routes.Recipes) }
                                )
                            }

                            Routes.Recipes -> NavEntry(key) {
                                RecipesScreen(
                                    refreshTrigger = recipesRefreshKey.value,
                                    onNavigateToManualInput = { backStack.add(Routes.ManualRecipeInput) }
                                )
                            }
                            Routes.Plan -> NavEntry(key) { PlanningScreen() }
                            Routes.History -> NavEntry(key) { HistoryScreen() }
                            Routes.Profile -> NavEntry(key) { ProfileScreen() }
                            Routes.ManualRecipeInput -> NavEntry(key) { ManualRecipeInputScreen(
                                onBack = popBackStack,
                                onSave = { 
                                    // 保存成功后返回上一页
                                    recipesRefreshKey.value += 1
                                    popBackStack()
                                }
                            ) }
                            else -> NavEntry(key) { Text("Unknown Route") }
                        }
                    }
                }
            }
        })
}