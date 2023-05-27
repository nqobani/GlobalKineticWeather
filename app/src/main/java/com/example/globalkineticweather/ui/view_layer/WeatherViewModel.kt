package com.example.globalkineticweather.ui.view_layer

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.globalkineticweather.Resource
import com.example.globalkineticweather.business_layer.WeatherRepository
import com.example.globalkineticweather.data_layer.remote.models.Coord
import com.example.globalkineticweather.data_layer.remote.models.CurrentWeather
import com.example.globalkineticweather.data_layer.remote.models.WeatherForecast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
   private val repository: WeatherRepository
): ViewModel() {
    var currentWeather = mutableStateOf<Resource<CurrentWeather>>(Resource(Resource.Status.LOADING, null, null))
    var weatherForecast = mutableStateOf<Resource<WeatherForecast>>(Resource(Resource.Status.LOADING, null, null))
    var mapIsReady = mutableStateOf(false)
    val mapCoord = mutableStateOf<Coord?>(null)

    fun getCurrentWeather(lat: Double, lon: Double) {
        viewModelScope.launch {
            repository.getCurrentWeather(lat, lon).collect { result ->
                currentWeather.value = result
            }
        }
    }

    fun getWeatherForecast(lat: Double, lon: Double) {
        viewModelScope.launch {
            repository.getWeatherForecast(lat, lon).collect { result ->
                weatherForecast.value = result
            }
        }
    }
}