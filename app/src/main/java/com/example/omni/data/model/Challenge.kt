package com.example.omni.data.model

import java.util.UUID

enum class ChallengeType {
    DAILY_COUNTER,
    CONTINUOUS_TIMER
}

data class Challenge(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String = "",
    val type: ChallengeType = ChallengeType.DAILY_COUNTER,
    val targetValue: Int = 1,
    val unit: String = "",
    val currentDailyProgress: Int = 0,
    val lastActiveDate: String = "",
    val streakDays: Int = 0,
    val completedDays: List<String> = emptyList(),
    val isTimerRunning: Boolean = false,
    val timerElapsedSeconds: Long = 0L,
    val timerLastStartedTime: Long = 0L
)
