package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.DashboardScreen
import com.example.ui.OnboardingScreen
import com.example.ui.theme.FootballWorldTheme
import com.example.viewmodel.FootballViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: FootballViewModel = viewModel()
            FootballWorldTheme(darkTheme = viewModel.isDarkTheme) {
                if (viewModel.showLanguageOverlay) {
                    OnboardingScreen(viewModel = viewModel)
                } else {
                    DashboardScreen(viewModel = viewModel)
                }
            }
        }
    }
}
