package org.xg.project.core.navigation

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import androidx.navigation3.runtime.NavKey

@Serializable
sealed class AppRoute : NavKey {
    @Serializable
    @SerialName("login")
    data object Login : AppRoute()

    @Serializable
    @SerialName("home")
    data object Home : AppRoute()

    @Serializable
    @SerialName("manual_recipe_input")
    data object ManualRecipeInput : AppRoute()

    @Serializable
    @SerialName("recipe_detail")
    data class RecipeDetail(val id: String) : AppRoute()
}

@Serializable
sealed class BottomTabRoute : NavKey {
    @Serializable
    @SerialName("tab_home")
    data object Home : BottomTabRoute()

    @Serializable
    @SerialName("tab_recipes")
    data object Recipes : BottomTabRoute()

    @Serializable
    @SerialName("tab_history")
    data object History : BottomTabRoute()

    @Serializable
    @SerialName("tab_profile")
    data object Profile : BottomTabRoute()
}

@Serializable
sealed class RecipesInternalRoute : NavKey {
    @Serializable
    @SerialName("recipes_from_home")
    data class FromHome(val mealType: String) : RecipesInternalRoute()
}

fun BottomTabRoute.toSaveableName(): String = when (this) {
    BottomTabRoute.Home -> "tab_home"
    BottomTabRoute.Recipes -> "tab_recipes"
    BottomTabRoute.History -> "tab_history"
    BottomTabRoute.Profile -> "tab_profile"
}

fun bottomTabFromSaveableName(name: String): BottomTabRoute = when (name) {
    "tab_recipes" -> BottomTabRoute.Recipes
    "tab_history" -> BottomTabRoute.History
    "tab_profile" -> BottomTabRoute.Profile
    else -> BottomTabRoute.Home
}
