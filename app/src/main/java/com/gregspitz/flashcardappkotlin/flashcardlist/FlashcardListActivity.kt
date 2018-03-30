package com.gregspitz.flashcardappkotlin.flashcardlist

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.gregspitz.flashcardappkotlin.Injection
import com.gregspitz.flashcardappkotlin.R
import com.gregspitz.flashcardappkotlin.data.model.Flashcard
import kotlinx.android.synthetic.main.activity_flashcard_list.*

class FlashcardListActivity : AppCompatActivity(), FlashcardListContract.View {

    private lateinit var presenter : FlashcardListContract.Presenter

    private lateinit var recyclerAdapter : FlashcardRecyclerAdapter

    private var active = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flashcard_list)

        flashcard_recycler_view.layoutManager = LinearLayoutManager(this)
        recyclerAdapter = FlashcardRecyclerAdapter(emptyList())
        flashcard_recycler_view.adapter = recyclerAdapter

        FlashcardListPresenter(Injection.provideUseCaseHandler(), this,
                Injection.provideGetFlashcards(applicationContext))
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
    }

    override fun setLoadingIndicator(active: Boolean) {
        // TODO: implement this
    }

    override fun showFlashcards(flashcards: List<Flashcard>) {
        no_flashcards_to_show.visibility = View.GONE
        recyclerAdapter.updateFlashcards(flashcards)
    }

    override fun showFailedToLoadFlashcards() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showNoFlashcardsToLoad() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showAddFlashcard() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showFlashcardDetailsUi(flashcardId: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isActive(): Boolean = active
}