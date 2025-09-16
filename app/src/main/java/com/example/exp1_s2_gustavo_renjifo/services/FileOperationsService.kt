package com.example.exp1_s2_gustavo_renjifo.services

import android.content.Context
import com.example.exp1_s2_gustavo_renjifo.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.*
import java.nio.file.AccessDeniedException
import kotlin.random.Random

/**
 * Servicio para operaciones de archivos con manejo completo de excepciones
 */
class FileOperationsService(private val context: Context) {

    // Guardar datos de usuario en archivo
    suspend fun saveUserData(userData: Map<String, Any>, fileName: String): OperationResult<String> {
        return ExceptionHandler.executeWithCatchSuspend(
            operation = {
                withContext(Dispatchers.IO) {
                    try {
                        // Validar datos de entrada
                        validateUserData(userData)
                        validateFileName(fileName)

                        // Verificar permisos de escritura
                        checkWritePermissions()

                        // Verificar espacio disponible
                        checkAvailableSpace(userData)

                        // Crear directorio si no existe
                        val directory = createUserDataDirectory()

                        // Escribir archivo
                        val filePath = writeUserDataToFile(directory, fileName, userData)

                        // Verificar integridad del archivo
                        verifyFileIntegrity(filePath)

                        filePath
                    } catch (e: Exception) {
                        handleFileWriteException(e, fileName)
                        throw e
                    }
                }
            }
        )
    }

    // Cargar datos de usuario desde archivo
    suspend fun loadUserData(fileName: String): OperationResult<Map<String, Any>> {
        return ExceptionHandler.executeWithCatchSuspend(
            operation = {
                withContext(Dispatchers.IO) {
                    try {
                        // Validar nombre de archivo
                        validateFileName(fileName)

                        // Verificar que el archivo existe
                        val filePath = getUserDataFilePath(fileName)
                        checkFileExists(filePath)

                        // Verificar permisos de lectura
                        checkReadPermissions(filePath)

                        // Leer archivo
                        val userData = readUserDataFromFile(filePath)

                        // Validar datos leídos
                        validateLoadedData(userData)

                        userData
                    } catch (e: Exception) {
                        handleFileReadException(e, fileName)
                        throw e
                    }
                }
            }
        )
    }

    // Eliminar archivo de usuario
    suspend fun deleteUserData(fileName: String): OperationResult<Boolean> {
        return ExceptionHandler.executeWithCatchSuspend(
            operation = {
                withContext(Dispatchers.IO) {
                    try {
                        validateFileName(fileName)

                        val filePath = getUserDataFilePath(fileName)

                        // Verificar que el archivo existe
                        checkFileExists(filePath)

                        // Verificar permisos de eliminación
                        checkDeletePermissions(filePath)

                        // Crear backup antes de eliminar
                        createBackupBeforeDelete(filePath)

                        // Eliminar archivo
                        val deleted = deleteFile(filePath)

                        if (!deleted) {
                            throw IOException("No se pudo eliminar el archivo")
                        }

                        // Verificar que fue eliminado
                        verifyFileDeletion(filePath)

                        true
                    } catch (e: Exception) {
                        handleFileDeleteException(e, fileName)
                        throw e
                    }
                }
            }
        )
    }

    // Copiar archivo con manejo de errores
    suspend fun copyUserData(sourceFileName: String, targetFileName: String): OperationResult<String> {
        return ExceptionHandler.executeWithCatchSuspend(
            operation = {
                withContext(Dispatchers.IO) {
                    try {
                        // Validar nombres
                        validateFileName(sourceFileName)
                        validateFileName(targetFileName)

                        val sourcePath = getUserDataFilePath(sourceFileName)
                        val targetPath = getUserDataFilePath(targetFileName)

                        // Verificar archivo origen
                        checkFileExists(sourcePath)
                        checkReadPermissions(sourcePath)

                        // Verificar que destino no existe
                        checkTargetFileDoesNotExist(targetPath)

                        // Verificar espacio para copia
                        checkSpaceForCopy(sourcePath)

                        // Realizar copia
                        copyFile(sourcePath, targetPath)

                        // Verificar copia exitosa
                        verifyCopySuccess(sourcePath, targetPath)

                        targetPath
                    } catch (e: Exception) {
                        handleFileCopyException(e, sourceFileName, targetFileName)
                        throw e
                    }
                }
            }
        )
    }

