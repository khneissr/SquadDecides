package com.example.squaddecides

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.squaddecides.navigation.AppNavigation
import com.example.squaddecides.ui.theme.SquadDecidesTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SquadDecidesTheme {
                MyApp()
            }
        }
    }
}

@Composable
fun MyApp() {
    val navController = rememberNavController()
    val systemUiController = rememberSystemUiController()

    SquadDecidesTheme {
        val backgroundColor = MaterialTheme.colorScheme.background

        DisposableEffect(systemUiController, backgroundColor) {
            systemUiController.setSystemBarsColor(
                color = backgroundColor,
                darkIcons = true
            )
            onDispose {}
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            AppNavigation(navController = navController)
        }
    }
}
