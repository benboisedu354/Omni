package com.example.omni.ui.modules.settings

import androidx.compose.runtime.Composable
import com.example.omni.ui.modules.notifications.NotificationsSettingsScreen

@Composable
fun SettingsScreen(
    onBack: () -> Unit
) {
    NotificationsSettingsScreen(onBack = onBack)
}
