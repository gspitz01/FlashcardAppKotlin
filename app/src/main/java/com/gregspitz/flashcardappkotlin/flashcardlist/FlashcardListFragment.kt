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
import com.gregspitz.flashcardappkotlin.R.id.detailContent
import com.gregspitz.flashcardappkotlin.UseCaseHandler
import com.gregspitz.flashcardappkotlin.addeditflashcard.AddEditFlashcardFragment
import com.gregspitz.flashcardappkotlin.data.model.Flashcard
import com.gregspitz.flashcardappkotlin.flashcardlist.domain.usecase.GetFlashcards
import kotlinx.android.synthetic.main.fragment_flashcard_list.*
import javax.inject.Inject

private const val FLASHCARD_ID = "flashcard_id"

class FlashcardListFragment : Fragment(), FlashcardListContract.View {

    private lateinit var presenter : FlashcardListContract.Presenter

    @Inject
    lateinit var getFlashcards: GetFlashcards

    @Inject
    lateinit var useCaseHandler: UseCaseHandler

    private lateinit var recyclerAdapter : FlashcardRecyclerAdapter

    private lateinit var viewModel: FlashcardListViewModel

    private lateinit var pagerAdapter: FlashcardDetailPagerAdapter

    private var active = false

    private var flashcardId: String? = null

    companion object {
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
        flashcardRecyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerAdapter = FlashcardRecyclerAdapter(emptyList())
        flashcardRecyclerView.adapter = recyclerAdapter
        pagerAdapter = FlashcardDetailPagerAdapter(childFragmentManager, listOf())
        detailContent.adapter = pagerAdapter
        detailContent.addOnPageChangeListener(object: ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageScrolled(position: Int, positionOffset: Float,
                                        positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {
                flashcardRecyclerView.scrollToPosition(position)
            }

        })

        val flashcardsObserver = Observer<List<Flashcard>> {
            if (it != null) {
                flashcardListMessages.visibility = View.GONE
                recyclerAdapter.updateFlashcards(it)
                initPagerAdapter(it)
                moveToDetailsFromArgument(it)
            }
        }

        viewModel.flashcards.observe(this, flashcardsObserver)

        FlashcardListPresenter(useCaseHandler, this, viewModel, getFlashcards)
    }

    private fun moveToDetailsFromArgument(flashcards: List<Flashcard>) {
        if (flashcardId != null && flashcardId != noParticularFlashcardExtra) {
            for ((index, flashcard) in flashcards.withIndex()) {
                if (flashcard.id == flashcardId) {
                    detailContent?.currentItem = index
                }
            }
        }
    }

    private fun initPagerAdapter(flashcards: List<Flashcard>) {
        val fragments = flashcards.map {
            val bundle = Bundle()
            bundle.putParcelable(FlashcardDetailFragment.flashcardBundleId, it)
            val fragment = FlashcardDetailFragment()
            fragment.arguments = bundle
            fragment
        }
        pagerAdapter.setFragments(fragments)
    }

    override fun onResume() {
        super.onResume()
        active = true
        presenter.start()
        viewModel.flashcards.value?.let { moveToDetailsFromArgument(it) }
    }

    override fun onPause() {
        super.onPause()
        active = false
    }

    override fun setDetailView(flashcardId: String) {
        this.flashcardId = flashcardId
    }

    override fun setLoadingIndicator(active: Boolean) {
        // TODO: implement this
    }

    override fun showFailedToLoadFlashcards() {
        flashcardListMessages.setText(R.string.failed_to_load_flashcard_text)
    }

    override fun showNoFlashcardsToLoad() {
        flashcardListMessages.setText(R.string.no_flashcards_to_show_text)
    }

    override fun showAddFlashcard() {
        (activity as MainFragmentRouter)
                .showAddEditFlashcard(AddEditFlashcardFragment.newFlashcardExtra)
    }

    override fun showFlashcardDetailsUi(flashcardPosition: Int) {
        detailContent.currentItem = flashcardPosition
    }

    override fun showEditFlashcard(flashcardId: String) {
        (activity as MainFragmentRouter).showAddEditFlashcard(flashcardId)
    }

    override fun isActive(): Boolean {
        return active
    }

    override fun setPresenter(presenter: FlashcardListContract.Presenter) {
        this.presenter = presenter
        recyclerAdapter.setPresenter(this.presenter)
        addFlashcardFab.setOnClickListener { this@FlashcardListFragment.presenter.addFlashcard() }
    }
}
