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
import com.gregspitz.flashcardappkotlin.addeditflashcard.domain.usecase.SaveFlashcard
import com.gregspitz.flashcardappkotlin.categorylist.domain.usecase.GetCategories
import com.gregspitz.flashcardappkotlin.data.model.Flashcard
import com.gregspitz.flashcardappkotlin.data.model.FlashcardPriority
import com.gregspitz.flashcardappkotlin.data.model.FlashcardSide
import com.gregspitz.flashcardappkotlin.randomflashcard.domain.usecase.GetRandomFlashcard

/**
 * Presenter for RandomFlashcard view
 */
class RandomFlashcardPresenter(
        private val useCaseHandler: UseCaseHandler,
        private val view: RandomFlashcardContract.View,
        private val viewModel: RandomFlashcardContract.ViewModel,
        private val getRandomFlashcard: GetRandomFlashcard,
        private val getCategories: GetCategories,
        private val saveFlashcard: SaveFlashcard
) : RandomFlashcardContract.Presenter {

    private var flashcard: Flashcard? = null

    init {
        view.setPresenter(this)
    }

    override fun start() {
        loadNewFlashcard()
        loadCategories()
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
        useCaseHandler.execute(getRandomFlashcard,
                GetRandomFlashcard.RequestValues(flashcardId, view.getCategoryName()),
                object : UseCase.UseCaseCallback<GetRandomFlashcard.ResponseValue> {
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

    override fun saveFlashcard(priority: FlashcardPriority) {
        flashcard?.let {
            val newFlashcard = Flashcard(it.id, it.category, it.front, it.back, priority)
            useCaseHandler.execute(saveFlashcard, SaveFlashcard.RequestValues(newFlashcard),
                    object : UseCase.UseCaseCallback<SaveFlashcard.ResponseValue> {
                        override fun onSuccess(response: SaveFlashcard.ResponseValue) {
                            loadNewFlashcard()
                        }

                        override fun onError() {
                            // TODO: put log statement here
                            loadNewFlashcard()
                        }
                    })
        }
    }

    private fun loadCategories() {
        useCaseHandler.execute(getCategories, GetCategories.RequestValues(),
                object : UseCase.UseCaseCallback<GetCategories.ResponseValue> {
                    override fun onSuccess(response: GetCategories.ResponseValue) {
                        viewModel.setSpinnerCategories(response.categories)
                    }

                    override fun onError() {
                        if (view.isActive()) {
                            view.showFailedToLoadCategories()
                        }
                    }
                })
    }
}
