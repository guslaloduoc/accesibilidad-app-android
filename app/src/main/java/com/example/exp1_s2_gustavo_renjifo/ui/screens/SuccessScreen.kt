package com.example.exp1_s2_gustavo_renjifo.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.exp1_s2_gustavo_renjifo.data.UserRepository
import com.example.exp1_s2_gustavo_renjifo.ui.theme.Exp1_S2_gustavo_renjifoTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuccessScreen(
    onNavigateBack: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val users = UserRepository.getAllUsers()
    
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // TopAppBar con botón de volver
        TopAppBar(
            title = { 
                Text(
                    "Acceso Exitoso",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                ) 
            },
            navigationIcon = {
                IconButton(
                    onClick = onNavigateBack
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Volver"
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color(0xFF4CAF50),
                titleContentColor = Color.White,
                navigationIconContentColor = Color.White
            )
        )
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            
            Text(
                text = "¡ACCESO EXITOSO!",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            // Botón de cerrar sesión
            Button(
                onClick = onLogout,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFF44336)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ExitToApp,
                    contentDescription = "Cerrar sesión",
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    "CERRAR SESIÓN",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }
            
            Text(
                text = "Lista de Usuarios (${users.size})",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(users) { user ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = user.fullName,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                text = "Email: ${user.email}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                            Text(
                                text = "Usuario: ${user.username}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                            if (user.phoneNumber.isNotEmpty()) {
                                Text(
                                    text = "Teléfono: ${user.phoneNumber}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun SuccessScreenPreview() {
    Exp1_S2_gustavo_renjifoTheme {
        SuccessScreen()
    }
}