package com.gregspitz.flashcardappkotlin.addeditflashcard

import android.content.Intent
import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.action.ViewActions.replaceText
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.intent.Intents.intended
import android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent
import android.support.test.espresso.intent.rule.IntentsTestRule
import android.support.test.espresso.matcher.RootMatchers.withDecorView
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.runner.AndroidJUnit4
import com.gregspitz.flashcardappkotlin.R
import com.gregspitz.flashcardappkotlin.data.model.Flashcard
import com.gregspitz.flashcardappkotlin.data.source.FakeFlashcardLocalDataSource
import com.gregspitz.flashcardappkotlin.data.source.FlashcardDataSource
import com.gregspitz.flashcardappkotlin.data.source.FlashcardRepository
import com.gregspitz.flashcardappkotlin.flashcardlist.FlashcardListActivity
import junit.framework.Assert.assertEquals
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Tests for the implementation of {@link AddEditFlashcardActivity}
 */
@RunWith(AndroidJUnit4::class)
class AddEditFlashcardActivityTest {

    private val flashcard = Flashcard("0", "Front", "Back")

    @Rule
    @JvmField
    val testRule = IntentsTestRule<AddEditFlashcardActivity>(AddEditFlashcardActivity::class.java,
            true, false)

    @Before
    fun setup() {
        FlashcardRepository.destroyInstance()
        FakeFlashcardLocalDataSource.getInstance(
                InstrumentationRegistry.getTargetContext()
        ).addFlashcards(flashcard)
    }

    @Test
    fun startWithFlashcardId_showsFlashcardInfo() {
        launchActivityWithStringIntent(flashcard.id)
        onView(withId(R.id.flashcardEditFront)).check(matches(withText(flashcard.front)))
        onView(withId(R.id.flashcardEditBack)).check(matches(withText(flashcard.back)))
    }

    @Test
    fun startWithNewFlashcardIntent_showsBlankFields() {
        launchActivityWithStringIntent(AddEditFlashcardActivity.newFlashcardExtra)
        onView(withId(R.id.flashcardEditFront)).check(matches(withText("")))
        onView(withId(R.id.flashcardEditBack)).check(matches(withText("")))
    }

    @Test
    fun startWithBadFlashcardId_showsFailedToLoad() {
        launchActivityWithStringIntent("Bad ID")
        onView(withId(R.id.failedToLoadFlashcard)).check(matches(isDisplayed()))
    }

    @Test
    fun saveFlashcardButtonClick_savesChangesToExistingFlashcard() {
        launchActivityWithStringIntent(flashcard.id)

        val newFront = "New Front"
        val newBack = "New Back"
        onView(withId(R.id.flashcardEditFront)).perform(replaceText(newFront))
        onView(withId(R.id.flashcardEditBack)).perform(replaceText(newBack))
        onView(withId(R.id.saveFlashcardButton)).perform(click())

        // Successful save shows save success toast
        checkForToast(R.string.save_successful_toast_text)

        val savedFlashcard = getFlashcardFromRepoById(flashcard.id)
        assertEquals(newFront, savedFlashcard.front)
        assertEquals(newBack, savedFlashcard.back)
    }

    @Test
    fun saveFailed_showsSaveFailedToast() {
        FakeFlashcardLocalDataSource.getInstance(InstrumentationRegistry.getTargetContext())
                .setFailure(true)
        launchActivityWithStringIntent(flashcard.id)
        onView(withId(R.id.saveFlashcardButton)).perform(click())
        checkForToast(R.string.save_failed_toast_text)

    }

    private fun checkForToast(stringId: Int) {
        onView(withText(stringId))
                .inRoot(withDecorView(not(testRule.activity.window.decorView)))
                .check(matches(isDisplayed()))
    }

    @Test
    fun showListButtonClick_showsFlashcardListView() {
        launchActivityWithStringIntent(flashcard.id)
        onView(withId(R.id.showFlashcardListButton)).perform(click())
        intended(hasComponent(FlashcardListActivity::class.java.name))
    }

    private fun getFlashcardFromRepoById(id: String): Flashcard {
        val savedFlashcards = mutableListOf<Flashcard>()
        FakeFlashcardLocalDataSource.getInstance(InstrumentationRegistry.getTargetContext())
                .getFlashcard(id, object : FlashcardDataSource.GetFlashcardCallback {
                    override fun onFlashcardLoaded(flashcard: Flashcard) {
                        savedFlashcards.add(flashcard)
                    }

                    override fun onDataNotAvailable() {

                    }

                })
        return savedFlashcards[0]
    }


    private fun launchActivityWithStringIntent(extra: String) {
        val intent = Intent()
        intent.putExtra(AddEditFlashcardActivity.flashcardIdExtra, extra)
        testRule.launchActivity(intent)
    }
}
