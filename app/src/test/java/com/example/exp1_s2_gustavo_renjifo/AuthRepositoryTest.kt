// app/src/test/java/com/example/exp1_s2_gustavo_renjifo/AuthRepositoryTest.kt
package com.example.exp1_s2_gustavo_renjifo

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.exp1_s2_gustavo_renjifo.data.room.AppDatabase
import com.example.exp1_s2_gustavo_renjifo.services.AuthRepository
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class AuthRepositoryTest {

    private lateinit var database: AppDatabase
    private lateinit var authRepository: AuthRepository

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        authRepository = AuthRepository(database.userDao())
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun signUp_successfullyCreatesUser() = runBlocking {
        // When
        val result = authRepository.signUp("test@example.com", "password123")

        // Then
        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull()!! > 0)
    }

    @Test
    fun signUp_failsWhenEmailAlreadyExists() = runBlocking {
        // Given
        authRepository.signUp("duplicate@example.com", "pass1")

        // When
        val result = authRepository.signUp("duplicate@example.com", "pass2")

        // Then
        assertTrue(result.isFailure)
        assertEquals("El correo ya está registrado", result.exceptionOrNull()?.message)
    }

    @Test
    fun login_successfulWithCorrectCredentials() = runBlocking {
        // Given
        authRepository.signUp("user@example.com", "mypassword")

        // When
        val result = authRepository.login("user@example.com", "mypassword")

        // Then
        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull()!! > 0)
    }

    @Test
    fun login_failsWithIncorrectPassword() = runBlocking {
        // Given
        authRepository.signUp("user@example.com", "correctpass")

        // When
        val result = authRepository.login("user@example.com", "wrongpass")

        // Then
        assertTrue(result.isFailure)
        assertEquals("Credenciales inválidas", result.exceptionOrNull()?.message)
    }

    @Test
    fun login_failsWithNonexistentEmail() = runBlocking {
        // When
        val result = authRepository.login("nonexistent@example.com", "anypass")

        // Then
        assertTrue(result.isFailure)
        assertEquals("Credenciales inválidas", result.exceptionOrNull()?.message)
    }

    @Test
    fun recover_successfulWhenEmailExists() = runBlocking {
        // Given
        authRepository.signUp("recover@example.com", "password")

        // When
        val result = authRepository.recover("recover@example.com")

        // Then
        assertTrue(result.isSuccess)
    }

    @Test
    fun recover_failsWhenEmailDoesNotExist() = runBlocking {
        // When
        val result = authRepository.recover("nonexistent@example.com")

        // Then
        assertTrue(result.isFailure)
        assertEquals("El correo no está registrado", result.exceptionOrNull()?.message)
    }
}
