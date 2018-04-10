package com.gregspitz.flashcardappkotlin.flashcarddetail

import com.gregspitz.flashcardappkotlin.UseCase
import com.gregspitz.flashcardappkotlin.UseCaseHandler
import com.gregspitz.flashcardappkotlin.data.model.Flashcard
import com.gregspitz.flashcardappkotlin.flashcarddetail.domain.usecase.GetFlashcard

/**
 * Presenter for FlashcardDetail view
 */
class FlashcardDetailPresenter(
        private val useCaseHandler: UseCaseHandler,
        private val view: FlashcardDetailContract.View,
        private val viewModel: FlashcardDetailContract.ViewModel,
        private val getFlashcard: GetFlashcard
) : FlashcardDetailContract.Presenter {

    private var flashcard: Flashcard? = null

    init {
        view.setPresenter(this)
    }

    override fun start() {
        loadFlashcard(view.getIdFromIntent())
    }

    override fun loadFlashcard(flashcardId: String) {
        view.setLoadingIndicator(true)

        useCaseHandler.execute(getFlashcard, GetFlashcard.RequestValues(flashcardId),
                object: UseCase.UseCaseCallback<GetFlashcard.ResponseValue> {
                    override fun onSuccess(response: GetFlashcard.ResponseValue) {
                        flashcard = response.flashcard
                        if (view.isActive()) {
                            view.setLoadingIndicator(false)
                            viewModel.setFlashcard(response.flashcard)
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

    override fun editFlashcard() {
        flashcard?.let { view.showEditFlashcard(it.id) }
    }

}
