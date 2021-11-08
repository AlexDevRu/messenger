package com.example.data.database.converters

import androidx.room.TypeConverter
import java.util.*

class MessengerTypeConverters {
    @TypeConverter
    fun toUUID(uuid: String?): UUID? {
        return UUID.fromString(uuid)
    }
    @TypeConverter
    fun fromUUID(uuid: UUID?): String? {
        return uuid?.toString()
    }

    @TypeConverter
    fun toDate(time: Long?): Date? {
        return if(time != null) Date(time) else time
    }
    @TypeConverter
    fun fromDate(date: Date?): Long? {
        return date?.time
    }
}