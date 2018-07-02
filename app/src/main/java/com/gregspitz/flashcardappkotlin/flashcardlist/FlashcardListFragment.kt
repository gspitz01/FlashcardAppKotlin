package com.gregspitz.flashcardappkotlin.flashcardlist


import android.app.AlertDialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import android.widget.Toast
import com.gregspitz.flashcardappkotlin.FlashcardApplication
import com.gregspitz.flashcardappkotlin.MainFragmentRouter
import com.gregspitz.flashcardappkotlin.R
import com.gregspitz.flashcardappkotlin.UseCaseHandler
import com.gregspitz.flashcardappkotlin.addeditflashcard.AddEditFlashcardFragment
import com.gregspitz.flashcardappkotlin.data.model.Category
import com.gregspitz.flashcardappkotlin.data.model.Flashcard
import com.gregspitz.flashcardappkotlin.data.model.FlashcardListItem
import com.gregspitz.flashcardappkotlin.flashcardlist.domain.usecase.DeleteFlashcards
import com.gregspitz.flashcardappkotlin.flashcardlist.domain.usecase.GetFlashcards
import kotlinx.android.synthetic.main.fragment_flashcard_list.*
import javax.inject.Inject

private const val FLASHCARD_ID = "flashcard_id"
private const val CATEGORY_NAME = "category_name"

/**
 * Fragment for listing all Flashcards with a RecyclerView
 */
class FlashcardListFragment : Fragment(), FlashcardListContract.View {

    // Dagger Dependency Injection
    @Inject
    lateinit var getFlashcards: GetFlashcards
    @Inject
    lateinit var deleteFlashcards: DeleteFlashcards
    @Inject
    lateinit var useCaseHandler: UseCaseHandler

    private lateinit var presenter: FlashcardListContract.Presenter
    private lateinit var viewModel: FlashcardListViewModel

    private lateinit var recyclerAdapter: FlashcardRecyclerAdapter
    private lateinit var pagerAdapter: FlashcardDetailPagerAdapter

    // A map to figure out the recycler position from the pager position for auto-scrolling
    private val pagerPositionToRecyclerPositionMap: MutableMap<Int, Int> = mutableMapOf()
    // And vice versa
    private val recyclerPositionToPagerPositionMap: MutableMap<Int, Int> = mutableMapOf()

    // Active onResume; inactive onPause
    private var active = false
    private var flashcardId: String? = null
    private var categoryName: String? = null

    companion object {
        // Represents that no particular Flashcard was asked to be shown in the detail view
        const val noParticularFlashcardExtra = "-1"

        fun newInstance(flashcardId: String, categoryName: String? = null) = FlashcardListFragment().apply {
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
        FlashcardApplication.useCaseComponent.inject(this)
        viewModel = ViewModelProviders.of(this).get(FlashcardListViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_flashcard_list, container, false)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.flashcard_list_fragment_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.deleteFlashcardsButton -> {
                if (categoryName == null) {
                    AlertDialog.Builder(activity)
                            .setTitle(R.string.confirm_delete_multiple_title_text)
                            .setMessage(R.string.confirm_delete_all_flashcards_text)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton(android.R.string.yes) { _, _ ->
                                presenter.deleteAllFlashcards()
                            }
                            .setNegativeButton(android.R.string.no, null)
                            .show()
                } else {
                    AlertDialog.Builder(activity)
                            .setTitle(R.string.confirm_delete_multiple_title_text)
                            .setMessage(activity?.getString(
                                    R.string.confirm_delete_flashcards_from_category_text,
                                    categoryName))
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton(android.R.string.yes) { _, _ ->
                                presenter.deleteFlashcardsFromCategory(categoryName!!)
                            }
                            .setNegativeButton(android.R.string.no, null)
                            .show()
                }
                return true
            }
            R.id.playButton -> {
                (activity as MainFragmentRouter).showRandomFlashcard(categoryName)
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Restore state
        savedInstanceState?.let {
            flashcardId = savedInstanceState.getString(FLASHCARD_ID)
        }

        // Setup the RecyclerView
        flashcardRecyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerAdapter = FlashcardRecyclerAdapter(emptyList())
        flashcardRecyclerView.adapter = recyclerAdapter

        // Setup the viewPager
        pagerAdapter = FlashcardDetailPagerAdapter(childFragmentManager, listOf())
        detailPager.adapter = pagerAdapter
        detailPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageScrolled(position: Int, positionOffset: Float,
                                        positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                // Have RecyclerView scroll when the ViewPager is swiped
                pagerPositionToRecyclerPositionMap[position]?.let {
                    flashcardRecyclerView.scrollToPosition(it)
                    // Save new flashcardId for restoring instance state
                    if (viewModel.flashcards.value != null) {
                        flashcardId = (viewModel.flashcards.value!![it] as Flashcard).id
                    }
                }

            }
        })

        // Observe on the ViewModel
        val flashcardsObserver = Observer<List<FlashcardListItem>> {
            if (it != null) {
                if (it.isNotEmpty()) {
                    flashcardListMessages.visibility = View.GONE
                }
                recyclerAdapter.updateFlashcards(it)
                initPagerAdapter(it)
                moveToDetailsFromArgument(it)
            }
        }

        viewModel.flashcards.observe(this, flashcardsObserver)

        // Create the presenter
        FlashcardListPresenter(useCaseHandler, this, viewModel, getFlashcards, deleteFlashcards)
    }


