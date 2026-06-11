package com.wordscape.utils

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Modifier.accessibleClickable(
    label: String,
    role: Role? = Role.Button,
    onClick: () -> Unit
): Modifier {
    return this
        .semantics {
            contentDescription = label
            if (role != null) {
                this.role = role
            }
        }
        .clickable(onClick = onClick)
}

fun Modifier.minTouchTarget(size: Dp = 48.dp): Modifier {
    return this.defaultMinSize(minWidth = size, minHeight = size)
}

fun Modifier.screenReaderHeading(): Modifier {
    return this.semantics {
        heading()
    }
}

fun letterContentDescription(letter: Char, isPlaced: Boolean): String {
    return if (isPlaced) {
        "Letter $letter, placed correctly."
    } else {
        "Letter $letter, draggable puzzle piece."
    }
}

fun wordSlotContentDescription(index: Int, total: Int, filled: Char?): String {
    return if (filled != null) {
        "Slot ${index + 1} of $total, filled with letter $filled."
    } else {
        "Slot ${index + 1} of $total, empty."
    }
}

fun animalContentDescription(type: String, state: String): String {
    return "A cute magical $type, currently $state. Double tap to feed, hold to pet."
}

fun forestTreeContentDescription(wordText: String, stage: Int): String {
    val stageName = when (stage) {
        0 -> "Seed"
        1 -> "Sprout"
        2 -> "Sapling"
        3 -> "Young Tree"
        else -> "Mature Tree"
    }
    return "A $stageName grown from learning the word: $wordText"
}
