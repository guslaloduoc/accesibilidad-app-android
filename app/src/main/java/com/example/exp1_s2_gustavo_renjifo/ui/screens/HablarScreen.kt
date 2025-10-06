// app/src/main/java/com/example/exp1_s2_gustavo_renjifo/ui/screens/HablarScreen.kt
package com.example.exp1_s2_gustavo_renjifo.ui.screens

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.exp1_s2_gustavo_renjifo.ui.components.ScreenWithAccessibilityBar
import com.example.exp1_s2_gustavo_renjifo.ui.theme.AppPreferences
import java.util.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HablarScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    var texto by remember { mutableStateOf("") }
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }
    var ttsReady by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Inicializar Text-to-Speech
    DisposableEffect(Unit) {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = tts?.setLanguage(Locale("es", "ES"))
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    tts?.setLanguage(Locale.US)
                }
                ttsReady = true
            }
        }
        onDispose {
            tts?.stop()
            tts?.shutdown()
        }
    }

    // Launcher para reconocimiento de voz
    val speechRecognizerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val spokenText = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            texto = spokenText?.get(0) ?: ""
        }
    }

    ScreenWithAccessibilityBar {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "Hablar",
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
                // Título
                Text(
                    text = "Convierte texto a voz y voz a texto",
                    fontSize = (18.sp * AppPreferences.fontSizeScale),
                    fontWeight = FontWeight.Medium,
                    color = if (AppPreferences.isDarkTheme) Color.White else Color.Black,
                    modifier = Modifier.semantics {
                        contentDescription = "Título: Convierte texto a voz y voz a texto"
                    }
                )

                // Campo de texto
                OutlinedTextField(
                    value = texto,
                    onValueChange = { texto = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .semantics {
                            contentDescription = "Campo de texto para ingresar o mostrar texto"
                        },
                    label = { Text("Texto a leer o dictado") },
                    textStyle = LocalTextStyle.current.copy(
                        fontSize = (18.sp * AppPreferences.fontSizeScale)
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = if (AppPreferences.isDarkTheme)
                            Color.Gray else Color.DarkGray
                    )
                )

                // Sección Text-to-Speech
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (AppPreferences.isDarkTheme)
                            Color(0xFF1E1E1E) else Color(0xFFE8F5E9)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            "🔊 Texto a Voz",
                            fontSize = (16.sp * AppPreferences.fontSizeScale),
                            fontWeight = FontWeight.Bold,
                            color = if (AppPreferences.isDarkTheme) Color.White else Color.Black
                        )

                        Button(
                            onClick = {
                                if (texto.isNotBlank() && ttsReady) {
                                    tts?.speak(texto, TextToSpeech.QUEUE_FLUSH, null, "")
                                } else {
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar(
                                            if (!ttsReady) "TTS no disponible"
                                            else "Escribe algo primero"
                                        )
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp)
                                .semantics { contentDescription = "Botón Reproducir texto en voz alta" },
                            enabled = texto.isNotBlank() && ttsReady
                        ) {
                            Icon(
                                Icons.Default.VolumeUp,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Reproducir en voz alta",
                                fontSize = (16.sp * AppPreferences.fontSizeScale)
                            )
                        }

                        Text(
                            "Escucha el texto escrito en voz alta",
                            fontSize = (14.sp * AppPreferences.fontSizeScale),
                            color = if (AppPreferences.isDarkTheme) Color.LightGray else Color.DarkGray
                        )
                    }
                }

                // Sección Speech-to-Text
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (AppPreferences.isDarkTheme)
                            Color(0xFF1E1E1E) else Color(0xFFE3F2FD)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            "🎤 Voz a Texto",
                            fontSize = (16.sp * AppPreferences.fontSizeScale),
                            fontWeight = FontWeight.Bold,
                            color = if (AppPreferences.isDarkTheme) Color.White else Color.Black
                        )

                        Button(
                            onClick = {
                                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                                    putExtra(
                                        RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                                    )
                                    putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale("es", "ES"))
                                    putExtra(RecognizerIntent.EXTRA_PROMPT, "Habla ahora...")
                                }
                                try {
                                    speechRecognizerLauncher.launch(intent)
                                } catch (e: Exception) {
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Error: ${e.message}")
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp)
                                .semantics { contentDescription = "Botón Dictar por voz" },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondary
                            )
                        ) {
                            Icon(
                                Icons.Default.Mic,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Dictar por voz",
                                fontSize = (16.sp * AppPreferences.fontSizeScale)
                            )
                        }

                        Text(
                            "Convierte tu voz en texto automáticamente",
                            fontSize = (14.sp * AppPreferences.fontSizeScale),
                            color = if (AppPreferences.isDarkTheme) Color.LightGray else Color.DarkGray
                        )
                    }
                }

                // Botón Limpiar
                OutlinedButton(
                    onClick = {
                        texto = ""
                        tts?.stop()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .semantics { contentDescription = "Botón Limpiar texto" },
                    enabled = texto.isNotBlank()
                ) {
                    Text(
                        "Limpiar",
                        fontSize = (16.sp * AppPreferences.fontSizeScale)
                    )
                }

                // Información
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (AppPreferences.isDarkTheme)
                            Color(0xFF1E1E1E) else Color(0xFFFFF3E0)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            "💡 Ayuda:",
                            fontSize = (16.sp * AppPreferences.fontSizeScale),
                            fontWeight = FontWeight.Bold,
                            color = if (AppPreferences.isDarkTheme) Color.White else Color.Black
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "• Escribe texto y toca 'Reproducir' para escucharlo\n" +
                            "• Toca 'Dictar' para convertir tu voz en texto\n" +
                            "• Ideal para personas con discapacidad visual",
                            fontSize = (14.sp * AppPreferences.fontSizeScale),
                            color = if (AppPreferences.isDarkTheme) Color.LightGray else Color.DarkGray
                        )
                    }
                }
            }
        }
    }
}
