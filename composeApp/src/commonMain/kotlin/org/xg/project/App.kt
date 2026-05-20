package org.xg.project

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.unit.dp
import androidx.compose.ui.mediaQuery
import androidx.compose.ui.ExperimentalMediaQueryApi
import androidx.compose.ui.UiMediaScope
import androidx.compose.ui.ComposeUiFlags
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import coil3.network.ktor3.KtorNetworkFetcherFactory
import org.koin.compose.KoinApplication
import org.koin.dsl.koinConfiguration
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import org.xg.project.Routes.AppRoute
import org.xg.project.Routes.BottomTabRoute
import org.xg.project.Routes.RecipesInternalRoute
import org.xg.project.di.appModule
import org.xg.project.screen.BottomTabBar
import org.xg.project.screen.SideNavigationBar
import org.xg.project.screen.HistoryScreen
import org.xg.project.screen.HomeScreen
import org.xg.project.screen.ManualRecipeInputScreen
import org.xg.project.screen.ProfileScreen
import org.xg.project.screen.RecipesScreen
import org.xg.project.screen.RecipeDetailScreen
import org.xg.project.screen.LoginScreen

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun App() {
    ComposeUiFlags.isMediaQueryIntegrationEnabled = true
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
                val rootBackStack = rememberAppNavBackStack(AppRoute.Login)
                
                val popRootBackStack = {
                    if (rootBackStack.size > 1) {
                        rootBackStack.removeAt(rootBackStack.lastIndex)
                    }
                    Unit
                }

                NavDisplay(
                    backStack = rootBackStack,
                    onBack = popRootBackStack,
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
                        entry<AppRoute.Login> {
                            LoginScreen(
                                onLoginSuccess = {
                                    rootBackStack.clear()
                                    rootBackStack.add(AppRoute.Home)
                                }
                            )
                        }
                        entry<AppRoute.Home> {
                            HomeNavDisplay(
                                onNavigateToManualInput = {
                                    rootBackStack.add(AppRoute.ManualRecipeInput)
                                },
                                onNavigateToRecipeDetail = { recipeId ->
                                    rootBackStack.add(AppRoute.RecipeDetail(recipeId))
                                }
                            )
                        }
                        entry<AppRoute.ManualRecipeInput> {
                            ManualRecipeInputScreen(
                                onBack = popRootBackStack,
                                onSave = {
                                    // 手动录入完成后，返回上一级。
                                    // 注意：这里可能需要通知 RecipesScreen 刷新列表，目前使用重新进入或状态管理来更新
                                    popRootBackStack()
                                }
                            )
                        }
                        entry<AppRoute.RecipeDetail> { route ->
                            RecipeDetailScreen(
                                recipeId = route.id,
                                onBack = popRootBackStack
                            )
                        }
                    }
                )
            }
        })
}

@OptIn(ExperimentalMediaQueryApi::class)
@Composable
private fun HomeNavDisplay(
    onNavigateToManualInput: () -> Unit,
    onNavigateToRecipeDetail: (String) -> Unit
) {
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

    Box(
        modifier = Modifier
            .background(
                Brush.linearGradient(colors = listOf(Color(0xFFD9CA8F),Color(0xFFD7ECF6)),
                    start = Offset(0f, 0f),
                    end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY))
            ),
    ){
        val isWideScreen = mediaQuery { windowSize.width >= 600.dp }

        Scaffold(
            modifier = Modifier,
            bottomBar = {
                if (!isWideScreen) {
                    BottomTabBar(
                        activeTab = selectedTab.value,
                        onTabClick = { selectedTab.value = it }
                    )
                }
            },
            containerColor = Transparent
        ) { innerPadding ->
            Row(modifier = Modifier.padding(innerPadding)) {
                if (isWideScreen) {
                    SideNavigationBar(
                        activeTab = selectedTab.value,
                        onTabClick = { selectedTab.value = it }
                    )
                }

                NavDisplay(
                    backStack = activeBackStack,
                    modifier = Modifier.weight(1f),
                    onBack = popActiveBackStack,
                    entryProvider = entryProvider {
                        entry<BottomTabRoute.Home> {
                            HomeScreen(
                                onAddPlan = { mealType ->
                                    recipesBackStack.clear()
                                    recipesBackStack.add(RecipesInternalRoute.FromHome(mealType.name))
                                    selectedTab.value = BottomTabRoute.Recipes
                                }
                            )
                        }
                        entry<BottomTabRoute.Recipes> {
                            RecipesScreen(
                                isFromHome = false,
                                refreshTrigger = recipesRefreshKey.value,
                                onNavigateToManualInput = onNavigateToManualInput,
                                onNavigateToDetail = onNavigateToRecipeDetail
                            )
                        }
                        entry<RecipesInternalRoute.FromHome> { route ->
                            RecipesScreen(
                                isFromHome = true,
                                initialMealType = route.mealType,
                                refreshTrigger = recipesRefreshKey.value,
                                onNavigateToManualInput = onNavigateToManualInput,
                                onBack = popActiveBackStack,
                                onSaveSuccess = {
                                    selectedTab.value = BottomTabRoute.Home
                                    recipesBackStack.clear()
                                    recipesBackStack.add(BottomTabRoute.Recipes)
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
    }
}
