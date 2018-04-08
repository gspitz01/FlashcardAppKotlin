package com.gregspitz.flashcardappkotlin.randomflashcard

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.gregspitz.flashcardappkotlin.FlashcardApplication
import com.gregspitz.flashcardappkotlin.R
import com.gregspitz.flashcardappkotlin.UseCaseHandler
import com.gregspitz.flashcardappkotlin.randomflashcard.domain.usecase.GetRandomFlashcard
import kotlinx.android.synthetic.main.activity_random_flashcard.*
import javax.inject.Inject

class RandomFlashcardActivity : AppCompatActivity(), RandomFlashcardContract.View {

    private lateinit var presenter: RandomFlashcardContract.Presenter

    @Inject
    lateinit var getRandomFlashcard: GetRandomFlashcard

    @Inject
    lateinit var useCaseHandler: UseCaseHandler

    private var active = false

    init {
        FlashcardApplication.useCaseComponent.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_random_flashcard)

        RandomFlashcardPresenter(useCaseHandler, this, getRandomFlashcard)
    }

    override fun onResume() {
        super.onResume()
        presenter.start()
        active = true
    }

    override fun onPause() {
        super.onPause()
        active = false
    }

    override fun setPresenter(presenter: RandomFlashcardContract.Presenter) {
        this.presenter = presenter
        flashcardSide.setOnClickListener(object: View.OnClickListener {
            override fun onClick(v: View?) {
                this@RandomFlashcardActivity.presenter.turnFlashcard()
            }
        })

        nextFlashcardButton.setOnClickListener(object: View.OnClickListener {
            override fun onClick(v: View?) {
                this@RandomFlashcardActivity.presenter.loadNewFlashcard()
            }
        })
    }

    override fun setLoadingIndicator(active: Boolean) {
        // TODO: implement
    }

    override fun showFlashcardSide(side: String) {
        flashcardSide.text = side
    }

    override fun showFailedToLoadFlashcard() {
        flashcardSide.setText(R.string.failed_to_load_flashcard_text)
    }

    override fun isActive(): Boolean = active
}
