package com.wordscape.motion

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset

data class PhysicsBody(
    val x: Float,
    val y: Float,
    val vx: Float,
    val vy: Float,
    val mass: Float = 1f,
    val friction: Float = 0.98f,
    val bounciness: Float = 0.6f
)

data class SpringConfig(
    val stiffness: Float,
    val damping: Float,
    val mass: Float = 1f
) {
    companion object {
        val GENTLE = SpringConfig(100f, 15f)
        val BOUNCY = SpringConfig(300f, 10f)
        val SNAPPY = SpringConfig(500f, 20f)
    }
}

fun applySpring(
    body: PhysicsBody,
    targetX: Float,
    targetY: Float,
    config: SpringConfig,
    dt: Float
): PhysicsBody {
    // F = -k*x - c*v
    val dx = body.x - targetX
    val dy = body.y - targetY
    
    val forceX = -config.stiffness * dx - config.damping * body.vx
    val forceY = -config.stiffness * dy - config.damping * body.vy
    
    val ax = forceX / config.mass
    val ay = forceY / config.mass
    
    val vx = (body.vx + ax * dt) * body.friction
    val vy = (body.vy + ay * dt) * body.friction
    
    val x = body.x + vx * dt
    val y = body.y + vy * dt
    
    return body.copy(x = x, y = y, vx = vx, vy = vy)
}

fun applyGravity(body: PhysicsBody, gravity: Float, dt: Float): PhysicsBody {
    val vy = body.vy + gravity * dt
    val y = body.y + vy * dt
    return body.copy(y = y, vy = vy)
}

@Composable
fun rememberPhysicsState(
    initialX: Float,
    initialY: Float,
    config: SpringConfig = SpringConfig.BOUNCY
): Animatable<Offset, *> {
    return remember {
        Animatable(
            initialValue = Offset(initialX, initialY),
            typeConverter = Offset.VectorConverter
        )
    }
}
