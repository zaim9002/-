package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey val dhikrId: Int,
    val addedTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "reading_progress")
data class ReadingProgressEntity(
    @PrimaryKey val dhikrId: Int,
    val categoryId: String,
    val currentCount: Int,
    val targetCount: Int,
    val isCompleted: Boolean = false,
    val lastReadTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "tasbeeh_records")
data class TasbeehRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val phrase: String,
    val currentCount: Int = 0,
    val targetCount: Int = 33,
    val totalCount: Int = 0,
    val lastUpdated: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_settings")
data class UserSettingsEntity(
    @PrimaryKey val id: Int = 1,
    val fontSizeSp: Float = 22f,
    val isDarkMode: Boolean? = null, // null for system default
    val isHapticEnabled: Boolean = true,
    val isSoundEnabled: Boolean = true,
    val autoAdvance: Boolean = true,
    val languageCode: String = "ar",
    val morningReminderEnabled: Boolean = true,
    val morningReminderTime: String = "06:30",
    val eveningReminderEnabled: Boolean = true,
    val eveningReminderTime: String = "17:00",
    val sleepReminderEnabled: Boolean = false,
    val sleepReminderTime: String = "22:00"
)
