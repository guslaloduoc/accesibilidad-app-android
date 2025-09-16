package com.example.exp1_s2_gustavo_renjifo.utils

import android.util.Log
import kotlinx.coroutines.TimeoutCancellationException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLException

/**
 * Utilidades para manejo de excepciones en escenarios prácticos
 */

// Tipos de excepciones personalizadas
sealed class AppException(message: String, cause: Throwable? = null) : Exception(message, cause) {
    class ValidationException(message: String) : AppException(message)
    class AuthenticationException(message: String) : AppException(message)
    class NetworkException(message: String, cause: Throwable? = null) : AppException(message, cause)
    class DatabaseException(message: String, cause: Throwable? = null) : AppException(message, cause)
    class FileException(message: String, cause: Throwable? = null) : AppException(message, cause)
    class UnknownException(message: String, cause: Throwable? = null) : AppException(message, cause)
}

// Resultado de operaciones que pueden fallar
sealed class OperationResult<out T> {
    data class Success<T>(val data: T) : OperationResult<T>()
    data class Error(val exception: AppException) : OperationResult<Nothing>()
    data class Loading(val message: String = "Cargando...") : OperationResult<Nothing>()
}

// Objeto para manejo centralizado de excepciones
object ExceptionHandler {

    private const val TAG = "ExceptionHandler"

    // Función básica para ejecutar operaciones con try/catch
    inline fun <T> executeWithCatch(
        operation: () -> T,
        onError: (AppException) -> Unit = { logError(it) }
    ): OperationResult<T> {
        return try {
            val result = operation()
            OperationResult.Success(result)
        } catch (e: Exception) {
            val appException = mapToAppException(e)
            onError(appException)
            OperationResult.Error(appException)
        }
    }

    // Función para operaciones suspendidas
    suspend inline fun <T> executeWithCatchSuspend(
        crossinline operation: suspend () -> T,
        crossinline onError: (AppException) -> Unit = { logError(it) }
    ): OperationResult<T> {
        return try {
            val result = operation()
            OperationResult.Success(result)
        } catch (e: Exception) {
            val appException = mapToAppException(e)
            onError(appException)
            OperationResult.Error(appException)
        }
    }

    // Mapear excepciones del sistema a excepciones de la app
    fun mapToAppException(exception: Exception): AppException {
        return when (exception) {
            // Excepciones de red
            is UnknownHostException -> AppException.NetworkException(
                "Sin conexión a internet. Verifica tu conectividad.", exception
            )
            is ConnectException -> AppException.NetworkException(
                "No se pudo conectar al servidor. Intenta más tarde.", exception
            )
            is SocketTimeoutException -> AppException.NetworkException(
                "Tiempo de espera agotado. Verifica tu conexión.", exception
            )
            is SSLException -> AppException.NetworkException(
                "Error de seguridad en la conexión.", exception
            )
            is IOException -> AppException.NetworkException(
                "Error de comunicación con el servidor.", exception
            )

            // Excepciones de tiempo
            is TimeoutCancellationException -> AppException.NetworkException(
                "Operación cancelada por tiempo de espera.", exception
            )

            // Excepciones de validación
            is IllegalArgumentException -> AppException.ValidationException(
                exception.message ?: "Datos inválidos proporcionados."
            )
            is IllegalStateException -> AppException.ValidationException(
                exception.message ?: "Estado inválido de la aplicación."
            )

            // Excepciones de archivo
            is java.io.FileNotFoundException -> AppException.FileException(
                "Archivo no encontrado.", exception
            )
            is SecurityException -> AppException.FileException(
                "Sin permisos para acceder al archivo.", exception
            )

            // Excepciones personalizadas de la app
            is AppException -> exception

            // Otras excepciones
            else -> AppException.UnknownException(
                exception.message ?: "Error desconocido.", exception
            )
        }
    }

