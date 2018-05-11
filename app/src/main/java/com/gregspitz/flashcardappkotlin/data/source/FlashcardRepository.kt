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

import com.gregspitz.flashcardappkotlin.data.model.Flashcard

/**
 * Data source for flashcards
 */
open class FlashcardRepository(private val localDataSource: FlashcardDataSource) : FlashcardDataSource {

    override fun getFlashcards(callback: FlashcardDataSource.GetFlashcardsCallback) {
        localDataSource.getFlashcards(callback)
    }

    override fun getFlashcard(flashcardId: String, callback: FlashcardDataSource.GetFlashcardCallback) {
        localDataSource.getFlashcard(flashcardId, callback)
    }

    override fun saveFlashcard(flashcard: Flashcard, callback: FlashcardDataSource.SaveFlashcardCallback) {
        localDataSource.saveFlashcard(flashcard, callback)
    }

    override fun deleteAllFlashcards() {
        localDataSource.deleteAllFlashcards()
    }

    override fun refreshFlashcards() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
