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
     * Get all Flashcards with a certain Category name
     * @param categoryName the name of the Category
     * @param callback to be called on success or failure
     */
    override fun getFlashcardsByCategoryName(categoryName: String, callback: FlashcardDataSource.GetFlashcardsCallback) {
        callback.onFlashcardsLoaded(flashcardDao.getFlashcardsByCategoryName(categoryName))
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


    override fun getCategories(callback: FlashcardDataSource.GetCategoriesCallback) {
        callback.onCategoriesLoaded(flashcardDao.getCategories())
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

    override fun saveFlashcards(flashcards: List<Flashcard>, callback: FlashcardDataSource.SaveFlashcardsCallback) {
        flashcardDao.insertFlashcards(flashcards)
        callback.onSaveSuccessful()
    }

    /**
     * Delete a single Flashcard from the data source
     */
    override fun deleteFlashcard(flashcardId: String, callback: FlashcardDataSource.DeleteFlashcardCallback) {
        flashcardDao.deleteFlashcard(flashcardId)
        callback.onDeleteSuccessful()
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
