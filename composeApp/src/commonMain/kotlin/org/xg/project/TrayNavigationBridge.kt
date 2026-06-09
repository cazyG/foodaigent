package org.xg.project

/**
 * 桌面端系统托盘菜单与 Compose 应用之间的导航桥接。
 */
object TrayNavigationBridge {
    var onOpenSettings: (() -> Unit)? = null
    var onOpenFeedback: (() -> Unit)? = null
}
