// app/src/main/java/com/example/exp1_s2_gustavo_renjifo/ui/screens/EscribirScreen.kt
package com.example.exp1_s2_gustavo_renjifo.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.exp1_s2_gustavo_renjifo.ui.components.ScreenWithAccessibilityBar
import com.example.exp1_s2_gustavo_renjifo.ui.theme.AppPreferences
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EscribirScreen(
    onNavigateBack: () -> Unit
) {
    var texto by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    ScreenWithAccessibilityBar {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "Escribir",
                            fontSize = (20.sp * AppPreferences.fontSizeScale)
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Volver"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = if (AppPreferences.isDarkTheme)
                            Color(0xFF1E1E1E) else MaterialTheme.colorScheme.primaryContainer
                    )
                )
            },
            snackbarHost = { SnackbarHost(snackbarHostState) },
            containerColor = if (AppPreferences.isDarkTheme) Color(0xFF121212) else Color(0xFFF5F5F5)
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Título descriptivo
                Text(
                    text = "Escribe tu mensaje con texto grande y accesible",
                    fontSize = (18.sp * AppPreferences.fontSizeScale),
                    fontWeight = FontWeight.Medium,
                    color = if (AppPreferences.isDarkTheme) Color.White else Color.Black,
                    modifier = Modifier.semantics {
                        contentDescription = "Título: Escribe tu mensaje"
                    }
                )

                // Campo de texto grande
                OutlinedTextField(
                    value = texto,
                    onValueChange = { texto = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .semantics {
                            contentDescription = "Campo de texto para escribir mensaje largo"
                        },
                    label = { Text("Tu mensaje aquí...") },
                    textStyle = LocalTextStyle.current.copy(
                        fontSize = (20.sp * AppPreferences.fontSizeScale)
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = if (AppPreferences.isDarkTheme)
                            Color.Gray else Color.DarkGray
                    )
                )

                // Contador de caracteres
                Text(
                    text = "${texto.length} caracteres",
                    fontSize = (14.sp * AppPreferences.fontSizeScale),
                    color = if (AppPreferences.isDarkTheme) Color.LightGray else Color.Gray,
                    modifier = Modifier.semantics {
                        contentDescription = "Contador: ${texto.length} caracteres escritos"
                    }
                )

                // Botón Guardar
                Button(
                    onClick = {
                        if (texto.isNotBlank()) {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(
                                    "Texto guardado: ${texto.take(30)}..."
                                )
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .semantics { contentDescription = "Botón Guardar texto" },
                    enabled = texto.isNotBlank()
                ) {
                    Text(
                        "Guardar",
                        fontSize = (18.sp * AppPreferences.fontSizeScale),
                        fontWeight = FontWeight.Bold
                    )
                }

                // Botón Limpiar
                OutlinedButton(
                    onClick = { texto = "" },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .semantics { contentDescription = "Botón Limpiar texto" },
                    enabled = texto.isNotBlank()
                ) {
                    Text(
                        "Limpiar",
                        fontSize = (18.sp * AppPreferences.fontSizeScale)
                    )
                }

                // Información de accesibilidad
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (AppPreferences.isDarkTheme)
                            Color(0xFF1E1E1E) else Color(0xFFE3F2FD)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            "💡 Funciones de accesibilidad:",
                            fontSize = (16.sp * AppPreferences.fontSizeScale),
                            fontWeight = FontWeight.Bold,
                            color = if (AppPreferences.isDarkTheme) Color.White else Color.Black
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "• Texto grande para fácil lectura\n" +
                            "• Alto contraste en modo oscuro\n" +
                            "• Compatible con lectores de pantalla\n" +
                            "• Ajusta el tamaño desde la barra superior",
                            fontSize = (14.sp * AppPreferences.fontSizeScale),
                            color = if (AppPreferences.isDarkTheme) Color.LightGray else Color.DarkGray
                        )
                    }
                }
            }
        }
    }
}
