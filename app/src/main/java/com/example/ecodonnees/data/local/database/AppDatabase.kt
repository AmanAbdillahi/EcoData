package com.example.ecodonnees.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.ecodonnees.data.local.dao.QuotaDao
import com.example.ecodonnees.data.local.dao.UsageDao
import com.example.ecodonnees.data.local.entity.QuotaEntity
import com.example.ecodonnees.data.local.entity.UsageEntity

@Database(
    entities = [QuotaEntity::class, UsageEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun quotaDao(): QuotaDao
    abstract fun usageDao(): UsageDao
}