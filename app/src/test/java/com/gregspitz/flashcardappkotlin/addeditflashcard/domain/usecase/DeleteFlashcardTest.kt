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
    fun execute_onSuccessFromRepo_callsSuccessOnCallback() {
        repositoryCallbackCaptor.firstValue.onDeleteSuccessful()
        verify(callback).onSuccess(any())
    }

    @Test
    fun execute_onFailureFromRepo_callsErrorOnCallback() {
        repositoryCallbackCaptor.firstValue.onDeleteFailed()
        verify(callback).onError()
    }
}
