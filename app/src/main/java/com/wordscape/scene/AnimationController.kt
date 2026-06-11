package com.wordscape.scene

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnimationController @Inject constructor() {
    private val _globalTime = MutableStateFlow(0f)
    val globalTime = _globalTime.asStateFlow()

    private val _isPaused = MutableStateFlow(false)
    val isPaused = _isPaused.asStateFlow()

    fun updateTime(deltaTime: Float) {
        if (!_isPaused.value) {
            _globalTime.value += deltaTime
        }
    }

    fun pause() {
        _isPaused.value = true
    }

    fun resume() {
        _isPaused.value = false
    }

    fun getPhase(period: Float): Float {
        if (period <= 0f) return 0f
        return (_globalTime.value % period) / period
    }
}
