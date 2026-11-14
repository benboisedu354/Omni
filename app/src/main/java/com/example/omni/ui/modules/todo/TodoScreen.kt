package com.example.omni.ui.modules.todo

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.omni.data.model.Todo
import com.example.omni.ui.components.OmniTopBar
import com.example.omni.ui.theme.OmniBlue

@Composable
fun TodoScreen(
    onBack: () -> Unit,
    viewModel: TodoViewModel = viewModel()
) {
    val todos by viewModel.todos.collectAsState()
    val labels by viewModel.labels.collectAsState()
    val currentFilter by viewModel.filter.collectAsState()
    val selectedLabelFilter by viewModel.selectedLabelFilter.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    var showFormDialog by remember { mutableStateOf(false) }
    var editingTodo by remember { mutableStateOf<Todo?>(null) }
    var showLabelManager by remember { mutableStateOf(false) }
    var showWeeklyReport by remember { mutableStateOf(false) }

    val filteredTodos = remember(todos, currentFilter, selectedLabelFilter, searchQuery) {
        todos.filter { todo ->
            val matchesFilter = when (currentFilter) {
                TodoFilter.ALL -> true
                TodoFilter.ACTIVE -> !todo.isCompleted
                TodoFilter.COMPLETED -> todo.isCompleted
            }
            val matchesLabel = selectedLabelFilter == null || todo.labelIds.contains(selectedLabelFilter)
            val matchesSearch = searchQuery.isBlank() ||
                    todo.title.contains(searchQuery, ignoreCase = true) ||
                    todo.description.contains(searchQuery, ignoreCase = true) ||
                    todo.location.contains(searchQuery, ignoreCase = true)

            matchesFilter && matchesLabel && matchesSearch
        }
    }

    Scaffold(
        topBar = {
            OmniTopBar(
                title = "Todo-list",
                onBack = onBack,
                accentColor = OmniBlue,
                actions = {
                    IconButton(onClick = { showWeeklyReport = true }) {
                        Icon(
                            imageVector = Icons.Default.BarChart,
                            contentDescription = "Rapport hebdomadaire",
                            tint = OmniBlue
                        )
                    }
                    IconButton(onClick = { showLabelManager = true }) {
                        Icon(
                            imageVector = Icons.Default.Label,
                            contentDescription = "Gérer les étiquettes",
                            tint = OmniBlue
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    editingTodo = null
                    showFormDialog = true
                },
                containerColor = OmniBlue,
                contentColor = Color.White
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Nouvelle tâche")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                placeholder = { Text("Rechercher une tâche...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotBlank()) {
                        IconButton(onClick = { viewModel.setSearchQuery("") }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Effacer",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )

            // Status Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val filters = listOf(
                    TodoFilter.ALL to "Toutes (${todos.size})",
                    TodoFilter.ACTIVE to "En cours (${todos.count { !it.isCompleted }})",
                    TodoFilter.COMPLETED to "Terminées (${todos.count { it.isCompleted }})"
                )
                filters.forEach { (f, label) ->
                    val isSelected = currentFilter == f
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (isSelected) OmniBlue else MaterialTheme.colorScheme.surfaceVariant
                            )
                            .clickable { viewModel.setFilter(f) }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = label,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Labels Horizontal Filter
            if (labels.isNotEmpty()) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        val isSelected = selectedLabelFilter == null
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.surfaceVariant
                                )
                                .clickable { viewModel.setSelectedLabelFilter(null) }
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Tous les labels",
                                fontSize = 11.sp,
                                color = if (isSelected) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    items(labels) { label ->
                        val isSelected = selectedLabelFilter == label.id
                        val lColor = Color(label.colorHex)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) lColor else lColor.copy(alpha = 0.12f))
                                .clickable {
                                    viewModel.setSelectedLabelFilter(if (isSelected) null else label.id)
                                }
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = label.name,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (isSelected) Color.White else lColor
                            )
                        }
                    }
                }
            }

            // Todos List
            if (filteredTodos.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (searchQuery.isNotBlank()) "Aucune tâche trouvée" else "Aucune tâche",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Appuyez sur + pour ajouter une nouvelle tâche",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredTodos, key = { it.id }) { todo ->
                        TodoItemRow(
                            todo = todo,
                            labels = labels,
                            onToggle = { viewModel.toggleTodo(todo.id) },
                            onEdit = {
                                editingTodo = todo
                                showFormDialog = true
                            },
                            onDelete = { viewModel.deleteTodo(todo.id) }
                        )
                    }
                }
            }
        }
    }

    // Dialogs
    if (showFormDialog) {
        TodoFormDialog(
            initialTodo = editingTodo,
            labels = labels,
            onDismiss = {
                showFormDialog = false
                editingTodo = null
            },
            onSave = { title, desc, loc, priority, labelIds, dueDate, rHour, rMin ->
                if (editingTodo == null) {
                    viewModel.addTodo(title, desc, loc, priority, labelIds, dueDate, rHour, rMin)
                } else {
                    viewModel.updateTodo(
                        editingTodo!!.copy(
                            title = title,
                            description = desc,
                            location = loc,
                            priority = priority,
                            labelIds = labelIds,
                            dueDate = dueDate,
                            reminderHour = rHour,
                            reminderMinute = rMin
                        )
                    )
                }
                showFormDialog = false
                editingTodo = null
            }
        )
    }

    if (showLabelManager) {
        TodoLabelManagerDialog(
            labels = labels,
            onAddLabel = { name, color -> viewModel.addLabel(name, color) },
            onDeleteLabel = { labelId -> viewModel.deleteLabel(labelId) },
            onDismiss = { showLabelManager = false }
        )
    }

    if (showWeeklyReport) {
        TodoWeeklyReportDialog(
            reportData = viewModel.getWeeklyReport(),
            onDismiss = { showWeeklyReport = false }
        )
    }
}
