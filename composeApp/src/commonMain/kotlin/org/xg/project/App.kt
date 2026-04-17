package org.xg.project

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
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
import org.xg.project.screen.TabBar

private data class ScreenChrome(
    val showBottomBar: Boolean,
    val applyStatusBarsPadding: Boolean
)

private fun Routes.chrome(): ScreenChrome {
    return when (this) {
        Routes.ManualRecipeInput -> ScreenChrome(showBottomBar = false, applyStatusBarsPadding = false)
        else -> ScreenChrome(showBottomBar = true, applyStatusBarsPadding = true)
    }
}

private fun routesStackSaver(defaultRoot: Routes) = listSaver<SnapshotStateList<Routes>, String>(
    save = { stack -> stack.map { it.id } },
    restore = { ids ->
        val restored = ids.mapNotNull { Routes.fromId(it) }
        mutableStateListOf<Routes>().apply {
            addAll(
                if (restored.isNotEmpty()) restored else listOf(defaultRoot)
            )
        }
    }
)

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
                val homeStack = rememberSaveable(saver = routesStackSaver(Routes.Home)) { mutableStateListOf(Routes.Home) }
                val recipesStack = rememberSaveable(saver = routesStackSaver(Routes.Recipes)) { mutableStateListOf(Routes.Recipes) }
                val historyStack = rememberSaveable(saver = routesStackSaver(Routes.History)) { mutableStateListOf(Routes.History) }
                val profileStack = rememberSaveable(saver = routesStackSaver(Routes.Profile)) { mutableStateListOf(Routes.Profile) }
                val currentTabId = rememberSaveable { mutableStateOf(Routes.Home.id) }
                val recipesRefreshKey = remember { mutableStateOf(0) }
                val currentTab = Routes.fromId(currentTabId.value) ?: Routes.Home
                val activeStack = when (currentTab) {
                    Routes.Home -> homeStack
                    Routes.Recipes -> recipesStack
                    Routes.History -> historyStack
                    Routes.Profile -> profileStack
                    else -> homeStack
                }
                val currentRoute = activeStack.lastOrNull() ?: currentTab
                val chrome = currentRoute.chrome()
                val popBackStack = {
                    if (activeStack.size > 1) {
                        activeStack.removeAt(activeStack.lastIndex)
                    } else if (currentTab != Routes.Home) {
                        currentTabId.value = Routes.Home.id
                    }
                }
                Scaffold(
                    modifier = Modifier.then(
                        if (chrome.applyStatusBarsPadding) {
                            Modifier.statusBarsPadding()
                        } else {
                            Modifier
                        }
                    ),
                    bottomBar = {
                        if (chrome.showBottomBar) {
                            TabBar(
                                activeRoute = currentTab,
                                onTabClick = { route ->
                                    if (currentTab == route) {
                                        activeStack.clear()
                                        activeStack.add(route)
                                        return@TabBar
                                    }
                                    currentTabId.value = route.id
                                }
                            )
                        }
                    }
                ) { innerPadding ->
                    NavDisplay(
                        backStack = activeStack,
                        modifier = Modifier.padding(innerPadding),
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
                                    onAddPlan = { currentTabId.value = Routes.Recipes.id }
                                )
                            }
                            entry<Routes.Recipes> {
                                RecipesScreen(
                                    refreshTrigger = recipesRefreshKey.value,
                                    onNavigateToManualInput = { activeStack.add(Routes.ManualRecipeInput) }
                                )
                            }
                            entry<Routes.History> { HistoryScreen() }
                            entry<Routes.Profile> { ProfileScreen() }
                            entry<Routes.Plan> { PlanningScreen() }
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
            }
        })
}
