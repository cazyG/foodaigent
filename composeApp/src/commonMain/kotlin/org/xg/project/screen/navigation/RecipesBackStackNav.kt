package org.xg.project.screen.navigation

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import org.xg.project.Routes.BottomTabRoute
import org.xg.project.Routes.RecipesInternalRoute

internal fun openRecipesFromHome(
    recipesBackStack: NavBackStack<NavKey>,
    mealType: String,
    selectTab: (BottomTabRoute) -> Unit,
) {
    if (recipesBackStack.isEmpty()) {
        recipesBackStack.add(BottomTabRoute.Recipes)
    }
    while (recipesBackStack.lastOrNull() is RecipesInternalRoute.FromHome) {
        recipesBackStack.removeAt(recipesBackStack.lastIndex)
    }
    if (recipesBackStack.lastOrNull() !is BottomTabRoute.Recipes) {
        recipesBackStack.add(BottomTabRoute.Recipes)
    }
    recipesBackStack.add(RecipesInternalRoute.FromHome(mealType))
    selectTab(BottomTabRoute.Recipes)
}

internal fun closeRecipesFromHome(
    recipesBackStack: NavBackStack<NavKey>,
    selectTab: (BottomTabRoute) -> Unit,
) {
    selectTab(BottomTabRoute.Home)
    if (recipesBackStack.lastOrNull() is RecipesInternalRoute.FromHome) {
        recipesBackStack.removeAt(recipesBackStack.lastIndex)
    }
    if (recipesBackStack.isEmpty()) {
        recipesBackStack.add(BottomTabRoute.Recipes)
    }
}
