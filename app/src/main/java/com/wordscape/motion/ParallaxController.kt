package com.wordscape.motion

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp

data class ParallaxLayer(
    val content: @Composable () -> Unit,
    val speedFactor: Float,
    val offsetY: Float = 0f
)

@Composable
fun ParallaxBackground(
    layers: List<ParallaxLayer>,
    scrollState: Float,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        for (layer in layers) {
            Box(
                modifier = Modifier
                    .graphicsLayer {
                        translationX = -scrollState * layer.speedFactor
                        translationY = layer.offsetY
                    }
            ) {
                layer.content()
            }
        }
    }
}

@Composable
fun rememberAutoParallax(
    speed: Float = 100f,
    duration: Int = 10000,
    reducedMotion: Boolean = false
): Float {
    if (reducedMotion) return 0f
    val transition = rememberInfiniteTransition(label = "autoParallax")
    val offset by transition.animateFloat(
        initialValue = 0f,
        targetValue = speed,
        animationSpec = infiniteRepeatable(
            animation = tween(duration, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "offset"
    )
    return offset
}

fun Modifier.parallaxOffset(scrollState: Float, speedFactor: Float): Modifier {
    return this.offset(x = (scrollState * speedFactor).dp)
}
