package com.wordscape.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.wordscape.components.AnimatedSky
import com.wordscape.components.FloatingIsland
import com.wordscape.components.GlassButton
import com.wordscape.components.GlassCard
import com.wordscape.components.MagicalText
import com.wordscape.components.ParticleOverlay
import com.wordscape.motion.ParticleType
import com.wordscape.scene.TimeOfDay
import com.wordscape.viewmodels.HomeViewModel

@Composable
fun HomeScreen(
    onNavigateToLearning: (String, Int) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val greeting by viewModel.greeting.collectAsState()
    val currentWorld by viewModel.currentWorld.collectAsState()
    val wordsLearned by viewModel.wordsLearned.collectAsState()
    val streak by viewModel.currentStreak.collectAsState()
    val category by viewModel.todayCategory.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedSky(
            timeOfDay = TimeOfDay.MORNING,
            showClouds = true,
            modifier = Modifier.fillMaxSize()
        )

        ParticleOverlay(
            type = ParticleType.DUST,
            density = 0.8f,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MagicalText(
                    text = greeting,
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp
                    ),
                    fadeIn = true,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Ready to discover a new word?",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // Centerpiece floating island
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                FloatingIsland(scale = 1.1f, hasWaterfall = true)
            }

            // Main Category Action Card
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                backgroundAlpha = 0.15f
            ) {
                Text(
                    text = "TODAY'S WORLD",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = category.displayName,
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = category.description,
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                GlassButton(
                    text = "Continue Adventure →",
                    onClick = {
                        onNavigateToLearning(currentWorld?.id ?: "animals", 0)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    glowColor = Color(0xFF7DD3FC)
                )
            }

            // Bottom stats row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                GlassCard(
                    modifier = Modifier.weight(1f),
                    cornerRadius = 16.dp,
                    backgroundAlpha = 0.1f
                ) {
                    Text(
                        text = "Words Learned",
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "$wordsLearned 🌳",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                GlassCard(
                    modifier = Modifier.weight(1f),
                    cornerRadius = 16.dp,
                    backgroundAlpha = 0.1f
                ) {
                    Text(
                        text = "Explorer Streak",
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "$streak Days ✨",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
