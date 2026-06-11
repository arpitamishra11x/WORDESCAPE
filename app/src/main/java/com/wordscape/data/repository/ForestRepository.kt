package com.wordscape.data.repository

import com.wordscape.data.models.ForestProgress
import com.wordscape.data.models.ForestTree
import com.wordscape.data.models.ForestZone
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class ForestRepository @Inject constructor() {
    private val _trees = MutableStateFlow<List<ForestTree>>(emptyList())
    val trees: StateFlow<List<ForestTree>> = _trees.asStateFlow()

    private val _zones = MutableStateFlow<List<ForestZone>>(
        listOf(
            ForestZone(1, "Whispering Glade", "A quiet sunlit meadow where your journey begins.", 0, true, "temperate"),
            ForestZone(2, "Sunlit Meadow", "A breezy field filled with blooming flowers.", 5, false, "temperate"),
            ForestZone(3, "Crystal Brook", "A fresh clearing next to a sparkling stream.", 15, false, "temperate"),
            ForestZone(4, "Starlight Canopy", "A glowing forest zone that sparkles under the night sky.", 30, false, "magical"),
            ForestZone(5, "Enchanted Depths", "A deep, mysterious valley rich with magic.", 50, false, "magical"),
            ForestZone(6, "Ancient Grove", "An old clearing where nature spirits dwell.", 100, false, "mystic"),
            ForestZone(7, "Mythical Realm", "A floating wonderland high above the clouds.", 200, false, "mystic")
        )
    )
    val zones: StateFlow<List<ForestZone>> = _zones.asStateFlow()

    private val _progress = MutableStateFlow(ForestProgress(0, 1, listOf(1), 1))
    val progress: StateFlow<ForestProgress> = _progress.asStateFlow()

    fun getForestProgress(): Flow<ForestProgress> = progress
    fun getTrees(): Flow<List<ForestTree>> = trees
    fun getZones(): Flow<List<ForestZone>> = zones

    suspend fun plantTree(wordId: Int): ForestTree {
        val currentTrees = _trees.value
        val newId = currentTrees.size + 1
        
        // Random position on ground level
        val px = Random.nextFloat() * 0.8f + 0.1f // Keep away from edges
        val py = Random.nextFloat() * 0.15f + 0.65f // Ground y-range
        
        val treeTypes = listOf("oak", "willow", "cherry_blossom", "magical")
        val randomType = treeTypes[Random.nextInt(treeTypes.size)]
        val treeColors = listOf(0xFF86EFAC, 0xFFA78BFA, 0xFFF9A8D4, 0xFF7DD3FC)
        val randomColor = treeColors[Random.nextInt(treeColors.size)]

        val newTree = ForestTree(
            id = newId,
            wordId = wordId,
            treeType = randomType,
            growthStage = 4, // mature immediately for reward, or could grow over time
            positionX = px,
            positionY = py,
            plantedAt = System.currentTimeMillis(),
            color = randomColor
        )

        val updatedTrees = currentTrees + newTree
        _trees.value = updatedTrees

        // Update progress and zone unlocks
        val total = updatedTrees.size
        val unlockedList = mutableListOf<Int>()
        var activeZoneId = 1
        
        val updatedZones = _zones.value.map { zone ->
            val unlocked = total >= zone.unlockThreshold
            if (unlocked) {
                unlockedList.add(zone.id)
                activeZoneId = zone.id
            }
            zone.copy(isUnlocked = unlocked)
        }
        _zones.value = updatedZones

        val level = when {
            total < 5 -> 1
            total < 15 -> 2
            total < 30 -> 3
            total < 50 -> 4
            else -> 5
        }

        _progress.value = ForestProgress(
            totalTrees = total,
            currentZoneId = activeZoneId,
            unlockedZones = unlockedList,
            forestLevel = level,
            lastTreePlantedAt = System.currentTimeMillis()
        )

        return newTree
    }
}
