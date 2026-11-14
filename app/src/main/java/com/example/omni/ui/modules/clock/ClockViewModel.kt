package com.example.omni.ui.modules.clock

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.omni.data.repository.OmniStorage
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.TimeZone

data class WorldCity(
    val timeZoneId: String,
    val name: String
)

data class LapRecord(
    val lapNumber: Int,
    val lapTimeMs: Long,
    val totalTimeMs: Long
)

class ClockViewModel(application: Application) : AndroidViewModel(application) {
    private val storage = OmniStorage(application)

    // Current Time Flow (ticks every second)
    private val _currentTimeMs = MutableStateFlow(System.currentTimeMillis())
    val currentTimeMs: StateFlow<Long> = _currentTimeMs.asStateFlow()

    // World Cities
    private val _cities = MutableStateFlow<List<String>>(emptyList())
    val cities: StateFlow<List<String>> = _cities.asStateFlow()

    // Stopwatch State
    private val _stopwatchRunning = MutableStateFlow(false)
    val stopwatchRunning: StateFlow<Boolean> = _stopwatchRunning.asStateFlow()

    private val _stopwatchTimeMs = MutableStateFlow(0L)
    val stopwatchTimeMs: StateFlow<Long> = _stopwatchTimeMs.asStateFlow()

    private val _laps = MutableStateFlow<List<LapRecord>>(emptyList())
    val laps: StateFlow<List<LapRecord>> = _laps.asStateFlow()

    private var stopwatchStartTimestamp = 0L
    private var stopwatchAccumulated = 0L

    // Timer State
    private val _timerTotalSeconds = MutableStateFlow(300) // Default 5 mins
    val timerTotalSeconds: StateFlow<Int> = _timerTotalSeconds.asStateFlow()

    private val _timerRemainingSeconds = MutableStateFlow(300)
    val timerRemainingSeconds: StateFlow<Int> = _timerRemainingSeconds.asStateFlow()

    private val _timerRunning = MutableStateFlow(false)
    val timerRunning: StateFlow<Boolean> = _timerRunning.asStateFlow()

    init {
        _cities.value = storage.getWorldCities()

        // Clock 1-second ticker
        viewModelScope.launch {
            while (isActive) {
                delay(1000)
                _currentTimeMs.value = System.currentTimeMillis()
            }
        }

        // Stopwatch fast ticker (every 30ms)
        viewModelScope.launch {
            while (isActive) {
                delay(30)
                if (_stopwatchRunning.value) {
                    val current = System.currentTimeMillis()
                    _stopwatchTimeMs.value = stopwatchAccumulated + (current - stopwatchStartTimestamp)
                }
            }
        }

        // Timer 1-second ticker
        viewModelScope.launch {
            while (isActive) {
                delay(1000)
                if (_timerRunning.value) {
                    if (_timerRemainingSeconds.value > 0) {
                        _timerRemainingSeconds.value -= 1
                        if (_timerRemainingSeconds.value == 0) {
                            _timerRunning.value = false
                            triggerVibration()
                        }
                    }
                }
            }
        }
    }

    // World Cities Methods
    fun addCity(timeZoneId: String) {
        if (!_cities.value.contains(timeZoneId)) {
            val updated = _cities.value + timeZoneId
            _cities.value = updated
            storage.saveWorldCities(updated)
        }
    }

    fun removeCity(timeZoneId: String) {
        val updated = _cities.value.filter { it != timeZoneId }
        _cities.value = updated
        storage.saveWorldCities(updated)
    }

    // Stopwatch Methods
    fun startStopwatch() {
        if (!_stopwatchRunning.value) {
            stopwatchStartTimestamp = System.currentTimeMillis()
            _stopwatchRunning.value = true
        }
    }

    fun pauseStopwatch() {
        if (_stopwatchRunning.value) {
            stopwatchAccumulated += System.currentTimeMillis() - stopwatchStartTimestamp
            _stopwatchTimeMs.value = stopwatchAccumulated
            _stopwatchRunning.value = false
        }
    }

    fun resetStopwatch() {
        _stopwatchRunning.value = false
        stopwatchStartTimestamp = 0L
        stopwatchAccumulated = 0L
        _stopwatchTimeMs.value = 0L
        _laps.value = emptyList()
    }

    fun recordLap() {
        if (_stopwatchTimeMs.value > 0) {
            val total = _stopwatchTimeMs.value
            val lastTotal = _laps.value.firstOrNull()?.totalTimeMs ?: 0L
            val lapTime = total - lastTotal
            val record = LapRecord(
                lapNumber = _laps.value.size + 1,
                lapTimeMs = lapTime,
                totalTimeMs = total
            )
            _laps.value = listOf(record) + _laps.value
        }
    }

    // Timer Methods
    fun setTimerSeconds(seconds: Int) {
        if (!_timerRunning.value) {
            val safe = seconds.coerceAtLeast(1)
            _timerTotalSeconds.value = safe
            _timerRemainingSeconds.value = safe
        }
    }

    fun addTimerMinutes(minutes: Int) {
        if (!_timerRunning.value) {
            val safe = (_timerTotalSeconds.value + minutes * 60).coerceAtLeast(60)
            _timerTotalSeconds.value = safe
            _timerRemainingSeconds.value = safe
        }
    }

    fun startTimer() {
        if (_timerRemainingSeconds.value > 0) {
            _timerRunning.value = true
        }
    }

    fun pauseTimer() {
        _timerRunning.value = false
    }

    fun resetTimer() {
        _timerRunning.value = false
        _timerRemainingSeconds.value = _timerTotalSeconds.value
    }

    private fun triggerVibration() {
        val context = getApplication<Application>()
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                vm.defaultVibrator.vibrate(
                    VibrationEffect.createWaveform(longArrayOf(0, 400, 200, 400), -1)
                )
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(
                        VibrationEffect.createWaveform(longArrayOf(0, 400, 200, 400), -1)
                    )
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(longArrayOf(0, 400, 200, 400), -1)
                }
            }
        } catch (_: Exception) {}
    }
}
