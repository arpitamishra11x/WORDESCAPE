package com.wordscape.scene

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class AnimalConfig(
    val type: String, // e.g. "cat", "dog", "owl", "fox"
    val size: Float = 1.2f,
    val primaryColor: Color,
    val secondaryColor: Color,
    val position: Offset = Offset.Zero,
    val facing: Direction = Direction.RIGHT
)

enum class Direction { LEFT, RIGHT }

enum class AnimalState { IDLE, WALKING, SITTING, PLAYING, SLEEPING, BLINKING }

interface AnimalEntity {
    val config: AnimalConfig
    val currentState: StateFlow<AnimalState>
    fun setState(state: AnimalState)
    fun getPosition(): Offset
    fun setPosition(position: Offset)
}

class DefaultAnimalEntity(
    override val config: AnimalConfig
) : AnimalEntity {
    private val _currentState = MutableStateFlow(AnimalState.IDLE)
    override val currentState: StateFlow<AnimalState> = _currentState.asStateFlow()

    private var currentPosition = config.position

    override fun setState(state: AnimalState) {
        _currentState.value = state
    }

    override fun getPosition(): Offset {
        return currentPosition
    }

    override fun setPosition(position: Offset) {
        currentPosition = position
    }
}
