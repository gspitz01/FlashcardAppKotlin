package com.gregspitz.flashcardappkotlin.flashcarddetail

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.gregspitz.flashcardappkotlin.data.model.Flashcard

class FlashcardDetailViewModel : ViewModel(), FlashcardDetailContract.ViewModel {

    val flashcard: MutableLiveData<Flashcard> = MutableLiveData()

    override fun setFlashcard(flashcard: Flashcard) {
        this.flashcard.value = flashcard
    }

    override fun getFlashcard(): Flashcard? {
        return flashcard.value
    }
}
