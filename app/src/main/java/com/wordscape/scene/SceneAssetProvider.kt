package com.wordscape.scene

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import kotlin.math.sin

interface SceneAssetProvider {
    @Composable
    fun RenderSky(config: SceneConfig, animationProgress: Float, modifier: Modifier)
    
    @Composable
    fun RenderGround(config: SceneConfig, modifier: Modifier)
    
    fun drawCloud(drawScope: DrawScope, cloud: SceneElement.Cloud, progress: Float)
    fun drawTree(drawScope: DrawScope, tree: SceneElement.Tree)
    fun drawIsland(drawScope: DrawScope, island: SceneElement.Island, progress: Float)
    fun drawWater(drawScope: DrawScope, water: SceneElement.WaterBody, progress: Float)
    fun drawFlower(drawScope: DrawScope, flower: SceneElement.Flower)
    fun drawMountain(drawScope: DrawScope, mountain: SceneElement.Mountain)
}

class CanvasSceneAssetProvider : SceneAssetProvider {
    
    @Composable
    override fun RenderSky(config: SceneConfig, animationProgress: Float, modifier: Modifier) {
        Canvas(modifier = modifier.fillMaxSize()) {
            val skyBrush = Brush.verticalGradient(
                colors = config.skyGradient
            )
            drawRect(brush = skyBrush)
            
            // Draw a subtle warm glowing sun or silver moon based on time of day
            val centerX = size.width * 0.75f
            val centerY = size.height * 0.25f + sin(animationProgress) * 10f
            
            when (config.timeOfDay) {
                TimeOfDay.SUNSET -> {
                    // Sun glow
                    drawCircle(
                        color = Color(0xFFFFD166).copy(alpha = 0.2f),
                        radius = 120f,
                        center = Offset(centerX, centerY)
                    )
                    drawCircle(
                        color = Color(0xFFFF8C00).copy(alpha = 0.4f),
                        radius = 80f,
                        center = Offset(centerX, centerY)
                    )
                }
                TimeOfDay.MORNING, TimeOfDay.AFTERNOON -> {
                    drawCircle(
                        color = Color(0xFFFFFDF0).copy(alpha = 0.3f),
                        radius = 100f,
                        center = Offset(centerX, centerY)
                    )
                }
                TimeOfDay.NIGHT -> {
                    // Moon
                    drawCircle(
                        color = Color(0xFFE2E8F0).copy(alpha = 0.8f),
                        radius = 45f,
                        center = Offset(centerX, centerY)
                    )
                    drawCircle(
                        color = Color(0xFF0F172A), // Shade to make crescent
                        radius = 40f,
                        center = Offset(centerX - 15f, centerY - 5f)
                    )
                }
                else -> {}
            }
        }
    }

    @Composable
    override fun RenderGround(config: SceneConfig, modifier: Modifier) {
        Canvas(modifier = modifier.fillMaxSize()) {
            val path = Path().apply {
                val startY = size.height * 0.75f
                moveTo(0f, startY)
                quadraticBezierTo(size.width * 0.35f, startY - 40f, size.width * 0.7f, startY + 20f)
                quadraticBezierTo(size.width * 0.85f, startY + 40f, size.width, startY - 10f)
                lineTo(size.width, size.height)
                lineTo(0f, size.height)
                close()
            }
            
            val groundBrush = Brush.verticalGradient(
                colors = listOf(config.groundColor, config.groundColor.copy(alpha = 0.8f)),
                startY = size.height * 0.7f,
                endY = size.height
            )
            drawPath(path = path, brush = groundBrush)
        }
    }

    override fun drawCloud(drawScope: DrawScope, cloud: SceneElement.Cloud, progress: Float) {
        with(drawScope) {
            val width = size.width
            val cx = ((cloud.x + progress * cloud.speed) * width) % (width + 300f) - 150f
            val cy = cloud.y * size.height
            val scale = cloud.scale
            
            val paintColor = Color.White.copy(alpha = 0.35f)
            
            // Draw cloud as overlapping ellipses
            drawCircle(paintColor, 50f * scale, Offset(cx, cy))
            drawCircle(paintColor, 40f * scale, Offset(cx - 50f * scale, cy + 10f * scale))
            drawCircle(paintColor, 45f * scale, Offset(cx + 50f * scale, cy + 5f * scale))
            drawCircle(paintColor, 35f * scale, Offset(cx - 90f * scale, cy + 15f * scale))
            drawCircle(paintColor, 30f * scale, Offset(cx + 90f * scale, cy + 15f * scale))
        }
    }

