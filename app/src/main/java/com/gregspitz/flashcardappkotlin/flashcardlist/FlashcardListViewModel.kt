package com.gregspitz.flashcardappkotlin.flashcardlist

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.gregspitz.flashcardappkotlin.data.model.Flashcard

/**
 * ViewModel for FlashcardListActivity
 */
class FlashcardListViewModel : ViewModel(), FlashcardListContract.ViewModel {

    val flashcards: MutableLiveData<List<Flashcard>> = MutableLiveData()

    override fun setFlashcards(flashcards: List<Flashcard>) {
        this.flashcards.value = flashcards
    }

    override fun getFlashcards() : List<Flashcard>? {
        return flashcards.value
    }

}
