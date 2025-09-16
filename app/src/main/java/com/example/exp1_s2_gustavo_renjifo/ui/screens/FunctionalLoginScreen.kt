package com.example.exp1_s2_gustavo_renjifo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.exp1_s2_gustavo_renjifo.data.UserRepository
import com.example.exp1_s2_gustavo_renjifo.utils.*

@Composable
fun FunctionalLoginScreen(
    onNavigateToRegister: () -> Unit = {},
    onNavigateToForgotPassword: () -> Unit = {},
    onLoginSuccess: (String) -> Unit = {}
) {
    // Estado del formulario usando utilidades funcionales
    val formState = rememberFormState()

    // Estados validados usando funciones de orden superior
    var email by rememberValidatedState(
        initialValue = "",
        validator = { it.isValidEmail() },
        onValidationFailed = { /* Manejar error de validación */ }
    )

    var password by rememberValidatedState(
        initialValue = "",
        validator = { it.isValidPassword() },
        onValidationFailed = { /* Manejar error de validación */ }
    )

    // Estado de carga para autenticación
    var loginState by remember { mutableStateOf<LoadingState<String>>(LoadingState.Idle) }

    // Estado derivado para validación del formulario
    val isFormValid by remember {
        derivedStateOf {
            email.isValidEmail() && password.isValidPassword()
        }
    }

    // Función de autenticación usando lambdas
    val performLogin: () -> Unit = {
        loginState = LoadingState.Loading

        UserRepository.authenticateUser(
            identifier = email,
            password = password,
            onSuccess = { user ->
                loginState = LoadingState.Success("Bienvenido ${user.fullName}")
                onLoginSuccess(user.fullName)
            },
            onError = { error ->
                loginState = LoadingState.Error(error)
            }
        )
    }

    // Degradado verde usando funciones predefinidas
    val gradientBrush = remember {
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

        // Título
        Text(
            text = "AccesibilidApp",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2E7D32),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // Card con campos
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.85f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Email con validación en tiempo real
                OutlinedTextField(
                    value = email,
                    onValueChange = { newEmail ->
                        email = newEmail
                        // Usar lambda para validación inmediata
                        newEmail.ifNotEmpty { validatedEmail ->
                            // Aquí se puede agregar lógica adicional
                        }
                    },
                    label = { Text("Email") },
                    isError = email.isNotEmpty() && !email.isValidEmail(),
                    supportingText = {
                        if (email.isNotEmpty() && !email.isValidEmail()) {
                            Text(
                                text = "Email inválido",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )

                // Password con validación
                OutlinedTextField(
                    value = password,
                    onValueChange = { newPassword ->
                        password = newPassword
                    },
                    label = { Text("Contraseña") },
                    isError = password.isNotEmpty() && !password.isValidPassword(),
                    supportingText = {
                        if (password.isNotEmpty() && !password.isValidPassword()) {
                            Text(
                                text = "Mínimo 6 caracteres",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )

                // Botón login con estado de carga
                Button(
                    onClick = performLogin,
                    enabled = isFormValid && loginState !is LoadingState.Loading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50),
                        disabledContainerColor = Color(0xFF4CAF50).copy(alpha = 0.5f)
                    )
                ) {
                    when (loginState) {
                        is LoadingState.Loading -> {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White
                            )
                        }
                        else -> {
                            Text(
                                "INICIAR SESIÓN",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Mostrar estado de login usando lambda
                loginState.onError { errorMessage ->
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                loginState.onSuccess { successMessage ->
                    Text(
                        text = successMessage,
                        color = Color(0xFF2E7D32),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Enlaces usando funciones de orden superior para navegación
        val createClickableText: (String, () -> Unit, Color) -> @Composable () -> Unit = { text, action, color ->
            {
                Text(
                    text = text,
                    color = color,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    modifier = Modifier
                        .clickable { action() }
                        .padding(vertical = 6.dp)
                )
            }
        }

        // Aplicar las funciones de navegación
        createClickableText(
            "¿No tienes cuenta? Regístrate",
            onNavigateToRegister,
            Color(0xFF388E3C)
        )()

        createClickableText(
            "¿Olvidaste tu contraseña?",
            onNavigateToForgotPassword,
            Color(0xFF66BB6A)
        )()

        // Información adicional usando funciones de análisis
        LaunchedEffect(Unit) {
            val stats = UserRepository.getUserStatistics()
            // Usar las estadísticas si es necesario
        }
    }
}

@Preview
@Composable
fun FunctionalLoginScreenPreview() {
    FunctionalLoginScreen()
}