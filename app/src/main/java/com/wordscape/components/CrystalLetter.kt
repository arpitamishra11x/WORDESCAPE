package com.wordscape.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.wordscape.motion.floatAndRotate
import com.wordscape.motion.pulsingGlow
import kotlin.math.sin

@Composable
fun CrystalLetter(
    letter: Char = 'A',
    size: Dp = 200.dp,
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "crystalRefraction")
    
    // Shifting reflection/rainbow angle
    val sweepProgress by transition.animateFloat(
        initialValue = -100f,
        targetValue = 300f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sweep"
    )

    Box(
        modifier = modifier
            .size(size)
            .pulsingGlow(
                color = Color(0xFFA78BFA),
                minAlpha = 0.2f,
                maxAlpha = 0.5f,
                duration = 3000,
                radius = 32.dp
            )
            .floatAndRotate(amplitude = 12f, maxDegrees = 4f)
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val w = this.size.width
            val h = this.size.height
            val cx = w / 2f
            val cy = h / 2f
            val scale = w / 200f
            
            // Draw A letter crystal body paths
            // Outer triangle
            val outerPath = Path().apply {
                moveTo(cx, cy - 80f * scale)
                lineTo(cx - 60f * scale, cy + 70f * scale)
                lineTo(cx - 30f * scale, cy + 70f * scale)
                lineTo(cx - 10f * scale, cy + 20f * scale)
                lineTo(cx + 10f * scale, cy + 20f * scale)
                lineTo(cx + 30f * scale, cy + 70f * scale)
                lineTo(cx + 60f * scale, cy + 70f * scale)
                close()
            }

            // Inner hole
            val innerPath = Path().apply {
                moveTo(cx, cy - 35f * scale)
                lineTo(cx - 15f * scale, cy + 5f * scale)
                lineTo(cx + 15f * scale, cy + 5f * scale)
                close()
            }

            // 1. Draw base refraction background (lavender reflection)
            drawPath(
                path = outerPath,
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFFC4B5FD).copy(alpha = 0.6f), Color(0xFFA78BFA).copy(alpha = 0.2f)),
                    center = Offset(cx, cy),
                    radius = 120f * scale
                )
            )

            // 2. Draw moving reflection highlight streak
            val highlightBrush = Brush.linearGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.0f),
                    Color.White.copy(alpha = 0.8f),
                    Color(0xFFF9A8D4).copy(alpha = 0.4f), // SunsetPink reflection
                    Color.White.copy(alpha = 0.0f)
                ),
                start = Offset(sweepProgress - 80f, sweepProgress - 80f),
                end = Offset(sweepProgress + 80f, sweepProgress + 80f)
            )
            drawPath(path = outerPath, brush = highlightBrush)

            // 3. Draw glass border details (refraction edge highlights)
            drawPath(
                path = outerPath,
                color = Color.White.copy(alpha = 0.7f),
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3.5f * scale)
            )
            drawPath(
                path = innerPath,
                color = Color.White.copy(alpha = 0.5f),
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.5f * scale)
            )
            
            // Mask/Draw the inner hole to clear the A shape
            drawPath(
                path = innerPath,
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF4C1D95).copy(alpha = 0.8f), Color(0xFFF97316).copy(alpha = 0.6f))
                )
            )
            
            // Draw a subtle rainbow glare on the left edge
            drawLine(
                brush = Brush.verticalGradient(listOf(Color(0xFF7DD3FC), Color(0xFFF9A8D4))),
                start = Offset(cx - 40f * scale, cy + 20f * scale),
                end = Offset(cx - 55f * scale, cy + 60f * scale),
                strokeWidth = 2.5f
            )
        }
    }
}
