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

/**
 * View for adding a new or editing an existing Flashcard
 */
class AddEditFlashcardFragment : Fragment(), AddEditFlashcardContract.View {

    // Dagger dependency injection
    @Inject lateinit var getFlashcard: GetFlashcard
    @Inject lateinit var  saveFlashcard: SaveFlashcard
    @Inject lateinit var useCaseHandler: UseCaseHandler

    private lateinit var presenter: AddEditFlashcardContract.Presenter

    private var flashcardId: String? = null
    private var flashcard: Flashcard? = null

    // Becomes active onResume; inactive onPause
    private var active = false

    // Reference to toast for testing purposes
    // Toast shows response for save success or failure
    private var toast: Toast? = null

    companion object {
        // If no Flashcard id was given, use this to mean creation of a new Flashcard
        const val newFlashcardId = "-1"

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
        return inflater.inflate(R.layout.fragment_add_edit_flashcard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Create the presenter
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

    /**
     * Set the flashcard to be shown. Should not be called from the presenter.
     * @param flashcardId id of the Flashcard to be shown
     */
    override fun setFlashcard(flashcardId: String) {
        this.flashcardId = flashcardId
        presenter.loadFlashcard(flashcardId)
    }

    override fun setLoadingIndicator(active: Boolean) {
        // TODO: fill this in
    }

    /**
     * Get the flashcard id which was saved from the arguments bundle in onCreate
     * If there is no flashcardId, return the new Flashcard default value
     * @return the Flashcard id
     */
    override fun getIdFromArguments(): String {
        return flashcardId ?: newFlashcardId
    }

    /**
     * Show a certain Flashcard. Called by presenter
     * @param flashcard the Flashcard to be shown
     */
    override fun showFlashcard(flashcard: Flashcard) {
        this.flashcard = flashcard
        flashcardEditCategory.setText(flashcard.category)
        flashcardEditFront.setText(flashcard.front)
        flashcardEditBack.setText(flashcard.back)
    }

    /**
     * Show blank fields for a new Flashcard
     */
    override fun showNewFlashcard() {
        flashcardEditCategory.setText("")
        flashcardEditFront.setText("")
        flashcardEditBack.setText("")
    }

    /**
     * Tell the router (containing activity) to move to the Flashcard list
     * Provide the id of the Flashcard to be shown in the detail view
     * @param flashcardId id of the Flashcard to be shown in detail view
     */
    override fun showFlashcardList(flashcardId: String) {
        (activity as MainFragmentRouter).showFlashcardList(flashcardId)
    }

    /**
     * If Flashcard failed to load, show the failed to load text
     */
    override fun showFailedToLoadFlashcard() {
        failedToLoadFlashcard.visibility = View.VISIBLE
    }

    /**
     * Show a toast for a successful save attempt
     */
    override fun showSaveSuccessful() {
        toast = Toast.makeText(activity, R.string.save_successful_toast_text, Toast.LENGTH_LONG)
        toast?.show()
    }

    /**
     * Show a toast for a failed save attempt
     */
    override fun showSaveFailed() {
        toast = Toast.makeText(activity, R.string.save_failed_toast_text, Toast.LENGTH_LONG)
        toast?.show()
    }

    override fun isActive(): Boolean {
        return active
    }

    /**
     * Set the presenter which was created in onViewCreated
     * Set the presenter as the listeners on the buttons
     * @param presenter the presenter to be set
     */
    override fun setPresenter(presenter: AddEditFlashcardContract.Presenter) {
        this.presenter = presenter

        saveFlashcardButton.setOnClickListener {
            val category = flashcardEditCategory.text.toString()
            val front = flashcardEditFront.text.toString()
            val back = flashcardEditBack.text.toString()
            flashcard = if (flashcard != null) {
                // If changing an existing flashcard, create a new one with the same id
                Flashcard(flashcard!!.id, category, front, back)
            } else {
                // If no existing flashcard, create a new one with new id
                Flashcard(category = category, front = front, back = back)
            }
            // flashcard will definitely not be null
            this@AddEditFlashcardFragment.presenter.saveFlashcard(flashcard!!)
        }

        showFlashcardListButton.setOnClickListener {
            this@AddEditFlashcardFragment.presenter.showList()
        }
    }

    @VisibleForTesting
    fun getToast(): Toast? {
        return toast
    }
}
