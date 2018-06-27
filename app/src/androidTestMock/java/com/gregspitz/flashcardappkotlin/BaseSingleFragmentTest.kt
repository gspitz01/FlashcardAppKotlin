package com.gregspitz.flashcardappkotlin

import android.content.Intent
import android.support.test.rule.ActivityTestRule
import android.support.v4.app.Fragment
import com.gregspitz.flashcardappkotlin.data.model.Flashcard
import com.gregspitz.flashcardappkotlin.data.source.FlashcardDataSource
import org.junit.Before
import org.junit.Rule

open class BaseSingleFragmentTest {

    protected val dataSource = FlashcardApplication.repoComponent.exposeRepository()
    protected val localDataSource =
            FlashcardApplication.repoComponent.exposeLocalDataSource()

    @Rule
    @JvmField
    val testRule = ActivityTestRule<SingleFragmentActivity>(
            SingleFragmentActivity::class.java, true, false)

    @Before
    fun setup() {
        dataSource.deleteAllFlashcards()
    }

    protected fun launchActivity(fragment: Fragment) {
        testRule.launchActivity(Intent())
        testRule.activity.setFragment(fragment)
    }

    protected fun addFlashcardsToDataSource(vararg flashcards: Flashcard) {
        for (flashcard in flashcards) {
            dataSource.saveFlashcard(flashcard, object: FlashcardDataSource.SaveFlashcardCallback {
                override fun onSaveSuccessful() { /* ignore */ }

                override fun onSaveFailed() { /* ignore */ }
            })
        }
    }

    protected fun getFlashcardFromRepoById(id: String): Flashcard? {
        val savedFlashcards = mutableListOf<Flashcard>()
        dataSource.getFlashcard(id, object : FlashcardDataSource.GetFlashcardCallback {
            override fun onFlashcardLoaded(flashcard: Flashcard) {
                savedFlashcards.add(flashcard)
            }

            override fun onDataNotAvailable() {

            }

        })
        return savedFlashcards[0]
    }

    protected fun requestOrientation(orientation: Int) {
        testRule.activity.requestedOrientation = orientation
    }

    protected fun checkForToast(text: Int) {
        TestUtils.checkForToast(testRule, text)
    }
}
