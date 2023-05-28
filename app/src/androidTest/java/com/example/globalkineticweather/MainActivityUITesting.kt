package com.example.globalkineticweather

import android.content.Context
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import com.example.globalkineticweather.business_layer.WeatherRepository
import com.example.globalkineticweather.data_layer.remote.models.CurrentWeather
import com.example.globalkineticweather.data_layer.remote.models.WeatherForecast
import com.example.globalkineticweather.di.AppModule
import com.example.globalkineticweather.ui.view_layer.CurrentWeatherView
import com.example.globalkineticweather.ui.view_layer.MainView
import com.example.globalkineticweather.ui.view_layer.WeatherForecastView
import com.example.globalkineticweather.ui.view_layer.WeatherViewModel
import com.google.gson.Gson
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
@UninstallModules(AppModule::class)
class MainActivityUITesting {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createComposeRule()

    @Inject
    lateinit var weatherRepository: WeatherRepository
    private lateinit var viewModel: WeatherViewModel
    private lateinit var viewModelStore: ViewModelStore


    private lateinit var currentWeather: CurrentWeather
    private lateinit var weatherForecast: WeatherForecast

    @Before
    fun start(){
        hiltRule.inject()
        val gson = Gson()
        viewModelStore = ViewModelStore()
        currentWeather = gson.fromJson(TestConstants.currentWeatherJSON, CurrentWeather::class.java)
        weatherForecast = gson.fromJson(TestConstants.weatherForecastJSON, WeatherForecast::class.java)
        val viewModelFactory = WeatherViewModelFactory(weatherRepository)
        viewModel = ViewModelProvider(viewModelStore, viewModelFactory)[WeatherViewModel::class.java]
    }

    @After
    fun shutdown() {
        viewModelStore.clear()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testCurrentWeatherMainViewUI() = runTest {
        composeRule.setContent {
            CurrentWeatherView(currentWeather)
        }
        composeRule.onNode(
            hasText(currentWeather.name)
        ).assertExists()

        composeRule.onNode(
            hasText("${currentWeather.weather[0].description} ${currentWeather.main.temp_max}°/${currentWeather.main.temp_min}°")
        ).assertExists()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testWeatherForecastMainViewUI() = runTest {
        composeRule.setContent {
            WeatherForecastView(Resource.success(weatherForecast))
        }
        composeRule.onNode(
            hasText("5-DAY FORECAST")
        ).assertExists()

        composeRule.onNode(
            hasText("Fri")
        ).assertExists()
    }

}