package com.example.globalkineticweather.data_layer.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class OpenWeatherServiceBuilder {
    companion object {
        private var retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        var weatherAPIService: OpenWeatherAPI = retrofit.create(OpenWeatherAPI::class.java)
    }
}