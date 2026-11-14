package com.example.omni.ui.modules.todo

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.omni.data.model.Todo
import com.example.omni.data.model.TodoLabel
import com.example.omni.data.model.TodoPriority
import com.example.omni.data.repository.OmniStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Calendar
import java.util.UUID

enum class TodoFilter {
    ALL, ACTIVE, COMPLETED
}

data class DailyCompletionStat(
    val dayName: String,
    val count: Int
)

data class WeeklyReportData(
    val createdCount: Int,
    val completedCount: Int,
    val dailyStats: List<DailyCompletionStat>
)

class TodoViewModel(application: Application) : AndroidViewModel(application) {
    private val storage = OmniStorage(application)

    private val _todos = MutableStateFlow<List<Todo>>(emptyList())
    val todos: StateFlow<List<Todo>> = _todos.asStateFlow()

    private val _labels = MutableStateFlow<List<TodoLabel>>(emptyList())
    val labels: StateFlow<List<TodoLabel>> = _labels.asStateFlow()

    private val _filter = MutableStateFlow(TodoFilter.ALL)
    val filter: StateFlow<TodoFilter> = _filter.asStateFlow()

    private val _selectedLabelFilter = MutableStateFlow<String?>(null)
    val selectedLabelFilter: StateFlow<String?> = _selectedLabelFilter.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        _labels.value = storage.getLabels()
        _todos.value = storage.getTodos()
    }

    fun setFilter(filter: TodoFilter) {
        _filter.value = filter
    }

    fun setSelectedLabelFilter(labelId: String?) {
        _selectedLabelFilter.value = labelId
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun addTodo(
        title: String,
        description: String,
        location: String = "",
        priority: TodoPriority,
        labelIds: List<String>,
        dueDate: Long?,
        reminderHour: Int?,
        reminderMinute: Int?
    ) {
        val newTodo = Todo(
            id = UUID.randomUUID().toString(),
            title = title,
            description = description,
            location = location,
            priority = priority,
            labelIds = labelIds,
            dueDate = dueDate,
            reminderHour = reminderHour,
            reminderMinute = reminderMinute,
            createdAt = System.currentTimeMillis()
        )
        val updated = _todos.value + newTodo
        _todos.value = updated
        storage.saveTodos(updated)
    }

    fun updateTodo(todo: Todo) {
        val updated = _todos.value.map { if (it.id == todo.id) todo else it }
        _todos.value = updated
        storage.saveTodos(updated)
    }

    fun toggleTodo(todoId: String) {
        val updated = _todos.value.map {
            if (it.id == todoId) {
                val newStatus = !it.isCompleted
                it.copy(
                    isCompleted = newStatus,
                    completedAt = if (newStatus) System.currentTimeMillis() else null
                )
            } else it
        }
        _todos.value = updated
        storage.saveTodos(updated)
    }

    fun deleteTodo(todoId: String) {
        val updated = _todos.value.filter { it.id != todoId }
        _todos.value = updated
        storage.saveTodos(updated)
    }

    fun addLabel(name: String, colorHex: Long) {
        val newLabel = TodoLabel(
            id = UUID.randomUUID().toString(),
            name = name,
            colorHex = colorHex
        )
        val updated = _labels.value + newLabel
        _labels.value = updated
        storage.saveLabels(updated)
    }

    fun updateLabel(label: TodoLabel) {
        val updated = _labels.value.map { if (it.id == label.id) label else it }
        _labels.value = updated
        storage.saveLabels(updated)
    }

    fun deleteLabel(labelId: String) {
        val updatedLabels = _labels.value.filter { it.id != labelId }
        _labels.value = updatedLabels
        storage.saveLabels(updatedLabels)

        // remove from todos as well
        val updatedTodos = _todos.value.map { todo ->
            if (todo.labelIds.contains(labelId)) {
                todo.copy(labelIds = todo.labelIds.filter { it != labelId })
            } else todo
        }
        _todos.value = updatedTodos
        storage.saveTodos(updatedTodos)
    }

    fun getWeeklyReport(): WeeklyReportData {
        val cal = Calendar.getInstance()
        val now = System.currentTimeMillis()
        val sevenDaysAgo = now - (7L * 24 * 60 * 60 * 1000)

        val createdInLast7Days = _todos.value.count { it.createdAt >= sevenDaysAgo }
        val completedInLast7Days = _todos.value.count {
            it.isCompleted && (it.completedAt ?: 0L) >= sevenDaysAgo
        }

        val dayNames = listOf("Dim", "Lun", "Mar", "Mer", "Jeu", "Ven", "Sam")
        val stats = mutableListOf<DailyCompletionStat>()

        for (i in 6 downTo 0) {
            val dayCal = Calendar.getInstance().apply {
                timeInMillis = now - (i * 24L * 60 * 60 * 1000)
            }
            val startOfDay = dayCal.apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis

            val endOfDay = startOfDay + (24L * 60 * 60 * 1000) - 1
            val dayOfWeek = dayCal.get(Calendar.DAY_OF_WEEK) - 1
            val dayLabel = dayNames[dayOfWeek]

            val count = _todos.value.count {
                it.isCompleted && (it.completedAt ?: 0L) in startOfDay..endOfDay
            }
            stats.add(DailyCompletionStat(dayLabel, count))
        }

        return WeeklyReportData(
            createdCount = createdInLast7Days,
            completedCount = completedInLast7Days,
            dailyStats = stats
        )
    }
}
