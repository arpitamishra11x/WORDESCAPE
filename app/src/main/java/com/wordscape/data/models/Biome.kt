package com.wordscape.data.models

data class Biome(
    val id: String,
    val name: String,
    val worldId: String,
    val skyGradient: List<Long>,
    val groundColor: Long,
    val ambientSoundName: String,
    val particleType: String // e.g. "leaves", "snowflakes", "bubbles", "stardust"
)
