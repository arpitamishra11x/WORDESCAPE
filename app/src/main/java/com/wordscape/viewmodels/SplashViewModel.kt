package com.wordscape.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor() : ViewModel() {
    private val _currentPhase = MutableStateFlow(0)
    val currentPhase: StateFlow<Int> = _currentPhase.asStateFlow()

    private val _isAnimationComplete = MutableStateFlow(false)
    val isAnimationComplete: StateFlow<Boolean> = _isAnimationComplete.asStateFlow()

    init {
        startAnimation()
    }

    fun startAnimation() {
        viewModelScope.launch {
            _currentPhase.value = 0
            
            // Phase 1: Sky fades in (800ms)
            delay(800)
            _currentPhase.value = 1
            
            // Phase 2: Floating island appears (800ms)
            delay(800)
            _currentPhase.value = 2
            
            // Phase 3: Crystal Letter A appears (800ms)
            delay(800)
            _currentPhase.value = 3
            
            // Phase 4: WORDSCAPE title appears (800ms)
            delay(800)
            _currentPhase.value = 4
            
            // Phase 5: Subtitle appears (600ms)
            delay(600)
            _currentPhase.value = 5
            
            // Phase 6: Button slides up (600ms)
            delay(600)
            _currentPhase.value = 6
            _isAnimationComplete.value = true
        }
    }

    fun skipAnimation() {
        _currentPhase.value = 6
        _isAnimationComplete.value = true
    }
}
