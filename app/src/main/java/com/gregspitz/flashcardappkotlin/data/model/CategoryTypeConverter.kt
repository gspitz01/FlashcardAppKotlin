package com.gregspitz.flashcardappkotlin.data.model

import android.arch.persistence.room.TypeConverter

class CategoryTypeConverter {

    @TypeConverter
    fun toCategory(name: String): Category {
        return Category(name)
    }

    @TypeConverter
    fun toString(category: Category): String {
        return category.name
    }
}
