package com.wordscape.scene

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput

sealed class AnimalInteractionType {
    object Pet : AnimalInteractionType()
    object Feed : AnimalInteractionType()
    object Play : AnimalInteractionType()
    object Tap : AnimalInteractionType()
}

interface AnimalInteractionHandler {
    fun onInteraction(entity: AnimalEntity, type: AnimalInteractionType)
    fun getAvailableInteractions(entity: AnimalEntity): List<AnimalInteractionType>
}

class DefaultAnimalInteractionHandler(
    private val onTriggerSfx: (String) -> Unit
) : AnimalInteractionHandler {
    
    override fun onInteraction(entity: AnimalEntity, type: AnimalInteractionType) {
        when (type) {
            AnimalInteractionType.Tap -> {
                entity.setState(AnimalState.BLINKING)
                onTriggerSfx("tiny_pop")
            }
            AnimalInteractionType.Pet -> {
                entity.setState(AnimalState.SITTING)
                onTriggerSfx("sparkle")
            }
            AnimalInteractionType.Feed -> {
                entity.setState(AnimalState.PLAYING)
                onTriggerSfx("magical_chime")
            }
            AnimalInteractionType.Play -> {
                entity.setState(AnimalState.WALKING)
                onTriggerSfx("soft_bell")
            }
        }
    }

    override fun getAvailableInteractions(entity: AnimalEntity): List<AnimalInteractionType> {
        return listOf(
            AnimalInteractionType.Tap,
            AnimalInteractionType.Pet,
            AnimalInteractionType.Feed,
            AnimalInteractionType.Play
        )
    }
}

@Composable
fun Modifier.animalInteractions(
    entity: AnimalEntity,
    handler: AnimalInteractionHandler
): Modifier {
    return this.pointerInput(entity) {
        detectTapGestures(
            onTap = { handler.onInteraction(entity, AnimalInteractionType.Tap) },
            onDoubleTap = { handler.onInteraction(entity, AnimalInteractionType.Feed) },
            onLongPress = { handler.onInteraction(entity, AnimalInteractionType.Pet) }
        )
    }
}
