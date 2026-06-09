package org.xg.project.presentation.navigation

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import org.xg.project.Routes.BottomTabRoute

sealed interface AppNavigationEvent {
    data object OpenProfileTab : AppNavigationEvent
    data object RefreshRecipes : AppNavigationEvent
}

class AppNavigationCoordinator {
    private val _events = MutableSharedFlow<AppNavigationEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<AppNavigationEvent> = _events.asSharedFlow()

    private val _targetTab = MutableStateFlow<BottomTabRoute?>(null)
    val targetTab: StateFlow<BottomTabRoute?> = _targetTab.asStateFlow()

    fun openProfileTab() {
        _targetTab.value = BottomTabRoute.Profile
        _events.tryEmit(AppNavigationEvent.OpenProfileTab)
    }

    fun clearTargetTab() {
        _targetTab.value = null
    }

    fun refreshRecipes() {
        _events.tryEmit(AppNavigationEvent.RefreshRecipes)
    }
}
