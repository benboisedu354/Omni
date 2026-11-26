package com.example.omni.ui.navigation

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.omni.ui.components.BottomNavBar
import com.example.omni.ui.modules.calendar.CalendarScreen
import com.example.omni.ui.modules.challenges.ChallengesScreen
import com.example.omni.ui.modules.clock.ClockScreen
import com.example.omni.ui.modules.dashboard.DashboardScreen
import com.example.omni.ui.modules.gps.GpsScreen
import com.example.omni.ui.modules.sensors.SensorsScreen
import com.example.omni.ui.modules.splash.SplashScreen
import com.example.omni.ui.modules.todo.TodoScreen
import com.example.omni.ui.modules.weather.WeatherScreen
import com.example.omni.ui.modules.index_page.IndexPageScreen
import com.example.omni.ui.modules.settings.SettingsScreen
import com.example.omni.ui.modules.notifications.NotificationsSettingsScreen

@Composable
fun OmniNavGraph(
    navController: NavHostController = rememberNavController(),
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        modifier = modifier,
        bottomBar = {
            val visibleRoutes = listOf(
                Screen.Dashboard.route,
                Screen.Settings.route,
                Screen.IndexPage.route
            )

            if (currentRoute in visibleRoutes) {
                BottomNavBar(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            launchSingleTop = true
                            restoreState = true

                            if (route == Screen.IndexPage.route) {
                                popUpTo(Screen.IndexPage.route) { inclusive = false }
                            } else {
                                popUpTo(Screen.IndexPage.route) { saveState = true }
                            }
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Splash.route) {
                SplashScreen(
                    onSplashFinished = {
                        navController.navigate(Screen.IndexPage.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    }
                )
            }

            composable ( Screen.IndexPage.route ) {
                IndexPageScreen(
                    onNavigate = { route ->
                        navController.navigate(route)
                    },

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

            composable(Screen.NotificationsSettings.route) {
                NotificationsSettingsScreen(onBack = { navController.popBackStack() })
            }

            composable(Screen.Settings.route) {
                SettingsScreen(
                    onBack = { navController.popBackStack() },
                )
            }
        }
    }
}