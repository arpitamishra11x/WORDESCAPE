package com.wordscape.motion

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import kotlin.random.Random

data class Particle(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    var alpha: Float,
    var size: Float,
    val color: Color,
    var life: Float,
    val maxLife: Float,
    var rotation: Float = 0f,
    var rotationSpeed: Float = 0f
)

enum class ParticleType {
    SPARKLE, DUST, GLOW_TRAIL, MAGICAL, SNOW, LEAVES, BUBBLES, STARDUST
}

class ParticleEmitter(
    val type: ParticleType,
    val emitRate: Int, // particles per second
    var position: Offset,
    val spread: Float = 20f,
    val colors: List<Color> = listOf(Color.White),
    val maxParticles: Int = 100
) {
    private var emitAccumulator = 0f

    fun emit(deltaTime: Float, list: MutableList<Particle>) {
        if (list.size >= maxParticles) return
        emitAccumulator += emitRate * deltaTime
        val toEmit = emitAccumulator.toInt()
        emitAccumulator -= toEmit

        for (i in 0 until toEmit) {
            if (list.size >= maxParticles) break
            val color = colors[Random.nextInt(colors.size)]
            val life = Random.nextFloat() * 0.5f + 0.5f // 0.5 - 1.0s
            
            val vx: Float
            val vy: Float
            val size: Float
            
            when (type) {
                ParticleType.SPARKLE -> {
                    vx = (Random.nextFloat() - 0.5f) * 150f
                    vy = (Random.nextFloat() - 0.5f) * 150f
                    size = Random.nextFloat() * 12f + 4f
                }
                ParticleType.DUST -> {
                    vx = (Random.nextFloat() - 0.5f) * 40f
                    vy = -Random.nextFloat() * 60f - 10f // Drift upwards
                    size = Random.nextFloat() * 8f + 2f
                }
                ParticleType.GLOW_TRAIL -> {
                    vx = (Random.nextFloat() - 0.5f) * 30f
                    vy = (Random.nextFloat() - 0.5f) * 30f
                    size = Random.nextFloat() * 20f + 10f
                }
                ParticleType.MAGICAL -> {
                    vx = (Random.nextFloat() - 0.5f) * 200f
                    vy = -Random.nextFloat() * 180f - 50f
                    size = Random.nextFloat() * 18f + 6f
                }
                ParticleType.LEAVES -> {
                    vx = (Random.nextFloat() - 0.3f) * 80f
                    vy = Random.nextFloat() * 100f + 80f
                    size = Random.nextFloat() * 16f + 8f
                }
                else -> {
                    vx = (Random.nextFloat() - 0.5f) * 100f
                    vy = (Random.nextFloat() - 0.5f) * 100f
                    size = Random.nextFloat() * 10f + 5f
                }
            }

            list.add(
                Particle(
                    x = position.x + (Random.nextFloat() - 0.5f) * spread,
                    y = position.y + (Random.nextFloat() - 0.5f) * spread,
                    vx = vx,
                    vy = vy,
                    alpha = 1f,
                    size = size,
                    color = color,
                    life = life,
                    maxLife = life,
                    rotation = Random.nextFloat() * 360f,
                    rotationSpeed = (Random.nextFloat() - 0.5f) * 360f
                )
            )
        }
    }

    fun update(particles: MutableList<Particle>, deltaTime: Float) {
        val iterator = particles.iterator()
        while (iterator.hasNext()) {
            val p = iterator.next()
            p.life -= deltaTime
            if (p.life <= 0f) {
                iterator.remove()
                continue
            }
            
            p.x += p.vx * deltaTime
            p.y += p.vy * deltaTime
            p.alpha = p.life / p.maxLife
            p.rotation += p.rotationSpeed * deltaTime

            // Apply slight gravity or wind
            if (type == ParticleType.LEAVES) {
                p.vx += (Random.nextFloat() - 0.5f) * 10f
            }
        }
    }
}

@Composable
fun ParticleCanvas(
    emitters: List<ParticleEmitter>,
    modifier: Modifier = Modifier
) {
    val particles = remember { mutableStateListOf<Particle>() }

    LaunchedEffect(emitters) {
        var lastTime = withFrameMillis { it }
        while (true) {
            withFrameMillis { time ->
                val dt = ((time - lastTime) / 1000f).coerceAtMost(0.033f) // Cap dt at ~30fps equivalent
                lastTime = time

                for (emitter in emitters) {
                    emitter.emit(dt, particles)
                    emitter.update(particles, dt)
                }
            }
        }
    }

    Canvas(modifier = modifier) {
        for (p in particles) {
            drawParticle(p)
        }
    }
}

fun DrawScope.drawParticle(p: Particle) {
    val alphaColor = p.color.copy(alpha = p.alpha)
    drawCircle(
        color = alphaColor,
        radius = p.size / 2f,
        center = Offset(p.x, p.y)
    )
}
