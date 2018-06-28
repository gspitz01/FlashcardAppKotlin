package com.gregspitz.flashcardappkotlin.flashcardlist.domain.usecase

import com.gregspitz.flashcardappkotlin.TestData.FLASHCARD_1
import com.gregspitz.flashcardappkotlin.TestUseCaseScheduler
import com.gregspitz.flashcardappkotlin.UseCase
import com.gregspitz.flashcardappkotlin.UseCaseHandler
import com.gregspitz.flashcardappkotlin.data.source.FlashcardDataSource
import com.gregspitz.flashcardappkotlin.data.source.FlashcardRepository
import com.nhaarman.mockito_kotlin.*
import org.junit.Before
import org.junit.Test

class DeleteFlashcardsTest {

    private val noCategoryRequest =
            DeleteFlashcards.RequestValues()
    private val categoryRequest =
            DeleteFlashcards.RequestValues(FLASHCARD_1.category)

    private val flashcardRepository: FlashcardRepository = mock()

    private val useCaseHandler = UseCaseHandler(TestUseCaseScheduler())

    private val deleteAllRepositoryCallbackCaptor =
            argumentCaptor<FlashcardDataSource.DeleteAllFlashcardsCallback>()
    private val deleteByCategoryNameRepositoryCallbackCaptor =
            argumentCaptor<FlashcardDataSource.DeleteFlashcardsByCategoryNameCallback>()

    private val callback: UseCase.UseCaseCallback<DeleteFlashcards.ResponseValue> = mock()

    private lateinit var deleteFlashcards: DeleteFlashcards

    @Before
    fun setup() {
        deleteFlashcards = DeleteFlashcards(flashcardRepository)
    }

    @Test
    fun `execute without category, deletes all flashcards from repository`() {
        useCaseHandler.execute(deleteFlashcards, noCategoryRequest, callback)
        verify(flashcardRepository).deleteAllFlashcards(deleteAllRepositoryCallbackCaptor.capture())
        deleteAllRepositoryCallbackCaptor.firstValue.onDeleteSuccessful()
        verify(callback).onSuccess(any())
    }

    @Test
    fun `execute without category, failure from repository, calls error on callback`() {
        useCaseHandler.execute(deleteFlashcards, noCategoryRequest, callback)
        verify(flashcardRepository).deleteAllFlashcards(deleteAllRepositoryCallbackCaptor.capture())
        deleteAllRepositoryCallbackCaptor.firstValue.onDeleteFailed()
        verify(callback).onError()
    }

    @Test
    fun `execute with category, deletes by category on repository`() {
        useCaseHandler.execute(deleteFlashcards, categoryRequest, callback)
        verify(flashcardRepository).deleteFlashcardsByCategoryName(
                eq(categoryRequest.categoryName!!),
                deleteByCategoryNameRepositoryCallbackCaptor.capture())
        deleteByCategoryNameRepositoryCallbackCaptor.firstValue.onDeleteSuccessful()
        verify(callback).onSuccess(any())
    }

    @Test
    fun `execute with category, failure from repository, calls error on callback`() {
        useCaseHandler.execute(deleteFlashcards, categoryRequest, callback)
        verify(flashcardRepository).deleteFlashcardsByCategoryName(
                eq(categoryRequest.categoryName!!),
                deleteByCategoryNameRepositoryCallbackCaptor.capture())
        deleteByCategoryNameRepositoryCallbackCaptor.firstValue.onDeleteFailed()
        verify(callback).onError()
    }
}