    // Listar archivos de usuario
    fun listUserDataFiles(): OperationResult<List<FileInfo>> {
        return ExceptionHandler.executeWithCatch(
            operation = {
                try {
                    val directory = File(context.filesDir, "userdata")

                    if (!directory.exists()) {
                        return@executeWithCatch emptyList<FileInfo>()
                    }

                    // Verificar permisos de lectura del directorio
                    if (!directory.canRead()) {
                        throw AccessDeniedException("Sin permisos para leer el directorio")
                    }

                    // Listar archivos
                    val files = directory.listFiles()
                        ?: throw IOException("Error al listar archivos del directorio")

                    // Convertir a FileInfo
                    files.map { file ->
                        createFileInfo(file)
                    }.sortedBy { it.name }

                } catch (e: Exception) {
                    handleFileListException(e)
                    throw e
                }
            }
        )
    }

    // Métodos de validación
    private fun validateUserData(userData: Map<String, Any>) {
        try {
            if (userData.isEmpty()) {
                throw IllegalArgumentException("Los datos del usuario no pueden estar vacíos")
            }

            // Verificar campos obligatorios
            val requiredFields = listOf("id", "name", "email")
            requiredFields.forEach { field ->
                if (!userData.containsKey(field)) {
                    throw IllegalArgumentException("Campo obligatorio faltante: $field")
                }
            }

            // Verificar tamaño de datos
            val dataSize = userData.toString().length
            if (dataSize > 500000) { // 500KB
                throw IllegalArgumentException("Datos demasiado grandes para guardar")
            }

        } catch (e: IllegalArgumentException) {
            throw AppException.ValidationException(e.message ?: "Datos de usuario inválidos")
        }
    }

    private fun validateFileName(fileName: String) {
        try {
            if (fileName.isBlank()) {
                throw IllegalArgumentException("Nombre de archivo no puede estar vacío")
            }

            if (fileName.length > 100) {
                throw IllegalArgumentException("Nombre de archivo demasiado largo")
            }

            // Validar caracteres prohibidos
            val forbiddenChars = listOf("/", "\\", ":", "*", "?", "\"", "<", ">", "|")
            forbiddenChars.forEach { char ->
                if (fileName.contains(char)) {
                    throw IllegalArgumentException("Nombre de archivo contiene caracteres prohibidos: $char")
                }
            }

            // Validar extensión
            if (!fileName.endsWith(".json") && !fileName.endsWith(".txt")) {
                throw IllegalArgumentException("Tipo de archivo no soportado")
            }

        } catch (e: IllegalArgumentException) {
            throw AppException.ValidationException(e.message ?: "Nombre de archivo inválido")
        }
    }

    // Métodos de verificación
    private fun checkWritePermissions() {
        try {
            val testFile = File(context.filesDir, "test_write_permission.tmp")

            if (!testFile.createNewFile()) {
                throw SecurityException("No se pueden crear archivos")
            }

            testFile.delete()

        } catch (e: IOException) {
            throw AppException.FileException("Sin permisos de escritura", e)
        } catch (e: SecurityException) {
            throw AppException.FileException("Permisos de escritura denegados", e)
        }
    }

    private fun checkReadPermissions(filePath: String) {
        try {
            val file = File(filePath)

            if (!file.canRead()) {
                throw SecurityException("Sin permisos de lectura para el archivo")
            }

        } catch (e: SecurityException) {
            throw AppException.FileException("Sin permisos de lectura", e)
        }
    }

    private fun checkDeletePermissions(filePath: String) {
        try {
            val file = File(filePath)
            val parentDir = file.parentFile

            if (parentDir != null && !parentDir.canWrite()) {
                throw SecurityException("Sin permisos de eliminación")
            }

        } catch (e: SecurityException) {
            throw AppException.FileException("Sin permisos de eliminación", e)
        }
    }

    private fun checkAvailableSpace(userData: Map<String, Any>) {
        try {
            val dataSize = userData.toString().toByteArray().size
            val availableSpace = context.filesDir.freeSpace

            if (availableSpace < dataSize * 2) { // Dejar margen
                throw IOException("Espacio insuficiente en el dispositivo")
            }

        } catch (e: IOException) {
            throw AppException.FileException("Verificación de espacio falló", e)
        }
    }

