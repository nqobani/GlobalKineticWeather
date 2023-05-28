package com.example.globalkineticweather.di

import android.content.Context
import com.example.globalkineticweather.business_layer.WeatherRepository
import com.example.globalkineticweather.data_layer.remote.OpenWeatherRemoteDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped


@Module
@InstallIn(ViewModelComponent::class)
object AppModule {
    @ViewModelScoped
    @Provides
    fun providesWeatherRepository(
        remoteDataSource: OpenWeatherRemoteDataSource
    ): WeatherRepository = WeatherRepository(remoteDataSource)
}