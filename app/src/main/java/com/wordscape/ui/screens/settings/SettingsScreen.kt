package com.wordscape.ui.screens.settings

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.wordscape.components.AnimatedSky
import com.wordscape.components.GlassCard
import com.wordscape.components.MagicalText
import com.wordscape.components.ParticleOverlay
import com.wordscape.motion.ParticleType
import com.wordscape.scene.TimeOfDay
import com.wordscape.viewmodels.SettingsViewModel

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val voiceGuidance by viewModel.voiceGuidance.collectAsState()
    val largeText by viewModel.largeText.collectAsState()
    val dyslexiaFont by viewModel.dyslexiaFont.collectAsState()
    val colorblindMode by viewModel.colorblindMode.collectAsState()
    val highContrast by viewModel.highContrast.collectAsState()
    val reducedMotion by viewModel.reducedMotion.collectAsState()
    val sfxVolume by viewModel.sfxVolume.collectAsState()
    val musicVolume by viewModel.musicVolume.collectAsState()

    val scrollState = rememberScrollState()

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedSky(
            timeOfDay = TimeOfDay.NIGHT,
            showClouds = false,
            showStars = true,
            modifier = Modifier.fillMaxSize()
        )

        ParticleOverlay(
            type = ParticleType.DUST,
            density = 0.4f,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(scrollState)
        ) {
            // Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 28.dp, bottom = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MagicalText(
                    text = "Settings",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp
                    ),
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Configure your magical workspace",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Accessibility Group
            Text(
                text = "ACCESSIBILITY",
                color = Color(0xFFA78BFA),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp,
                modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
            )

            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundAlpha = 0.15f
            ) {
                SettingSwitchRow(
                    label = "Voice Guidance",
                    desc = "Hear instructions and word pronunciations",
                    checked = voiceGuidance,
                    onCheckedChange = { viewModel.setVoiceGuidance(it) }
                )
                
                Spacer(modifier = Modifier.height(16.dp))

                SettingSwitchRow(
                    label = "Large Text Mode",
                    desc = "Enlarge wording sizes throughout",
                    checked = largeText,
                    onCheckedChange = { viewModel.setLargeText(it) }
                )

                Spacer(modifier = Modifier.height(16.dp))

                SettingSwitchRow(
                    label = "Dyslexia Friendly Font",
                    desc = "Use monospaced spacing rules",
                    checked = dyslexiaFont,
                    onCheckedChange = { viewModel.setDyslexiaFont(it) }
                )

                Spacer(modifier = Modifier.height(16.dp))

                SettingSwitchRow(
                    label = "Colorblind Mode",
                    desc = "Adjust displays for red-green sight",
                    checked = colorblindMode,
                    onCheckedChange = { viewModel.setColorblindMode(it) }
                )

                Spacer(modifier = Modifier.height(16.dp))

                SettingSwitchRow(
                    label = "High Contrast Mode",
                    desc = "Render black text highlights",
                    checked = highContrast,
                    onCheckedChange = { viewModel.setHighContrast(it) }
                )

                Spacer(modifier = Modifier.height(16.dp))

                SettingSwitchRow(
                    label = "Reduced Motion",
                    desc = "Minimize hovering island rotations",
                    checked = reducedMotion,
                    onCheckedChange = { viewModel.setReducedMotion(it) }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Audio Group
            Text(
                text = "AUDIO OPTIONS",
                color = Color(0xFF7DD3FC),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp,
                modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
            )

            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundAlpha = 0.15f
            ) {
                Text(
                    text = "Sound Effects",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Slider(
                    value = sfxVolume,
                    onValueChange = { viewModel.setSfxVolume(it) },
                    colors = SliderDefaults.colors(
                        thumbColor = Color(0xFFA78BFA),
                        activeTrackColor = Color(0xFFA78BFA),
                        inactiveTrackColor = Color.White.copy(alpha = 0.2f)
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Background Music",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Slider(
                    value = musicVolume,
                    onValueChange = { viewModel.setMusicVolume(it) },
                    colors = SliderDefaults.colors(
                        thumbColor = Color(0xFF7DD3FC),
                        activeTrackColor = Color(0xFF7DD3FC),
                        inactiveTrackColor = Color.White.copy(alpha = 0.2f)
                    )
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun SettingSwitchRow(
    label: String,
    desc: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = desc,
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 12.sp
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color(0xFFA78BFA),
                checkedTrackColor = Color(0xFFA78BFA).copy(alpha = 0.4f),
                uncheckedThumbColor = Color.LightGray,
                uncheckedTrackColor = Color.DarkGray
            )
        )
    }
}
