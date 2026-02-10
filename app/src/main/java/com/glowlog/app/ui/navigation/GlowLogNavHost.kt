package com.glowlog.app.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.glowlog.app.R
import com.glowlog.app.ui.bloodpressure.add.AddBloodPressureScreen
import com.glowlog.app.ui.bloodpressure.chart.BloodPressureChartScreen
import com.glowlog.app.ui.bloodpressure.list.BloodPressureListScreen
import com.glowlog.app.ui.glucose.add.AddGlucoseScreen
import com.glowlog.app.ui.glucose.chart.GlucoseChartScreen
import com.glowlog.app.ui.glucose.list.GlucoseListScreen
import com.glowlog.app.ui.home.HomeScreen
import com.glowlog.app.ui.settings.SettingsScreen
import com.glowlog.app.ui.auth.SignInScreen

private val bottomNavRoutes = BottomNavItem.entries.map { it.screen.route }.toSet()

@StringRes
private fun screenTitleRes(route: String?): Int = when (route) {
    Screen.Home.route -> R.string.app_name
    Screen.GlucoseList.route -> R.string.nav_glucose
    Screen.AddGlucose.route -> R.string.title_new_entry
    Screen.GlucoseChart.route -> R.string.title_glucose_chart
    Screen.BloodPressureList.route -> R.string.nav_pressure
    Screen.AddBloodPressure.route -> R.string.title_new_entry
    Screen.BloodPressureChart.route -> R.string.title_bp_chart
    Screen.Settings.route -> R.string.title_settings
    Screen.SignIn.route -> R.string.title_sign_in
    else -> {
        when {
            route?.startsWith("edit_glucose") == true -> R.string.title_editing
            route?.startsWith("edit_blood_pressure") == true -> R.string.title_editing
            else -> R.string.app_name
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlowLogNavHost() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute in bottomNavRoutes
    val showBackArrow = currentRoute != null && currentRoute !in bottomNavRoutes

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(screenTitleRes(currentRoute))) },
                navigationIcon = {
                    if (showBackArrow) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.cd_back))
                        }
                    }
                },
                actions = {
                    if (currentRoute == Screen.GlucoseList.route) {
                        IconButton(onClick = { navController.navigate(Screen.GlucoseChart.route) }) {
                            Icon(Icons.AutoMirrored.Filled.ShowChart, contentDescription = stringResource(R.string.cd_chart))
                        }
                    }
                    if (currentRoute == Screen.BloodPressureList.route) {
                        IconButton(onClick = { navController.navigate(Screen.BloodPressureChart.route) }) {
                            Icon(Icons.AutoMirrored.Filled.ShowChart, contentDescription = stringResource(R.string.cd_chart))
                        }
                    }
                    if (currentRoute != Screen.Settings.route && currentRoute != Screen.SignIn.route) {
                        IconButton(onClick = {
                            navController.navigate(Screen.Settings.route)
                        }) {
                            Icon(Icons.Default.Settings, contentDescription = stringResource(R.string.cd_settings))
                        }
                    }
                }
            )
        },
        bottomBar = {
            if (showBottomBar) {
                BottomNavBar(navController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    onNavigateToAddGlucose = { navController.navigate(Screen.AddGlucose.route) },
                    onNavigateToAddBloodPressure = { navController.navigate(Screen.AddBloodPressure.route) },
                    onNavigateToGlucoseList = {
                        navController.navigate(Screen.GlucoseList.route) {
                            popUpTo(Screen.Home.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onNavigateToBloodPressureList = {
                        navController.navigate(Screen.BloodPressureList.route) {
                            popUpTo(Screen.Home.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }

            composable(Screen.GlucoseList.route) {
                GlucoseListScreen(
                    onNavigateToAdd = { navController.navigate(Screen.AddGlucose.route) },
                    onNavigateToEdit = { id -> navController.navigate(Screen.EditGlucose.createRoute(id)) }
                )
            }

            composable(Screen.AddGlucose.route) {
                AddGlucoseScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Screen.EditGlucose.route,
                arguments = listOf(navArgument("readingId") { type = NavType.StringType })
            ) {
                AddGlucoseScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.GlucoseChart.route) {
                GlucoseChartScreen()
            }

            composable(Screen.BloodPressureList.route) {
                BloodPressureListScreen(
                    onNavigateToAdd = { navController.navigate(Screen.AddBloodPressure.route) },
                    onNavigateToEdit = { id -> navController.navigate(Screen.EditBloodPressure.createRoute(id)) }
                )
            }

            composable(Screen.AddBloodPressure.route) {
                AddBloodPressureScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Screen.EditBloodPressure.route,
                arguments = listOf(navArgument("readingId") { type = NavType.StringType })
            ) {
                AddBloodPressureScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.BloodPressureChart.route) {
                BloodPressureChartScreen()
            }

            composable(Screen.Settings.route) {
                SettingsScreen(
                    onNavigateToSignIn = { navController.navigate(Screen.SignIn.route) },
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.SignIn.route) {
                SignInScreen(
                    onSignInComplete = { navController.popBackStack() }
                )
            }
        }
    }
}
