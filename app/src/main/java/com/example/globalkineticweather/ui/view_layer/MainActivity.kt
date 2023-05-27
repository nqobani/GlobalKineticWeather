package com.example.globalkineticweather.ui.view_layer

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import coil.compose.AsyncImage
import com.example.globalkineticweather.DateTimeUtils
import com.example.globalkineticweather.R
import com.example.globalkineticweather.Resource
import com.example.globalkineticweather.data_layer.remote.models.Coord
import com.example.globalkineticweather.data_layer.remote.models.CurrentWeather
import com.example.globalkineticweather.data_layer.remote.models.WeatherForecast
import com.example.globalkineticweather.data_layer.remote.models.list
import com.example.globalkineticweather.ui.theme.OpenWeatherDialogButtonColor
import com.example.globalkineticweather.ui.theme.OpenWeatherErrorColor
import com.example.globalkineticweather.ui.theme.OpenWeatherLightBlue
import com.example.globalkineticweather.ui.theme.OpenWeatherStrongBlue
import com.example.globalkineticweather.ui.theme.OpenWeatherStrongBlue30PercentOpacity
import com.example.globalkineticweather.ui.theme.OpenWeatherWhite
import com.example.globalkineticweather.ui.theme.OpenWeatherWhite30PercentOpacity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val locationPermissionDeniedFlow = MutableStateFlow(false)

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1231
    }

    private val viewModel by viewModels<WeatherViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        setContent {
            var locationPermissionDenied by remember {
                mutableStateOf(false)
            }
            LaunchedEffect(key1 = "1", block = {
                locationPermissionDeniedFlow.collect{
                    locationPermissionDenied = it
                }
            })
            if(!locationPermissionDenied) {
                MainView(viewModel)
            } else {
                LocationError()
            }

            if (checkLocationPermission()) {
                getLastKnownLocation { lat, lon ->
                    viewModel.getCurrentWeather(lat, lon)
                    viewModel.getWeatherForecast(lat, lon)
                }
            } else {
                RequestLocationPermissionDialog(true, {
                    requestLocationPermission()
                }) {
                    lifecycleScope.launch {
                        locationPermissionDeniedFlow.emit(true)
                    }
                }
            }
        }
    }
    private fun checkLocationPermission(): Boolean {
        val fineLocationPermission =
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        val coarseLocationPermission =
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)

        return fineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                coarseLocationPermission == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    private fun getLastKnownLocation(onLocationResult:(Double,Double)->Unit) {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            lifecycleScope.launch {
                locationPermissionDeniedFlow.emit(true)
            }
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    onLocationResult(location.latitude, location.longitude)
                }
            }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastKnownLocation{ lat, lon ->
                    viewModel.getCurrentWeather(lat, lon)
                    viewModel.getWeatherForecast(lat, lon)
                }
            } else {
                lifecycleScope.launch {
                    locationPermissionDeniedFlow.emit(true)
                }
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            locationPermissionDeniedFlow.emit(!checkLocationPermission())
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainView(viewModel: WeatherViewModel) {
    Scaffold { paddingValues ->
        Column(modifier = Modifier
            .padding(paddingValues)
            .fillMaxHeight()
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        OpenWeatherStrongBlue,
                        OpenWeatherLightBlue
                    )
                )
            )
            .verticalScroll(rememberScrollState(), true, reverseScrolling = false)
        ) {
            when(viewModel.currentWeather.value.status) {
                Resource.Status.SUCCESS -> {
                    viewModel.currentWeather.value.data?.let { data ->
                        CurrentWeatherView(currentWeather = data)
                        viewModel.mapCoord.value = data.coord
                        viewModel.mapIsReady.value = true
                    }
                }
                Resource.Status.LOADING -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(CenterHorizontally)
                            .padding(
                                top = dimensionResource(id = R.dimen.space_xx_large),
                                bottom = dimensionResource(id = R.dimen.space_xx_large)
                            )
                    )
                }
                Resource.Status.ERROR -> {
                    FailedProcessErrorView(
                        Icons.Default.Close,
                        stringResource(R.string.something_went_wrong),
                        stringResource(R.string.check_internet_error)
                    )
                }
            }

            viewModel.weatherForecast.value.let { data ->
                when (data.status) {
                    Resource.Status.SUCCESS, Resource.Status.LOADING -> {
                        WeatherForecastView(data)
                    }
                    Resource.Status.ERROR -> {
                    }
                }
            }

            if(viewModel.mapIsReady.value){
                MapContainer(viewModel)
            }
        }
    }
}

