package com.wordscape.scene

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.wordscape.motion.ParticleCanvas
import com.wordscape.motion.ParticleEmitter
import com.wordscape.motion.rememberAutoParallax

@Composable
fun SceneRenderer(
    config: SceneConfig,
    assetProvider: SceneAssetProvider = CanvasSceneAssetProvider(),
    showParticles: Boolean = true,
    modifier: Modifier = Modifier
) {
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

    Box(modifier = modifier.fillMaxSize()) {
        // 1. Sky layer
        assetProvider.RenderSky(config = config, animationProgress = animProgress, modifier = Modifier.fillMaxSize())

        // 2. Scene elements layer (Mountains, Islands, Water, Ground, Trees, Flowers, Clouds)
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Mountains
            config.elements.filterIsInstance<SceneElement.Mountain>().forEach {
                assetProvider.drawMountain(this, it)
            }
            
            // Islands
            config.elements.filterIsInstance<SceneElement.Island>().forEach {
                assetProvider.drawIsland(this, it, animProgress)
            }
            
            // Water
            config.elements.filterIsInstance<SceneElement.WaterBody>().forEach {
                assetProvider.drawWater(this, it, animProgress)
            }
        }

        // 3. Ground layer
        assetProvider.RenderGround(config = config, modifier = Modifier.fillMaxSize())

        // 4. Foreground elements layer (Trees, Flowers, Clouds)
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Trees
            config.elements.filterIsInstance<SceneElement.Tree>().forEach {
                assetProvider.drawTree(this, it)
            }

            // Flowers
            config.elements.filterIsInstance<SceneElement.Flower>().forEach {
                assetProvider.drawFlower(this, it)
            }

            // Clouds
            config.elements.filterIsInstance<SceneElement.Cloud>().forEach {
                assetProvider.drawCloud(this, it, animProgress)
            }
        }

        // 5. Particles layer
        if (showParticles && config.hasParticles) {
            val emitter = remember(config) {
                ParticleEmitter(
                    type = config.particleType,
                    emitRate = 12,
                    position = androidx.compose.ui.geometry.Offset(500f, 300f),
                    spread = 800f,
                    colors = listOf(Color(0xFFA78BFA), Color(0xFF7DD3FC), Color(0xFFFCD34D), Color.White)
                )
            }
            ParticleCanvas(
                emitters = listOf(emitter),
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
