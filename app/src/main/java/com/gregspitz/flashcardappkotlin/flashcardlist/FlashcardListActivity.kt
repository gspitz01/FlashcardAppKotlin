package com.gregspitz.flashcardappkotlin.flashcardlist

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.gregspitz.flashcardappkotlin.FlashcardApplication
import com.gregspitz.flashcardappkotlin.R
import com.gregspitz.flashcardappkotlin.UseCaseHandler
import com.gregspitz.flashcardappkotlin.addeditflashcard.AddEditFlashcardActivity
import com.gregspitz.flashcardappkotlin.data.model.Flashcard
import com.gregspitz.flashcardappkotlin.flashcardlist.domain.usecase.GetFlashcards
import kotlinx.android.synthetic.main.activity_flashcard_list.*
import javax.inject.Inject

class FlashcardListActivity : AppCompatActivity(), FlashcardListContract.View {

    private lateinit var presenter : FlashcardListContract.Presenter

    @Inject
    lateinit var getFlashcards: GetFlashcards

    @Inject
    lateinit var useCaseHandler: UseCaseHandler

    private lateinit var recyclerAdapter : FlashcardRecyclerAdapter

    private lateinit var viewModel: FlashcardListViewModel

    private lateinit var pagerAdapter: FlashcardDetailPagerAdapter

    private var active = false

    init {
        FlashcardApplication.useCaseComponent.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flashcard_list)

        flashcardRecyclerView.layoutManager = LinearLayoutManager(this)
        recyclerAdapter = FlashcardRecyclerAdapter(emptyList())
        flashcardRecyclerView.adapter = recyclerAdapter
        pagerAdapter = FlashcardDetailPagerAdapter(supportFragmentManager, listOf())
        detailContent.adapter = pagerAdapter

        viewModel = ViewModelProviders.of(this).get(FlashcardListViewModel::class.java)

        val flashcardsObserver = Observer<List<Flashcard>> {
            if (it != null) {
                flashcardListMessages.visibility = View.GONE
                recyclerAdapter.updateFlashcards(it)
                initPagerAdapter(it)
            }
        }

        viewModel.flashcards.observe(this, flashcardsObserver)

        FlashcardListPresenter(useCaseHandler, this, viewModel, getFlashcards)
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
    }

    override fun onPause() {
        super.onPause()
        active = false
    }

    override fun setPresenter(presenter: FlashcardListContract.Presenter) {
        this.presenter = presenter
        recyclerAdapter.setPresenter(this.presenter)
        addFlashcardFab.setOnClickListener { this@FlashcardListActivity.presenter.addFlashcard() }
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
        val intent = Intent(this, AddEditFlashcardActivity::class.java)
        intent.putExtra(AddEditFlashcardActivity.flashcardIdExtra,
                AddEditFlashcardActivity.newFlashcardExtra)
        startActivity(intent)
    }

    override fun showEditFlashcard(flashcardId: String) {
        val intent = Intent(this, AddEditFlashcardActivity::class.java)
        intent.putExtra(AddEditFlashcardActivity.flashcardIdExtra, flashcardId)
        startActivity(intent)
    }

    override fun showFlashcardDetailsUi(flashcardPosition: Int) {
        detailContent.currentItem = flashcardPosition
    }

    override fun isActive(): Boolean = active
}
