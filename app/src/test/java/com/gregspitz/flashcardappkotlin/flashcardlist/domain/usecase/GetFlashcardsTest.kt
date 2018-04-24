package com.gregspitz.flashcardappkotlin.flashcardlist.domain.usecase

import com.gregspitz.flashcardappkotlin.TestUseCaseScheduler
import com.gregspitz.flashcardappkotlin.UseCase
import com.gregspitz.flashcardappkotlin.UseCaseHandler
import com.gregspitz.flashcardappkotlin.data.model.Flashcard
import com.gregspitz.flashcardappkotlin.data.source.FlashcardDataSource
import com.gregspitz.flashcardappkotlin.data.source.FlashcardRepository
import com.nhaarman.mockito_kotlin.argumentCaptor
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import junit.framework.Assert.assertEquals
import org.junit.Before
import org.junit.Test

/**
 * Tests for {@link GetFlashcards}
 */
class GetFlashcardsTest {

    private val flashcard1 = Flashcard("0", "Front", "Back")
    private val flashcard2 = Flashcard("1", "Front", "Back")
    private val flashcards = listOf(flashcard1, flashcard2)

    private val values = GetFlashcards.RequestValues()

    private val flashcardRepository: FlashcardRepository = mock()

    private val useCaseHandler = UseCaseHandler(TestUseCaseScheduler())

    private val repositoryCallbackCaptor =
            argumentCaptor<FlashcardDataSource.GetFlashcardsCallback>()

    private val callback: UseCase.UseCaseCallback<GetFlashcards.ResponseValue> = mock()

    private val responseCaptor = argumentCaptor<GetFlashcards.ResponseValue>()

    private lateinit var getFlashcards: GetFlashcards

    @Before
    fun setup() {
        getFlashcards = GetFlashcards(flashcardRepository)
        useCaseHandler.execute(getFlashcards, values, callback)
        verify(flashcardRepository).getFlashcards(repositoryCallbackCaptor.capture())
    }

    @Test
    fun executeUseCase_getsFlashcardsFromRepositoryAndOnSuccessCallsSuccessOnCallback() {
        repositoryCallbackCaptor.firstValue.onFlashcardsLoaded(flashcards)
        verify(callback).onSuccess(responseCaptor.capture())
        assertEquals(flashcards, responseCaptor.firstValue.flashcards)
    }

    @Test
    fun executeUseCase_getsFlashcardsFromRepositoryAndOnFailureCallsFailOnCallback() {
        repositoryCallbackCaptor.firstValue.onDataNotAvailable()
        verify(callback).onError()
    }
}
