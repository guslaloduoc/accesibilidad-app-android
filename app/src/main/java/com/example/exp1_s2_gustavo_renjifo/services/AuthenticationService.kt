package com.example.exp1_s2_gustavo_renjifo.services

import com.example.exp1_s2_gustavo_renjifo.data.User
import com.example.exp1_s2_gustavo_renjifo.data.UserRepository
import com.example.exp1_s2_gustavo_renjifo.utils.*
import kotlinx.coroutines.delay
import kotlin.random.Random

/**
 * Servicio de autenticación con manejo de excepciones try/catch
 */
class AuthenticationService {

    // Simular operaciones de autenticación con posibles errores
    suspend fun authenticateUser(email: String, password: String): OperationResult<User> {
        return ExceptionHandler.executeWithCatchSuspend(
            operation = {
                // Simular delay de red
                simulateNetworkDelay()

                // Validaciones básicas con try/catch
                validateCredentials(email, password)

                // Simulación de posibles errores de red
                simulateNetworkErrors()

                // Buscar usuario en el repositorio
                val user = findUserWithExceptionHandling(email)
                    ?: throw AppException.AuthenticationException("Usuario no encontrado")

                // Verificar contraseña
                if (!verifyPassword(user.password, password)) {
                    throw AppException.AuthenticationException("Contraseña incorrecta")
                }

                // Simular validación de cuenta activa
                validateUserAccount(user)

                user
            },
            onError = { exception ->
                // Log personalizado para autenticación
                when (exception) {
                    is AppException.AuthenticationException -> {
                        println("🔐 Error de autenticación: ${exception.message}")
                    }
                    is AppException.NetworkException -> {
                        println("🌐 Error de red durante autenticación: ${exception.message}")
                    }
                    else -> {
                        println("❌ Error inesperado en autenticación: ${exception.message}")
                    }
                }
            }
        )
    }

    // Autenticación con retry automático
    suspend fun authenticateWithRetry(email: String, password: String): OperationResult<User> {
        return ExceptionHandler.retryWithBackoff(
            maxRetries = 3,
            initialDelayMs = 1000
        ) {
            // Validar credenciales primero (sin retry si fallan)
            try {
                validateCredentials(email, password)
            } catch (e: Exception) {
                throw AppException.ValidationException(e.message ?: "Credenciales inválidas")
            }

            // Simular operación de red que puede fallar
            simulateUnstableNetwork()

            // Buscar y autenticar usuario
            val user = UserRepository.findUserByEmailOrUsername(email)
                ?: throw AppException.AuthenticationException("Usuario no encontrado")

            if (user.password != password) {
                throw AppException.AuthenticationException("Contraseña incorrecta")
            }

            user
        }
    }

    // Validación de credenciales con manejo de excepciones
    private fun validateCredentials(email: String, password: String) {
        try {
            // Validar email
            if (email.isBlank()) {
                throw IllegalArgumentException("El email no puede estar vacío")
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                throw IllegalArgumentException("Formato de email inválido")
            }

            // Validar contraseña
            if (password.isBlank()) {
                throw IllegalArgumentException("La contraseña no puede estar vacía")
            }

            if (password.length < 6) {
                throw IllegalArgumentException("La contraseña debe tener al menos 6 caracteres")
            }

        } catch (e: IllegalArgumentException) {
            throw AppException.ValidationException(e.message ?: "Datos inválidos")
        }
    }

    // Buscar usuario con manejo de excepciones
    private fun findUserWithExceptionHandling(email: String): User? {
        return try {
            UserRepository.findUserByEmailOrUsername(email)
        } catch (e: Exception) {
            throw AppException.DatabaseException("Error al buscar usuario en la base de datos", e)
        }
    }

    // Verificar contraseña con manejo de excepciones
    private fun verifyPassword(storedPassword: String, providedPassword: String): Boolean {
        return try {
            // En una app real, aquí habría hashing/encryption
            storedPassword == providedPassword
        } catch (e: Exception) {
            throw AppException.AuthenticationException("Error al verificar contraseña")
        }
    }

