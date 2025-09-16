package com.example.exp1_s2_gustavo_renjifo.services

import com.example.exp1_s2_gustavo_renjifo.utils.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeout
import java.io.IOException
import java.net.*
import kotlin.random.Random

/**
 * Servicio de red con manejo completo de excepciones de conectividad
 */
class NetworkService {

    companion object {
        private const val DEFAULT_TIMEOUT = 30000L // 30 segundos
        private const val RETRY_DELAY = 2000L // 2 segundos
    }

    // Simulación de llamada API con manejo de errores de red
    suspend fun fetchUserProfile(userId: String): OperationResult<Map<String, Any>> {
        return ExceptionHandler.retryWithBackoff(
            maxRetries = 3,
            initialDelayMs = RETRY_DELAY
        ) {
            try {
                // Validar parámetros de entrada
                validateApiParameters(userId)

                // Simular timeout de red
                withTimeout(DEFAULT_TIMEOUT) {
                    // Simular delay de red
                    delay(Random.nextLong(1000, 5000))

                    // Simular diferentes tipos de errores de red
                    simulateNetworkConditions()

                    // Simular respuesta exitosa
                    mapOf(
                        "id" to userId,
                        "profile" to "User profile data",
                        "timestamp" to System.currentTimeMillis()
                    )
                }

            } catch (e: Exception) {
                handleNetworkException(e, "fetchUserProfile")
                throw e
            }
        }
    }

    // Envío de datos con manejo de errores
    suspend fun sendUserData(data: Map<String, Any>): OperationResult<Boolean> {
        return ExceptionHandler.executeWithCatchSuspend(
            operation = {
                try {
                    // Validar datos antes de enviar
                    validateDataForSending(data)

                    // Simular compresión de datos
                    val compressedData = compressData(data)

                    // Simular envío con posibles errores
                    withTimeout(DEFAULT_TIMEOUT) {
                        delay(Random.nextLong(2000, 6000))

                        // Simular errores específicos de envío
                        simulateSendingErrors()

                        // Verificar respuesta del servidor
                        verifyServerResponse()

                        true
                    }

                } catch (e: Exception) {
                    handleSendingException(e, data)
                    throw e
                }
            }
        )
    }

    // Descarga de archivos con manejo de errores
    suspend fun downloadFile(url: String, fileName: String): OperationResult<String> {
        return ExceptionHandler.executeWithCatchSuspend(
            operation = {
                try {
                    // Validar URL
                    validateUrl(url)

                    // Validar nombre de archivo
                    validateFileName(fileName)

                    // Verificar espacio en disco
                    checkDiskSpace()

                    // Simular descarga con progreso
                    simulateFileDownload(url, fileName)

                } catch (e: Exception) {
                    handleDownloadException(e, url, fileName)
                    throw e
                }
            }
        )
    }

    // Verificar conectividad de red
    fun checkNetworkConnectivity(): OperationResult<NetworkStatus> {
        return ExceptionHandler.executeWithCatch(
            operation = {
                try {
                    val status = performConnectivityCheck()
                    status
                } catch (e: Exception) {
                    NetworkStatus.DISCONNECTED
                }
            }
        )
    }

    // Ping a servidor específico
    suspend fun pingServer(serverUrl: String): OperationResult<Long> {
        return ExceptionHandler.executeWithCatchSuspend(
            operation = {
                try {
                    val startTime = System.currentTimeMillis()

                    // Simular ping con timeout
                    withTimeout(5000) {
                        delay(Random.nextLong(100, 2000))

                        // Simular posibles errores de ping
                        simulatePingErrors()

                        val endTime = System.currentTimeMillis()
                        endTime - startTime
                    }

                } catch (e: Exception) {
                    handlePingException(e, serverUrl)
                    throw e
                }
            }
        )
    }

    // Métodos de validación
    private fun validateApiParameters(userId: String) {
        try {
            if (userId.isBlank()) {
                throw IllegalArgumentException("User ID no puede estar vacío")
            }

            if (userId.length > 50) {
                throw IllegalArgumentException("User ID demasiado largo")
            }

            // Validar formato de ID
            if (!userId.matches(Regex("^[a-zA-Z0-9]+$"))) {
                throw IllegalArgumentException("Formato de User ID inválido")
            }

        } catch (e: IllegalArgumentException) {
            throw AppException.ValidationException(e.message ?: "Parámetros inválidos")
        }
    }

    private fun validateDataForSending(data: Map<String, Any>) {
        try {
            if (data.isEmpty()) {
                throw IllegalArgumentException("No hay datos para enviar")
            }

            // Validar tamaño de datos
            val dataSize = data.toString().length
            if (dataSize > 1000000) { // 1MB
                throw IllegalArgumentException("Datos demasiado grandes para enviar")
            }

            // Validar campos obligatorios
            val requiredFields = listOf("id", "type")
            requiredFields.forEach { field ->
                if (!data.containsKey(field)) {
                    throw IllegalArgumentException("Campo obligatorio faltante: $field")
                }
            }

        } catch (e: IllegalArgumentException) {
            throw AppException.ValidationException(e.message ?: "Datos inválidos")
        }
    }

