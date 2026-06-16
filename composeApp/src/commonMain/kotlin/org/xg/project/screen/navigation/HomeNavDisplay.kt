package org.xg.project.screen.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import org.koin.compose.koinInject
import org.xg.project.Routes.BottomTabRoute
import org.xg.project.Routes.RecipesInternalRoute
import org.xg.project.Routes.bottomTabFromSaveableName
import org.xg.project.Routes.toSaveableName
import org.xg.project.data.session.UserSessionRepository
import org.xg.project.presentation.navigation.AppNavigationCoordinator
import org.xg.project.presentation.navigation.AppNavigationEvent
import org.xg.project.screen.BottomTabBar
import org.xg.project.screen.GlassStyle
import org.xg.project.screen.HistoryScreen
import org.xg.project.screen.HomeScreen
import org.xg.project.screen.ProfileScreen
import org.xg.project.screen.RecipesScreen

@Composable
internal fun HomeNavDisplay(
    onNavigateToManualInput: () -> Unit,
    onNavigateToRecipeDetail: (String) -> Unit,
    onLogout: () -> Unit,
) {
    val userSessionRepository = koinInject<UserSessionRepository>()
    val navigationCoordinator = koinInject<AppNavigationCoordinator>()
    val currentUser by userSessionRepository.currentUser.collectAsState()
    val selectedTabName = rememberSaveable { mutableStateOf(BottomTabRoute.Home.toSaveableName()) }
    val selectedTab = bottomTabFromSaveableName(selectedTabName.value)
    val selectTab: (BottomTabRoute) -> Unit = { tab ->
        selectedTabName.value = tab.toSaveableName()
    }
    val recipesRefreshKey = remember { mutableStateOf(0) }
    val targetTab by navigationCoordinator.targetTab.collectAsState()

    LaunchedEffect(targetTab) {
        if (targetTab == BottomTabRoute.Profile) {
            selectTab(BottomTabRoute.Profile)
            navigationCoordinator.clearTargetTab()
        }
    }

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
                bottomBar = {
                    if (!tabletLandscape) {
                        BottomTabBar(
                            activeTab = selectedTab,
                            onTabClick = selectTab,
                        )
                    }
                },
                containerColor = Transparent,
            ) { innerPadding ->
                Row(modifier = Modifier.padding(innerPadding)) {
                    if (tabletLandscape) {
                        AppDesktopSidebar(
                            activeTab = selectedTab,
                            onTabClick = selectTab,
                            onProfileClick = {
                                if (selectedTab != BottomTabRoute.Profile) {
                                    selectTab(BottomTabRoute.Profile)
                                }
                            },
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
                        onBack = { activeBackStack.popOne() },
                        entryProvider = homeTabEntries(
                            recipesBackStack = recipesBackStack,
                            recipesRefreshKey = recipesRefreshKey.value,
                            selectTab = selectTab,
                            onNavigateToManualInput = onNavigateToManualInput,
                            onNavigateToRecipeDetail = onNavigateToRecipeDetail,
                            onLogout = onLogout,
                        ),
                    )
                }
            }
        }
    }
}

private fun homeTabEntries(
    recipesBackStack: NavBackStack<NavKey>,
    recipesRefreshKey: Int,
    selectTab: (BottomTabRoute) -> Unit,
    onNavigateToManualInput: () -> Unit,
    onNavigateToRecipeDetail: (String) -> Unit,
    onLogout: () -> Unit,
) = entryProvider {
    entry<BottomTabRoute.Home> {
        HomeScreen(
            onAddPlan = { mealType ->
                openRecipesFromHome(
                    recipesBackStack = recipesBackStack,
                    mealType = mealType.name,
                    selectTab = selectTab,
                )
            },
            onNavigateToProfile = { selectTab(BottomTabRoute.Profile) },
        )
    }
    entry<BottomTabRoute.Recipes> {
        RecipesScreen(
            isFromHome = false,
            refreshTrigger = recipesRefreshKey,
            onNavigateToManualInput = onNavigateToManualInput,
            onNavigateToDetail = onNavigateToRecipeDetail,
        )
    }
    entry<RecipesInternalRoute.FromHome> { route ->
        RecipesScreen(
            isFromHome = true,
            initialMealType = route.mealType,
            refreshTrigger = recipesRefreshKey,
            onNavigateToManualInput = onNavigateToManualInput,
            onBack = { recipesBackStack.popOne() },
            onSaveSuccess = {
                closeRecipesFromHome(
                    recipesBackStack = recipesBackStack,
                    selectTab = selectTab,
                )
            },
        )
    }
    entry<BottomTabRoute.History> { HistoryScreen() }
    entry<BottomTabRoute.Profile> {
        ProfileScreen(
            refreshTrigger = recipesRefreshKey,
            onNavigateLogin = {
                selectTab(BottomTabRoute.Home)
                onLogout()
            },
        )
    }
}
