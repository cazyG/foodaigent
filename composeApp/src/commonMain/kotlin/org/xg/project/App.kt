package org.xg.project

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import org.koin.compose.koinInject
import org.xg.project.data.session.UserSessionRepository
import org.xg.project.presentation.navigation.AppNavigationCoordinator
import org.xg.project.presentation.navigation.AppNavigationEvent
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import coil3.network.ktor3.KtorNetworkFetcherFactory
import org.koin.compose.KoinApplication
import org.koin.dsl.koinConfiguration
import androidx.navigation3.runtime.rememberNavBackStack
import org.xg.project.Routes.AppRoute
import org.xg.project.Routes.BottomTabRoute
import org.xg.project.Routes.RecipesInternalRoute
import org.xg.project.di.appModule
import org.xg.project.screen.BottomTabBar
import org.xg.project.screen.HistoryScreen
import org.xg.project.screen.HomeScreen
import org.xg.project.screen.ManualRecipeInputScreen
import org.xg.project.screen.ProfileScreen
import org.xg.project.screen.RecipesScreen
import org.xg.project.screen.RecipeDetailScreen
import org.xg.project.screen.LoginScreen
import org.xg.project.screen.GlassStyle
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import org.xg.project.screen.home.HomeColors
import org.xg.project.screen.navigation.AppDesktopSidebar
import org.xg.project.screen.navigation.isTabletLandscape
import org.xg.project.screen.recipes.RecipesColors
import org.xg.project.screen.recipes.RecipesFonts

private fun BottomTabRoute.toSaveableName(): String = when (this) {
    BottomTabRoute.Home -> "tab_home"
    BottomTabRoute.Recipes -> "tab_recipes"
    BottomTabRoute.History -> "tab_history"
    BottomTabRoute.Profile -> "tab_profile"
}

private fun bottomTabRouteFromSaveableName(name: String): BottomTabRoute = when (name) {
    "tab_recipes" -> BottomTabRoute.Recipes
    "tab_history" -> BottomTabRoute.History
    "tab_profile" -> BottomTabRoute.Profile
    else -> BottomTabRoute.Home
}

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
                val rootBackStack = rememberAppNavBackStack(AppRoute.Login)
                
                val popRootBackStack = {
                    if (rootBackStack.size > 1) {
                        rootBackStack.removeAt(rootBackStack.lastIndex)
                    }
                    Unit
                }

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
                                val navigationCoordinator = koinInject<AppNavigationCoordinator>()
                                ManualRecipeInputScreen(
                                    onBack = popRootBackStack,
                                    onSave = {
                                        navigationCoordinator.refreshRecipes()
                                        popRootBackStack()
                                    },
                                    onNavigateToProfile = {
                                        navigationCoordinator.openProfileTab()
                                        popRootBackStack()
                                    },
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
            }
        })
}

