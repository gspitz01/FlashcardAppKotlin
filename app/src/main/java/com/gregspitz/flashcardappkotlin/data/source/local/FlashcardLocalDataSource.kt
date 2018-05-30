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
 * Local data source accepts a Room DAO for Flashcards
 */
class FlashcardLocalDataSource(private val flashcardDao: FlashcardDao) : FlashcardDataSource {

    /**
     * Get all Flashcards from the dao
     * @param callback to be called on success or failure
     */
    override fun getFlashcards(callback: FlashcardDataSource.GetFlashcardsCallback) {
        callback.onFlashcardsLoaded(flashcardDao.getFlashcards())
    }

    /**
     * Get a single Flashcard from a dao based on the Flashcard id
     * @param flashcardId the id of the Flashcard to be found
     * @param callback to be called on success or failure
     */
    override fun getFlashcard(flashcardId: String, callback: FlashcardDataSource.GetFlashcardCallback) {
        val flashcard = flashcardDao.getFlashcard(flashcardId)
        if (flashcard == null) {
            callback.onDataNotAvailable()
        } else {
            callback.onFlashcardLoaded(flashcard)
        }
    }

    /**
     * Save a Flashcard to the dao
     * Currently no mechanism for failed save as inserts will always replace any conflicting content
     * @param flashcard the Flashcard to be saved
     * @param callback to be called on success or failure
     */
    override fun saveFlashcard(flashcard: Flashcard,
                               callback: FlashcardDataSource.SaveFlashcardCallback) {
        flashcardDao.insertFlashcard(flashcard)
        callback.onSaveSuccessful()
    }

    /**
     * Delete all the Flashcards from the dao
     */
    override fun deleteAllFlashcards() {
        flashcardDao.deleteFlashcards()
    }

    override fun refreshFlashcards() {
        // Don't need to implement this. Implemented by FlashcardRepository
    }

}
