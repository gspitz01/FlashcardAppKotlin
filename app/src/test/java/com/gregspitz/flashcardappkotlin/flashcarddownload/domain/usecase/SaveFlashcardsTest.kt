package com.gregspitz.flashcardappkotlin.flashcarddownload.domain.usecase

import com.gregspitz.flashcardappkotlin.TestData.FLASHCARD_LIST
import com.gregspitz.flashcardappkotlin.TestUseCaseScheduler
import com.gregspitz.flashcardappkotlin.UseCase
import com.gregspitz.flashcardappkotlin.UseCaseHandler
import com.gregspitz.flashcardappkotlin.data.source.FlashcardDataSource
import com.gregspitz.flashcardappkotlin.data.source.FlashcardRepository
import com.nhaarman.mockito_kotlin.*
import org.junit.Before
import org.junit.Test

class SaveFlashcardsTest {

    private val values = SaveFlashcards.RequestValues(FLASHCARD_LIST)

    private val useCaseHandler = UseCaseHandler(TestUseCaseScheduler())

    private val flashcardRepository: FlashcardRepository = mock()

    private val repoCallbackCaptor =
            argumentCaptor<FlashcardDataSource.SaveFlashcardsCallback>()

    private val callback: UseCase.UseCaseCallback<SaveFlashcards.ResponseValue> = mock()

    private lateinit var saveFlashcards: SaveFlashcards

    @Before
    fun setup() {
        saveFlashcards = SaveFlashcards(flashcardRepository)
        useCaseHandler.execute(saveFlashcards, values, callback)
        verify(flashcardRepository).saveFlashcards(eq(FLASHCARD_LIST), repoCallbackCaptor.capture())
    }

    @Test
    fun onExecute_successFromRepo_callsSuccessOnCallback() {
        repoCallbackCaptor.firstValue.onSaveSuccessful()
        verify(callback).onSuccess(any())
    }

    @Test
    fun onExecute_failureFromRepo_callsFailureOnCallback() {
        repoCallbackCaptor.firstValue.onSaveFailed()
        verify(callback).onError()
    }
}
