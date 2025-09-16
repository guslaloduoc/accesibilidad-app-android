package com.example.exp1_s2_gustavo_renjifo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.exp1_s2_gustavo_renjifo.services.*
import com.example.exp1_s2_gustavo_renjifo.ui.components.ScreenWithAccessibilityBar
import com.example.exp1_s2_gustavo_renjifo.ui.theme.AppPreferences
import com.example.exp1_s2_gustavo_renjifo.ui.theme.*
import com.example.exp1_s2_gustavo_renjifo.utils.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ErrorHandlingDemoScreen(
    onNavigateBack: () -> Unit = {}
) {
    // Services
    val authService = remember { AuthenticationService() }
    val networkService = remember { NetworkService() }
    val scope = rememberCoroutineScope()

    // Estados para mostrar resultados
    var authResult by remember { mutableStateOf<OperationResult<*>?>(null) }
    var networkResult by remember { mutableStateOf<OperationResult<*>?>(null) }
    var fileResult by remember { mutableStateOf<OperationResult<*>?>(null) }

    // Estados de loading
    var isAuthLoading by remember { mutableStateOf(false) }
    var isNetworkLoading by remember { mutableStateOf(false) }
    var isFileLoading by remember { mutableStateOf(false) }

    val gradientBrush = if (AppPreferences.isDarkTheme) {
        Brush.verticalGradient(
            colors = listOf(
                GradientGreenDark1,
                GradientGreenDark2,
                GradientGreenDark3
            )
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(
                GradientGreen1,
                GradientGreen2,
                GradientGreen3
            )
        )
    }

    ScreenWithAccessibilityBar {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(brush = gradientBrush)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Atrás",
                        tint = if (AppPreferences.isDarkTheme) Color.White else Color.Black
                    )
                }
                Text(
                    text = "Demo de Manejo de Errores",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = if (AppPreferences.isDarkTheme) Color.White else Color.Black,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Text(
                text = "Ejemplos Prácticos de try/catch",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = if (AppPreferences.isDarkTheme) Color(0xFF81C784) else Color(0xFF2E7D32),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Sección 1: Autenticación con errores
            ErrorDemoCard(
                title = "🔐 Autenticación con Errores",
                description = "Prueba diferentes escenarios de error en login",
                isLoading = isAuthLoading,
                result = authResult,
                onTest = {
                    scope.launch {
                        isAuthLoading = true
                        authResult = OperationResult.Loading("Autenticando...")

                        // Ejemplo 1: Login con credenciales inválidas
                        authResult = authService.authenticateUser(
                            email = "invalid@email.com",
                            password = "123" // Muy corta
                        )

                        isAuthLoading = false
                    }
                },
                onRetry = {
                    scope.launch {
                        isAuthLoading = true
                        authResult = OperationResult.Loading("Reintentando...")

                        // Ejemplo 2: Login con retry automático
                        authResult = authService.authenticateWithRetry(
                            email = "maria.silva@email.com",
                            password = "maria123"
                        )

                        isAuthLoading = false
                    }
                }
            )

            // Sección 2: Errores de red
            ErrorDemoCard(
                title = "🌐 Errores de Red",
                description = "Simula problemas de conectividad y timeouts",
                isLoading = isNetworkLoading,
                result = networkResult,
                onTest = {
                    scope.launch {
                        isNetworkLoading = true
                        networkResult = OperationResult.Loading("Conectando...")

                        // Ejemplo 1: Verificar conectividad
                        val connectivityResult = networkService.checkNetworkConnectivity()

                        if (connectivityResult is OperationResult.Success) {
                            // Ejemplo 2: Fetch con posibles errores de red
                            networkResult = networkService.fetchUserProfile("user123")
                        } else {
                            networkResult = connectivityResult
                        }

                        isNetworkLoading = false
                    }
                },
                onRetry = {
                    scope.launch {
                        isNetworkLoading = true
                        networkResult = OperationResult.Loading("Haciendo ping...")

                        // Ejemplo 3: Ping con retry
                        networkResult = networkService.pingServer("https://api.example.com")

                        isNetworkLoading = false
                    }
                }
            )

            // Sección 3: Operaciones de archivo
            ErrorDemoCard(
                title = "📁 Operaciones de Archivo",
                description = "Manejo de errores en lectura/escritura de archivos",
                isLoading = isFileLoading,
                result = fileResult,
                onTest = {
                    scope.launch {
                        isFileLoading = true
                        fileResult = OperationResult.Loading("Guardando archivo...")

                        // Simular operación de archivo sin Context real
                        fileResult = ExceptionHandler.executeWithCatchSuspend(
                            operation = {
                                // Simular validación de datos
                                val userData = mapOf(
                                    "id" to "123",
                                    "name" to "Test User",
                                    "email" to "test@email.com"
                                )

                                // Simular posibles errores de archivo
                                if (userData.isEmpty()) {
                                    throw AppException.ValidationException("Datos vacíos")
                                }

                                // Simular error de permisos
                                if (kotlin.random.Random.nextInt(100) < 30) {
                                    throw SecurityException("Sin permisos de escritura")
                                }

                                // Simular éxito
                                "Archivo guardado exitosamente"
                            }
                        )

                        isFileLoading = false
                    }
                },
                onRetry = {
                    scope.launch {
                        isFileLoading = true
                        fileResult = OperationResult.Loading("Intentando nuevamente...")

                        // Simular operación crítica
                        fileResult = criticalOperation("Backup de datos") {
                            // Simular operación que puede fallar
                            if (kotlin.random.Random.nextBoolean()) {
                                "Backup completado exitosamente"
                            } else {
                                throw java.io.IOException("Error al crear backup")
                            }
                        }

                        isFileLoading = false
                    }
                }
            )

            // Sección de información
            InfoCard()
        }
    }
}

