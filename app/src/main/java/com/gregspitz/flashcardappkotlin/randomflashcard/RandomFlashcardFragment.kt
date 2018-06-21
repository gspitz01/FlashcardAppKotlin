package com.gregspitz.flashcardappkotlin.randomflashcard

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.*
import com.gregspitz.flashcardappkotlin.FlashcardApplication
import com.gregspitz.flashcardappkotlin.R
import com.gregspitz.flashcardappkotlin.UseCaseHandler
import com.gregspitz.flashcardappkotlin.addeditflashcard.domain.usecase.SaveFlashcard
import com.gregspitz.flashcardappkotlin.data.model.Flashcard
import com.gregspitz.flashcardappkotlin.data.model.FlashcardPriority
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
    lateinit var saveFlashcard: SaveFlashcard
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
        setHasOptionsMenu(true)
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
        RandomFlashcardPresenter(useCaseHandler, this, viewModel, getRandomFlashcard,
                saveFlashcard)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.random_flashcard_fragment_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.nextFlashcardButton -> {
                presenter.loadNewFlashcard()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()
        active = true
        if (viewModel.randomFlashcard.value == null) {
            presenter.start()
        }
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
        flashcardSide.textAlignment = View.TEXT_ALIGNMENT_CENTER
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

        flashcardSide.setOnClickListener {
            this@RandomFlashcardFragment.presenter.turnFlashcard()
        }

        flashcardPriorityLowButton.setOnClickListener {
            this@RandomFlashcardFragment.presenter.saveFlashcard(FlashcardPriority.LOW)
        }

        flashcardPriorityMediumButton.setOnClickListener {
            this@RandomFlashcardFragment.presenter.saveFlashcard(FlashcardPriority.MEDIUM)
        }

        flashcardPriorityHighButton.setOnClickListener {
            this@RandomFlashcardFragment.presenter.saveFlashcard(FlashcardPriority.HIGH)
        }

        flashcardPriorityUrgentButton.setOnClickListener {
            this@RandomFlashcardFragment.presenter.saveFlashcard(FlashcardPriority.URGENT)
        }
    }
}
