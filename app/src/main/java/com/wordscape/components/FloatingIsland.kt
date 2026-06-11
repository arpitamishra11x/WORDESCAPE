package com.wordscape.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.dp
import com.wordscape.motion.floatAndRotate

@Composable
fun FloatingIsland(
    modifier: Modifier = Modifier,
    scale: Float = 1f,
    hasWaterfall: Boolean = true,
    hasVegetation: Boolean = true
) {
    Box(
        modifier = modifier
            .size((300 * scale).dp, (250 * scale).dp)
            .floatAndRotate()
    ) {
        Canvas(modifier = Modifier.size((300 * scale).dp, (250 * scale).dp)) {
            val w = size.width
            val h = size.height
            val cx = w / 2f
            val cy = h * 0.45f
            
            // 1. Rock bottom dirt layer
            val dirtPath = Path().apply {
                moveTo(cx - 100f * scale, cy)
                lineTo(cx + 100f * scale, cy)
                quadraticTo(cx + 60f * scale, cy + 50f * scale, cx, cy + 80f * scale)
                quadraticTo(cx - 60f * scale, cy + 50f * scale, cx - 100f * scale, cy)
                close()
            }
            drawPath(
                path = dirtPath,
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF5B3E31), Color(0xFF3B2314))
                )
            )

            // Rocks detailing
            drawCircle(Color(0xFF78716C), 8f * scale, Offset(cx - 30f * scale, cy + 40f * scale))
            drawCircle(Color(0xFF57534E), 12f * scale, Offset(cx + 20f * scale, cy + 30f * scale))

            // 2. Green grass layer
            val grassPath = Path().apply {
                moveTo(cx - 110f * scale, cy)
                quadraticTo(cx, cy - 20f * scale, cx + 110f * scale, cy)
                lineTo(cx + 110f * scale, cy + 10f * scale)
                lineTo(cx - 110f * scale, cy + 10f * scale)
                close()
            }
            drawPath(
                path = grassPath,
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF86EFAC), Color(0xFF22C55E))
                )
            )

            // 3. Waterfall
            if (hasWaterfall) {
                val flowBrush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF38BDF8),
                        Color(0xFF0EA5E9),
                        Color(0xFF0284C7).copy(alpha = 0.1f)
                    )
                )
                drawRect(
                    brush = flowBrush,
                    topLeft = Offset(cx - 15f * scale, cy + 5f * scale),
                    size = Size(30f * scale, 90f * scale)
                )
            }

            // 4. Vegetation (draw some small trees/bushes)
            if (hasVegetation) {
                // Left tree
                val lx = cx - 50f * scale
                val ly = cy - 8f * scale
                // Trunk
                drawRoundRect(
                    color = Color(0xFF78350F),
                    topLeft = Offset(lx - 4f * scale, ly - 20f * scale),
                    size = Size(8f * scale, 20f * scale),
                    cornerRadius = CornerRadius(2f)
                )
                // Leaves
                drawCircle(Color(0xFF047857), 18f * scale, Offset(lx, ly - 26f * scale))
                drawCircle(Color(0xFF059669), 12f * scale, Offset(lx - 10f * scale, ly - 20f * scale))
                drawCircle(Color(0xFF059669), 12f * scale, Offset(lx + 10f * scale, ly - 20f * scale))

                // Right small magical bush
                val rx = cx + 60f * scale
                val ry = cy - 4f * scale
                drawCircle(Color(0xFFA78BFA), 10f * scale, Offset(rx, ry - 10f * scale))
                drawCircle(Color(0xFFC4B5FD), 8f * scale, Offset(rx - 8f * scale, ry - 6f * scale))
            }
        }
    }
}
