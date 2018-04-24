package com.gregspitz.flashcardappkotlin.flashcarddetail.domain.usecase

import com.gregspitz.flashcardappkotlin.TestUseCaseScheduler
import com.gregspitz.flashcardappkotlin.UseCase
import com.gregspitz.flashcardappkotlin.UseCaseHandler
import com.gregspitz.flashcardappkotlin.data.model.Flashcard
import com.gregspitz.flashcardappkotlin.data.source.FlashcardDataSource
import com.gregspitz.flashcardappkotlin.data.source.FlashcardRepository
import com.nhaarman.mockito_kotlin.argumentCaptor
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import junit.framework.Assert.assertEquals
import org.junit.Before
import org.junit.Test

/**
 * Tests for {@link GetFlashcard}
 */
class GetFlashcardTest {

    private val flashcard = Flashcard("0", "Front", "Back")

    private val values = GetFlashcard.RequestValues(flashcard.id)

    private val flashcardRepository: FlashcardRepository = mock()

    private val useCaseHandler = UseCaseHandler(TestUseCaseScheduler())

    private val repositoryCallbackCaptor =
            argumentCaptor<FlashcardDataSource.GetFlashcardCallback>()

    private val callback: UseCase.UseCaseCallback<GetFlashcard.ResponseValue> = mock()

    private val responseCaptor = argumentCaptor<GetFlashcard.ResponseValue>()

    private lateinit var getFlashcard: GetFlashcard

    @Before
    fun setup() {
        getFlashcard = GetFlashcard(flashcardRepository)
        useCaseHandler.execute(getFlashcard, values, callback)
        verify(flashcardRepository)
                .getFlashcard(eq(flashcard.id), repositoryCallbackCaptor.capture())
    }

    @Test
    fun executeUseCase_getsFlashcardFromRepoAndOnSuccessCallsSuccessOnCallback() {
        repositoryCallbackCaptor.firstValue.onFlashcardLoaded(flashcard)
        verify(callback).onSuccess(responseCaptor.capture())
        assertEquals(flashcard, responseCaptor.firstValue.flashcard)
    }

    @Test
    fun executeUseCase_getsFlashcardFromRepoAndOnFailCallsFailOnCallback() {
        repositoryCallbackCaptor.firstValue.onDataNotAvailable()
        verify(callback).onError()
    }
}
