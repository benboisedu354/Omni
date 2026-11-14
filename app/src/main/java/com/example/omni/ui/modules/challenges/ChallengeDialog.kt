package com.example.omni.ui.modules.challenges

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import com.example.omni.data.model.ChallengeType
import com.example.omni.ui.theme.OmniOrange

@Composable
fun AddChallengeDialog(
    onDismiss: () -> Unit,
    onAdd: (title: String, desc: String, type: ChallengeType, target: Int, unit: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var type by remember { mutableStateOf(ChallengeType.DAILY_COUNTER) }
    var targetStr by remember { mutableStateOf("1") }
    var unit by remember { mutableStateOf("fois") }
    var isError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Nouveau défi",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                        if (it.isNotBlank()) isError = false
                    },
                    label = { Text("Nom du défi *") },
                    singleLine = true,
                    isError = isError,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (optionnelle)") },
                    maxLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "Type de défi",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val isCounter = type == ChallengeType.DAILY_COUNTER
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isCounter) OmniOrange else MaterialTheme.colorScheme.surfaceVariant)
                            .clickable {
                                type = ChallengeType.DAILY_COUNTER
                                if (unit == "secondes (24h)") unit = "fois"
                            }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Compteur quotidien",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isCounter) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    val isTimer = type == ChallengeType.CONTINUOUS_TIMER
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isTimer) OmniOrange else MaterialTheme.colorScheme.surfaceVariant)
                            .clickable {
                                type = ChallengeType.CONTINUOUS_TIMER
                                targetStr = "86400"
                                unit = "secondes (24h)"
                            }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Chrono 24h continu",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isTimer) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                if (type == ChallengeType.DAILY_COUNTER) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = targetStr,
                            onValueChange = { targetStr = it.filter { c -> c.isDigit() } },
                            label = { Text("Objectif") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = unit,
                            onValueChange = { unit = it },
                            label = { Text("Unité") },
                            singleLine = true,
                            modifier = Modifier.weight(1.5f)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isBlank()) {
                        isError = true
                    } else {
                        val target = targetStr.toIntOrNull()?.coerceAtLeast(1) ?: 1
                        onAdd(title.trim(), description.trim(), type, target, unit.trim())
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = OmniOrange)
            ) {
                Text("Créer le défi")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler")
            }
        }
    )
}
