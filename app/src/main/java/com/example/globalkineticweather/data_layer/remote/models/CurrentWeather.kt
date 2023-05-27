package com.example.globalkineticweather.data_layer.remote.models

data class CurrentWeather(
    val base: String,
    val clouds: Clouds,
    val cod: Double,
    val coord: Coord,
    val dt: Double,
    val id: Int,
    val main: Main,
    val name: String,
    val sys: Sys,
    val timezone: Int,
    val visibility: Double,
    val weather: List<Weather>,
    val wind: Wind
)