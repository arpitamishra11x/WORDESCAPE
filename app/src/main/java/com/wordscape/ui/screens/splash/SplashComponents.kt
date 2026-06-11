package com.wordscape.ui.screens.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Random
import kotlin.math.cos
import kotlin.math.sin

// Representation of stars in the twilight sky
data class Star(val x: Float, val y: Float, val scale: Float, val speed: Float, val phase: Float)

// Representation of drifting clouds
data class SplashCloud(val x: Float, val y: Float, val scale: Float, val speed: Float)

// Represent fireflies in the foreground
data class Firefly(val x: Float, val y: Float, val scale: Float, val baseSpeed: Float, val phaseOffset: Float)

@Composable
fun SplashSky(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "SkyTransition")
    
    // Time animation for drifting elements
    val time by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(100000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "time"
    )

    // Breathing sky glow alpha
    val skyGlowAlpha by transition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "skyGlowAlpha"
    )

    // Twinkling effect
    val twinkleFactor by transition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "twinkle"
    )

    // Static set of stars
    val stars = remember {
        val rand = Random(42)
        List(40) {
            Star(
                x = rand.nextFloat(),
                y = rand.nextFloat() * 0.5f, // Only top half of screen
                scale = 1f + rand.nextFloat() * 2f,
                speed = 0.5f + rand.nextFloat() * 0.5f,
                phase = rand.nextFloat() * 10f
            )
        }
    }

    // Clouds setup
    val clouds = remember {
        val rand = Random(99)
        List(4) { i ->
            SplashCloud(
                x = rand.nextFloat(),
                y = 0.15f + i * 0.12f,
                scale = 0.7f + rand.nextFloat() * 0.6f,
                speed = 0.015f + rand.nextFloat() * 0.01f
            )
        }
    }

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // 1. Cinematic Sky Background Gradient (twilight blue -> deep purple -> warm sunset pink -> gold)
        val skyBrush = Brush.verticalGradient(
            colorStops = arrayOf(
                0.0f to Color(0xFF131127), // Deep twilight top
                0.3f to Color(0xFF2C1F45), // Upper mid purple
                0.6f to Color(0xFF5A315C), // Sunset pink
                0.85f to Color(0xFF964B6E), // Soft rose/pink clouds base
                1.0f to Color(0xFFF99D63)  // Warm horizon gold
            )
        )
        drawRect(brush = skyBrush)

        // 2. Stars
        stars.forEach { star ->
            val alpha = (sin(time * star.speed + star.phase) * 0.5f + 0.5f) * twinkleFactor
            drawCircle(
                color = Color.White.copy(alpha = alpha),
                radius = star.scale,
                center = Offset(star.x * w, star.y * h)
            )
            // Tiny sparkle reflection
            if (alpha > 0.8f) {
                drawLine(
                    color = Color.White.copy(alpha = (alpha - 0.8f) * 5f),
                    start = Offset(star.x * w - star.scale * 2.5f, star.y * h),
                    end = Offset(star.x * w + star.scale * 2.5f, star.y * h),
                    strokeWidth = 0.8f
                )
                drawLine(
                    color = Color.White.copy(alpha = (alpha - 0.8f) * 5f),
                    start = Offset(star.x * w, star.y * h - star.scale * 2.5f),
                    end = Offset(star.x * w, star.y * h + star.scale * 2.5f),
                    strokeWidth = 0.8f
                )
            }
        }

        // 3. Shooting Star (animated traverse)
        val shootingStarTime = (time % 8f) / 8f
        if (shootingStarTime < 0.2f) {
            val progress = shootingStarTime / 0.2f
            val startX = w * 0.6f
            val startY = h * 0.05f
            val endX = w * 0.2f
            val endY = h * 0.25f
            val currentX = startX + (endX - startX) * progress
            val currentY = startY + (endY - startY) * progress

            val trailBrush = Brush.linearGradient(
                colors = listOf(Color.White, Color.White.copy(alpha = 0.0f)),
                start = Offset(currentX, currentY),
                end = Offset(currentX + w * 0.1f, currentY - h * 0.05f)
            )
            drawLine(
                brush = trailBrush,
                start = Offset(currentX, currentY),
                end = Offset(currentX + w * 0.1f, currentY - h * 0.05f),
                strokeWidth = 2f
            )
            drawCircle(
                color = Color.White,
                radius = 2.5f,
                center = Offset(currentX, currentY)
            )
        }

        // 4. Distant Floating Islands (Silhouettes)
        // Background island 1 (left)
        val bi1x = w * 0.12f
        val bi1y = h * 0.48f
        val bi1Scale = 0.4f
        val bi1Path = Path().apply {
            moveTo(bi1x - 60f * bi1Scale, bi1y)
            lineTo(bi1x + 60f * bi1Scale, bi1y)
            quadraticTo(bi1x + 30f * bi1Scale, bi1y + 30f * bi1Scale, bi1x, bi1y + 45f * bi1Scale)
            quadraticTo(bi1x - 30f * bi1Scale, bi1y + 30f * bi1Scale, bi1x - 60f * bi1Scale, bi1y)
            close()
        }
        drawPath(
            path = bi1Path,
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFF513D5E).copy(alpha = 0.7f), Color(0xFF2E203C).copy(alpha = 0.7f))
            )
        )
        // Waterfall from distant island
        drawLine(
            color = Color(0xFF8CD8FF).copy(alpha = 0.4f),
            start = Offset(bi1x - 10f * bi1Scale, bi1y),
            end = Offset(bi1x - 10f * bi1Scale, bi1y + 80f * bi1Scale),
            strokeWidth = 1f
        )

        // Background island 2 (right)
        val bi2x = w * 0.88f
        val bi2y = h * 0.44f
        val bi2Scale = 0.45f
        val bi2Path = Path().apply {
            moveTo(bi2x - 60f * bi2Scale, bi2y)
            lineTo(bi2x + 60f * bi2Scale, bi2y)
            quadraticTo(bi2x + 35f * bi2Scale, bi2y + 25f * bi2Scale, bi2x, bi2y + 40f * bi2Scale)
            quadraticTo(bi2x - 35f * bi2Scale, bi2y + 25f * bi2Scale, bi2x - 60f * bi2Scale, bi2y)
            close()
        }
        drawPath(
            path = bi2Path,
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFF4C3B5E).copy(alpha = 0.8f), Color(0xFF2C1F3F).copy(alpha = 0.8f))
            )
        )

        // 5. Sun behind/right of the main centerpiece
        val sunX = w * 0.75f
        val sunY = h * 0.38f
        val sunRadius = 60f
        
        // Ambient God Rays / Light Beams from Sun
        for (i in 0..4) {
            val angle = -20f - i * 35f
            val rad = Math.toRadians(angle.toDouble())
            val rx1 = sunX + cos(rad - 0.15) * 1000f
            val ry1 = sunY + sin(rad - 0.15) * 1000f
            val rx2 = sunX + cos(rad + 0.15) * 1000f
            val ry2 = sunY + sin(rad + 0.15) * 1000f

            val rayPath = Path().apply {
                moveTo(sunX, sunY)
                lineTo(rx1.toFloat(), ry1.toFloat())
                lineTo(rx2.toFloat(), ry2.toFloat())
                close()
            }
            drawPath(
                path = rayPath,
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFFFFEECC).copy(alpha = 0.08f), Color.Transparent),
                    center = Offset(sunX, sunY),
                    radius = w * 0.7f
                )
            )
        }

        // Draw Sun Core with extreme glow
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color.White, Color(0xFFFFF7C2).copy(alpha = 0.7f), Color.Transparent),
                center = Offset(sunX, sunY),
                radius = sunRadius * 4f
            ),
            radius = sunRadius * 4f,
            center = Offset(sunX, sunY)
        )
        drawCircle(
            color = Color.White,
            radius = sunRadius,
            center = Offset(sunX, sunY)
        )

        // 6. Volumetric Layered Clouds (drifting slowly)
        clouds.forEachIndexed { idx, cloud ->
            val driftX = ((cloud.x + time * cloud.speed) % 1.2f - 0.2f) * w
            val cloudY = cloud.y * h
            val scale = cloud.scale
            
            // Draw cloud cluster
            val cloudColor = when (idx) {
                0 -> Color(0xFFFFCCDD).copy(alpha = 0.25f) // Pinkish cloud
                1 -> Color(0xFFFFAA88).copy(alpha = 0.3f)  // Golden pink cloud
                2 -> Color(0xFFEBD5FF).copy(alpha = 0.2f)  // Lavender cloud
                else -> Color.White.copy(alpha = 0.15f)
            }

            drawCircle(cloudColor, 80f * scale, Offset(driftX, cloudY))
            drawCircle(cloudColor, 60f * scale, Offset(driftX - 70f * scale, cloudY + 15f * scale))
            drawCircle(cloudColor, 70f * scale, Offset(driftX + 60f * scale, cloudY + 10f * scale))
            drawCircle(cloudColor, 50f * scale, Offset(driftX - 110f * scale, cloudY + 25f * scale))
            drawCircle(cloudColor, 50f * scale, Offset(driftX + 110f * scale, cloudY + 25f * scale))
        }

        // Additional horizontal soft fog along the middle to blend
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(Color.Transparent, Color(0xFF7A4A76).copy(alpha = 0.2f), Color.Transparent),
                startY = h * 0.45f,
                endY = h * 0.65f
            )
        )
    }
}

