package com.example.omni.ui.modules.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.omni.data.model.WeatherInfo
import com.example.omni.data.repository.WeatherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CityLocation(
    val name: String,
    val lat: Double,
    val lon: Double
)

class WeatherViewModel : ViewModel() {
    private val repository = WeatherRepository()

    val presetCities = listOf(
        CityLocation("Paris", 48.8566, 2.3522),
        CityLocation("Lyon", 45.7640, 4.8357),
        CityLocation("Marseille", 43.2965, 5.3698),
        CityLocation("Bordeaux", 44.8378, -0.5792),
        CityLocation("Toulouse", 43.6047, 1.4442),
        CityLocation("Nantes", 47.2184, -1.5536),
        CityLocation("Strasbourg", 48.5734, 7.7521),
        CityLocation("Bruxelles", 50.8503, 4.3517),
        CityLocation("Genève", 46.2044, 6.1432),
        CityLocation("Montréal", 45.5017, -73.5673)
    )

    private val _selectedCity = MutableStateFlow(presetCities[0])
    val selectedCity: StateFlow<CityLocation> = _selectedCity.asStateFlow()

    private val _weatherState = MutableStateFlow<WeatherInfo?>(null)
    val weatherState: StateFlow<WeatherInfo?> = _weatherState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadWeather(presetCities[0])
    }

    fun selectCity(city: CityLocation) {
        _selectedCity.value = city
        loadWeather(city)
    }

    fun refresh() {
        loadWeather(_selectedCity.value)
    }

    private fun loadWeather(city: CityLocation) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.fetchWeather(city.lat, city.lon, city.name)
            result.onSuccess {
                _weatherState.value = it
            }
            _isLoading.value = false
        }
    }
}
