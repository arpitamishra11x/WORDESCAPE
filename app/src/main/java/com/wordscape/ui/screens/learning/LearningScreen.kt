package com.wordscape.ui.screens.learning

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.wordscape.components.DraggableLetter
import com.wordscape.components.GlassCard
import com.wordscape.components.ParticleOverlay
import com.wordscape.components.WordSlots
import com.wordscape.data.models.Word
import com.wordscape.motion.ParticleType
import com.wordscape.motion.floatAndRotate
import com.wordscape.scene.CanvasAnimalAnimator
import com.wordscape.scene.DefaultAnimalEntity
import com.wordscape.scene.DefaultAnimalInteractionHandler
import com.wordscape.scene.SceneConfig
import com.wordscape.scene.SceneRenderer
import com.wordscape.scene.animalInteractions
import com.wordscape.utils.accessibleClickable
import com.wordscape.utils.animalContentDescription
import com.wordscape.utils.distanceTo
import com.wordscape.viewmodels.LearningViewModel

@Composable
fun LearningScreen(
    worldId: String,
    wordIndex: Int,
    onNavigateBack: () -> Unit,
    viewModel: LearningViewModel = hiltViewModel()
) {
    val currentWord by viewModel.currentWord.collectAsState()
    val placedLetters by viewModel.placedLetters.collectAsState()
    val availableLetters by viewModel.availableLetters.collectAsState()
    val isComplete by viewModel.isWordComplete.collectAsState()
    val showAnimal by viewModel.showAnimal.collectAsState()
    val showNextPath by viewModel.showNextPath.collectAsState()
    val sceneConfig by viewModel.sceneConfig.collectAsState()

    var initializedPositions by remember { mutableStateOf(false) }
    val letterPositions = remember { mutableStateMapOf<Int, Offset>() }
    
    // Reset positions whenever word changes
    LaunchedEffect(currentWord) {
        initializedPositions = false
        letterPositions.clear()
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val width = constraints.maxWidth.toFloat()
        val height = constraints.maxHeight.toFloat()

        // 1. Background scene
        sceneConfig?.let { config ->
            SceneRenderer(config = config, showParticles = true)
        } ?: run {
            // Fallback sky
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF7DD3FC), Color(0xFFA78BFA))
                    )
                )
            }
        }

        // Particle Bursts on completion
        if (isComplete) {
            ParticleOverlay(
                type = ParticleType.MAGICAL,
                density = 1.5f,
                modifier = Modifier.fillMaxSize()
            )
        }

        // Initialize positions once screen dimensions are available
        if (currentWord != null && !initializedPositions && width > 0) {
            val text = currentWord!!.text
            // Spread letters evenly or randomly in the lower part of the screen
            val letterSpacing = width / (text.length + 1)
            for (i in text.indices) {
                val px = letterSpacing * (i + 1) - 100f
                val py = height * 0.6f + (if (i % 2 == 0) 40f else -40f)
                letterPositions[i] = Offset(px, py)
            }
            initializedPositions = true
        }

        // Main Gameplay Layer
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 28.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.Rounded.ArrowBack,
                        contentDescription = "Back to home",
                        tint = Color.White
                    )
                }
                
                GlassCard(
                    cornerRadius = 16.dp,
                    backgroundAlpha = 0.1f,
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text(
                        text = "World: ${worldId.uppercase()}",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Word Slots area
            currentWord?.let { word ->
                WordSlots(
                    word = word.text,
                    placedLetters = placedLetters,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            }

            // Word completed Reward visual (Animal appears)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                if (showAnimal && currentWord != null) {
                    val word = currentWord!!
                    val animalEntity = remember(word) {
                        DefaultAnimalEntity(
                            com.wordscape.scene.AnimalConfig(
                                type = word.animalType ?: "cat",
                                primaryColor = Color(0xFFFCD34D),
                                secondaryColor = Color(0xFFF59E0B)
                            )
                        )
                    }
                    val animator = remember { CanvasAnimalAnimator() }
                    val handler = remember { DefaultAnimalInteractionHandler { sfxName -> } }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .animalInteractions(animalEntity, handler)
                            .accessibleClickable(
                                label = animalContentDescription(word.text, "sitting"),
                                onClick = {
                                    viewModel.onAnimalInteraction("tap")
                                }
                            )
                    ) {
                        Box(modifier = Modifier.size(150.dp)) {
                            animator.RenderAnimal(
                                entity = animalEntity,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        GlassCard(cornerRadius = 16.dp, backgroundAlpha = 0.2f) {
                            Text(
                                text = word.text,
                                color = Color.White,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Letters scatter box (Only active if not complete)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                if (currentWord != null && initializedPositions && !isComplete) {
                    val word = currentWord!!
                    for (i in word.text.indices) {
                        val initialPos = letterPositions[i] ?: Offset.Zero
                        val isLetterPlaced = placedLetters.containsKey(i)
                        
                        if (!isLetterPlaced) {
                            DraggableLetter(
                                letter = word.text[i],
                                initialPosition = initialPos,
                                onDragStart = {},
                                onDragEnd = { finalPos ->
                                    // Calculate closest slot index
                                    // Screen slots are centered horizontally at y = 140dp approximately
                                    // To make it simple, check slots snapping distance
                                    val targetY = height * 0.2f // slots y approximately
                                    val slotWidth = width / (word.text.length + 1)
                                    
                                    var bestSlot = -1
                                    var bestDist = 200f // snapping threshold
                                    
                                    for (s in word.text.indices) {
                                        val slotX = slotWidth * (s + 1) - 50f
                                        val dist = Offset(finalPos.x, finalPos.y).distanceTo(Offset(slotX, targetY))
                                        if (dist < bestDist) {
                                            bestDist = dist
                                            bestSlot = s
                                        }
                                    }
                                    
                                    if (bestSlot != -1) {
                                        val placed = viewModel.onLetterPlaced(word.text[i], bestSlot)
                                        if (!placed) {
                                            // Snap back to initial
                                            letterPositions[i] = initialPos
                                        }
                                    } else {
                                        // Snap back
                                        letterPositions[i] = initialPos
                                    }
                                },
                                onPositionChange = { pos ->
                                    letterPositions[i] = pos
                                },
                                isPlaced = false
                            )
                        }
                    }
                }
            }

            // Glowing next path transition button
            AnimatedVisibility(
                visible = showNextPath,
                enter = fadeIn() + scaleIn()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .clickable { viewModel.loadNextWord() },
                    contentAlignment = Alignment.Center
                ) {
                    // Glowing path line representation
                    Canvas(modifier = Modifier
                        .fillMaxWidth()
                        .height(30.dp)) {
                        val path = Path().apply {
                            moveTo(0f, size.height / 2f)
                            quadraticBezierTo(size.width * 0.5f, size.height / 2f - 10f, size.width, size.height / 2f)
                        }
                        drawPath(
                            path = path,
                            brush = Brush.horizontalGradient(
                                colors = listOf(Color(0xFFA78BFA).copy(alpha = 0.2f), Color(0xFF7DD3FC), Color(0xFFA78BFA).copy(alpha = 0.2f))
                            ),
                            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 6f)
                        )
                    }
                    
                    GlassCard(cornerRadius = 16.dp, backgroundAlpha = 0.3f) {
                        Text(
                            text = "Next Challenge →",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
