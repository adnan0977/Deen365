package com.example.data.local

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "user_habits")
data class UserHabitEntity(
    @PrimaryKey
    val id: String = "current_user",
    val userName: String = "Learner of Deen",
    val userEmail: String = "user@deen365.org",
    val isLoggedIn: Boolean = false,
    val subscriptionTier: String = "Free Tier",
    val isSubscriptionActive: Boolean = true,
    val watchedVideoIdsCsv: String = "",
    val totalWatchMinutes: Int = 45,
    val primarySpiritualGoal: String = "Consistently pray 5 daily prayers and learn Tafseer",
    val lastAiHabitInsight: String = "",
    val lastAnalyzedDate: String = "",
    val selectedBottomTabsCsv: String = "DASHBOARD,QURAN,AI_GUIDE,HADITH_DUAS,MORE"
) {
    fun getWatchedIdsSet(): Set<String> {
        return if (watchedVideoIdsCsv.isBlank()) emptySet() else watchedVideoIdsCsv.split(",").filter { it.isNotBlank() }.toSet()
    }

    fun getSelectedBottomTabNames(): List<String> {
        return if (selectedBottomTabsCsv.isBlank()) {
            listOf("DASHBOARD", "QURAN", "AI_GUIDE", "HADITH_DUAS", "MORE")
        } else {
            selectedBottomTabsCsv.split(",").filter { it.isNotBlank() }
        }
    }
}

@Dao
interface UserHabitDao {
    @Query("SELECT * FROM user_habits WHERE id = 'current_user' LIMIT 1")
    fun getUserHabitFlow(): Flow<UserHabitEntity?>

    @Query("SELECT * FROM user_habits WHERE id = 'current_user' LIMIT 1")
    suspend fun getUserHabitSync(): UserHabitEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUserHabit(habit: UserHabitEntity)
}
