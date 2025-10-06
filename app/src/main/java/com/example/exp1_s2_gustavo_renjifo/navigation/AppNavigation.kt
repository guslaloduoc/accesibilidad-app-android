package com.example.exp1_s2_gustavo_renjifo.navigation

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.exp1_s2_gustavo_renjifo.services.AuthRepository
import com.example.exp1_s2_gustavo_renjifo.services.ServiceLocator
import com.example.exp1_s2_gustavo_renjifo.ui.components.ScreenWithAccessibilityBar
import com.example.exp1_s2_gustavo_renjifo.ui.screens.HomeScreen
import com.example.exp1_s2_gustavo_renjifo.ui.screens.HomeViewModel
import com.example.exp1_s2_gustavo_renjifo.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun AppNavigation(navController: NavHostController) {
    val authRepository = ServiceLocator.getAuthRepository()
    val messageRepository = ServiceLocator.getMessageRepository()

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            SimpleLoginScreenWithAuth(
                authRepository = authRepository,
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                onNavigateToForgotPassword = {
                    navController.navigate(Screen.ForgotPassword.route)
                },
                onLoginSuccess = { userId ->
                    navController.navigate(Screen.Home.createRoute(userId)) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreenWithAuth(
                authRepository = authRepository,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onRegisterSuccess = { userId ->
                    navController.navigate(Screen.Home.createRoute(userId)) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.ForgotPassword.route) {
            ForgotPasswordScreenWithAuth(
                authRepository = authRepository,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.Home.route,
            arguments = listOf(navArgument("userId") { type = NavType.IntType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getInt("userId") ?: 0
            val viewModel = HomeViewModel(userId, messageRepository)

            HomeScreen(
                userId = userId,
                viewModel = viewModel,
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
    }
}

@Composable
fun SimpleLoginScreenWithAuth(
    authRepository: AuthRepository,
    onNavigateToRegister: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
    onLoginSuccess: (Int) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    ScreenWithAccessibilityBar {
        val gradientBrush = if (AppPreferences.isDarkTheme) {
            Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF1B5E20),
                    Color(0xFF2E7D32),
                    Color(0xFF388E3C)
                )
            )
        } else {
            Brush.verticalGradient(
                colors = listOf(
                    Color(0xFFE8F5E8),
                    Color(0xFFC8E6C9),
                    Color(0xFFA5D6A7)
                )
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(brush = gradientBrush)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "AccesibilidApp",
                fontSize = (32.sp * AppPreferences.fontSizeScale),
                fontWeight = FontWeight.Bold,
                color = if (AppPreferences.isDarkTheme)
                    Color(0xFF81C784) else Color(0xFF2E7D32),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (AppPreferences.isDarkTheme) {
                        Color(0xFF2E2E2E).copy(alpha = 0.95f)
                    } else {
                        Color.White.copy(alpha = 0.85f)
                    }
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it; errorMessage = "" },
                        label = { Text("Email", fontSize = (14.sp * AppPreferences.fontSizeScale)) },
                        textStyle = LocalTextStyle.current.copy(fontSize = (16.sp * AppPreferences.fontSizeScale)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        enabled = !isLoading
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it; errorMessage = "" },
                        label = { Text("Contraseña", fontSize = (14.sp * AppPreferences.fontSizeScale)) },
                        textStyle = LocalTextStyle.current.copy(fontSize = (16.sp * AppPreferences.fontSizeScale)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        enabled = !isLoading
                    )

                    if (errorMessage.isNotEmpty()) {
                        Text(
                            text = errorMessage,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = (14.sp * AppPreferences.fontSizeScale),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    Button(
                        onClick = {
                            coroutineScope.launch {
                                isLoading = true
                                authRepository.login(email, password).fold(
                                    onSuccess = { userId -> onLoginSuccess(userId) },
                                    onFailure = { error -> errorMessage = error.message ?: "Error de autenticación" }
                                )
                                isLoading = false
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4CAF50)
                        ),
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White
                            )
                        } else {
                            Text(
                                "INICIAR SESIÓN",
                                fontWeight = FontWeight.Bold,
                                fontSize = (16.sp * AppPreferences.fontSizeScale)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "¿No tienes cuenta? Regístrate",
                color = if (AppPreferences.isDarkTheme)
                    Color(0xFF81C784) else Color(0xFF388E3C),
                fontWeight = FontWeight.Medium,
                fontSize = (16.sp * AppPreferences.fontSizeScale),
                modifier = Modifier
                    .clickable { onNavigateToRegister() }
                    .padding(bottom = 12.dp)
            )

            Text(
                text = "¿Olvidaste tu contraseña?",
                color = if (AppPreferences.isDarkTheme)
                    Color(0xFFA5D6A7) else Color(0xFF66BB6A),
                fontWeight = FontWeight.Medium,
                fontSize = (16.sp * AppPreferences.fontSizeScale),
                modifier = Modifier.clickable { onNavigateToForgotPassword() }
            )
        }
    }
}

@Composable
fun RegisterScreenWithAuth(
    authRepository: AuthRepository,
    onNavigateBack: () -> Unit,
    onRegisterSuccess: (Int) -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var selectedOption by remember { mutableStateOf("opcion1") }
    var checkbox1 by remember { mutableStateOf(false) }
    var checkbox2 by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val scaleAnimation = remember { Animatable(0.9f) }

    LaunchedEffect(Unit) {
        scaleAnimation.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
    }

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
                .verticalScroll(rememberScrollState())
                .scale(scaleAnimation.value)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Atrás",
                        tint = if (AppPreferences.isDarkTheme)
                            Color.White else Color.Black
                    )
                }
                Text(
                    text = "Registro",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = if (AppPreferences.isDarkTheme)
                        Color.White else Color.Black,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Text(
                text = "Crear Cuenta",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = if (AppPreferences.isDarkTheme)
                    Color.White else Color.Black
            )

            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it; errorMessage = "" },
                label = { Text("Nombre completo") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it; errorMessage = "" },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it; errorMessage = "" },
                label = { Text("Contraseña") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            )

            Text(
                text = "Tipo de asistencia:",
                style = MaterialTheme.typography.titleMedium,
                color = if (AppPreferences.isDarkTheme)
                    Color.White else Color.Black
            )

            val radioOptions = listOf(
                "opcion1" to "Lector de pantalla",
                "opcion2" to "Alto contraste",
                "opcion3" to "Comandos de voz"
            )

            radioOptions.forEach { (value, label) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = selectedOption == value,
                            onClick = { selectedOption = value },
                            role = Role.RadioButton
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedOption == value,
                        onClick = { selectedOption = value },
                        enabled = !isLoading
                    )
                    Text(
                        text = label,
                        modifier = Modifier.padding(start = 8.dp),
                        color = if (AppPreferences.isDarkTheme)
                            Color.White else Color.Black
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = checkbox1,
                    onCheckedChange = { checkbox1 = it },
                    enabled = !isLoading
                )
                Text(
                    text = "Acepto términos y condiciones",
                    modifier = Modifier.padding(start = 8.dp),
                    color = if (AppPreferences.isDarkTheme)
                        Color.White else Color.Black
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = checkbox2,
                    onCheckedChange = { checkbox2 = it },
                    enabled = !isLoading
                )
                Text(
                    text = "Recibir notificaciones",
                    modifier = Modifier.padding(start = 8.dp),
                    color = if (AppPreferences.isDarkTheme)
                        Color.White else Color.Black
                )
            }

            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            Button(
                onClick = {
                    coroutineScope.launch {
                        if (fullName.isEmpty() || email.isEmpty() || password.isEmpty()) {
                            errorMessage = "Todos los campos son obligatorios"
                            return@launch
                        }
                        isLoading = true
                        authRepository.signUp(email, password).fold(
                            onSuccess = { userId -> onRegisterSuccess(userId) },
                            onFailure = { error -> errorMessage = error.message ?: "Error al registrar" }
                        )
                        isLoading = false
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text("REGISTRARSE")
            }
        }
    }
}

@Composable
fun ForgotPasswordScreenWithAuth(
    authRepository: AuthRepository,
    onNavigateBack: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    ScreenWithAccessibilityBar {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Recuperar Contraseña",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(bottom = 24.dp)
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it; message = "" },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            )

            if (message.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = message,
                    color = if (isError) MaterialTheme.colorScheme.error
                           else MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    coroutineScope.launch {
                        isLoading = true
                        val result = authRepository.recover(email)
                        result.fold(
                            onSuccess = {
                                message = "Se ha enviado un correo de recuperación"
                                isError = false
                            },
                            onFailure = { error ->
                                message = error.message ?: "Error al recuperar contraseña"
                                isError = true
                            }
                        )
                        isLoading = false
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text("Recuperar")
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = onNavigateBack,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Volver")
            }
        }
    }
}
