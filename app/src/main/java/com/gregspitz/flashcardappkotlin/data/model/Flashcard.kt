package com.gregspitz.flashcardappkotlin.data.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

/**
 * An immutable text flashcard with a front and a back
 */
@Parcelize
@Entity(tableName = "flashcard")
data class Flashcard(@PrimaryKey val id: String = UUID.randomUUID().toString(),
                     val front: String, val back: String) : Parcelable