@Composable
fun SplashFloatingIsland(
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "IslandHover")
    
    // Slow hovering animation (Sine wave)
    val hoverOffset by transition.animateFloat(
        initialValue = -8f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "hover"
    )

    // Waterfall cascading animation
    val waterPhase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "waterfall"
    )

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(280.dp)
            .offset(y = hoverOffset.dp)
    ) {
        val w = size.width
        val h = size.height
        val cx = w / 2f
        val cy = h * 0.4f // Island vertical center line
        val scale = w / 360f

        // 1. Dynamic island underside glow
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFFE082B4).copy(alpha = 0.35f), Color.Transparent),
                center = Offset(cx, cy + 30f * scale),
                radius = 120f * scale
            ),
            radius = 120f * scale,
            center = Offset(cx, cy + 30f * scale)
        )

        // 2. Rocky earth underside (Polygonal cliff details)
        val cliffPath = Path().apply {
            moveTo(cx - 130f * scale, cy)
            lineTo(cx + 130f * scale, cy)
            lineTo(cx + 105f * scale, cy + 20f * scale)
            lineTo(cx + 70f * scale, cy + 55f * scale)
            lineTo(cx + 45f * scale, cy + 85f * scale)
            lineTo(cx + 10f * scale, cy + 120f * scale) // Central peak waterfall outlet
            lineTo(cx - 15f * scale, cy + 120f * scale)
            lineTo(cx - 50f * scale, cy + 80f * scale)
            lineTo(cx - 90f * scale, cy + 45f * scale)
            lineTo(cx - 120f * scale, cy + 20f * scale)
            close()
        }
        
        // Rocky shading gradient (warm/cool transitions)
        val rockBrush = Brush.verticalGradient(
            colors = listOf(Color(0xFF4C3730), Color(0xFF281E1C), Color(0xFF181110))
        )
        drawPath(path = cliffPath, brush = rockBrush)

        // Cracks and rock facets for 3D appearance
        val crackPen = Stroke(width = 2f * scale, cap = StrokeCap.Round)
        
        // Rock facet highlights (pointing towards sun on the right)
        drawPath(
            path = Path().apply {
                moveTo(cx + 70f * scale, cy + 55f * scale)
                lineTo(cx + 45f * scale, cy + 85f * scale)
                lineTo(cx + 10f * scale, cy + 120f * scale)
            },
            color = Color(0xFF705A54),
            style = crackPen
        )
        drawPath(
            path = Path().apply {
                moveTo(cx - 50f * scale, cy + 80f * scale)
                lineTo(cx, cy + 50f * scale)
                lineTo(cx + 70f * scale, cy + 55f * scale)
            },
            color = Color(0xFF1E1615),
            style = crackPen
        )

        // Hanging roots/vines
        drawLine(Color(0xFF332A20), Offset(cx - 90f * scale, cy + 20f * scale), Offset(cx - 92f * scale, cy + 75f * scale), strokeWidth = 1.5f)
        drawLine(Color(0xFF332A20), Offset(cx - 55f * scale, cy + 40f * scale), Offset(cx - 57f * scale, cy + 95f * scale), strokeWidth = 1.2f)
        drawLine(Color(0xFF4C3730), Offset(cx + 80f * scale, cy + 15f * scale), Offset(cx + 78f * scale, cy + 65f * scale), strokeWidth = 1.8f)

        // 3. Magical green top grass layer
        val grassPath = Path().apply {
            moveTo(cx - 140f * scale, cy)
            quadraticTo(cx, cy - 22f * scale, cx + 140f * scale, cy)
            lineTo(cx + 130f * scale, cy + 12f * scale)
            quadraticTo(cx, cy + 28f * scale, cx - 130f * scale, cy + 12f * scale)
            close()
        }

        // Vibrant grass gradient with yellow-green highlights facing the sun
        val grassBrush = Brush.linearGradient(
            colors = listOf(Color(0xFF277242), Color(0xFF37A95C), Color(0xFF99F57D)),
            start = Offset(cx - 100f, cy),
            end = Offset(cx + 100f, cy)
        )
        drawPath(path = grassPath, brush = grassBrush)

        // Grass rim shadow detailing
        drawPath(
            path = grassPath,
            color = Color(0xFF114223),
            style = Stroke(width = 1.5f * scale)
        )

        // 4. Central Magical River and Waterfall
        val riverWidth = 30f * scale
        
        // Draw cascading waterfall
        val waterfallBrush = Brush.verticalGradient(
            colorStops = arrayOf(
                0.0f to Color(0xFFCCF2FF),
                0.2f to Color(0xFF6EDCFF),
                0.8f to Color(0xFF0EA5E9).copy(alpha = 0.9f),
                1.0f to Color(0xFFCCF2FF).copy(alpha = 0.2f)
            )
        )

        val waterfallHeight = 160f * scale
        val waterfallLeft = cx - riverWidth / 2f
        val waterfallTop = cy + 10f * scale

        // Draw main waterfall rectangle
        drawRect(
            brush = waterfallBrush,
            topLeft = Offset(waterfallLeft, waterfallTop),
            size = Size(riverWidth, waterfallHeight)
        )

        // Shimmer lines on the waterfall
        val numShimmerLines = 4
        for (i in 0 until numShimmerLines) {
            val lineOffsetFactor = (waterPhase + (i * 0.25f)) % 1f
            val lineY = waterfallTop + waterfallHeight * lineOffsetFactor
            drawLine(
                color = Color.White.copy(alpha = 0.8f * (1f - lineOffsetFactor)),
                start = Offset(waterfallLeft + (i * 6f + 3f) * scale, lineY),
                end = Offset(waterfallLeft + (i * 6f + 3f) * scale, (lineY + 20f * scale).coerceAtMost(waterfallTop + waterfallHeight)),
                strokeWidth = 2.2f * scale,
                cap = StrokeCap.Round
            )
        }

        // 5. Mist Particles at bottom of waterfall
        val rand = Random(123)
        for (j in 0..12) {
            val pOffsetFactor = (waterPhase + (j * 0.08f)) % 1f
            val pRadius = (4f + rand.nextFloat() * 6f) * scale
            val pAlpha = (1f - pOffsetFactor) * 0.8f
            val pX = cx + (-20f + rand.nextFloat() * 40f) * scale
            val pY = (waterfallTop + waterfallHeight) - (20f * pOffsetFactor * scale) + (rand.nextFloat() * 15f)

            drawCircle(
                color = Color.White.copy(alpha = pAlpha),
                radius = pRadius,
                center = Offset(pX, pY)
            )
            // Soft glow background for mist
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFFD0F4FF).copy(alpha = pAlpha * 0.2f), Color.Transparent),
                    center = Offset(pX, pY),
                    radius = pRadius * 3f
                ),
                radius = pRadius * 3f,
                center = Offset(pX, pY)
            )
        }

        // 6. Lush vegetation on the island (magical glowing trees)
        // Left tree
        val lx = cx - 90f * scale
        val ly = cy - 4f * scale
        // Trunk
        drawRect(Color(0xFF382315), Offset(lx - 3f * scale, ly - 15f * scale), Size(6f * scale, 15f * scale))
        // Canopy
        drawCircle(Color(0xFF165A31), 16f * scale, Offset(lx, ly - 24f * scale))
        drawCircle(Color(0xFF22C55E), 12f * scale, Offset(lx - 8f * scale, ly - 20f * scale))
        drawCircle(Color(0xFF4ADE80), 9f * scale, Offset(lx + 8f * scale, ly - 25f * scale))

        // Right glowing tree (golden pink)
        val rx = cx + 85f * scale
        val ry = cy - 2f * scale
        // Trunk
        drawRect(Color(0xFF332014), Offset(rx - 2.5f * scale, ry - 18f * scale), Size(5f * scale, 18f * scale))
        // Canopy
        drawCircle(Color(0xFFB45309), 15f * scale, Offset(rx, ry - 28f * scale))
        drawCircle(Color(0xFFF59E0B), 11f * scale, Offset(rx - 7f * scale, ry - 22f * scale))
        drawCircle(Color(0xFFFCD34D), 8f * scale, Offset(rx + 6f * scale, ry - 30f * scale))
    }
}