    /**
     * Initialize the PagerAdapter to have Fragments of details for each Flashcard
     * Also setup mapping between the indices of the ViewPager and
     * the RecyclerView for clicking and scroll-following
     * @param flashcards the list of Flashcards (from the ViewModel)
     */
    private fun initPagerAdapter(flashcards: List<FlashcardListItem>) {
        val fragments = mutableListOf<FlashcardDetailFragment>()
        var pagerPosition = 0
        for ((index, flashcard) in flashcards.withIndex()) {
            if (flashcard is Flashcard) {
                val bundle = Bundle()
                bundle.putParcelable(FlashcardDetailFragment.flashcardBundleId, flashcard)
                val fragment = FlashcardDetailFragment()
                fragment.arguments = bundle
                fragments.add(fragment)

                pagerPositionToRecyclerPositionMap[pagerPosition] = index
                recyclerPositionToPagerPositionMap[index] = pagerPosition
                pagerPosition++
            }
        }
        pagerAdapter.setFragments(fragments)
    }

    /**
     * Set the ViewPager to the correct Fragment as represented by the field flashcardId
     * @param flashcards the list of Flashcards (from the ViewModel)
     */
    private fun moveToDetailsFromArgument(flashcards: List<FlashcardListItem>) {
        if (flashcardId != null && flashcardId != noParticularFlashcardExtra) {
            for ((index, flashcard) in flashcards.filter { it is Flashcard }.withIndex()) {
                when (flashcard) {
                    is Flashcard -> {
                        if (flashcard.id == flashcardId) {
                            detailPager?.currentItem = index
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        active = true
        presenter.start()
        // if restarting, move to the correct fragment
        viewModel.flashcards.value?.let { moveToDetailsFromArgument(it) }
    }

    override fun onPause() {
        super.onPause()
        active = false
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (flashcardId != null && flashcardId != noParticularFlashcardExtra) {
            outState.putString(FLASHCARD_ID, flashcardId)
        }
    }

    /**
     * Set the Flashcard id to be shown in the detail ViewPager
     * @param flashcardId id of the Flashcard to be shown
     */
    override fun setDetailView(flashcardId: String) {
        this.flashcardId = flashcardId
    }

    override fun setLoadingIndicator(active: Boolean) {
        // TODO: implement this
    }

    /**
     * Show message if Flashcards failed to load
     */
    override fun showFailedToLoadFlashcards() {
        flashcardListMessages.visibility = View.VISIBLE
        flashcardListMessages.setText(R.string.failed_to_load_flashcard_text)
    }

    /**
     * Show message if there are no Flashcards to show
     */
    override fun showNoFlashcardsToLoad() {
        flashcardListMessages.visibility = View.VISIBLE
        flashcardListMessages.setText(R.string.no_flashcards_to_show_text)
    }

    /**
     * Tell router (containing Activity) to move to the add Flashcard view
     */
    override fun showAddFlashcard() {
        (activity as MainFragmentRouter)
                .showAddEditFlashcard(AddEditFlashcardFragment.newFlashcardId)
    }

    /**
     * Move the ViewPager to the Fragment associated with a particular Flashcard
     * @param recyclerPosition the position in the RecyclerView of the Flashcard to be shown
     */
    override fun showFlashcardDetailsUi(recyclerPosition: Int) {
        recyclerPositionToPagerPositionMap[recyclerPosition]?.let {
            detailPager.currentItem = it
        }
    }

    /**
     * Show the list of only Flashcards with a certain Category name
     * @param recyclerPosition the position in the recyclerAdapter of the category
     */
    override fun showCategoryFlashcardList(recyclerPosition: Int) {
        viewModel.flashcards.value?.let {
            if (it[recyclerPosition] is Category) {
                (activity as MainFragmentRouter)
                        .showCategoryFlashcardList((it[recyclerPosition] as Category).name)
            }
        }
    }

    /**
     * Show the list of Categories
     */
    override fun showCategoryList() {
        (activity as MainFragmentRouter).showCategoryList()
    }

    /**
     * Tell router (containing Activity) to move to the edit Flashcard view
     * @param flashcardId id of the Flashcard to be edited
     */
    override fun showEditFlashcard(flashcardId: String) {
        (activity as MainFragmentRouter).showAddEditFlashcard(flashcardId)
    }

    /**
     * Show that a deletion was successful
     */
    override fun showDeleteSuccess() {
        Toast.makeText(activity, R.string.delete_succeeded_toast_text, Toast.LENGTH_LONG).show()
    }

    /**
     * Show that a deletion failed
     */
    override fun showDeleteFailed() {
        Toast.makeText(activity, R.string.delete_failed_toast_text, Toast.LENGTH_LONG).show()
    }

    override fun isActive(): Boolean {
        return active
    }

    override fun getCategoryName(): String? {
        return categoryName
    }

    /**
     * Set presenter and button click listeners
     */
    override fun setPresenter(presenter: FlashcardListContract.Presenter) {
        this.presenter = presenter
        recyclerAdapter.setPresenter(this.presenter)
        addFlashcardFab.setOnClickListener {
            this@FlashcardListFragment.presenter.addFlashcard()
        }
    }
}
