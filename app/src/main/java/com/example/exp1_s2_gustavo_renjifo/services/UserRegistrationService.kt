package com.example.exp1_s2_gustavo_renjifo.services

import com.example.exp1_s2_gustavo_renjifo.data.User
import com.example.exp1_s2_gustavo_renjifo.data.UserRepository
import com.example.exp1_s2_gustavo_renjifo.utils.*
import kotlinx.coroutines.delay
import kotlin.random.Random

/**
 * Servicio de registro de usuarios con manejo completo de excepciones
 */
class UserRegistrationService {

    // Registro de usuario con validaciones y manejo de errores
    suspend fun registerUser(
        fullName: String,
        email: String,
        username: String,
        password: String,
        phoneNumber: String
    ): OperationResult<User> {

        return ExceptionHandler.executeWithCatchSuspend(
            operation = {
                // 1. Validar datos de entrada
                validateRegistrationData(fullName, email, username, password, phoneNumber)

                // 2. Verificar disponibilidad
                checkUserAvailability(email, username)

                // 3. Simular delay de operación
                delay(Random.nextLong(1000, 3000))

                // 4. Simular posibles errores durante el registro
                simulateRegistrationErrors()

                // 5. Crear y guardar usuario
                val newUser = createUser(fullName, email, username, password, phoneNumber)
                saveUserToDatabase(newUser)

                // 6. Enviar email de confirmación (simulado)
                sendConfirmationEmail(newUser)

                newUser
            }
        )
    }

    // Validación completa de datos de registro
    private fun validateRegistrationData(
        fullName: String,
        email: String,
        username: String,
        password: String,
        phoneNumber: String
    ) {
        try {
            // Validar nombre completo
            if (fullName.isBlank()) {
                throw IllegalArgumentException("El nombre completo es obligatorio")
            }

            if (fullName.length < 2) {
                throw IllegalArgumentException("El nombre debe tener al menos 2 caracteres")
            }

            if (fullName.length > 100) {
                throw IllegalArgumentException("El nombre es demasiado largo")
            }

            // Validar email
            validateEmailFormat(email)

            // Validar username
            validateUsername(username)

            // Validar contraseña
            validatePasswordStrength(password)

            // Validar teléfono
            validatePhoneNumber(phoneNumber)

        } catch (e: IllegalArgumentException) {
            throw AppException.ValidationException(e.message ?: "Datos de registro inválidos")
        }
    }

    private fun validateEmailFormat(email: String) {
        try {
            if (email.isBlank()) {
                throw IllegalArgumentException("El email es obligatorio")
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                throw IllegalArgumentException("Formato de email inválido")
            }

            // Validar dominios permitidos (ejemplo práctico)
            val allowedDomains = listOf("gmail.com", "email.com", "hotmail.com", "outlook.com")
            val domain = email.substringAfter("@")

            if (domain !in allowedDomains) {
                throw IllegalArgumentException("Dominio de email no permitido")
            }

        } catch (e: Exception) {
            throw IllegalArgumentException("Error en validación de email: ${e.message}")
        }
    }

    private fun validateUsername(username: String) {
        try {
            if (username.isBlank()) {
                throw IllegalArgumentException("El nombre de usuario es obligatorio")
            }

            if (username.length < 3) {
                throw IllegalArgumentException("El nombre de usuario debe tener al menos 3 caracteres")
            }

            if (username.length > 20) {
                throw IllegalArgumentException("El nombre de usuario es demasiado largo")
            }

            // Validar caracteres permitidos
            if (!username.matches(Regex("^[a-zA-Z0-9_]+$"))) {
                throw IllegalArgumentException("El nombre de usuario solo puede contener letras, números y guiones bajos")
            }

            // Validar palabras prohibidas
            val forbiddenWords = listOf("admin", "root", "system", "test")
            if (forbiddenWords.any { username.contains(it, ignoreCase = true) }) {
                throw IllegalArgumentException("Nombre de usuario no permitido")
            }

        } catch (e: Exception) {
            throw IllegalArgumentException("Error en validación de username: ${e.message}")
        }
    }

    private fun validatePasswordStrength(password: String) {
        try {
            if (password.isBlank()) {
                throw IllegalArgumentException("La contraseña es obligatoria")
            }

            if (password.length < 6) {
                throw IllegalArgumentException("La contraseña debe tener al menos 6 caracteres")
            }

            if (password.length > 50) {
                throw IllegalArgumentException("La contraseña es demasiado larga")
            }

            // Validar que contenga al menos una letra
            if (!password.any { it.isLetter() }) {
                throw IllegalArgumentException("La contraseña debe contener al menos una letra")
            }

            // Validar que contenga al menos un número
            if (!password.any { it.isDigit() }) {
                throw IllegalArgumentException("La contraseña debe contener al menos un número")
            }

            // Validar contraseñas comunes
            val commonPasswords = listOf("123456", "password", "qwerty", "abc123")
            if (commonPasswords.any { password.equals(it, ignoreCase = true) }) {
                throw IllegalArgumentException("Contraseña demasiado común, elige una más segura")
            }

        } catch (e: Exception) {
            throw IllegalArgumentException("Error en validación de contraseña: ${e.message}")
        }
    }

    private fun validatePhoneNumber(phoneNumber: String) {
        try {
            if (phoneNumber.isBlank()) {
                throw IllegalArgumentException("El número de teléfono es obligatorio")
            }

            // Remover espacios y caracteres especiales para validación
            val cleanPhone = phoneNumber.replace(Regex("[\\s\\-\\(\\)]"), "")

            if (!cleanPhone.matches(Regex("^[+]?[0-9]{8,15}$"))) {
                throw IllegalArgumentException("Formato de teléfono inválido")
            }

        } catch (e: Exception) {
            throw IllegalArgumentException("Error en validación de teléfono: ${e.message}")
        }
    }

