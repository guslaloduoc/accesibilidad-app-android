package com.example.exp1_s2_gustavo_renjifo.data

import com.example.exp1_s2_gustavo_renjifo.utils.*

object UserRepository {
    private val users = mutableListOf(
        User(
            id = "1",
            username = "maria_silva",
            email = "maria.silva@email.com",
            password = "maria1233",
            fullName = "María Silva González",
            phoneNumber = "+56912345678"
        ),
        User(
            id = "2",
            username = "carlos_mendez",
            email = "carlos.mendez@email.com",
            password = "carlos456",
            fullName = "Carlos Méndez López",
            phoneNumber = "+56923456789"
        ),
        User(
            id = "3",
            username = "ana_rodriguez",
            email = "ana.rodriguez@email.com",
            password = "ana789",
            fullName = "Ana Rodríguez Pérez",
            phoneNumber = "+56934567890"
        ),
        User(
            id = "4",
            username = "jose_torres",
            email = "jose.torres@email.com",
            password = "jose321",
            fullName = "José Torres Morales",
            phoneNumber = "+56945678901"
        ),
        User(
            id = "5",
            username = "lucia_ramirez",
            email = "lucia.ramirez@email.com",
            password = "lucia654",
            fullName = "Lucía Ramírez Castro",
            phoneNumber = "+56956789012"
        )
    )

    // Cache funcional para búsquedas frecuentes
    private val userCache = FunctionalCache<String, User?>()

    // Funciones básicas usando funcionalidades mejoradas
    fun getAllUsers(): List<User> = users.toList()

    // Función mejorada usando lambdas
    fun findUserByEmailOrUsername(identifier: String): User? {
        return userCache.getOrCompute("find_$identifier") {
            UserFunctionalUtils.findUser(users, UserFunctionalUtils.byEmailOrUsername(identifier))
                .let { result ->
                    when (result) {
                        is Result.Success -> result.data
                        is Result.Error -> null
                    }
                }
        }
    }

    // Autenticación usando función de orden superior
    fun authenticateUser(
        identifier: String,
        password: String,
        onSuccess: (User) -> Unit = {},
        onError: (String) -> Unit = {}
    ): Result<User> {
        return UserFunctionalUtils.authenticate(
            users = users,
            identifier = identifier,
            password = password,
            onSuccess = onSuccess,
            onError = onError
        )
    }

    // Agregar usuario con validación funcional
    fun addUser(
        user: User,
        validator: (User) -> ValidationResult = { ValidationResult(true) }
    ): Result<User> {
        return UserFunctionalUtils.processUser(
            user = user,
            validators = listOf(
                validator,
                { u -> if (findUserByEmailOrUsername(u.email) == null) ValidationResult(true) else ValidationResult(false, "Email ya existe") },
                { u -> if (findUserByEmailOrUsername(u.username) == null) ValidationResult(true) else ValidationResult(false, "Username ya existe") }
            ),
            processor = { u ->
                val newUser = u.copy(id = (users.size + 1).toString())
                users.add(newUser)
                userCache.invalidate { true } // Limpiar cache
                Result.Success(newUser)
            }
        )
    }

    // Actualizar contraseña usando función de orden superior
    fun updateUserPassword(
        identifier: String,
        newPassword: String,
        passwordValidator: (String) -> ValidationResult = Validators.passwordValidator
    ): Result<User> {
        val validationResult = passwordValidator(newPassword)
        if (!validationResult.isValid) {
            return Result.Error(validationResult.errorMessage ?: "Contraseña inválida")
        }

        return tryWithResult {
            val userIndex = users.indexOfFirst {
                UserFunctionalUtils.byEmailOrUsername(identifier)(it)
            }

            if (userIndex != -1) {
                val updatedUser = users[userIndex].copy(password = newPassword)
                users[userIndex] = updatedUser
                userCache.invalidate { true } // Limpiar cache
                updatedUser
            } else {
                throw Exception("Usuario no encontrado")
            }
        }
    }

    // Funciones adicionales usando lambdas

    // Filtrar usuarios por criterio
    fun filterUsers(predicate: (User) -> Boolean): List<User> =
        UserFunctionalUtils.filterUsers(users, predicate)

    // Buscar usuarios por dominio de email
    fun getUsersByEmailDomain(domain: String): List<User> =
        UserFunctionalUtils.analyzeUsers(users, UserFunctionalUtils.getUsersByDomain(domain))

    // Obtener estadísticas usando lambdas
    fun getUserStatistics(): Map<String, Any> = mapOf(
        "totalUsers" to UserFunctionalUtils.analyzeUsers(users, UserFunctionalUtils.countUsers),
        "emails" to UserFunctionalUtils.analyzeUsers(users, UserFunctionalUtils.getUserEmails),
        "domains" to users.map { it.email.substringAfter("@") }.distinct()
    )

    // Buscar usuario con múltiples criterios
    fun findUserByCriteria(vararg criteria: (User) -> Boolean): User? =
        users.find { user -> criteria.all { criterion -> criterion(user) } }

    // Función de orden superior para operaciones batch
    fun <R> performBatchOperation(
        users: List<User>,
        operation: (List<User>) -> R,
        onSuccess: (R) -> Unit = {},
        onError: (String) -> Unit = {}
    ): Result<R> {
        return tryWithResult {
            operation(users)
        }.onSuccess(onSuccess).onError(onError)
    }

    // Validar usuario completo usando chain de validaciones
    fun validateUser(user: User): ValidationResult {
        val validationChain = ValidationChain<User>()
            .addRule { u -> Validators.nameValidator(u.fullName) }
            .addRule { u -> Validators.emailValidator(u.email) }
            .addRule { u -> Validators.passwordValidator(u.password) }
            .addRule { u -> Validators.phoneValidator(u.phoneNumber) }

        return validationChain.validate(user)
    }

    // Transformar usuarios usando función de orden superior
    fun <R> transformUsers(transformer: (User) -> R): List<R> =
        UserFunctionalUtils.transformUsers(users, transformer)
}