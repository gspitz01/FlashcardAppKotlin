package com.gregspitz.flashcardappkotlin.flashcardlist


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gregspitz.flashcardappkotlin.FlashcardApplication
import com.gregspitz.flashcardappkotlin.MainFragmentRouter
import com.gregspitz.flashcardappkotlin.R
import com.gregspitz.flashcardappkotlin.UseCaseHandler
import com.gregspitz.flashcardappkotlin.addeditflashcard.AddEditFlashcardFragment
import com.gregspitz.flashcardappkotlin.data.model.Flashcard
import com.gregspitz.flashcardappkotlin.data.model.FlashcardListItem
import com.gregspitz.flashcardappkotlin.flashcardlist.domain.usecase.GetFlashcards
import kotlinx.android.synthetic.main.fragment_flashcard_list.*
import javax.inject.Inject

private const val FLASHCARD_ID = "flashcard_id"

/**
 * Fragment for listing all Flashcards with a RecyclerView
 */
class FlashcardListFragment : Fragment(), FlashcardListContract.View {

    // Dagger Dependency Injection
    @Inject lateinit var getFlashcards: GetFlashcards
    @Inject lateinit var useCaseHandler: UseCaseHandler

    private lateinit var presenter : FlashcardListContract.Presenter
    private lateinit var viewModel: FlashcardListViewModel

    private lateinit var recyclerAdapter : FlashcardRecyclerAdapter
    private lateinit var pagerAdapter: FlashcardDetailPagerAdapter

    // A map to figure out the recycler position from the pager position for auto-scrolling
    private val pagerPositionToRecyclerPositionMap: MutableMap<Int, Int> = mutableMapOf()
    // And vice versa
    private val recyclerPositionToPagerPositionMap: MutableMap<Int, Int> = mutableMapOf()

    // Active onResume; inactive onPause
    private var active = false
    private var flashcardId: String? = null

    companion object {
        // Represents that no particular Flashcard was asked to be shown in the detail view
        const val noParticularFlashcardExtra = "-1"

        fun newInstance(flashcardId: String) = FlashcardListFragment().apply {
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
        viewModel = ViewModelProviders.of(this).get(FlashcardListViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_flashcard_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup the RecyclerView
        flashcardRecyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerAdapter = FlashcardRecyclerAdapter(emptyList())
        flashcardRecyclerView.adapter = recyclerAdapter

        // Setup the viewPager
        pagerAdapter = FlashcardDetailPagerAdapter(childFragmentManager, listOf())
        detailContent.adapter = pagerAdapter
        detailContent.addOnPageChangeListener(object: ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageScrolled(position: Int, positionOffset: Float,
                                        positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {
                // Have RecyclerView scroll when the ViewPager is swiped
                pagerPositionToRecyclerPositionMap[position]?.let {
                    flashcardRecyclerView.scrollToPosition(it)
                }
            }
        })

        // Observe on the ViewModel
        val flashcardsObserver = Observer<List<FlashcardListItem>> {
            if (it != null) {
                flashcardListMessages.visibility = View.GONE
                recyclerAdapter.updateFlashcards(it)
                initPagerAdapter(it)
                moveToDetailsFromArgument(it)
            }
        }

        viewModel.flashcards.observe(this, flashcardsObserver)

        // Create the presenter
        FlashcardListPresenter(useCaseHandler, this, viewModel, getFlashcards)
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
            for ((index, flashcard) in flashcards.withIndex()) {
                when (flashcard) {
                    is Flashcard -> {
                        if (flashcard.id == flashcardId) {
                            detailContent?.currentItem = index
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
        flashcardListMessages.setText(R.string.failed_to_load_flashcard_text)
    }

    /**
     * Show message if there are no Flashcards to show
     */
    override fun showNoFlashcardsToLoad() {
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
            detailContent.currentItem = it
        }
    }

    /**
     * Tell router (containing Activity) to move to the edit Flashcard view
     * @param flashcardId id of the Flashcard to be edited
     */
    override fun showEditFlashcard(flashcardId: String) {
        (activity as MainFragmentRouter).showAddEditFlashcard(flashcardId)
    }

    override fun isActive(): Boolean {
        return active
    }

    /**
     * Set presenter and button click listeners
     */
    override fun setPresenter(presenter: FlashcardListContract.Presenter) {
        this.presenter = presenter
        recyclerAdapter.setPresenter(this.presenter)
        addFlashcardFab.setOnClickListener { this@FlashcardListFragment.presenter.addFlashcard() }
    }
}
