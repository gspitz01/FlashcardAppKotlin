package com.gregspitz.flashcardappkotlin.addeditflashcard

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.gregspitz.flashcardappkotlin.FlashcardApplication
import com.gregspitz.flashcardappkotlin.R
import com.gregspitz.flashcardappkotlin.UseCaseHandler
import com.gregspitz.flashcardappkotlin.addeditflashcard.domain.usecase.SaveFlashcard
import com.gregspitz.flashcardappkotlin.data.model.Flashcard
import com.gregspitz.flashcardappkotlin.flashcarddetail.domain.usecase.GetFlashcard
import com.gregspitz.flashcardappkotlin.flashcardlist.FlashcardListActivity
import kotlinx.android.synthetic.main.activity_add_edit_flashcard.*
import javax.inject.Inject

class AddEditFlashcardActivity : AppCompatActivity(), AddEditFlashcardContract.View {

    private lateinit var presenter: AddEditFlashcardContract.Presenter

    @Inject
    lateinit var getFlashcard: GetFlashcard

    @Inject
    lateinit var  saveFlashcard: SaveFlashcard

    @Inject
    lateinit var useCaseHandler: UseCaseHandler

    private var flashcard: Flashcard? = null

    private var active = false

    init {
        FlashcardApplication.useCaseComponent.inject(this)
    }

    companion object {
        const val flashcardIdExtra = "flashcard_id_extra"
        const val newFlashcardExtra = "-1"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_flashcard)

        AddEditFlashcardPresenter(useCaseHandler, this, getFlashcard, saveFlashcard)
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

    override fun setPresenter(presenter: AddEditFlashcardContract.Presenter) {
        this.presenter = presenter
        saveFlashcardButton.setOnClickListener(object: View.OnClickListener {
            override fun onClick(v: View?) {
                val front = flashcardEditFront.text.toString()
                val back = flashcardEditBack.text.toString()
                if (flashcard != null) {
                    flashcard = Flashcard(flashcard!!.id, front, back)
                } else {
                    flashcard = Flashcard(front = front, back = back)
                }
                this@AddEditFlashcardActivity.presenter.saveFlashcard(flashcard!!)
            }
        })

        showFlashcardListButton.setOnClickListener(object: View.OnClickListener {
            override fun onClick(v: View?) {
                this@AddEditFlashcardActivity.presenter.showList()
            }
        })
    }

    override fun setLoadingIndicator(active: Boolean) {
        // TODO: fill this in
    }

    override fun getIdFromIntent(): String {
        return intent.getStringExtra(flashcardIdExtra) ?: newFlashcardExtra
    }

    override fun showFlashcard(flashcard: Flashcard) {
        this.flashcard = flashcard
        flashcardEditFront.setText(flashcard.front)
        flashcardEditBack.setText(flashcard.back)
    }

    override fun showFlashcardList() {
        startActivity(Intent(this, FlashcardListActivity::class.java))
    }

    override fun showFailedToLoadFlashcard() {
        failedToLoadFlashcard.visibility = View.VISIBLE
    }

    override fun showNewFlashcard() {
        flashcardEditFront.setText("")
        flashcardEditBack.setText("")
    }

    override fun showSaveSuccessful() {
        Toast.makeText(this, R.string.save_successful_toast_text, Toast.LENGTH_LONG).show()
    }

    override fun showSaveFailed() {
        Toast.makeText(this, R.string.save_failed_toast_text, Toast.LENGTH_LONG).show()
    }

    override fun isActive(): Boolean {
        return active
    }
}
