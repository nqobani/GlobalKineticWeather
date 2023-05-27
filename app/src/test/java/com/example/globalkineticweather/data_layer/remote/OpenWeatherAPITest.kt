package com.example.globalkineticweather.data_layer.remote

import com.example.globalkineticweather.TestConstants
import com.example.globalkineticweather.data_layer.remote.models.CurrentWeather
import com.example.globalkineticweather.data_layer.remote.models.WeatherForecast
import com.google.gson.Gson
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import retrofit2.Response

class OpenWeatherAPITest {

    @Mock
    lateinit var openWeatherAPI: OpenWeatherAPI

    private lateinit var currentWeather: CurrentWeather
    private lateinit var weatherForecast: WeatherForecast

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        val gson = Gson()
        currentWeather = gson.fromJson(TestConstants.currentWeatherJSON, CurrentWeather::class.java)
        weatherForecast = gson.fromJson(TestConstants.weatherForecastJSON, WeatherForecast::class.java)
    }

    @Test
    fun `test a successful request for current weather`() = runBlocking {
        val response = Response.success(currentWeather)
        `when`(openWeatherAPI.getRemoteCurrentWeather(12.1,12.1,"test", "test")).thenReturn(response)
        val result = openWeatherAPI.getRemoteCurrentWeather(12.1,12.1,"test", "test")
        assertEquals(response.isSuccessful, result.isSuccessful)
        assertEquals(response.body(), result.body())
    }

    @Test
    fun `test a failed request for current weather`() = runBlocking {
        val responseMessage = "Invalid API Key"
        val errorResponseBody = responseMessage.toResponseBody("text/plain".toMediaType())
        val response = Response.error<CurrentWeather>(401, errorResponseBody)
        `when`(openWeatherAPI.getRemoteCurrentWeather(12.1,12.1,"test", "test")).thenReturn(response)
        val result = openWeatherAPI.getRemoteCurrentWeather(12.1,12.1,"test", "test")
        assertEquals(response.isSuccessful, result.isSuccessful)
        assertEquals(response.errorBody(), result.errorBody())
    }

    @Test
    fun `test a successful request for a weather forecast`() = runBlocking {
        val response = Response.success(weatherForecast)
        `when`(openWeatherAPI.getRemoteWeatherForecast(12.1,12.1,"test", "test")).thenReturn(response)
        val result = openWeatherAPI.getRemoteWeatherForecast(12.1,12.1,"test", "test")
        assertEquals(response.isSuccessful, result.isSuccessful)
        assertEquals(response.body(), result.body())
    }

    @Test
    fun `test a failed request for a weather forecast`() = runBlocking {
        val responseMessage = "Invalid API Key"
        val errorResponseBody = responseMessage.toResponseBody("text/plain".toMediaType())
        val response = Response.error<CurrentWeather>(401, errorResponseBody)
        `when`(openWeatherAPI.getRemoteCurrentWeather(12.1,12.1,"test", "test")).thenReturn(response)
        val result = openWeatherAPI.getRemoteCurrentWeather(12.1,12.1,"test", "test")
        assertEquals(response.isSuccessful, result.isSuccessful)
        assertEquals(response.errorBody(), result.errorBody())
    }
}