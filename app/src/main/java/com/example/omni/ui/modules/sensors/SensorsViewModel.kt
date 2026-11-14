package com.example.omni.ui.modules.sensors

import android.app.Application
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.sqrt

data class SensorState(
    val accelX: Float = 0f,
    val accelY: Float = 0f,
    val accelZ: Float = 9.8f,
    val totalG: Float = 1.0f,
    val gyroX: Float = 0f,
    val gyroY: Float = 0f,
    val gyroZ: Float = 0f,
    val lightLux: Float = 120f,
    val magneticMicroTesla: Float = 45f,
    val pressureHpa: Float = 1013.25f,
    val hasPressure: Boolean = false,
    val steps: Int = 0
)

class SensorsViewModel(application: Application) : AndroidViewModel(application), SensorEventListener {
    private val sensorManager = application.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    private val _state = MutableStateFlow(SensorState())
    val state: StateFlow<SensorState> = _state.asStateFlow()

    private var lastStepAccel = 0f

    init {
        registerSensors()
    }

    private fun registerSensors() {
        val accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val gyro = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        val light = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
        val mag = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        val press = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE)

        accel?.let { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI) }
        gyro?.let { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI) }
        light?.let { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI) }
        mag?.let { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI) }
        press?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
            _state.value = _state.value.copy(hasPressure = true)
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return

        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]
                val mag = sqrt((x * x + y * y + z * z).toDouble()).toFloat()
                val g = mag / 9.80665f

                // Simple step detector via accelerometer peak
                var currentSteps = _state.value.steps
                if (mag - lastStepAccel > 2.5f) {
                    currentSteps += 1
                }
                lastStepAccel = mag

                _state.value = _state.value.copy(
                    accelX = x,
                    accelY = y,
                    accelZ = z,
                    totalG = g,
                    steps = currentSteps
                )
            }
            Sensor.TYPE_GYROSCOPE -> {
                _state.value = _state.value.copy(
                    gyroX = event.values[0],
                    gyroY = event.values[1],
                    gyroZ = event.values[2]
                )
            }
            Sensor.TYPE_LIGHT -> {
                _state.value = _state.value.copy(lightLux = event.values[0])
            }
            Sensor.TYPE_MAGNETIC_FIELD -> {
                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]
                val strength = sqrt((x * x + y * y + z * z).toDouble()).toFloat()
                _state.value = _state.value.copy(magneticMicroTesla = strength)
            }
            Sensor.TYPE_PRESSURE -> {
                _state.value = _state.value.copy(
                    pressureHpa = event.values[0],
                    hasPressure = true
                )
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    fun resetSteps() {
        _state.value = _state.value.copy(steps = 0)
    }

    override fun onCleared() {
        super.onCleared()
        sensorManager.unregisterListener(this)
    }
}
