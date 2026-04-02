package org.xg.project

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.painterResource

import aigent.composeapp.generated.resources.Res
import aigent.composeapp.generated.resources.compose_multiplatform
import androidx.compose.material3.Scaffold
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import org.xg.project.Routes.Routes
import org.xg.project.screen.HistoryScreen
import org.xg.project.screen.IndexScreen
import org.xg.project.screen.PlanningScreen
import org.xg.project.screen.RecipesScreen
import org.xg.project.screen.TabBar

//import com.multiplatform.webview.web.WebView
//import com.multiplatform.webview.web.rememberWebViewState

@Composable
fun App() {
    MaterialTheme {
        val navController = rememberNavController()
        val currentBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = currentBackStackEntry?.destination?.route ?: Routes.Home
        Scaffold(
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
        ) {
            NavHost(
                navController = navController,
                startDestination = Routes.Home
            ) {
                composable(Routes.Home) { IndexScreen() }
                composable(Routes.Recipes) { RecipesScreen() }
                composable(Routes.Plan) { PlanningScreen() }
                composable(Routes.History) { HistoryScreen() }
                composable(Routes.Profile) { Text("测试") }
            }
        }
//        var showContent by remember { mutableStateOf(false) }
////        val state = rememberWebViewState("http://baidu.com")
////        WebView(state = state)
//        Column(
//            modifier = Modifier
//                .background(MaterialTheme.colorScheme.primaryContainer)
//                .safeContentPadding()
//                .fillMaxSize(),
//            horizontalAlignment = Alignment.CenterHorizontally,
//        ) {
//            Button(onClick = { showContent = !showContent }) {
//                Text("Click me!")
//            }
//            AnimatedVisibility(showContent) {
//                val greeting = remember { Greeting().greet() }
//                Column(
//                    horizontalAlignment = Alignment.CenterHorizontally,
//                ) {
//                    Image(painterResource(Res.drawable.compose_multiplatform), null)
//                    Text("Compose: $greeting")
//                }
//            }
//
//
//
//        }
    }
}