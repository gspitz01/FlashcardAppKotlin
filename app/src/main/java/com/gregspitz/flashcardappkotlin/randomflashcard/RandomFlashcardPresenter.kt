package com.gregspitz.flashcardappkotlin.randomflashcard

import com.gregspitz.flashcardappkotlin.UseCase
import com.gregspitz.flashcardappkotlin.UseCaseHandler
import com.gregspitz.flashcardappkotlin.data.model.Flashcard
import com.gregspitz.flashcardappkotlin.randomflashcard.domain.usecase.GetRandomFlashcard

/**
 * Presenter for RandomFlashcard view
 */
class RandomFlashcardPresenter(
        private val useCaseHandler: UseCaseHandler,
        private val view: RandomFlashcardContract.View,
        private val getRandomFlashcard: GetRandomFlashcard
) : RandomFlashcardContract.Presenter {

    private var flashcard: Flashcard? = null
    private var showingFront = false

    init {
        view.setPresenter(this)
    }

    override fun start() {
        loadNewFlashcard()
    }

    override fun turnFlashcard() {
        if (view.isActive()) {
            if (flashcard != null) {
                if (showingFront) {
                    view.showFlashcardSide(flashcard!!.back)
                } else {
                    view.showFlashcardSide(flashcard!!.front)
                }
                showingFront = !showingFront
            }
        }
    }

    override fun loadNewFlashcard() {
        view.setLoadingIndicator(true)

        val flashcardId = flashcard?.id
        useCaseHandler.execute(
                getRandomFlashcard, GetRandomFlashcard.RequestValues(flashcardId),
                object: UseCase.UseCaseCallback<GetRandomFlashcard.ResponseValue> {
                    override fun onSuccess(response: GetRandomFlashcard.ResponseValue) {
                        flashcard = response.flashcard
                        if (view.isActive()) {
                            view.setLoadingIndicator(false)
                            // Sure at this point flashcard isn't null so safe to use !!
                            view.showFlashcardSide(flashcard!!.front)
                            showingFront = true
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
