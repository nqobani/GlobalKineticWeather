package com.example.globalkineticweather.data_layer.remote

import com.example.globalkineticweather.Resource
import com.example.globalkineticweather.data_layer.remote.models.CurrentWeather
import com.example.globalkineticweather.data_layer.remote.models.WeatherForecast

interface OpenWeatherRemoteDataSource {
    suspend fun getCurrentWeather(
        lat: Double,
        lon: Double,
        apiKey: String,
        units: String
    ): Resource<CurrentWeather>

    suspend fun getWeatherForecast(
        lat: Double,
        lon: Double,
        apiKey: String,
        units: String
    ): Resource<WeatherForecast>
}