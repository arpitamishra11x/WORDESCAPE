package com.wordscape.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wordscape.data.models.ForestProgress
import com.wordscape.data.models.ForestTree
import com.wordscape.data.models.ForestZone
import com.wordscape.data.models.Word
import com.wordscape.data.repository.ForestRepository
import com.wordscape.data.repository.WordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ForestViewModel @Inject constructor(
    private val forestRepository: ForestRepository,
    private val wordRepository: WordRepository
) : ViewModel() {

    val trees: StateFlow<List<ForestTree>> = forestRepository.trees
    val zones: StateFlow<List<ForestZone>> = forestRepository.zones
    val forestProgress: StateFlow<ForestProgress> = forestRepository.progress

    val treeCount: StateFlow<Int> = trees
        .map { it.size }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val currentZoneName: StateFlow<String> = zones.map { list ->
        list.lastOrNull { it.isUnlocked }?.name ?: "Whispering Glade"
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Whispering Glade")

    val nextMilestone: StateFlow<Pair<String, Int>> = treeCount.map { count ->
        when {
            count < 10 -> Pair("Sunlit Meadow", 10)
            count < 25 -> Pair("Crystal Brook", 25)
            count < 50 -> Pair("Starlight Canopy", 50)
            count < 100 -> Pair("Enchanted Depths", 100)
            count < 200 -> Pair("Ancient Grove", 200)
            else -> Pair("Mythical Realm", 500)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Pair("Sunlit Meadow", 10))

    fun getWordForTree(tree: ForestTree): Flow<Word?> {
        return wordRepository.getWordById(tree.wordId)
    }

    fun getForestLevelName(): String {
        return forestProgress.value.getForestLevelName()
    }
}
