package com.gregspitz.flashcardappkotlin.flashcarddownload.domain.usecase

import com.gregspitz.flashcardappkotlin.TestData.CATEGORY_1
import com.gregspitz.flashcardappkotlin.TestData.FLASHCARD_LIST
import com.gregspitz.flashcardappkotlin.TestUseCaseScheduler
import com.gregspitz.flashcardappkotlin.UseCase
import com.gregspitz.flashcardappkotlin.UseCaseHandler
import com.gregspitz.flashcardappkotlin.data.service.FlashcardDownloadService
import com.gregspitz.flashcardappkotlin.data.service.model.DownloadCategory
import com.gregspitz.flashcardappkotlin.data.service.model.DownloadFlashcard
import com.gregspitz.flashcardappkotlin.flashcarddownload.DownloadCategoryFlexItem
import com.nhaarman.mockito_kotlin.*
import org.junit.Before
import org.junit.Test

/**
 * Tests for {@link DownloadFlashcards}
 */
class DownloadFlashcardsTest {

    private val downloadCategory = DownloadCategory(CATEGORY_1.name, 4)
    private val downloadCategoryFlexItem = DownloadCategoryFlexItem(downloadCategory)
    private val values =
            DownloadFlashcards.RequestValues(downloadCategoryFlexItem)

    private val downloadFlashcardList = FLASHCARD_LIST.map {
        DownloadFlashcard(it.id, it.category, it.front, it.back)
    }

    private val useCaseHandler = UseCaseHandler(TestUseCaseScheduler())

    private val flashcardDownloadService: FlashcardDownloadService = mock()

    private val downloadCallbackCaptor =
            argumentCaptor<FlashcardDownloadService.DownloadFlashcardsCallback>()

    private val callback: UseCase.UseCaseCallback<DownloadFlashcards.ResponseValue> = mock()

    private lateinit var downloadFlashcards: DownloadFlashcards

    @Before
    fun setup() {
        downloadFlashcards = DownloadFlashcards(flashcardDownloadService)
        useCaseHandler.execute(downloadFlashcards, values, callback)
    }

    @Test
    fun `success from download service, calls success on callback`() {
        verify(flashcardDownloadService).downloadFlashcardsByCategory(eq(downloadCategory),
                downloadCallbackCaptor.capture())
        downloadCallbackCaptor.firstValue.onFlashcardsDownloaded(downloadFlashcardList)
        verify(callback).onSuccess(any())
    }


    @Test
    fun `failure from download service, calls error on callback`() {
        verify(flashcardDownloadService).downloadFlashcardsByCategory(eq(downloadCategory),
                downloadCallbackCaptor.capture())
        downloadCallbackCaptor.firstValue.onDataNotAvailable()
        verify(callback).onError()
    }
}
