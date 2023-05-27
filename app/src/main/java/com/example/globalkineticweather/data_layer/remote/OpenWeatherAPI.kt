package com.example.globalkineticweather.data_layer.remote

import com.example.globalkineticweather.data_layer.remote.models.CurrentWeather
import com.example.globalkineticweather.data_layer.remote.models.WeatherForecast
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenWeatherAPI {
    @GET("weather?")
    suspend fun getRemoteCurrentWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String
    ): Response<CurrentWeather>

    @GET("forecast?")
    suspend fun getRemoteWeatherForecast(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String
    ): Response<WeatherForecast>
}