@Composable
fun SplashCrystalLetterA(
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "CrystalLetterATransition")

    // Slow floating translation (out of phase with the island for dynamic depth)
    val floatTranslation by transition.animateFloat(
        initialValue = -12f,
        targetValue = 12f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float"
    )

    // Breathing neon/bloom alpha glow
    val bloomAlpha by transition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.75f,
        animationSpec = infiniteRepeatable(
            animation = tween(3500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bloomAlpha"
    )

    // Dynamic rainbow reflection angle shifting
    val sweepOffset by transition.animateFloat(
        initialValue = -150f,
        targetValue = 450f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "refractionSweep"
    )

    // Glare phase for twinkling sparkles
    val glarePhase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "glarePhase"
    )

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(280.dp)
            .offset(y = floatTranslation.dp)
    ) {
        val w = size.width
        val h = size.height
        val cx = w / 2f
        val cy = h * 0.5f
        val scale = w / 280f // Relative scaling factor

        // Bounding box size: width = 160 * scale, height = 200 * scale
        val letterWidth = 160f * scale
        val letterHeight = 200f * scale
        val startX = cx - letterWidth / 2f
        val startY = cy - letterHeight / 2f

        // Let's create the path for 'A' with rounded feet and peak
        val outerPath = Path().apply {
            moveTo(startX + letterWidth * 0.15f, startY + letterHeight * 0.92f)
            quadraticTo(
                startX + letterWidth * 0.15f, startY + letterHeight * 0.85f,
                startX + letterWidth * 0.24f, startY + letterHeight * 0.7f
            )
            lineTo(startX + letterWidth * 0.43f, startY + letterHeight * 0.15f)
            quadraticTo(
                startX + letterWidth * 0.46f, startY + letterHeight * 0.08f,
                startX + letterWidth * 0.5f, startY + letterHeight * 0.08f
            )
            quadraticTo(
                startX + letterWidth * 0.54f, startY + letterHeight * 0.08f,
                startX + letterWidth * 0.57f, startY + letterHeight * 0.15f
            )
            lineTo(startX + letterWidth * 0.76f, startY + letterHeight * 0.7f)
            quadraticTo(
                startX + letterWidth * 0.85f, startY + letterHeight * 0.85f,
                startX + letterWidth * 0.85f, startY + letterHeight * 0.92f
            )
            // Right foot round bottom edge
            quadraticTo(
                startX + letterWidth * 0.85f, startY + letterHeight * 0.98f,
                startX + letterWidth * 0.72f, startY + letterHeight * 0.98f
            )
            quadraticTo(
                startX + letterWidth * 0.65f, startY + letterHeight * 0.98f,
                startX + letterWidth * 0.65f, startY + letterHeight * 0.91f
            )
            // Outer crossbar right side inner leg line
            lineTo(startX + letterWidth * 0.58f, startY + letterHeight * 0.74f)
            lineTo(startX + letterWidth * 0.42f, startY + letterHeight * 0.74f)
            // Outer crossbar left side inner leg line
            lineTo(startX + letterWidth * 0.35f, startY + letterHeight * 0.91f)
            quadraticTo(
                startX + letterWidth * 0.35f, startY + letterHeight * 0.98f,
                startX + letterWidth * 0.28f, startY + letterHeight * 0.98f
            )
            quadraticTo(
                startX + letterWidth * 0.15f, startY + letterHeight * 0.98f,
                startX + letterWidth * 0.15f, startY + letterHeight * 0.92f
            )
            close()
        }

        // Inner triangular cut path for 'A'
        val innerPath = Path().apply {
            moveTo(startX + letterWidth * 0.40f, startY + letterHeight * 0.62f)
            lineTo(startX + letterWidth * 0.50f, startY + letterHeight * 0.36f)
            lineTo(startX + letterWidth * 0.60f, startY + letterHeight * 0.62f)
            close()
        }

        // Combine paths: Subtracted region to create actual A shape
        val crystalShape = Path.combine(PathOperation.Difference, outerPath, innerPath)

        // 1. Soft base shadow for depth
        drawPath(
            path = crystalShape,
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFF231E3C).copy(alpha = 0.4f), Color.Transparent),
                center = Offset(cx, cy + 20f * scale),
                radius = 110f * scale
            )
        )

        // 2. Neon Bloom Aura underlay
        drawPath(
            path = crystalShape,
            color = Color(0xFFD68BF2).copy(alpha = bloomAlpha * 0.25f),
            style = Stroke(width = 30f * scale)
        )
        drawPath(
            path = crystalShape,
            color = Color(0xFF8CD8FF).copy(alpha = bloomAlpha * 0.2f),
            style = Stroke(width = 15f * scale)
        )

        // 3. Iridescent Base Fill (Gradients of Violet, Magenta, Cyan)
        val crystalGradient = Brush.linearGradient(
            colors = listOf(
                Color(0xFFB989FF).copy(alpha = 0.85f),
                Color(0xFFE28CFF).copy(alpha = 0.85f),
                Color(0xFF86D7FF).copy(alpha = 0.85f)
            ),
            start = Offset(startX, startY),
            end = Offset(startX + letterWidth, startY + letterHeight)
        )
        drawPath(path = crystalShape, brush = crystalGradient)

        // 4. Iridescent Diagonal Reflection (moving highlights)
        val sweepBrush = Brush.linearGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.0f),
                Color.White.copy(alpha = 0.7f),
                Color(0xFFFFBDE7).copy(alpha = 0.9f),
                Color(0xFFCCEEFF).copy(alpha = 0.7f),
                Color.White.copy(alpha = 0.0f)
            ),
            start = Offset(sweepOffset, sweepOffset),
            end = Offset(sweepOffset + 120f * scale, sweepOffset + 120f * scale)
        )
        clipPath(crystalShape) {
            drawRect(brush = sweepBrush, size = size)
        }

        // 5. 3D Facet & Glass Refraction Lines
        // Draw diagonal seams from inner vertices to outer vertices
        val facetPen = Stroke(width = 1.8f * scale, cap = StrokeCap.Round)
        
        // Diagonal seams (makes it look geometric and crystal cut)
        drawLine(Color.White.copy(alpha = 0.4f), Offset(startX + letterWidth * 0.43f, startY + letterHeight * 0.15f), Offset(startX + letterWidth * 0.50f, startY + letterHeight * 0.36f), strokeWidth = 1.5f * scale)
        drawLine(Color.White.copy(alpha = 0.4f), Offset(startX + letterWidth * 0.57f, startY + letterHeight * 0.15f), Offset(startX + letterWidth * 0.50f, startY + letterHeight * 0.36f), strokeWidth = 1.5f * scale)
        drawLine(Color.White.copy(alpha = 0.4f), Offset(startX + letterWidth * 0.35f, startY + letterHeight * 0.91f), Offset(startX + letterWidth * 0.40f, startY + letterHeight * 0.62f), strokeWidth = 1.5f * scale)
        drawLine(Color.White.copy(alpha = 0.4f), Offset(startX + letterWidth * 0.65f, startY + letterHeight * 0.91f), Offset(startX + letterWidth * 0.60f, startY + letterHeight * 0.62f), strokeWidth = 1.5f * scale)

        // Highlight horizontal cross seam
        drawLine(
            color = Color.White.copy(alpha = 0.6f),
            start = Offset(startX + letterWidth * 0.42f, startY + letterHeight * 0.74f),
            end = Offset(startX + letterWidth * 0.58f, startY + letterHeight * 0.74f),
            strokeWidth = 2.0f * scale
        )

        // 6. Glowing Neon Border Outlines (Puffy 3D Highlight)
        drawPath(
            path = crystalShape,
            color = Color.White.copy(alpha = 0.85f),
            style = Stroke(width = 4.0f * scale)
        )

        // Subtle dark core shadow to give depth to the glass
        drawPath(
            path = crystalShape,
            color = Color(0xFF38155C).copy(alpha = 0.2f),
            style = Stroke(width = 8.0f * scale)
        )

        // 7. Dynamic Bright Reflection Glare Sparkles (top-left & right corners)
        // Draw cross star glares
        val glares = listOf(
            Offset(startX + letterWidth * 0.35f, startY + letterHeight * 0.3f),
            Offset(startX + letterWidth * 0.62f, startY + letterHeight * 0.25f),
            Offset(startX + letterWidth * 0.5f, startY + letterHeight * 0.1f)
        )
        glares.forEach { gl ->
            val scaleGlare = (0.7f + 0.3f * sin(glarePhase + gl.x))
            drawCircle(Color.White, 3f * scale * scaleGlare, gl)
            drawLine(Color.White.copy(alpha = 0.9f * scaleGlare), Offset(gl.x - 12f * scale, gl.y), Offset(gl.x + 12f * scale, gl.y), strokeWidth = 1.5f)
            drawLine(Color.White.copy(alpha = 0.9f * scaleGlare), Offset(gl.x, gl.y - 12f * scale), Offset(gl.x, gl.y + 12f * scale), strokeWidth = 1.5f)
        }
    }
}

