package com.wordscape.utils

import android.content.Context
import android.view.accessibility.AccessibilityManager as AndroidAccessibilityManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.wordscape.data.repository.SettingsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import javax.inject.Singleton

enum class ColorblindType { NONE, PROTANOPIA, DEUTERANOPIA, TRITANOPIA }

@Singleton
class AccessibilityManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val settingsRepository: SettingsRepository
) {
    private val scope = CoroutineScope(Dispatchers.Main)

    private val androidAccessibilityManager =
        context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AndroidAccessibilityManager

    val isScreenReaderActive: StateFlow<Boolean> = MutableStateFlow(
        androidAccessibilityManager.isEnabled && androidAccessibilityManager.isTouchExplorationEnabled
    )

    val isLargeTextEnabled: StateFlow<Boolean> = settingsRepository.largeTextEnabled
        .stateIn(scope, SharingStarted.WhileSubscribed(5000), false)

    val isDyslexiaFontEnabled: StateFlow<Boolean> = settingsRepository.dyslexiaFontEnabled
        .stateIn(scope, SharingStarted.WhileSubscribed(5000), false)

    val isHighContrastEnabled: StateFlow<Boolean> = settingsRepository.highContrastEnabled
        .stateIn(scope, SharingStarted.WhileSubscribed(5000), false)

    val isReducedMotionEnabled: StateFlow<Boolean> = settingsRepository.reducedMotionEnabled
        .stateIn(scope, SharingStarted.WhileSubscribed(5000), false)

    val isColorblindModeEnabled: StateFlow<Boolean> = settingsRepository.colorblindModeEnabled
        .stateIn(scope, SharingStarted.WhileSubscribed(5000), false)

    val colorblindType: StateFlow<ColorblindType> = MutableStateFlow(ColorblindType.NONE)

    fun getMinTouchTarget(): Dp {
        return if (isLargeTextEnabled.value) 56.dp else 48.dp
    }

    fun getAnimationDuration(normalMs: Int): Int {
        return if (isReducedMotionEnabled.value) 0 else normalMs
    }

    fun getAdjustedColor(color: Color): Color {
        if (!isColorblindModeEnabled.value) return color
        return when (colorblindType.value) {
            ColorblindType.PROTANOPIA -> adjustForProtanopia(color)
            ColorblindType.DEUTERANOPIA -> adjustForDeuteranopia(color)
            ColorblindType.TRITANOPIA -> adjustForTritanopia(color)
            else -> color
        }
    }

    private fun adjustForProtanopia(color: Color): Color {
        // Red-green blind adjustment
        val r = color.red * 0.567f + color.green * 0.433f
        val g = color.red * 0.558f + color.green * 0.442f
        val b = color.blue
        return Color(r, g, b, color.alpha)
    }

    private fun adjustForDeuteranopia(color: Color): Color {
        // Green-red blind adjustment
        val r = color.red * 0.625f + color.green * 0.375f
        val g = color.red * 0.7f + color.green * 0.3f
        val b = color.blue
        return Color(r, g, b, color.alpha)
    }

    private fun adjustForTritanopia(color: Color): Color {
        // Blue-yellow blind adjustment
        val r = color.red
        val g = color.green * 0.95f + color.blue * 0.05f
        val b = color.green * 0.433f + color.blue * 0.567f
        return Color(r, g, b, color.alpha)
    }
}
