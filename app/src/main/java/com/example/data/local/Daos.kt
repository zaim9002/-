package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.FavoriteEntity
import com.example.data.model.ReadingProgressEntity
import com.example.data.model.TasbeehRecordEntity
import com.example.data.model.UserSettingsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    @Query("SELECT dhikrId FROM favorites")
    fun getAllFavoriteIds(): Flow<List<Int>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE dhikrId = :dhikrId)")
    fun isFavorite(dhikrId: Int): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE dhikrId = :dhikrId")
    suspend fun deleteFavorite(dhikrId: Int)

    @Query("DELETE FROM favorites")
    suspend fun clearFavorites()
}

@Dao
interface ReadingProgressDao {
    @Query("SELECT * FROM reading_progress")
    fun getAllProgress(): Flow<List<ReadingProgressEntity>>

    @Query("SELECT * FROM reading_progress WHERE dhikrId = :dhikrId LIMIT 1")
    fun getProgressForDhikr(dhikrId: Int): Flow<ReadingProgressEntity?>

    @Query("SELECT * FROM reading_progress WHERE dhikrId = :dhikrId LIMIT 1")
    suspend fun getProgressForDhikrSync(dhikrId: Int): ReadingProgressEntity?

    @Query("SELECT * FROM reading_progress ORDER BY lastReadTimestamp DESC LIMIT 1")
    fun getLastRead(): Flow<ReadingProgressEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveProgress(progress: ReadingProgressEntity)

    @Query("DELETE FROM reading_progress")
    suspend fun resetAllProgress()
}

@Dao
interface TasbeehDao {
    @Query("SELECT * FROM tasbeeh_records ORDER BY lastUpdated DESC")
    fun getAllTasbeehRecords(): Flow<List<TasbeehRecordEntity>>

    @Query("SELECT * FROM tasbeeh_records WHERE phrase = :phrase LIMIT 1")
    suspend fun getByPhrase(phrase: String): TasbeehRecordEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(record: TasbeehRecordEntity): Long

    @Query("DELETE FROM tasbeeh_records WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("UPDATE tasbeeh_records SET currentCount = 0 WHERE id = :id")
    suspend fun resetCounter(id: Int)
}

@Dao
interface UserSettingsDao {
    @Query("SELECT * FROM user_settings WHERE id = 1 LIMIT 1")
    fun getSettings(): Flow<UserSettingsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSettings(settings: UserSettingsEntity)
}
