package com.example.globalkineticweather.business_layer

import com.example.globalkineticweather.Constants
import com.example.globalkineticweather.Resource
import com.example.globalkineticweather.TestConstants
import com.example.globalkineticweather.data_layer.remote.OpenWeatherAPI
import com.example.globalkineticweather.data_layer.remote.OpenWeatherRemoteDataSource
import com.example.globalkineticweather.isError
import com.example.globalkineticweather.isLoading
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class WeatherRepositoryTest {
    @Mock
    lateinit var remoteDataSource: OpenWeatherRemoteDataSource
    private lateinit var weatherRepository: WeatherRepository
    private lateinit var mockWebServer: MockWebServer
    private lateinit var openWeatherAPI: OpenWeatherAPI

    @Before
    fun before() {
        MockitoAnnotations.openMocks(this)
        mockWebServer = MockWebServer()
        mockWebServer.start()
        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        openWeatherAPI = retrofit.create(OpenWeatherAPI::class.java)
        weatherRepository = WeatherRepository(remoteDataSource)
    }

    @After
    fun shutdown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `successfully get current weather - make sure the app through the correct steps`() = runBlocking {
        mockWebServer.enqueue(MockResponse().setBody(TestConstants.currentWeatherJSON))
        val result = openWeatherAPI.getRemoteCurrentWeather(12.0,12.0,"test", "test")
        val resourceResult = Resource(Resource.Status.SUCCESS, result.body(), null)
        Mockito.`when`(remoteDataSource.getCurrentWeather(12.0, 12.0, Constants.apiKey, Constants.weatherUnits)).thenReturn(resourceResult)
        val expectedList = listOf(Resource(Resource.Status.LOADING, null, null), Resource(Resource.Status.SUCCESS, result.body(), null))
        val emittedData = weatherRepository.getCurrentWeather(12.0, 12.0).toList()
        assertEquals(expectedList, emittedData)
    }

    @Test
    fun `successfully get weather forecast - make sure the app through the correct steps`() = runBlocking {
        mockWebServer.enqueue(MockResponse().setBody(TestConstants.weatherForecastJSON))
        val result = openWeatherAPI.getRemoteWeatherForecast(12.0,12.0,"test", "test")
        val resourceResult = Resource(Resource.Status.SUCCESS, result.body(), null)
        Mockito.`when`(remoteDataSource.getWeatherForecast(12.0, 12.0, Constants.apiKey, Constants.weatherUnits)).thenReturn(resourceResult)
        val expectedList = listOf(Resource(Resource.Status.LOADING, null, null), Resource(Resource.Status.SUCCESS, result.body(), null))
        val emittedData = weatherRepository.getWeatherForecast(12.0, 12.0).toList()
        assertEquals(expectedList, emittedData)
    }

    @Test
    fun `fail test to get current weather - make sure the app through the correct steps`() = runBlocking {
        val errorMessage = "Server Error"
        mockWebServer.enqueue(MockResponse().setResponseCode(500))
        val result = openWeatherAPI.getRemoteCurrentWeather(12.0,12.0,"test", "test")
        val resourceResult = Resource(Resource.Status.ERROR, null, Exception(result.message()))
        Mockito.`when`(remoteDataSource.getCurrentWeather(12.0, 12.0, Constants.apiKey, Constants.weatherUnits)).thenReturn(resourceResult)
        //val expectedList = listOf(Resource(Resource.Status.LOADING, null, null), Resource(Resource.Status.ERROR, null, Exception(errorMessage)))
        val emittedData = weatherRepository.getCurrentWeather(12.0, 12.0).toList()
        //assertEquals(expectedList, emittedData)
        assert(emittedData[0].isLoading())
        assert(emittedData[1].isError())
        assert(emittedData[1].error?.message == errorMessage)
    }

    @Test
    fun `fail test to get the weather forecast - make sure the app through the correct steps`() = runBlocking {
        val errorMessage = "Server Error"
        mockWebServer.enqueue(MockResponse().setResponseCode(500))
        val result = openWeatherAPI.getRemoteWeatherForecast(12.0,12.0,"test", "test")
        val resourceResult = Resource(Resource.Status.ERROR, null, Exception(result.message()))
        Mockito.`when`(remoteDataSource.getWeatherForecast(12.0, 12.0, Constants.apiKey, Constants.weatherUnits)).thenReturn(resourceResult)
        //val expectedList = listOf(Resource(Resource.Status.LOADING, null, null), Resource(Resource.Status.ERROR, null, Exception(errorMessage)))
        val emittedData = weatherRepository.getWeatherForecast(12.0, 12.0).toList()
        //assertEquals(expectedList, emittedData) the expected and actual results are the same but the test is failing
        assert(emittedData[0].isLoading())
        assert(emittedData[1].isError())
        assert(emittedData[1].error?.message == errorMessage)
    }
}