package com.example.omni.data.model

data class OmniSettings(
    val taskReminderHour: Int = 6,
    val taskReminderMinute: Int = 0,
    val challengeReminderHour: Int = 8,
    val challengeReminderMinute: Int = 0,
    val notificationsEnabled: Boolean = false
)
