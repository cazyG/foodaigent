package org.xg.project.core.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Badge
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.ExperimentalMaterial3AdaptiveNavigationSuiteApi
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import org.koin.compose.koinInject
import org.xg.project.feature.history.HistoryScreen
import org.xg.project.feature.home.HomeScreen
import org.xg.project.feature.home.HomeColors
import org.xg.project.feature.profile.ProfileScreen
import org.xg.project.feature.recipes.RecipesScreen

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

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            navigationItems(
                selectedTab = selectedTab,
                selectTab = selectTab,
            )
        },
        layoutType = currentAppAdaptiveLayout().navigationSuiteType(),
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

@OptIn(ExperimentalMaterial3AdaptiveNavigationSuiteApi::class)
private fun NavigationSuiteScope.navigationItems(
    selectedTab: BottomTabRoute,
    selectTab: (BottomTabRoute) -> Unit,
) {
    appTabNavItems.forEach { item ->
        val selected = selectedTab == item.route
        item(
            selected = selected,
            onClick = { selectTab(item.route) },
            icon = {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.label,
                    tint = if (selected) HomeColors.BrandBrown else HomeColors.TextSecondary,
                )
            },
            label = {
                Text(
                    text = item.label,
                    color = if (selected) HomeColors.TextPrimary else HomeColors.TextSecondary,
                    fontSize = 15.sp,
                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                )
            },
            badge = { Badge { Text("+99") } },
        )
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
