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

import com.gregspitz.flashcardappkotlin.UseCase
import com.gregspitz.flashcardappkotlin.UseCaseHandler
import com.gregspitz.flashcardappkotlin.flashcardlist.domain.usecase.GetFlashcards

/**
 * A presenter for FlashcardListView
 */
class FlashcardListPresenter(private val useCaseHandler: UseCaseHandler,
                             private val view: FlashcardListContract.View,
                             private val viewModel: FlashcardListContract.ViewModel,
                             private val getFlashcards: GetFlashcards)
    : FlashcardListContract.Presenter {

    init {
        view.setPresenter(this)
    }

    override fun start() {
        loadFlashcards()
    }

    override fun addFlashcard() {
        view.showAddFlashcard()
    }

    override fun loadFlashcards() {
        view.setLoadingIndicator(true)
        useCaseHandler.execute(getFlashcards, GetFlashcards.RequestValues(),
                object: UseCase.UseCaseCallback<GetFlashcards.ResponseValue> {
                    override fun onSuccess(response: GetFlashcards.ResponseValue) {
                        if (view.isActive()) {
                            view.setLoadingIndicator(false)
                            if (response.flashcards.isNotEmpty()) {
                                viewModel.setFlashcards(response.flashcards)
                            } else {
                                view.showNoFlashcardsToLoad()
                            }
                        }
                    }

                    override fun onError() {
                        if (view.isActive()) {
                            view.setLoadingIndicator(false)
                            view.showFailedToLoadFlashcards()
                        }
                    }

                })
    }

    override fun onFlashcardClick(flashcardPosition: Int) {
        view.showFlashcardDetailsUi(flashcardPosition)
    }

}
