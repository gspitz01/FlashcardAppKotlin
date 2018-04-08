package com.gregspitz.flashcardappkotlin.data.source

import android.util.Log
import com.gregspitz.flashcardappkotlin.SingletonHolder
import com.gregspitz.flashcardappkotlin.data.model.Flashcard

/**
 * Data source for flashcards
 */
open class FlashcardRepository(private val localDataSource: FlashcardDataSource) : FlashcardDataSource {

    companion object : SingletonHolder<FlashcardRepository, FlashcardDataSource>(
            ::FlashcardRepository)

    override fun getFlashcards(callback: FlashcardDataSource.GetFlashcardsCallback) {
        localDataSource.getFlashcards(callback)
    }

    override fun getFlashcard(flashcardId: String, callback: FlashcardDataSource.GetFlashcardCallback) {
        Log.d("FlashcardRepository", "Getting from local: $localDataSource")
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
