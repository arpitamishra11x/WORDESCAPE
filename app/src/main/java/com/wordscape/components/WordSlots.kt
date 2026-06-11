package com.wordscape.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wordscape.motion.glowingBorder
import com.wordscape.utils.wordSlotContentDescription

@Composable
fun WordSlots(
    word: String,
    placedLetters: Map<Int, Char>,
    activeSlot: Int? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in word.indices) {
            val letter = placedLetters[i]
            val isActive = activeSlot == i

            val letterScale by animateFloatAsState(
                targetValue = if (letter != null) 1f else 0f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                label = "letterScale"
            )

            Column(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .semantics {
                        contentDescription = wordSlotContentDescription(i, word.length, letter)
                    },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .height(60.dp)
                        .width(48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (letter != null) {
                        Text(
                            text = letter.toString(),
                            color = Color.White,
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.scale(letterScale)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(4.dp))

                // Underline slot indicator
                val underlineColor = when {
                    letter != null -> Color(0xFF86EFAC) // Placed successfully
                    isActive -> Color(0xFFA78BFA) // Current active target
                    else -> Color.White.copy(alpha = 0.5f)
                }

                val underlineHeight = if (isActive || letter != null) 4.dp else 2.dp

                Box(
                    modifier = Modifier
                        .width(44.dp)
                        .height(underlineHeight)
                        .background(color = underlineColor)
                        .then(
                            if (isActive) Modifier.glowingBorder(color = Color(0xFFA78BFA)) else Modifier
                        )
                )
            }
        }
    }
}
