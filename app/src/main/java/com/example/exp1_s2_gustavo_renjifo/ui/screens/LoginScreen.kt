package com.example.exp1_s2_gustavo_renjifo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.exp1_s2_gustavo_renjifo.data.UserRepository
import com.example.exp1_s2_gustavo_renjifo.ui.theme.Exp1_S2_gustavo_renjifoTheme

@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    
    // Degradado pastel bonito
    val gradientBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFE3F2FD), // Azul pastel claro
            Color(0xFFF3E5F5), // Rosa pastel claro
            Color(0xFFE8F5E8)  // Verde pastel claro
        )
    )
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = gradientBrush)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        
        // Título con sombra sutil
        Text(
            text = "AccesibilidApp",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2E7D32) // Verde más oscuro para contraste
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 32.dp)
        )
        
        // Card con fondo semi-transparente para los campos
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
                // Input field para email
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )
                
                // Input field para contraseña  
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Contraseña") },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = "Toggle password"
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )
                
                // Botón de login
                Button(
                    onClick = {
                        // Acceso directo sin validación
                        onLoginSuccess()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50) // Verde bonito
                    )
                ) {
                    Text(
                        "INICIAR SESIÓN",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Vínculos de navegación con mejor estilo
        ClickableText(
            text = AnnotatedString("¿No tienes cuenta? Regístrate"),
            onClick = { onNavigateToRegister() },
            style = TextStyle(
                color = Color(0xFF1976D2), // Azul bonito
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp
            ),
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        ClickableText(
            text = AnnotatedString("¿Olvidaste tu contraseña?"),
            onClick = { onNavigateToForgotPassword() },
            style = TextStyle(
                color = Color(0xFF7B1FA2), // Púrpura bonito
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp
            )
        )
    }
}

@Preview
@Composable
fun LoginScreenPreview() {
    Exp1_S2_gustavo_renjifoTheme {
        LoginScreen(
            onNavigateToRegister = {},
            onNavigateToForgotPassword = {},
            onLoginSuccess = {}
        )
    }
}