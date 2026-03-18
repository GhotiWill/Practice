package com.example.bardakovexam

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.bardakovexam.presentation.navigation.navHost
import com.example.bardakovexam.ui.theme.BardakovExamTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BardakovExamTheme {
                Surface (modifier = Modifier.fillMaxSize()) {
                    navHost()
                }
            }
        }
    }
}