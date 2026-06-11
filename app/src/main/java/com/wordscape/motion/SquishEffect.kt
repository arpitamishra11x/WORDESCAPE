package com.wordscape.motion

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class SquishState(
    val scaleX: Animatable<Float, *>,
    val scaleY: Animatable<Float, *>
) {
    suspend fun press(amount: Float = 0.15f) {
        coroutineScope {
            launch {
                scaleX.animateTo(
                    targetValue = 1f + amount,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
            }
            launch {
                scaleY.animateTo(
                    targetValue = 1f - amount,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
            }
        }
    }

    suspend fun release() {
        coroutineScope {
            launch {
                scaleX.animateTo(
                    targetValue = 1f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                )
            }
            launch {
                scaleY.animateTo(
                    targetValue = 1f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                )
            }
        }
    }
}

@Composable
fun rememberSquishState(): SquishState {
    val scaleX = remember { Animatable(1f) }
    val scaleY = remember { Animatable(1f) }
    return remember { SquishState(scaleX, scaleY) }
}

@Composable
fun Modifier.squishOnPress(amount: Float = 0.15f): Modifier {
    val state = rememberSquishState()
    val scope = rememberCoroutineScope()
    
    return this
        .graphicsLayer {
            scaleX = state.scaleX.value
            scaleY = state.scaleY.value
        }
        .pointerInput(Unit) {
            forEachGesture {
                awaitPointerEventScope {
                    awaitFirstDown(requireUnconsumed = false)
                    scope.launch { state.press(amount) }
                    
                    // Wait for touch up
                    do {
                        val event = awaitPointerEvent()
                        val anyDown = event.changes.any { it.pressed }
                    } while (anyDown)
                    
                    scope.launch { state.release() }
                }
            }
        }
}

fun Modifier.squishOnDrag(dragOffset: Offset): Modifier {
    // Simple deformation calculation based on velocity/direction
    val maxDeformation = 0.2f
    val length = dragOffset.isSpecified().let { if (it) dragOffset.getDistance() else 0f }
    val clamp = (length / 800f).coerceAtMost(maxDeformation)
    
    val sx = 1f - clamp
    val sy = 1f + clamp

    return this.graphicsLayer {
        scaleX = sx
        scaleY = sy
    }
}

private fun Offset.isSpecified(): Boolean {
    return this.x.isFinite() && this.y.isFinite()
}
