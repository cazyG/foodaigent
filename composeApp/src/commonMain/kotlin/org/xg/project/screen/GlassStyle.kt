package org.xg.project.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

object GlassStyle {
    val Accent = Color(0xFF95B6FF)
    val AccentStrong = Color(0xFF89A7FF)
    val TextPrimary = Color(0xFF475569)
    val TextSecondary = Color(0xFF64748B)
    val Danger = Color(0xFFFFA0B4)

    val BgGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFF2F7FF),
            Color(0xFFFAF4FF)
        )
    )

    val Surface = Color.White.copy(alpha = 0.18f)
    val SurfaceStrong = Color.White.copy(alpha = 0.26f)
    val Stroke = Color.White.copy(alpha = 0.52f)
    val StrokeSoft = Color.White.copy(alpha = 0.22f)
    val Highlight = Color.White.copy(alpha = 0.34f)

    val SurfaceGradient = Brush.verticalGradient(
        colors = listOf(
            Color.White.copy(alpha = 0.34f),
            Color.White.copy(alpha = 0.12f)
        )
    )

    val SurfaceStrongGradient = Brush.verticalGradient(
        colors = listOf(
            Color.White.copy(alpha = 0.42f),
            Color.White.copy(alpha = 0.16f)
        )
    )
}

fun Modifier.glassPanel(shape: RoundedCornerShape = RoundedCornerShape(24.dp)): Modifier {
    return this
        .shadow(
            elevation = 10.dp,
            shape = shape,
            ambientColor = Color.White.copy(alpha = 0.22f),
            spotColor = Color.Black.copy(alpha = 0.12f)
        )
        .clip(shape)
        .background(GlassStyle.Surface, shape)
        .background(GlassStyle.SurfaceGradient, shape)
        .border(1.dp, GlassStyle.Stroke, shape)
        .border(0.5.dp, GlassStyle.StrokeSoft, shape)
}

fun Modifier.glassPanelStrong(shape: RoundedCornerShape = RoundedCornerShape(24.dp)): Modifier {
    return this
        .shadow(
            elevation = 12.dp,
            shape = shape,
            ambientColor = Color.White.copy(alpha = 0.24f),
            spotColor = Color.Black.copy(alpha = 0.14f)
        )
        .clip(shape)
        .background(GlassStyle.SurfaceStrong, shape)
        .background(GlassStyle.SurfaceStrongGradient, shape)
        .border(1.dp, GlassStyle.Stroke, shape)
        .border(0.5.dp, GlassStyle.StrokeSoft, shape)
}

@Composable
fun GlassHighlight(content: @Composable BoxScope.() -> Unit) {
    Box {
        content()
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            GlassStyle.Highlight,
                            Color.Transparent
                        )
                    )
                )
        )
    }
}
