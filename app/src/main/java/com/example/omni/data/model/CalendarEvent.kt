package com.example.omni.data.model

import java.util.UUID

data class CalendarEvent(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String = "",
    val date: String, // Format YYYY-MM-DD
    val startTime: String = "", // Format HH:mm
    val endTime: String = "",
    val category: String = "Général",
    val colorHex: Long = 0xFFEC4899
)
