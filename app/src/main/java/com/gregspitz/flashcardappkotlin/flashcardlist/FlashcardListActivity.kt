package com.gregspitz.flashcardappkotlin.flashcardlist

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.gregspitz.flashcardappkotlin.FlashcardApplication
import com.gregspitz.flashcardappkotlin.R
import com.gregspitz.flashcardappkotlin.UseCaseHandler
import com.gregspitz.flashcardappkotlin.addeditflashcard.AddEditFlashcardActivity
import com.gregspitz.flashcardappkotlin.data.model.Flashcard
import com.gregspitz.flashcardappkotlin.flashcarddetail.FlashcardDetailActivity
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

        FlashcardListPresenter(useCaseHandler, this, getFlashcards)
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
        addFlashcardFab.setOnClickListener(object: View.OnClickListener {
            override fun onClick(v: View?) {
                this@FlashcardListActivity.presenter.addFlashcard()
            }
        })
    }

    override fun setLoadingIndicator(active: Boolean) {
        // TODO: implement this
    }

    override fun showFlashcards(flashcards: List<Flashcard>) {
        flashcardListMessages.visibility = View.GONE
        recyclerAdapter.updateFlashcards(flashcards)
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

    override fun showFlashcardDetailsUi(flashcardId: String) {
        val intent = Intent(this, FlashcardDetailActivity::class.java)
        intent.putExtra(FlashcardDetailActivity.flashcardIntentId, flashcardId)
        startActivity(intent)
    }

    override fun isActive(): Boolean = active
}
