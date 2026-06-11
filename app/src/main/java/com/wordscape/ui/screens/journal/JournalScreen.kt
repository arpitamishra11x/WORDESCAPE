package com.wordscape.ui.screens.journal

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.wordscape.components.AnimatedSky
import com.wordscape.components.GlassCard
import com.wordscape.components.MagicalText
import com.wordscape.components.ParticleOverlay
import com.wordscape.data.models.Category
import com.wordscape.motion.ParticleType
import com.wordscape.scene.TimeOfDay
import com.wordscape.utils.toFormattedDate
import com.wordscape.utils.toPhonetic
import com.wordscape.viewmodels.JournalViewModel

@Composable
fun JournalScreen(
    viewModel: JournalViewModel = hiltViewModel()
) {
    val entries by viewModel.journalEntries.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val totalCount by viewModel.entryCount.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedSky(
            timeOfDay = TimeOfDay.NIGHT,
            showClouds = false,
            showStars = true,
            modifier = Modifier.fillMaxSize()
        )

        ParticleOverlay(
            type = ParticleType.DUST,
            density = 0.5f,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            // Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 28.dp, bottom = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MagicalText(
                    text = "WordScape Journal",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp
                    ),
                    shimmer = true,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Your collection of discovered words: $totalCount",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // Category filter chips
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    CategoryChip(
                        name = "All",
                        isSelected = selectedCategory == null,
                        onClick = { viewModel.setFilter(null) }
                    )
                }
                items(Category.entries) { category ->
                    CategoryChip(
                        name = category.displayName,
                        isSelected = selectedCategory == category,
                        onClick = { viewModel.setFilter(category) }
                    )
                }
            }

            // Entries List
            if (entries.isEmpty()) {
                Box(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Your journal awaits its first word...\nPlant seeds by playing matching challenges!",
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 24.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(entries) { (entry, word) ->
                        GlassCard(
                            modifier = Modifier.fillMaxWidth(),
                            backgroundAlpha = 0.15f
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = word.text,
                                            color = Color.White,
                                            fontSize = 24.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = word.text.toPhonetic(),
                                            color = Color.White.copy(alpha = 0.6f),
                                            fontSize = 14.sp,
                                            fontStyle = FontStyle.Italic
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = word.category.uppercase(),
                                        color = Color(0xFFA78BFA),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        letterSpacing = 1.sp
                                    )
                                }

                                IconButton(onClick = { viewModel.toggleFavorite(entry.id) }) {
                                    Icon(
                                        imageVector = if (entry.isFavorite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                                        contentDescription = "Favorite",
                                        tint = if (entry.isFavorite) Color(0xFFF9A8D4) else Color.White
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = word.funFact,
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 14.sp,
                                lineHeight = 20.sp
                            )
                            
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Discovered: ${entry.learnedAt.toFormattedDate()}",
                                    color = Color.White.copy(alpha = 0.5f),
                                    fontSize = 11.sp
                                )

                                // Mastery Level Tracker (stars)
                                Row {
                                    for (m in 1..5) {
                                        val color = if (m <= entry.masteryLevel) Color(0xFFFCD34D) else Color.White.copy(alpha = 0.2f)
                                        Text(
                                            text = "★",
                                            color = color,
                                            fontSize = 14.sp,
                                            modifier = Modifier.padding(horizontal = 1.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryChip(
    name: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val bg = if (isSelected) Color(0xFFA78BFA) else Color.White.copy(alpha = 0.1f)
    val textColor = if (isSelected) Color.White else Color.White.copy(alpha = 0.7f)

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .clickable(onClick = onClick)
            .background(color = bg)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = name,
            color = textColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
