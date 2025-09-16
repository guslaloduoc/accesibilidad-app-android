package com.example.exp1_s2_gustavo_renjifo.utils

import com.example.exp1_s2_gustavo_renjifo.data.User

/**
 * Utilidades funcionales usando funciones de orden superior y lambdas
 */

// Tipos de resultado para operaciones
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val message: String) : Result<Nothing>()
}

// Funciones de extensión para Result usando lambdas
inline fun <T, R> Result<T>.map(transform: (T) -> R): Result<R> = when (this) {
    is Result.Success -> Result.Success(transform(data))
    is Result.Error -> this
}

inline fun <T, R> Result<T>.flatMap(transform: (T) -> Result<R>): Result<R> = when (this) {
    is Result.Success -> transform(data)
    is Result.Error -> this
}

inline fun <T> Result<T>.onSuccess(action: (T) -> Unit): Result<T> {
    if (this is Result.Success) action(data)
    return this
}

inline fun <T> Result<T>.onError(action: (String) -> Unit): Result<T> {
    if (this is Result.Error) action(message)
    return this
}

// Objeto con utilidades funcionales para manejo de usuarios
object UserFunctionalUtils {

    // Función de orden superior para filtrar usuarios
    fun filterUsers(
        users: List<User>,
        predicate: (User) -> Boolean
    ): List<User> = users.filter(predicate)

    // Funciones de orden superior para búsqueda
    fun findUser(
        users: List<User>,
        matcher: (User) -> Boolean
    ): Result<User> = users.find(matcher)?.let { Result.Success(it) }
        ?: Result.Error("Usuario no encontrado")

    // Lambda para transformar usuarios
    fun <R> transformUsers(
        users: List<User>,
        transformer: (User) -> R
    ): List<R> = users.map(transformer)

    // Función de orden superior para validar y procesar usuarios
    fun processUser(
        user: User,
        validators: List<(User) -> ValidationResult>,
        processor: (User) -> Result<User>
    ): Result<User> {
        val validationResults = validators.map { it(user) }
        val hasErrors = validationResults.any { !it.isValid }

        return if (hasErrors) {
            val errorMessage = validationResults
                .filter { !it.isValid }
                .joinToString(", ") { it.errorMessage ?: "Error" }
            Result.Error(errorMessage)
        } else {
            processor(user)
        }
    }

    // Lambdas predefinidas para operaciones comunes
    val byEmail: (String) -> (User) -> Boolean = { email ->
        { user -> user.email.equals(email, ignoreCase = true) }
    }

    val byUsername: (String) -> (User) -> Boolean = { username ->
        { user -> user.username.equals(username, ignoreCase = true) }
    }

    val byEmailOrUsername: (String) -> (User) -> Boolean = { identifier ->
        { user ->
            user.email.equals(identifier, ignoreCase = true) ||
            user.username.equals(identifier, ignoreCase = true)
        }
    }

    // Función de orden superior para autenticación
    fun authenticate(
        users: List<User>,
        identifier: String,
        password: String,
        onSuccess: (User) -> Unit = {},
        onError: (String) -> Unit = {}
    ): Result<User> {
        val result = findUser(users, byEmailOrUsername(identifier))
            .flatMap { user ->
                if (user.password == password) {
                    Result.Success(user)
                } else {
                    Result.Error("Contraseña incorrecta")
                }
            }

        result.onSuccess(onSuccess).onError(onError)
        return result
    }

    // Función de orden superior para estadísticas
    fun <R> analyzeUsers(
        users: List<User>,
        analyzer: (List<User>) -> R
    ): R = analyzer(users)

    // Lambdas para análisis comunes
    val countUsers: (List<User>) -> Int = { it.size }
    val getUserEmails: (List<User>) -> List<String> = { users -> users.map { it.email } }
    val getUsersByDomain: (String) -> (List<User>) -> List<User> = { domain ->
        { users -> users.filter { it.email.endsWith(domain) } }
    }
}

// Utilidades para manejo de listas con lambdas
object ListUtils {

    // Función de orden superior para agrupar elementos
    fun <T, K> List<T>.groupByKey(keySelector: (T) -> K): Map<K, List<T>> =
        this.groupBy(keySelector)

    // Función de orden superior para particionar
    fun <T> List<T>.partitionBy(predicate: (T) -> Boolean): Pair<List<T>, List<T>> =
        this.partition(predicate)

    // Función de orden superior para reducir con acumulador
    fun <T, R> List<T>.foldWithIndex(
        initial: R,
        operation: (Int, R, T) -> R
    ): R = this.foldIndexed(initial, operation)
}

// Extensiones funcionales para String
fun String.ifNotEmpty(action: (String) -> Unit): String = apply {
    if (isNotEmpty()) action(this)
}

fun String.ifEmpty(defaultValue: () -> String): String =
    if (isEmpty()) defaultValue() else this

// Función de orden superior para manejo de errores
inline fun <T> tryWithResult(block: () -> T): Result<T> = try {
    Result.Success(block())
} catch (e: Exception) {
    Result.Error(e.message ?: "Error desconocido")
}

// Función de orden superior para cache con lambda
class FunctionalCache<K, V> {
    private val cache = mutableMapOf<K, V>()

    fun getOrCompute(key: K, compute: (K) -> V): V =
        cache.getOrPut(key) { compute(key) }

    fun invalidate(predicate: (Map.Entry<K, V>) -> Boolean) {
        cache.entries.removeAll(predicate)
    }
}