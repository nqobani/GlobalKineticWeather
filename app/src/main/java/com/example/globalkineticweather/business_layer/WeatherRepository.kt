package com.example.globalkineticweather.business_layer

import com.example.globalkineticweather.Constants
import com.example.globalkineticweather.Resource
import com.example.globalkineticweather.data_layer.remote.OpenWeatherRemoteDataSource
import com.example.globalkineticweather.isError
import com.example.globalkineticweather.isSuccess
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class WeatherRepository @Inject constructor(
    private val remoteDataSource: OpenWeatherRemoteDataSource
) {
    fun getCurrentWeather(
        lat: Double,
        lon: Double
    ) = flow {
        emit(Resource(Resource.Status.LOADING, null, null))
        val result = remoteDataSource.getCurrentWeather(lat, lon, Constants.apiKey, Constants.weatherUnits)
        if (result.isSuccess()) {
            emit(Resource(Resource.Status.SUCCESS, result.data, null))
        }
        if (result.isError()) {
            emit(Resource(Resource.Status.ERROR, null, result.error))
        }
    }

    fun getWeatherForecast(
        lat: Double,
        lon: Double
    ) = flow {
        emit(Resource(Resource.Status.LOADING, null, null))
        val result = remoteDataSource.getWeatherForecast(lat, lon, Constants.apiKey, Constants.weatherUnits)
        if (result.isSuccess()) {
            emit(Resource(Resource.Status.SUCCESS, result.data, null))
        }
        if (result.isError()) {
            emit(Resource(Resource.Status.ERROR, null, result.error))
        }
    }
}