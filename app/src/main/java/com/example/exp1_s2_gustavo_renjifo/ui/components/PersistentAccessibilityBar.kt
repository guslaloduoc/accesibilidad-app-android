package com.example.exp1_s2_gustavo_renjifo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.TextDecrease
import androidx.compose.material.icons.filled.TextIncrease
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.exp1_s2_gustavo_renjifo.ui.theme.AppPreferences

@Composable
fun PersistentAccessibilityBar() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .zIndex(10f) // Asegurar que esté por encima de todo
            .shadow(4.dp),
        color = if (AppPreferences.isDarkTheme) {
            Color(0xFF2E2E2E)
        } else {
            Color(0xFFF8F9FA)
        },
        tonalElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Botón modo oscuro/claro
            AccessibilityButton(
                onClick = { AppPreferences.toggleDarkTheme() },
                icon = if (AppPreferences.isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                contentDescription = if (AppPreferences.isDarkTheme) "Modo Claro" else "Modo Oscuro",
                color = if (AppPreferences.isDarkTheme) Color(0xFFFFC107) else Color(0xFF5E35B1)
            )

            // Botón aumentar texto
            AccessibilityButton(
                onClick = { AppPreferences.increaseFontSize() },
                icon = Icons.Default.TextIncrease,
                contentDescription = "Aumentar Texto",
                color = Color(0xFF2E7D32),
                enabled = AppPreferences.fontSizeScale < 1.5f
            )

            // Botón disminuir texto
            AccessibilityButton(
                onClick = { AppPreferences.decreaseFontSize() },
                icon = Icons.Default.TextDecrease,
                contentDescription = "Disminuir Texto",
                color = Color(0xFFD32F2F),
                enabled = AppPreferences.fontSizeScale > 0.8f
            )

            // Botón resetear texto
            AccessibilityButton(
                onClick = { AppPreferences.resetFontSize() },
                text = "A",
                contentDescription = "Resetear Tamaño",
                color = Color(0xFF1976D2),
                enabled = AppPreferences.fontSizeScale != 1.0f
            )
        }
    }
}

@Composable
private fun AccessibilityButton(
    onClick: () -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    text: String? = null,
    contentDescription: String,
    color: Color,
    enabled: Boolean = true
) {
    IconButton(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .size(48.dp)
            .background(
                color = if (enabled) color.copy(alpha = 0.1f) else Color.Gray.copy(alpha = 0.1f),
                shape = RoundedCornerShape(12.dp)
            )
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = if (enabled) color else Color.Gray,
                modifier = Modifier.size(24.dp)
            )
        } else if (text != null) {
            Text(
                text = text,
                color = if (enabled) color else Color.Gray,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
fun ScreenWithAccessibilityBar(
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Barra de accesibilidad siempre en la parte superior
        PersistentAccessibilityBar()

        // Contenido de la pantalla
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 4.dp) // Pequeño espacio entre la barra y el contenido
        ) {
            content()
        }
    }
}