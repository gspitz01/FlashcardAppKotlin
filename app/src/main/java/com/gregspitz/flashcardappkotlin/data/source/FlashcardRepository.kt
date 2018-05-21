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
open class FlashcardRepository(private val localDataSource: FlashcardDataSource,
                               private val remoteDataSource: FlashcardDataSource)
    : FlashcardDataSource {

    private val cache = mutableMapOf<String, Flashcard>()
    // Tells repo to only go to remote source for call to getFlashcards(), updates cache and local
    private var cacheDirty = true

    override fun getFlashcards(callback: FlashcardDataSource.GetFlashcardsCallback) {
        if (cacheDirty) {
            getFlashcardsFromRemoteSource(callback)
        } else {
            if (cache.isNotEmpty()) {
                callback.onFlashcardsLoaded(cache.values.toList())
            } else {
                // Never end up here
                getFlashcardsFromLocalSource(callback)
            }
        }
    }

    override fun getFlashcard(flashcardId: String,
                              callback: FlashcardDataSource.GetFlashcardCallback) {
        // Try to get it from the cache first
        if (!cacheDirty && cache.containsKey(flashcardId)) {
            val flashcard = cache[flashcardId]
            if (flashcard != null) {
                callback.onFlashcardLoaded(flashcard)
            } else {
                // This should never happen but let's be careful anyway
                getFlashcardFromLocalSource(flashcardId, callback)
            }
        } else {
            // Not in cache or cache is dirty
            getFlashcardFromLocalSource(flashcardId, callback)
        }

    }

    override fun saveFlashcard(flashcard: Flashcard,
                               callback: FlashcardDataSource.SaveFlashcardCallback) {
        remoteDataSource.saveFlashcard(flashcard, callback)
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
                // Make sure cache gets updated on next call to getFlashcards()
                cacheDirty = true
            }

            override fun onDataNotAvailable() {
                // Couldn't find it in local source, so try from remote source
                getFlashcardFromRemoteSource(flashcardId, callback)
            }
        })
    }

    private fun getFlashcardsFromLocalSource(callback: FlashcardDataSource.GetFlashcardsCallback) {
        localDataSource.getFlashcards(object: FlashcardDataSource.GetFlashcardsCallback {
            override fun onFlashcardsLoaded(flashcards: List<Flashcard>) {
                callback.onFlashcardsLoaded(flashcards)
            }

            override fun onDataNotAvailable() {
                getFlashcardsFromRemoteSource(callback)
            }

        })
    }

    private fun getFlashcardFromRemoteSource(flashcardId: String,
                                             callback: FlashcardDataSource.GetFlashcardCallback) {
        remoteDataSource.getFlashcard(flashcardId, callback)
        // Make sure cache gets updated on next call to getFlashcards()
        cacheDirty = true
    }

    private fun getFlashcardsFromRemoteSource(callback: FlashcardDataSource.GetFlashcardsCallback) {
        remoteDataSource.getFlashcards(object: FlashcardDataSource.GetFlashcardsCallback {
            override fun onFlashcardsLoaded(flashcards: List<Flashcard>) {
                updateLocalSource(flashcards)
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

    private fun updateLocalSource(flashcards: List<Flashcard>) {
        flashcards.forEach {
            localDataSource.saveFlashcard(it, object: FlashcardDataSource.SaveFlashcardCallback {
                override fun onSaveSuccessful() { /* Ignore */ }
                override fun onSaveFailed() { /* Ignore */ }
            })
        }
    }
}
