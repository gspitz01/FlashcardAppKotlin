package com.gregspitz.flashcardappkotlin.categorylist


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gregspitz.flashcardappkotlin.FlashcardApplication
import com.gregspitz.flashcardappkotlin.MainFragmentRouter
import com.gregspitz.flashcardappkotlin.R
import com.gregspitz.flashcardappkotlin.UseCaseHandler
import com.gregspitz.flashcardappkotlin.categorylist.domain.usecase.GetCategories
import com.gregspitz.flashcardappkotlin.data.model.Category
import kotlinx.android.synthetic.main.fragment_category_list.*
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 *
 */
class CategoryListFragment : Fragment(), CategoryListContract.View {

    // Dagger injection
    @Inject
    lateinit var getCategories: GetCategories
    @Inject
    lateinit var useCaseHandler: UseCaseHandler

    private lateinit var presenter: CategoryListContract.Presenter
    private lateinit var viewModel: CategoryListViewModel

    private lateinit var recyclerAdapter: CategoryRecyclerAdapter

    private var active = false

    companion object {
        fun newInstance() = CategoryListFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FlashcardApplication.useCaseComponent.inject(this)
        viewModel = ViewModelProviders.of(this).get(CategoryListViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_category_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up RecyclerView
        categoryRecyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerAdapter = CategoryRecyclerAdapter(emptyList())
        categoryRecyclerView.adapter = recyclerAdapter

        // Observe ViewModel
        val categoriesObserver = Observer<List<Category>> {
            if (it != null) {
                categoryListMessages.visibility = View.GONE
                recyclerAdapter.updateCategories(it)
            }
        }
        viewModel.categories.observe(this, categoriesObserver)

        CategoryListPresenter(useCaseHandler, this, viewModel, getCategories)
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

    override fun setLoadingIndicator(active: Boolean) {
        // TODO: implement
    }

    override fun showFailedToLoadCategories() {
        categoryListMessages.visibility = View.VISIBLE
        categoryListMessages.setText(R.string.failed_to_load_categories_text)
    }

    override fun showNoCategoriesToLoad() {
        categoryListMessages.visibility = View.VISIBLE
        categoryListMessages.setText(R.string.no_categories_to_show_text)
    }

    override fun showFlashcardList(recyclerPosition: Int) {
        viewModel.categories.value?.let {
            (activity as MainFragmentRouter).showCategoryFlashcardList(it[recyclerPosition].name)
        }
    }

    override fun isActive() = active

    override fun setPresenter(presenter: CategoryListContract.Presenter) {
        this.presenter = presenter
        recyclerAdapter.setPresenter(this.presenter)
    }
}