    private fun checkFileExists(filePath: String) {
        try {
            val file = File(filePath)

            if (!file.exists()) {
                throw FileNotFoundException("Archivo no encontrado: $filePath")
            }

        } catch (e: FileNotFoundException) {
            throw AppException.FileException("Archivo no encontrado", e)
        }
    }

    private fun checkTargetFileDoesNotExist(targetPath: String) {
        try {
            val file = File(targetPath)

            if (file.exists()) {
                throw IllegalStateException("El archivo destino ya existe")
            }

        } catch (e: IllegalStateException) {
            throw AppException.FileException("Archivo destino ya existe", e)
        }
    }

    // Métodos de operaciones de archivo
    private fun createUserDataDirectory(): File {
        return try {
            val directory = File(context.filesDir, "userdata")

            if (!directory.exists() && !directory.mkdirs()) {
                throw IOException("No se pudo crear el directorio de datos")
            }

            directory
        } catch (e: IOException) {
            throw AppException.FileException("Error al crear directorio", e)
        }
    }

    private fun writeUserDataToFile(directory: File, fileName: String, userData: Map<String, Any>): String {
        val file = File(directory, fileName)

        return try {
            FileOutputStream(file).use { fos ->
                OutputStreamWriter(fos, "UTF-8").use { writer ->
                    BufferedWriter(writer).use { bufferedWriter ->
                        // Simular escritura de datos (en formato JSON simplificado)
                        val jsonData = userData.entries.joinToString(",\n") { (key, value) ->
                            "\"$key\": \"$value\""
                        }
                        bufferedWriter.write("{\n$jsonData\n}")
                        bufferedWriter.flush()
                    }
                }
            }

            file.absolutePath
        } catch (e: IOException) {
            throw AppException.FileException("Error al escribir archivo", e)
        } catch (e: SecurityException) {
            throw AppException.FileException("Sin permisos para escribir archivo", e)
        }
    }

    private fun readUserDataFromFile(filePath: String): Map<String, Any> {
        return try {
            FileInputStream(filePath).use { fis ->
                InputStreamReader(fis, "UTF-8").use { reader ->
                    BufferedReader(reader).use { bufferedReader ->
                        val content = bufferedReader.readText()

                        // Parseo simple (en una app real usarías JSON parser)
                        parseSimpleJson(content)
                    }
                }
            }
        } catch (e: IOException) {
            throw AppException.FileException("Error al leer archivo", e)
        } catch (e: SecurityException) {
            throw AppException.FileException("Sin permisos para leer archivo", e)
        }
    }

    private fun deleteFile(filePath: String): Boolean {
        return try {
            val file = File(filePath)
            file.delete()
        } catch (e: SecurityException) {
            throw AppException.FileException("Sin permisos para eliminar archivo", e)
        }
    }

    private fun copyFile(sourcePath: String, targetPath: String) {
        try {
            FileInputStream(sourcePath).use { input ->
                FileOutputStream(targetPath).use { output ->
                    input.copyTo(output)
                }
            }
        } catch (e: IOException) {
            throw AppException.FileException("Error al copiar archivo", e)
        }
    }

    // Métodos auxiliares
    private fun getUserDataFilePath(fileName: String): String {
        return File(File(context.filesDir, "userdata"), fileName).absolutePath
    }

    private fun parseSimpleJson(content: String): Map<String, Any> {
        // Parser JSON simplificado para el ejemplo
        return try {
            val result = mutableMapOf<String, Any>()
            val lines = content.lines()

            lines.forEach { line ->
                if (line.contains(":")) {
                    val parts = line.split(":")
                    if (parts.size >= 2) {
                        val key = parts[0].trim().removeSurrounding("\"")
                        val value = parts[1].trim().removeSurrounding("\"", ",")
                        result[key] = value
                    }
                }
            }

            result
        } catch (e: Exception) {
            throw AppException.FileException("Error al parsear datos del archivo", e)
        }
    }

    private fun validateLoadedData(userData: Map<String, Any>) {
        if (userData.isEmpty()) {
            throw AppException.ValidationException("Archivo contiene datos vacíos")
        }
    }

