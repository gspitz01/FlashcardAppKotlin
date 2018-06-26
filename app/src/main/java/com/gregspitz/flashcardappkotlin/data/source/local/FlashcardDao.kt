/*
 * Copyright (C) 2018 Greg Spitz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gregspitz.flashcardappkotlin.data.source.local

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import android.arch.persistence.room.Query
import com.gregspitz.flashcardappkotlin.data.model.Category
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

    @Query("SELECT DISTINCT category from flashcard")
    fun getCategories(): List<Category>

    @Insert(onConflict = REPLACE)
    fun insertFlashcard(flashcard: Flashcard)

    @Insert(onConflict = REPLACE)
    fun insertFlashcards(flashcards: List<Flashcard>)

    @Query("DELETE from flashcard where id = :flashcardId")
    fun deleteFlashcard(flashcardId: String)

    @Query("DELETE from flashcard")
    fun deleteFlashcards()
}
