package com.wordscape.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wordscape.motion.glowingBorder
import com.wordscape.motion.squishOnDrag
import com.wordscape.utils.letterContentDescription
import kotlinx.coroutines.launch

@Composable
fun DraggableLetter(
    letter: Char,
    initialPosition: Offset,
    onDragStart: () -> Unit,
    onDragEnd: (Offset) -> Unit,
    onPositionChange: (Offset) -> Unit,
    isPlaced: Boolean = false,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    
    // Position tracking animatable
    val animX = remember { Animatable(initialPosition.x) }
    val animY = remember { Animatable(initialPosition.y) }
    
    var isDragging by remember { mutableStateOf(false) }
    var dragOffset by remember { mutableStateOf(Offset.Zero) }
    
    // Scale tracking
    val scaleAnim = remember { Animatable(1f) }

    LaunchedEffect(initialPosition) {
        if (!isDragging) {
            animX.animateTo(initialPosition.x, spring(dampingRatio = Spring.DampingRatioLowBouncy))
            animY.animateTo(initialPosition.y, spring(dampingRatio = Spring.DampingRatioLowBouncy))
        }
    }

    LaunchedEffect(isDragging) {
        scaleAnim.animateTo(
            targetValue = if (isDragging) 1.2f else 1f,
            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
        )
    }

    val dragModifier = if (!isPlaced) {
        Modifier.pointerInput(letter) {
            detectDragGestures(
                onDragStart = {
                    isDragging = true
                    onDragStart()
                },
                onDragEnd = {
                    isDragging = false
                    onDragEnd(Offset(animX.value, animY.value))
                    dragOffset = Offset.Zero
                },
                onDragCancel = {
                    isDragging = false
                    dragOffset = Offset.Zero
                },
                onDrag = { change, dragAmount ->
                    change.consume()
                    dragOffset += dragAmount
                    coroutineScope.launch {
                        animX.snapTo(animX.value + dragAmount.x)
                        animY.snapTo(animY.value + dragAmount.y)
                        onPositionChange(Offset(animX.value, animY.value))
                    }
                }
            )
        }
    } else {
        Modifier
    }

    val bgBrush = Brush.verticalGradient(
        colors = listOf(
            Color.White.copy(alpha = if (isPlaced) 0.4f else 0.2f),
            Color.White.copy(alpha = if (isPlaced) 0.15f else 0.05f)
        )
    )

    val borderBrush = Brush.verticalGradient(
        colors = listOf(
            Color.White.copy(alpha = if (isPlaced) 0.6f else 0.4f),
            Color.White.copy(alpha = if (isPlaced) 0.3f else 0.1f)
        )
    )

    Box(
        modifier = modifier
            .offset {
                IntOffset(
                    animX.value.toInt(),
                    animY.value.toInt()
                )
            }
            .scale(scaleAnim.value)
            .squishOnDrag(dragOffset)
            .size(64.dp)
            .clip(CircleShape)
            .background(brush = bgBrush)
            .border(width = 1.5.dp, brush = borderBrush, shape = CircleShape)
            .glowingBorder(
                color = if (isPlaced) Color(0xFF86EFAC) else Color(0xFFA78BFA),
                blurRadius = if (isDragging) 30f else 15f
            )
            .semantics {
                contentDescription = letterContentDescription(letter, isPlaced)
            }
            .then(dragModifier),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = letter.toString(),
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
