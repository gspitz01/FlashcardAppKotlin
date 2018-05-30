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

import com.gregspitz.flashcardappkotlin.BasePresenter
import com.gregspitz.flashcardappkotlin.BaseView
import com.gregspitz.flashcardappkotlin.data.model.Flashcard
import com.gregspitz.flashcardappkotlin.data.model.FlashcardListItem

/**
 * A contract between the flashcard list view and its presenter
 */

interface FlashcardListContract {
    interface View : BaseView<Presenter> {

        fun setLoadingIndicator(active: Boolean)

        fun showFailedToLoadFlashcards()

        fun showNoFlashcardsToLoad()

        fun showAddFlashcard()

        fun setDetailView(flashcardId: String)

        fun showFlashcardDetailsUi(recyclerPosition: Int)

        fun showEditFlashcard(flashcardId: String)

        fun isActive(): Boolean
    }

    interface Presenter : BasePresenter {

        fun addFlashcard()

        fun loadFlashcards()

        fun onFlashcardClick(recyclerPosition: Int)
    }

    interface ViewModel {

        fun setFlashcards(flashcards: List<FlashcardListItem>)

        fun getFlashcards() : List<FlashcardListItem>?
    }
}
