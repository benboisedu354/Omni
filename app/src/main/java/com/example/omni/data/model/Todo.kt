package com.example.omni.data.model

import java.util.UUID

enum class TodoPriority(val labelFr: String, val colorHex: Long) {
    LOW("Basse", 0xFF10B981),
    MEDIUM("Moyenne", 0xFFF59E0B),
    HIGH("Haute", 0xFFEF4444)
}

data class TodoLabel(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val colorHex: Long
)

data class Todo(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String = "",
    val location: String = "",
    val isCompleted: Boolean = false,
    val priority: TodoPriority = TodoPriority.MEDIUM,
    val labelIds: List<String> = emptyList(),
    val dueDate: Long? = null,
    val reminderHour: Int? = null,
    val reminderMinute: Int? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null
)
