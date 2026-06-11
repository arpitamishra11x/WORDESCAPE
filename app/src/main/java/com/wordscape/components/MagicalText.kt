package com.wordscape.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun MagicalText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = TextStyle.Default,
    shimmer: Boolean = false,
    fadeIn: Boolean = true,
    fadeInDelay: Int = 0,
    letterSpacing: TextUnit = 8.sp,
    color: Color = Color.White
) {
    val textAlpha = remember { Animatable(0f) }

    LaunchedEffect(fadeIn, fadeInDelay) {
        if (fadeIn) {
            delay(fadeInDelay.toLong())
            textAlpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(1000, easing = LinearEasing)
            )
        } else {
            textAlpha.snapTo(1f)
        }
    }

    Text(
        text = text,
        modifier = modifier.alpha(textAlpha.value),
        color = color,
        fontWeight = FontWeight.Bold,
        letterSpacing = letterSpacing,
        style = style
    )
}
