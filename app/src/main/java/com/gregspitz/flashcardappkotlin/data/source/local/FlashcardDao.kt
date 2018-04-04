package com.gregspitz.flashcardappkotlin.data.source.local

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import android.arch.persistence.room.Query
import com.gregspitz.flashcardappkotlin.data.model.Flashcard

/**
 * Database access object for Flashcards
 */
@Dao
interface FlashcardDao {

    @Query("SELECT * from flashcard")
    fun getFlashcards() : List<Flashcard>

    @Query("SELECT * from flashcard where id = :flashcardId")
    fun getFlashcard(flashcardId: String): Flashcard?

    @Insert(onConflict = REPLACE)
    fun insertFlashcard(flashcard: Flashcard)

    @Insert
    fun insertFlashcards(flashcards: List<Flashcard>)

    @Query("DELETE from flashcard")
    fun deleteFlashcards()
}
