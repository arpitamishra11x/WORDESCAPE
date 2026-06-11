package com.wordscape.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "wordscape_settings")

@Singleton
class SettingsRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore = context.dataStore

    companion object {
        val VOICE_GUIDANCE = booleanPreferencesKey("voice_guidance")
        val LARGE_TEXT = booleanPreferencesKey("large_text")
        val DYSLEXIA_FONT = booleanPreferencesKey("dyslexia_font")
        val COLORBLIND_MODE = booleanPreferencesKey("colorblind_mode")
        val HIGH_CONTRAST = booleanPreferencesKey("high_contrast")
        val REDUCED_MOTION = booleanPreferencesKey("reduced_motion")
        val SFX_VOLUME = floatPreferencesKey("sfx_volume")
        val MUSIC_VOLUME = floatPreferencesKey("music_volume")
        val LANGUAGE = stringPreferencesKey("language")
        val PARENT_PIN = stringPreferencesKey("parent_pin")
    }

    val voiceGuidanceEnabled: Flow<Boolean> = dataStore.data.map { it[VOICE_GUIDANCE] ?: true }
    val largeTextEnabled: Flow<Boolean> = dataStore.data.map { it[LARGE_TEXT] ?: false }
    val dyslexiaFontEnabled: Flow<Boolean> = dataStore.data.map { it[DYSLEXIA_FONT] ?: false }
    val colorblindModeEnabled: Flow<Boolean> = dataStore.data.map { it[COLORBLIND_MODE] ?: false }
    val highContrastEnabled: Flow<Boolean> = dataStore.data.map { it[HIGH_CONTRAST] ?: false }
    val reducedMotionEnabled: Flow<Boolean> = dataStore.data.map { it[REDUCED_MOTION] ?: false }
    val soundEffectsVolume: Flow<Float> = dataStore.data.map { it[SFX_VOLUME] ?: 0.8f }
    val musicVolume: Flow<Float> = dataStore.data.map { it[MUSIC_VOLUME] ?: 0.5f }
    val language: Flow<String> = dataStore.data.map { it[LANGUAGE] ?: "en" }
    val parentPinCode: Flow<String> = dataStore.data.map { it[PARENT_PIN] ?: "" }

    suspend fun setVoiceGuidance(enabled: Boolean) {
        dataStore.edit { it[VOICE_GUIDANCE] = enabled }
    }

    suspend fun setLargeText(enabled: Boolean) {
        dataStore.edit { it[LARGE_TEXT] = enabled }
    }

    suspend fun setDyslexiaFont(enabled: Boolean) {
        dataStore.edit { it[DYSLEXIA_FONT] = enabled }
    }

    suspend fun setColorblindMode(enabled: Boolean) {
        dataStore.edit { it[COLORBLIND_MODE] = enabled }
    }

    suspend fun setHighContrast(enabled: Boolean) {
        dataStore.edit { it[HIGH_CONTRAST] = enabled }
    }

    suspend fun setReducedMotion(enabled: Boolean) {
        dataStore.edit { it[REDUCED_MOTION] = enabled }
    }

    suspend fun setSfxVolume(volume: Float) {
        dataStore.edit { it[SFX_VOLUME] = volume }
    }

    suspend fun setMusicVolume(volume: Float) {
        dataStore.edit { it[MUSIC_VOLUME] = volume }
    }

    suspend fun setLanguage(lang: String) {
        dataStore.edit { it[LANGUAGE] = lang }
    }

    suspend fun setParentPinCode(pin: String) {
        dataStore.edit { it[PARENT_PIN] = pin }
    }
}
