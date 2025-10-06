// app/src/main/java/com/example/exp1_s2_gustavo_renjifo/navigation/Screen.kt
package com.example.exp1_s2_gustavo_renjifo.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object ForgotPassword : Screen("forgot_password")
    object Home : Screen("home/{userId}") {
        fun createRoute(userId: Int) = "home/$userId"
    }
    object Escribir : Screen("escribir")
    object Hablar : Screen("hablar")
    object BuscarDispositivo : Screen("buscar_dispositivo")
}
