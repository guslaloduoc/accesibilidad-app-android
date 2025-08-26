package com.example.exp1_s2_gustavo_renjifo.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object ForgotPassword : Screen("forgot_password")
    object Success : Screen("success")
}