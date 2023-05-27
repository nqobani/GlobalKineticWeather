package com.example.globalkineticweather.ui.view_layer

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.globalkineticweather.R
import com.example.globalkineticweather.ui.theme.OpenWeatherLightBlue
import com.example.globalkineticweather.ui.theme.OpenWeatherStrongBlue
import java.util.Timer
import java.util.TimerTask


class SplashScreenActivity : ComponentActivity() {
    private val timer = Timer()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainView()
        }

        timer.schedule(object : TimerTask() {
            override fun run() {
                val intent = Intent(this@SplashScreenActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }, 2000)
    }

    override fun onDestroy() {
        super.onDestroy()
        timer.cancel()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainView() {
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
            horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painterResource(R.drawable.logo_white_cropped),
                contentDescription = "App Icon",
                modifier = Modifier
                    .width(239.dp)
                    .height(102.dp)
            )
        }
    }
}

@Preview()
@Composable
fun GreetingPreview() {
    MainView()
}