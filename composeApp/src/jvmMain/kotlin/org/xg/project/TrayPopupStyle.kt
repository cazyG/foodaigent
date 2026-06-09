package org.xg.project

import java.awt.Color
import java.awt.Component
import java.awt.Cursor
import java.awt.Dimension
import java.awt.Font
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.Insets
import java.awt.RenderingHints
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.BorderFactory
import javax.swing.BoxLayout
import javax.swing.JButton
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JPopupMenu
import javax.swing.border.AbstractBorder

internal object TrayPopupStyle {
    val background = Color(0xFF, 0xFF, 0xFF)
    val text = Color(0x33, 0x33, 0x33)
    val hover = Color(0xF2, 0xF2, 0xF2)
    const val cornerRadius = 10
    const val horizontalPadding = 18
    const val verticalPadding = 10
    const val menuWidth = 132
}

internal class RoundedPopupBorder(
    private val radius: Int = TrayPopupStyle.cornerRadius,
    private val background: Color = TrayPopupStyle.background,
) : AbstractBorder() {
    override fun paintBorder(
        component: Component,
        graphics: Graphics,
        x: Int,
        y: Int,
        width: Int,
        height: Int,
    ) {
        val g2 = graphics.create() as Graphics2D
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        g2.color = background
        g2.fillRoundRect(x, y, width - 1, height - 1, radius, radius)
        g2.color = Color(0, 0, 0, 18)
        g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius)
        g2.dispose()
    }

    override fun getBorderInsets(component: Component): Insets =
        Insets(6, 0, 6, 0)
}

internal fun createModernTrayPopup(
    font: Font,
    onOpen: () -> Unit,
    onSettings: () -> Unit,
    onFeedback: () -> Unit,
    onExit: () -> Unit,
): JPopupMenu {
    val popup = JPopupMenu().apply {
        isBorderPainted = true
        border = RoundedPopupBorder()
        isOpaque = false
    }

    fun dismissPopup() {
        popup.isVisible = false
    }

    val panel = JPanel().apply {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)
        isOpaque = false
        border = BorderFactory.createEmptyBorder(4, 0, 4, 0)
        background = TrayPopupStyle.background
        add(createTrayMenuButton("打开", font, ::dismissPopup, onOpen))
        add(createTrayMenuButton("设置", font, ::dismissPopup, onSettings))
        add(createTrayMenuButton("反馈", font, ::dismissPopup, onFeedback))
        add(createTrayMenuButton("退出", font, ::dismissPopup, onExit))
    }

    popup.add(panel)
    return popup
}

private fun createTrayMenuButton(
    label: String,
    font: Font,
    onDismiss: () -> Unit,
    onClick: () -> Unit,
): JButton {
    return JButton(label).apply {
        this.font = font
        foreground = TrayPopupStyle.text
        isFocusPainted = false
        isBorderPainted = false
        isContentAreaFilled = false
        isOpaque = true
        background = TrayPopupStyle.background
        alignmentX = JComponent.LEFT_ALIGNMENT
        maximumSize = Dimension(TrayPopupStyle.menuWidth, 36)
        preferredSize = Dimension(TrayPopupStyle.menuWidth, 36)
        minimumSize = Dimension(TrayPopupStyle.menuWidth, 36)
        border = BorderFactory.createEmptyBorder(
            TrayPopupStyle.verticalPadding,
            TrayPopupStyle.horizontalPadding,
            TrayPopupStyle.verticalPadding,
            TrayPopupStyle.horizontalPadding,
        )
        cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        horizontalAlignment = JButton.LEFT

        addActionListener {
            onDismiss()
            onClick()
        }

        addMouseListener(
            object : MouseAdapter() {
                override fun mouseEntered(event: MouseEvent) {
                    background = TrayPopupStyle.hover
                }

                override fun mouseExited(event: MouseEvent) {
                    background = TrayPopupStyle.background
                }
            },
        )
    }
}
