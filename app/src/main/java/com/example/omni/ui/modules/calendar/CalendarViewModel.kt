package com.example.omni.ui.modules.calendar

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.omni.data.model.CalendarEvent
import com.example.omni.data.repository.OmniStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID

class CalendarViewModel(application: Application) : AndroidViewModel(application) {
    private val storage = OmniStorage(application)

    private val _events = MutableStateFlow<List<CalendarEvent>>(emptyList())
    val events: StateFlow<List<CalendarEvent>> = _events.asStateFlow()

    private val _selectedDate = MutableStateFlow(getTodayDateStr())
    val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()

    private val _displayedMonthCal = MutableStateFlow(Calendar.getInstance())
    val displayedMonthCal: StateFlow<Calendar> = _displayedMonthCal.asStateFlow()

    init {
        loadEvents()
    }

    private fun getTodayDateStr(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    private fun loadEvents() {
        val loaded = storage.getEvents()
        if (loaded.isEmpty()) {
            val starter = listOf(
                CalendarEvent(
                    id = UUID.randomUUID().toString(),
                    title = "Réunion de cadrage Omni",
                    description = "Présentation des fonctionnalités et de l'architecture locale",
                    date = getTodayDateStr(),
                    startTime = "10:00",
                    endTime = "11:00",
                    category = "Travail",
                    colorHex = 0xFF2563EB
                ),
                CalendarEvent(
                    id = UUID.randomUUID().toString(),
                    title = "Séance de sport & étirements",
                    description = "Session cardio et renforcement",
                    date = getTodayDateStr(),
                    startTime = "18:30",
                    endTime = "19:30",
                    category = "Personnel",
                    colorHex = 0xFF10B981
                )
            )
            _events.value = starter
            storage.saveEvents(starter)
        } else {
            _events.value = loaded
        }
    }

    fun setSelectedDate(dateStr: String) {
        _selectedDate.value = dateStr
    }

    fun prevMonth() {
        val c = (_displayedMonthCal.value.clone() as Calendar).apply {
            add(Calendar.MONTH, -1)
        }
        _displayedMonthCal.value = c
    }

    fun nextMonth() {
        val c = (_displayedMonthCal.value.clone() as Calendar).apply {
            add(Calendar.MONTH, 1)
        }
        _displayedMonthCal.value = c
    }

    fun addEvent(
        title: String,
        description: String,
        date: String,
        startTime: String,
        endTime: String,
        category: String,
        colorHex: Long
    ) {
        val newEvent = CalendarEvent(
            id = UUID.randomUUID().toString(),
            title = title,
            description = description,
            date = date,
            startTime = startTime,
            endTime = endTime,
            category = category,
            colorHex = colorHex
        )
        val updated = _events.value + newEvent
        _events.value = updated
        storage.saveEvents(updated)
    }

    fun deleteEvent(eventId: String) {
        val updated = _events.value.filter { it.id != eventId }
        _events.value = updated
        storage.saveEvents(updated)
    }
}
