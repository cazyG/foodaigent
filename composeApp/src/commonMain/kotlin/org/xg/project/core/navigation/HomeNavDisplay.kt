package org.xg.project.core.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.ExperimentalMaterial3AdaptiveNavigationSuiteApi
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuite
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldLayout
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import org.koin.compose.koinInject
import org.xg.project.core.ui.GlassStyle
import org.xg.project.feature.history.HistoryScreen
import org.xg.project.feature.home.HomeScreen
import org.xg.project.feature.profile.ProfileScreen
import org.xg.project.feature.recipes.RecipesScreen

private val AppNavigationMediumRailWidth = 80.dp
private val AppNavigationExpandedRailWidth = 140.dp
private val AppNavigationMediumItemHorizontalPadding = 10.dp
private val AppNavigationExpandedItemHorizontalPadding = 18.dp

@OptIn(ExperimentalMaterial3AdaptiveNavigationSuiteApi::class)
@Composable
internal fun HomeNavDisplay(
    onNavigateToManualInput: () -> Unit,
    onNavigateToRecipeDetail: (String) -> Unit,
    onLogout: () -> Unit,
) {
    val navigationCoordinator = koinInject<AppNavigationCoordinator>()
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

    Box(modifier = Modifier.fillMaxSize()) {
        val adaptiveLayout = currentAppAdaptiveLayout()
        val navigationSuiteType =
            if (adaptiveLayout == AppAdaptiveLayout.Compact) {
                NavigationSuiteType.NavigationBar
            } else {
                NavigationSuiteType.NavigationRail
            }
        val useSideNavigation = navigationSuiteType != NavigationSuiteType.NavigationBar
        val navigationRailWidth = adaptiveLayout.navigationRailWidth()

        Box(
            modifier = Modifier
                .fillMaxSize()
                .then(
                    if (useSideNavigation) {
                        Modifier.background(Color.White)
                    } else {
                        Modifier.background(GlassStyle.BgGradient)
                    },
                ),
        ) {
            NavigationSuiteScaffoldLayout(
                navigationSuite = {
                    NavigationSuite(
                        layoutType = navigationSuiteType,
                        modifier = if (useSideNavigation) {
                            Modifier
                                .width(navigationRailWidth)
                                .padding(vertical = 8.dp)
                        } else {
                            Modifier
                        },
                    ) {
                        appNavigationSuiteItems(
                            selectedTab = selectedTab,
                            selectTab = selectTab,
                            adaptiveLayout = adaptiveLayout,
                        )
                    }
                },
                layoutType = navigationSuiteType,
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            start = if (useSideNavigation) 12.dp else 0.dp,
                            end = if (useSideNavigation) 12.dp else 0.dp,
                        ),
                ) {
                    NavDisplay(
                        backStack = activeBackStack,
                        modifier = Modifier.fillMaxSize(),
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

@OptIn(ExperimentalMaterial3AdaptiveNavigationSuiteApi::class)
private fun androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScope.appNavigationSuiteItems(
    selectedTab: BottomTabRoute,
    selectTab: (BottomTabRoute) -> Unit,
    adaptiveLayout: AppAdaptiveLayout,
) {
    appTabNavItems.forEach { item ->
        item(
            selected = selectedTab == item.route,
            onClick = { selectTab(item.route) },
            icon = {
                Icon(item.icon, contentDescription = null)
            },
            label = {
                Text(item.label)
            },
            modifier = if (adaptiveLayout == AppAdaptiveLayout.Compact) {
                Modifier
            } else {
                Modifier.padding(
                    horizontal = adaptiveLayout.navigationItemHorizontalPadding(),
                    vertical = 4.dp,
                )
            },
        )
    }
}

private fun AppAdaptiveLayout.navigationRailWidth(): Dp = when (this) {
    AppAdaptiveLayout.Compact -> 0.dp
    AppAdaptiveLayout.Medium -> AppNavigationMediumRailWidth
    AppAdaptiveLayout.Expanded -> AppNavigationExpandedRailWidth
}

private fun AppAdaptiveLayout.navigationItemHorizontalPadding(): Dp = when (this) {
    AppAdaptiveLayout.Compact -> 0.dp
    AppAdaptiveLayout.Medium -> AppNavigationMediumItemHorizontalPadding
    AppAdaptiveLayout.Expanded -> AppNavigationExpandedItemHorizontalPadding
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
