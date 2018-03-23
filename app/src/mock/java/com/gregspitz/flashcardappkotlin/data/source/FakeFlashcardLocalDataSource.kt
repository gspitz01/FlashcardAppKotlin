package com.gregspitz.flashcardappkotlin.data.source

import android.content.Context
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
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun saveFlashcard(flashcard: Flashcard, callback: FlashcardDataSource.SaveFlashcardCallback) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun deleteAllFlashcards() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun refreshFlashcards() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun addFlashcards(vararg flashcards: Flashcard) {
        flashcards.forEach { database.put(it.id, it) }
    }
}
