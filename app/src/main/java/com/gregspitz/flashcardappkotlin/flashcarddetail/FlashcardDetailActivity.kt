package com.gregspitz.flashcardappkotlin.flashcarddetail

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.gregspitz.flashcardappkotlin.Injection
import com.gregspitz.flashcardappkotlin.R
import com.gregspitz.flashcardappkotlin.data.model.Flashcard
import kotlinx.android.synthetic.main.activity_flashcard_detail.*

class FlashcardDetailActivity : AppCompatActivity(), FlashcardDetailContract.View {

    private var active = false

    private lateinit var presenter: FlashcardDetailContract.Presenter

    companion object {
        const val flashcardIntentId = "flashcard_intent_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flashcard_detail)

        FlashcardDetailPresenter(Injection.provideUseCaseHandler(), this,
                Injection.provideGetFlashcard(applicationContext))
    }

    override fun onResume() {
        super.onResume()
        active = true
        presenter.start()
    }

    override fun onPause() {
        super.onPause()
        active = false
    }

    override fun setPresenter(presenter: FlashcardDetailContract.Presenter) {
        this.presenter = presenter
    }

    override fun setLoadingIndicator(active: Boolean) {
        // TODO: implement
    }

    override fun showFlashcard(flashcard: Flashcard) {
        flashcard_front.text = flashcard.front
        flashcard_back.text = flashcard.back
    }

    override fun showEditFlashcard(flashcardId: String) {
        // TODO: implement
    }

    override fun showFailedToLoadFlashcard() {
        flashcard_front.text = getString(R.string.failed_to_load_flashcard_text)
    }

    override fun getIdFromIntent(): String {
        return intent.getStringExtra(flashcardIntentId)
    }

    override fun isActive(): Boolean {
        return active
    }
}
