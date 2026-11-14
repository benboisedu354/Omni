package com.example.omni.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.omni.ui.modules.calendar.CalendarScreen
import com.example.omni.ui.modules.challenges.ChallengesScreen
import com.example.omni.ui.modules.clock.ClockScreen
import com.example.omni.ui.modules.dashboard.DashboardScreen
import com.example.omni.ui.modules.gps.GpsScreen
import com.example.omni.ui.modules.sensors.SensorsScreen
import com.example.omni.ui.modules.settings.SettingsScreen
import com.example.omni.ui.modules.splash.SplashScreen
import com.example.omni.ui.modules.todo.TodoScreen
import com.example.omni.ui.modules.weather.WeatherScreen

@Composable
fun OmniNavGraph(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onSplashFinished = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onNavigate = { route ->
                    navController.navigate(route)
                }
            )
        }

        composable(Screen.Todo.route) {
            TodoScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.Challenges.route) {
            ChallengesScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.Clock.route) {
            ClockScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.Weather.route) {
            WeatherScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.Gps.route) {
            GpsScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.Sensors.route) {
            SensorsScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.Calendar.route) {
            CalendarScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.Settings.route) {
            SettingsScreen(onBack = { navController.popBackStack() })
        }
    }
}
