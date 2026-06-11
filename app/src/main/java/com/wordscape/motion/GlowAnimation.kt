package com.wordscape.motion

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun rememberGlowAlpha(
    minAlpha: Float = 0.2f,
    maxAlpha: Float = 0.6f,
    duration: Int = 2000,
    reducedMotion: Boolean = false
): Float {
    if (reducedMotion) return maxAlpha
    val transition = rememberInfiniteTransition(label = "glowAlpha")
    val alpha by transition.animateFloat(
        initialValue = minAlpha,
        targetValue = maxAlpha,
        animationSpec = infiniteRepeatable(
            animation = tween(duration, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )
    return alpha
}

fun Modifier.softGlow(
    color: Color,
    radius: Dp = 16.dp
): Modifier = this.drawBehind {
    val paint = Paint().asFrameworkPaint().apply {
        isAntiAlias = true
        this.color = color.toArgb()
        // Simple shadow-based blur for glow
        setShadowLayer(radius.toPx(), 0f, 0f, color.toArgb())
    }
    drawIntoCanvas { canvas ->
        canvas.drawCircle(
            center = center,
            radius = size.minDimension / 2f,
            paint = androidx.compose.ui.graphics.Paint().apply {
                asFrameworkPaint().set(paint)
            }
        )
    }
}

@Composable
fun Modifier.pulsingGlow(
    color: Color,
    minAlpha: Float = 0.2f,
    maxAlpha: Float = 0.7f,
    duration: Int = 2000,
    radius: Dp = 24.dp,
    reducedMotion: Boolean = false
): Modifier {
    val alpha = rememberGlowAlpha(minAlpha, maxAlpha, duration, reducedMotion)
    return this.drawBehind {
        val paint = Paint().asFrameworkPaint().apply {
            isAntiAlias = true
            this.color = color.copy(alpha = alpha).toArgb()
            setShadowLayer(radius.toPx(), 0f, 0f, color.copy(alpha = alpha).toArgb())
        }
        drawIntoCanvas { canvas ->
            canvas.drawCircle(
                center = center,
                radius = size.minDimension / 2f + (radius.toPx() * alpha * 0.2f),
                paint = androidx.compose.ui.graphics.Paint().apply {
                    asFrameworkPaint().set(paint)
                }
            )
        }
    }
}

@Composable
fun Modifier.glowingBorder(
    color: Color,
    blurRadius: Float = 20f,
    pulseSpeed: Int = 2500,
    reducedMotion: Boolean = false
): Modifier {
    val alpha = rememberGlowAlpha(0.3f, 0.9f, pulseSpeed, reducedMotion)
    return this.drawBehind {
        val paint = Paint().asFrameworkPaint().apply {
            isAntiAlias = true
            this.color = Color.Transparent.toArgb()
            setShadowLayer(blurRadius * alpha, 0f, 0f, color.copy(alpha = alpha).toArgb())
        }
        drawIntoCanvas { canvas ->
            canvas.drawRoundRect(
                left = 0f,
                top = 0f,
                right = size.width,
                bottom = size.height,
                radiusX = 24f,
                radiusY = 24f,
                paint = androidx.compose.ui.graphics.Paint().apply {
                    asFrameworkPaint().set(paint)
                }
            )
        }
    }
}