    // Validar estado de la cuenta
    private fun validateUserAccount(user: User) {
        try {
            // Simulación de validaciones de cuenta
            if (user.email.isEmpty()) {
                throw IllegalStateException("Cuenta en estado inválido")
            }

            // Simular validación de cuenta bloqueada (ejemplo práctico)
            if (user.email.contains("blocked")) {
                throw IllegalStateException("Cuenta bloqueada")
            }

        } catch (e: IllegalStateException) {
            throw AppException.AuthenticationException("Cuenta no disponible: ${e.message}")
        }
    }

    // Simulaciones para ejemplos prácticos

    private suspend fun simulateNetworkDelay() {
        try {
            delay(Random.nextLong(500, 2000)) // Simular delay de red variable
        } catch (e: Exception) {
            // En caso de que la coroutina sea cancelada
            throw AppException.NetworkException("Operación cancelada")
        }
    }

    private fun simulateNetworkErrors() {
        // Simular errores de red aleatorios para demostración
        val random = Random.nextInt(100)
        when {
            random < 5 -> throw java.net.UnknownHostException("No internet connection")
            random < 10 -> throw java.net.SocketTimeoutException("Request timeout")
            random < 15 -> throw java.net.ConnectException("Connection refused")
        }
    }

    private suspend fun simulateUnstableNetwork() {
        // Simular red inestable para demostrar retry
        delay(Random.nextLong(100, 500))

        if (Random.nextBoolean()) {
            throw java.net.SocketTimeoutException("Network timeout")
        }
    }

    // Logout con manejo de excepciones
    fun logout(): OperationResult<Unit> {
        return ExceptionHandler.executeWithCatch(
            operation = {
                try {
                    // Simular limpieza de sesión
                    clearUserSession()

                    // Simular limpieza de cache
                    clearCache()

                    println("✅ Logout exitoso")

                } catch (e: SecurityException) {
                    throw AppException.AuthenticationException("Error al cerrar sesión de forma segura")
                } catch (e: Exception) {
                    throw AppException.UnknownException("Error inesperado durante logout", e)
                }
            }
        )
    }

    private fun clearUserSession() {
        try {
            // Simular limpieza de tokens, preferencias, etc.
            // En una app real aquí irían operaciones como:
            // - Limpiar SharedPreferences
            // - Invalidar tokens
            // - Limpiar variables de sesión
        } catch (e: Exception) {
            throw SecurityException("No se pudo limpiar la sesión de forma segura")
        }
    }

    private fun clearCache() {
        try {
            // Simular limpieza de cache
            // En una app real aquí irían operaciones como:
            // - Limpiar cache de imágenes
            // - Limpiar cache de datos
            // - Limpiar archivos temporales
        } catch (e: Exception) {
            // Log pero no fallar por esto
            println("⚠️ Advertencia: No se pudo limpiar el cache completamente")
        }
    }

    // Verificar si el usuario está autenticado
    fun isUserAuthenticated(): OperationResult<Boolean> {
        return ExceptionHandler.executeWithCatch(
            operation = {
                try {
                    // Simular verificación de token/sesión
                    val hasValidSession = checkValidSession()
                    val hasValidToken = checkValidToken()

                    hasValidSession && hasValidToken

                } catch (e: SecurityException) {
                    throw AppException.AuthenticationException("Sesión inválida")
                } catch (e: Exception) {
                    // En caso de duda, asumir no autenticado
                    false
                }
            }
        )
    }

    private fun checkValidSession(): Boolean {
        try {
            // Simular verificación de sesión
            return true // Simplificado para el ejemplo
        } catch (e: Exception) {
            throw SecurityException("No se pudo verificar la sesión")
        }
    }

    private fun checkValidToken(): Boolean {
        try {
            // Simular verificación de token
            return true // Simplificado para el ejemplo
        } catch (e: Exception) {
            throw SecurityException("Token inválido")
        }
    }
}