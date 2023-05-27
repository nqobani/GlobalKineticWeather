package com.example.globalkineticweather.data_layer.remote

import com.example.globalkineticweather.TestConstants
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.MockitoAnnotations
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class OpenWeatherRemoteDataSourceImpTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var openWeatherAPI: OpenWeatherAPI
    private lateinit var openWeatherRemoteDataSource: OpenWeatherRemoteDataSource

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        mockWebServer = MockWebServer()
        mockWebServer.start()
        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        openWeatherAPI = retrofit.create(OpenWeatherAPI::class.java)
        openWeatherRemoteDataSource = OpenWeatherRemoteDataSourceImp(openWeatherAPI)
    }

    @Test
    fun `get current weather success`() = runBlocking {
        mockWebServer.enqueue(MockResponse().setBody(TestConstants.currentWeatherJSON))
        val result = openWeatherAPI.getRemoteCurrentWeather(12.1,12.1,"test", "test")
        assert(result.isSuccessful)
        assertEquals(result.code(), 200)
        assertEquals(result.body()?.name, "Shuzenji")
    }

    @Test
    fun `get current weather fail with 500`() = runBlocking {
        mockWebServer.enqueue(MockResponse().setResponseCode(500))
        val result = openWeatherAPI.getRemoteCurrentWeather(12.1,12.1,"test", "test")
        assert(!result.isSuccessful)
        assertEquals(result.code(), 500)
    }

    @Test
    fun `get current weather fail with 401`() = runBlocking {
        mockWebServer.enqueue(MockResponse().setResponseCode(401))
        val result = openWeatherAPI.getRemoteCurrentWeather(12.1,12.1,"test", "test")
        assert(!result.isSuccessful)
        assertEquals(result.code(), 401)
    }

    @Test
    fun `get weather forecast success`() = runBlocking {
        mockWebServer.enqueue(MockResponse().setBody(TestConstants.weatherForecastJSON))
        val result = openWeatherAPI.getRemoteWeatherForecast(12.1,12.1,"test", "test")
        assert(result.isSuccessful)
        assertEquals(result.code(), 200)
        assertEquals(result.body()?.city?.name, "Shuzenji")
    }

    @Test
    fun `get weather forecast fail with 500`() = runBlocking {
        mockWebServer.enqueue(MockResponse().setResponseCode(500))
        val result = openWeatherAPI.getRemoteWeatherForecast(12.1,12.1,"test", "test")
        assert(!result.isSuccessful)
        assertEquals(result.code(), 500)
    }

    @Test
    fun `get weather forecast fail with 401`() = runBlocking {
        mockWebServer.enqueue(MockResponse().setResponseCode(401))
        val result = openWeatherAPI.getRemoteWeatherForecast(12.1,12.1,"test", "test")
        assert(!result.isSuccessful)
        assertEquals(result.code(), 401)
    }


    @After
    fun shutDown() {
        mockWebServer.shutdown()
    }
}