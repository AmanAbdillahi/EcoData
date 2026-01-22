package com.example.ecodonnees.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usage")
data class UsageEntity(
    @PrimaryKey val id: Int = 1,
    val totalBytesUsed: Long,
    val lastResetTimestamp: Long
)