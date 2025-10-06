// app/src/test/java/com/example/exp1_s2_gustavo_renjifo/UserDaoTest.kt
package com.example.exp1_s2_gustavo_renjifo

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.exp1_s2_gustavo_renjifo.data.room.AppDatabase
import com.example.exp1_s2_gustavo_renjifo.data.room.dao.UserDao
import com.example.exp1_s2_gustavo_renjifo.data.room.entity.User
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
class UserDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var userDao: UserDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        userDao = database.userDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insertUser_andRetrieveByEmail() = runBlocking {
        // Given
        val user = User(email = "test@example.com", password = "password123")

        // When
        userDao.insert(user)
        val retrieved = userDao.findByEmail("test@example.com")

        // Then
        assertNotNull(retrieved)
        assertEquals("test@example.com", retrieved?.email)
        assertEquals("password123", retrieved?.password)
    }

    @Test
    fun exists_returnsTrueWhenUserExists() = runBlocking {
        // Given
        val user = User(email = "exists@example.com", password = "pass")
        userDao.insert(user)

        // When
        val exists = userDao.exists("exists@example.com")

        // Then
        assertTrue(exists)
    }

    @Test
    fun exists_returnsFalseWhenUserDoesNotExist() = runBlocking {
        // When
        val exists = userDao.exists("nonexistent@example.com")

        // Then
        assertFalse(exists)
    }

    @Test
    fun login_returnsUserWhenCredentialsAreCorrect() = runBlocking {
        // Given
        val user = User(email = "login@example.com", password = "correctpass")
        userDao.insert(user)

        // When
        val loggedInUser = userDao.login("login@example.com", "correctpass")

        // Then
        assertNotNull(loggedInUser)
        assertEquals("login@example.com", loggedInUser?.email)
    }

    @Test
    fun login_returnsNullWhenCredentialsAreIncorrect() = runBlocking {
        // Given
        val user = User(email = "login@example.com", password = "correctpass")
        userDao.insert(user)

        // When
        val loggedInUser = userDao.login("login@example.com", "wrongpass")

        // Then
        assertNull(loggedInUser)
    }
}
