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

import com.gregspitz.flashcardappkotlin.UseCase
import com.gregspitz.flashcardappkotlin.UseCaseHandler
import com.gregspitz.flashcardappkotlin.data.model.Flashcard
import com.gregspitz.flashcardappkotlin.data.model.FlashcardSide
import com.gregspitz.flashcardappkotlin.randomflashcard.domain.usecase.GetRandomFlashcard

/**
 * Presenter for RandomFlashcard view
 */
class RandomFlashcardPresenter(
        private val useCaseHandler: UseCaseHandler,
        private val view: RandomFlashcardContract.View,
        private val viewModel: RandomFlashcardContract.ViewModel,
        private val getRandomFlashcard: GetRandomFlashcard
) : RandomFlashcardContract.Presenter {

    private var flashcard: Flashcard? = null

    init {
        view.setPresenter(this)
    }

    override fun start() {
        loadNewFlashcard()
    }

    override fun turnFlashcard() {
        if (view.isActive()) {
            when (viewModel.getFlashcardSide()) {
                FlashcardSide.FRONT -> viewModel.setFlashcardSide(FlashcardSide.BACK)
                FlashcardSide.BACK -> viewModel.setFlashcardSide(FlashcardSide.FRONT)
            }
        }
    }

    override fun loadNewFlashcard() {
        if (view.isActive()) {
            view.setLoadingIndicator(true)
        }

        val flashcardId = flashcard?.id
        useCaseHandler.execute(
                getRandomFlashcard, GetRandomFlashcard.RequestValues(flashcardId),
                object: UseCase.UseCaseCallback<GetRandomFlashcard.ResponseValue> {
                    override fun onSuccess(response: GetRandomFlashcard.ResponseValue) {
                        flashcard = response.flashcard
                        if (view.isActive()) {
                            view.setLoadingIndicator(false)
                            // Sure at this point flashcard isn't null so safe to use !!
                            viewModel.setFlashcard(flashcard!!)
                            viewModel.setFlashcardSide(FlashcardSide.FRONT)
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
}
