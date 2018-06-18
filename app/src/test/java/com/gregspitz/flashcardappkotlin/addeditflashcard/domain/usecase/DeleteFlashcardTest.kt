package com.gregspitz.flashcardappkotlin.addeditflashcard.domain.usecase

import com.gregspitz.flashcardappkotlin.TestData.FLASHCARD_1
import com.gregspitz.flashcardappkotlin.TestUseCaseScheduler
import com.gregspitz.flashcardappkotlin.UseCase
import com.gregspitz.flashcardappkotlin.UseCaseHandler
import com.gregspitz.flashcardappkotlin.data.source.FlashcardDataSource
import com.gregspitz.flashcardappkotlin.data.source.FlashcardRepository
import com.nhaarman.mockito_kotlin.*
import org.junit.Before
import org.junit.Test

class DeleteFlashcardTest {

    private val values = DeleteFlashcard.RequestValues(FLASHCARD_1.id)

    private val flashcardRepository: FlashcardRepository = mock()

    private val useCaseHandler = UseCaseHandler(TestUseCaseScheduler())

    private val repositoryCallbackCaptor =
            argumentCaptor<FlashcardDataSource.DeleteFlashcardCallback>()

    private val callback: UseCase.UseCaseCallback<DeleteFlashcard.ResponseValue> = mock()

    private lateinit var deleteFlashcard: DeleteFlashcard

    @Before
    fun setup() {
        deleteFlashcard = DeleteFlashcard(flashcardRepository)
        useCaseHandler.execute(deleteFlashcard, values, callback)
        verify(flashcardRepository)
                .deleteFlashcard(eq(FLASHCARD_1.id), repositoryCallbackCaptor.capture())
    }

    @Test
    fun `on success from repository, calls success on callback`() {
        repositoryCallbackCaptor.firstValue.onDeleteSuccessful()
        verify(callback).onSuccess(any())
    }

    @Test
    fun `on failure from repository, calls error on callback`() {
        repositoryCallbackCaptor.firstValue.onDeleteFailed()
        verify(callback).onError()
    }
}