    // Logging de errores
    fun logError(exception: AppException) {
        when (exception) {
            is AppException.NetworkException -> {
                Log.e(TAG, "Error de red: ${exception.message}", exception.cause)
            }
            is AppException.AuthenticationException -> {
                Log.w(TAG, "Error de autenticación: ${exception.message}")
            }
            is AppException.ValidationException -> {
                Log.w(TAG, "Error de validación: ${exception.message}")
            }
            is AppException.DatabaseException -> {
                Log.e(TAG, "Error de base de datos: ${exception.message}", exception.cause)
            }
            is AppException.FileException -> {
                Log.e(TAG, "Error de archivo: ${exception.message}", exception.cause)
            }
            is AppException.UnknownException -> {
                Log.e(TAG, "Error desconocido: ${exception.message}", exception.cause)
            }
        }
    }

    // Función para retry con exponential backoff
    suspend fun <T> retryWithBackoff(
        maxRetries: Int = 3,
        initialDelayMs: Long = 1000,
        operation: suspend () -> T
    ): OperationResult<T> {
        var lastException: AppException? = null
        var delayMs = initialDelayMs

        repeat(maxRetries) { attempt ->
            try {
                val result = operation()
                return OperationResult.Success(result)
            } catch (e: Exception) {
                lastException = mapToAppException(e)

                // No hacer retry para ciertos tipos de errores
                if (lastException is AppException.ValidationException ||
                    lastException is AppException.AuthenticationException) {
                    return OperationResult.Error(lastException!!)
                }

                if (attempt < maxRetries - 1) {
                    Log.w("ExceptionHandler", "Intento ${attempt + 1} falló, reintentando en ${delayMs}ms: ${e.message}")
                    kotlinx.coroutines.delay(delayMs)
                    delayMs *= 2 // Exponential backoff
                }
            }
        }

        return OperationResult.Error(lastException ?: AppException.UnknownException("Falló después de $maxRetries intentos"))
    }

    // Función para mostrar mensajes de error amigables al usuario
    fun getUserFriendlyMessage(exception: AppException): String {
        return when (exception) {
            is AppException.NetworkException -> "Problema de conexión. Verifica tu internet y vuelve a intentar."
            is AppException.AuthenticationException -> "Credenciales incorrectas. Verifica tu email y contraseña."
            is AppException.ValidationException -> exception.message ?: "Error de validación"
            is AppException.DatabaseException -> "Error al guardar información. Intenta nuevamente."
            is AppException.FileException -> "Error al acceder al archivo. Verifica los permisos."
            is AppException.UnknownException -> "Ocurrió un error inesperado. Intenta nuevamente."
        }
    }
}

// Funciones de extensión para manejo de errores
fun <T> OperationResult<T>.onSuccess(action: (T) -> Unit): OperationResult<T> {
    if (this is OperationResult.Success) {
        action(data)
    }
    return this
}

fun <T> OperationResult<T>.onError(action: (AppException) -> Unit): OperationResult<T> {
    if (this is OperationResult.Error) {
        action(exception)
    }
    return this
}

fun <T> OperationResult<T>.onLoading(action: (String) -> Unit): OperationResult<T> {
    if (this is OperationResult.Loading) {
        action(message)
    }
    return this
}

// Función para validaciones con try/catch
fun <T> validateAndExecute(
    data: T,
    validator: (T) -> Boolean,
    errorMessage: String,
    operation: (T) -> Unit
): OperationResult<Unit> {
    return try {
        if (!validator(data)) {
            OperationResult.Error(AppException.ValidationException(errorMessage))
        } else {
            operation(data)
            OperationResult.Success(Unit)
        }
    } catch (e: Exception) {
        OperationResult.Error(ExceptionHandler.mapToAppException(e))
    }
}

// Función para operaciones críticas con logging detallado
fun <T> criticalOperation(
    operationName: String,
    operation: () -> T
): OperationResult<T> {
    Log.d("CriticalOp", "Iniciando operación crítica: $operationName")

    return try {
        val startTime = System.currentTimeMillis()
        val result = operation()
        val duration = System.currentTimeMillis() - startTime

        Log.d("CriticalOp", "Operación '$operationName' completada exitosamente en ${duration}ms")
        OperationResult.Success(result)

    } catch (e: Exception) {
        Log.e("CriticalOp", "Operación crítica '$operationName' falló", e)
        val appException = ExceptionHandler.mapToAppException(e)
        ExceptionHandler.logError(appException)
        OperationResult.Error(appException)
    }
}