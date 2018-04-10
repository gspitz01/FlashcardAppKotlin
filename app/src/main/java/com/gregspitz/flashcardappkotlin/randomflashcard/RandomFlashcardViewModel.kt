package com.gregspitz.flashcardappkotlin.randomflashcard

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.gregspitz.flashcardappkotlin.data.model.Flashcard
import com.gregspitz.flashcardappkotlin.data.model.FlashcardSide

/**
 * Holds RandomFlashcard LiveData for maintaining the same flashcard across rotation etc.
 */
class RandomFlashcardViewModel : ViewModel(), RandomFlashcardContract.ViewModel {

    val randomFlashcard: MutableLiveData<Flashcard> = MutableLiveData()

    val flashcardSide: MutableLiveData<FlashcardSide> = MutableLiveData()

    override fun setFlashcard(flashcard: Flashcard) {
        randomFlashcard.value = flashcard
    }

    override fun setFlashcardSide(flashcardSide: FlashcardSide) {
        this.flashcardSide.value = flashcardSide
    }

    override fun getFlashcard(): Flashcard? {
        return randomFlashcard.value
    }

    override fun getFlashcardSide(): FlashcardSide? {
        return flashcardSide.value
    }
}
