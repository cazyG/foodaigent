package org.xg.project

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.savedstate.serialization.SavedStateConfiguration
import androidx.savedstate.serialization.serializersModule
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import org.xg.project.Routes.AppRoute

// Creates the required serialization configuration for open polymorphism
private val config = SavedStateConfiguration {
    serializersModule = SerializersModule {
        polymorphic(Any::class) {
            subclass(AppRoute.Home::class, AppRoute.Home.serializer())
            subclass(AppRoute.Recipes::class, AppRoute.Recipes.serializer())
            subclass(AppRoute.Plan::class, AppRoute.Plan.serializer())
            subclass(AppRoute.History::class, AppRoute.History.serializer())
            subclass(AppRoute.Profile::class, AppRoute.Profile.serializer())
            subclass(AppRoute.ManualRecipeInput::class, AppRoute.ManualRecipeInput.serializer())
        }
    }
}

@Composable
fun rememberAppNavBackStack(initialDestination: AppRoute): SnapshotStateList<Any> {
    return rememberNavBackStack(config, initialDestination)
}

