// app/src/main/java/com/example/exp1_s2_gustavo_renjifo/ui/screens/BuscarDispositivoScreen.kt
package com.example.exp1_s2_gustavo_renjifo.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.media.MediaPlayer
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.exp1_s2_gustavo_renjifo.ui.components.ScreenWithAccessibilityBar
import com.example.exp1_s2_gustavo_renjifo.ui.theme.AppPreferences
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuscarDispositivoScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    var ubicacion by remember { mutableStateOf<Location?>(null) }
    var permisoUbicacion by remember { mutableStateOf(false) }
    var cargando by remember { mutableStateOf(false) }
    var sonandoAlarma by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Verificar permiso inicial
    LaunchedEffect(Unit) {
        permisoUbicacion = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    // Launcher para solicitar permiso
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        permisoUbicacion = granted
        if (!granted) {
            coroutineScope.launch {
                snackbarHostState.showSnackbar("Permiso de ubicación denegado")
            }
        }
    }

    // MediaPlayer para sonido
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer?.release()
        }
    }

    ScreenWithAccessibilityBar {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "Buscar Dispositivo",
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
                    text = "Encuentra tu dispositivo extraviado",
                    fontSize = (18.sp * AppPreferences.fontSizeScale),
                    fontWeight = FontWeight.Medium,
                    color = if (AppPreferences.isDarkTheme) Color.White else Color.Black,
                    modifier = Modifier.semantics {
                        contentDescription = "Título: Encuentra tu dispositivo"
                    }
                )

                // Sección de ubicación
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
                            "📍 Ubicación del Dispositivo",
                            fontSize = (16.sp * AppPreferences.fontSizeScale),
                            fontWeight = FontWeight.Bold,
                            color = if (AppPreferences.isDarkTheme) Color.White else Color.Black
                        )

                        if (!permisoUbicacion) {
                            Button(
                                onClick = {
                                    permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(60.dp)
                                    .semantics { contentDescription = "Botón Solicitar permiso de ubicación" }
                            ) {
                                Icon(Icons.Default.LocationOn, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "Solicitar Permiso de Ubicación",
                                    fontSize = (16.sp * AppPreferences.fontSizeScale)
                                )
                            }
                        } else {
                            Button(
                                onClick = {
                                    cargando = true
                                    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

                                    try {
                                        fusedLocationClient.getCurrentLocation(
                                            Priority.PRIORITY_HIGH_ACCURACY,
                                            null
                                        ).addOnSuccessListener { location ->
                                            cargando = false
                                            ubicacion = location
                                        }.addOnFailureListener { e ->
                                            cargando = false
                                            coroutineScope.launch {
                                                snackbarHostState.showSnackbar("Error: ${e.message}")
                                            }
                                        }
                                    } catch (e: SecurityException) {
                                        cargando = false
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar("Error de permisos")
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(60.dp)
                                    .semantics { contentDescription = "Botón Obtener ubicación actual" },
                                enabled = !cargando
                            ) {
                                if (cargando) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(24.dp),
                                        color = Color.White
                                    )
                                } else {
                                    Icon(Icons.Default.LocationOn, contentDescription = null)
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    if (cargando) "Obteniendo..." else "Obtener Ubicación",
                                    fontSize = (16.sp * AppPreferences.fontSizeScale)
                                )
                            }
                        }

                        if (ubicacion != null) {
                            Divider()
                            Text(
                                "Coordenadas actuales:",
                                fontSize = (14.sp * AppPreferences.fontSizeScale),
                                fontWeight = FontWeight.Bold,
                                color = if (AppPreferences.isDarkTheme) Color.White else Color.Black
                            )
                            Text(
                                "Latitud: ${ubicacion!!.latitude}\nLongitud: ${ubicacion!!.longitude}",
                                fontSize = (14.sp * AppPreferences.fontSizeScale),
                                color = if (AppPreferences.isDarkTheme) Color.LightGray else Color.DarkGray,
                                modifier = Modifier.semantics {
                                    contentDescription = "Coordenadas: Latitud ${ubicacion!!.latitude}, Longitud ${ubicacion!!.longitude}"
                                }
                            )
                        }
                    }
                }

                // Sección de alarma sonora
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (AppPreferences.isDarkTheme)
                            Color(0xFF1E1E1E) else Color(0xFFFFEBEE)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            "🔔 Alarma Sonora",
                            fontSize = (16.sp * AppPreferences.fontSizeScale),
                            fontWeight = FontWeight.Bold,
                            color = if (AppPreferences.isDarkTheme) Color.White else Color.Black
                        )

                        Button(
                            onClick = {
                                if (!sonandoAlarma) {
                                    // Iniciar sonido de notificación
                                    try {
                                        mediaPlayer = MediaPlayer.create(
                                            context,
                                            android.provider.Settings.System.DEFAULT_RINGTONE_URI
                                        )
                                        mediaPlayer?.isLooping = true
                                        mediaPlayer?.start()
                                        sonandoAlarma = true
                                    } catch (e: Exception) {
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar("Error al reproducir sonido")
                                        }
                                    }
                                } else {
                                    // Detener sonido
                                    mediaPlayer?.stop()
                                    mediaPlayer?.release()
                                    mediaPlayer = null
                                    sonandoAlarma = false
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp)
                                .semantics {
                                    contentDescription = if (sonandoAlarma)
                                        "Botón Detener alarma" else "Botón Activar alarma"
                                },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (sonandoAlarma)
                                    MaterialTheme.colorScheme.error
                                else MaterialTheme.colorScheme.secondary
                            )
                        ) {
                            Icon(Icons.Default.Notifications, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                if (sonandoAlarma) "Detener Alarma" else "Activar Alarma",
                                fontSize = (16.sp * AppPreferences.fontSizeScale)
                            )
                        }

                        Text(
                            "Reproduce un sonido fuerte para encontrar el dispositivo",
                            fontSize = (14.sp * AppPreferences.fontSizeScale),
                            color = if (AppPreferences.isDarkTheme) Color.LightGray else Color.DarkGray
                        )
                    }
                }

                // Sección de vibración
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (AppPreferences.isDarkTheme)
                            Color(0xFF1E1E1E) else Color(0xFFFFF3E0)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            "📳 Vibración",
                            fontSize = (16.sp * AppPreferences.fontSizeScale),
                            fontWeight = FontWeight.Bold,
                            color = if (AppPreferences.isDarkTheme) Color.White else Color.Black
                        )

                        Button(
                            onClick = {
                                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    vibrator.vibrate(
                                        VibrationEffect.createWaveform(
                                            longArrayOf(0, 500, 200, 500, 200, 500),
                                            -1
                                        )
                                    )
                                } else {
                                    @Suppress("DEPRECATION")
                                    vibrator.vibrate(2000)
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp)
                                .semantics { contentDescription = "Botón Vibrar dispositivo" },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFF6F00)
                            )
                        ) {
                            Icon(Icons.Default.Vibration, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Vibrar Dispositivo",
                                fontSize = (16.sp * AppPreferences.fontSizeScale)
                            )
                        }

                        Text(
                            "Activa una vibración fuerte para sentir el dispositivo",
                            fontSize = (14.sp * AppPreferences.fontSizeScale),
                            color = if (AppPreferences.isDarkTheme) Color.LightGray else Color.DarkGray
                        )
                    }
                }

                // Información
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
                            "💡 Ayuda:",
                            fontSize = (16.sp * AppPreferences.fontSizeScale),
                            fontWeight = FontWeight.Bold,
                            color = if (AppPreferences.isDarkTheme) Color.White else Color.Black
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "• Obtén la ubicación GPS del dispositivo\n" +
                            "• Activa una alarma sonora para encontrarlo\n" +
                            "• Usa vibración si está cerca pero no lo ves\n" +
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
