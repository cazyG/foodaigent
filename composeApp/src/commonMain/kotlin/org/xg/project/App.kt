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
import org.xg.project.Routes.AppRoute
import org.xg.project.Routes.BottomTabRoute
import org.xg.project.Routes.HomeInternalRoute
import org.xg.project.Routes.SearchInternalRoute
import org.xg.project.di.appModule
import org.xg.project.screen.BottomTabBar
import org.xg.project.screen.HomeDetailScreen
import org.xg.project.screen.IndexScreen
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
    val homeBackStack = rememberAppNavBackStack(HomeInternalRoute.List)
    val searchBackStack = rememberAppNavBackStack(SearchInternalRoute.Main)
    val profileBackStack = rememberAppNavBackStack(BottomTabRoute.Profile)
    val activeBackStack = when (selectedTab.value) {
        BottomTabRoute.Home -> homeBackStack
        BottomTabRoute.Search -> searchBackStack
        BottomTabRoute.Profile -> profileBackStack
    }
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
                entry<HomeInternalRoute.List> {
                    IndexScreen(
                        onSelectTab = { selectedTab.value = it },
                        onNavigateToDetail = { id ->
                            homeBackStack.add(HomeInternalRoute.Detail(id = id))
                        }
                    )
                }
                entry<HomeInternalRoute.Detail> { backStackEntry ->
                    HomeDetailScreen(
                        id = backStackEntry.key.id,
                        onNavigateBack = {
                            popActiveBackStack()
                        }
                    )
                }
                entry<SearchInternalRoute.Main> {
                    RecipesScreen()
                }
                entry<BottomTabRoute.Profile> {
                    ProfileScreen()
                }
            }
        )
    }
}
