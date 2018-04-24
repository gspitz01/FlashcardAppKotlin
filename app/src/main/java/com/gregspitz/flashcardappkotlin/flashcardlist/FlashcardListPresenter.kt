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

    override fun onFlashcardClick(flashcardId: String) {
        view.showFlashcardDetailsUi(flashcardId)
    }

}
