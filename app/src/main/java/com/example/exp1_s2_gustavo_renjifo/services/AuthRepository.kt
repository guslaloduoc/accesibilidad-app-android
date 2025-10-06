package com.example.exp1_s2_gustavo_renjifo.services

import com.example.exp1_s2_gustavo_renjifo.data.room.dao.UserDao
import com.example.exp1_s2_gustavo_renjifo.data.room.entity.User

class AuthRepository(private val userDao: UserDao) {

    suspend fun signUp(email: String, password: String): Result<Int> {
        return try {
            if (userDao.exists(email)) {
                Result.failure(Exception("El correo ya está registrado"))
            } else {
                val userId = userDao.insert(User(email = email, password = password))
                Result.success(userId.toInt())
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun login(email: String, password: String): Result<Int> {
        return try {
            val user = userDao.login(email, password)
            if (user != null) {
                Result.success(user.id)
            } else {
                Result.failure(Exception("Credenciales inválidas"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun recover(email: String): Result<Unit> {
        return try {
            if (userDao.exists(email)) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("El correo no está registrado"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