    override fun drawTree(drawScope: DrawScope, tree: SceneElement.Tree) {
        with(drawScope) {
            val tx = tree.x * size.width
            val base = size.height * 0.76f
            val th = tree.height
            
            // Trunk
            drawRoundRect(
                color = Color(0xFF78350F),
                topLeft = Offset(tx - 8f, base - th),
                size = Size(16f, th),
                cornerRadius = CornerRadius(4f, 4f)
            )
            
            // Canopy
            val canopyColor = tree.tint ?: when (tree.type) {
                TreeType.PINE -> Color(0xFF065F46)
                TreeType.WILLOW -> Color(0xFF34D399)
                TreeType.CHERRY -> Color(0xFFF9A8D4) // Pink cherry blossom
                TreeType.MAGICAL -> Color(0xFFA78BFA) // Violet magical tree
                else -> Color(0xFF047857) // Oak green
            }
            
            val cy = base - th
            when (tree.type) {
                TreeType.PINE -> {
                    // Triangular layers
                    val path = Path().apply {
                        moveTo(tx, cy - 40f)
                        lineTo(tx - 35f, cy + 10f)
                        lineTo(tx + 35f, cy + 10f)
                        close()
                        
                        moveTo(tx, cy - 20f)
                        lineTo(tx - 45f, cy + 40f)
                        lineTo(tx + 45f, cy + 40f)
                        close()
                    }
                    drawPath(path, canopyColor)
                }
                else -> {
                    // Rounded cloud layers
                    drawCircle(canopyColor, 45f, Offset(tx, cy - 10f))
                    drawCircle(canopyColor.copy(alpha = 0.9f), 35f, Offset(tx - 25f, cy + 10f))
                    drawCircle(canopyColor.copy(alpha = 0.9f), 35f, Offset(tx + 25f, cy + 10f))
                }
            }
        }
    }

    override fun drawIsland(drawScope: DrawScope, island: SceneElement.Island, progress: Float) {
        with(drawScope) {
            val ix = island.x * size.width
            val iy = island.y * size.height + sin(progress * 2f) * 8f // gentle float
            val scale = island.scale
            
            // Rocky dirt bottom
            val dirtPath = Path().apply {
                moveTo(ix - 120f * scale, iy)
                lineTo(ix + 120f * scale, iy)
                quadraticBezierTo(ix + 80f * scale, iy + 60f * scale, ix, iy + 90f * scale)
                quadraticBezierTo(ix - 80f * scale, iy + 60f * scale, ix - 120f * scale, iy)
                close()
            }
            drawPath(dirtPath, Color(0xFF5B3E31))
            
            // Grass top
            drawRoundRect(
                color = Color(0xFF86EFAC),
                topLeft = Offset(ix - 124f * scale, iy - 10f * scale),
                size = Size(248f * scale, 20f * scale),
                cornerRadius = CornerRadius(8f * scale, 8f * scale)
            )
            
            // Waterfall
            if (island.hasWaterfall) {
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF38BDF8), Color(0xFF0284C7).copy(alpha = 0.1f))
                    ),
                    topLeft = Offset(ix - 20f * scale, iy + 5f * scale),
                    size = Size(40f * scale, 120f * scale)
                )
            }
        }
    }

    override fun drawWater(drawScope: DrawScope, water: SceneElement.WaterBody, progress: Float) {
        with(drawScope) {
            val wy = water.y * size.height
            val path = Path().apply {
                moveTo(0f, wy)
                for (x in 0..size.width.toInt() step 50) {
                    val waveY = wy + sin((x / 100f) + progress * 3f) * 6f
                    lineTo(x.toFloat(), waveY)
                }
                lineTo(size.width, size.height)
                lineTo(0f, size.height)
                close()
            }
            
            val waterBrush = Brush.verticalGradient(
                colors = listOf(Color(0xFF38BDF8).copy(alpha = 0.9f), Color(0xFF0369A1)),
                startY = wy,
                endY = size.height
            )
            drawPath(path, waterBrush)
        }
    }

    override fun drawFlower(drawScope: DrawScope, flower: SceneElement.Flower) {
        with(drawScope) {
            val fx = flower.x * size.width
            val fy = flower.y * size.height
            
            // Stem
            drawRect(
                color = Color(0xFF15803D),
                topLeft = Offset(fx - 2f, fy),
                size = Size(4f, size.height * 0.76f - fy)
            )
            
            // Petals
            val r = 8f
            drawCircle(flower.color, r, Offset(fx - r, fy))
            drawCircle(flower.color, r, Offset(fx + r, fy))
            drawCircle(flower.color, r, Offset(fx, fy - r))
            drawCircle(flower.color, r, Offset(fx, fy + r))
            
            // Center
            drawCircle(Color(0xFFFCD34D), 6f, Offset(fx, fy))
        }
    }

    override fun drawMountain(drawScope: DrawScope, mountain: SceneElement.Mountain) {
        with(drawScope) {
            val mx = mountain.x * size.width
            val base = size.height * 0.76f
            val mh = mountain.height
            
            val mountainPath = Path().apply {
                moveTo(mx, base - mh)
                lineTo(mx - 150f, base)
                lineTo(mx + 150f, base)
                close()
            }
            drawPath(mountainPath, mountain.color)
            
            // Snowcap
            val capPath = Path().apply {
                moveTo(mx, base - mh)
                lineTo(mx - 35f, base - mh + 70f)
                lineTo(mx - 15f, base - mh + 50f)
                lineTo(mx, base - mh + 60f)
                lineTo(mx + 15f, base - mh + 50f)
                lineTo(mx + 35f, base - mh + 70f)
                close()
            }
            drawPath(capPath, Color.White.copy(alpha = 0.9f))
        }
    }
}


