package com.gregspitz.flashcardappkotlin.addeditflashcard

import com.gregspitz.flashcardappkotlin.UseCase
import com.gregspitz.flashcardappkotlin.UseCaseHandler
import com.gregspitz.flashcardappkotlin.addeditflashcard.domain.usecase.SaveFlashcard
import com.gregspitz.flashcardappkotlin.data.model.Flashcard
import com.gregspitz.flashcardappkotlin.flashcarddetail.domain.usecase.GetFlashcard

/**
 * Presenter for AddEditFlashcard view
 */
class AddEditFlashcardPresenter (
        private val useCaseHandler: UseCaseHandler,
        private val view: AddEditFlashcardContract.View,
        private val getFlashcard: GetFlashcard,
        private val saveFlashcard: SaveFlashcard
) : AddEditFlashcardContract.Presenter {

    init {
        view.setPresenter(this)
    }

    override fun start() {
        val id = view.getIdFromIntent()
        if (id == AddEditFlashcardActivity.newFlashcardExtra) {
            view.showNewFlashcard()
        } else {
            loadFlashcard(view.getIdFromIntent())
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
            view.showFlashcardList()
        }
    }
}
