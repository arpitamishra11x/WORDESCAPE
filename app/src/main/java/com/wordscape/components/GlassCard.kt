package com.wordscape.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 24.dp,
    backgroundAlpha: Float = 0.15f,
    borderAlpha: Float = 0.3f,
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)
    
    val bgBrush = Brush.verticalGradient(
        colors = listOf(
            Color.White.copy(alpha = backgroundAlpha),
            Color.White.copy(alpha = backgroundAlpha * 0.5f)
        )
    )
    
    val borderBrush = Brush.verticalGradient(
        colors = listOf(
            Color.White.copy(alpha = borderAlpha),
            Color.White.copy(alpha = borderAlpha * 0.3f)
        )
    )

    Box(
        modifier = modifier
            .clip(shape)
            .background(brush = bgBrush)
            .border(width = 1.dp, brush = borderBrush, shape = shape)
            .padding(20.dp)
    ) {
        Column {
            content()
        }
    }
}
