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

import com.gregspitz.flashcardappkotlin.data.model.Flashcard
import com.gregspitz.flashcardappkotlin.data.source.FlashcardDataSource

/**
 * Local data source accepts a Room DAO for flashcards
 */
class FlashcardLocalDataSource(private val flashcardDao: FlashcardDao) : FlashcardDataSource {

    override fun getFlashcards(callback: FlashcardDataSource.GetFlashcardsCallback) {
        callback.onFlashcardsLoaded(flashcardDao.getFlashcards())
    }

    override fun getFlashcard(flashcardId: String, callback: FlashcardDataSource.GetFlashcardCallback) {
        val flashcard = flashcardDao.getFlashcard(flashcardId)
        if (flashcard == null) {
            callback.onDataNotAvailable()
        } else {
            callback.onFlashcardLoaded(flashcard)
        }
    }

    override fun saveFlashcard(flashcard: Flashcard,
                               callback: FlashcardDataSource.SaveFlashcardCallback) {
        flashcardDao.insertFlashcard(flashcard)
        callback.onSaveSuccessful()
    }

    override fun deleteAllFlashcards() {
        flashcardDao.deleteFlashcards()
    }

    override fun refreshFlashcards() {
        // Don't need to implement this. Implemented by FlashcardRepository
    }

}
