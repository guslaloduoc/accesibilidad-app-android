package com.example.exp1_s2_gustavo_renjifo.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.exp1_s2_gustavo_renjifo.data.room.entity.Message
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {

    @Insert
    suspend fun insert(message: Message): Long

    @Query("SELECT * FROM messages WHERE userId = :uid ORDER BY timestamp DESC")
    fun getByUser(uid: Int): Flow<List<Message>>

    @Query("SELECT * FROM messages WHERE userId = :uid ORDER BY timestamp DESC")
    suspend fun getByUserOnce(uid: Int): List<Message>
}
