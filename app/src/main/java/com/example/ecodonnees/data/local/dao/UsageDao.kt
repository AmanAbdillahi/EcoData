package com.example.ecodonnees.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.ecodonnees.data.local.entity.UsageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UsageDao {
    @Query("SELECT * FROM usage WHERE id = 1")
    fun getUsage(): Flow<UsageEntity?>

    @Query("SELECT * FROM usage WHERE id = 1")
    suspend fun getUsageOnce(): UsageEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsage(usage: UsageEntity)

    @Query("UPDATE usage SET totalBytesUsed = :bytes WHERE id = 1")
    suspend fun updateUsage(bytes: Long)

    @Query("UPDATE usage SET totalBytesUsed = 0, lastResetTimestamp = :timestamp WHERE id = 1")
    suspend fun resetUsage(timestamp: Long)
}