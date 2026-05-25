package com.example.kioskproject

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.kioskproject.navigation.AppNavGraph
import com.example.kioskproject.ui.theme.KioskprojectTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KioskprojectTheme {
                AppNavGraph()
            }
        }
    }
}