@Composable
fun SplashGlassButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "ButtonGlowTransition")

    // Pulsing shadow glow
    val glowIntensity by transition.animateFloat(
        initialValue = 6f,
        targetValue = 18f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowIntensity"
    )

    // Breathing glow alpha
    val borderAlpha by transition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "borderAlpha"
    )

    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .width(280.dp)
            .height(64.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val shapeRect = Rect(0f, 0f, w, h)
            val corner = CornerRadius(h / 2f)

            // 1. Soft back blur shadow (colored)
            drawRoundRect(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF8CD8FF).copy(alpha = 0.4f), Color.Transparent),
                    center = Offset(w / 2f, h / 2f),
                    radius = w * 0.6f
                ),
                topLeft = Offset(-20f, -20f),
                size = Size(w + 40f, h + 40f)
            )

            // 2. Frosted glass gradient fill (rose/peach left, sky blue right)
            val glassBrush = Brush.linearGradient(
                colors = listOf(
                    Color(0xFFFFB399).copy(alpha = 0.35f), // Peach rose
                    Color(0xFF80D4FF).copy(alpha = 0.35f)  // Sky blue
                ),
                start = Offset(0f, h / 2f),
                end = Offset(w, h / 2f)
            )

            drawRoundRect(
                brush = glassBrush,
                cornerRadius = corner
            )

            // 3. Neon white-blue border (semi-transparent white, with glow)
            drawRoundRect(
                color = Color.White.copy(alpha = borderAlpha * 0.75f),
                cornerRadius = corner,
                style = Stroke(width = 2.5f)
            )
            // Extra soft blue outer rim stroke to blend
            drawRoundRect(
                color = Color(0xFFCCEEFF).copy(alpha = borderAlpha * 0.4f),
                cornerRadius = corner,
                style = Stroke(width = 4.5f)
            )

            // 4. White sheen reflection across top edge
            val sheenPath = Path().apply {
                moveTo(20f, 4f)
                quadraticTo(w / 2f, 12f, w - 20f, 4f)
            }
            drawPath(
                path = sheenPath,
                color = Color.White.copy(alpha = 0.5f),
                style = Stroke(width = 1.5f)
            )
        }

        // 5. Button Text & Arrow
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = text,
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp
            )
        }
    }
}

