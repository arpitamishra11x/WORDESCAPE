package com.wordscape.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wordscape.data.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val voiceGuidance: StateFlow<Boolean> = settingsRepository.voiceGuidanceEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val largeText: StateFlow<Boolean> = settingsRepository.largeTextEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val dyslexiaFont: StateFlow<Boolean> = settingsRepository.dyslexiaFontEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val colorblindMode: StateFlow<Boolean> = settingsRepository.colorblindModeEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val highContrast: StateFlow<Boolean> = settingsRepository.highContrastEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val reducedMotion: StateFlow<Boolean> = settingsRepository.reducedMotionEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val sfxVolume: StateFlow<Float> = settingsRepository.soundEffectsVolume
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.8f)

    val musicVolume: StateFlow<Float> = settingsRepository.musicVolume
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.5f)

    val language: StateFlow<String> = settingsRepository.language
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "en")

    fun setVoiceGuidance(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.setVoiceGuidance(enabled) }
    }

    fun setLargeText(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.setLargeText(enabled) }
    }

    fun setDyslexiaFont(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.setDyslexiaFont(enabled) }
    }

    fun setColorblindMode(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.setColorblindMode(enabled) }
    }

    fun setHighContrast(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.setHighContrast(enabled) }
    }

    fun setReducedMotion(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.setReducedMotion(enabled) }
    }

    fun setSfxVolume(volume: Float) {
        viewModelScope.launch { settingsRepository.setSfxVolume(volume) }
    }

    fun setMusicVolume(volume: Float) {
        viewModelScope.launch { settingsRepository.setMusicVolume(volume) }
    }

    fun setLanguage(lang: String) {
        viewModelScope.launch { settingsRepository.setLanguage(lang) }
    }
}
