package com.example.exp1_s2_gustavo_renjifo.ui.screens

import androidx.compose.animation.core.*
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
import androidx.compose.ui.draw.scale
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
import com.example.exp1_s2_gustavo_renjifo.ui.components.AccessibilityButtons
import com.example.exp1_s2_gustavo_renjifo.ui.theme.*

@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    
    // Animación de escala para entrada
    val scaleAnimation = remember { Animatable(0.8f) }

    LaunchedEffect(Unit) {
        scaleAnimation.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
    }

    // Degradado verde coherente
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
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = gradientBrush)
            .padding(16.dp)
            .scale(scaleAnimation.value),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        // Botones de accesibilidad en la parte superior
        AccessibilityButtons()
        
        // Título con sombra sutil y tamaño adaptativo
        Text(
            text = "AccesibilidApp",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = if (AppPreferences.isDarkTheme) GreenDark40 else Color(0xFF2E7D32),
                fontSize = MaterialTheme.typography.headlineMedium.fontSize * AppPreferences.fontSizeScale
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
                // Input field para email con tamaño adaptativo
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = {
                        Text(
                            "Email",
                            fontSize = MaterialTheme.typography.bodyMedium.fontSize * AppPreferences.fontSizeScale
                        )
                    },
                    textStyle = LocalTextStyle.current.copy(
                        fontSize = MaterialTheme.typography.bodyMedium.fontSize * AppPreferences.fontSizeScale
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )
                
                // Input field para contraseña con tamaño adaptativo
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = {
                        Text(
                            "Contraseña",
                            fontSize = MaterialTheme.typography.bodyMedium.fontSize * AppPreferences.fontSizeScale
                        )
                    },
                    textStyle = LocalTextStyle.current.copy(
                        fontSize = MaterialTheme.typography.bodyMedium.fontSize * AppPreferences.fontSizeScale
                    ),
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
                
                // Botón de login con animación y colores coherentes
                var buttonPressed by remember { mutableStateOf(false) }
                val buttonScale by animateFloatAsState(
                    targetValue = if (buttonPressed) 0.95f else 1f,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                    label = "buttonScale"
                )

                Button(
                    onClick = {
                        buttonPressed = true
                        // Acceso directo sin validación
                        onLoginSuccess()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .scale(buttonScale),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GreenAccent40
                    )
                ) {
                    Text(
                        "INICIAR SESIÓN",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = MaterialTheme.typography.labelLarge.fontSize * AppPreferences.fontSizeScale
                        )
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Vínculos de navegación con colores coherentes de la paleta verde
        ClickableText(
            text = AnnotatedString("¿No tienes cuenta? Regístrate"),
            onClick = { onNavigateToRegister() },
            style = TextStyle(
                color = if (AppPreferences.isDarkTheme) GreenAccentDark40 else Green40,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp * AppPreferences.fontSizeScale
            ),
            modifier = Modifier.padding(bottom = 12.dp)
        )

        ClickableText(
            text = AnnotatedString("¿Olvidaste tu contraseña?"),
            onClick = { onNavigateToForgotPassword() },
            style = TextStyle(
                color = if (AppPreferences.isDarkTheme) GreenGreyDark40 else GreenGrey40,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp * AppPreferences.fontSizeScale
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