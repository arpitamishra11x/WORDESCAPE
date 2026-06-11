package com.wordscape.scene

import androidx.compose.ui.graphics.Color
import com.wordscape.motion.ParticleType

data class SceneConfig(
    val id: String,
    val skyGradient: List<Color>,
    val groundColor: Color,
    val hasWater: Boolean = false,
    val hasClouds: Boolean = true,
    val hasParticles: Boolean = true,
    val particleType: ParticleType = ParticleType.SPARKLE,
    val timeOfDay: TimeOfDay = TimeOfDay.SUNSET,
    val elements: List<SceneElement> = emptyList()
)

enum class TimeOfDay { DAWN, MORNING, AFTERNOON, SUNSET, NIGHT }

sealed class SceneElement {
    data class Cloud(val x: Float, val y: Float, val scale: Float, val speed: Float) : SceneElement()
    data class Tree(val x: Float, val height: Float, val type: TreeType, val tint: Color? = null) : SceneElement()
    data class Flower(val x: Float, val y: Float, val color: Color) : SceneElement()
    data class Mountain(val x: Float, val height: Float, val color: Color) : SceneElement()
    data class Island(val x: Float, val y: Float, val scale: Float, val hasWaterfall: Boolean) : SceneElement()
    data class WaterBody(val y: Float, val type: WaterType) : SceneElement()
}

enum class TreeType { OAK, PINE, WILLOW, CHERRY, MAGICAL }
enum class WaterType { RIVER, LAKE, WATERFALL, OCEAN }
