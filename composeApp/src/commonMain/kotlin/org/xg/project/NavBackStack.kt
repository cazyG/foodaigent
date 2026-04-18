package org.xg.project

import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation3.runtime.NavBackStack
import androidx.savedstate.serialization.SavedStateConfiguration
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import org.xg.project.Routes.AppRoute
import org.xg.project.Routes.BottomTabRoute

val appSavedStateConfiguration = SavedStateConfiguration {
    serializersModule = SerializersModule {
        polymorphic(NavKey::class) {
            subclass(AppRoute.Login::class, AppRoute.Login.serializer())
            subclass(AppRoute.Home::class, AppRoute.Home.serializer())
            subclass(AppRoute.ManualRecipeInput::class, AppRoute.ManualRecipeInput.serializer())
            subclass(BottomTabRoute.Home::class, BottomTabRoute.Home.serializer())
            subclass(BottomTabRoute.Recipes::class, BottomTabRoute.Recipes.serializer())
            subclass(BottomTabRoute.History::class, BottomTabRoute.History.serializer())
            subclass(BottomTabRoute.Profile::class, BottomTabRoute.Profile.serializer())
        }
    }
}

@Composable
fun rememberAppNavBackStack(vararg initialDestinations: NavKey): NavBackStack<NavKey> {
    return rememberNavBackStack(appSavedStateConfiguration, *initialDestinations)
}
