package com.example.globalkineticweather.data_layer.remote

import com.example.globalkineticweather.Resource
import com.example.globalkineticweather.data_layer.remote.models.CurrentWeather
import com.example.globalkineticweather.data_layer.remote.models.WeatherForecast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async

class OpenWeatherRemoteDataSourceImp(
    private val openWeatherAPI: OpenWeatherAPI
    ): OpenWeatherRemoteDataSource {

    override suspend fun getCurrentWeather(
        lat: Double,
        lon: Double,
        apiKey: String,
        units: String
    ): Resource<CurrentWeather> {
        return try {
            val call = CoroutineScope(Dispatchers.IO).async {
                openWeatherAPI.getRemoteCurrentWeather(lat, lon, apiKey, units)
            }
            val response = call.await()
            if (response.isSuccessful && response.body() != null) {
                Resource(Resource.Status.SUCCESS, response.body(), null)
            } else {
                Resource(Resource.Status.ERROR, null, Exception(response.message() ?: "Something went wrong"))
            }
        } catch (e :Exception) {
            Resource(Resource.Status.ERROR, null, e)
        }
    }

    override suspend fun getWeatherForecast(
        lat: Double,
        lon: Double,
        apiKey: String,
        units: String
    ): Resource<WeatherForecast> {
        return try {
            val call = CoroutineScope(Dispatchers.IO).async {
                openWeatherAPI.getRemoteWeatherForecast(lat, lon, apiKey, units)
            }
            val response =  call.await()
            if (response.isSuccessful && response.body() != null) {
                Resource(Resource.Status.SUCCESS, response.body(), null)
            } else {
                Resource(Resource.Status.ERROR, null, Exception(response.message() ?: "Something went wrong"))
            }
        } catch (e :Exception) {
            Resource(Resource.Status.ERROR, null, e)
        }
    }
}