    private fun verifyFileIntegrity(filePath: String) {
        try {
            val file = File(filePath)
            if (!file.exists() || file.length() == 0L) {
                throw IOException("Archivo creado incorrectamente")
            }
        } catch (e: IOException) {
            throw AppException.FileException("Error de integridad del archivo", e)
        }
    }

    private fun createBackupBeforeDelete(filePath: String) {
        try {
            val backupPath = "$filePath.backup"
            copyFile(filePath, backupPath)
        } catch (e: Exception) {
            // Log pero no fallar por el backup
            println("⚠️ No se pudo crear backup antes de eliminar")
        }
    }

    private fun verifyFileDeletion(filePath: String) {
        val file = File(filePath)
        if (file.exists()) {
            throw AppException.FileException("El archivo no fue eliminado correctamente")
        }
    }

    private fun checkSpaceForCopy(sourcePath: String) {
        try {
            val sourceFile = File(sourcePath)
            val fileSize = sourceFile.length()
            val availableSpace = context.filesDir.freeSpace

            if (availableSpace < fileSize * 2) {
                throw IOException("Espacio insuficiente para copiar archivo")
            }
        } catch (e: IOException) {
            throw AppException.FileException("Error al verificar espacio para copia", e)
        }
    }

    private fun verifyCopySuccess(sourcePath: String, targetPath: String) {
        try {
            val sourceFile = File(sourcePath)
            val targetFile = File(targetPath)

            if (!targetFile.exists()) {
                throw IOException("Archivo no fue copiado")
            }

            if (sourceFile.length() != targetFile.length()) {
                throw IOException("Copia incompleta - tamaños diferentes")
            }
        } catch (e: IOException) {
            throw AppException.FileException("Error al verificar copia", e)
        }
    }

    private fun createFileInfo(file: File): FileInfo {
        return try {
            FileInfo(
                name = file.name,
                size = file.length(),
                lastModified = file.lastModified(),
                isReadable = file.canRead(),
                isWritable = file.canWrite()
            )
        } catch (e: Exception) {
            throw AppException.FileException("Error al obtener información del archivo", e)
        }
    }

    // Manejadores de excepciones específicos
    private fun handleFileWriteException(e: Exception, fileName: String) {
        when (e) {
            is IOException -> println("💾 Error al escribir archivo $fileName: ${e.message}")
            is SecurityException -> println("🔒 Sin permisos para escribir $fileName")
            is OutOfMemoryError -> println("💾 Sin memoria para escribir $fileName")
            else -> println("💾 Error inesperado escribiendo $fileName: ${e.message}")
        }
    }

    private fun handleFileReadException(e: Exception, fileName: String) {
        when (e) {
            is FileNotFoundException -> println("📄 Archivo $fileName no encontrado")
            is IOException -> println("📄 Error al leer archivo $fileName: ${e.message}")
            is SecurityException -> println("🔒 Sin permisos para leer $fileName")
            else -> println("📄 Error inesperado leyendo $fileName: ${e.message}")
        }
    }

    private fun handleFileDeleteException(e: Exception, fileName: String) {
        when (e) {
            is SecurityException -> println("🔒 Sin permisos para eliminar $fileName")
            is IOException -> println("🗑️ Error al eliminar $fileName: ${e.message}")
            else -> println("🗑️ Error inesperado eliminando $fileName: ${e.message}")
        }
    }

    private fun handleFileCopyException(e: Exception, source: String, target: String) {
        when (e) {
            is IOException -> println("📋 Error al copiar $source a $target: ${e.message}")
            is SecurityException -> println("🔒 Sin permisos para copiar archivos")
            else -> println("📋 Error inesperado copiando archivos: ${e.message}")
        }
    }

    private fun handleFileListException(e: Exception) {
        when (e) {
            is SecurityException -> println("🔒 Sin permisos para listar archivos")
            is IOException -> println("📁 Error al acceder al directorio: ${e.message}")
            else -> println("📁 Error inesperado listando archivos: ${e.message}")
        }
    }
}

// Clase de información de archivo
data class FileInfo(
    val name: String,
    val size: Long,
    val lastModified: Long,
    val isReadable: Boolean,
    val isWritable: Boolean
)