    // Verificar disponibilidad de email y username
    private suspend fun checkUserAvailability(email: String, username: String) {
        try {
            delay(500) // Simular consulta a base de datos

            // Verificar email
            val existingUserByEmail = UserRepository.findUserByEmailOrUsername(email)
            if (existingUserByEmail != null) {
                throw IllegalStateException("Ya existe un usuario con este email")
            }

            // Verificar username
            val existingUserByUsername = UserRepository.findUserByEmailOrUsername(username)
            if (existingUserByUsername != null) {
                throw IllegalStateException("Ya existe un usuario con este nombre de usuario")
            }

        } catch (e: IllegalStateException) {
            throw AppException.ValidationException(e.message ?: "Usuario no disponible")
        } catch (e: Exception) {
            throw AppException.DatabaseException("Error al verificar disponibilidad del usuario", e)
        }
    }

    // Crear objeto usuario con manejo de excepciones
    private fun createUser(
        fullName: String,
        email: String,
        username: String,
        password: String,
        phoneNumber: String
    ): User {
        return try {
            User(
                id = generateUserId(),
                fullName = fullName.trim(),
                email = email.trim().lowercase(),
                username = username.trim().lowercase(),
                password = password, // En producción debería estar hasheado
                phoneNumber = phoneNumber.trim()
            )
        } catch (e: Exception) {
            throw AppException.ValidationException("Error al crear objeto usuario: ${e.message}")
        }
    }

    // Generar ID único
    private fun generateUserId(): String {
        return try {
            val timestamp = System.currentTimeMillis()
            val random = Random.nextInt(1000, 9999)
            "$timestamp$random"
        } catch (e: Exception) {
            throw AppException.UnknownException("Error al generar ID de usuario")
        }
    }

    // Guardar usuario en base de datos
    private fun saveUserToDatabase(user: User): User {
        return try {
            val result = UserRepository.addUser(user) { validatedUser ->
                // Validación adicional antes de guardar
                UserRepository.validateUser(validatedUser)
            }

            when (result) {
                is Result.Success -> result.data
                is Result.Error -> throw AppException.DatabaseException(result.message)
            }

        } catch (e: AppException) {
            throw e
        } catch (e: Exception) {
            throw AppException.DatabaseException("Error al guardar usuario en base de datos", e)
        }
    }

    // Simular envío de email de confirmación
    private suspend fun sendConfirmationEmail(user: User) {
        try {
            delay(1000) // Simular tiempo de envío

            // Simular posibles errores de email
            if (Random.nextInt(100) < 10) {
                throw java.net.SocketTimeoutException("Timeout al enviar email")
            }

            println("📧 Email de confirmación enviado a: ${user.email}")

        } catch (e: Exception) {
            // No fallar el registro por el email, solo logear
            println("⚠️ No se pudo enviar email de confirmación: ${e.message}")
        }
    }

    // Simulación de errores durante registro
    private fun simulateRegistrationErrors() {
        val random = Random.nextInt(100)
        when {
            random < 3 -> throw java.net.UnknownHostException("Sin conexión durante registro")
            random < 6 -> throw java.net.SocketTimeoutException("Timeout durante registro")
            random < 8 -> throw IllegalStateException("Servicio de registro temporalmente no disponible")
        }
    }

    // Verificar email de confirmación
    fun verifyEmail(email: String, verificationCode: String): OperationResult<Boolean> {
        return ExceptionHandler.executeWithCatch(
            operation = {
                try {
                    if (email.isBlank() || verificationCode.isBlank()) {
                        throw IllegalArgumentException("Email y código de verificación son obligatorios")
                    }

                    // Simular verificación de código
                    val isValidCode = verificationCode == "123456" // Código de ejemplo

                    if (!isValidCode) {
                        throw IllegalArgumentException("Código de verificación inválido")
                    }

                    // Simular actualización de estado del usuario
                    updateUserVerificationStatus(email, true)

                    true

                } catch (e: IllegalArgumentException) {
                    throw AppException.ValidationException(e.message ?: "Error de verificación")
                } catch (e: Exception) {
                    throw AppException.DatabaseException("Error al verificar email", e)
                }
            }
        )
    }

    private fun updateUserVerificationStatus(email: String, verified: Boolean) {
        try {
            // En una app real, aquí se actualizaría el estado en la base de datos
            println("✅ Usuario $email marcado como verificado: $verified")
        } catch (e: Exception) {
            throw Exception("Error al actualizar estado de verificación")
        }
    }

    // Reenviar email de verificación
    suspend fun resendVerificationEmail(email: String): OperationResult<Unit> {
        return ExceptionHandler.executeWithCatchSuspend(
            operation = {
                try {
                    // Validar que el usuario existe
                    val user = UserRepository.findUserByEmailOrUsername(email)
                        ?: throw IllegalArgumentException("Usuario no encontrado")

                    // Verificar que no esté ya verificado
                    if (isUserVerified(user)) {
                        throw IllegalStateException("El usuario ya está verificado")
                    }

                    // Reenviar email
                    sendConfirmationEmail(user)

                } catch (e: IllegalArgumentException) {
                    throw AppException.ValidationException(e.message ?: "Error en reenvío")
                } catch (e: IllegalStateException) {
                    throw AppException.ValidationException(e.message ?: "Estado inválido")
                } catch (e: Exception) {
                    throw AppException.NetworkException("Error al reenviar email", e)
                }
            }
        )
    }

    private fun isUserVerified(user: User): Boolean {
        return try {
            // Simular verificación de estado
            false // Por defecto no verificado en este ejemplo
        } catch (e: Exception) {
            false
        }
    }
}