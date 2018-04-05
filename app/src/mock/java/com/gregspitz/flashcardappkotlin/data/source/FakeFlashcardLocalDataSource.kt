package com.gregspitz.flashcardappkotlin.data.source

import android.content.Context
import android.util.Log
import com.gregspitz.flashcardappkotlin.SingletonHolder
import com.gregspitz.flashcardappkotlin.data.model.Flashcard

/**
 * Fake local data source for testing
 */
class FakeFlashcardLocalDataSource(context: Context) : FlashcardDataSource {

    private val database : MutableMap<String, Flashcard> = mutableMapOf()

    companion object : SingletonHolder<FakeFlashcardLocalDataSource, Context>(
            ::FakeFlashcardLocalDataSource)

    override fun getFlashcards(callback: FlashcardDataSource.GetFlashcardsCallback) {
        callback.onFlashcardsLoaded(database.values.toList())
    }

    override fun getFlashcard(flashcardId: String, callback: FlashcardDataSource.GetFlashcardCallback) {
        val flashcard = database.get(flashcardId)
        if (flashcard == null) {
            callback.onDataNotAvailable()
        } else {
            callback.onFlashcardLoaded(flashcard)
        }
    }

    override fun saveFlashcard(flashcard: Flashcard, callback: FlashcardDataSource.SaveFlashcardCallback) {
        database.remove(flashcard.id)
        database[flashcard.id] = flashcard
    }

    override fun deleteAllFlashcards() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun refreshFlashcards() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun addFlashcards(vararg flashcards: Flashcard) {
        flashcards.forEach { database[it.id] = it }
    }

    fun clearFlashcards() {
        database.clear()
    }
}