@Composable
fun FailedProcessErrorView(icon: ImageVector = Icons.Default.Close, title: String, errorMessage: String) {
    Surface(
        modifier = Modifier
            .padding(dimensionResource(id = R.dimen.space_normal))
            .fillMaxWidth(),
        shape = RoundedCornerShape(corner = CornerSize(dimensionResource(id = R.dimen.space_normal)))
    ) {
        Column(
            modifier = Modifier.background(OpenWeatherErrorColor),
            horizontalAlignment = CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                imageVector = icon,
                contentDescription = stringResource(R.string.error_icon),
                modifier = Modifier.padding(
                    dimensionResource(id = R.dimen.space_normal)
                )
            )
            Text(
                text = title,
                modifier = Modifier.padding(
                    top = dimensionResource(id = R.dimen.space_small),
                    end = dimensionResource(id = R.dimen.space_small),
                    start = dimensionResource(id = R.dimen.space_small)
                ),
                fontSize = 14.sp,
                style = TextStyle(fontFamily = FontFamily(Font(R.font.source_sans_pro_semibold))),
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Text(
                text = errorMessage,
                modifier = Modifier.padding(
                    top = dimensionResource(id = R.dimen.space_small),
                    end = dimensionResource(id = R.dimen.space_normal),
                    start = dimensionResource(id = R.dimen.space_normal),
                    bottom = dimensionResource(id = R.dimen.space_normal)
                ),
                fontSize = 14.sp,
                style = TextStyle(fontFamily = FontFamily(Font(R.font.source_sans_pro_regular))),
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun CurrentWeatherView(currentWeather: CurrentWeather) {
    Column( modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = CenterHorizontally,

    ) {
        Text(
            text = currentWeather.name,
            modifier = Modifier.padding(
                top = dimensionResource(id = R.dimen.space_xx_large),
                end = dimensionResource(id = R.dimen.space_normal),
                start = dimensionResource(id = R.dimen.space_normal)),
            fontSize = 36.sp,
            style = TextStyle(fontFamily = FontFamily(Font(R.font.source_sans_pro_regular))),
            color = OpenWeatherWhite,
            textAlign = TextAlign.Center
        )
        AsyncImage(
            model = "https://openweathermap.org/img/wn/${currentWeather.weather[0].icon}.png",
            contentDescription = stringResource(R.string.current_weather_icon),
            modifier = Modifier.size(dimensionResource(id = R.dimen.large_icon))
        )
        Text(
            text = "${currentWeather.main.temp}°",
            modifier = Modifier.padding(
                top = dimensionResource(id = R.dimen.space_normal),
                end = dimensionResource(id = R.dimen.space_normal),
                start = dimensionResource(id = R.dimen.space_normal)),
            fontSize = 72.sp,
            style = TextStyle(fontFamily = FontFamily(Font(R.font.source_sans_pro_light))),
            color = OpenWeatherWhite,
            textAlign = TextAlign.Center
        )
        Text(
            text = "${currentWeather.weather[0].description} ${currentWeather.main.temp_max}°/${currentWeather.main.temp_min}°",
            modifier = Modifier.padding(
                top = dimensionResource(id = R.dimen.space_small),
                end = dimensionResource(id = R.dimen.space_normal),
                start = dimensionResource(id = R.dimen.space_normal),
                bottom = dimensionResource(id = R.dimen.space_large)),
            fontSize = 16.sp,
            style = TextStyle(fontFamily = FontFamily(Font(R.font.source_sans_pro_regular))),
            color = OpenWeatherWhite,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun WeatherForecastView(forecast: Resource<WeatherForecast>) {
    Surface(
        modifier = Modifier
            .padding(
                start = dimensionResource(id = R.dimen.space_normal),
                end = dimensionResource(id = R.dimen.space_normal)
            )
            .fillMaxWidth(),
        shape = RoundedCornerShape(corner = CornerSize(dimensionResource(id = R.dimen.space_normal))),
        color = OpenWeatherStrongBlue30PercentOpacity
    ){
        Column(content = {
            Text(
                text = stringResource(R.string.weather_forecast_title),
                modifier = Modifier.padding(
                    top = dimensionResource(id = R.dimen.space_normal),
                    end = dimensionResource(id = R.dimen.space_normal),
                    start = dimensionResource(id = R.dimen.space_normal)
                ),
                fontSize = 14.sp,
                style = TextStyle(fontFamily = FontFamily(Font(R.font.source_sans_pro_semibold))),
                color = OpenWeatherWhite,
                textAlign = TextAlign.Center)
            Divider(
                modifier = Modifier.padding(
                    start = dimensionResource(id = R.dimen.space_normal), 
                    end = dimensionResource(id = R.dimen.space_normal),
                    top = dimensionResource(id = R.dimen.space_normal)
                ),
                thickness = 1.dp,
                color = OpenWeatherWhite30PercentOpacity
            )
            when(forecast.status) {
                Resource.Status.SUCCESS -> {
                    forecast.data?.list?.filter {
                        DateTimeUtils.getHour(it.dt_txt) == "12"
                    }?.forEach { weatherItem ->
                        WeatherItem(weatherItem)
                    }
                }
                Resource.Status.LOADING -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(CenterHorizontally)
                            .padding(
                                top = dimensionResource(id = R.dimen.space_xx_large),
                                bottom = dimensionResource(id = R.dimen.space_xx_large)
                            )
                    )
                }
                Resource.Status.ERROR -> {
                    //Being handled by the upper/calling function
                }
            }
            Spacer(modifier = Modifier
                .height(dimensionResource(id = R.dimen.space_list))
                .background(Color.Red))
        })
    }
}

@Composable
fun MapContainer(viewModel: WeatherViewModel){
    Surface(
        modifier = Modifier
            .padding(
                start = dimensionResource(id = R.dimen.space_normal),
                end = dimensionResource(id = R.dimen.space_normal),
                top = dimensionResource(id = R.dimen.space_normal),
                bottom = dimensionResource(id = R.dimen.space_normal)
            )
            .fillMaxWidth(),
        shape = RoundedCornerShape(corner = CornerSize(dimensionResource(id = R.dimen.space_normal))),
        color = OpenWeatherStrongBlue30PercentOpacity
    ) {
        AndroidView(
            factory = { context ->
                MapView(context).apply {
                    onCreate(Bundle())
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp)
                .clipToBounds()
        ) { mapView ->
            mapView.getMapAsync { googleMap ->
                viewModel.mapCoord.value?.let { coord ->
                    val currentLocation = LatLng(coord.lat, coord.lon)
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 10f))
                    googleMap.addMarker(MarkerOptions().position(currentLocation))
                    googleMap.setOnMapClickListener { clickedLatLng ->
                        viewModel.mapCoord.value = Coord(clickedLatLng.latitude, clickedLatLng.longitude)
                        googleMap.clear()
                        googleMap.addMarker(MarkerOptions().position(clickedLatLng))
                        viewModel.getCurrentWeather(clickedLatLng.latitude, clickedLatLng.longitude)
                        viewModel.getWeatherForecast(clickedLatLng.latitude, clickedLatLng.longitude)
                    }
                }
            }
        }
    }
}

@Composable
fun WeatherItem(weather: list){
    Row(modifier = Modifier.padding(top = dimensionResource(id = R.dimen.space_list)), verticalAlignment = Alignment.CenterVertically) {
        AsyncImage(
            model = "https://openweathermap.org/img/wn/${weather.weather[0].icon}.png",
            contentDescription = stringResource(R.string.weather_icon_desc),
            modifier = Modifier
                .size(dimensionResource(id = R.dimen.small_icon))
                .weight(1f)
        )
        Text(text = DateTimeUtils.getWeekDay(weather.dt_txt),
            modifier = Modifier
                .padding(
                    end = dimensionResource(id = R.dimen.space_normal),
                    start = dimensionResource(id = R.dimen.space_x_small)
                )
                .weight(1f),
            fontSize = 16.sp,
            style = TextStyle(fontFamily = FontFamily(Font(R.font.source_sans_pro_regular))),
            color = OpenWeatherWhite,
            textAlign = TextAlign.Start)
        Text(
            text = "${weather.weather[0].description} ${weather.main.temp_max}°/${weather.main.temp_min}°",
            modifier = Modifier
                .padding(
                    end = dimensionResource(id = R.dimen.space_normal),
                    start = dimensionResource(id = R.dimen.space_normal)
                )
                .weight(3f),
            fontSize = 14.sp,
            style = TextStyle(fontFamily = FontFamily(Font(R.font.source_sans_pro_regular))),
            color = OpenWeatherWhite,
            textAlign = TextAlign.End
        )
    }
}


@Composable
fun RequestLocationPermissionDialog(
    shouldShowDialog: Boolean,
    onPositiveButtonClick: () -> Unit,
    onNegativeButtonClick: () -> Unit
) {
    val shouldKeepShowingDialog = remember {
        mutableStateOf(shouldShowDialog)
    }
    if (shouldKeepShowingDialog.value){
        AlertDialog(
            onDismissRequest = {
            },
            title = {
                Text(
                    text = stringResource(R.string.location_request_title),
                    modifier = Modifier.padding(
                        end = dimensionResource(id = R.dimen.space_dialog_padding),
                        start = dimensionResource(id = R.dimen.space_dialog_padding)
                    ),
                    style = TextStyle(fontFamily = FontFamily(Font(R.font.source_sans_pro_regular))),
                    fontSize = 16.sp,
                )},
            text = { Text(
                text = stringResource(R.string.location_request_sub_text),
                modifier = Modifier.padding(
                    end = dimensionResource(id = R.dimen.space_dialog_padding),
                    start = dimensionResource(id = R.dimen.space_dialog_padding)
                ),
                style = TextStyle(fontFamily = FontFamily(Font(R.font.source_sans_pro_regular))),
                fontSize = 16.sp
            )},
            confirmButton = {
                Text(text = stringResource(R.string.allow),
                    modifier = Modifier
                        .padding(
                            end = dimensionResource(id = R.dimen.space_dialog_padding),
                            start = dimensionResource(id = R.dimen.space_dialog_padding)
                        )
                        .clickable {
                            onPositiveButtonClick()
                            shouldKeepShowingDialog.value = false
                        },
                    style = TextStyle(fontFamily = FontFamily(Font(R.font.source_sans_pro_semibold))),
                    fontSize = 14.sp,
                    color = OpenWeatherDialogButtonColor
                )
            },
            dismissButton = {
                Text(text = stringResource(R.string.deny),
                    modifier = Modifier
                        .padding(
                            end = dimensionResource(id = R.dimen.space_dialog_padding),
                            start = dimensionResource(id = R.dimen.space_dialog_padding)
                        )
                        .clickable {
                            onNegativeButtonClick()
                            shouldKeepShowingDialog.value = false
                        },
                    style = TextStyle(fontFamily = FontFamily(Font(R.font.source_sans_pro_semibold))),
                    fontSize = 14.sp,
                    color = OpenWeatherDialogButtonColor
                )
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationError() {
    val context = LocalContext.current
    Scaffold { paddingValues ->
        Column(modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        OpenWeatherStrongBlue,
                        OpenWeatherLightBlue
                    )
                )
            ), verticalArrangement = Arrangement.Center,
            horizontalAlignment = CenterHorizontally) {
            Image(
                painterResource(R.drawable.logo_white_cropped),
                contentDescription = stringResource(R.string.app_icon),
                modifier = Modifier
                    .width(239.dp)
                    .height(102.dp)
            )

            Text(
                text = stringResource(R.string.weather_app_is_unavailable),
                modifier = Modifier.padding(
                    top = dimensionResource(id = R.dimen.space_normal),
                    end = dimensionResource(id = R.dimen.space_normal),
                    start = dimensionResource(id = R.dimen.space_normal)
                ),
                fontSize = 20.sp,
                style = TextStyle(fontFamily = FontFamily(Font(R.font.source_sans_pro_semibold))),
                color = OpenWeatherWhite,
                textAlign = TextAlign.Center
            )
            Text(
                text = stringResource(R.string.ask_for_location_permission),
                modifier = Modifier.padding(
                    top = dimensionResource(id = R.dimen.space_normal),
                    end = dimensionResource(id = R.dimen.space_normal),
                    start = dimensionResource(id = R.dimen.space_normal)
                ),
                fontSize = 16.sp,
                style = TextStyle(fontFamily = FontFamily(Font(R.font.source_sans_pro_regular))),
                color = OpenWeatherWhite,
                textAlign = TextAlign.Center
            )
            Text(text = stringResource(R.string.go_to_setting),
                modifier = Modifier
                    .padding(
                        end = dimensionResource(id = R.dimen.space_normal),
                        start = dimensionResource(id = R.dimen.space_normal),
                        top = dimensionResource(id = R.dimen.space_normal)
                    )
                    .clickable {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        val uri = Uri.fromParts("package", context.packageName, null)
                        intent.data = uri
                        context.startActivity(intent)
                    },
                style = TextStyle(fontFamily = FontFamily(Font(R.font.source_sans_pro_semibold))),
                fontSize = 14.sp,
                color = OpenWeatherDialogButtonColor
            )
        }
    }
}
