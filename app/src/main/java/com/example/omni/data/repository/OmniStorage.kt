package com.example.omni.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.omni.data.model.CalendarEvent
import com.example.omni.data.model.Challenge
import com.example.omni.data.model.ChallengeType
import com.example.omni.data.model.OmniSettings
import com.example.omni.data.model.Todo
import com.example.omni.data.model.TodoLabel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class OmniStorage(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("omni_local_storage", Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        private const val KEY_TODOS = "todos"
        private const val KEY_LABELS = "todo_labels"
        private const val KEY_CHALLENGES = "challenges"
        private const val KEY_EVENTS = "calendar_events"
        private const val KEY_SETTINGS = "settings"
        private const val KEY_WORLD_CITIES = "world_cities"
    }

    // --- LABELS ---
    fun getLabels(): List<TodoLabel> {
        val json = prefs.getString(KEY_LABELS, null)
        return if (json != null) {
            val type = object : TypeToken<List<TodoLabel>>() {}.type
            gson.fromJson(json, type) ?: defaultLabels()
        } else {
            val defaults = defaultLabels()
            saveLabels(defaults)
            defaults
        }
    }

    fun saveLabels(labels: List<TodoLabel>) {
        prefs.edit().putString(KEY_LABELS, gson.toJson(labels)).apply()
    }

    private fun defaultLabels(): List<TodoLabel> {
        return listOf(
            TodoLabel(id = "travail", name = "Travail", colorHex = 0xFF2563EB),
            TodoLabel(id = "perso", name = "Perso", colorHex = 0xFF10B981),
            TodoLabel(id = "courses", name = "Courses", colorHex = 0xFFF97316),
            TodoLabel(id = "sante", name = "Santé", colorHex = 0xFFEF4444)
        )
    }

    // --- TODOS ---
    fun getTodos(): List<Todo> {
        val json = prefs.getString(KEY_TODOS, null) ?: return emptyList()
        val type = object : TypeToken<List<Todo>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }

    fun saveTodos(todos: List<Todo>) {
        prefs.edit().putString(KEY_TODOS, gson.toJson(todos)).apply()
    }

    // --- CHALLENGES ---
    fun getChallenges(): List<Challenge> {
        val json = prefs.getString(KEY_CHALLENGES, null)
        return if (json != null) {
            val type = object : TypeToken<List<Challenge>>() {}.type
            gson.fromJson(json, type) ?: defaultChallenges()
        } else {
            val defaults = defaultChallenges()
            saveChallenges(defaults)
            defaults
        }
    }

    fun saveChallenges(challenges: List<Challenge>) {
        prefs.edit().putString(KEY_CHALLENGES, gson.toJson(challenges)).apply()
    }

    private fun defaultChallenges(): List<Challenge> {
        return listOf(
            Challenge(
                id = "water",
                title = "Hydratation quotidienne",
                description = "Boire au moins 2L d'eau par jour",
                type = ChallengeType.DAILY_COUNTER,
                targetValue = 8,
                unit = "verres (250ml)",
                currentDailyProgress = 0,
                streakDays = 0
            ),
            Challenge(
                id = "reading",
                title = "30 min de lecture",
                description = "Lire un livre inspirant chaque jour",
                type = ChallengeType.DAILY_COUNTER,
                targetValue = 30,
                unit = "minutes",
                currentDailyProgress = 0,
                streakDays = 0
            ),
            Challenge(
                id = "noscreen24h",
                title = "Chronomètre 24h sans réseaux",
                description = "Épreuve continue de 24h de déconnexion",
                type = ChallengeType.CONTINUOUS_TIMER,
                targetValue = 86400,
                unit = "secondes (24h)",
                currentDailyProgress = 0,
                streakDays = 0
            )
        )
    }

    // --- CALENDAR EVENTS ---
    fun getEvents(): List<CalendarEvent> {
        val json = prefs.getString(KEY_EVENTS, null) ?: return emptyList()
        val type = object : TypeToken<List<CalendarEvent>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }

    fun saveEvents(events: List<CalendarEvent>) {
        prefs.edit().putString(KEY_EVENTS, gson.toJson(events)).apply()
    }

    // --- SETTINGS ---
    fun getSettings(): OmniSettings {
        val json = prefs.getString(KEY_SETTINGS, null) ?: return OmniSettings()
        return gson.fromJson(json, OmniSettings::class.java) ?: OmniSettings()
    }

    fun saveSettings(settings: OmniSettings) {
        prefs.edit().putString(KEY_SETTINGS, gson.toJson(settings)).apply()
    }

    // --- WORLD CLOCK CITIES ---
    fun getWorldCities(): List<String> {
        val json = prefs.getString(KEY_WORLD_CITIES, null)
        return if (json != null) {
            val type = object : TypeToken<List<String>>() {}.type
            gson.fromJson(json, type) ?: defaultCities()
        } else {
            val defaults = defaultCities()
            saveWorldCities(defaults)
            defaults
        }
    }

    fun saveWorldCities(cities: List<String>) {
        prefs.edit().putString(KEY_WORLD_CITIES, gson.toJson(cities)).apply()
    }

    private fun defaultCities(): List<String> {
        return listOf("Europe/Paris", "America/New_York", "Asia/Tokyo", "Europe/London", "Australia/Sydney")
    }
}
