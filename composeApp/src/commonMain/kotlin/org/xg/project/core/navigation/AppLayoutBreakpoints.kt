package org.xg.project.core.navigation

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

object AppLayoutBreakpoints {
    /** 手机竖屏上限；超过且横屏则进入平板布局 */
    val TabletMin = 720.dp

    /** 桌面完整壳（顶栏 + 侧栏 + 页脚） */
    val DesktopMin = 1100.dp
}

fun isTabletLandscape(maxWidth: Dp, maxHeight: Dp): Boolean =
    maxWidth > maxHeight && maxWidth >= AppLayoutBreakpoints.TabletMin

fun isDesktopWidth(maxWidth: Dp): Boolean =
    maxWidth >= AppLayoutBreakpoints.DesktopMin
