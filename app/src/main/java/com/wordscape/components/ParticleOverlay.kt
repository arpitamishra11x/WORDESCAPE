package com.wordscape.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.wordscape.motion.ParticleCanvas
import com.wordscape.motion.ParticleEmitter
import com.wordscape.motion.ParticleType

@Composable
fun ParticleOverlay(
    type: ParticleType = ParticleType.SPARKLE,
    density: Float = 1f,
    modifier: Modifier = Modifier
) {
    val emitters = remember(type, density) {
        val rate = (10 * density).toInt()
        val colors = when (type) {
            ParticleType.LEAVES -> listOf(Color(0xFF86EFAC), Color(0xFF4ADE80), Color(0xFF22C55E))
            ParticleType.MAGICAL -> listOf(Color(0xFFA78BFA), Color(0xFFF9A8D4), Color(0xFFFCD34D), Color(0xFF7DD3FC))
            ParticleType.GLOW_TRAIL -> listOf(Color(0xFFA78BFA).copy(alpha = 0.4f), Color(0xFF7DD3FC).copy(alpha = 0.4f))
            else -> listOf(Color.White, Color(0xFFFFFDF0), Color(0xFFE2E8F0))
        }
        
        listOf(
            ParticleEmitter(
                type = type,
                emitRate = rate,
                position = Offset(500f, 200f), // Will spawn around this area
                spread = 1000f,
                colors = colors
            )
        )
    }

    ParticleCanvas(
        emitters = emitters,
        modifier = modifier.fillMaxSize()
    )
}
