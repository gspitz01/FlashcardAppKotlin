package com.gregspitz.flashcardappkotlin.flashcardlist

import com.gregspitz.flashcardappkotlin.TestUseCaseScheduler
import com.gregspitz.flashcardappkotlin.UseCaseHandler
import com.gregspitz.flashcardappkotlin.data.source.FlashcardDataSource
import com.gregspitz.flashcardappkotlin.data.source.FlashcardRepository
import com.gregspitz.flashcardappkotlin.flashcardlist.domain.usecase.GetFlashcards
import org.junit.Before
import org.junit.Test
import org.mockito.*
import org.mockito.Mockito.verify

/**
 * Tests for the implementation of {@link FlashcardListPresenter}
 */
class FlashcardListPresenterTest {

    @Mock
    lateinit var mFlashcardRepository: FlashcardRepository

    @Mock
    lateinit var mFlashcardListView: FlashcardListContract.View

    @Captor
    lateinit var mCallbackArgumentCaptor: ArgumentCaptor<FlashcardDataSource.GetFlashcardsCallback>

    lateinit var mFlashcardListPresenter: FlashcardListPresenter

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        Mockito.`when`(mFlashcardListView.isActive()).thenReturn(true)
    }

    @Test
    fun creation_setsPresenterOnView() {
        mFlashcardListPresenter = createPresenter()
        verify(mFlashcardListView).setPresenter(mFlashcardListPresenter)
    }

    private fun createPresenter(): FlashcardListPresenter {
        return FlashcardListPresenter(
                UseCaseHandler(TestUseCaseScheduler()),
                mFlashcardListView, GetFlashcards(mFlashcardRepository))
    }
}
