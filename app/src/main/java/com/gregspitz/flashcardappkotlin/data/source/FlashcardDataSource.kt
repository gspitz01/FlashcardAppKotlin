package com.gregspitz.flashcardappkotlin.data.source

import com.gregspitz.flashcardappkotlin.data.model.Flashcard

/**
 * Interface for flashcard data sources
 */
interface FlashcardDataSource {

    interface GetFlashcardsCallback {

        fun onFlashcardsLoaded(flashcards: List<Flashcard>)

        fun onDataNotAvailable()
    }

    fun getFlashcards(callback: GetFlashcardsCallback)

    interface GetFlashcardCallback {

        fun onFlashcardLoaded(flashcard: Flashcard)

        fun onDataNotAvailable()
    }

    fun getFlashcard(flashcardId: String, callback: GetFlashcardCallback)

    interface SaveFlashcardCallback {

        fun onSaveSuccessful()

        fun onSaveFailed()
    }

    fun saveFlashcard(flashcard: Flashcard, callback: SaveFlashcardCallback)

    fun deleteAllFlashcards()

    fun refreshFlashcards()
}
