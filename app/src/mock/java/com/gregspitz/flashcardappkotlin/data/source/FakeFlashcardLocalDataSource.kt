package com.gregspitz.flashcardappkotlin.data.source

import android.util.Log
import com.gregspitz.flashcardappkotlin.data.model.Flashcard

/**
 * Fake local data source for testing
 */
class FakeFlashcardLocalDataSource : FlashcardDataSource {

    private val database : MutableMap<String, Flashcard> = mutableMapOf()
    private var failure = false

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

    override fun saveFlashcard(flashcard: Flashcard, callback: FlashcardDataSource.SaveFlashcardCallback) {
        if (failure) {
            callback.onSaveFailed()
            return
        }

        database.remove(flashcard.id)
        database[flashcard.id] = flashcard
        callback.onSaveSuccessful()
    }

    override fun deleteAllFlashcards() {
        database.clear()
        failure = false
    }

    override fun refreshFlashcards() {
        // Don't need to implement this, handled by FlashcardRepository
    }

    fun addFlashcards(vararg flashcards: Flashcard) {
        Log.d("FakeFlashcardRepo", "Adding cards: $this")
        flashcards.forEach { database[it.id] = it }
    }

    fun setFailure(failure: Boolean) {
        this.failure = failure
    }
}
