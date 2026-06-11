package com.wordscape.audio

enum class SoundEffect(
    val displayName: String,
    val fileName: String,
    val category: SoundCategory
) {
    SOFT_BELL("Soft Bell", "soft_bell", SoundCategory.FEEDBACK),
    TINY_POP("Tiny Pop", "tiny_pop", SoundCategory.FEEDBACK),
    SPARKLE("Sparkle", "sparkle", SoundCategory.MAGICAL),
    MAGICAL_CHIME("Magical Chime", "magical_chime", SoundCategory.MAGICAL),
    CORRECT_PLACE("Correct Placement", "correct_place", SoundCategory.FEEDBACK),
    WORD_COMPLETE("Word Complete", "word_complete", SoundCategory.ACHIEVEMENT),
    LETTER_PICKUP("Letter Pickup", "letter_pickup", SoundCategory.INTERACTION),
    LETTER_DROP("Letter Drop", "letter_drop", SoundCategory.INTERACTION),
    PAGE_TURN("Page Turn", "page_turn", SoundCategory.UI),
    BUTTON_TAP("Button Tap", "button_tap", SoundCategory.UI),
    TREE_GROW("Tree Grow", "tree_grow", SoundCategory.ACHIEVEMENT),
    ANIMAL_APPEAR("Animal Appear", "animal_appear", SoundCategory.MAGICAL);
}

enum class SoundCategory { FEEDBACK, MAGICAL, ACHIEVEMENT, INTERACTION, UI }
