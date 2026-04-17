package org.xg.project

import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.savedstate.serialization.SavedStateConfiguration
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import org.jetbrains.androidx.navigation3.runtime.NavKey
import org.jetbrains.androidx.navigation3.runtime.rememberNavBackStack
import org.xg.project.Routes.AppRoute
import org.xg.project.Routes.BottomTabRoute
import org.xg.project.Routes.HomeInternalRoute
import org.xg.project.Routes.SearchInternalRoute

val appSavedStateConfiguration = SavedStateConfiguration {
    serializersModule = SerializersModule {
        polymorphic(NavKey::class) {
            subclass(AppRoute.Main::class, AppRoute.Main.serializer())
            subclass(BottomTabRoute.Home::class, BottomTabRoute.Home.serializer())
            subclass(BottomTabRoute.Search::class, BottomTabRoute.Search.serializer())
            subclass(BottomTabRoute.Profile::class, BottomTabRoute.Profile.serializer())
            subclass(HomeInternalRoute.List::class, HomeInternalRoute.List.serializer())
            subclass(HomeInternalRoute.Detail::class, HomeInternalRoute.Detail.serializer())
            subclass(SearchInternalRoute.Main::class, SearchInternalRoute.Main.serializer())
        }
    }
}

@Composable
fun rememberAppNavBackStack(vararg initialDestinations: NavKey): SnapshotStateList<NavKey> {
    return rememberNavBackStack(appSavedStateConfiguration, *initialDestinations)
}
