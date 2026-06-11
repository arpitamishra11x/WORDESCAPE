package com.wordscape.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import android.util.Log
import com.wordscape.data.repository.SettingsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WordScapeAudioManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val settingsRepository: SettingsRepository
) {
    private var soundPool: SoundPool? = null
    private var mediaPlayer: MediaPlayer? = null
    private val soundIds = mutableMapOf<SoundEffect, Int>()
    
    private var sfxVolume = 0.8f
    private var musicVolume = 0.5f

    init {
        val attributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
            
        soundPool = SoundPool.Builder()
            .setMaxStreams(5)
            .setAudioAttributes(attributes)
            .build()

        CoroutineScope(Dispatchers.IO).launch {
            sfxVolume = settingsRepository.soundEffectsVolume.first()
            musicVolume = settingsRepository.musicVolume.first()
            loadPlaceholders()
        }
    }

    private fun loadPlaceholders() {
        // Safe loading of sound resources.
        // For production, actual raw resource files must exist: e.g. R.raw.soft_bell.
        // We will log and catch missing resources since they are placeholders.
        SoundEffect.entries.forEach { effect ->
            try {
                val resId = context.resources.getIdentifier(effect.fileName, "raw", context.packageName)
                if (resId != 0) {
                    val id = soundPool?.load(context, resId, 1) ?: 0
                    if (id != 0) {
                        soundIds[effect] = id
                    }
                }
            } catch (e: Exception) {
                Log.w("WordScapeAudio", "Could not load sfx file: ${effect.fileName}", e)
            }
        }
    }

    fun playSoundEffect(effect: SoundEffect) {
        val soundId = soundIds[effect]
        if (soundId != null && soundId != 0) {
            soundPool?.play(soundId, sfxVolume, sfxVolume, 1, 0, 1.0f)
        } else {
            // Log fallback indicator
            Log.i("WordScapeAudio", "SFX Playback Fallback: ${effect.displayName}")
        }
    }

    fun playAmbientTrack(track: AmbientTrack) {
        try {
            stopAmbientTrack()
            val resId = context.resources.getIdentifier(track.fileName, "raw", context.packageName)
            if (resId != 0) {
                mediaPlayer = MediaPlayer.create(context, resId).apply {
                    isLooping = track.loopable
                    setVolume(musicVolume, musicVolume)
                    start()
                }
            } else {
                Log.i("WordScapeAudio", "Ambient Track Fallback: ${track.displayName}")
            }
        } catch (e: Exception) {
            Log.e("WordScapeAudio", "Error playing ambient track: ${track.displayName}", e)
        }
    }

    fun stopAmbientTrack() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.stop()
            }
            it.release()
        }
        mediaPlayer = null
    }

    fun setSfxVolume(volume: Float) {
        sfxVolume = volume.coerceIn(0f, 1f)
    }

    fun setMusicVolume(volume: Float) {
        musicVolume = volume.coerceIn(0f, 1f)
        mediaPlayer?.setVolume(musicVolume, musicVolume)
    }

    fun release() {
        soundPool?.release()
        soundPool = null
        stopAmbientTrack()
    }
}
