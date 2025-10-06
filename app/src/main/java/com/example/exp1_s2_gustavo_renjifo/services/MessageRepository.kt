package com.example.exp1_s2_gustavo_renjifo.services

import com.example.exp1_s2_gustavo_renjifo.data.room.dao.MessageDao
import com.example.exp1_s2_gustavo_renjifo.data.room.entity.Message
import kotlinx.coroutines.flow.Flow

class MessageRepository(private val messageDao: MessageDao) {

    fun messages(uid: Int): Flow<List<Message>> {
        return messageDao.getByUser(uid)
    }

    suspend fun add(uid: Int, text: String): Result<Unit> {
        return try {
            if (text.isBlank()) {
                Result.failure(Exception("El mensaje no puede estar vacío"))
            } else {
                messageDao.insert(Message(userId = uid, text = text.trim()))
                Result.success(Unit)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
