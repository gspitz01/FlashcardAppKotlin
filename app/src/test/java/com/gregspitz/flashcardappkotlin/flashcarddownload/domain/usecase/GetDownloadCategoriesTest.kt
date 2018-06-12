package com.gregspitz.flashcardappkotlin.flashcarddownload.domain.usecase

import com.gregspitz.flashcardappkotlin.TestData.CATEGORY_1
import com.gregspitz.flashcardappkotlin.TestData.CATEGORY_2
import com.gregspitz.flashcardappkotlin.TestUseCaseScheduler
import com.gregspitz.flashcardappkotlin.UseCase
import com.gregspitz.flashcardappkotlin.UseCaseHandler
import com.gregspitz.flashcardappkotlin.data.service.FlashcardDownloadService
import com.nhaarman.mockito_kotlin.argumentCaptor
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetDownloadCategoriesTest {

    private val values = GetDownloadCategories.RequestValues()

    private val useCaseHandler = UseCaseHandler(TestUseCaseScheduler())

    private val flashcardDownloadService: FlashcardDownloadService = mock()

    private val downloadCallbackCaptor =
            argumentCaptor<FlashcardDownloadService.GetDownloadCategoriesCallback>()

    private val callback: UseCase.UseCaseCallback<GetDownloadCategories.ResponseValue> = mock()

    private val responseCaptor =
            argumentCaptor<GetDownloadCategories.ResponseValue>()

    private lateinit var getDownloadCategories: GetDownloadCategories

    @Before
    fun setup() {
        getDownloadCategories = GetDownloadCategories(flashcardDownloadService)
        useCaseHandler.execute(getDownloadCategories, values, callback)
    }

    @Test
    fun onExecute_successFromService_callsSuccessOnCallback() {
        verify(flashcardDownloadService).getDownloadCategories(downloadCallbackCaptor.capture())
        val numberOfFlashcardsMap = mapOf(CATEGORY_1 to 4, CATEGORY_2 to 3)
        downloadCallbackCaptor.firstValue.onCategoriesLoaded(numberOfFlashcardsMap)
        verify(callback).onSuccess(responseCaptor.capture())
        assertEquals(numberOfFlashcardsMap, responseCaptor.firstValue.categoryToNumberOfFlashcardsMap)
    }

    @Test
    fun onExecute_failureFromService_callsErrorOnCallback() {
        verify(flashcardDownloadService).getDownloadCategories(downloadCallbackCaptor.capture())
        downloadCallbackCaptor.firstValue.onDataNotAvailable()
        verify(callback).onError()
    }
}
