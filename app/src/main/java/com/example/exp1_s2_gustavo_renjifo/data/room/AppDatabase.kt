package com.example.exp1_s2_gustavo_renjifo.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.exp1_s2_gustavo_renjifo.data.room.dao.MessageDao
import com.example.exp1_s2_gustavo_renjifo.data.room.dao.UserDao
import com.example.exp1_s2_gustavo_renjifo.data.room.entity.Message
import com.example.exp1_s2_gustavo_renjifo.data.room.entity.User

@Database(
    entities = [User::class, Message::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun messageDao(): MessageDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
