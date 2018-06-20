package com.gregspitz.flashcardappkotlin.data.model

import android.arch.persistence.room.TypeConverter

class FlashcardPriorityTypeConverter {

    @TypeConverter
    fun fromFlashcardPriority(priority: FlashcardPriority): Int {
        return when(priority) {
            FlashcardPriority.NEW -> 0
            FlashcardPriority.URGENT -> 1
            FlashcardPriority.HIGH -> 2
            FlashcardPriority.MEDIUM -> 3
            FlashcardPriority.LOW -> 4
        }
    }

    @TypeConverter
    fun intToFlashcardPriority(priority: Int): FlashcardPriority {
        return when(priority) {
            0 -> FlashcardPriority.NEW
            1 -> FlashcardPriority.URGENT
            2 -> FlashcardPriority.HIGH
            3 -> FlashcardPriority.MEDIUM
            4 -> FlashcardPriority.LOW
            else -> FlashcardPriority.NEW
        }
    }
}
