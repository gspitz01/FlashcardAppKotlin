package com.gregspitz.flashcardappkotlin.flashcarddownload


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import com.gregspitz.flashcardappkotlin.FlashcardApplication
import com.gregspitz.flashcardappkotlin.MainFragmentRouter
import com.gregspitz.flashcardappkotlin.R
import com.gregspitz.flashcardappkotlin.UseCaseHandler
import com.gregspitz.flashcardappkotlin.flashcarddownload.domain.usecase.DownloadFlashcards
import com.gregspitz.flashcardappkotlin.flashcarddownload.domain.usecase.GetDownloadCategories
import com.gregspitz.flashcardappkotlin.flashcarddownload.domain.usecase.SaveFlashcards
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.SelectableAdapter
import kotlinx.android.synthetic.main.fragment_flashcard_download.*
import javax.inject.Inject

/**
 * View for FlashcardDownload
 */
class FlashcardDownloadFragment : Fragment(), FlashcardDownloadContract.View,
        FlexibleAdapter.OnItemClickListener {

    // Dagger dependency injection
    @Inject
    lateinit var getDownloadCategory: GetDownloadCategories
    @Inject
    lateinit var downloadFlashcards: DownloadFlashcards
    @Inject
    lateinit var saveFlashcards: SaveFlashcards
    @Inject
    lateinit var useCaseHandler: UseCaseHandler

    private lateinit var presenter: FlashcardDownloadContract.Presenter

    private lateinit var flexRecyclerAdapter: FlexibleAdapter<DownloadCategoryFlexItem>
    private var activatedPosition: Int? = null
    private var isDownloadEnabled = false

    private var active = false

    companion object {
        fun newInstance() = FlashcardDownloadFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FlashcardApplication.useCaseComponent.inject(this)
        // Instantiate recycler adapter
        flexRecyclerAdapter = FlexibleAdapter(listOf())
        flexRecyclerAdapter.addListener(this)
        flexRecyclerAdapter.mode = SelectableAdapter.Mode.SINGLE

        if (savedInstanceState != null) {
            flexRecyclerAdapter.onRestoreInstanceState(savedInstanceState)
            if (flexRecyclerAdapter.selectedItemCount > 0) {
                isDownloadEnabled = true
                activatedPosition = flexRecyclerAdapter.selectedPositions.first()
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_flashcard_download, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        categoriesRecyclerView.layoutManager = LinearLayoutManager(activity)
        categoriesRecyclerView.adapter = flexRecyclerAdapter

        FlashcardDownloadPresenter(useCaseHandler, this,
                getDownloadCategory, downloadFlashcards, saveFlashcards)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.flashcard_download_fragment_menu, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)
        menu?.findItem(R.id.downloadFlashcardsButton)?.isEnabled = isDownloadEnabled
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.downloadFlashcardsButton -> {
                flexRecyclerAdapter.getItem(flexRecyclerAdapter.selectedPositions.first())
                        ?.let {
                            presenter.downloadFlashcards(it)
                        }
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
        flexRecyclerAdapter.onSaveInstanceState(outState)
        super.onSaveInstanceState(outState)
    }

    /**
     * On click of an item in the RecyclerView
     * @param view the clicked view
     * @param position which position it is in the dataset
     * @return true for itemView activation
     */
    override fun onItemClick(view: View?, position: Int): Boolean {
        isDownloadEnabled = true
        activity?.invalidateOptionsMenu()
        if (position != activatedPosition) {
            setActivatedPosition(position)
        }
        return true
    }

    private fun setActivatedPosition(position: Int) {
        activatedPosition = position
        flexRecyclerAdapter.toggleSelection(position)
    }

    override fun setLoadingIndicator(active: Boolean) {
        // TODO: implement this
    }

    override fun showDownloadCategories(downloadCategories: List<DownloadCategoryFlexItem>) {
        flexRecyclerAdapter.updateDataSet(downloadCategories)
    }

    override fun showFailedToGetDownloadCategories() {
        activity?.let {
            downloadCategoriesMessage
                    .setTextColor(ContextCompat.getColor(it, R.color.colorError))
        }
        downloadCategoriesMessage.visibility = View.VISIBLE
        downloadCategoriesMessage.setText(R.string.failed_to_load_download_categories_text)
    }

    override fun showFlashcardDownloadSuccessful() {
        (activity as MainFragmentRouter).showSnackbar(R.string.download_flashcards_successful)
    }

    override fun showFlashcardDownloadFailure() {
        (activity as MainFragmentRouter).showSnackbar(R.string.download_flashcards_failed)
    }

    override fun isActive(): Boolean = active

    override fun setPresenter(presenter: FlashcardDownloadContract.Presenter) {
        this.presenter = presenter
    }
}
