package com.example.ecodonnees.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.ecodonnees.data.local.entity.QuotaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface QuotaDao {
    @Query("SELECT * FROM quota WHERE id = 1")
    fun getQuota(): Flow<QuotaEntity?>

    @Query("SELECT * FROM quota WHERE id = 1")
    suspend fun getQuotaOnce(): QuotaEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuota(quota: QuotaEntity)

    @Query("UPDATE quota SET isEnabled = :enabled WHERE id = 1")
    suspend fun updateEnabled(enabled: Boolean)
}