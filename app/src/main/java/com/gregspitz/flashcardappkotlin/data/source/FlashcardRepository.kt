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
 * Repository for Flashcards
 * Uses a local data source (i.e. Room database) and a cache
 */
open class FlashcardRepository(private val localDataSource: FlashcardDataSource)
    : FlashcardDataSource {

    // The cache
    private var cache = mutableMapOf<String, Flashcard>()

    // Tells repo to go straight to local source for call to getFlashcards(), updates cache
    private var cacheDirty = true

    /**
     * Get all Flashcards from either the cache or the local data source
     * @param callback to be called with either success or failure
     */
    override fun getFlashcards(callback: FlashcardDataSource.GetFlashcardsCallback) {
        // If cache is dirty or empty try to get from local source
        if (cacheDirty || cache.isEmpty()) {
            getFlashcardsFromLocalSource(callback)
        } else {
            // If cache is neither dirty nor empty, simply get from cache
            callback.onFlashcardsLoaded(cache.values.toList())
        }
    }

    /**
     * Get all the Flashcards with a certain Category name
     * @param categoryName the name of the Category
     * @param callback to be called with either success or failure
     */
    override fun getFlashcardsByCategoryName(categoryName: String, callback: FlashcardDataSource.GetFlashcardsCallback) {
        // Try to get from the cache first
        if (!cacheDirty) {
            callback.onFlashcardsLoaded(cache.values.filter {
                it.category == categoryName
            })
        } else {
            localDataSource.getFlashcardsByCategoryName(categoryName, callback)
        }
    }

    /**
     * Get a single Flashcard either from the cache or the local data source
     * @param flashcardId the id of the Flashcard to be found
     * @param callback to be called with either success or failure
     */
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

    /**
     * Get all available categories
     * @param callback to be called on either success or failure
     */
    override fun getCategories(callback: FlashcardDataSource.GetCategoriesCallback) {
        localDataSource.getCategories(callback)
    }

    /**
     * Save a Flashcard to the local data source
     * Sets the cache dirty so it will be updated on next call to getFlashcards
     * @param flashcard the Flashcard to be saved
     * @param callback to be called on either success or failure
     */
    override fun saveFlashcard(flashcard: Flashcard,
                               callback: FlashcardDataSource.SaveFlashcardCallback) {
        localDataSource.saveFlashcard(flashcard, callback)
        cacheDirty = true
    }

    /**
     * Save multiple Flashcards to the local data source
     * Sets the cache dirty so it will be updated on next call to getFlashcards
     * @param flashcards List of Flashcards to be saved
     * @param callback to be called on either success or failure
     */
    override fun saveFlashcards(flashcards: List<Flashcard>, callback: FlashcardDataSource.SaveFlashcardsCallback) {
        localDataSource.saveFlashcards(flashcards, callback)
        cacheDirty = true
    }

    /**
     * Delete a single Flashcard from the repository
     */
    override fun deleteFlashcard(flashcardId: String, callback: FlashcardDataSource.DeleteFlashcardCallback) {
        cache.remove(flashcardId)
        localDataSource.deleteFlashcard(flashcardId, callback)
    }

    /**
     * Delete all the Flashcards from the local data source and the cache
     */
    override fun deleteAllFlashcards(callback: FlashcardDataSource.DeleteAllFlashcardsCallback) {
        localDataSource.deleteAllFlashcards(callback)
        cache.clear()
        cacheDirty = true
    }

    /**
     * Delete all Flashcards with a certain Category name
     */
    override fun deleteFlashcardsByCategoryName(
            categoryName: String,
            callback: FlashcardDataSource.DeleteFlashcardsByCategoryNameCallback) {
        localDataSource.deleteFlashcardsByCategoryName(categoryName, callback)
        cache = cache.filter {
            it.value.category != categoryName
        }.toMutableMap()
    }

    /**
     * Set cache dirty so it will be updated on next call to getFlashcards
     */
    override fun refreshFlashcards() {
        cacheDirty = true
    }

    /**
     * Get a single Flashcard from the local data source
     * @param flashcardId the id of the Flashcard to be found
     * @param callback to be called on either success or failure
     */
    private fun getFlashcardFromLocalSource(flashcardId: String,
                                            callback: FlashcardDataSource.GetFlashcardCallback) {
        localDataSource.getFlashcard(flashcardId, callback)
    }

    /**
     * Get all Flashcards from the local data source
     * Update the cache
     * @param callback to be called on either success or failure
     */
    private fun getFlashcardsFromLocalSource(callback: FlashcardDataSource.GetFlashcardsCallback) {
        localDataSource.getFlashcards(object : FlashcardDataSource.GetFlashcardsCallback {
            override fun onFlashcardsLoaded(flashcards: List<Flashcard>) {
                updateCache(flashcards)
                callback.onFlashcardsLoaded(flashcards)
            }

            override fun onDataNotAvailable() {
                callback.onDataNotAvailable()
            }

        })
    }

    /**
     * Update the cache
     * @param flashcards list of Flashcards to replace current contents of cache
     */
    private fun updateCache(flashcards: List<Flashcard>) {
        cache.clear()
        flashcards.forEach {
            cache[it.id] = it
        }
        cacheDirty = false
    }
}
