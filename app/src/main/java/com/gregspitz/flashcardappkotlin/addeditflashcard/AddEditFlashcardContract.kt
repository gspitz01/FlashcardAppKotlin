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

package com.gregspitz.flashcardappkotlin.addeditflashcard

import com.gregspitz.flashcardappkotlin.BasePresenter
import com.gregspitz.flashcardappkotlin.BaseView
import com.gregspitz.flashcardappkotlin.data.model.Flashcard

/**
 * Contract between AddEditFlashcard view and its presenter
 */
interface AddEditFlashcardContract {

    interface View : BaseView<Presenter> {

        fun setLoadingIndicator(active: Boolean)

        fun getIdFromArguments() : String

        fun setFlashcard(flashcardId: String)

        fun showFlashcard(flashcard: Flashcard)

        fun showNewFlashcard()

        fun showFlashcardList(flashcardId: String)

        fun showFailedToLoadFlashcard()

        fun showSaveSuccessful(flashcardId: String, categoryName: String)

        fun showSaveFailed()

        fun showDeleteFailed()

        fun isActive() : Boolean
    }

    interface Presenter : BasePresenter {

        fun loadFlashcard(flashcardId: String)

        fun saveFlashcard(flashcard: Flashcard)

        fun deleteFlashcard(flashcardId: String)

        fun showList()
    }
}
