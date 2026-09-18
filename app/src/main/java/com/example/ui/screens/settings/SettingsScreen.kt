package com.example.ui.screens.settings

import android.content.Intent
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.FormatLineSpacing
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.Spellcheck
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.HisnTopBar
import com.example.ui.components.IslamicCard
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicGreen
import com.example.ui.viewmodel.HisnViewModel

@Composable
fun SettingsScreen(
    viewModel: HisnViewModel,
    onNavigateToStats: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val userSettings by viewModel.userSettings.collectAsState()

    var showResetDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("settings_screen")
    ) {
        HisnTopBar(
            title = "الإعدادات والتخصيص"
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // ================= 1. المظهر والألوان الإسلامية =================
            item {
                SettingsSectionHeader(title = "المظهر والألوان")
            }

            item {
                IslamicCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Dark Mode Toggle (System / Light / Dark)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.DarkMode,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "الوضع الداكن المريح",
                                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
                                    )
                                    Text(
                                        text = when (userSettings.isDarkMode) {
                                            true -> "الوضع الليلي مفعّل"
                                            false -> "الوضع النهاري مفعّل"
                                            null -> "تلقائي حسب النظام"
                                        },
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    )
                                }
                            }

                            Switch(
                                checked = userSettings.isDarkMode == true,
                                onCheckedChange = { isChecked ->
                                    viewModel.setDarkMode(isChecked)
                                },
                                modifier = Modifier.testTag("dark_mode_switch")
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Calm Islamic Palettes
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Palette,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "الألوان الهادئة المريحة للعين",
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val palettes = listOf(
                                Triple("emerald", "زمردي هادئ", Color(0xFF16805A)),
                                Triple("teal", "فيروزي", Color(0xFF0D7E73)),
                                Triple("navy", "كحلي هادئ", Color(0xFF1D4E89)),
                                Triple("amber", "ذهبي عنبري", Color(0xFF8C6212))
                            )

                            palettes.forEach { (key, label, swatchColor) ->
                                val isSelected = userSettings.themePalette == key
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isSelected) swatchColor.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant,
                                    border = androidx.compose.foundation.BorderStroke(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) swatchColor else MaterialTheme.colorScheme.outlineVariant
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { viewModel.setThemePalette(key) }
                                ) {
                                    Column(
                                        modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(20.dp)
                                                .clip(CircleShape)
                                                .background(swatchColor)
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = label,
                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            textAlign = TextAlign.Center,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // ================= 2. تخصيص القراءة =================
            item {
                SettingsSectionHeader(title = "تخصيص القراءة والخطوط")
            }

            item {
                IslamicCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Font size slider
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.FormatSize,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "حجم خط الأذكار (${userSettings.fontSizeSp.toInt()} sp)",
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
                            )
                        }

                        Slider(
                            value = userSettings.fontSizeSp,
                            onValueChange = { viewModel.setFontSize(it) },
                            valueRange = 16f..34f,
                            steps = 9,
                            colors = SliderDefaults.colors(
                                thumbColor = MaterialTheme.colorScheme.primary,
                                activeTrackColor = MaterialTheme.colorScheme.primary
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Line spacing slider
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.FormatLineSpacing,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "تباعد الأسطر (${userSettings.lineSpacingSp.toInt()} sp)",
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
                            )
                        }

                        Slider(
                            value = userSettings.lineSpacingSp,
                            onValueChange = { viewModel.setLineSpacing(it) },
                            valueRange = 4f..20f,
                            steps = 8,
                            colors = SliderDefaults.colors(
                                thumbColor = MaterialTheme.colorScheme.primary,
                                activeTrackColor = MaterialTheme.colorScheme.primary
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Tashkeel toggle
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Spellcheck,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "عرض التشكيل والحركات",
                                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
                                    )
                                    Text(
                                        text = if (userSettings.showTashkeel) "التشكيل الكامل معروض" else "نص هادئ بدون تشكيل",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    )
                                }
                            }

                            Switch(
                                checked = userSettings.showTashkeel,
                                onCheckedChange = { viewModel.setShowTashkeel(it) }
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Live Preview Box
                        Text(
                            text = "معاينة حية للنص:",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            val sampleText = viewModel.formatDhikrText(
                                "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ ، سُبْحَانَ اللَّهِ الْعَظِيمِ",
                                userSettings.showTashkeel
                            )
                            Text(
                                text = sampleText,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontSize = userSettings.fontSizeSp.sp,
                                    lineHeight = (userSettings.fontSizeSp * 1.6f + userSettings.lineSpacingSp).sp,
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.onSurface
                                ),
                                modifier = Modifier.padding(14.dp)
                            )
                        }
                    }
                }
            }

            // ================= 3. نمط العد والتفاعل =================
            item {
                SettingsSectionHeader(title = "أنماط العد والتفاعل")
            }

            item {
                IslamicCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Count Mode (Button vs Fullscreen)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.TouchApp,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "نمط ضغط العداد",
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            val isButtonMode = userSettings.countMode == "button"
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isButtonMode) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { viewModel.setCountMode("button") }
                            ) {
                                Text(
                                    text = "زر دائري مخصص",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = if (isButtonMode) FontWeight.Bold else FontWeight.Normal,
                                        textAlign = TextAlign.Center
                                    ),
                                    modifier = Modifier.padding(12.dp)
                                )
                            }

                            val isScreenMode = userSettings.countMode == "fullscreen"
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isScreenMode) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { viewModel.setCountMode("fullscreen") }
                            ) {
                                Text(
                                    text = "لمس البطاقة بأكملها",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = if (isScreenMode) FontWeight.Bold else FontWeight.Normal,
                                        textAlign = TextAlign.Center
                                    ),
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Haptic feedback
                        SettingsToggleRow(
                            title = "الاهتزاز عند الضغط على العداد",
                            subtitle = "إحساس لمسي خفيف عند كل تسبيحة",
                            icon = Icons.Default.Vibration,
                            isChecked = userSettings.isHapticEnabled,
                            onCheckedChange = { viewModel.toggleHaptic(it) }
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Sound click
                        SettingsToggleRow(
                            title = "صوت نقرة العداد",
                            subtitle = "صوت خفيف وهادئ عند العد",
                            icon = Icons.Default.VolumeUp,
                            isChecked = userSettings.isSoundEnabled,
                            onCheckedChange = { viewModel.toggleSound(it) }
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Auto advance
                        SettingsToggleRow(
                            title = "الانتقال التلقائي للذكر التالي",
                            subtitle = "عند اكتمال عدد مرات الذكر ينتقل تلقائياً للذكر الذي يليه",
                            icon = Icons.Default.SkipNext,
                            isChecked = userSettings.autoAdvance,
                            onCheckedChange = { viewModel.toggleAutoAdvance(it) }
                        )
                    }
                }
            }

            // ================= 4. التنبيهات اليومية =================
            item {
                SettingsSectionHeader(title = "التنبيهات والأوراد")
            }

            item {
                IslamicCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        SettingsToggleRow(
                            title = "تذكير أذكار الصباح",
                            subtitle = "الساعة ${userSettings.morningReminderTime}",
                            icon = Icons.Default.WbSunny,
                            isChecked = userSettings.morningReminderEnabled,
                            onCheckedChange = { viewModel.toggleReminder("morning", it) }
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        SettingsToggleRow(
                            title = "تذكير أذكار المساء",
                            subtitle = "الساعة ${userSettings.eveningReminderTime}",
                            icon = Icons.Default.NightsStay,
                            isChecked = userSettings.eveningReminderEnabled,
                            onCheckedChange = { viewModel.toggleReminder("evening", it) }
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        SettingsToggleRow(
                            title = "تذكير أذكار النوم",
                            subtitle = "الساعة ${userSettings.sleepReminderTime}",
                            icon = Icons.Default.Bedtime,
                            isChecked = userSettings.sleepReminderEnabled,
                            onCheckedChange = { viewModel.toggleReminder("sleep", it) }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                viewModel.reminderHelper.showReminderNotification(
                                    "تذكير أذكار المساء",
                                    "﴿ أَلَا بِذِكْرِ اللَّهِ تَطْمَئِنُّ الْقُلُوبُ ﴾ حان وقت أذكار المساء"
                                )
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                            ),
                            modifier = Modifier.fillMaxWidth().testTag("test_notification_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("تجربة إشعار التذكير الآن")
                        }
                    }
                }
            }

            // ================= 5. البيانات وعن التطبيق =================
            item {
                SettingsSectionHeader(title = "البيانات والمعلومات")
            }

            item {
                IslamicCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Reset progress
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showResetDialog = true }
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.RestartAlt,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "تصفير تقدم اليوم",
                                        style = MaterialTheme.typography.bodyLarge.copy(
                                            fontWeight = FontWeight.Medium,
                                            color = MaterialTheme.colorScheme.error
                                        )
                                    )
                                    Text(
                                        text = "إعادة تعيين العدادات ونسب الإنجاز لليوم الحالي",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Share App
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    val sendIntent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(
                                            Intent.EXTRA_TEXT,
                                            "تطبيق حصن المسلم - أذكارك اليومية بين يديك بصوت وتلاوة وإحصائيات مفصلة."
                                        )
                                        type = "text/plain"
                                    }
                                    context.startActivity(Intent.createChooser(sendIntent, "مشاركة تطبيق حصن المسلم"))
                                }
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "مشاركة التطبيق مع الأهل والأصدقاء",
                                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // About App
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showAboutDialog = true }
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "عن تطبيق حصن المسلم",
                                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Reset Confirmation Dialog
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("تأكيد تصفير تقدم اليوم") },
            text = { Text("هل تود حقاً إعادة ضبط عدادات القراءة لليوم الحالي؟ لن يتم حذف مفضلتك أو إحصائياتك الإجمالية.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.resetAllTodayProgress()
                        showResetDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("نعم، تصفير")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }

    // About Dialog
    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = { Text("حصن المسلم - الإصدار الاحترافي") },
            text = {
                Column {
                    Text(
                        text = "تطبيق حصن المسلم مبني على كتاب «حصن المسلم من أذكار الكتاب والسنة» للشيخ سعيد بن علي بن وهف القحطاني رحمه الله.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "• محتوى موثوق ومحقق بدقة.\n• عمل كامل بدون إنترنت.\n• عدادات ذكية وتتبع يومي.\n• تصميم إسلامي حديث وسريع وخفيف.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                Button(onClick = { showAboutDialog = false }) {
                    Text("إغلاق")
                }
            }
        )
    }
}

@Composable
private fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
    )
}

@Composable
private fun SettingsToggleRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange
        )
    }
}
