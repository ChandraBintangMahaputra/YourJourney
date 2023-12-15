package com.example.yourjourney.datainjection

import com.example.yourjourney.data.JourneyService
import com.example.yourjourney.data.dcService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
class ServiceModule {

    @Provides
    fun provideAuthService(retrofit: Retrofit): dcService = retrofit.create(dcService::class.java)

    @Provides
    fun provideStoryService(retrofit: Retrofit):JourneyService = retrofit.create(JourneyService::class.java)


}