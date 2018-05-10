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
