package com.wordscape.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.wordscape.scene.TimeOfDay
import kotlin.math.sin

@Composable
fun AnimatedSky(
    timeOfDay: TimeOfDay = TimeOfDay.SUNSET,
    showClouds: Boolean = true,
    showStars: Boolean = false,
    showBirds: Boolean = false,
    modifier: Modifier = Modifier
) {
    val skyGradients = when (timeOfDay) {
        TimeOfDay.SUNSET -> listOf(Color(0xFF1E1B4B), Color(0xFF4C1D95), Color(0xFFF97316))
        TimeOfDay.DAWN -> listOf(Color(0xFF0F172A), Color(0xFF3B0764), Color(0xFFEC4899))
        TimeOfDay.MORNING -> listOf(Color(0xFF0284C7), Color(0xFF38BDF8), Color(0xFFBAE6FD))
        TimeOfDay.AFTERNOON -> listOf(Color(0xFF0284C7), Color(0xFF7DD3FC), Color(0xFFE0F2FE))
        TimeOfDay.NIGHT -> listOf(Color(0xFF020617), Color(0xFF0F172A), Color(0xFF1E293B))
    }

    var animProgress by remember { mutableStateOf(0f) }

    LaunchedEffect(Unit) {
        var lastTime = withFrameMillis { it }
        while (true) {
            withFrameMillis { time ->
                val dt = (time - lastTime) / 1000f
                lastTime = time
                animProgress += dt
            }
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        
        // Draw sky gradient background
        drawRect(
            brush = Brush.verticalGradient(
                colors = skyGradients
            )
        )

        // Draw Twinkling Stars if night
        if (showStars || timeOfDay == TimeOfDay.NIGHT) {
            val starCount = 30
            val random = java.util.Random(42) // Constant seed
            for (i in 0 until starCount) {
                val sx = random.nextFloat() * w
                val sy = random.nextFloat() * (h * 0.6f)
                val rate = random.nextFloat() * 2f + 0.5f
                val twinkle = (sin(animProgress * rate + i) + 1f) / 2f
                val size = random.nextFloat() * 6f + 2f
                drawCircle(
                    color = Color.White.copy(alpha = twinkle * 0.8f),
                    radius = size / 2f,
                    center = Offset(sx, sy)
                )
            }
        }

        // Draw Sun/Moon
        val sunX = w * 0.75f
        val sunY = h * 0.25f + sin(animProgress * 0.2f) * 15f
        val sunRadius = 60f
        
        if (timeOfDay == TimeOfDay.SUNSET) {
            // Sun glow rings
            drawCircle(Color(0xFFFFD34D).copy(alpha = 0.15f), sunRadius * 2f, Offset(sunX, sunY))
            drawCircle(Color(0xFFFF9E0B).copy(alpha = 0.3f), sunRadius * 1.3f, Offset(sunX, sunY))
            drawCircle(Color(0xFFFFFDF0).copy(alpha = 0.8f), sunRadius, Offset(sunX, sunY))
        } else if (timeOfDay == TimeOfDay.MORNING || timeOfDay == TimeOfDay.AFTERNOON) {
            drawCircle(Color(0xFFFFFDF0).copy(alpha = 0.2f), sunRadius * 1.8f, Offset(sunX, sunY))
            drawCircle(Color(0xFFFFFDF0), sunRadius, Offset(sunX, sunY))
        } else if (timeOfDay == TimeOfDay.NIGHT) {
            // Moon
            drawCircle(Color(0xFFE2E8F0), 40f, Offset(sunX, sunY))
            drawCircle(Color(0xFF0F172A), 36f, Offset(sunX - 12f, sunY - 4f)) // shade
        }

        // Drifting Clouds
        if (showClouds) {
            val clouds = listOf(
                Pair(0.1f, 0.15f), // x, y
                Pair(0.45f, 0.1f),
                Pair(0.8f, 0.22f)
            )
            val cloudColor = Color.White.copy(alpha = 0.25f)
            
            for (i in clouds.indices) {
                val cloud = clouds[i]
                val speed = (i + 1) * 20f
                val cx = ((cloud.first * w) + animProgress * speed) % (w + 200f) - 100f
                val cy = cloud.second * h
                
                drawCircle(cloudColor, 40f, Offset(cx, cy))
                drawCircle(cloudColor, 32f, Offset(cx - 30f, cy + 5f))
                drawCircle(cloudColor, 36f, Offset(cx + 30f, cy + 3f))
            }
        }
    }
}
