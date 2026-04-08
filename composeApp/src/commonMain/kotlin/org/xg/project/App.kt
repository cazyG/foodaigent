package org.xg.project

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import coil3.network.ktor3.KtorNetworkFetcherFactory
import org.koin.compose.KoinApplication
import org.koin.core.KoinApplication
import org.koin.dsl.koinConfiguration
import org.xg.project.Routes.Routes
import org.xg.project.di.appModule
import org.xg.project.screen.HistoryScreen
import org.xg.project.screen.IndexScreen
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
                val backStack = remember { mutableStateListOf<String>(Routes.Home) }
                val currentRoute = backStack.lastOrNull() ?: Routes.Home
                Scaffold(
                    modifier = Modifier.statusBarsPadding(),
                    bottomBar = {
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
                ) { innerPadding ->
                    NavDisplay(
                        backStack = backStack,
//                        modifier = Modifier.padding(innerPadding)
                    ) { key ->
                        when (key) {
                            Routes.Home -> NavEntry(key) {
                                IndexScreen(
                                    onAddPlan = { backStack.add(Routes.Recipes) }
                                )
                            }

                            Routes.Recipes -> NavEntry(key) { RecipesScreen() }
                            Routes.Plan -> NavEntry(key) { PlanningScreen() }
                            Routes.History -> NavEntry(key) { HistoryScreen() }
                            Routes.Profile -> NavEntry(key) { ProfileScreen() }
                            else -> NavEntry(key) { Text("Unknown Route") }
                        }
                    }
                }
            }
        })
}