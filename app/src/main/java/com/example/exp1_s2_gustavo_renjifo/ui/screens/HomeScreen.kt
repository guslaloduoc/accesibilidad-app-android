// app/src/main/java/com/example/exp1_s2_gustavo_renjifo/ui/screens/HomeScreen.kt
package com.example.exp1_s2_gustavo_renjifo.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.exp1_s2_gustavo_renjifo.data.room.entity.Message
import com.example.exp1_s2_gustavo_renjifo.ui.components.ScreenWithAccessibilityBar
import com.example.exp1_s2_gustavo_renjifo.ui.theme.AppPreferences
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HomeScreen(
    userId: Int,
    viewModel: HomeViewModel,
    onLogout: () -> Unit,
    onNavigateToEscribir: () -> Unit = {},
    onNavigateToHablar: () -> Unit = {},
    onNavigateToBuscarDispositivo: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.saveStatus) {
        when (val status = uiState.saveStatus) {
            is SaveStatus.Success -> {
                snackbarHostState.showSnackbar("Mensaje guardado correctamente")
                viewModel.clearStatus()
            }
            is SaveStatus.Error -> {
                snackbarHostState.showSnackbar(status.message)
                viewModel.clearStatus()
            }
            else -> {}
        }
    }

    ScreenWithAccessibilityBar {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            containerColor = if (AppPreferences.isDarkTheme) Color(0xFF121212) else Color(0xFFF5F5F5)
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {
                // Título
                Text(
                    text = "AccesibilidApp - Menú Principal",
                    fontSize = (28.sp * AppPreferences.fontSizeScale),
                    fontWeight = FontWeight.Bold,
                    color = if (AppPreferences.isDarkTheme) Color.White else Color.Black,
                    modifier = Modifier
                        .padding(bottom = 24.dp)
                        .semantics { contentDescription = "Título: AccesibilidApp Menú Principal" }
                )

                // Botones de funcionalidades principales
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (AppPreferences.isDarkTheme)
                            Color(0xFF1E1E1E) else Color.White
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            "🎯 Funciones de Accesibilidad",
                            fontSize = (18.sp * AppPreferences.fontSizeScale),
                            fontWeight = FontWeight.Bold,
                            color = if (AppPreferences.isDarkTheme) Color.White else Color.Black
                        )

                        // Botón Escribir
                        Button(
                            onClick = onNavigateToEscribir,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp)
                                .semantics { contentDescription = "Botón Escribir con texto grande" },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF4CAF50)
                            )
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Escribir",
                                fontSize = (18.sp * AppPreferences.fontSizeScale),
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Botón Hablar
                        Button(
                            onClick = onNavigateToHablar,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp)
                                .semantics { contentDescription = "Botón Hablar con voz" },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF2196F3)
                            )
                        ) {
                            Icon(Icons.Default.VolumeUp, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Hablar",
                                fontSize = (18.sp * AppPreferences.fontSizeScale),
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Botón Buscar Dispositivo
                        Button(
                            onClick = onNavigateToBuscarDispositivo,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp)
                                .semantics { contentDescription = "Botón Buscar Dispositivo" },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFF9800)
                            )
                        ) {
                            Icon(Icons.Default.LocationOn, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Buscar Dispositivo",
                                fontSize = (18.sp * AppPreferences.fontSizeScale),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Sección de mensajes rápidos
                Text(
                    text = "Mis Mensajes Rápidos",
                    fontSize = (20.sp * AppPreferences.fontSizeScale),
                    fontWeight = FontWeight.Bold,
                    color = if (AppPreferences.isDarkTheme) Color.White else Color.Black,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Campo de entrada
                OutlinedTextField(
                    value = uiState.inputText,
                    onValueChange = { viewModel.onTextChange(it) },
                    label = { Text("Escribe un mensaje...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics { contentDescription = "Campo de texto para mensaje" },
                    enabled = uiState.saveStatus !is SaveStatus.Saving,
                    textStyle = LocalTextStyle.current.copy(fontSize = (16.sp * AppPreferences.fontSizeScale))
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Botón guardar
                Button(
                    onClick = { viewModel.saveMessage() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics { contentDescription = "Botón Guardar mensaje" },
                    enabled = uiState.inputText.isNotBlank() && uiState.saveStatus !is SaveStatus.Saving
                ) {
                    if (uiState.saveStatus is SaveStatus.Saving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text(
                        "Guardar",
                        fontSize = (16.sp * AppPreferences.fontSizeScale)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Lista de mensajes (solo mostrar los últimos 3)
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .semantics { contentDescription = "Lista de mensajes guardados" },
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.messages.take(3), key = { it.id }) { message ->
                        MessageItem(message)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Botón logout
                OutlinedButton(
                    onClick = onLogout,
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics { contentDescription = "Botón Cerrar sesión" }
                ) {
                    Text(
                        "Cerrar Sesión",
                        fontSize = (16.sp * AppPreferences.fontSizeScale)
                    )
                }
            }
        }
    }
}

@Composable
fun MessageItem(message: Message) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .semantics { contentDescription = "Mensaje: ${message.text}" },
        colors = CardDefaults.cardColors(
            containerColor = if (AppPreferences.isDarkTheme)
                Color(0xFF1E1E1E) else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Text(
                text = message.text,
                fontSize = (16.sp * AppPreferences.fontSizeScale),
                color = if (AppPreferences.isDarkTheme) Color.White else Color.Black
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = formatTimestamp(message.timestamp),
                fontSize = (12.sp * AppPreferences.fontSizeScale),
                color = if (AppPreferences.isDarkTheme) Color.Gray else Color.DarkGray
            )
        }
    }
}

fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
