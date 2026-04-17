package org.xg.project.Routes

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import androidx.navigation3.runtime.NavKey

@Serializable
sealed class AppRoute : NavKey {
    @Serializable
    @SerialName("main")
    data object Main : AppRoute()
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
    @SerialName("manual_recipe_input")
    data object ManualRecipeInput : RecipesInternalRoute()
}
