package com.pawix25.spooler.di

import android.content.Context
import androidx.room.Room
import com.pawix25.spooler.data.AppDatabase
import com.pawix25.spooler.data.SpoolDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    fun provideSpoolDao(database: AppDatabase): SpoolDao {
        return database.spoolDao()
    }
}
