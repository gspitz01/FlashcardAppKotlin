package com.gregspitz.flashcardappkotlin.flashcarddownload.domain.usecase

import com.gregspitz.flashcardappkotlin.TestData.CATEGORY_1
import com.gregspitz.flashcardappkotlin.TestData.CATEGORY_2
import com.gregspitz.flashcardappkotlin.TestData.FLASHCARD_LIST
import com.gregspitz.flashcardappkotlin.TestUseCaseScheduler
import com.gregspitz.flashcardappkotlin.UseCase
import com.gregspitz.flashcardappkotlin.UseCaseHandler
import com.gregspitz.flashcardappkotlin.data.service.FlashcardDownloadService
import com.gregspitz.flashcardappkotlin.data.service.model.DownloadCategory
import com.gregspitz.flashcardappkotlin.data.service.model.DownloadFlashcard
import com.gregspitz.flashcardappkotlin.data.source.FlashcardDataSource
import com.gregspitz.flashcardappkotlin.data.source.FlashcardRepository
import com.gregspitz.flashcardappkotlin.flashcarddownload.DownloadCategoryFlexItem
import com.nhaarman.mockito_kotlin.*
import org.junit.Before
import org.junit.Test

/**
 * Tests for {@link DownloadFlashcards}
 */
class DownloadFlashcardsTest {

    private val downloadCategoryList =
            listOf(DownloadCategory(CATEGORY_1.name, 4),
                    DownloadCategory(CATEGORY_2.name, 5))
    private val downloadCategoryFlexItems =
            downloadCategoryList.map { DownloadCategoryFlexItem(it) }
    private val values =
            DownloadFlashcards.RequestValues(downloadCategoryFlexItems)

    private val downloadFlashcardList = FLASHCARD_LIST.map {
        DownloadFlashcard(it.id, DownloadCategory(it.category), it.front, it.back)
    }

    private val useCaseHandler = UseCaseHandler(TestUseCaseScheduler())

    private val flashcardDownloadService: FlashcardDownloadService = mock()

    private val downloadCallbackCaptor =
            argumentCaptor<FlashcardDownloadService.DownloadFlashcardsCallback>()

    private val flashcardRepository: FlashcardRepository = mock()

    private val saveFlashcardCallbackCaptor =
            argumentCaptor<FlashcardDataSource.SaveFlashcardsCallback>()

    private val callback: UseCase.UseCaseCallback<DownloadFlashcards.ResponseValue> = mock()

    private lateinit var downloadFlashcards: DownloadFlashcards

    @Before
    fun setup() {
        downloadFlashcards = DownloadFlashcards(flashcardDownloadService, flashcardRepository)
        useCaseHandler.execute(downloadFlashcards, values, callback)
    }

    @Test
    fun successFromDownloadService_successfulSaveToRepository_callsSuccessOnCallback() {
        verify(flashcardDownloadService).downloadFlashcardsByCategory(eq(downloadCategoryList),
                downloadCallbackCaptor.capture())
        downloadCallbackCaptor.firstValue.onFlashcardsDownloaded(downloadFlashcardList)
        verify(flashcardRepository).saveFlashcards(eq(FLASHCARD_LIST),
                saveFlashcardCallbackCaptor.capture())
        saveFlashcardCallbackCaptor.firstValue.onSaveSuccessful()
        verify(callback).onSuccess(any())
    }

    @Test
    fun successFromDownloadService_failedSaveOnRepo_callsErrorOnCallback() {
        verify(flashcardDownloadService).downloadFlashcardsByCategory(eq(downloadCategoryList),
                downloadCallbackCaptor.capture())
        downloadCallbackCaptor.firstValue.onFlashcardsDownloaded(downloadFlashcardList)
        verify(flashcardRepository).saveFlashcards(eq(FLASHCARD_LIST),
                saveFlashcardCallbackCaptor.capture())
        saveFlashcardCallbackCaptor.firstValue.onSaveFailed()
        verify(callback).onError()
    }

    @Test
    fun failureFromDownloadService_doesNotCallRepo_callsErrorOnCallback() {
        verify(flashcardDownloadService).downloadFlashcardsByCategory(eq(downloadCategoryList),
                downloadCallbackCaptor.capture())
        downloadCallbackCaptor.firstValue.onDataNotAvailable()
        verify(callback).onError()
        verifyNoMoreInteractions(flashcardRepository)
    }
}
