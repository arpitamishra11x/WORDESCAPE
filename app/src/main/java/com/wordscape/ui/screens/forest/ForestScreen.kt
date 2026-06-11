package com.wordscape.ui.screens.forest

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.wordscape.components.AnimatedSky
import com.wordscape.components.GlassCard
import com.wordscape.components.MagicalText
import com.wordscape.components.ParticleOverlay
import com.wordscape.data.models.ForestTree
import com.wordscape.motion.ParticleType
import com.wordscape.scene.CanvasSceneAssetProvider
import com.wordscape.scene.SceneElement
import com.wordscape.scene.TreeType
import com.wordscape.viewmodels.ForestViewModel
import kotlin.math.sin

@Composable
fun ForestScreen(
    viewModel: ForestViewModel = hiltViewModel()
) {
    val trees by viewModel.trees.collectAsState()
    val count by viewModel.treeCount.collectAsState()
    val zoneName by viewModel.currentZoneName.collectAsState()
    val milestone by viewModel.nextMilestone.collectAsState()

    var animTime by remember { mutableStateOf(0f) }

    LaunchedEffect(Unit) {
        var lastTime = withFrameMillis { it }
        while (true) {
            withFrameMillis { time ->
                val dt = (time - lastTime) / 1000f
                lastTime = time
                animTime += dt
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Dawn/Sunrise theme sky background
        AnimatedSky(
            timeOfDay = com.wordscape.scene.TimeOfDay.DAWN,
            showClouds = true,
            modifier = Modifier.fillMaxSize()
        )

        // Leaf Particles floating down
        ParticleOverlay(
            type = ParticleType.LEAVES,
            density = 1f,
            modifier = Modifier.fillMaxSize()
        )

        // Forest rolling hills and trees rendering
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val groundY = h * 0.76f

            // Hill 1 (Back hill - darker green)
            val path1 = Path().apply {
                moveTo(0f, groundY - 60f)
                quadraticBezierTo(w * 0.3f, groundY - 120f, w * 0.6f, groundY - 30f)
                quadraticBezierTo(w * 0.8f, groundY + 10f, w, groundY - 40f)
                lineTo(w, h)
                lineTo(0f, h)
                close()
            }
            drawPath(path1, Color(0xFF166534))

            // Hill 2 (Front hill - lighter green)
            val path2 = Path().apply {
                moveTo(0f, groundY + 20f)
                quadraticBezierTo(w * 0.4f, groundY - 40f, w * 0.75f, groundY + 10f)
                quadraticBezierTo(w * 0.9f, groundY + 30f, w, groundY - 10f)
                lineTo(w, h)
                lineTo(0f, h)
                close()
            }
            drawPath(path2, Color(0xFF22C55E))

            // Draw trees
            val assetProvider = CanvasSceneAssetProvider()
            trees.forEach { tree ->
                // Apply a gentle sway animation to trees
                val sway = sin(animTime * 1.5f + tree.positionX * 10f) * 4f
                val th = 70f + (tree.wordId % 3) * 15f
                
                val relativeTree = SceneElement.Tree(
                    x = tree.positionX,
                    height = th,
                    type = when (tree.treeType) {
                        "willow" -> TreeType.WILLOW
                        "cherry_blossom" -> TreeType.CHERRY
                        "magical" -> TreeType.MAGICAL
                        else -> TreeType.OAK
                    },
                    tint = Color(tree.color)
                )

                // Render tree trunk and canopy with custom offsets for sway
                drawTreeWithSway(this, relativeTree, sway, groundY)
            }
        }

        // Overlay UI HUD
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header stats
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MagicalText(
                    text = "Word Forest",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp
                    ),
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Your living progress world",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // Stats overlay
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                backgroundAlpha = 0.15f
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🌳",
                        fontSize = 32.sp
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "Zone: $zoneName",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Total Trees: $count",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Progress to next zone indicator
                val targetCount = milestone.second
                val progressFraction = if (targetCount > 0) (count.toFloat() / targetCount).coerceAtMost(1f) else 1f
                
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Next: ${milestone.first}",
                            color = Color.White.copy(alpha = 0.6f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "$count/$targetCount",
                            color = Color.White.copy(alpha = 0.6f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    // Glass style progress bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(50))
                            .background(color = Color.White.copy(alpha = 0.15f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(progressFraction)
                                .height(8.dp)
                                .clip(RoundedCornerShape(50))
                                .background(color = Color(0xFF86EFAC))
                        )
                    }
                }
            }
        }
    }
}

private fun drawTreeWithSway(
    drawScope: androidx.compose.ui.graphics.drawscope.DrawScope,
    tree: SceneElement.Tree,
    sway: Float,
    groundY: Float
) {
    with(drawScope) {
        val tx = tree.x * size.width
        val base = groundY + (tree.x % 0.1f) * 200f
        val th = tree.height
        
        // Trunk with sway rotation
        drawScope.drawLine(
            color = Color(0xFF78350F),
            start = Offset(tx, base),
            end = Offset(tx + sway, base - th),
            strokeWidth = 10f
        )
        
        // Canopy circular cluster
        val cy = base - th
        val cx = tx + sway
        val r = 35f
        
        drawScope.drawCircle(tree.tint ?: Color(0xFF047857), r, Offset(cx, cy))
        drawScope.drawCircle((tree.tint ?: Color(0xFF047857)).copy(alpha = 0.85f), r * 0.8f, Offset(cx - 20f, cy + 5f))
        drawScope.drawCircle((tree.tint ?: Color(0xFF047857)).copy(alpha = 0.85f), r * 0.8f, Offset(cx + 20f, cy + 5f))
    }
}
