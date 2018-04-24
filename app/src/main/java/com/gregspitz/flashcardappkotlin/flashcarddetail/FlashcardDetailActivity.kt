package com.gregspitz.flashcardappkotlin.flashcarddetail

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.gregspitz.flashcardappkotlin.FlashcardApplication
import com.gregspitz.flashcardappkotlin.R
import com.gregspitz.flashcardappkotlin.UseCaseHandler
import com.gregspitz.flashcardappkotlin.addeditflashcard.AddEditFlashcardActivity
import com.gregspitz.flashcardappkotlin.data.model.Flashcard
import com.gregspitz.flashcardappkotlin.flashcarddetail.domain.usecase.GetFlashcard
import kotlinx.android.synthetic.main.activity_flashcard_detail.*
import javax.inject.Inject

class FlashcardDetailActivity : AppCompatActivity(), FlashcardDetailContract.View {

    private var active = false

    private lateinit var presenter: FlashcardDetailContract.Presenter

    @Inject
    lateinit var getFlashcard: GetFlashcard

    @Inject
    lateinit var useCaseHandler: UseCaseHandler

    private lateinit var viewModel: FlashcardDetailViewModel

    init {
        FlashcardApplication.useCaseComponent.inject(this)
    }

    companion object {
        const val flashcardIntentId = "flashcard_intent_id"
        const val noFlashcardIntentId = "-1"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flashcard_detail)

        viewModel = ViewModelProviders.of(this).get(FlashcardDetailViewModel::class.java)

        val flashcardObserver = Observer<Flashcard> {
            flashcard_front.text = it?.front
            flashcard_back.text = it?.back
        }

        viewModel.flashcard.observe(this, flashcardObserver)

        FlashcardDetailPresenter(useCaseHandler, this, viewModel, getFlashcard)
    }

    override fun onResume() {
        super.onResume()
        active = true
        if (viewModel.flashcard.value == null) {
            presenter.start()
        }
    }

    override fun onPause() {
        super.onPause()
        active = false
    }

    override fun setPresenter(presenter: FlashcardDetailContract.Presenter) {
        this.presenter = presenter
        editFlashcardButton.setOnClickListener {
            this@FlashcardDetailActivity.presenter.editFlashcard()
        }
    }

    override fun setLoadingIndicator(active: Boolean) {
        // TODO: implement
    }

    override fun showEditFlashcard(flashcardId: String) {
        val intent = Intent(this, AddEditFlashcardActivity::class.java)
        intent.putExtra(AddEditFlashcardActivity.flashcardIdExtra, flashcardId)
        startActivity(intent)
    }

    override fun showFailedToLoadFlashcard() {
        flashcard_front.text = getString(R.string.failed_to_load_flashcard_text)
    }

    override fun getIdFromIntent(): String {
        return intent.getStringExtra(flashcardIntentId) ?: noFlashcardIntentId
    }

    override fun isActive(): Boolean {
        return active
    }
}
