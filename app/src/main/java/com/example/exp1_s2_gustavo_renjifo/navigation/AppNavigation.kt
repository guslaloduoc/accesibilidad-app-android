package com.example.exp1_s2_gustavo_renjifo.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.exp1_s2_gustavo_renjifo.ui.screens.ForgotPasswordScreen
import com.example.exp1_s2_gustavo_renjifo.ui.screens.LoginScreen
import com.example.exp1_s2_gustavo_renjifo.ui.screens.RegisterScreen
import com.example.exp1_s2_gustavo_renjifo.ui.screens.SuccessScreen

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                onNavigateToForgotPassword = {
                    navController.navigate(Screen.ForgotPassword.route)
                },
                onLoginSuccess = {
                    navController.navigate(Screen.Success.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.Register.route) {
            RegisterScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(Screen.ForgotPassword.route) {
            ForgotPasswordScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(Screen.Success.route) {
            SuccessScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
    }
}