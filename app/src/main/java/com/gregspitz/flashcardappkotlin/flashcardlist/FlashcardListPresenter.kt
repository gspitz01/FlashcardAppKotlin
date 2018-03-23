package com.gregspitz.flashcardappkotlin.flashcardlist

import com.gregspitz.flashcardappkotlin.UseCase
import com.gregspitz.flashcardappkotlin.UseCaseHandler
import com.gregspitz.flashcardappkotlin.data.model.Flashcard
import com.gregspitz.flashcardappkotlin.flashcardlist.domain.usecase.GetFlashcards

/**
 * A presenter for FlashcardListView
 */
class FlashcardListPresenter(private val mUseCaseHandler: UseCaseHandler,
                             private val mView: FlashcardListContract.View,
                             private val mGetFlashcards: GetFlashcards)
    : FlashcardListContract.Presenter {

    init {
        mView.setPresenter(this)
    }

    override fun start() {
        loadFlashcards()
    }

    override fun selectFlashcard(flashcard: Flashcard) {
        mView.showFlashcardDetailsUi(flashcardId = flashcard.id)
    }

    override fun addFlashcard() {
        mView.showAddFlashcard()
    }

    override fun loadFlashcards() {
        mView.setLoadingIndicator(true)
        mUseCaseHandler.execute(mGetFlashcards, GetFlashcards.RequestValues(),
                object: UseCase.UseCaseCallback<GetFlashcards.ResponseValue> {
                    override fun onSuccess(response: GetFlashcards.ResponseValue) {
                        if (mView.isActive()) {
                            mView.setLoadingIndicator(false)
                            if (response.flashcards.isNotEmpty()) {
                                mView.showFlashcards(response.flashcards)
                            } else {
                                mView.showNoFlashcardsToLoad()
                            }
                        }
                    }

                    override fun onError() {
                        if (mView.isActive()) {
                            mView.setLoadingIndicator(false)
                            mView.showFailedToLoadFlashcards()
                        }
                    }

                })
    }

    override fun onFlashcardClick(flashcardId: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
