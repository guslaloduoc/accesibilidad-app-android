package com.example.exp1_s2_gustavo_renjifo.data.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "messages",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["userId"])]
)
data class Message(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int,
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)
