package com.example.omni.ui.modules.todo

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.omni.data.model.Todo
import com.example.omni.data.model.TodoLabel
import com.example.omni.data.model.TodoPriority
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun TodoFormDialog(
    initialTodo: Todo? = null,
    labels: List<TodoLabel>,
    onDismiss: () -> Unit,
    onSave: (
        title: String,
        description: String,
        location: String,
        priority: TodoPriority,
        labelIds: List<String>,
        dueDate: Long?,
        reminderHour: Int?,
        reminderMinute: Int?
    ) -> Unit
) {
    var title by remember { mutableStateOf(initialTodo?.title ?: "") }
    var description by remember { mutableStateOf(initialTodo?.description ?: "") }
    var location by remember { mutableStateOf(initialTodo?.location ?: "") }
    var priority by remember { mutableStateOf(initialTodo?.priority ?: TodoPriority.MEDIUM) }
    var selectedLabels by remember { mutableStateOf(initialTodo?.labelIds?.toSet() ?: emptySet()) }
    var dueDate by remember { mutableStateOf(initialTodo?.dueDate) }
    var showDatePickerDialog by remember { mutableStateOf(false) }
    var hasReminder by remember { mutableStateOf(initialTodo?.reminderHour != null) }
    var reminderHour by remember { mutableIntStateOf(initialTodo?.reminderHour ?: 6) }
    var reminderMinute by remember { mutableIntStateOf(initialTodo?.reminderMinute ?: 0) }

    var titleError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (initialTodo == null) "Nouvelle tâche" else "Modifier la tâche",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Title
                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                        if (it.isNotBlank()) titleError = false
                    },
                    label = { Text("Titre de la tâche *") },
                    isError = titleError,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                if (titleError) {
                    Text(
                        text = "Le titre est obligatoire",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp
                    )
                }

                // Description
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (optionnelle)") },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )

                // Location / Lieu
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Lieu (optionnel)") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Place,
                            contentDescription = "Lieu",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Priority
                Text(
                    text = "Priorité",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TodoPriority.entries.forEach { p ->
                        val isSelected = priority == p
                        val pColor = Color(p.colorHex)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) pColor else pColor.copy(alpha = 0.1f))
                                .clickable { priority = p }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = p.labelFr,
                                color = if (isSelected) Color.White else pColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                // Labels
                if (labels.isNotEmpty()) {
                    Text(
                        text = "Étiquettes",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        labels.forEach { label ->
                            val isSelected = selectedLabels.contains(label.id)
                            val labelColor = Color(label.colorHex)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) labelColor else labelColor.copy(alpha = 0.12f))
                                    .border(
                                        width = 1.dp,
                                        color = if (isSelected) labelColor else Color.Transparent,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable {
                                        selectedLabels = if (isSelected) {
                                            selectedLabels - label.id
                                        } else {
                                            selectedLabels + label.id
                                        }
                                    }
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    text = label.name,
                                    color = if (isSelected) Color.White else labelColor,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                // Due date
                Text(
                    text = "Échéance",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )

                // Date Picker trigger button
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                        .clickable { showDatePickerDialog = true }
                        .padding(horizontal = 14.dp, vertical = 12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (dueDate != null) {
                                val sdf = SimpleDateFormat("EEEE d MMMM yyyy", Locale.FRENCH)
                                sdf.format(Date(dueDate!!)).replaceFirstChar { it.uppercase() }
                            } else {
                                "Choisir une date avec le calendrier..."
                            },
                            fontSize = 13.sp,
                            fontWeight = if (dueDate != null) FontWeight.SemiBold else FontWeight.Normal,
                            color = if (dueDate != null) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    if (dueDate != null) {
                        Text(
                            text = "Modifier",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                // Quick shortcuts
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val today = remember {
                        Calendar.getInstance().apply {
                            set(Calendar.HOUR_OF_DAY, 23)
                            set(Calendar.MINUTE, 59)
                        }.timeInMillis
                    }
                    val tomorrow = remember {
                        Calendar.getInstance().apply {
                            add(Calendar.DAY_OF_YEAR, 1)
                            set(Calendar.HOUR_OF_DAY, 23)
                            set(Calendar.MINUTE, 59)
                        }.timeInMillis
                    }
                    val in3Days = remember {
                        Calendar.getInstance().apply {
                            add(Calendar.DAY_OF_YEAR, 3)
                            set(Calendar.HOUR_OF_DAY, 23)
                            set(Calendar.MINUTE, 59)
                        }.timeInMillis
                    }

                    DateChip(
                        label = "Aujourd'hui",
                        isSelected = dueDate != null && Math.abs(dueDate!! - today) < 1000 * 3600 * 12,
                        onClick = { dueDate = today }
                    )
                    DateChip(
                        label = "Demain",
                        isSelected = dueDate != null && Math.abs(dueDate!! - tomorrow) < 1000 * 3600 * 12,
                        onClick = { dueDate = tomorrow }
                    )
                    DateChip(
                        label = "+3 jours",
                        isSelected = dueDate != null && Math.abs(dueDate!! - in3Days) < 1000 * 3600 * 12,
                        onClick = { dueDate = in3Days }
                    )
                    if (dueDate != null) {
                        DateChip(
                            label = "Effacer",
                            isSelected = false,
                            onClick = { dueDate = null },
                            color = Color(0xFFEF4444)
                        )
                    }
                }

                // Reminder
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Alarm,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.width(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Activer un rappel",
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp
                        )
                    }
                    Switch(
                        checked = hasReminder,
                        onCheckedChange = { hasReminder = it }
                    )
                }

                if (hasReminder) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = "Heure : ", fontSize = 13.sp)
                        val commonHours = listOf(6 to 0, 8 to 0, 12 to 0, 18 to 0, 20 to 0)
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            commonHours.forEach { (h, m) ->
                                val isSelected = reminderHour == h && reminderMinute == m
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(
                                            if (isSelected) MaterialTheme.colorScheme.primary
                                            else MaterialTheme.colorScheme.surfaceVariant
                                        )
                                        .clickable {
                                            reminderHour = h
                                            reminderMinute = m
                                        }
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = String.format(Locale.getDefault(), "%02d:%02d", h, m),
                                        fontSize = 12.sp,
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isBlank()) {
                        titleError = true
                    } else {
                        onSave(
                            title.trim(),
                            description.trim(),
                            location.trim(),
                            priority,
                            selectedLabels.toList(),
                            dueDate,
                            if (hasReminder) reminderHour else null,
                            if (hasReminder) reminderMinute else null
                        )
                    }
                }
            ) {
                Text("Enregistrer")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler")
            }
        }
    )

    if (showDatePickerDialog) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = dueDate ?: System.currentTimeMillis()
        )
        DatePickerDialog(
            onDismissRequest = { showDatePickerDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    dueDate = datePickerState.selectedDateMillis
                    showDatePickerDialog = false
                }) {
                    Text("Confirmer")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePickerDialog = false }) {
                    Text("Annuler")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Composable
fun DateChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    color: Color = MaterialTheme.colorScheme.primary
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) color else color.copy(alpha = 0.12f))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = if (isSelected) Color.White else color
        )
    }
}
