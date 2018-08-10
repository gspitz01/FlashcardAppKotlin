package com.gregspitz.flashcardappkotlin.addeditflashcard


import android.app.AlertDialog
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.*
import com.gregspitz.flashcardappkotlin.FlashcardApplication
import com.gregspitz.flashcardappkotlin.MainFragmentRouter
import com.gregspitz.flashcardappkotlin.R
import com.gregspitz.flashcardappkotlin.UseCaseHandler
import com.gregspitz.flashcardappkotlin.addeditflashcard.domain.usecase.DeleteFlashcard
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
    @Inject
    lateinit var getFlashcard: GetFlashcard
    @Inject
    lateinit var saveFlashcard: SaveFlashcard
    @Inject
    lateinit var deleteFlashcard: DeleteFlashcard
    @Inject
    lateinit var useCaseHandler: UseCaseHandler

    private lateinit var presenter: AddEditFlashcardContract.Presenter

    private var flashcardId: String? = null
    private var flashcard: Flashcard? = null

    // Becomes active onResume; inactive onPause
    private var active = false

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
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_add_edit_flashcard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Create the presenter
        AddEditFlashcardPresenter(useCaseHandler, this, getFlashcard, saveFlashcard, deleteFlashcard)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.add_edit_flashcard_fragment_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.saveFlashcardButton -> {
                val category = flashcardEditCategory.text.toString()
                val front = flashcardEditFront.text.toString()
                val back = flashcardEditBack.text.toString()
                // Input validation
                if (category.isEmpty()) {
                    (activity as MainFragmentRouter).showSnackbar(
                            R.string.flashcard_must_have_category_message_text)
                    return true
                } else if (front.isEmpty()) {
                    (activity as MainFragmentRouter)
                            .showSnackbar(R.string.flashcard_must_have_front_message_text)
                    return true
                } else if (back.isEmpty()) {
                    (activity as MainFragmentRouter)
                            .showSnackbar(R.string.flashcard_must_have_back_message_text)
                    return true
                }
                flashcard = if (flashcard != null) {
                    // If changing an existing flashcard, create a new one with the same id
                    Flashcard(flashcard!!.id, category, front, back)
                } else {
                    // If no existing flashcard, create a new one with new id
                    Flashcard(category = category, front = front, back = back)
                }
                // flashcard will definitely not be null
                this@AddEditFlashcardFragment.presenter.saveFlashcard(flashcard!!)
                return true
            }
            R.id.deleteFlashcardButton -> {
                AlertDialog.Builder(activity)
                        .setTitle(R.string.confirm_delete_title_text)
                        .setMessage(R.string.confirm_delete_message_text)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes) { _, _ ->
                            flashcard?.let {
                                this@AddEditFlashcardFragment.presenter.deleteFlashcard(it.id)
                            }
                        }
                        .setNegativeButton(android.R.string.no, null)
                        .show()
                return true
            }
            R.id.showFlashcardListButton -> {
                this@AddEditFlashcardFragment.presenter.showList()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
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
        (activity as MainFragmentRouter).showSnackbar(R.string.failed_to_load_flashcard_text)
    }

    /**
     * Show a message for a successful save attempt
     * And then move to FlashcardList view
     */
    override fun showSaveSuccessful(flashcardId: String, categoryName: String) {
        (activity as MainFragmentRouter).showSnackbar(R.string.save_successful_message_text)
        (activity as MainFragmentRouter).showCategoryFlashcardList(categoryName, flashcardId)
    }

    /**
     * Show a message for a failed save attempt
     */
    override fun showSaveFailed() {
        (activity as MainFragmentRouter).showSnackbar(R.string.save_failed_message_text)
    }

    /**
     * Show a message to say the deletion attempt failed
     */
    override fun showDeleteFailed() {
        (activity as MainFragmentRouter).showSnackbar(R.string.delete_failed_message_text)
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
    }
}
