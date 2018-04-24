package com.gregspitz.flashcardappkotlin.addeditflashcard.domain.usecase

import com.gregspitz.flashcardappkotlin.TestUseCaseScheduler
import com.gregspitz.flashcardappkotlin.UseCase
import com.gregspitz.flashcardappkotlin.UseCaseHandler
import com.gregspitz.flashcardappkotlin.data.model.Flashcard
import com.gregspitz.flashcardappkotlin.data.source.FlashcardDataSource
import com.gregspitz.flashcardappkotlin.data.source.FlashcardRepository
import com.nhaarman.mockito_kotlin.*
import org.junit.Before
import org.junit.Test

/**
 * Tests for {@link SaveFlashcard}
 */
class SaveFlashcardTest {

    private val flashcard = Flashcard("0", "Front", "Back")

    private val values = SaveFlashcard.RequestValues(flashcard)

    private val flashcardRepository: FlashcardRepository = mock()

    private val useCaseHandler = UseCaseHandler(TestUseCaseScheduler())

    private val repositoryCallbackCaptor =
            argumentCaptor<FlashcardDataSource.SaveFlashcardCallback>()

    private val callback: UseCase.UseCaseCallback<SaveFlashcard.ResponseValue> = mock()

    private lateinit var saveFlashcard: SaveFlashcard

    @Before
    fun setup() {
        saveFlashcard = SaveFlashcard(flashcardRepository)
        useCaseHandler.execute(saveFlashcard, values, callback)
        verify(flashcardRepository).saveFlashcard(eq(flashcard), repositoryCallbackCaptor.capture())
    }

    @Test
    fun executeUseCase_savesFlashcardToRepositoryAndSuccessCallsCallbackSuccess() {
        repositoryCallbackCaptor.firstValue.onSaveSuccessful()
        verify(callback).onSuccess(any())
    }

    @Test
    fun executeUseCase_savesFlashcardToRepositoryAndFailCallsCallbackFail() {
        repositoryCallbackCaptor.firstValue.onSaveFailed()
        verify(callback).onError()
    }
}
