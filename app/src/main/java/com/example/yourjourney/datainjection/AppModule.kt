package com.example.yourjourney.datainjection

import android.content.Context
import androidx.room.Room
import com.example.yourjourney.database.AppDatabase
import com.example.yourjourney.dao.JourneyDao
import com.example.yourjourney.dao.RemoteKeysDao
import com.example.yourjourney.extention.Value.DB_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, DB_NAME)
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideStoryDao(database: AppDatabase): JourneyDao = database.getStoryDao()

    @Provides
    fun provideRemoteKeyDao(database: AppDatabase): RemoteKeysDao = database.getRemoteKeysDao()

}