@Composable
fun ErrorDemoCard(
    title: String,
    description: String,
    isLoading: Boolean,
    result: OperationResult<*>?,
    onTest: () -> Unit,
    onRetry: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (AppPreferences.isDarkTheme) {
                Color(0xFF2E2E2E).copy(alpha = 0.95f)
            } else {
                Color.White.copy(alpha = 0.9f)
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = if (AppPreferences.isDarkTheme) Color(0xFF81C784) else Color(0xFF2E7D32)
            )

            Text(
                text = description,
                fontSize = 14.sp,
                color = if (AppPreferences.isDarkTheme) Color(0xFFB0BEC5) else Color.Gray,
                modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
            )

            // Botones de acción
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onTest,
                    enabled = !isLoading,
                    modifier = Modifier.weight(1f)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = Color.White
                        )
                    } else {
                        Text("Probar")
                    }
                }

                OutlinedButton(
                    onClick = onRetry,
                    enabled = !isLoading && result is OperationResult.Error,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = "Reintentar",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Reintentar")
                }
            }

            // Mostrar resultado
            result?.let { operationResult ->
                Spacer(modifier = Modifier.height(12.dp))

                when (operationResult) {
                    is OperationResult.Loading -> {
                        ResultCard(
                            message = operationResult.message,
                            isError = false,
                            isLoading = true
                        )
                    }
                    is OperationResult.Success -> {
                        ResultCard(
                            message = "✅ Éxito: ${operationResult.data}",
                            isError = false,
                            isLoading = false
                        )
                    }
                    is OperationResult.Error -> {
                        val friendlyMessage = ExceptionHandler.getUserFriendlyMessage(operationResult.exception)
                        ResultCard(
                            message = "❌ Error: $friendlyMessage",
                            isError = true,
                            isLoading = false
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ResultCard(
    message: String,
    isError: Boolean,
    isLoading: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when {
                isLoading -> Color(0xFFF3E5F5)
                isError -> Color(0xFFFFEBEE)
                else -> Color(0xFFE8F5E8)
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color(0xFF7B1FA2)
                )
            } else {
                Icon(
                    imageVector = if (isError) Icons.Default.Error else Icons.Default.Warning,
                    contentDescription = null,
                    tint = if (isError) Color(0xFFD32F2F) else Color(0xFF2E7D32),
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = message,
                fontSize = 14.sp,
                color = if (isError) Color(0xFFD32F2F) else Color(0xFF2E7D32)
            )
        }
    }
}

@Composable
fun InfoCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (AppPreferences.isDarkTheme) {
                Color(0xFF1A237E).copy(alpha = 0.3f)
            } else {
                Color(0xFFE3F2FD).copy(alpha = 0.7f)
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "ℹ️ Información sobre Manejo de Errores",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = if (AppPreferences.isDarkTheme) Color(0xFF64B5F6) else Color(0xFF1976D2)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = """
                • try/catch captura excepciones específicas
                • Retry automático para errores temporales
                • Mensajes amigables para el usuario
                • Logging detallado para debugging
                • Validación antes de operaciones críticas
                • Fallbacks para operaciones fallidas
                """.trimIndent(),
                fontSize = 14.sp,
                color = if (AppPreferences.isDarkTheme) Color(0xFF90CAF9) else Color(0xFF1976D2)
            )
        }
    }
}

@Preview
@Composable
fun ErrorHandlingDemoScreenPreview() {
    ErrorHandlingDemoScreen()
}