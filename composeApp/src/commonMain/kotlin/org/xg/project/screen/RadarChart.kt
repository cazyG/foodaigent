package org.xg.project.screen

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun RadarChart(
    data: Map<String, Float>,
    modifier: Modifier = Modifier,
    gridColor: Color = Color.LightGray,
    dataColor: Color = Color(0xFFF59E42)
) {
    val labels = data.keys.toList()
    val values = data.values.toList()

    val textMeasurer = rememberTextMeasurer()
    val textStyle = TextStyle(fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Medium)

    Canvas(modifier = modifier) {
        val center = Offset(size.width / 2, size.height / 2)
        val radius = minOf(size.width, size.height) / 2 * 0.75f // 留出边缘显示文字的空间
        val sides = labels.size
        val angleStep = (2 * PI / sides).toFloat()

        // 绘制背景网格（例如 4 圈）
        val levels = 4
        for (level in 1..levels) {
            val currentRadius = radius * (level.toFloat() / levels)
            val path = Path()
            for (i in 0 until sides) {
                val angle = i * angleStep - PI.toFloat() / 2 // 从顶部开始（-90度）
                val x = center.x + currentRadius * cos(angle)
                val y = center.y + currentRadius * sin(angle)
                if (i == 0) {
                    path.moveTo(x, y)
                } else {
                    path.lineTo(x, y)
                }
            }
            path.close()
            drawPath(
                path = path,
                color = gridColor,
                style = Stroke(width = 1.dp.toPx())
            )
        }

        // 绘制轴线和标签
        for (i in 0 until sides) {
            val angle = i * angleStep - PI.toFloat() / 2
            val x = center.x + radius * cos(angle)
            val y = center.y + radius * sin(angle)
            drawLine(
                color = gridColor,
                start = center,
                end = Offset(x, y),
                strokeWidth = 1.dp.toPx()
            )

            // 绘制标签文字
            val label = labels.getOrElse(i) { "" }
            val textLayoutResult = textMeasurer.measure(label, textStyle)
            // 文字稍微往外偏一点
            val labelX = center.x + (radius * 1.25f) * cos(angle) - textLayoutResult.size.width / 2
            val labelY = center.y + (radius * 1.25f) * sin(angle) - textLayoutResult.size.height / 2
            drawText(
                textMeasurer = textMeasurer,
                text = label,
                style = textStyle,
                topLeft = Offset(labelX, labelY)
            )
        }

        // 绘制数据多边形
        if (values.isNotEmpty()) {
            val dataPath = Path()
            for (i in 0 until sides) {
                val value = values.getOrElse(i) { 0f }.coerceIn(0f, 1f)
                val angle = i * angleStep - PI.toFloat() / 2
                val x = center.x + radius * value * cos(angle)
                val y = center.y + radius * value * sin(angle)
                if (i == 0) {
                    dataPath.moveTo(x, y)
                } else {
                    dataPath.lineTo(x, y)
                }
            }
            dataPath.close()
            // 填充半透明颜色
            drawPath(
                path = dataPath,
                color = dataColor.copy(alpha = 0.3f)
            )
            // 绘制数据边缘线
            drawPath(
                path = dataPath,
                color = dataColor,
                style = Stroke(width = 2.dp.toPx())
            )
        }
    }
}