package com.example.exp1_s2_gustavo_renjifo.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.TextFormat
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.exp1_s2_gustavo_renjifo.ui.theme.AppPreferences

@Composable
fun AccessibilityButtons() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Botón modo oscuro/claro
        FloatingActionButton(
            onClick = { AppPreferences.toggleDarkTheme() },
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                imageVector = if (AppPreferences.isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                contentDescription = if (AppPreferences.isDarkTheme) "Modo Claro" else "Modo Oscuro"
            )
        }

        // Botón aumentar texto
        FloatingActionButton(
            onClick = { AppPreferences.increaseFontSize() },
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                imageVector = Icons.Default.TextFormat,
                contentDescription = "Aumentar Texto"
            )
        }

        // Botón resetear texto (mantener presionado)
        FloatingActionButton(
            onClick = { AppPreferences.resetFontSize() },
            modifier = Modifier.size(48.dp)
        ) {
            Text(
                text = "A",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}