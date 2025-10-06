// app/src/androidTest/java/com/example/exp1_s2_gustavo_renjifo/LoginFlowTest.kt
package com.example.exp1_s2_gustavo_renjifo

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.exp1_s2_gustavo_renjifo.services.ServiceLocator
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginFlowTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setup() {
        // Limpiar la base de datos antes de cada test
        val database = ServiceLocator.getDatabase()
        runBlocking {
            database.clearAllTables()
        }
    }

    @Test
    fun loginFlow_registerNewUser_loginSuccessfully_addMessage() {
        // Paso 1: Ir a la pantalla de registro
        composeTestRule.onNodeWithText("¿No tienes cuenta? Regístrate")
            .performClick()

        // Esperar a que se cargue la pantalla de registro
        composeTestRule.waitForIdle()

        // Paso 2: Registrar un nuevo usuario
        val testEmail = "test${System.currentTimeMillis()}@example.com"
        val testPassword = "password123"

        composeTestRule.onNodeWithText("Nombre completo")
            .performTextInput("Test User")

        composeTestRule.onNodeWithText("Email")
            .performTextInput(testEmail)

        composeTestRule.onAllNodesWithText("Contraseña")[0]
            .performTextInput(testPassword)

        // Hacer scroll si es necesario y clickear REGISTRARSE
        composeTestRule.onNodeWithText("REGISTRARSE")
            .performScrollTo()
            .performClick()

        // Esperar navegación a Home
        composeTestRule.waitForIdle()
        Thread.sleep(1000)

        // Paso 3: Verificar que estamos en la pantalla Home
        composeTestRule.onNodeWithText("Mis Mensajes", substring = true)
            .assertExists()

        // Paso 4: Agregar un mensaje
        val testMessage = "Mi primer mensaje de prueba"

        composeTestRule.onNodeWithText("Escribe un mensaje...")
            .performTextInput(testMessage)

        composeTestRule.onNodeWithText("Guardar")
            .performClick()

        // Esperar a que se guarde
        composeTestRule.waitForIdle()
        Thread.sleep(500)

        // Paso 5: Verificar que el mensaje aparece en la lista
        composeTestRule.onNodeWithText(testMessage, substring = true)
            .assertExists()
    }

    @Test
    fun loginFlow_existingUser_loginAndAddMessage() = runBlocking {
        // Paso 1: Crear un usuario de prueba directamente en la BD
        val testEmail = "existing@example.com"
        val testPassword = "mypassword"

        val authRepo = ServiceLocator.getAuthRepository()
        val signUpResult = authRepo.signUp(testEmail, testPassword)
        assert(signUpResult.isSuccess)

        // Paso 2: Iniciar sesión con el usuario existente
        composeTestRule.onAllNodesWithText("Email")[0]
            .performTextInput(testEmail)

        composeTestRule.onAllNodesWithText("Contraseña")[0]
            .performTextInput(testPassword)

        composeTestRule.onNodeWithText("INICIAR SESIÓN")
            .performClick()

        // Esperar navegación
        composeTestRule.waitForIdle()
        Thread.sleep(1000)

        // Paso 3: Verificar que estamos en Home
        composeTestRule.onNodeWithText("Mis Mensajes", substring = true)
            .assertExists()

        // Paso 4: Agregar un mensaje
        val message = "Mensaje desde test de login"

        composeTestRule.onNodeWithText("Escribe un mensaje...")
            .performTextInput(message)

        composeTestRule.onNodeWithText("Guardar")
            .performClick()

        composeTestRule.waitForIdle()
        Thread.sleep(500)

        // Paso 5: Verificar que el mensaje está visible
        composeTestRule.onNodeWithText(message, substring = true)
            .assertExists()
    }
}
