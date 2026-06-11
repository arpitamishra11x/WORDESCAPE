package com.wordscape.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wordscape.data.models.Category
import com.wordscape.data.models.JournalEntry
import com.wordscape.data.models.Word
import com.wordscape.data.repository.JournalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class JournalViewModel @Inject constructor(
    private val journalRepository: JournalRepository
) : ViewModel() {

    private val _selectedCategory = MutableStateFlow<Category?>(null)
    val selectedCategory: StateFlow<Category?> = _selectedCategory.asStateFlow()

    val journalEntries: StateFlow<List<Pair<JournalEntry, Word>>> = journalRepository.getJournalWithWords()
        .combine(_selectedCategory) { entries, category ->
            if (category == null) {
                entries
            } else {
                entries.filter { it.second.category.equals(category.name, ignoreCase = true) }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val entryCount: StateFlow<Int> = journalRepository.getEntryCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun setFilter(category: Category?) {
        _selectedCategory.value = category
    }

    fun toggleFavorite(entryId: Int) {
        viewModelScope.launch {
            journalRepository.toggleFavorite(entryId)
        }
    }
}
