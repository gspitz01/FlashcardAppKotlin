package com.gregspitz.flashcardappkotlin.addeditflashcard


import android.os.Bundle
import android.support.annotation.VisibleForTesting
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.gregspitz.flashcardappkotlin.FlashcardApplication
import com.gregspitz.flashcardappkotlin.MainFragmentRouter
import com.gregspitz.flashcardappkotlin.R
import com.gregspitz.flashcardappkotlin.UseCaseHandler
import com.gregspitz.flashcardappkotlin.addeditflashcard.domain.usecase.GetFlashcard
import com.gregspitz.flashcardappkotlin.addeditflashcard.domain.usecase.SaveFlashcard
import com.gregspitz.flashcardappkotlin.data.model.Flashcard
import kotlinx.android.synthetic.main.fragment_add_edit_flashcard.*
import javax.inject.Inject


private const val FLASHCARD_ID = "flashcard_id"

class AddEditFlashcardFragment : Fragment(), AddEditFlashcardContract.View {

    private lateinit var presenter: AddEditFlashcardContract.Presenter

    @Inject
    lateinit var getFlashcard: GetFlashcard

    @Inject
    lateinit var  saveFlashcard: SaveFlashcard

    @Inject
    lateinit var useCaseHandler: UseCaseHandler

    private var flashcardId: String? = null

    private var flashcard: Flashcard? = null

    private var active = false

    private var toast: Toast? = null

    companion object {
        const val newFlashcardExtra = "-1"

        fun newInstance(flashcardId: String) = AddEditFlashcardFragment().apply {
            arguments = Bundle().apply {
                putString(FLASHCARD_ID, flashcardId)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            flashcardId = it.getString(FLASHCARD_ID)
        }
        FlashcardApplication.useCaseComponent.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_edit_flashcard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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

    override fun setFlashcard(flashcardId: String) {
        this.flashcardId = flashcardId
        presenter.loadFlashcard(flashcardId)
    }

    override fun setLoadingIndicator(active: Boolean) {
        // TODO: fill this in
    }

    override fun getIdFromArguments(): String {
        return flashcardId ?: newFlashcardExtra
    }

    override fun showFlashcard(flashcard: Flashcard) {
        this.flashcard = flashcard
        flashcardEditFront.setText(flashcard.front)
        flashcardEditBack.setText(flashcard.back)
    }

    override fun showNewFlashcard() {
        flashcardEditFront.setText("")
        flashcardEditBack.setText("")
    }

    override fun showFlashcardList(flashcardId: String) {
        (activity as MainFragmentRouter).showFlashcardList(flashcardId)
    }

    override fun showFailedToLoadFlashcard() {
        failedToLoadFlashcard.visibility = View.VISIBLE
    }

    override fun showSaveSuccessful() {
        toast = Toast.makeText(activity, R.string.save_successful_toast_text, Toast.LENGTH_LONG)
        toast?.show()
    }

    override fun showSaveFailed() {
        toast = Toast.makeText(activity, R.string.save_failed_toast_text, Toast.LENGTH_LONG)
        toast?.show()
    }

    override fun isActive(): Boolean {
        return active
    }

    override fun setPresenter(presenter: AddEditFlashcardContract.Presenter) {
        this.presenter = presenter
        saveFlashcardButton.setOnClickListener {
            val front = flashcardEditFront.text.toString()
            val back = flashcardEditBack.text.toString()
            flashcard = if (flashcard != null) {
                Flashcard(flashcard!!.id, front, back)
            } else {
                Flashcard(front = front, back = back)
            }
            this@AddEditFlashcardFragment.presenter.saveFlashcard(flashcard!!)
        }

        showFlashcardListButton.setOnClickListener { this@AddEditFlashcardFragment.presenter.showList() }
    }

    @VisibleForTesting
    fun getToast(): Toast? {
        return toast
    }
}
