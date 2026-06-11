package com.wordscape.audio

import android.content.Context
import android.speech.tts.TextToSpeech
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

interface VoiceNarration {
    suspend fun speak(text: String, language: String = "en")
    suspend fun pronounceWord(word: String, language: String = "en")
    fun stop()
    fun setSpeed(speed: Float)
    fun setVolume(volume: Float)
}

@Singleton
class TtsVoiceNarration @Inject constructor(
    @ApplicationContext private val context: Context
) : VoiceNarration, TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var isInitialized = false
    private var queuedText: String? = null

    init {
        tts = TextToSpeech(context, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.language = Locale.US
            isInitialized = true
            // Speak queued text if any
            queuedText?.let {
                speakText(it)
                queuedText = null
            }
        }
    }

    override suspend fun speak(text: String, language: String) {
        if (isInitialized) {
            setTtsLanguage(language)
            speakText(text)
        } else {
            queuedText = text
        }
    }

    override suspend fun pronounceWord(word: String, language: String) {
        val message = "Spelled: $word"
        speak(message, language)
    }

    override fun stop() {
        tts?.stop()
    }

    override fun setSpeed(speed: Float) {
        tts?.setSpeechRate(speed)
    }

    override fun setVolume(volume: Float) {
        // TextToSpeech does not expose direct volume setter, handled by audio channels
    }

    private fun setTtsLanguage(lang: String) {
        val locale = when (lang.lowercase()) {
            "fr" -> Locale.FRANCE
            "es" -> Locale("es", "ES")
            else -> Locale.US
        }
        tts?.language = locale
    }

    private fun speakText(text: String) {
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }
}
