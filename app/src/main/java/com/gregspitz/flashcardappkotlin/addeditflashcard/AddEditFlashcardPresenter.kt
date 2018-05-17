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

import com.gregspitz.flashcardappkotlin.UseCase
import com.gregspitz.flashcardappkotlin.UseCaseHandler
import com.gregspitz.flashcardappkotlin.addeditflashcard.domain.usecase.GetFlashcard
import com.gregspitz.flashcardappkotlin.addeditflashcard.domain.usecase.SaveFlashcard
import com.gregspitz.flashcardappkotlin.data.model.Flashcard

/**
 * Presenter for AddEditFlashcard view
 */
class AddEditFlashcardPresenter (
        private val useCaseHandler: UseCaseHandler,
        private val view: AddEditFlashcardContract.View,
        private val getFlashcard: GetFlashcard,
        private val saveFlashcard: SaveFlashcard
) : AddEditFlashcardContract.Presenter {

    private lateinit var flashcardId: String

    init {
        view.setPresenter(this)
    }

    override fun start() {
        flashcardId = view.getIdFromArguments()
        if (flashcardId == AddEditFlashcardFragment.newFlashcardExtra) {
            view.showNewFlashcard()
        } else {
            loadFlashcard(flashcardId)
        }
    }

    override fun loadFlashcard(flashcardId: String) {
        view.setLoadingIndicator(true)
        useCaseHandler.execute(getFlashcard, GetFlashcard.RequestValues(flashcardId),
                object: UseCase.UseCaseCallback<GetFlashcard.ResponseValue> {
                    override fun onSuccess(response: GetFlashcard.ResponseValue) {
                        if (view.isActive()) {
                            view.setLoadingIndicator(false)
                            view.showFlashcard(response.flashcard)
                        }
                    }

                    override fun onError() {
                        if (view.isActive()) {
                            view.setLoadingIndicator(false)
                            view.showFailedToLoadFlashcard()
                        }
                    }

                })
    }

    override fun saveFlashcard(flashcard: Flashcard) {
        useCaseHandler.execute(saveFlashcard, SaveFlashcard.RequestValues(flashcard),
                object: UseCase.UseCaseCallback<SaveFlashcard.ResponseValue> {
                    override fun onSuccess(response: SaveFlashcard.ResponseValue) {
                        if (view.isActive()) {
                            view.showSaveSuccessful()
                        }
                    }

                    override fun onError() {
                        if (view.isActive()) {
                            view.showSaveFailed()
                        }
                    }
                })
    }

    override fun showList() {
        if (view.isActive()) {
            view.showFlashcardList(flashcardId)
        }
    }
}
