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
import androidx.compose.ui.graphics.graphicsLayer

@Composable
fun rememberFloatingState(
    amplitude: Float = 8f,
    duration: Int = 3000,
    reducedMotion: Boolean = false
): Float {
    if (reducedMotion) return 0f
    val transition = rememberInfiniteTransition(label = "floating")
    val offsetY by transition.animateFloat(
        initialValue = -amplitude,
        targetValue = amplitude,
        animationSpec = infiniteRepeatable(
            animation = tween(duration, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offsetY"
    )
    return offsetY
}

@Composable
fun rememberGentleRotation(
    maxDegrees: Float = 3f,
    duration: Int = 4000,
    reducedMotion: Boolean = false
): Float {
    if (reducedMotion) return 0f
    val transition = rememberInfiniteTransition(label = "rotation")
    val rotation by transition.animateFloat(
        initialValue = -maxDegrees,
        targetValue = maxDegrees,
        animationSpec = infiniteRepeatable(
            animation = tween(duration, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "rotation"
    )
    return rotation
}

@Composable
fun Modifier.floating(
    amplitude: Float = 8f,
    duration: Int = 3000,
    reducedMotion: Boolean = false
): Modifier {
    val offsetY = rememberFloatingState(amplitude, duration, reducedMotion)
    return this.graphicsLayer {
        translationY = offsetY
    }
}

@Composable
fun Modifier.gentleRotate(
    maxDegrees: Float = 3f,
    duration: Int = 4000,
    reducedMotion: Boolean = false
): Modifier {
    val rotation = rememberGentleRotation(maxDegrees, duration, reducedMotion)
    return this.graphicsLayer {
        rotationZ = rotation
    }
}

@Composable
fun Modifier.floatAndRotate(
    amplitude: Float = 8f,
    maxDegrees: Float = 3f,
    floatDuration: Int = 3000,
    rotateDuration: Int = 4000,
    reducedMotion: Boolean = false
): Modifier {
    val offsetY = rememberFloatingState(amplitude, floatDuration, reducedMotion)
    val rotation = rememberGentleRotation(maxDegrees, rotateDuration, reducedMotion)
    return this.graphicsLayer {
        translationY = offsetY
        rotationZ = rotation
    }
}
