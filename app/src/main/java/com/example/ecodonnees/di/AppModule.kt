package com.example.ecodonnees.di

import android.content.Context
import androidx.room.Room
import com.example.ecodonnees.data.local.database.AppDatabase
import com.example.ecodonnees.data.repository.QuotaRepositoryImpl
import com.example.ecodonnees.data.repository.UsageRepositoryImpl
import com.example.ecodonnees.domain.repository.QuotaRepository
import com.example.ecodonnees.domain.repository.UsageRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "ecodata_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideQuotaRepository(db: AppDatabase): QuotaRepository {
        return QuotaRepositoryImpl(db.quotaDao())
    }

    @Provides
    @Singleton
    fun provideUsageRepository(db: AppDatabase): UsageRepository {
        return UsageRepositoryImpl(db.usageDao())
    }
}