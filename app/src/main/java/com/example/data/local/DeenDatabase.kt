package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        AyahEntity::class,
        HadithEntity::class,
        PrayerRecordEntity::class,
        FastingRecordEntity::class,
        DailyReflectionEntity::class,
        PrayerTimeEntity::class,
        IslamicVideoEntity::class,
        UserHabitEntity::class,
        DhikrItemEntity::class,
        DhikrDailyLogEntity::class,
        NamazPrayerEntity::class,
        NamazStepEntity::class,
        NamazAzanEntity::class,
        DuaEntity::class,
        KitabCompletionEntity::class,
        HadithBookEntity::class,
        HadithChapterEntity::class,
        QuranHighlightEntity::class,
        QuranGoalBookmarkEntity::class
    ],
    version = 15,
    exportSchema = false
)
abstract class DeenDatabase : RoomDatabase() {
    abstract fun ayahDao(): AyahDao
    abstract fun hadithDao(): HadithDao
    abstract fun trackerDao(): TrackerDao
    abstract fun dailyReflectionDao(): DailyReflectionDao
    abstract fun prayerTimeDao(): PrayerTimeDao
    abstract fun islamicVideoDao(): IslamicVideoDao
    abstract fun userHabitDao(): UserHabitDao
    abstract fun dhikrDao(): DhikrDao
    abstract fun namazDao(): NamazDao
    abstract fun duaDao(): DuaDao
    abstract fun kitabCompletionDao(): KitabCompletionDao
    abstract fun hadithKitabTrackerDao(): HadithKitabTrackerDao
    abstract fun quranStudyDao(): QuranStudyDao

    companion object {
        @Volatile
        private var INSTANCE: DeenDatabase? = null

        fun getDatabase(context: Context): DeenDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DeenDatabase::class.java,
                    "deen365_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
