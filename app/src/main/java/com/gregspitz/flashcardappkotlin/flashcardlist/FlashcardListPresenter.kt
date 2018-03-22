package com.gregspitz.flashcardappkotlin.flashcardlist

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
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun selectFlashcard(flashcard: Flashcard) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun addFlashcard() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun loadFlashcards() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onFlashcardClick(flashcardId: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
