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
open class FlashcardRepository(private val localDataSource: FlashcardDataSource)
    : FlashcardDataSource {

    private val cache = mutableMapOf<String, Flashcard>()
    // Tells repo to go straight to local source for call to getFlashcards(), updates cache
    private var cacheDirty = true

    override fun getFlashcards(callback: FlashcardDataSource.GetFlashcardsCallback) {
        // If cache is dirty or empty try to get from local source
        if (cacheDirty || cache.isEmpty()) {
            getFlashcardsFromLocalSource(callback)
        } else {
            // If cache is neither dirty nor empty, simply get from cache
            callback.onFlashcardsLoaded(cache.values.toList())
        }
    }

    override fun getFlashcard(flashcardId: String,
                              callback: FlashcardDataSource.GetFlashcardCallback) {
        // Try to get it from the cache first
        if (!cacheDirty && cache.containsKey(flashcardId) && cache[flashcardId] != null) {
            callback.onFlashcardLoaded(cache[flashcardId]!!)
        } else {
            // Not in cache or cache is dirty
            getFlashcardFromLocalSource(flashcardId, callback)
        }

    }

    override fun saveFlashcard(flashcard: Flashcard,
                               callback: FlashcardDataSource.SaveFlashcardCallback) {
        localDataSource.saveFlashcard(flashcard, callback)
        cacheDirty = true
    }

    override fun deleteAllFlashcards() {
        localDataSource.deleteAllFlashcards()
        cache.clear()
        cacheDirty = true
    }

    override fun refreshFlashcards() {
        cacheDirty = true
    }

    private fun getFlashcardFromLocalSource(flashcardId: String,
                                            callback: FlashcardDataSource.GetFlashcardCallback) {
        localDataSource.getFlashcard(flashcardId, object: FlashcardDataSource.GetFlashcardCallback {
            override fun onFlashcardLoaded(flashcard: Flashcard) {
                callback.onFlashcardLoaded(flashcard)
            }

            override fun onDataNotAvailable() {
                callback.onDataNotAvailable()
            }
        })
    }

    private fun getFlashcardsFromLocalSource(callback: FlashcardDataSource.GetFlashcardsCallback) {
        localDataSource.getFlashcards(object: FlashcardDataSource.GetFlashcardsCallback {
            override fun onFlashcardsLoaded(flashcards: List<Flashcard>) {
                updateCache(flashcards)
                callback.onFlashcardsLoaded(flashcards)
            }

            override fun onDataNotAvailable() {
                callback.onDataNotAvailable()
            }

        })
    }

    private fun updateCache(flashcards: List<Flashcard>) {
        cache.clear()
        flashcards.forEach {
            cache[it.id] = it
        }
        cacheDirty = false
    }
}
