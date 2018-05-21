package com.gregspitz.flashcardappkotlin.data.source.remote

import com.gregspitz.flashcardappkotlin.data.model.Flashcard
import com.gregspitz.flashcardappkotlin.data.source.FlashcardDataSource
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FlashcardRemoteDataSource : FlashcardDataSource {

    private val flashcardService = FlashcardService.create()

    override fun getFlashcards(callback: FlashcardDataSource.GetFlashcardsCallback) {
        val call = flashcardService.getFlashcards()
        call.enqueue(object: Callback<List<Flashcard>> {
            override fun onFailure(call: Call<List<Flashcard>>?, t: Throwable?) {
                callback.onDataNotAvailable()
            }

            override fun onResponse(call: Call<List<Flashcard>>?, response: Response<List<Flashcard>>) {
                val flashcards = response.body()
                flashcards?.let {
                    callback.onFlashcardsLoaded(it)
                }
            }

        })
    }

    override fun getFlashcard(flashcardId: String, callback: FlashcardDataSource.GetFlashcardCallback) {
        val call = flashcardService.getFlashcardById(flashcardId)
        call.enqueue(object: Callback<Flashcard> {
            override fun onFailure(call: Call<Flashcard>?, t: Throwable?) {
                callback.onDataNotAvailable()
            }

            override fun onResponse(call: Call<Flashcard>?, response: Response<Flashcard>) {
                val flashcard = response.body()
                flashcard?.let {
                    callback.onFlashcardLoaded(it)
                }
            }

        })
    }

    override fun saveFlashcard(flashcard: Flashcard, callback: FlashcardDataSource.SaveFlashcardCallback) {
        val call = flashcardService.saveFlashcard(flashcard)
        call.enqueue(object: Callback<Flashcard> {
            override fun onFailure(call: Call<Flashcard>?, t: Throwable?) {
                callback.onSaveFailed()
            }

            override fun onResponse(call: Call<Flashcard>?, response: Response<Flashcard>?) {
                callback.onSaveSuccessful()
            }

        })
    }

    override fun deleteAllFlashcards() {
        // Don't allow this
    }

    override fun refreshFlashcards() {
        // Don't need to implement this. Implemented by FlashcardRepository
    }

}
