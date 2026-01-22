package com.example.ecodonnees.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quota")
data class QuotaEntity(
    @PrimaryKey val id: Int = 1,
    val quotaBytes: Long,
    val expiryTimestamp: Long,
    val isEnabled: Boolean
)