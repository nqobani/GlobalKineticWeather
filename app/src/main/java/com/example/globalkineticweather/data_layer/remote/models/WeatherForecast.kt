package com.example.globalkineticweather.data_layer.remote.models

data class WeatherForecast(
    val city: City,
    val cnt: Int,
    val cod: String,
    val list: List<list>,
    val message: String
)