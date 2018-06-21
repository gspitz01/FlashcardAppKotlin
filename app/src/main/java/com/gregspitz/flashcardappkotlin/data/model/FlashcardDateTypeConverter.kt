package com.gregspitz.flashcardappkotlin.data.model

import android.arch.persistence.room.TypeConverter
import java.util.*


class FlashcardDateTypeConverter {

    @TypeConverter
    fun dateToTimestamp(date: Date): Long {
        return date.time
    }

    @TypeConverter
    fun fromTimestamp(timestamp: Long): Date {
        return Date(timestamp)
    }
}
