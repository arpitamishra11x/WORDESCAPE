package com.wordscape.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wordscape.audio.SoundEffect
import com.wordscape.audio.VoiceNarration
import com.wordscape.audio.WordScapeAudioManager
import com.wordscape.data.models.Word
import com.wordscape.data.repository.ForestRepository
import com.wordscape.data.repository.JournalRepository
import com.wordscape.data.repository.WordRepository
import com.wordscape.domain.DifficultyLevel
import com.wordscape.domain.LearningEngine
import com.wordscape.scene.SceneConfig
import com.wordscape.scene.TimeOfDay
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LearningViewModel @Inject constructor(
    private val wordRepository: WordRepository,
    private val learningEngine: LearningEngine,
    private val journalRepository: JournalRepository,
    private val forestRepository: ForestRepository,
    private val audioManager: WordScapeAudioManager,
    private val voiceNarration: VoiceNarration,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val worldId: String = savedStateHandle["worldId"] ?: "animals"
    private var wordIndex: Int = savedStateHandle["wordIndex"] ?: 0

    private val _currentWord = MutableStateFlow<Word?>(null)
    val currentWord: StateFlow<Word?> = _currentWord.asStateFlow()

    private val _placedLetters = MutableStateFlow<Map<Int, Char>>(emptyMap())
    val placedLetters: StateFlow<Map<Int, Char>> = _placedLetters.asStateFlow()

    private val _availableLetters = MutableStateFlow<List<Char>>(emptyList())
    val availableLetters: StateFlow<List<Char>> = _availableLetters.asStateFlow()

    private val _isWordComplete = MutableStateFlow(false)
    val isWordComplete: StateFlow<Boolean> = _isWordComplete.asStateFlow()

    private val _showAnimal = MutableStateFlow(false)
    val showAnimal: StateFlow<Boolean> = _showAnimal.asStateFlow()

    private val _showNextPath = MutableStateFlow(false)
    val showNextPath: StateFlow<Boolean> = _showNextPath.asStateFlow()

    private val _sceneConfig = MutableStateFlow<SceneConfig?>(null)
    val sceneConfig: StateFlow<SceneConfig?> = _sceneConfig.asStateFlow()

    init {
        loadWord()
    }

    private fun loadWord() {
        viewModelScope.launch {
            _placedLetters.value = emptyMap()
            _isWordComplete.value = false
            _showAnimal.value = false
            _showNextPath.value = false
            
            val word = wordRepository.getNextWord(worldId, wordIndex).first()
            _currentWord.value = word
            
            if (word != null) {
                // Populate available letters with the word's letters shuffled
                val letters = word.text.toList().shuffled()
                _availableLetters.value = letters
                
                // Configure scene settings
                _sceneConfig.value = SceneConfig(
                    id = word.sceneName,
                    skyGradient = when (word.category.uppercase()) {
                        "SPACE" -> listOf(androidx.compose.ui.graphics.Color(0xFF0F172A), androidx.compose.ui.graphics.Color(0xFF1E1B4B))
                        "NATURE" -> listOf(androidx.compose.ui.graphics.Color(0xFF86EFAC), androidx.compose.ui.graphics.Color(0xFF22C55E))
                        else -> listOf(androidx.compose.ui.graphics.Color(0xFF7DD3FC), androidx.compose.ui.graphics.Color(0xFFA78BFA), androidx.compose.ui.graphics.Color(0xFFF9A8D4))
                    },
                    groundColor = androidx.compose.ui.graphics.Color(0xFF86EFAC),
                    timeOfDay = TimeOfDay.SUNSET
                )
            }
        }
    }

    fun onLetterPlaced(letter: Char, slotIndex: Int): Boolean {
        val word = _currentWord.value ?: return false
        
        // Validate correct letter position
        val isCorrect = learningEngine.checkLetterPlacement(letter, slotIndex, word.text)
        if (isCorrect) {
            val updated = _placedLetters.value.toMutableMap().apply {
                put(slotIndex, letter)
            }
            _placedLetters.value = updated
            
            audioManager.playSoundEffect(SoundEffect.CORRECT_PLACE)
            
            // Remove from available letters pool
            val available = _availableLetters.value.toMutableList()
            available.remove(letter)
            _availableLetters.value = available

            // Check word completion
            if (learningEngine.isWordComplete(updated, word.text)) {
                onWordCompleted()
            }
            return true
        } else {
            audioManager.playSoundEffect(SoundEffect.LETTER_DROP)
            return false
        }
    }

    fun onLetterRemoved(slotIndex: Int) {
        val letter = _placedLetters.value[slotIndex] ?: return
        
        val updated = _placedLetters.value.toMutableMap().apply {
            remove(slotIndex)
        }
        _placedLetters.value = updated

        // Put back to available list
        _availableLetters.value = _availableLetters.value + letter
        audioManager.playSoundEffect(SoundEffect.LETTER_DROP)
    }

    private fun onWordCompleted() {
        viewModelScope.launch {
            val word = _currentWord.value ?: return@launch
            _isWordComplete.value = true
            
            audioManager.playSoundEffect(SoundEffect.WORD_COMPLETE)
            
            // TTS pronunciation
            voiceNarration.pronounceWord(word.text)
            
            // Mark learned in db
            wordRepository.markWordLearned(word.id)
            wordRepository.updateMastery(word.id, 5)
            
            // Save in journal
            journalRepository.addEntry(word.id)
            
            // Plant in forest progress
            forestRepository.plantTree(word.id)

            // Trigger animal appear scene reward
            delay(1200)
            if (word.animalType != null) {
                _showAnimal.value = true
                audioManager.playSoundEffect(SoundEffect.ANIMAL_APPEAR)
            }
            
            delay(1800)
            _showNextPath.value = true
        }
    }

    fun loadNextWord() {
        wordIndex++
        loadWord()
    }

    fun onAnimalInteraction(type: String) {
        viewModelScope.launch {
            val word = _currentWord.value ?: return@launch
            wordRepository.incrementInteraction(word.id)
            audioManager.playSoundEffect(SoundEffect.SPARKLE)
            voiceNarration.speak(word.funFact)
        }
    }
}