@Composable
private fun HomeNavDisplay(
    onNavigateToManualInput: () -> Unit,
    onNavigateToRecipeDetail: (String) -> Unit
) {
    val userSessionRepository = koinInject<UserSessionRepository>()
    val navigationCoordinator = koinInject<AppNavigationCoordinator>()
    val currentUser by userSessionRepository.currentUser.collectAsState()
    val selectedTabName = rememberSaveable { mutableStateOf(BottomTabRoute.Home.toSaveableName()) }
    val selectedTab = bottomTabRouteFromSaveableName(selectedTabName.value)
    val selectTab: (BottomTabRoute) -> Unit = { tab ->
        selectedTabName.value = tab.toSaveableName()
    }
    val recipesRefreshKey = remember { mutableStateOf(0) }

    LaunchedEffect(navigationCoordinator) {
        navigationCoordinator.events.collect { event ->
            when (event) {
                AppNavigationEvent.OpenProfileTab -> selectTab(BottomTabRoute.Profile)
                AppNavigationEvent.RefreshRecipes -> recipesRefreshKey.value++
            }
        }
    }
    val homeBackStack = rememberAppNavBackStack(BottomTabRoute.Home)
    val recipesBackStack = rememberAppNavBackStack(BottomTabRoute.Recipes)
    val historyBackStack = rememberAppNavBackStack(BottomTabRoute.History)
    val profileBackStack = rememberAppNavBackStack(BottomTabRoute.Profile)
    val activeBackStack = when (selectedTab) {
        BottomTabRoute.Home -> homeBackStack
        BottomTabRoute.Recipes -> recipesBackStack
        BottomTabRoute.History -> historyBackStack
        BottomTabRoute.Profile -> profileBackStack
    }
    val popActiveBackStack = {
        if (activeBackStack.size > 1) {
            activeBackStack.removeAt(activeBackStack.lastIndex)
        }
        Unit
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val tabletLandscape = isTabletLandscape(maxWidth, maxHeight)

        Box(
            modifier = Modifier
                .fillMaxSize()
                .then(
                    if (tabletLandscape) {
                        Modifier.background(Color.White)
                    } else {
                        Modifier.background(GlassStyle.BgGradient)
                    },
                ),
        ) {
        Scaffold(
            modifier = Modifier,
            bottomBar = {
                if (!tabletLandscape) {
                    BottomTabBar(
                        activeTab = selectedTab,
                        onTabClick = selectTab,
                    )
                }
            },
            containerColor = Transparent
        ) { innerPadding ->
            Row(modifier = Modifier.padding(innerPadding)) {
                if (tabletLandscape) {
                    AppDesktopSidebar(
                        activeTab = selectedTab,
                        onTabClick = selectTab,
                        onProfileClick = { selectTab(BottomTabRoute.Profile) },
                        userAccount = currentUser,
                        modifier = Modifier.fillMaxHeight(),
                        footerTop = {
                            AppSidebarFooterTop(
                                selectedTab = selectedTab,
                                onNavigateToManualInput = onNavigateToManualInput,
                            )
                        },
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
                                    // 保留食谱库原始页面状态，只叠加「从首页进入」的临时层，
                                    // 这样返回时可以回到原食谱库，而不是丢失原状态。
                                    if (recipesBackStack.isEmpty()) {
                                        recipesBackStack.add(BottomTabRoute.Recipes)
                                    }
                                    while (recipesBackStack.lastOrNull() is RecipesInternalRoute.FromHome) {
                                        recipesBackStack.removeAt(recipesBackStack.lastIndex)
                                    }
                                    if (recipesBackStack.lastOrNull() !is BottomTabRoute.Recipes) {
                                        recipesBackStack.add(BottomTabRoute.Recipes)
                                    }
                                    recipesBackStack.add(RecipesInternalRoute.FromHome(mealType.name))
                                    selectTab(BottomTabRoute.Recipes)
                                },
                                onNavigateToProfile = { selectTab(BottomTabRoute.Profile) },
                            )
                        }
                        entry<BottomTabRoute.Recipes> {
                            RecipesScreen(
                                isFromHome = false,
                                refreshTrigger = recipesRefreshKey.value,
                                onNavigateToManualInput = onNavigateToManualInput,
                                onNavigateToDetail = onNavigateToRecipeDetail,
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
                                    selectTab(BottomTabRoute.Home)
                                    if (recipesBackStack.lastOrNull() is RecipesInternalRoute.FromHome) {
                                        recipesBackStack.removeAt(recipesBackStack.lastIndex)
                                    }
                                    if (recipesBackStack.isEmpty()) {
                                        recipesBackStack.add(BottomTabRoute.Recipes)
                                    }
                                },
                            )
                        }
                        entry<BottomTabRoute.History> { HistoryScreen() }
                        entry<BottomTabRoute.Profile> {
                            ProfileScreen(refreshTrigger = recipesRefreshKey.value)
                        }
                    }
                )
            }
        }
        }
    }
}

@Composable
private fun AppSidebarFooterTop(
    selectedTab: BottomTabRoute,
    onNavigateToManualInput: () -> Unit,
) {
    when (selectedTab) {
        BottomTabRoute.Home -> {
            Button(
                onClick = { },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = HomeColors.BrandBrown),
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(6.dp))
                Text("上传新菜谱", color = Color.White)
            }
        }
        BottomTabRoute.Recipes -> {
            Button(
                onClick = onNavigateToManualInput,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = RecipesColors.BrandBrown),
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(6.dp))
                Text("+ 手动录入", fontSize = RecipesFonts.actionButton, color = Color.White)
            }
        }
        else -> Unit
    }
}
