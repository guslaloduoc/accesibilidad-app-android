package com.example.exp1_s2_gustavo_renjifo.services

import android.content.Context
import com.example.exp1_s2_gustavo_renjifo.data.room.AppDatabase

object ServiceLocator {

    private var database: AppDatabase? = null
    private var authRepository: AuthRepository? = null
    private var messageRepository: MessageRepository? = null

    fun provide(context: Context) {
        if (database == null) {
            database = AppDatabase.getDatabase(context)
            authRepository = AuthRepository(database!!.userDao())
            messageRepository = MessageRepository(database!!.messageDao())
        }
    }

    fun getAuthRepository(): AuthRepository {
        return authRepository ?: throw IllegalStateException("ServiceLocator not initialized. Call provide(context) first.")
    }

    fun getMessageRepository(): MessageRepository {
        return messageRepository ?: throw IllegalStateException("ServiceLocator not initialized. Call provide(context) first.")
    }

    fun getDatabase(): AppDatabase {
        return database ?: throw IllegalStateException("ServiceLocator not initialized. Call provide(context) first.")
    }
}