    private fun validateUrl(url: String) {
        try {
            if (url.isBlank()) {
                throw IllegalArgumentException("URL no puede estar vacía")
            }

            // Validar formato de URL
            URL(url) // Esto lanza MalformedURLException si es inválida

            // Validar protocolo
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                throw IllegalArgumentException("Protocolo de URL no soportado")
            }

        } catch (e: MalformedURLException) {
            throw AppException.ValidationException("URL mal formada: ${e.message}")
        } catch (e: IllegalArgumentException) {
            throw AppException.ValidationException(e.message ?: "URL inválida")
        }
    }

    private fun validateFileName(fileName: String) {
        try {
            if (fileName.isBlank()) {
                throw IllegalArgumentException("Nombre de archivo no puede estar vacío")
            }

            // Validar caracteres prohibidos
            val forbiddenChars = listOf("/", "\\", ":", "*", "?", "\"", "<", ">", "|")
            forbiddenChars.forEach { char ->
                if (fileName.contains(char)) {
                    throw IllegalArgumentException("Nombre de archivo contiene caracteres prohibidos")
                }
            }

            // Validar extensión
            if (!fileName.contains(".")) {
                throw IllegalArgumentException("Archivo debe tener extensión")
            }

        } catch (e: IllegalArgumentException) {
            throw AppException.ValidationException(e.message ?: "Nombre de archivo inválido")
        }
    }

    // Simulaciones de errores de red
    private fun simulateNetworkConditions() {
        val random = Random.nextInt(100)
        when {
            random < 5 -> throw UnknownHostException("Host no encontrado")
            random < 10 -> throw ConnectException("Conexión rechazada")
            random < 15 -> throw SocketTimeoutException("Timeout de conexión")
            random < 18 -> throw ProtocolException("Error de protocolo HTTP")
            random < 20 -> throw IOException("Error de E/O durante la conexión")
        }
    }

    private fun simulateSendingErrors() {
        val random = Random.nextInt(100)
        when {
            random < 8 -> throw SocketTimeoutException("Timeout al enviar datos")
            random < 12 -> throw ConnectException("Conexión perdida durante envío")
            random < 15 -> throw IOException("Error al escribir datos")
        }
    }

    private fun simulatePingErrors() {
        val random = Random.nextInt(100)
        when {
            random < 10 -> throw UnknownHostException("Servidor no alcanzable")
            random < 15 -> throw SocketTimeoutException("Timeout en ping")
        }
    }

    // Métodos auxiliares
    private fun compressData(data: Map<String, Any>): ByteArray {
        return try {
            // Simular compresión de datos
            data.toString().toByteArray()
        } catch (e: Exception) {
            throw AppException.UnknownException("Error al comprimir datos", e)
        }
    }

    private fun verifyServerResponse() {
        // Simular verificación de respuesta del servidor
        if (Random.nextInt(100) < 5) {
            throw ProtocolException("Respuesta del servidor inválida")
        }
    }

    private fun checkDiskSpace() {
        // Simular verificación de espacio en disco
        if (Random.nextInt(100) < 3) {
            throw IOException("Espacio insuficiente en disco")
        }
    }

    private suspend fun simulateFileDownload(url: String, fileName: String): String {
        try {
            val totalBytes = Random.nextLong(1000000, 10000000) // 1-10MB
            var downloadedBytes = 0L

            // Simular descarga por chunks
            while (downloadedBytes < totalBytes) {
                delay(100)
                downloadedBytes += Random.nextLong(10000, 100000)

                // Simular posibles errores durante descarga
                if (Random.nextInt(1000) < 2) {
                    throw IOException("Conexión interrumpida durante descarga")
                }

                // Simular progreso
                val progress = (downloadedBytes * 100 / totalBytes).toInt()
                if (progress % 20 == 0) {
                    println("📥 Descarga: $progress%")
                }
            }

            return "/downloads/$fileName"

        } catch (e: IOException) {
            throw AppException.NetworkException("Error durante descarga", e)
        } catch (e: Exception) {
            throw AppException.UnknownException("Error inesperado en descarga", e)
        }
    }

    private fun performConnectivityCheck(): NetworkStatus {
        return try {
            // Simular verificación de conectividad
            val random = Random.nextInt(100)
            when {
                random < 10 -> NetworkStatus.DISCONNECTED
                random < 20 -> NetworkStatus.POOR
                random < 40 -> NetworkStatus.FAIR
                else -> NetworkStatus.GOOD
            }
        } catch (e: Exception) {
            NetworkStatus.DISCONNECTED
        }
    }

    // Manejadores de excepciones específicos
    private fun handleNetworkException(e: Exception, operation: String) {
        when (e) {
            is UnknownHostException -> println("🌐 Sin conexión a internet en $operation")
            is SocketTimeoutException -> println("⏱️ Timeout en $operation")
            is ConnectException -> println("🔌 No se pudo conectar en $operation")
            is IOException -> println("📡 Error de E/O en $operation: ${e.message}")
            else -> println("❌ Error desconocido en $operation: ${e.message}")
        }
    }

    private fun handleSendingException(e: Exception, data: Map<String, Any>) {
        when (e) {
            is SocketTimeoutException -> println("📤 Timeout al enviar datos")
            is IOException -> println("📤 Error al enviar datos: ${e.message}")
            else -> println("📤 Error inesperado al enviar: ${e.message}")
        }
    }

    private fun handleDownloadException(e: Exception, url: String, fileName: String) {
        when (e) {
            is IOException -> println("📥 Error descargando $fileName desde $url: ${e.message}")
            is SecurityException -> println("🔒 Sin permisos para descargar $fileName")
            else -> println("📥 Error inesperado descargando $fileName: ${e.message}")
        }
    }

    private fun handlePingException(e: Exception, serverUrl: String) {
        when (e) {
            is UnknownHostException -> println("🏓 Servidor $serverUrl no alcanzable")
            is SocketTimeoutException -> println("🏓 Timeout en ping a $serverUrl")
            else -> println("🏓 Error en ping a $serverUrl: ${e.message}")
        }
    }
}

// Enum para estado de red
enum class NetworkStatus {
    GOOD,
    FAIR,
    POOR,
    DISCONNECTED
}