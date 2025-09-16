package com.example.exp1_s2_gustavo_renjifo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.exp1_s2_gustavo_renjifo.ui.components.ScreenWithAccessibilityBar
import com.example.exp1_s2_gustavo_renjifo.ui.theme.AppPreferences
import com.example.exp1_s2_gustavo_renjifo.ui.theme.Exp1_S2_gustavo_renjifoTheme
import com.example.exp1_s2_gustavo_renjifo.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(
    onNavigateBack: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    
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
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
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
                    text = "Recuperar Contraseña",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = if (AppPreferences.isDarkTheme) Color.White else Color.Black,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
            
            Text(
                text = "Ingresa tu email",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = if (AppPreferences.isDarkTheme) Color.White else Color.Black,
                modifier = Modifier.padding(bottom = 24.dp)
            )
            
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )
            
            Button(
                onClick = {
                    if (email.isNotEmpty()) {
                        message = "Se enviaron instrucciones a: $email"
                    } else {
                        message = "Ingresa un email válido"
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("ENVIAR INSTRUCCIONES")
            }
            
            if (message.isNotEmpty()) {
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }
    }
}

@Preview
@Composable
fun ForgotPasswordScreenPreview() {
    Exp1_S2_gustavo_renjifoTheme {
        ForgotPasswordScreen(onNavigateBack = {})
    }
}