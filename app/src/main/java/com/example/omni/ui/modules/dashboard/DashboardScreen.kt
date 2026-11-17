package com.example.omni.ui.modules.dashboard

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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AllInclusive
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.omni.ui.navigation.Screen
import com.example.omni.ui.theme.OmniBlue
import com.example.omni.ui.theme.OmniCyan
import com.example.omni.ui.theme.OmniEmerald
import com.example.omni.ui.theme.OmniOrange
import com.example.omni.ui.theme.OmniPink
import com.example.omni.ui.theme.OmniPurple
import com.example.omni.ui.theme.OmniSky
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class DashboardModule(
    val id: String,
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val accentColor: Color,
    val route: String
)

@Composable
fun DashboardScreen(
    onNavigate: (String) -> Unit
) {
    val currentDateStr = remember {
        val sdf = SimpleDateFormat("EEEE d MMMM", Locale.FRENCH)
        sdf.format(Date()).replaceFirstChar { it.uppercase() }
    }

    val modules = remember {
        listOf(
            DashboardModule(
                id = "todo",
                title = "Todo-list",
                subtitle = "Tâches, rappels & bilan",
                icon = Icons.Default.CheckCircle,
                accentColor = OmniBlue,
                route = Screen.Todo.route
            ),
            DashboardModule(
                id = "challenges",
                title = "Défis",
                subtitle = "Compteurs & chrono 24h",
                icon = Icons.Default.EmojiEvents,
                accentColor = OmniOrange,
                route = Screen.Challenges.route
            ),
            DashboardModule(
                id = "clock",
                title = "Horloge",
                subtitle = "Monde, chrono & timer",
                icon = Icons.Default.Schedule,
                accentColor = OmniCyan,
                route = Screen.Clock.route
            ),
            DashboardModule(
                id = "weather",
                title = "Météo",
                subtitle = "Prévisions Open-Meteo",
                icon = Icons.Default.WbSunny,
                accentColor = OmniSky,
                route = Screen.Weather.route
            ),
            DashboardModule(
                id = "gps",
                title = "GPS & Cap",
                subtitle = "Coordonnées & boussole",
                icon = Icons.Default.Explore,
                accentColor = OmniEmerald,
                route = Screen.Gps.route
            ),
            DashboardModule(
                id = "sensors",
                title = "Capteurs",
                subtitle = "Mesures physiques en direct",
                icon = Icons.Default.Sensors,
                accentColor = OmniPurple,
                route = Screen.Sensors.route
            ),
            DashboardModule(
                id = "calendar",
                title = "Agenda",
                subtitle = "Événements & planning",
                icon = Icons.Default.CalendarMonth,
                accentColor = OmniPink,
                route = Screen.Calendar.route
            )
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = innerPadding.calculateTopPadding() + 8.dp,
                bottom = innerPadding.calculateBottomPadding() + 20.dp
            ),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            item(span = { GridItemSpan(2) }) {
                Column(modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.linearGradient(
                                            listOf(OmniBlue, OmniPurple)
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AllInclusive,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = "OMNI",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 1.sp,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Text(
                                    text = currentDateStr,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(999.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.92f),
                            tonalElevation = 1.dp,
                            shadowElevation = 0.dp
                        ) {
                            IconButton(
                                onClick = { onNavigate(Screen.Settings.route) },
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Notifications,
                                    contentDescription = "Rappels & notifications",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(
                                            OmniPurple.copy(alpha = 0.18f),
                                            OmniBlue.copy(alpha = 0.08f),
                                            Color.Transparent
                                        )
                                    )
                                )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(18.dp)
                            ) {
                                Text(
                                    text = "Tableau de bord",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Tous vos outils quotidiens réunis dans une application locale, fluide et sans backend.",
                                    fontSize = 13.sp,
                                    lineHeight = 18.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            items(modules) { module ->
                ModuleCard(module = module, onClick = { onNavigate(module.route) })
            }
        }
    }
}

@Composable
fun ModuleCard(
    module: DashboardModule,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            module.accentColor.copy(alpha = 0.14f),
                            Color.Transparent
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(module.accentColor.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = module.icon,
                        contentDescription = module.title,
                        tint = module.accentColor,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Column {
                    Text(
                        text = module.title,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = module.subtitle,
                        fontSize = 11.sp,
                        lineHeight = 15.sp,
                        maxLines = 2,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}