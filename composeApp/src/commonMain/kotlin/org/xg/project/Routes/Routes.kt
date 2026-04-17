package org.xg.project.Routes

import kotlinx.serialization.Serializable

sealed interface Routes {
    val id: String

    @Serializable
    data object Home : Routes {
        override val id: String = "home"
    }

    @Serializable
    data object Recipes : Routes {
        override val id: String = "recipes"
    }

    @Serializable
    data object Plan : Routes {
        override val id: String = "plan"
    }

    @Serializable
    data object History : Routes {
        override val id: String = "history"
    }

    @Serializable
    data object Profile : Routes {
        override val id: String = "profile"
    }

    @Serializable
    data object ManualRecipeInput : Routes {
        override val id: String = "manual_recipe_input"
    }

    companion object {
        val tabRoots: List<Routes> = listOf(Home, Recipes, History, Profile)

        fun fromId(id: String): Routes? {
            return when (id) {
                Home.id -> Home
                Recipes.id -> Recipes
                Plan.id -> Plan
                History.id -> History
                Profile.id -> Profile
                ManualRecipeInput.id -> ManualRecipeInput
                else -> null
            }
        }
    }
}
