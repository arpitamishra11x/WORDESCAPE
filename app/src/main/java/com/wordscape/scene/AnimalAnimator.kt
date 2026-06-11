package com.wordscape.scene

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import kotlin.math.sin

interface AnimalAnimator {
    @Composable
    fun RenderAnimal(
        entity: AnimalEntity,
        modifier: Modifier
    )
}

class CanvasAnimalAnimator : AnimalAnimator {
    @Composable
    override fun RenderAnimal(entity: AnimalEntity, modifier: Modifier) {
        val state by entity.currentState.collectAsState()
        
        // Idle breathing transition
        val transition = rememberInfiniteTransition(label = "animalBreathing")
        val breathScale by transition.animateFloat(
            initialValue = 1.0f,
            targetValue = 1.05f,
            animationSpec = infiniteRepeatable(
                animation = tween(1200, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "breath"
        )
        
        // Tail wag/ear wiggle transition
        val tailWag by transition.animateFloat(
            initialValue = -15f,
            targetValue = 15f,
            animationSpec = infiniteRepeatable(
                animation = tween(400, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "tailWag"
        )

        Canvas(modifier = modifier.fillMaxSize()) {
            val scale = entity.config.size
            val baseColor = entity.config.primaryColor
            val accentColor = entity.config.secondaryColor
            
            // Adjust position
            val cx = size.width / 2f
            val cy = size.height * 0.7f
            
            when (entity.config.type.lowercase()) {
                "cat" -> drawCat(cx, cy, scale, breathScale, tailWag, baseColor, accentColor, state)
                "dog" -> drawDog(cx, cy, scale, breathScale, tailWag, baseColor, accentColor, state)
                "owl" -> drawOwl(cx, cy, scale, breathScale, baseColor, accentColor, state)
                "fox" -> drawFox(cx, cy, scale, breathScale, tailWag, baseColor, accentColor, state)
                else -> drawGeneric(cx, cy, scale, breathScale, baseColor)
            }
        }
    }

    private fun DrawScope.drawCat(
        cx: Float, cy: Float,
        scale: Float, breath: Float, tailWag: Float,
        baseColor: Color, accentColor: Color, state: AnimalState
    ) {
        val r = 40f * scale
        
        // Tail
        val tailPath = Path().apply {
            moveTo(cx + r * 0.7f, cy + r * 0.5f)
            quadraticBezierTo(
                cx + r * 1.5f + tailWag, cy - r * 0.3f,
                cx + r * 1.2f, cy - r * 1.2f + tailWag
            )
        }
        drawPath(
            path = tailPath,
            color = baseColor,
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 12f * scale)
        )

        // Body
        drawCircle(
            color = baseColor,
            radius = r * breath,
            center = Offset(cx, cy)
        )
        
        // Head
        val hx = cx
        val hy = cy - r * 1.1f
        val hr = 30f * scale
        drawCircle(
            color = baseColor,
            radius = hr,
            center = Offset(hx, hy)
        )

        // Ears
        val leftEar = Path().apply {
            moveTo(hx - hr * 0.8f, hy - hr * 0.5f)
            lineTo(hx - hr * 0.8f, hy - hr * 1.4f)
            lineTo(hx - hr * 0.2f, hy - hr * 0.8f)
            close()
        }
        val rightEar = Path().apply {
            moveTo(hx + hr * 0.8f, hy - hr * 0.5f)
            lineTo(hx + hr * 0.8f, hy - hr * 1.4f)
            lineTo(hx + hr * 0.2f, hy - hr * 0.8f)
            close()
        }
        drawPath(leftEar, baseColor)
        drawPath(rightEar, baseColor)
        
        // Inner ears
        val innerLeftEar = Path().apply {
            moveTo(hx - hr * 0.7f, hy - hr * 0.6f)
            lineTo(hx - hr * 0.7f, hy - hr * 1.1f)
            lineTo(hx - hr * 0.3f, hy - hr * 0.8f)
            close()
        }
        drawPath(innerLeftEar, accentColor)

        // Eyes
        val eyeRadius = 4f * scale
        val isBlinking = state == AnimalState.BLINKING || (breath > 1.04f) // simulate natural blinks
        
        if (isBlinking) {
            // Closed eyes line
            drawLine(Color.Black, Offset(hx - 12f * scale, hy), Offset(hx - 4f * scale, hy), strokeWidth = 3f)
            drawLine(Color.Black, Offset(hx + 4f * scale, hy), Offset(hx + 12f * scale, hy), strokeWidth = 3f)
        } else {
            drawCircle(Color.Black, eyeRadius, Offset(hx - 10f * scale, hy))
            drawCircle(Color.Black, eyeRadius, Offset(hx + 10f * scale, hy))
            // Eye shines
            drawCircle(Color.White, 1.5f * scale, Offset(hx - 11f * scale, hy - 2f * scale))
            drawCircle(Color.White, 1.5f * scale, Offset(hx + 9f * scale, hy - 2f * scale))
        }

        // Nose/Mouth
        drawCircle(accentColor, 3f * scale, Offset(hx, hy + 4f * scale))
    }

    private fun DrawScope.drawDog(
        cx: Float, cy: Float,
        scale: Float, breath: Float, tailWag: Float,
        baseColor: Color, accentColor: Color, state: AnimalState
    ) {
        val r = 42f * scale
        
        // Tail
        val tailPath = Path().apply {
            moveTo(cx - r * 0.7f, cy + r * 0.3f)
            quadraticBezierTo(
                cx - r * 1.6f + tailWag, cy + tailWag,
                cx - r * 1.3f, cy - r * 0.8f + tailWag
            )
        }
        drawPath(
            path = tailPath,
            color = baseColor,
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 14f * scale)
        )

        // Body
        drawCircle(
            color = baseColor,
            radius = r * breath,
            center = Offset(cx, cy)
        )

        // Head
        val hx = cx
        val hy = cy - r * 1.0f
        val hr = 32f * scale
        drawCircle(
            color = baseColor,
            radius = hr,
            center = Offset(hx, hy)
        )

        // Floppy ears
        drawRoundRect(
            color = accentColor,
            topLeft = Offset(hx - hr * 1.2f, hy - hr * 0.4f),
            size = Size(14f * scale, 36f * scale),
            cornerRadius = CornerRadius(8f, 8f)
        )
        drawRoundRect(
            color = accentColor,
            topLeft = Offset(hx + hr * 0.8f, hy - hr * 0.4f),
            size = Size(14f * scale, 36f * scale),
            cornerRadius = CornerRadius(8f, 8f)
        )

        // Eyes
        val eyeRadius = 4.5f * scale
        drawCircle(Color.Black, eyeRadius, Offset(hx - 10f * scale, hy - 2f * scale))
        drawCircle(Color.Black, eyeRadius, Offset(hx + 10f * scale, hy - 2f * scale))

        // Snout & Nose
        drawCircle(accentColor, 10f * scale, Offset(hx, hy + 8f * scale))
        drawCircle(Color.Black, 5f * scale, Offset(hx, hy + 5f * scale))
    }

    private fun DrawScope.drawOwl(
        cx: Float, cy: Float,
        scale: Float, breath: Float,
        baseColor: Color, accentColor: Color, state: AnimalState
    ) {
        val w = 70f * scale
        val h = 90f * scale * breath
        
        // Owl Body (rounded rectangle)
        drawRoundRect(
            color = baseColor,
            topLeft = Offset(cx - w / 2f, cy - h / 2f),
            size = Size(w, h),
            cornerRadius = CornerRadius(30f * scale, 30f * scale)
        )
        
        // Chest patch
        drawCircle(
            color = accentColor,
            radius = 22f * scale,
            center = Offset(cx, cy + h * 0.1f)
        )

        // Big Eyes
        val eyeR = 16f * scale
        drawCircle(Color.White, eyeR, Offset(cx - 16f * scale, cy - h * 0.15f))
        drawCircle(Color.White, eyeR, Offset(cx + 16f * scale, cy - h * 0.15f))
        
        // Pupils
        drawCircle(Color.Black, 8f * scale, Offset(cx - 16f * scale, cy - h * 0.15f))
        drawCircle(Color.Black, 8f * scale, Offset(cx + 16f * scale, cy - h * 0.15f))

        // Beak
        val beak = Path().apply {
            moveTo(cx, cy - h * 0.05f)
            lineTo(cx - 6f * scale, cy - h * 0.05f - 12f * scale)
            lineTo(cx + 6f * scale, cy - h * 0.05f - 12f * scale)
            close()
        }
        drawPath(beak, Color(0xFFF59E0B)) // orange beak
    }

    private fun DrawScope.drawFox(
        cx: Float, cy: Float,
        scale: Float, breath: Float, tailWag: Float,
        baseColor: Color, accentColor: Color, state: AnimalState
    ) {
        val r = 38f * scale
        
        // Bushy Tail
        val tailPath = Path().apply {
            moveTo(cx - r * 0.5f, cy + r * 0.5f)
            quadraticBezierTo(cx - r * 1.8f + tailWag, cy + tailWag, cx - r * 1.5f, cy - r * 0.5f + tailWag)
        }
        drawPath(
            path = tailPath,
            color = baseColor,
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 20f * scale)
        )

        // Body
        drawCircle(
            color = baseColor,
            radius = r * breath,
            center = Offset(cx, cy)
        )

        // Head (angular snout)
        val hx = cx
        val hy = cy - r * 1.1f
        val hr = 28f * scale
        
        // Main head circle
        drawCircle(baseColor, hr, Offset(hx, hy))
        
        // Snout triangle
        val snoutPath = Path().apply {
            moveTo(hx - hr * 0.7f, hy + hr * 0.2f)
            lineTo(hx + hr * 0.7f, hy + hr * 0.2f)
            lineTo(hx, hy + hr * 1.2f)
            close()
        }
        drawPath(snoutPath, Color.White)
        drawCircle(Color.Black, 4f * scale, Offset(hx, hy + hr * 1.1f)) // Nose tip

        // Large pointy ears
        val leftEar = Path().apply {
            moveTo(hx - hr * 0.9f, hy - hr * 0.2f)
            lineTo(hx - hr * 1.0f, hy - hr * 1.5f)
            lineTo(hx - hr * 0.1f, hy - hr * 0.8f)
            close()
        }
        val rightEar = Path().apply {
            moveTo(hx + hr * 0.9f, hy - hr * 0.2f)
            lineTo(hx + hr * 1.0f, hy - hr * 1.5f)
            lineTo(hx + hr * 0.1f, hy - hr * 0.8f)
            close()
        }
        drawPath(leftEar, baseColor)
        drawPath(rightEar, baseColor)

        // Eyes
        drawCircle(Color.Black, 3.5f * scale, Offset(hx - 9f * scale, hy))
        drawCircle(Color.Black, 3.5f * scale, Offset(hx + 9f * scale, hy))
    }

    private fun DrawScope.drawGeneric(cx: Float, cy: Float, scale: Float, breath: Float, color: Color) {
        drawCircle(
            color = color,
            radius = 45f * scale * breath,
            center = Offset(cx, cy)
        )
        // Draw eyes
        drawCircle(Color.Black, 5f, Offset(cx - 15f, cy - 10f))
        drawCircle(Color.Black, 5f, Offset(cx + 15f, cy - 10f))
    }
}
