package com.example.omni

import com.example.omni.data.model.Challenge
import com.example.omni.data.model.ChallengeType
import com.example.omni.data.model.Todo
import com.example.omni.data.model.TodoPriority
import com.example.omni.data.model.WeatherCodeHelper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class OmniUnitTest {

    @Test
    fun testTodoCreationAndToggle() {
        val todo = Todo(
            title = "Acheter des fruits",
            description = "Pommes et bananes",
            priority = TodoPriority.HIGH
        )

        assertFalse(todo.isCompleted)
        assertEquals("Haute", todo.priority.labelFr)
        assertEquals("Acheter des fruits", todo.title)

        val completed = todo.copy(
            isCompleted = true,
            completedAt = System.currentTimeMillis()
        )
        assertTrue(completed.isCompleted)
        assertNotNull(completed.completedAt)
    }

    @Test
    fun testChallengeDailyCounter() {
        val challenge = Challenge(
            title = "Boire de l'eau",
            type = ChallengeType.DAILY_COUNTER,
            targetValue = 8,
            currentDailyProgress = 7,
            streakDays = 2
        )

        assertFalse(challenge.currentDailyProgress >= challenge.targetValue)

        val updated = challenge.copy(
            currentDailyProgress = challenge.currentDailyProgress + 1,
            streakDays = challenge.streakDays + 1
        )
        assertTrue(updated.currentDailyProgress >= updated.targetValue)
        assertEquals(8, updated.currentDailyProgress)
        assertEquals(3, updated.streakDays)
    }

    @Test
    fun testWeatherCodeHelper() {
        assertEquals("Ciel dégagé", WeatherCodeHelper.getDescription(0))
        assertEquals("☀️", WeatherCodeHelper.getIconEmoji(0))
        assertEquals("Pluie", WeatherCodeHelper.getDescription(61))
        assertEquals("🌧️", WeatherCodeHelper.getIconEmoji(61))
        assertEquals("Orage", WeatherCodeHelper.getDescription(95))
        assertEquals("⛈️", WeatherCodeHelper.getIconEmoji(95))
    }
}
