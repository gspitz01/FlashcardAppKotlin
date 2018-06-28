package com.gregspitz.flashcardappkotlin

import android.content.Intent
import android.support.test.espresso.Espresso
import android.support.test.espresso.action.ViewActions
import android.support.test.espresso.assertion.ViewAssertions
import android.support.test.espresso.matcher.RootMatchers
import android.support.test.espresso.matcher.ViewMatchers
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
        dataSource.deleteAllFlashcards(object: FlashcardDataSource.DeleteAllFlashcardsCallback {
            override fun onDeleteSuccessful() { /* ignore */ }

            override fun onDeleteFailed() { /* ignore */ }
        })
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

    protected fun clickOnDeleteDialog(responseStringId: Int) {
        Espresso.onView(ViewMatchers.withText(responseStringId))
                .inRoot(RootMatchers.isDialog())
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
                .perform(ViewActions.click())
    }

    protected fun verifyFlashcardNotInRepo(flashcard: Flashcard): Boolean {
        var returnValue = false
        dataSource.getFlashcard(flashcard.id, object : FlashcardDataSource.GetFlashcardCallback {
            override fun onDataNotAvailable() {
                returnValue = true
            }

            override fun onFlashcardLoaded(flashcard: Flashcard) {
                // Ignore
            }
        })

        return returnValue
    }

    protected fun verifyFlashcardInInRepo(flashcard: Flashcard): Boolean {
        var returnValue = false
        dataSource.getFlashcard(flashcard.id, object : FlashcardDataSource.GetFlashcardCallback {
            override fun onDataNotAvailable() {

            }

            override fun onFlashcardLoaded(flashcard: Flashcard) {
                returnValue = true
            }
        })

        return returnValue
    }
}
