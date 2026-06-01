package org.xg.project.presentation.navigation

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

sealed interface AppNavigationEvent {
    data object OpenProfileTab : AppNavigationEvent
}

class AppNavigationCoordinator {
    private val _events = MutableSharedFlow<AppNavigationEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<AppNavigationEvent> = _events.asSharedFlow()

    fun openProfileTab() {
        _events.tryEmit(AppNavigationEvent.OpenProfileTab)
    }
}
