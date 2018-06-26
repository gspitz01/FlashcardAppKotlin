package com.gregspitz.flashcardappkotlin.categorylist

import com.gregspitz.flashcardappkotlin.TestData.CATEGORY_LIST
import com.gregspitz.flashcardappkotlin.UseCase
import com.gregspitz.flashcardappkotlin.UseCaseHandler
import com.gregspitz.flashcardappkotlin.categorylist.domain.usecase.GetCategories
import com.nhaarman.mockito_kotlin.*
import org.junit.Before
import org.junit.Test

class CategoryListPresenterTest {

    private val getCategories: GetCategories = mock()

    private val useCaseHandler: UseCaseHandler = mock()

    private val categoryListView: CategoryListContract.View = mock()

    // InOrder for verifying setLoadingIndicator on view
    private val inOrder = inOrder(categoryListView)

    private val categoryListViewModel: CategoryListContract.ViewModel = mock()

    private val useCaseCallbackCaptor =
            argumentCaptor<UseCase.UseCaseCallback<GetCategories.ResponseValue>>()

    private lateinit var categoryListPresenter: CategoryListPresenter

    @Before
    fun setup() {
        whenever(categoryListView.isActive()).thenReturn(true)
    }

    @Test
    fun `on creation, sets self on view`() {
        createPresenter()
        verify(categoryListView).setPresenter(categoryListPresenter)
    }

    @Test
    fun `on load categories, success from use case, calls set categories on view model`() {
        createPresenter()
        categoryListPresenter.loadCategories()
        verifyLoadCategoriesSuccess()
    }

    @Test
    fun `on load categories, failure from use case, calls show failed to load categories on view`() {
        createPresenter()
        categoryListPresenter.loadCategories()
        verifySetLoadingIndicator(true)
        verify(useCaseHandler).execute(eq(getCategories), any(), useCaseCallbackCaptor.capture())
        useCaseCallbackCaptor.firstValue.onError()
        verifySetLoadingIndicator(false)
        verify(categoryListView).showFailedToLoadCategories()
    }

    @Test
    fun `on load categories, empty list from use case, calls no categories to load on view`() {
        createPresenter()
        categoryListPresenter.loadCategories()
        verifySetLoadingIndicator(true)
        verify(useCaseHandler).execute(eq(getCategories), any(), useCaseCallbackCaptor.capture())
        val response = GetCategories.ResponseValue(listOf())
        useCaseCallbackCaptor.firstValue.onSuccess(response)
        verifySetLoadingIndicator(false)
        verify(categoryListView).showNoCategoriesToLoad()
    }

    @Test
    fun `on category click, calls show flashcard list on view`() {
        createPresenter()
        val recyclerPosition = 0
        categoryListPresenter.onCategoryClick(recyclerPosition)
        verify(categoryListView).showFlashcardList(recyclerPosition)
    }

    @Test
    fun `on start, loads categories`() {
        createPresenter()
        categoryListPresenter.start()
        verifyLoadCategoriesSuccess()
    }

    private fun createPresenter() {
        categoryListPresenter = CategoryListPresenter(useCaseHandler, categoryListView,
                categoryListViewModel, getCategories)
    }

    private fun verifySetLoadingIndicator(active: Boolean) {
        inOrder.verify(categoryListView).setLoadingIndicator(active)
    }

    private fun verifyLoadCategoriesSuccess() {
        verifySetLoadingIndicator(true)
        verify(useCaseHandler).execute(eq(getCategories), any(), useCaseCallbackCaptor.capture())
        val response = GetCategories.ResponseValue(CATEGORY_LIST)
        useCaseCallbackCaptor.firstValue.onSuccess(response)
        verifySetLoadingIndicator(false)
        verify(categoryListViewModel).setCategories(CATEGORY_LIST)
    }
}
