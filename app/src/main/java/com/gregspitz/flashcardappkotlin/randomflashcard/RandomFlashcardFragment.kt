package com.gregspitz.flashcardappkotlin.randomflashcard

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gregspitz.flashcardappkotlin.FlashcardApplication
import com.gregspitz.flashcardappkotlin.R
import com.gregspitz.flashcardappkotlin.UseCaseHandler
import com.gregspitz.flashcardappkotlin.data.model.Flashcard
import com.gregspitz.flashcardappkotlin.data.model.FlashcardSide
import com.gregspitz.flashcardappkotlin.randomflashcard.domain.usecase.GetRandomFlashcard
import kotlinx.android.synthetic.main.fragment_random_flashcard.*
import javax.inject.Inject

/**
 * Fragment for showing a random Flashcard
 */
class RandomFlashcardFragment : Fragment(), RandomFlashcardContract.View {

    // Dagger Dependency Injection
    @Inject
    lateinit var getRandomFlashcard: GetRandomFlashcard
    @Inject
    lateinit var useCaseHandler: UseCaseHandler

    private lateinit var presenter: RandomFlashcardContract.Presenter
    private lateinit var viewModel: RandomFlashcardViewModel

    // Active onResume; inactive onPause
    private var active = false

    companion object {
        @JvmStatic
        fun newInstance() = RandomFlashcardFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FlashcardApplication.useCaseComponent.inject(this)
        viewModel = ViewModelProviders.of(this).get(RandomFlashcardViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_random_flashcard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Observe the Flashcard on the ViewModel
        val randomFlashcardObserver = Observer<Flashcard> {
            flashcardCategory.text = it?.category
            when (viewModel.flashcardSide.value) {
                FlashcardSide.FRONT -> {
                    flashcardSide.textAlignment = View.TEXT_ALIGNMENT_CENTER
                    flashcardSide.text = it?.front
                }
                FlashcardSide.BACK -> {
                    flashcardSide.textAlignment = View.TEXT_ALIGNMENT_VIEW_START
                    flashcardSide.text = it?.back
                }
            }
        }

        // Observe the FlashcardSide on the ViewModel
        val flashcardSideObserver = Observer<FlashcardSide> {
            when (it) {
                FlashcardSide.FRONT -> {
                    flashcardSide.textAlignment = View.TEXT_ALIGNMENT_CENTER
                    flashcardSide.text = viewModel.randomFlashcard.value?.front
                }
                FlashcardSide.BACK -> {
                    flashcardSide.textAlignment = View.TEXT_ALIGNMENT_VIEW_START
                    flashcardSide.text = viewModel.randomFlashcard.value?.back
                }
            }
        }

        viewModel.randomFlashcard.observe(this, randomFlashcardObserver)
        viewModel.flashcardSide.observe(this, flashcardSideObserver)

        // Create the presenter
        RandomFlashcardPresenter(useCaseHandler, this, viewModel, getRandomFlashcard)
    }

    override fun onResume() {
        super.onResume()
        if (viewModel.randomFlashcard.value == null) {
            presenter.start()
        }
        active = true
    }

    override fun onPause() {
        super.onPause()
        active = false
    }

    override fun setLoadingIndicator(active: Boolean) {
        // TODO: fill this in
    }

    /**
     * Show message if the Flashcard could not be loaded
     */
    override fun showFailedToLoadFlashcard() {
        flashcardSide.setText(R.string.failed_to_load_flashcard_text)
    }

    override fun isActive(): Boolean {
        return active
    }

    /**
     * Set the presenter and the click listeners
     * @param presenter the presenter (as created in onViewCreated)
     */
    override fun setPresenter(presenter: RandomFlashcardContract.Presenter) {
        this.presenter = presenter
        flashcardCardView.setOnClickListener {
            this@RandomFlashcardFragment.presenter.turnFlashcard()
        }

        nextFlashcardButton.setOnClickListener {
            this@RandomFlashcardFragment.presenter.loadNewFlashcard()
        }
    }
}
