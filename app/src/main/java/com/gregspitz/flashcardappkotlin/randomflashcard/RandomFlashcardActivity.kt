package com.gregspitz.flashcardappkotlin.randomflashcard

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.gregspitz.flashcardappkotlin.FlashcardApplication
import com.gregspitz.flashcardappkotlin.R
import com.gregspitz.flashcardappkotlin.UseCaseHandler
import com.gregspitz.flashcardappkotlin.data.model.Flashcard
import com.gregspitz.flashcardappkotlin.data.model.FlashcardSide
import com.gregspitz.flashcardappkotlin.randomflashcard.domain.usecase.GetRandomFlashcard
import kotlinx.android.synthetic.main.activity_random_flashcard.*
import javax.inject.Inject

class RandomFlashcardActivity : AppCompatActivity(), RandomFlashcardContract.View {

    private lateinit var presenter: RandomFlashcardContract.Presenter

    @Inject
    lateinit var getRandomFlashcard: GetRandomFlashcard

    @Inject
    lateinit var useCaseHandler: UseCaseHandler

    private lateinit var viewModel: RandomFlashcardViewModel

    private var active = false

    init {
        FlashcardApplication.useCaseComponent.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_random_flashcard)

        viewModel = ViewModelProviders.of(this).get(RandomFlashcardViewModel::class.java)

        val randomFlashcardObserver = Observer<Flashcard> {
            when(viewModel.flashcardSide.value) {
                FlashcardSide.FRONT -> flashcardSide.text = it?.front
                FlashcardSide.BACK -> flashcardSide.text = it?.back
            }
        }

        val flashcardSideObserver = Observer<FlashcardSide> {
            when(it) {
                FlashcardSide.FRONT -> flashcardSide.text = viewModel.randomFlashcard.value?.front
                FlashcardSide.BACK -> flashcardSide.text = viewModel.randomFlashcard.value?.back
            }
        }

        viewModel.randomFlashcard.observe(this, randomFlashcardObserver)
        viewModel.flashcardSide.observe(this, flashcardSideObserver)

        RandomFlashcardPresenter(useCaseHandler, this, viewModel, getRandomFlashcard)
    }

    override fun onResume() {
        super.onResume()
        if (viewModel.randomFlashcard.value == null) {
            presenter.start()
        }
        active = true
    }

    override fun onPause() {
        super.onPause()
        active = false
    }

    override fun setPresenter(presenter: RandomFlashcardContract.Presenter) {
        this.presenter = presenter
        flashcardSide.setOnClickListener {
            this@RandomFlashcardActivity.presenter.turnFlashcard() }

        nextFlashcardButton.setOnClickListener { this@RandomFlashcardActivity.presenter.loadNewFlashcard() }
    }

    override fun setLoadingIndicator(active: Boolean) {
        // TODO: implement
    }

    override fun showFailedToLoadFlashcard() {
        flashcardSide.setText(R.string.failed_to_load_flashcard_text)
    }

    override fun isActive(): Boolean = active
}
