package com.gregspitz.flashcardappkotlin.randomflashcard

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.gregspitz.flashcardappkotlin.FlashcardApplication
import com.gregspitz.flashcardappkotlin.R
import com.gregspitz.flashcardappkotlin.UseCaseHandler
import com.gregspitz.flashcardappkotlin.addeditflashcard.domain.usecase.SaveFlashcard
import com.gregspitz.flashcardappkotlin.categorylist.domain.usecase.GetCategories
import com.gregspitz.flashcardappkotlin.data.model.Category
import com.gregspitz.flashcardappkotlin.data.model.Flashcard
import com.gregspitz.flashcardappkotlin.data.model.FlashcardPriority
import com.gregspitz.flashcardappkotlin.data.model.FlashcardSide
import com.gregspitz.flashcardappkotlin.randomflashcard.domain.usecase.GetRandomFlashcard
import kotlinx.android.synthetic.main.fragment_random_flashcard.*
import javax.inject.Inject

private const val CATEGORY_NAME = "category_name"

/**
 * Fragment for showing a random Flashcard
 */
class RandomFlashcardFragment : Fragment(), RandomFlashcardContract.View {

    // Dagger Dependency Injection
    @Inject
    lateinit var getRandomFlashcard: GetRandomFlashcard
    @Inject
    lateinit var getCategories: GetCategories
    @Inject
    lateinit var saveFlashcard: SaveFlashcard
    @Inject
    lateinit var useCaseHandler: UseCaseHandler

    private lateinit var presenter: RandomFlashcardContract.Presenter
    private lateinit var viewModel: RandomFlashcardViewModel

    // Active onResume; inactive onPause
    private var active = false
    // Category to make sure only Flashcards of that category are chosen
    // Null categoryName means all categories
    private var categoryName: String? = null
    // Adapter for spinner
    private var spinnerAdapter: ArrayAdapter<String>? = null

    companion object {
        @JvmStatic
        fun newInstance(categoryName: String? = null) =
                RandomFlashcardFragment().apply {
                    arguments = Bundle().apply {
                        if (categoryName != null) {
                            putString(CATEGORY_NAME, categoryName)
                        }
                    }
                }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            categoryName = it.getString(CATEGORY_NAME)
        }
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

        val spinnerCategoriesObserver = Observer<List<Category>> {
            if (it != null) {
                val categoryNames = it.map { it.name }.toMutableList()
                activity?.let {
                    categoryNames.add(0, it.getString(R.string.all_flashcards_spinner_text))
                }
                spinnerAdapter =
                        ArrayAdapter(activity, android.R.layout.simple_spinner_item,
                                categoryNames)
                spinnerAdapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                categorySpinner.adapter = spinnerAdapter
                if (categoryName == null) {
                    categorySpinner.setSelection(0)
                } else {
                    categorySpinner.setSelection(spinnerAdapter!!.getPosition(categoryName))
                }
            }
        }

        viewModel.randomFlashcard.observe(this, randomFlashcardObserver)
        viewModel.flashcardSide.observe(this, flashcardSideObserver)
        viewModel.spinnerCategories.observe(this, spinnerCategoriesObserver)

        // Create the presenter
        RandomFlashcardPresenter(useCaseHandler, this, viewModel, getRandomFlashcard,
                getCategories, saveFlashcard)
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

    override fun showFailedToLoadCategories() {
        categorySpinner.visibility = View.GONE
    }

    override fun getCategoryName(): String? = categoryName

    override fun isActive(): Boolean = active

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

        categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?,
                                        position: Int, id: Long) {
                if (spinnerAdapter != null) {
                    // If the first item is selected, category is "All" which is represented by null
                    categoryName = if (position == 0) {
                        null
                    } else {
                        spinnerAdapter?.getItem(position)
                    }
                    this@RandomFlashcardFragment.presenter.loadNewFlashcard()
                }
            }
        }
    }
}
