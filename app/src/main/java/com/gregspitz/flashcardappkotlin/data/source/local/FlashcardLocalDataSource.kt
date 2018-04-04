package com.gregspitz.flashcardappkotlin.data.source.local

import com.gregspitz.flashcardappkotlin.SingletonHolder
import com.gregspitz.flashcardappkotlin.data.model.Flashcard
import com.gregspitz.flashcardappkotlin.data.source.FlashcardDataSource

/**
 * Local data source accepts a Room DAO for flashcards
 */
class FlashcardLocalDataSource(private val flashcardDao: FlashcardDao) : FlashcardDataSource {

    companion object: SingletonHolder<FlashcardLocalDataSource, FlashcardDao>(
            ::FlashcardLocalDataSource)

    override fun getFlashcards(callback: FlashcardDataSource.GetFlashcardsCallback) {
        callback.onFlashcardsLoaded(flashcardDao.getFlashcards())
    }

    override fun getFlashcard(flashcardId: String, callback: FlashcardDataSource.GetFlashcardCallback) {
        val flashcard = flashcardDao.getFlashcard(flashcardId)
        if (flashcard == null) {
            callback.onDataNotAvailable()
        } else {
            callback.onFlashcardLoaded(flashcard)
        }
    }

    override fun saveFlashcard(flashcard: Flashcard,
                               callback: FlashcardDataSource.SaveFlashcardCallback) {
        flashcardDao.insertFlashcard(flashcard)
        callback.onSaveSuccessful()
    }

    override fun deleteAllFlashcards() {
        flashcardDao.deleteFlashcards()
    }

    override fun refreshFlashcards() {
        // Don't need to implement this. Implemented by FlashcardRepository
    }

}
