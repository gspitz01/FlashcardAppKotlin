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
import com.gregspitz.flashcardappkotlin.flashcardlist.FlashcardListFragment
import kotlinx.android.synthetic.main.fragment_add_edit_flashcard.*
import javax.inject.Inject


private const val FLASHCARD_ID = "flashcard_id"
private const val CATEGORY_NAME = "category_name"
private const val FRONT_TEXT = "front_text"
private const val BACK_TEXT = "back_text"
private const val DELETE_DIALOG_SHOWING = "delete_dialog_showing"
private const val DISCARD_CHANGES_DIALOG_SHOWING = "discard_changes_dialog_showing"

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

    // Possible argument
    private var flashcardId: String? = null
    // Possible argument or saved state
    private var categoryName: String? = null
    // Possible saved state
    private var frontText: String? = null
    // Possible saved state
    private var backText: String? = null
    private var flashcard: Flashcard? = null

    // Becomes active onResume; inactive onPause
    private var active = false

    // Is discard changes Dialog showing
    var discardChangesDialogShowing = false
    // Is delete Dialog showing
    var deleteDialogShowing = false

    companion object {
        // If no Flashcard id was given, use this to mean creation of a new Flashcard
        const val newFlashcardId = "-1"

        fun newInstance(flashcardId: String, categoryName: String? = null) =
                AddEditFlashcardFragment().apply {
                    arguments = Bundle().apply {
                        putString(FLASHCARD_ID, flashcardId)
                        if (categoryName != null) {
                            putString(CATEGORY_NAME, categoryName)
                        }
                    }
                }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            flashcardId = it.getString(FLASHCARD_ID)
            categoryName = it.getString(CATEGORY_NAME)
        }
        savedInstanceState?.let {
            // Prioritize saved state categoryName
            categoryName = it.getString(CATEGORY_NAME, categoryName)
            frontText = it.getString(FRONT_TEXT)
            backText = it.getString(BACK_TEXT)
            discardChangesDialogShowing = it.getBoolean(DISCARD_CHANGES_DIALOG_SHOWING, false)
            deleteDialogShowing = it.getBoolean(DELETE_DIALOG_SHOWING, false)
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

        // Fill in category name if it's there from either argument or saved state
        categoryName?.let {
            flashcardEditCategory.setText(it)
        }

        // Fill in front text if it's there from saved state
        frontText?.let {
            flashcardEditFront.setText(it)
        }
        // Fill in back text if it's there from saved state
        backText?.let {
            flashcardEditBack.setText(it)
        }
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
                displayDeleteDialog()
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        // Save text in text fields
        outState.putString(CATEGORY_NAME, flashcardEditCategory.text.toString())
        outState.putString(FRONT_TEXT, flashcardEditFront.text.toString())
        outState.putString(BACK_TEXT, flashcardEditBack.text.toString())
        // Save state for discard changes Dialog
        outState.putBoolean(DISCARD_CHANGES_DIALOG_SHOWING, discardChangesDialogShowing)
        // Save state for delete Dialog
        outState.putBoolean(DELETE_DIALOG_SHOWING, deleteDialogShowing)
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

        updateUi(flashcard.category, flashcard.front, flashcard.back)
    }

    /**
     * Show blank fields for a new Flashcard
     */
    override fun showNewFlashcard() {
        updateUi("", "", "")
    }

    private fun updateUi(cardCategoryText: String, cardFrontText: String, cardBackText: String) {
        // If any of categoryName, frontText or backText are not null then either there was a
        // saved state or categoryName was introduced as an argument, in that case do not change
        // the edit texts, as there were correctly set in onViewCreated
        // Setting this.flashcard is still correct, though, because that saves the flashcard id
        if (categoryName == null) {
            flashcardEditCategory.setText(cardCategoryText)
        }
        if (frontText == null) {
            flashcardEditFront.setText(cardFrontText)
        }
        if (backText == null) {
            flashcardEditBack.setText(cardBackText)
        }

        // If either dialog showing variable is true, then this is a restarted fragment
        // and we need to show those dialogs again
        if (deleteDialogShowing) {
            displayDeleteDialog()
        } else if (discardChangesDialogShowing) {
            val flashcardId = this.flashcardId ?: FlashcardListFragment.noParticularFlashcardExtra
            displayDiscardChangesDialogForShowList(flashcardId)
        }
    }

    /**
     * Tell the router (containing activity) to move to the Flashcard list
     * Provide the id of the Flashcard to be shown in the detail view
     */
    override fun showFlashcardList() {
        val flashcardId = this.flashcardId ?: FlashcardListFragment.noParticularFlashcardExtra
        if (unsavedChangesExist()) {
            displayDiscardChangesDialogForShowList(flashcardId)
        } else {
            (activity as MainFragmentRouter).showFlashcardList(flashcardId)
        }
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

    /**
     * Show an AlertDialog to check if the user wants to discard changes
     * @param positiveListener The function to run if the user confirms discarding changes
     * @param negativeListener The function to run if the user does not want to discard changes
     */
    fun displayDiscardChangesDialog(positiveListener: (() -> Unit)? = null,
                                    negativeListener: (() -> Unit)? = null) {
        AlertDialog.Builder(activity)
                .setTitle(R.string.discard_changes_title_text)
                .setMessage(R.string.discard_changes_message_text)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes) { _, _ ->
                    discardChangesDialogShowing = false
                    positiveListener?.invoke()
                }
                .setNegativeButton(android.R.string.no) { _, _ ->
                    discardChangesDialogShowing = false
                    negativeListener?.invoke()
                }
                .show()
        discardChangesDialogShowing = true
    }

    fun unsavedChangesExist(): Boolean {
        if (flashcardId.isNullOrEmpty()) {
            return noPreviousFlashcardChangesExist()
        } else {
            if (flashcard != null) {
                return flashcardEditCategory.text.toString() != flashcard!!.category ||
                        flashcardEditFront.text.toString() != flashcard!!.front ||
                        flashcardEditBack.text.toString() != flashcard!!.back
            } else {
                return noPreviousFlashcardChangesExist()
            }
        }
    }

    private fun noPreviousFlashcardChangesExist(): Boolean {
        return flashcardEditCategory.text.isNotEmpty() ||
                flashcardEditFront.text.isNotEmpty() ||
                flashcardEditBack.text.isNotEmpty()
    }

    private fun displayDiscardChangesDialogForShowList(flashcardId: String) {
        val positiveListener =
                { -> (activity as MainFragmentRouter).showFlashcardList(flashcardId) }
        displayDiscardChangesDialog(positiveListener)
    }

    private fun displayDeleteDialog() {
        AlertDialog.Builder(activity)
                .setTitle(R.string.confirm_delete_title_text)
                .setMessage(R.string.confirm_delete_message_text)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes) { _, _ ->
                    flashcard?.let {
                        this@AddEditFlashcardFragment.presenter.deleteFlashcard(it.id)
                    }
                    deleteDialogShowing = false
                }
                .setNegativeButton(android.R.string.no) { _, _ ->
                    deleteDialogShowing = false
                }
                .show()
        deleteDialogShowing = true
    }
}
