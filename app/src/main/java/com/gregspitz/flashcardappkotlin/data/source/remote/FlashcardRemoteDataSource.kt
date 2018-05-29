package com.gregspitz.flashcardappkotlin.data.source.remote

import com.gregspitz.flashcardappkotlin.data.model.Flashcard
import com.gregspitz.flashcardappkotlin.data.source.FlashcardDataSource
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FlashcardRemoteDataSource : FlashcardDataSource {

    private val flashcardService = FlashcardService.create()

    override fun getFlashcards(callback: FlashcardDataSource.GetFlashcardsCallback) {
        val flashcards = flashcardService.getFlashcards().execute().body()
        if (flashcards != null) {
            callback.onFlashcardsLoaded(flashcards)
        } else {
            callback.onDataNotAvailable()
        }
    }

    override fun getFlashcard(flashcardId: String, callback: FlashcardDataSource.GetFlashcardCallback) {
        val flashcard = flashcardService.getFlashcardById(flashcardId).execute().body()
        if (flashcard != null) {
            callback.onFlashcardLoaded(flashcard)
        } else {
            callback.onDataNotAvailable()
        }
    }

    override fun saveFlashcard(flashcard: Flashcard, callback: FlashcardDataSource.SaveFlashcardCallback) {
        val returnFlashcard = flashcardService.saveFlashcard(flashcard).execute().body()
        if (returnFlashcard != null) {
            callback.onSaveSuccessful()
        } else {
            callback.onSaveFailed()
        }
    }

    override fun deleteAllFlashcards() {
        // Don't allow this
    }

    override fun refreshFlashcards() {
        // Don't need to implement this. Implemented by FlashcardRepository
    }

}
