package com.wordscape.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wordscape.data.models.Category
import com.wordscape.data.models.World
import com.wordscape.data.repository.SettingsRepository
import com.wordscape.data.repository.WordRepository
import com.wordscape.data.repository.WorldRepository
import com.wordscape.utils.getGreeting
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val wordRepository: WordRepository,
    private val worldRepository: WorldRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val greeting: StateFlow<String> = kotlinx.coroutines.flow.flow {
        emit(getGreeting())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Hello, Explorer ✨")

    val currentWorld: StateFlow<World?> = worldRepository.getWorlds()
        .map { it.firstOrNull { world -> world.isUnlocked } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val wordsLearned: StateFlow<Int> = wordRepository.getLearnedCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val currentStreak: StateFlow<Int> = kotlinx.coroutines.flow.flow {
        emit(3) // Dummy streak value for UI
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 3)

    val todayCategory: StateFlow<Category> = currentWorld
        .map { Category.fromName(it?.category ?: "ANIMALS") }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Category.ANIMALS)
}
