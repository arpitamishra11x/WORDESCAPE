package com.wordscape.data.repository

import com.wordscape.data.models.Biome
import com.wordscape.data.models.Category
import com.wordscape.data.models.Scene
import com.wordscape.data.models.World
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorldRepository @Inject constructor() {
    private val _worlds = MutableStateFlow(
        listOf(
            World(
                id = "animals",
                name = "Animals World",
                description = "Learn about friends with fur, feathers, and wings!",
                category = Category.ANIMALS.name,
                scenes = listOf("animals_forest", "animals_meadow"),
                isUnlocked = true,
                requiredWordsToUnlock = 0,
                gradientColors = listOf(0xFF7DD3FC, 0xFFA78BFA), // Sky blue to lavender
                iconName = "pets"
            ),
            World(
                id = "nature",
                name = "Nature World",
                description = "Explore trees, flowers, clouds, and beautiful landscapes.",
                category = Category.NATURE.name,
                scenes = listOf("nature_woods", "nature_clearing"),
                isUnlocked = false,
                requiredWordsToUnlock = 5,
                gradientColors = listOf(0xFF86EFAC, 0xFF22C55E), // Light green to deep green
                iconName = "forest"
            ),
            World(
                id = "space",
                name = "Space World",
                description = "Fly beyond the sky to stars, planets, and moons!",
                category = Category.SPACE.name,
                scenes = listOf("space_orbit"),
                isUnlocked = false,
                requiredWordsToUnlock = 15,
                gradientColors = listOf(0xFF1E1B4B, 0xFF4C1D95), // Night colors
                iconName = "rocket"
            )
        )
    )
    val worlds = _worlds.asStateFlow()

    private val biomes = listOf(
        Biome("forest_biome", "Enchanted Woods", "animals", listOf(0xFF4C1D95, 0xFFF9A8D4), 0xFF86EFAC, "forest_sounds", "leaves"),
        Biome("meadow_biome", "Sunny Plain", "animals", listOf(0xFF7DD3FC, 0xFFFCD34D), 0xFF86EFAC, "nature_ambience", "sparkles"),
        Biome("space_biome", "Outer Orbit", "space", listOf(0xFF0F172A, 0xFF1E1B4B), 0xFF1E293B, "magical_world", "stardust")
    )

    private val scenes = listOf(
        Scene("animals_forest", "animals", "forest_biome", "Whispering Forest", listOf("OWL", "FOX"), "forest"),
        Scene("animals_meadow", "animals", "meadow_biome", "Happy Meadow", listOf("CAT", "DOG", "BEE"), "meadow"),
        Scene("nature_woods", "nature", "forest_biome", "Deep Woods", listOf("TREE", "FLOWER"), "forest"),
        Scene("space_orbit", "space", "space_biome", "Cosmic Horizon", listOf("STAR", "MOON"), "space")
    )

    fun getWorlds(): Flow<List<World>> = worlds

    fun getWorldById(id: String): World? {
        return _worlds.value.firstOrNull { it.id == id }
    }

    fun getScenesForWorld(worldId: String): List<Scene> {
        return scenes.filter { it.worldId == worldId }
    }

    fun getBiome(biomeId: String): Biome? {
        return biomes.firstOrNull { it.id == biomeId }
    }

    suspend fun unlockWorld(worldId: String) {
        val list = _worlds.value.map {
            if (it.id == worldId) it.copy(isUnlocked = true) else it
        }
        _worlds.value = list
    }

    suspend fun checkAndUnlockWorlds(learnedCount: Int) {
        val list = _worlds.value.map { world ->
            if (!world.isUnlocked && learnedCount >= world.requiredWordsToUnlock) {
                world.copy(isUnlocked = true)
            } else {
                world
            }
        }
        _worlds.value = list
    }
}
