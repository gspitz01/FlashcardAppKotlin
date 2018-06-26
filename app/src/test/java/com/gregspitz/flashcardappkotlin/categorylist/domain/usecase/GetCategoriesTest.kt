package com.gregspitz.flashcardappkotlin.categorylist.domain.usecase

import com.gregspitz.flashcardappkotlin.TestData.CATEGORY_LIST
import com.gregspitz.flashcardappkotlin.TestUseCaseScheduler
import com.gregspitz.flashcardappkotlin.UseCase
import com.gregspitz.flashcardappkotlin.UseCaseHandler
import com.gregspitz.flashcardappkotlin.data.source.FlashcardDataSource
import com.gregspitz.flashcardappkotlin.data.source.FlashcardRepository
import com.nhaarman.mockito_kotlin.argumentCaptor
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetCategoriesTest {

    private val values = GetCategories.RequestValues()

    private val repository: FlashcardRepository = mock()

    private val useCaseHandler = UseCaseHandler(TestUseCaseScheduler())

    private val repositoryCallbackCaptor =
            argumentCaptor<FlashcardDataSource.GetCategoriesCallback>()

    private val callback: UseCase.UseCaseCallback<GetCategories.ResponseValue> = mock()

    private val responseCaptor =
            argumentCaptor<GetCategories.ResponseValue>()

    private lateinit var getCategories: GetCategories

    @Before
    fun setup() {
        getCategories = GetCategories(repository)
        useCaseHandler.execute(getCategories, values, callback)
        verify(repository).getCategories(repositoryCallbackCaptor.capture())
    }

    @Test
    fun `success from repository, calls success on callback with category list`() {
        repositoryCallbackCaptor.firstValue.onCategoriesLoaded(CATEGORY_LIST)
        verify(callback).onSuccess(responseCaptor.capture())
        assertEquals(CATEGORY_LIST, responseCaptor.firstValue.categories)
    }

    @Test
    fun `failure from repository, calls error on callback`() {
        repositoryCallbackCaptor.firstValue.onDataNotAvailable()
        verify(callback).onError()
    }

}
