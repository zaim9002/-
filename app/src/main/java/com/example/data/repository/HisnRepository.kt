package com.example.data.repository

import com.example.data.local.FavoriteDao
import com.example.data.local.ReadingProgressDao
import com.example.data.local.TasbeehDao
import com.example.data.local.UserSettingsDao
import com.example.data.model.Dhikr
import com.example.data.model.DhikrCategory
import com.example.data.model.FavoriteEntity
import com.example.data.model.ReadingProgressEntity
import com.example.data.model.TasbeehRecordEntity
import com.example.data.model.UserSettingsEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class HisnRepository(
    private val favoriteDao: FavoriteDao,
    private val readingProgressDao: ReadingProgressDao,
    private val tasbeehDao: TasbeehDao,
    private val userSettingsDao: UserSettingsDao
) {
    val categories: List<DhikrCategory> = HisnContentProvider.categories
    val allDhikrs: List<Dhikr> = HisnContentProvider.allDhikrs

    // Favorites Flow
    val favoriteIds: Flow<List<Int>> = favoriteDao.getAllFavoriteIds()

    val favoriteDhikrs: Flow<List<Dhikr>> = favoriteIds.map { ids ->
        val set = ids.toSet()
        allDhikrs.filter { it.id in set }
    }

    fun isFavorite(dhikrId: Int): Flow<Boolean> = favoriteDao.isFavorite(dhikrId)

    suspend fun toggleFavorite(dhikrId: Int, isFav: Boolean) {
        if (isFav) {
            favoriteDao.deleteFavorite(dhikrId)
        } else {
            favoriteDao.insertFavorite(FavoriteEntity(dhikrId))
        }
    }

    // Reading progress Flow
    val allProgress: Flow<List<ReadingProgressEntity>> = readingProgressDao.getAllProgress()
    val lastRead: Flow<ReadingProgressEntity?> = readingProgressDao.getLastRead()

    fun getProgressForDhikr(dhikrId: Int): Flow<ReadingProgressEntity?> =
        readingProgressDao.getProgressForDhikr(dhikrId)

    suspend fun saveProgress(dhikrId: Int, categoryId: String, currentCount: Int, targetCount: Int) {
        val completed = currentCount >= targetCount
        val entity = ReadingProgressEntity(
            dhikrId = dhikrId,
            categoryId = categoryId,
            currentCount = currentCount,
            targetCount = targetCount,
            isCompleted = completed,
            lastReadTimestamp = System.currentTimeMillis()
        )
        readingProgressDao.saveProgress(entity)
    }

    suspend fun resetAllProgress() {
        readingProgressDao.resetAllProgress()
    }

    // Tasbeeh
    val allTasbeehRecords: Flow<List<TasbeehRecordEntity>> = tasbeehDao.getAllTasbeehRecords()

    suspend fun getOrCreateTasbeeh(phrase: String, target: Int = 33): TasbeehRecordEntity {
        val existing = tasbeehDao.getByPhrase(phrase)
        if (existing != null) return existing
        val newRecord = TasbeehRecordEntity(
            phrase = phrase,
            currentCount = 0,
            targetCount = target,
            totalCount = 0
        )
        val id = tasbeehDao.insertOrUpdate(newRecord)
        return newRecord.copy(id = id.toInt())
    }

    suspend fun updateTasbeehCount(record: TasbeehRecordEntity, newCurrent: Int, newTotal: Int) {
        tasbeehDao.insertOrUpdate(
            record.copy(
                currentCount = newCurrent,
                totalCount = newTotal,
                lastUpdated = System.currentTimeMillis()
            )
        )
    }

    suspend fun updateTasbeehTarget(record: TasbeehRecordEntity, newTarget: Int) {
        tasbeehDao.insertOrUpdate(
            record.copy(
                targetCount = newTarget,
                lastUpdated = System.currentTimeMillis()
            )
        )
    }

    suspend fun resetTasbeeh(id: Int) {
        tasbeehDao.resetCounter(id)
    }

    // User Settings
    val userSettings: Flow<UserSettingsEntity> = userSettingsDao.getSettings().map { entity ->
        entity ?: UserSettingsEntity()
    }

    suspend fun saveSettings(settings: UserSettingsEntity) {
        userSettingsDao.saveSettings(settings)
    }

    // Search and helper
    fun search(query: String): List<Dhikr> = HisnContentProvider.searchDhikrs(query)

    fun getDhikrsForCategory(categoryId: String): List<Dhikr> =
        HisnContentProvider.getDhikrsByCategory(categoryId)

    fun getDhikrById(id: Int): Dhikr? = HisnContentProvider.getDhikrById(id)
}
