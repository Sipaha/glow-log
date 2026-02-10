package com.glowlog.app.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.ui.graphics.vector.ImageVector
import com.glowlog.app.R

sealed class Screen(val route: String) {
    data object Home : Screen("home")

    data object GlucoseList : Screen("glucose_list")
    data object AddGlucose : Screen("add_glucose")
    data object EditGlucose : Screen("edit_glucose/{readingId}") {
        fun createRoute(readingId: String) = "edit_glucose/$readingId"
    }
    data object GlucoseChart : Screen("glucose_chart")

    data object BloodPressureList : Screen("blood_pressure_list")
    data object AddBloodPressure : Screen("add_blood_pressure")
    data object EditBloodPressure : Screen("edit_blood_pressure/{readingId}") {
        fun createRoute(readingId: String) = "edit_blood_pressure/$readingId"
    }
    data object BloodPressureChart : Screen("blood_pressure_chart")

    data object Settings : Screen("settings")
    data object SignIn : Screen("sign_in")
}

enum class BottomNavItem(
    val screen: Screen,
    val icon: ImageVector,
    @get:StringRes val labelRes: Int
) {
    HOME(Screen.Home, Icons.Default.Home, R.string.nav_home),
    GLUCOSE(Screen.GlucoseList, Icons.Default.WaterDrop, R.string.nav_glucose),
    BLOOD_PRESSURE(Screen.BloodPressureList, Icons.Default.Favorite, R.string.nav_pressure)
}
