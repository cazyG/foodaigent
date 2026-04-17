package org.xg.project.Routes

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class AppRoute {
    @Serializable @SerialName("home")
    data object Home : AppRoute()

    @Serializable @SerialName("recipes")
    data object Recipes : AppRoute()

    @Serializable @SerialName("plan")
    data object Plan : AppRoute()

    @Serializable @SerialName("history")
    data object History : AppRoute()

    @Serializable @SerialName("profile")
    data object Profile : AppRoute()

    @Serializable @SerialName("manual_recipe_input")
    data object ManualRecipeInput : AppRoute()

    companion object {
        val tabRoots: List<AppRoute> = listOf(Home, Recipes, History, Profile)
    }
}
