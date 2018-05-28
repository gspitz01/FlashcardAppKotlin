package com.gregspitz.flashcardappkotlin.data.source

import com.gregspitz.flashcardappkotlin.data.model.Flashcard

class FakeFlashcardRemoteDataSource : FlashcardDataSource {
    override fun getFlashcards(callback: FlashcardDataSource.GetFlashcardsCallback) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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

}
