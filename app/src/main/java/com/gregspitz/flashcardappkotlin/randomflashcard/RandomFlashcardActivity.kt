package com.gregspitz.flashcardappkotlin.randomflashcard

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.gregspitz.flashcardappkotlin.Injection
import com.gregspitz.flashcardappkotlin.R
import kotlinx.android.synthetic.main.activity_random_flashcard.*

class RandomFlashcardActivity : AppCompatActivity(), RandomFlashcardContract.View {

    private lateinit var presenter: RandomFlashcardContract.Presenter

    private var active = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_random_flashcard)

        RandomFlashcardPresenter(Injection.provideUseCaseHandler(),
                this, Injection.provideGetRandomFlashcard())
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
