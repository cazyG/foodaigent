package org.xg.project

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import coil3.network.ktor3.KtorNetworkFetcherFactory
import org.koin.compose.KoinApplication
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

    KoinApplication(application = {
        modules(appModule)
    }) {
        MaterialTheme {
            val navController = rememberNavController()
            val currentBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = currentBackStackEntry?.destination?.route ?: Routes.Home
            Scaffold(
                modifier = Modifier.statusBarsPadding(),
                bottomBar = {
                    TabBar(
                        activeRoute = currentRoute,
                        onTabClick = { route ->
                            if (navController.currentBackStackEntry?.destination?.route != route) {
                                navController.navigate(route) {
                                    popUpTo(navController.graph.id) {
                                        inclusive = true
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                    )
                }
            ) { innerPadding ->
                NavHost(
                    navController = navController,
                    startDestination = Routes.Home,
                    modifier = Modifier.padding(innerPadding)
                ) {
                    composable(Routes.Home) {
                        IndexScreen(
                            onAddPlan = { navController.navigate(Routes.Recipes) }
                        )
                    }
                    composable(Routes.Recipes) { RecipesScreen() }
                    composable(Routes.Plan) { PlanningScreen() }
                    composable(Routes.History) { HistoryScreen() }
                    composable(Routes.Profile) {
                        ProfileScreen()
                    }
                }
            }
        }
    }
}