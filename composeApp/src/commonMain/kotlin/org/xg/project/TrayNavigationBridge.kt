package org.xg.project

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * 桌面端系统托盘菜单与 Compose 应用之间的导航桥接。
 * 通过 Flow 将托盘点击事件派发到 Compose 协程中处理，避免跨线程更新导航状态。
 */
object TrayNavigationBridge {
    private val _openSettingsRequests = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val openSettingsRequests: SharedFlow<Unit> = _openSettingsRequests.asSharedFlow()

    fun requestOpenSettings() {
        _openSettingsRequests.tryEmit(Unit)
    }
}
