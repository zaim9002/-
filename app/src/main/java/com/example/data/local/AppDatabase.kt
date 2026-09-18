package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.FavoriteEntity
import com.example.data.model.ReadingProgressEntity
import com.example.data.model.TasbeehRecordEntity
import com.example.data.model.UserSettingsEntity

@Database(
    entities = [
        FavoriteEntity::class,
        ReadingProgressEntity::class,
        TasbeehRecordEntity::class,
        UserSettingsEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteDao
    abstract fun readingProgressDao(): ReadingProgressDao
    abstract fun tasbeehDao(): TasbeehDao
    abstract fun userSettingsDao(): UserSettingsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "hisn_almuslim.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
