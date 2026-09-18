package com.example.ui.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Dhikr
import com.example.ui.theme.IslamicGold
import com.example.ui.viewmodel.HisnViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private data class QuickDhikrCard(
    val id: String,
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val emoji: String,
    val color: Color,
    val onClick: () -> Unit
)

@Composable
fun HomeScreen(
    viewModel: HisnViewModel,
    onNavigateToCategory: (String) -> Unit,
    onNavigateToReader: () -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToTasbeeh: () -> Unit,
    onNavigateToDuas: () -> Unit,
    onNavigateToCategories: () -> Unit,
    onNavigateToStats: () -> Unit
) {
    val userSettings by viewModel.userSettings.collectAsState()
    val allProgress by viewModel.allProgress.collectAsState()
    val lastReadEntity by viewModel.lastRead.collectAsState()
    val recentReads by viewModel.recentReads.collectAsState()
    val favoriteIds by viewModel.favoriteIds.collectAsState()
    val favoriteDhikrs by viewModel.favoriteDhikrs.collectAsState()

    var showGoalDialog by remember { mutableStateOf(false) }

    // Date formatting
    val arabicDate = remember {
        val sdf = SimpleDateFormat("EEEE، d MMMM yyyy", Locale("ar"))
        sdf.format(Date())
    }

    // Resolve Last Read Dhikr
    val lastReadDhikr = remember(lastReadEntity) {
        lastReadEntity?.let { entity ->
            viewModel.allDhikrs.find { it.id == entity.dhikrId }
        }
    }

    // Pick a featured inspiring dhikr
    val featuredDhikr = remember {
        viewModel.allDhikrs.find { it.id == 2401 }
            ?: viewModel.allDhikrs.find { it.id == 104 }
            ?: viewModel.allDhikrs.first()
    }
    val isFeaturedFav = favoriteIds.contains(featuredDhikr.id)

    // The 10 Primary Adhkar Cards requested explicitly by the user:
    val primaryCards = listOf(
        QuickDhikrCard(
            id = "morning",
            title = "أذكار الصباح",
            subtitle = "ابدأ يومك بحفظ الله",
            icon = Icons.Default.WbSunny,
            emoji = "🌅",
            color = Color(0xFFE89A3C),
            onClick = {
                val list = viewModel.allDhikrs.filter { it.categoryId == "morning" }
                viewModel.openDhikrReader(list, 0)
                onNavigateToReader()
            }
        ),
        QuickDhikrCard(
            id = "evening",
            title = "أذكار المساء",
            subtitle = "حصنك الحصين بالمساء",
            icon = Icons.Default.NightsStay,
            emoji = "🌙",
            color = Color(0xFF6366F1),
            onClick = {
                val list = viewModel.allDhikrs.filter { it.categoryId == "evening" }
                viewModel.openDhikrReader(list, 0)
                onNavigateToReader()
            }
        ),
        QuickDhikrCard(
            id = "after_prayer",
            title = "بعد الصلاة",
            subtitle = "أذكار دبر كل صلاة مكتوبة",
            icon = Icons.Default.Mosque,
            emoji = "🕌",
            color = Color(0xFF10B981),
            onClick = {
                val list = viewModel.allDhikrs.filter { it.categoryId == "after_prayer" }
                viewModel.openDhikrReader(list, 0)
                onNavigateToReader()
            }
        ),
        QuickDhikrCard(
            id = "sleep",
            title = "أذكار النوم",
            subtitle = "حصن المسلم قبل المنام",
            icon = Icons.Default.Bedtime,
            emoji = "😴",
            color = Color(0xFF8B5CF6),
            onClick = {
                val list = viewModel.allDhikrs.filter { it.categoryId == "sleep" }
                viewModel.openDhikrReader(list, 0)
                onNavigateToReader()
            }
        ),
        QuickDhikrCard(
            id = "waking",
            title = "الاستيقاظ",
            subtitle = "الحمد لله الذي أحيانا",
            icon = Icons.Default.Alarm,
            emoji = "☀️",
            color = Color(0xFFF59E0B),
            onClick = {
                val list = viewModel.allDhikrs.filter { it.categoryId == "waking" }
                viewModel.openDhikrReader(list, 0)
                onNavigateToReader()
            }
        ),
        QuickDhikrCard(
            id = "home",
            title = "دخول المنزل",
            subtitle = "دعاء الدخول والخروج",
            icon = Icons.Default.Home,
            emoji = "🚪",
            color = Color(0xFF0EA5E9),
            onClick = {
                val list = viewModel.allDhikrs.filter { it.categoryId == "home" }
                viewModel.openDhikrReader(list, 0)
                onNavigateToReader()
            }
        ),
        QuickDhikrCard(
            id = "travel",
            title = "السفر",
            subtitle = "دعاء الركوب والسفر",
            icon = Icons.Default.DirectionsCar,
            emoji = "🚗",
            color = Color(0xFF06B6D4),
            onClick = {
                val list = viewModel.allDhikrs.filter { it.categoryId == "travel" }
                viewModel.openDhikrReader(list, 0)
                onNavigateToReader()
            }
        ),
        QuickDhikrCard(
            id = "food",
            title = "الطعام والشراب",
            subtitle = "التسمية والحمد والبركة",
            icon = Icons.Default.Restaurant,
            emoji = "🍽️",
            color = Color(0xFF84CC16),
            onClick = {
                val list = viewModel.allDhikrs.filter { it.categoryId == "food" }
                viewModel.openDhikrReader(list, 0)
                onNavigateToReader()
            }
        ),
        QuickDhikrCard(
            id = "duas",
            title = "الأدعية",
            subtitle = "أدعية القرآن والسنة النبوية",
            icon = Icons.Default.VolunteerActivism,
            emoji = "🤲",
            color = Color(0xFFEC4899),
            onClick = onNavigateToDuas
        ),
        QuickDhikrCard(
            id = "all_adhkar",
            title = "جميع الأذكار",
            subtitle = "${viewModel.categories.size} تصنيفاً و ${viewModel.allDhikrs.size} ذكراً",
            icon = Icons.Default.MenuBook,
            emoji = "📖",
            color = Color(0xFF14B8A6),
            onClick = onNavigateToCategories
        )
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("home_screen"),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // ================= Hero App Bar =================
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primaryContainer,
                                MaterialTheme.colorScheme.surface
                            )
                        )
                    )
                    .padding(horizontal = 20.dp, vertical = 20.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "حصن المسلم",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.ExtraBold
                                ),
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "أذكارك اليومية بين يديك",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        // Top Action Icons
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            IconButton(
                                onClick = onNavigateToStats,
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f))
                                    .testTag("stats_icon_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.BarChart,
                                    contentDescription = "الإحصائيات",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                            IconButton(
                                onClick = onNavigateToSearch,
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f))
                                    .testTag("search_icon_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "البحث",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = arabicDate,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                    )
                }
            }
        }

        // ================= 🎯 Daily Goal Section =================
        item {
            val dailyGoal = userSettings.dailyGoalCount.coerceAtLeast(1)
            val todayCount = userSettings.todayDhikrCount
            val progressFrac = (todayCount.toFloat() / dailyGoal.toFloat()).coerceIn(0f, 1f)
            val percentage = (progressFrac * 100).toInt()

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .testTag("daily_goal_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "🎯",
                                fontSize = 22.sp,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Column {
                                Text(
                                    text = "هدف اليوم",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "أكمل $dailyGoal ذكر اليوم",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                )
                            }
                        }

                        OutlinedButton(
                            onClick = { showGoalDialog = true },
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "تعديل الهدف",
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "أنجزت $todayCount من $dailyGoal",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "$percentage%",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LinearProgressIndicator(
                        progress = { progressFrac },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(CircleShape),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                    )

                    if (todayCount >= dailyGoal) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "🎉 ما شاء الله! حققت هدفك اليومي، بارك الله فيك.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // ================= 10 Primary Adhkar Cards =================
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "الأذكار الأساسية",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
            )
        }

        // 2-Column Grid for the 10 cards
        val chunkedCards = primaryCards.chunked(2)
        items(chunkedCards) { pair ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 5.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                pair.forEach { card ->
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { card.onClick() }
                            .testTag("card_${card.id}"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = card.emoji,
                                    fontSize = 22.sp
                                )
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(card.color.copy(alpha = 0.12f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = card.icon,
                                        contentDescription = null,
                                        tint = card.color,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = card.title,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            Spacer(modifier = Modifier.height(2.dp))

                            Text(
                                text = card.subtitle,
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
                // If odd number in row
                if (pair.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }

        // ================= قسم "آخر ما قرأت" (Recently Read) =================
        if (lastReadDhikr != null) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "آخر ما قرأت",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "متابعة الورد",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .clickable {
                            viewModel.openDhikrById(lastReadDhikr.id)
                            onNavigateToReader()
                        }
                        .testTag("last_read_card"),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = lastReadDhikr.title,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            val catTitle = viewModel.categories.find { it.id == lastReadDhikr.categoryId }?.title ?: ""
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = catTitle,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = lastReadDhikr.text,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val progress = allProgress.find { it.dhikrId == lastReadDhikr.id }
                            val completed = progress?.currentCount ?: 0
                            val target = lastReadDhikr.count
                            Text(
                                text = "الإنجاز: $completed / $target",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )

                            Button(
                                onClick = {
                                    viewModel.openDhikrById(lastReadDhikr.id)
                                    onNavigateToReader()
                                },
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                modifier = Modifier.height(34.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("متابعة", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
            }
        }

        // ================= قسم "الذكر المفضل" (Favorite Dhikr) =================
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = if (favoriteDhikrs.isNotEmpty()) "أذكارك المفضلة (${favoriteDhikrs.size})" else "الذكر المفضل",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
            )
        }

        if (favoriteDhikrs.isNotEmpty()) {
            items(favoriteDhikrs.take(3)) { fav ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .clickable {
                            viewModel.openDhikrById(fav.id)
                            onNavigateToReader()
                        }
                        .testTag("fav_item_${fav.id}"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = fav.title,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            IconButton(
                                onClick = { viewModel.toggleFavorite(fav.id) },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Bookmark,
                                    contentDescription = "إزالة من المفضلة",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = fav.text,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = { viewModel.copyDhikr(fav) },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "نسخ",
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            IconButton(
                                onClick = { viewModel.shareDhikr(fav) },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = "مشاركة",
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        } else {
            // Featured Dhikr recommendation if no favorites yet
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .testTag("featured_dhikr_card"),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "✨ " + featuredDhikr.title,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            IconButton(
                                onClick = { viewModel.toggleFavorite(featuredDhikr.id) },
                                modifier = Modifier.size(30.dp)
                            ) {
                                Icon(
                                    imageVector = if (isFeaturedFav) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                    contentDescription = "حفظ بالمفضلة",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = featuredDhikr.text,
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 15.sp),
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 22.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = featuredDhikr.benefit,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = {
                                    viewModel.openDhikrById(featuredDhikr.id)
                                    onNavigateToReader()
                                },
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp),
                                modifier = Modifier.height(34.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("قراءة وتسبيح", style = MaterialTheme.typography.labelSmall)
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                IconButton(
                                    onClick = { viewModel.copyDhikr(featuredDhikr) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "نسخ",
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                IconButton(
                                    onClick = { viewModel.shareDhikr(featuredDhikr) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Share,
                                        contentDescription = "مشاركة",
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Daily Goal Dialog
    if (showGoalDialog) {
        GoalSettingDialog(
            currentGoal = userSettings.dailyGoalCount,
            onDismiss = { showGoalDialog = false },
            onSave = { newGoal ->
                viewModel.setDailyGoal(newGoal)
                showGoalDialog = false
            }
        )
    }
}

@Composable
private fun GoalSettingDialog(
    currentGoal: Int,
    onDismiss: () -> Unit,
    onSave: (Int) -> Unit
) {
    var goalText by remember { mutableStateOf(currentGoal.toString()) }
    val presets = listOf(50, 100, 200, 300, 500)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "🎯 تعديل الهدف اليومي",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                Text(
                    text = "حدد عدد الأذكار والتسبيحات التي ترغب في إتمامها يومياً:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(14.dp))

                // Presets
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    presets.forEach { preset ->
                        FilterChip(
                            selected = goalText == preset.toString(),
                            onClick = { goalText = preset.toString() },
                            label = { Text("$preset", style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = goalText,
                    onValueChange = { input ->
                        val filtered = input.filter { it.isDigit() }
                        if (filtered.length <= 4) goalText = filtered
                    },
                    label = { Text("العدد المطلوب") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val count = goalText.toIntOrNull() ?: 100
                    onSave(count)
                },
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("حفظ")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء")
            }
        }
    )
}
