package org.xg.project.Routes

sealed interface Routes {
    val id: String

    data object Home : Routes {
        override val id: String = "home"
    }

    data object Recipes : Routes {
        override val id: String = "recipes"
    }

    data object Plan : Routes {
        override val id: String = "plan"
    }

    data object History : Routes {
        override val id: String = "history"
    }

    data object Profile : Routes {
        override val id: String = "profile"
    }

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
