package com.wordscape.data.models

data class Scene(
    val id: String,
    val worldId: String,
    val biomeId: String,
    val name: String,
    val words: List<String>, // Word texts in this scene
    val backgroundType: String,
    val isCompleted: Boolean = false
)
