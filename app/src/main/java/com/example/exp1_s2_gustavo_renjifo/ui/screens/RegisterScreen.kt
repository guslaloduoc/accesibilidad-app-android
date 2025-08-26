package com.example.exp1_s2_gustavo_renjifo.ui.screens

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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.exp1_s2_gustavo_renjifo.data.User
import com.example.exp1_s2_gustavo_renjifo.data.UserRepository
import com.example.exp1_s2_gustavo_renjifo.ui.theme.Exp1_S2_gustavo_renjifoTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onNavigateBack: () -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var selectedOption by remember { mutableStateOf("opcion1") }
    var checkbox1 by remember { mutableStateOf(false) }
    var checkbox2 by remember { mutableStateOf(false) }
    var successMessage by remember { mutableStateOf("") }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        TopAppBar(
            title = { Text("Registro") },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                }
            }
        )
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            
            Text(
                text = "Crear Cuenta",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            )
            
            // Input fields
            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                label = { Text("Nombre completo") },
                modifier = Modifier.fillMaxWidth()
            )
            
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )
            
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                modifier = Modifier.fillMaxWidth()
            )
            
            // Radio buttons
            Text(
                text = "Tipo de asistencia:",
                style = MaterialTheme.typography.titleMedium
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
                        onClick = { selectedOption = value }
                    )
                    Text(
                        text = label,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
            
            // Checkboxes
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = checkbox1,
                    onCheckedChange = { checkbox1 = it }
                )
                Text(
                    text = "Acepto términos y condiciones",
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
            
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = checkbox2,
                    onCheckedChange = { checkbox2 = it }
                )
                Text(
                    text = "Recibir notificaciones",
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
            
            // Botón
            Button(
                onClick = {
                    if (fullName.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                        val newUser = User(
                            id = (UserRepository.getAllUsers().size + 1).toString(),
                            username = email.substringBefore("@"),
                            email = email,
                            password = password,
                            fullName = fullName
                        )
                        UserRepository.addUser(newUser)
                        successMessage = "Usuario registrado: $fullName"
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("REGISTRARSE")
            }
            
            if (successMessage.isNotEmpty()) {
                Text(
                    text = successMessage,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }
    }
}

@Preview
@Composable
fun RegisterScreenPreview() {
    Exp1_S2_gustavo_renjifoTheme {
        RegisterScreen(onNavigateBack = {})
    }
}