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
import com.example.exp1_s2_gustavo_renjifo.ui.components.ScreenWithAccessibilityBar
import com.example.exp1_s2_gustavo_renjifo.ui.theme.AppPreferences

@Composable
fun SimpleLoginScreen(
    onNavigateToRegister: () -> Unit = {},
    onNavigateToForgotPassword: () -> Unit = {},
    onLoginSuccess: () -> Unit = {}
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Usar el wrapper con barra de accesibilidad
    ScreenWithAccessibilityBar {
        // Degradado verde adaptativo según el tema
        val gradientBrush = if (AppPreferences.isDarkTheme) {
            Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF1B5E20),  // Verde oscuro
                    Color(0xFF2E7D32),  // Verde medio oscuro
                    Color(0xFF388E3C)   // Verde más claro
                )
            )
        } else {
            Brush.verticalGradient(
                colors = listOf(
                    Color(0xFFE8F5E8),  // Verde pastel claro
                    Color(0xFFC8E6C9),  // Verde pastel medio
                    Color(0xFFA5D6A7)   // Verde pastel más intenso
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

            // Título adaptativo
            Text(
                text = "AccesibilidApp",
                fontSize = (32.sp * AppPreferences.fontSizeScale),
                fontWeight = FontWeight.Bold,
                color = if (AppPreferences.isDarkTheme) Color(0xFF81C784) else Color(0xFF2E7D32),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // Card con campos adaptativo al tema
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
                // Email con tamaño adaptativo
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = {
                        Text(
                            "Email",
                            fontSize = (14.sp * AppPreferences.fontSizeScale)
                        )
                    },
                    textStyle = LocalTextStyle.current.copy(
                        fontSize = (16.sp * AppPreferences.fontSizeScale)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )

                // Password con tamaño adaptativo
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = {
                        Text(
                            "Contraseña",
                            fontSize = (14.sp * AppPreferences.fontSizeScale)
                        )
                    },
                    textStyle = LocalTextStyle.current.copy(
                        fontSize = (16.sp * AppPreferences.fontSizeScale)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )

                // Botón login adaptativo
                Button(
                    onClick = { onLoginSuccess() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50)
                    )
                ) {
                    Text(
                        "INICIAR SESIÓN",
                        fontWeight = FontWeight.Bold,
                        fontSize = (16.sp * AppPreferences.fontSizeScale)
                    )
                }
            }
        }

            Spacer(modifier = Modifier.height(16.dp))

            // Enlaces adaptativos
            Text(
                text = "¿No tienes cuenta? Regístrate",
                color = if (AppPreferences.isDarkTheme) Color(0xFF81C784) else Color(0xFF388E3C),
                fontWeight = FontWeight.Medium,
                fontSize = (16.sp * AppPreferences.fontSizeScale),
                modifier = Modifier
                    .clickable { onNavigateToRegister() }
                    .padding(bottom = 12.dp)
            )

            Text(
                text = "¿Olvidaste tu contraseña?",
                color = if (AppPreferences.isDarkTheme) Color(0xFFA5D6A7) else Color(0xFF66BB6A),
                fontWeight = FontWeight.Medium,
                fontSize = (16.sp * AppPreferences.fontSizeScale),
                modifier = Modifier.clickable { onNavigateToForgotPassword() }
            )
        }
    }
}

@Preview
@Composable
fun SimpleLoginScreenPreview() {
    SimpleLoginScreen()
}