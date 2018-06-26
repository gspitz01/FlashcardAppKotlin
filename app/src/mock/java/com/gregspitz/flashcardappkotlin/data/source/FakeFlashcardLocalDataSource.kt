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

package com.gregspitz.flashcardappkotlin.data.source

import com.gregspitz.flashcardappkotlin.data.model.Category
import com.gregspitz.flashcardappkotlin.data.model.Flashcard

/**
 * Fake local data source for testing
 */
class FakeFlashcardLocalDataSource : FlashcardDataSource {

    // TODO: add comments to this

    private val database : MutableMap<String, Flashcard> = mutableMapOf()
    private var failure = false
    private var deleteFailure = false

    override fun getFlashcards(callback: FlashcardDataSource.GetFlashcardsCallback) {
        if (failure) {
            callback.onDataNotAvailable()
            return
        }

        callback.onFlashcardsLoaded(database.values.toList())

    }

    override fun getFlashcard(flashcardId: String, callback: FlashcardDataSource.GetFlashcardCallback) {
        if (failure) {
            callback.onDataNotAvailable()
            return
        }

        val flashcard = database.get(flashcardId)
        if (flashcard == null) {
            callback.onDataNotAvailable()
        } else {
            callback.onFlashcardLoaded(flashcard)
        }
    }

    override fun getCategories(callback: FlashcardDataSource.GetCategoriesCallback) {
        if (failure) {
            callback.onDataNotAvailable()
        }

        val categories = database.values.map {
            it.category
        }.distinct().map {
            Category(it)
        }
        callback.onCategoriesLoaded(categories)
    }

    override fun saveFlashcard(flashcard: Flashcard, callback: FlashcardDataSource.SaveFlashcardCallback) {
        if (failure) {
            callback.onSaveFailed()
            return
        }

        database.remove(flashcard.id)
        database[flashcard.id] = flashcard
        callback.onSaveSuccessful()
    }

    override fun saveFlashcards(flashcards: List<Flashcard>, callback: FlashcardDataSource.SaveFlashcardsCallback) {
        if (failure) {
            callback.onSaveFailed()
            return
        }

        for (flashcard in flashcards) {
            database.remove(flashcard.id)
            database[flashcard.id] = flashcard
        }
        callback.onSaveSuccessful()
    }

    override fun deleteFlashcard(flashcardId: String, callback: FlashcardDataSource.DeleteFlashcardCallback) {
        if (deleteFailure) {
            callback.onDeleteFailed()
            return
        }

        database.remove(flashcardId)
        callback.onDeleteSuccessful()
    }

    override fun deleteAllFlashcards() {
        database.clear()
        failure = false
    }

    override fun refreshFlashcards() {
        // Don't need to implement this, handled by FlashcardRepository
    }

    fun addFlashcards(vararg flashcards: Flashcard) {
        flashcards.forEach { database[it.id] = it }
    }

    fun setFailure(failure: Boolean) {
        this.failure = failure
    }

    fun setDeleteFailure(failure: Boolean) {
        this.deleteFailure = failure
    }
}
