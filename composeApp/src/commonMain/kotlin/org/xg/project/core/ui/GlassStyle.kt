package org.xg.project.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

object GlassStyle {
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

    val SurfaceStrongGradient = Brush.verticalGradient(
        colors = listOf(
            Color.White.copy(alpha = 0.42f),
            Color.White.copy(alpha = 0.16f)
        )
    )
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
