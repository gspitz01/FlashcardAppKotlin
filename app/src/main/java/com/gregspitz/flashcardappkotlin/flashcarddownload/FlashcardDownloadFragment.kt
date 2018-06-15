package com.gregspitz.flashcardappkotlin.flashcarddownload


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.Toast
import com.gregspitz.flashcardappkotlin.FlashcardApplication
import com.gregspitz.flashcardappkotlin.R
import com.gregspitz.flashcardappkotlin.UseCaseHandler
import com.gregspitz.flashcardappkotlin.data.service.model.DownloadCategory
import com.gregspitz.flashcardappkotlin.flashcarddownload.domain.usecase.DownloadFlashcards
import com.gregspitz.flashcardappkotlin.flashcarddownload.domain.usecase.GetDownloadCategories
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.SelectableAdapter
import kotlinx.android.synthetic.main.fragment_flashcard_download.*
import javax.inject.Inject

/**
 * View for FlashcardDownload
 */
class FlashcardDownloadFragment : Fragment(), FlashcardDownloadContract.View,
        FlexibleAdapter.OnItemClickListener, ActionMode.Callback {

    // Dagger dependency injection
    @Inject
    lateinit var getDownloadCategory: GetDownloadCategories
    @Inject
    lateinit var downloadFlashcards: DownloadFlashcards
    @Inject
    lateinit var useCaseHandler: UseCaseHandler

    private lateinit var presenter: FlashcardDownloadContract.Presenter

    //    private lateinit var recyclerAdapter: DownloadCategoriesRecyclerAdapter
    private lateinit var flexRecyclerAdapter: FlexibleAdapter<DownloadCategoryFlexItem>

    private var actionMode: ActionMode? = null

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

        // Restore instance state
        if (savedInstanceState != null) {
            flexRecyclerAdapter.onRestoreInstanceState(savedInstanceState)
            if (flexRecyclerAdapter.selectedItemCount > 0) {
                actionMode = activity?.startActionMode(this)
                setActionModeContentTitle(flexRecyclerAdapter.selectedItemCount)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_flashcard_download, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        recyclerAdapter = DownloadCategoriesRecyclerAdapter(listOf())
        categoriesRecyclerView.layoutManager = LinearLayoutManager(activity)
        categoriesRecyclerView.adapter = flexRecyclerAdapter
//        categoriesRecyclerView.adapter = recyclerAdapter


        FlashcardDownloadPresenter(useCaseHandler, this,
                getDownloadCategory, downloadFlashcards)
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
        return if (position != RecyclerView.NO_POSITION) {
            if (actionMode == null) {
                actionMode = activity?.startActionMode(this)
            }
            toggleSelection(position)
            true
        } else {
            false
        }
    }

    /**
     * ActionMode.Callback methods
     */

    override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.downloadFlashcardsButton -> {
                presenter.downloadFlashcards(flexRecyclerAdapter.currentItems.withIndex()
                        .filter { it.index in flexRecyclerAdapter.selectedPositions }
                        .map { it.value.downloadCategory })
                true
            }
            else -> false
        }
    }

    override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        mode?.menuInflater?.inflate(R.menu.flashcard_download_fragment_menu, menu)
        flexRecyclerAdapter.mode = SelectableAdapter.Mode.MULTI
        return true
    }

    override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        return false
    }

    override fun onDestroyActionMode(mode: ActionMode?) {
        flexRecyclerAdapter.mode = SelectableAdapter.Mode.IDLE
        actionMode = null
    }

    private fun toggleSelection(position: Int) {
        flexRecyclerAdapter.toggleSelection(position)
        val count = flexRecyclerAdapter.selectedItemCount
        if (count == 0) {
            actionMode?.finish()
        } else {
            setActionModeContentTitle(count)
        }
    }

    private fun setActionModeContentTitle(count: Int) {
        actionMode?.title = "${count.toString()} ${(
                if (count == 1) activity?.getString(R.string.action_one_selected)
                else activity?.getString(R.string.action_many_selected))}"
    }

    override fun setLoadingIndicator(active: Boolean) {
        // TODO: implement this
    }

    override fun showDownloadCategories(downloadCategories: List<DownloadCategory>) {
//        recyclerAdapter.setDownloadCategories(downloadCategories)
        val flexCategories = downloadCategories.map {
            DownloadCategoryFlexItem(it)
        }
        flexRecyclerAdapter.updateDataSet(flexCategories)
    }

    override fun showFailedToGetDownloadCategories() {
        // TODO: add special style for warnings and errors
        downloadCategoriesMessage.visibility = View.VISIBLE
        downloadCategoriesMessage.setText(R.string.failed_to_load_download_categories_text)
    }

    override fun showFlashcardDownloadSuccessful() {
        Toast.makeText(activity, R.string.download_flashcards_successful, Toast.LENGTH_LONG).show()
    }

    override fun showFlashcardDownloadFailure() {
        Toast.makeText(activity, R.string.download_flashcards_failed, Toast.LENGTH_LONG).show()
    }

    override fun isActive(): Boolean = active

    override fun setPresenter(presenter: FlashcardDownloadContract.Presenter) {
        this.presenter = presenter
    }
}
