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
 * Interface for flashcard data sources
 */
interface FlashcardDataSource {

    interface GetFlashcardsCallback {

        fun onFlashcardsLoaded(flashcards: List<Flashcard>)

        fun onDataNotAvailable()
    }

    fun getFlashcards(callback: GetFlashcardsCallback)

    fun getFlashcardsByCategoryName(categoryName: String, callback: GetFlashcardsCallback)

    interface GetFlashcardCallback {

        fun onFlashcardLoaded(flashcard: Flashcard)

        fun onDataNotAvailable()
    }

    fun getFlashcard(flashcardId: String, callback: GetFlashcardCallback)

    interface GetCategoriesCallback {

        fun onCategoriesLoaded(categories: List<Category>)

        fun onDataNotAvailable()
    }

    fun getCategories(callback: GetCategoriesCallback)

    interface SaveFlashcardCallback {

        fun onSaveSuccessful()

        fun onSaveFailed()
    }

    fun saveFlashcard(flashcard: Flashcard, callback: SaveFlashcardCallback)

    interface SaveFlashcardsCallback {

        fun onSaveSuccessful()

        fun onSaveFailed()
    }

    fun saveFlashcards(flashcards: List<Flashcard>, callback: SaveFlashcardsCallback)

    interface DeleteFlashcardCallback {

        fun onDeleteSuccessful()

        fun onDeleteFailed()
    }

    fun deleteFlashcard(flashcardId: String, callback: DeleteFlashcardCallback)

    fun deleteAllFlashcards()

    fun refreshFlashcards()
}
