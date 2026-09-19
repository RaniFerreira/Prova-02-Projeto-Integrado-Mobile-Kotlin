package com.example.apptreino

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.apptreino.ui.navigation.AppNavHost
import com.example.apptreino.ui.theme.AppTreinoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTreinoTheme {
                AppNavHost()
            }
        }
    }
}
