package org.xg.project.Routes

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.androidx.navigation3.runtime.NavKey

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
    @SerialName("tab_search")
    data object Search : BottomTabRoute()

    @Serializable
    @SerialName("tab_profile")
    data object Profile : BottomTabRoute()
}

@Serializable
sealed class HomeInternalRoute : NavKey {
    @Serializable
    @SerialName("home_list")
    data object List : HomeInternalRoute()

    @Serializable
    @SerialName("home_detail")
    data class Detail(val id: Int) : HomeInternalRoute()
}

@Serializable
sealed class SearchInternalRoute : NavKey {
    @Serializable
    @SerialName("search_main")
    data object Main : SearchInternalRoute()
}
