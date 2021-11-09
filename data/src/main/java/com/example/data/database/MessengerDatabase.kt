package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.data.database.converters.MessengerTypeConverters
import com.example.data.database.dao.UserDao
import com.example.data.database.dao.remote_keys.UserRemoteKeysDao
import com.example.data.database.entities.ChatUserEntity
import com.example.data.database.entities.ChatUserImageEntity
import com.example.data.database.entities.ChatUserPhoneEntity
import com.example.data.database.entities.remote_keys.UserRemoteKeys

@Database(
    entities = [
        ChatUserEntity::class,
        ChatUserImageEntity::class,
        ChatUserPhoneEntity::class,
        UserRemoteKeys::class
    ],
    version = 1
)
@TypeConverters(MessengerTypeConverters::class)
abstract class MessengerDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun userRemoteKeysDao(): UserRemoteKeysDao

    companion object {

        private const val DATABASE_NAME = "messenger_database"

        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: MessengerDatabase? = null

        fun getDatabase(context: Context): MessengerDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MessengerDatabase::class.java,
                    DATABASE_NAME
                ).build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}
