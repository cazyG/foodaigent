package org.xg.project

import java.awt.Desktop
import java.awt.Font
import java.awt.Image
import java.awt.SystemTray
import java.awt.TrayIcon
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.image.BufferedImage
import java.net.URI
import javax.imageio.ImageIO
import javax.swing.SwingUtilities
import org.xg.project.core.navigation.TrayNavigationBridge

/**
 * 使用 Swing [javax.swing.JPopupMenu] 代替 Compose Tray 的 AWT PopupMenu，
 * 避免 Windows 系统托盘菜单中文显示为方块，并提供圆角现代化样式。
 */
class JvmSystemTrayController internal constructor(
    private val trayIcon: TrayIcon,
) {
    fun dispose() {
        if (!SystemTray.isSupported()) return
        runCatching {
            SystemTray.getSystemTray().remove(trayIcon)
        }
    }

    companion object {
        private const val ICON_RESOURCE =
            "composeResources/aigent.composeapp.generated.resources/drawable/app_icon.png"
        private const val FEEDBACK_URL = "https://github.com/cazyG/foodaigent/issues"

        fun install(
            tooltip: String,
            onShowWindow: () -> Unit,
            onExit: () -> Unit,
        ): JvmSystemTrayController? {
            if (!SystemTray.isSupported()) return null

            var controller: JvmSystemTrayController? = null
            val installAction = Runnable {
                controller = installOnEdt(
                    tooltip = tooltip,
                    onShowWindow = onShowWindow,
                    onExit = onExit,
                )
            }

            if (SwingUtilities.isEventDispatchThread()) {
                installAction.run()
            } else {
                SwingUtilities.invokeAndWait(installAction)
            }
            return controller
        }

        private fun installOnEdt(
            tooltip: String,
            onShowWindow: () -> Unit,
            onExit: () -> Unit,
        ): JvmSystemTrayController? {
            val trayImage = loadTrayImage() ?: return null
            val menuFont = preferredChineseMenuFont()
            val popup = createModernTrayPopup(
                font = menuFont,
                onOpen = onShowWindow,
                onSettings = {
                    onShowWindow()
                    TrayNavigationBridge.requestOpenSettings()
                },
                onFeedback = {
                    openFeedbackInBrowser()
                },
                onExit = onExit,
            )

            val trayIcon = TrayIcon(trayImage, tooltip).apply {
                isImageAutoSize = true
                addMouseListener(
                    object : MouseAdapter() {
                        override fun mouseClicked(event: MouseEvent) {
                            if (event.button == MouseEvent.BUTTON1 && event.clickCount >= 2) {
                                onShowWindow()
                            }
                        }

                        override fun mousePressed(event: MouseEvent) {
                            showPopupIfNeeded(event)
                        }

                        override fun mouseReleased(event: MouseEvent) {
                            showPopupIfNeeded(event)
                        }

                        private fun showPopupIfNeeded(event: MouseEvent) {
                            if (!event.isPopupTrigger) return
                            popup.show(event.component, event.x, event.y)
                        }
                    },
                )
            }

            return runCatching {
                SystemTray.getSystemTray().add(trayIcon)
                JvmSystemTrayController(trayIcon = trayIcon)
            }.getOrNull()
        }

        private fun openFeedbackInBrowser() {
            runCatching {
                if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                    Desktop.getDesktop().browse(URI(FEEDBACK_URL))
                }
            }
        }

        private fun loadTrayImage(): Image? {
            val source = readAppIcon() ?: return null
            return scaleForTray(source)
        }

        private fun readAppIcon(): BufferedImage? {
            val classLoader = JvmSystemTrayController::class.java.classLoader
            val stream = classLoader.getResourceAsStream(ICON_RESOURCE)
                ?: Thread.currentThread().contextClassLoader.getResourceAsStream(ICON_RESOURCE)
            return stream?.use { ImageIO.read(it) }
        }

        private fun scaleForTray(source: BufferedImage, size: Int = 16): Image {
            val scaled = BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB)
            val graphics = scaled.createGraphics()
            graphics.drawImage(
                source.getScaledInstance(size, size, Image.SCALE_SMOOTH),
                0,
                0,
                null,
            )
            graphics.dispose()
            return scaled
        }

        private fun preferredChineseMenuFont(): Font {
            val os = System.getProperty("os.name").orEmpty().lowercase()
            val fontName = when {
                os.contains("win") -> "Microsoft YaHei UI"
                os.contains("mac") -> "PingFang SC"
                else -> "Noto Sans CJK SC"
            }
            return Font(fontName, Font.PLAIN, 13)
        }
    }
}
