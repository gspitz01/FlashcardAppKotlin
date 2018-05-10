/*
 * Copyright (C) 2018 Greg Spitz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
