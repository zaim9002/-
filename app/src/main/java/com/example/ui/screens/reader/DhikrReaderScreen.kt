package com.example.ui.screens.reader

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.HisnTopBar
import com.example.ui.components.IslamicCard
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicGreen
import com.example.ui.theme.IslamicGreenLight
import com.example.ui.viewmodel.HisnViewModel

@Composable
fun DhikrReaderScreen(
    viewModel: HisnViewModel,
    onBackClick: () -> Unit
) {
    val dhikrList by viewModel.currentReaderDhikrList.collectAsState()
    val currentIndex by viewModel.currentReaderIndex.collectAsState()
    val remainingCount by viewModel.currentRemainingCount.collectAsState()
    val completedCount by viewModel.currentCompletedCount.collectAsState()
    val favoriteIds by viewModel.favoriteIds.collectAsState()
    val userSettings by viewModel.userSettings.collectAsState()
    val isTtsPlaying by viewModel.audioReciterService.isPlaying.collectAsState()
    val playingDhikrId by viewModel.audioReciterService.currentPlayingId.collectAsState()

    if (dhikrList.isEmpty() || currentIndex !in dhikrList.indices) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("لا يوجد ذكر محدد للقراءة")
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onBackClick) {
                Text("العودة للأذكار")
            }
        }
        return
    }

    val currentDhikr = dhikrList[currentIndex]
    val isFav = favoriteIds.contains(currentDhikr.id)
    val isThisDhikrPlaying = isTtsPlaying && (playingDhikrId == currentDhikr.id)

    val interactionSource = remember { MutableInteractionSource() }
    val isCounterPressed by interactionSource.collectIsPressedAsState()
    val counterScale by animateFloatAsState(
        targetValue = if (isCounterPressed) 0.93f else 1f,
        animationSpec = spring(),
        label = "counter_scale"
    )

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("dhikr_reader_screen")
    ) {
        // Top Bar with Actions
        HisnTopBar(
            title = currentDhikr.title,
            onBackClick = onBackClick,
            actions = {
                // Font Size zoom controls
                IconButton(
                    onClick = { viewModel.setFontSize(userSettings.fontSizeSp - 2f) },
                    modifier = Modifier.size(36.dp).testTag("font_decrease_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "تصغير الخط",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(
                    onClick = { viewModel.setFontSize(userSettings.fontSizeSp + 2f) },
                    modifier = Modifier.size(36.dp).testTag("font_increase_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "تكبير الخط",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Audio Button
                IconButton(
                    onClick = {
                        viewModel.audioReciterService.speak(currentDhikr.id, currentDhikr.text)
                    },
                    modifier = Modifier.size(36.dp).testTag("audio_tts_btn")
                ) {
                    Icon(
                        imageVector = if (isThisDhikrPlaying) Icons.Default.Stop else Icons.Default.VolumeUp,
                        contentDescription = if (isThisDhikrPlaying) "إيقاف الصوت" else "استماع للذكر",
                        tint = if (isThisDhikrPlaying) IslamicGold else MaterialTheme.colorScheme.primary
                    )
                }

                // Favorite
                IconButton(
                    onClick = { viewModel.toggleFavorite(currentDhikr.id) },
                    modifier = Modifier.size(36.dp).testTag("reader_fav_btn")
                ) {
                    Icon(
                        imageVector = if (isFav) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = "مفضلة",
                        tint = if (isFav) IslamicGold else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        )

        // Progress Bar across list
        LinearProgressIndicator(
            progress = { (currentIndex + 1).toFloat() / dhikrList.size },
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )

        // Reading Content + Bottom Controls
        Column(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Index badge (e.g. ذكر 3 من 12)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = "ذكر ${currentIndex + 1} من ${dhikrList.size}",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                Text(
                    text = "التكرار المطلوب: ${currentDhikr.count}",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Main Dhikr Text Card
            IslamicCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(22.dp)
                ) {
                    Text(
                        text = currentDhikr.text,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = userSettings.fontSizeSp.sp,
                            lineHeight = (userSettings.fontSizeSp * 1.75f).sp,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (currentDhikr.source.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(18.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "المصدر: ${currentDhikr.source}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = IslamicGold,
                                    fontWeight = FontWeight.SemiBold,
                                    textAlign = TextAlign.Center
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    if (currentDhikr.benefit.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "فضله: ${currentDhikr.benefit}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 20.sp,
                                textAlign = TextAlign.Center
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Copy & Share buttons inside card
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.clickable { viewModel.copyDhikr(currentDhikr) }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "نسخ",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "نسخ الذكر",
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.clickable { viewModel.shareDhikr(currentDhikr) }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = "مشاركة",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "مشاركة",
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Giant Interactive Counter Button (3 / 3 -> 2 / 3 -> 1 / 3 -> 0 / 3)
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(140.dp)
                    .scale(counterScale)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.radialGradient(
                            colors = if (remainingCount == 0) listOf(
                                IslamicGold,
                                MaterialTheme.colorScheme.primary
                            ) else listOf(
                                MaterialTheme.colorScheme.primary,
                                Color(0xFF0C4D35)
                            )
                        )
                    )
                    .border(
                        width = 4.dp,
                        color = if (remainingCount == 0) IslamicGold else IslamicGold.copy(alpha = 0.6f),
                        shape = CircleShape
                    )
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = { viewModel.onCounterTap() }
                    )
                    .testTag("dhikr_counter_button")
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "$remainingCount",
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 44.sp
                        )
                    )
                    Text(
                        text = "من ${currentDhikr.count}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color(0xFFE2F4EB),
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = if (remainingCount == 0) "أحسنت! تم إكمال هذا الذكر" else "المس الدائرة لاحتساب التكرار",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = if (remainingCount == 0) IslamicGold else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = if (remainingCount == 0) FontWeight.Bold else FontWeight.Normal
                )
            )

            Spacer(modifier = Modifier.height(24.dp))
        }

        // Fixed Bottom Navigation Controls (Previous, Reset, Next)
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                MaterialTheme.colorScheme.outlineVariant
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Previous
                Button(
                    onClick = { viewModel.prevDhikr() },
                    enabled = currentIndex > 0,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier.weight(1f).testTag("prev_dhikr_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "السابق",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("السابق")
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Reset
                IconButton(
                    onClick = { viewModel.resetCurrentCounter() },
                    modifier = Modifier.size(46.dp).testTag("reset_counter_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "إعادة العداد",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Next
                Button(
                    onClick = { viewModel.nextDhikr() },
                    enabled = currentIndex < dhikrList.size - 1,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.White
                    ),
                    modifier = Modifier.weight(1f).testTag("next_dhikr_button")
                ) {
                    Text("التالي")
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "التالي",
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
