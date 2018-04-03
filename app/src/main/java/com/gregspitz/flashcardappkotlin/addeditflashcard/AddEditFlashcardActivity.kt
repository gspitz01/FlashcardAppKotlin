package com.gregspitz.flashcardappkotlin.addeditflashcard

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.gregspitz.flashcardappkotlin.Injection
import com.gregspitz.flashcardappkotlin.R
import com.gregspitz.flashcardappkotlin.data.model.Flashcard
import kotlinx.android.synthetic.main.activity_add_edit_flashcard.*

class AddEditFlashcardActivity : AppCompatActivity(), AddEditFlashcardContract.View {

    private lateinit var presenter: AddEditFlashcardContract.Presenter
    private var flashcard: Flashcard? = null

    private var active = false

    companion object {
        const val flashcardIdExtra = "flashcard_id_extra"
        const val newFlashcardExtra = "-1"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_flashcard)

        AddEditFlashcardPresenter(Injection.provideUseCaseHandler(), this,
                Injection.provideGetFlashcard(applicationContext),
                Injection.provideSaveFlashcard(applicationContext))
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
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showFailedToLoadFlashcard() {
        failedToLoadFlashcard.visibility = View.VISIBLE
    }

    override fun showNewFlashcard() {
        flashcardEditFront.setText("")
        flashcardEditBack.setText("")
    }

    override fun showSaveSuccessful() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showSaveFailed() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isActive(): Boolean {
        return active
    }
}
