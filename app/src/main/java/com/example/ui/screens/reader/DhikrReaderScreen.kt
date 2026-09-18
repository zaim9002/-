package com.example.ui.screens.reader

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeOff
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
    val isCompleted = completedCount >= currentDhikr.count

    val interactionSource = remember { MutableInteractionSource() }
    val isCounterPressed by interactionSource.collectIsPressedAsState()
    val counterScale by animateFloatAsState(
        targetValue = if (isCounterPressed) 0.92f else 1f,
        animationSpec = spring(),
        label = "counter_scale"
    )

    val scrollState = rememberScrollState()

    // Formatted text based on tashkeel setting
    val displayText = remember(currentDhikr.text, userSettings.showTashkeel) {
        viewModel.formatDhikrText(currentDhikr.text, userSettings.showTashkeel)
    }

    val categoryTitle = remember(currentDhikr.categoryId) {
        viewModel.categories.find { it.id == currentDhikr.categoryId }?.title ?: "حصن المسلم"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("dhikr_reader_screen")
    ) {
        // ================= Top Bar =================
        HisnTopBar(
            title = currentDhikr.title,
            onBackClick = onBackClick,
            actions = {
                // Sound toggle
                IconButton(
                    onClick = { viewModel.toggleSound(!userSettings.isSoundEnabled) },
                    modifier = Modifier.size(36.dp).testTag("reader_sound_toggle")
                ) {
                    Icon(
                        imageVector = if (userSettings.isSoundEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                        contentDescription = "الصوت",
                        tint = if (userSettings.isSoundEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Haptic toggle
                IconButton(
                    onClick = { viewModel.toggleHaptic(!userSettings.isHapticEnabled) },
                    modifier = Modifier.size(36.dp).testTag("reader_haptic_toggle")
                ) {
                    Icon(
                        imageVector = Icons.Default.Vibration,
                        contentDescription = "الاهتزاز",
                        tint = if (userSettings.isHapticEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Audio recitation
                IconButton(
                    onClick = {
                        viewModel.audioReciterService.speak(currentDhikr.id, currentDhikr.text)
                    },
                    modifier = Modifier.size(36.dp).testTag("audio_tts_btn")
                ) {
                    Icon(
                        imageVector = if (isThisDhikrPlaying) Icons.Default.Stop else Icons.Default.VolumeUp,
                        contentDescription = if (isThisDhikrPlaying) "إيقاف التلاوة" else "استماع للتلاوة",
                        tint = if (isThisDhikrPlaying) IslamicGold else MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
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
                        tint = if (isFav) IslamicGold else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        )

        // Progress line across the category
        val listProgress = ((currentIndex + 1).toFloat() / dhikrList.size.toFloat()).coerceIn(0f, 1f)
        LinearProgressIndicator(
            progress = { listProgress },
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )

        // ================= Main Scrollable Content =================
        Column(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .verticalScroll(scrollState)
                .padding(horizontal = 18.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Category Badge & Index Indicator
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
                        text = categoryTitle,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Text(
                        text = "ذكر ${currentIndex + 1} من ${dhikrList.size}",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // ================= بطاقة الذكر الأنيقة =================
            IslamicCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .then(
                        if (userSettings.countMode == "fullscreen") {
                            Modifier.clickable { viewModel.onCounterTap() }
                        } else Modifier
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(22.dp)
                ) {
                    // Dhikr Text
                    Text(
                        text = displayText,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = userSettings.fontSizeSp.sp,
                            lineHeight = (userSettings.fontSizeSp * 1.65f + userSettings.lineSpacingSp).sp,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // عدد التكرار
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
                        ) {
                            Text(
                                text = "عدد التكرار: 🔢 ${currentDhikr.count} مرات",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                            )
                        }
                    }

                    // المصدر
                    if (currentDhikr.source.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "📚 المصدر: ${currentDhikr.source}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.Medium,
                                    textAlign = TextAlign.Center
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    // الفضيلة
                    if (currentDhikr.benefit.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "✨ الفضيلة: ${currentDhikr.benefit}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                                lineHeight = 18.sp,
                                textAlign = TextAlign.Center
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Action Buttons Row: ⭐ مفضلة, 📋 نسخ, 📤 مشاركة, 🔄 إعادة
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // المفضلة
                        IconButton(
                            onClick = { viewModel.toggleFavorite(currentDhikr.id) },
                            modifier = Modifier.testTag("action_fav_btn")
                        ) {
                            Icon(
                                imageVector = if (isFav) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = "مفضلة",
                                tint = if (isFav) IslamicGold else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // نسخ
                        IconButton(
                            onClick = { viewModel.copyDhikr(currentDhikr) },
                            modifier = Modifier.testTag("action_copy_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "نسخ الذكر",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // مشاركة
                        IconButton(
                            onClick = { viewModel.shareDhikr(currentDhikr) },
                            modifier = Modifier.testTag("action_share_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "مشاركة الذكر",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // إعادة
                        IconButton(
                            onClick = { viewModel.resetCurrentCounter() },
                            modifier = Modifier.testTag("action_reset_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "إعادة العداد",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ================= 🔢 نظام العداد الاحترافي =================
            // عرض العداد: e.g. "العداد: 37 / 100"
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "العداد: $completedCount / ${currentDhikr.count}",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // الزر الكبير: "اضغط للتسبيح" / "اضغط للعد"
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(148.dp)
                    .scale(counterScale)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.radialGradient(
                            colors = if (isCompleted) listOf(
                                IslamicGold,
                                MaterialTheme.colorScheme.primary
                            ) else listOf(
                                MaterialTheme.colorScheme.primary,
                                Color(0xFF093927)
                            )
                        )
                    )
                    .border(
                        width = 4.dp,
                        color = if (isCompleted) IslamicGold else IslamicGold.copy(alpha = 0.5f),
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
                        text = "$completedCount",
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (isCompleted) "اكتمل الذكر" else "اضغط للعد",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color.White.copy(alpha = 0.9f),
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // تنبيه لطيف عند إتمام الذكر
            AnimatedVisibility(
                visible = isCompleted,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "🎉 ما شاء الله! اكتمل العدد المطلوب للذكر",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // إجمالي الأذكار اليومية
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "عداد اليوم: ${userSettings.todayDhikrCount} ذكر  •  الإجمالي: ${userSettings.totalDhikrCount}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // ================= Fixed Bottom Navigation Bar =================
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // السابق
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

                // إعادة
                IconButton(
                    onClick = { viewModel.resetCurrentCounter() },
                    modifier = Modifier.size(44.dp).testTag("reset_counter_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "إعادة العداد",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // التالي
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
