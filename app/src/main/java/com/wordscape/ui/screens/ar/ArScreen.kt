package com.wordscape.ui.screens.ar

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wordscape.components.AnimatedSky
import com.wordscape.components.GlassCard
import com.wordscape.components.MagicalText
import com.wordscape.components.ParticleOverlay
import com.wordscape.motion.ParticleType
import com.wordscape.scene.TimeOfDay
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun ArScreen() {
    val transition = rememberInfiniteTransition(label = "arPreviewRotation")
    
    val rotationAngle by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedSky(
            timeOfDay = TimeOfDay.SUNSET,
            showClouds = false,
            showStars = true,
            modifier = Modifier.fillMaxSize()
        )

        ParticleOverlay(
            type = ParticleType.STARDUST,
            density = 1.2f,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MagicalText(
                    text = "AR Mode",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp
                    ),
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Bring words into your real world",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // Interactive Preview animation (Letters orbiting wireframe cube)
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(240.dp)) {
                    val cx = size.width / 2f
                    val cy = size.height / 2f
                    val scale = 60f
                    val rad = Math.toRadians(rotationAngle.toDouble()).toFloat()

                    // Draw wireframe cube
                    val p1 = Offset(cx - scale, cy - scale)
                    val p2 = Offset(cx + scale, cy - scale)
                    val p3 = Offset(cx + scale, cy + scale)
                    val p4 = Offset(cx - scale, cy + scale)
                    
                    // Draw outer box
                    drawLine(Color.White.copy(alpha = 0.4f), p1, p2, strokeWidth = 3f)
                    drawLine(Color.White.copy(alpha = 0.4f), p2, p3, strokeWidth = 3f)
                    drawLine(Color.White.copy(alpha = 0.4f), p3, p4, strokeWidth = 3f)
                    drawLine(Color.White.copy(alpha = 0.4f), p4, p1, strokeWidth = 3f)

                    // Draw inner projection
                    val offset = 20f
                    val q1 = Offset(p1.x + offset, p1.y + offset)
                    val q2 = Offset(p2.x + offset, p2.y + offset)
                    val q3 = Offset(p3.x + offset, p3.y + offset)
                    val q4 = Offset(p4.x + offset, p4.y + offset)

                    drawLine(Color.White.copy(alpha = 0.2f), q1, q2, strokeWidth = 2f)
                    drawLine(Color.White.copy(alpha = 0.2f), q2, q3, strokeWidth = 2f)
                    drawLine(Color.White.copy(alpha = 0.2f), q3, q4, strokeWidth = 2f)
                    drawLine(Color.White.copy(alpha = 0.2f), q4, q1, strokeWidth = 2f)

                    // Connect vertices
                    drawLine(Color.White.copy(alpha = 0.3f), p1, q1, strokeWidth = 2f)
                    drawLine(Color.White.copy(alpha = 0.3f), p2, q2, strokeWidth = 2f)
                    drawLine(Color.White.copy(alpha = 0.3f), p3, q3, strokeWidth = 2f)
                    drawLine(Color.White.copy(alpha = 0.3f), p4, q4, strokeWidth = 2f)

                    // Orbiting letter paths (visual preview)
                    val orbitX = cx + cos(rad) * 100f
                    val orbitY = cy + sin(rad) * 60f
                    drawCircle(Color(0xFFA78BFA), 20f, Offset(orbitX, orbitY))
                    
                    val orbitX2 = cx - cos(rad + 1.5f) * 100f
                    val orbitY2 = cy - sin(rad + 1.5f) * 60f
                    drawCircle(Color(0xFF7DD3FC), 20f, Offset(orbitX2, orbitY2))
                }
            }

            // Description and Architectural info
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                backgroundAlpha = 0.15f
            ) {
                Text(
                    text = "COMING SOON ✨",
                    color = Color(0xFFFCD34D),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "3D Interactive Worlds",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Soon you\'ll be able to see letter blocks floating in your room and watch your spelled animals walk across tables using ARCore!",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
