package com.wordscape.ui.screens.splash

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.wordscape.viewmodels.SplashViewModel

@Composable
fun SplashScreen(
    onNavigateToHome: () -> Unit,
    viewModel: SplashViewModel = viewModel()
) {
    val phase by viewModel.currentPhase.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        // 1. Cinematic twilight sunset sky background
        SplashSky(modifier = Modifier.fillMaxSize())

        // 2. Foreground flowers/leaves framing the bottom + fireflies
        SplashForeground(modifier = Modifier.fillMaxSize())

        // 3. Interactive/text content layer
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Spacer for top alignment
            Spacer(modifier = Modifier.height(16.dp))

            // Center visual group: Giant Crystal Letter A + Floating Island
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1.8f),
                contentAlignment = Alignment.Center
            ) {
                // Phase 2+: Floating Island (hovering)
                if (phase >= 2) {
                    SplashFloatingIsland(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 110.dp) // Positioned beneath the A
                    )
                }

                // Phase 3+: Giant Crystal Letter A (35%-40% screen height)
                if (phase >= 3) {
                    SplashCrystalLetterA(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp)
                    )
                }
            }

            // Bottom text and CTA button group
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1.2f)
                    .padding(bottom = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Phase 4+: Elegant WordScape Title
                AnimatedVisibility(
                    visible = phase >= 4,
                    enter = fadeIn(animationSpec = tween(1000))
                ) {
                    Text(
                        text = "WORDSCAPE",
                        color = Color.White,
                        fontSize = 38.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 10.sp,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.displayMedium
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Phase 5+: Subtitle Tagline
                AnimatedVisibility(
                    visible = phase >= 5,
                    enter = fadeIn(animationSpec = tween(1000))
                ) {
                    Text(
                        text = "Learn words by\nbringing worlds to life.",
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 17.sp,
                        lineHeight = 24.sp,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 0.5.sp
                    )
                }

                Spacer(modifier = Modifier.height(30.dp))

                // Phase 6+: Begin Adventure frosted glass button
                AnimatedVisibility(
                    visible = phase >= 6,
                    enter = fadeIn(animationSpec = tween(800)) + slideInVertically(
                        initialOffsetY = { 60 },
                        animationSpec = spring(stiffness = Spring.StiffnessLow)
                    )
                ) {
                    SplashGlassButton(
                        text = "Begin Adventure  →",
                        onClick = onNavigateToHome
                    )
                }
            }
        }
    }
}
