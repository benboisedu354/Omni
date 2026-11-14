package com.example.omni.data.model

data class WeatherInfo(
    val cityName: String,
    val temperature: Double,
    val apparentTemperature: Double,
    val humidity: Int,
    val windSpeed: Double,
    val weatherCode: Int,
    val weatherDescription: String,
    val dailyForecasts: List<DailyWeather>
)

data class DailyWeather(
    val date: String,
    val maxTemp: Double,
    val minTemp: Double,
    val weatherCode: Int,
    val description: String
)

object WeatherCodeHelper {
    fun getDescription(code: Int): String {
        return when (code) {
            0 -> "Ciel dégagé"
            1, 2, 3 -> "Partiellement nuageux"
            45, 48 -> "Brouillard"
            51, 53, 55 -> "Bruine"
            61, 63, 65 -> "Pluie"
            71, 73, 75 -> "Chute de neige"
            77 -> "Grains de neige"
            80, 81, 82 -> "Averses de pluie"
            85, 86 -> "Averses de neige"
            95 -> "Orage"
            96, 99 -> "Orage avec grêle"
            else -> "Nuageux"
        }
    }

    fun getIconEmoji(code: Int): String {
        return when (code) {
            0 -> "☀️"
            1, 2, 3 -> "⛅"
            45, 48 -> "🌫️"
            51, 53, 55 -> "🌦️"
            61, 63, 65 -> "🌧️"
            71, 73, 75, 77 -> "🌨️"
            80, 81, 82 -> "🌧️"
            85, 86 -> "❄️"
            95, 96, 99 -> "⛈️"
            else -> "☁️"
        }
    }
}
