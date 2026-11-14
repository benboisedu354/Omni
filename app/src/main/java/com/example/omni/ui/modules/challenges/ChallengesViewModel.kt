package com.example.omni.ui.modules.challenges

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.omni.data.model.Challenge
import com.example.omni.data.model.ChallengeType
import com.example.omni.data.repository.OmniStorage
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class ChallengesViewModel(application: Application) : AndroidViewModel(application) {
    private val storage = OmniStorage(application)

    private val _challenges = MutableStateFlow<List<Challenge>>(emptyList())
    val challenges: StateFlow<List<Challenge>> = _challenges.asStateFlow()

    init {
        loadData()
        startTimerTicker()
    }

    private fun getTodayDateStr(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    private fun loadData() {
        val today = getTodayDateStr()
        val loaded = storage.getChallenges().map { c ->
            // Update elapsed time for running timers
            var updated = c
            if (c.type == ChallengeType.CONTINUOUS_TIMER && c.isTimerRunning) {
                val now = System.currentTimeMillis()
                val additional = ((now - c.timerLastStartedTime) / 1000).coerceAtLeast(0)
                updated = c.copy(
                    timerElapsedSeconds = c.timerElapsedSeconds + additional,
                    timerLastStartedTime = now
                )
            }
            // Check daily reset for counter if date changed
            if (c.type == ChallengeType.DAILY_COUNTER && c.lastActiveDate.isNotEmpty() && c.lastActiveDate != today) {
                // Keep completed history and streak
                updated = updated.copy(
                    currentDailyProgress = 0,
                    lastActiveDate = today
                )
            }
            updated
        }
        _challenges.value = loaded
        storage.saveChallenges(loaded)
    }

    private fun startTimerTicker() {
        viewModelScope.launch {
            while (isActive) {
                delay(1000)
                val hasRunning = _challenges.value.any { it.isTimerRunning }
                if (hasRunning) {
                    val now = System.currentTimeMillis()
                    val updated = _challenges.value.map { c ->
                        if (c.isTimerRunning) {
                            val newElapsed = c.timerElapsedSeconds + 1
                            val isCompleted = newElapsed >= c.targetValue
                            c.copy(
                                timerElapsedSeconds = newElapsed,
                                timerLastStartedTime = now,
                                currentDailyProgress = newElapsed.toInt(),
                                isTimerRunning = !isCompleted
                            )
                        } else c
                    }
                    _challenges.value = updated
                    storage.saveChallenges(updated)
                }
            }
        }
    }

    fun incrementDailyProgress(challengeId: String, amount: Int = 1) {
        val today = getTodayDateStr()
        val updated = _challenges.value.map { c ->
            if (c.id == challengeId) {
                val newProgress = (c.currentDailyProgress + amount).coerceAtLeast(0)
                val wasCompleted = c.currentDailyProgress >= c.targetValue
                val isNowCompleted = newProgress >= c.targetValue

                var newStreak = c.streakDays
                val completedDays = c.completedDays.toMutableList()

                if (!wasCompleted && isNowCompleted) {
                    if (!completedDays.contains(today)) {
                        completedDays.add(today)
                        newStreak += 1
                    }
                } else if (wasCompleted && !isNowCompleted) {
                    completedDays.remove(today)
                    newStreak = (newStreak - 1).coerceAtLeast(0)
                }

                c.copy(
                    currentDailyProgress = newProgress,
                    lastActiveDate = today,
                    streakDays = newStreak,
                    completedDays = completedDays
                )
            } else c
        }
        _challenges.value = updated
        storage.saveChallenges(updated)
    }

    fun toggleContinuousTimer(challengeId: String) {
        val now = System.currentTimeMillis()
        val today = getTodayDateStr()

        val updated = _challenges.value.map { c ->
            if (c.id == challengeId) {
                val newRunning = !c.isTimerRunning
                c.copy(
                    isTimerRunning = newRunning,
                    timerLastStartedTime = if (newRunning) now else 0L,
                    lastActiveDate = today
                )
            } else c
        }
        _challenges.value = updated
        storage.saveChallenges(updated)
    }

    fun resetTimer(challengeId: String) {
        val updated = _challenges.value.map { c ->
            if (c.id == challengeId) {
                c.copy(
                    isTimerRunning = false,
                    timerElapsedSeconds = 0L,
                    timerLastStartedTime = 0L,
                    currentDailyProgress = 0
                )
            } else c
        }
        _challenges.value = updated
        storage.saveChallenges(updated)
    }

    fun addChallenge(
        title: String,
        description: String,
        type: ChallengeType,
        targetValue: Int,
        unit: String
    ) {
        val newChallenge = Challenge(
            id = UUID.randomUUID().toString(),
            title = title,
            description = description,
            type = type,
            targetValue = targetValue,
            unit = unit,
            currentDailyProgress = 0,
            lastActiveDate = getTodayDateStr(),
            streakDays = 0
        )
        val updated = _challenges.value + newChallenge
        _challenges.value = updated
        storage.saveChallenges(updated)
    }

    fun deleteChallenge(challengeId: String) {
        val updated = _challenges.value.filter { it.id != challengeId }
        _challenges.value = updated
        storage.saveChallenges(updated)
    }
}