@Composable
fun SplashForeground(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "ForegroundTransition")

    // Slow wind sway for flowers/foliage
    val swayOffset by transition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sway"
    )

    // Fireflies floating animation time
    val fTime by transition.animateFloat(
        initialValue = 0f,
        targetValue = 100f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "fireflyTime"
    )

    // Persistent random coordinates for fireflies
    val fireflies = remember {
        val rand = Random(555)
        List(8) {
            Firefly(
                x = rand.nextFloat(),
                y = 0.75f + rand.nextFloat() * 0.22f, // Bottom part
                scale = 3f + rand.nextFloat() * 4f,
                baseSpeed = 0.05f + rand.nextFloat() * 0.05f,
                phaseOffset = rand.nextFloat() * 50f
            )
        }
    }

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val scale = w / 360f

        // Draw foliage silhouettes in bottom left
        // Outer dark silhouette leaves
        val leftBush = Path().apply {
            moveTo(0f, h)
            quadraticTo(w * 0.1f + swayOffset, h * 0.92f, w * 0.15f + swayOffset, h * 0.86f)
            quadraticTo(w * 0.22f + swayOffset, h * 0.94f, w * 0.3f, h)
            close()
        }
        drawPath(leftBush, Color(0xFF1E1729))

        // Left Pink/Magenta Flowers
        val lf1x = w * 0.08f + swayOffset * 0.5f
        val lf1y = h * 0.9f
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFFFF82C3), Color(0xFFBD2E7A)),
                center = Offset(lf1x, lf1y),
                radius = 18f * scale
            ),
            radius = 18f * scale,
            center = Offset(lf1x, lf1y)
        )
        drawCircle(Color(0xFFFFF4B0), 5f * scale, Offset(lf1x, lf1y)) // Yellow center

        val lf2x = w * 0.18f + swayOffset * 0.6f
        val lf2y = h * 0.93f
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFFFF82C3), Color(0xFFBD2E7A)),
                center = Offset(lf2x, lf2y),
                radius = 15f * scale
            ),
            radius = 15f * scale,
            center = Offset(lf2x, lf2y)
        )
        drawCircle(Color(0xFFFFF4B0), 4f * scale, Offset(lf2x, lf2y))

        // Draw foliage silhouettes in bottom right
        val rightBush = Path().apply {
            moveTo(w, h)
            quadraticTo(w * 0.9f + swayOffset, h * 0.90f, w * 0.82f + swayOffset, h * 0.83f)
            quadraticTo(w * 0.76f + swayOffset, h * 0.93f, w * 0.68f, h)
            close()
        }
        drawPath(rightBush, Color(0xFF1B1526))

        val rf1x = w * 0.88f + swayOffset * 0.5f
        val rf1y = h * 0.88f
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFFFF82C3), Color(0xFFBD2E7A)),
                center = Offset(rf1x, rf1y),
                radius = 20f * scale
            ),
            radius = 20f * scale,
            center = Offset(rf1x, rf1y)
        )
        drawCircle(Color(0xFFFFF4B0), 6f * scale, Offset(rf1x, rf1y))

        // 3. Firefly glow particles drifting up
        fireflies.forEach { ff ->
            // Sine-wave horizontal float combined with continuous vertical drift
            val currentY = h * ff.y - (fTime * ff.baseSpeed * 80f) % (h * 0.25f)
            val currentX = w * ff.x + sin(fTime * 2f + ff.phaseOffset) * 15f * scale

            val pulseAlpha = (sin(fTime * 3f + ff.phaseOffset) * 0.4f + 0.6f)
            
            // Outer glow circle
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFFFFED85).copy(alpha = pulseAlpha * 0.4f), Color.Transparent),
                    center = Offset(currentX, currentY),
                    radius = ff.scale * 4.5f
                ),
                radius = ff.scale * 4.5f,
                center = Offset(currentX, currentY)
            )
            // Bright core circle
            drawCircle(
                color = Color(0xFFFFFA9C).copy(alpha = pulseAlpha),
                radius = ff.scale * 0.8f,
                center = Offset(currentX, currentY)
            )
        }
    }
}
