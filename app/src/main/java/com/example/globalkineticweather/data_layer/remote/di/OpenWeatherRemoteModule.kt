package com.example.globalkineticweather.data_layer.remote.di

import com.example.globalkineticweather.data_layer.remote.OpenWeatherAPI
import com.example.globalkineticweather.data_layer.remote.OpenWeatherRemoteDataSource
import com.example.globalkineticweather.data_layer.remote.OpenWeatherRemoteDataSourceImp
import com.example.globalkineticweather.data_layer.remote.OpenWeatherServiceBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object OpenWeatherRemoteModule {

    @Provides
    fun providesOpenWeatherAPI(): OpenWeatherAPI = OpenWeatherServiceBuilder.weatherAPIService

    @Provides
    fun providesOpenWeatherRemoteDataSource(
        openWeatherAPI: OpenWeatherAPI
    ): OpenWeatherRemoteDataSource = OpenWeatherRemoteDataSourceImp(openWeatherAPI)
}