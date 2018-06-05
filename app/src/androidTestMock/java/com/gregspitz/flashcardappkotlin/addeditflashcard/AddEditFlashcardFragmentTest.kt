/*
 * Copyright (C) 2018 Greg Spitz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gregspitz.flashcardappkotlin.addeditflashcard

import android.content.Intent
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.action.ViewActions.replaceText
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.RootMatchers.withDecorView
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import com.gregspitz.flashcardappkotlin.FlashcardApplication
import com.gregspitz.flashcardappkotlin.MockTestData.FLASHCARD_1
import com.gregspitz.flashcardappkotlin.R
import com.gregspitz.flashcardappkotlin.SingleFragmentActivity
import com.gregspitz.flashcardappkotlin.data.model.Flashcard
import com.gregspitz.flashcardappkotlin.data.source.FlashcardDataSource
import org.hamcrest.Matchers
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Tests for the implementation of {@link AddEditFlashcardFragment}
 */
@RunWith(AndroidJUnit4::class)
class AddEditFlashcardFragmentTest {

    private val dataSource = FlashcardApplication.repoComponent.exposeRepository()
    private val localDataSource =
            FlashcardApplication.repoComponent.exposeLocalDataSource()

    @Rule
    @JvmField
    val testRule = ActivityTestRule<SingleFragmentActivity>(SingleFragmentActivity::class.java,
            true, false)

    @Before
    fun setup() {
        dataSource.deleteAllFlashcards()
        saveFlashcardsToRepo(FLASHCARD_1)
    }

    @After
    fun tearDown() {
        try {
            (testRule.activity.supportFragmentManager.fragments[0] as AddEditFlashcardFragment)
                    .getToast()?.cancel()
        } catch (ex: ClassCastException) {
            // Ignore. This just means the fragment was switched to a different one. That's fine.
        }
    }

    @Test
    fun startWithFlashcardId_showsFlashcardInfo() {
        launchActivityWithFlashcardId(FLASHCARD_1.id)
        onView(withId(R.id.flashcardEditCategory)).check(matches(withText(FLASHCARD_1.category)))
        onView(withId(R.id.flashcardEditFront)).check(matches(withText(FLASHCARD_1.front)))
        onView(withId(R.id.flashcardEditBack)).check(matches(withText(FLASHCARD_1.back)))
    }

    @Test
    fun startWithNewFlashcardIntent_showsBlankFields() {
        launchActivityWithFlashcardId(AddEditFlashcardFragment.newFlashcardId)
        onView(withId(R.id.flashcardEditCategory)).check(matches(withText("")))
        onView(withId(R.id.flashcardEditFront)).check(matches(withText("")))
        onView(withId(R.id.flashcardEditBack)).check(matches(withText("")))
    }

    @Test
    fun startWithBadFlashcardId_showsFailedToLoad() {
        launchActivityWithFlashcardId("Bad ID")
        onView(withId(R.id.failedToLoadFlashcard)).check(matches(isDisplayed()))
    }

    @Test
    fun saveFlashcardButtonClick_savesChangesToExistingFlashcard() {
        launchActivityWithFlashcardId(FLASHCARD_1.id)

        val newCategory = "Category1"
        val newFront = "New Front"
        val newBack = "New Back"
        onView(withId(R.id.flashcardEditCategory)).perform(replaceText(newCategory))
        onView(withId(R.id.flashcardEditFront)).perform(replaceText(newFront))
        onView(withId(R.id.flashcardEditBack)).perform(replaceText(newBack))
        onView(withId(R.id.saveFlashcardButton)).perform(click())

        // Successful save shows save success toast
        checkForToast(R.string.save_successful_toast_text)

        val savedFlashcard = getFlashcardFromRepoById(FLASHCARD_1.id)
        assertEquals(newCategory, savedFlashcard.category)
        assertEquals(newFront, savedFlashcard.front)
        assertEquals(newBack, savedFlashcard.back)
    }

    @Test
    fun saveFailed_showsSaveFailedToast() {
        localDataSource.setFailure(true)
        launchActivityWithFlashcardId(FLASHCARD_1.id)
        onView(withId(R.id.saveFlashcardButton)).perform(click())
        checkForToast(R.string.save_failed_toast_text)
    }

    @Test
    fun deleteSuccess_deletesFlashcardAndShowsFlashcardListView() {
        launchActivityWithFlashcardId(FLASHCARD_1.id)

        onView(withId(R.id.deleteFlashcardButton)).perform(click())

        assertTrue(verifyFlashcardNoLongerInRepo(FLASHCARD_1))
        onView(withId(R.id.detailPager)).check(matches(isDisplayed()))
    }

    @Test
    fun deleteFailure_showsDeleteFailedToast() {
        localDataSource.setDeleteFailure(true)
        launchActivityWithFlashcardId(FLASHCARD_1.id)
        onView(withId(R.id.deleteFlashcardButton)).perform(click())
        checkForToast(R.string.delete_failed_toast_text)
    }

    @Test
    fun showListButtonClickIntentWithId_showsFlashcardListViewWithThatFlashcardInDetailView() {
        launchActivityWithFlashcardId(FLASHCARD_1.id)
        onView(withId(R.id.showFlashcardListButton)).perform(click())
        checkDetailViewMatchesFlashcard(FLASHCARD_1)
    }

    @Test
    fun showListButtonClickNewFlashcard_showsFlashcardListViewWithNoParticularFlashcard() {
        launchActivityWithFlashcardId(AddEditFlashcardFragment.newFlashcardId)
        onView(withId(R.id.showFlashcardListButton)).perform(click())
        onView(withId(R.id.detailPager)).check(matches(isDisplayed()))
    }

    private fun checkForToast(stringId: Int) {
        onView(withText(stringId))
                .inRoot(withDecorView(not(testRule.activity.window.decorView)))
                .check(matches(isDisplayed()))
    }

    private fun saveFlashcardsToRepo(vararg flashcards: Flashcard) {
        for (flashcard in flashcards) {
            dataSource.saveFlashcard(flashcard, object : FlashcardDataSource.SaveFlashcardCallback {
                override fun onSaveSuccessful() { /* ignore */ }
                override fun onSaveFailed() { /* ignore */ }
            })
        }
    }

    private fun getFlashcardFromRepoById(id: String): Flashcard {
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

    private fun verifyFlashcardNoLongerInRepo(flashcard: Flashcard): Boolean {
        var returnValue = false
        dataSource.getFlashcard(flashcard.id, object: FlashcardDataSource.GetFlashcardCallback {
            override fun onDataNotAvailable() {
                returnValue = true
            }

            override fun onFlashcardLoaded(flashcard: Flashcard) {
                // Ignore
            }
        })

        return returnValue
    }

    private fun checkDetailViewMatchesFlashcard(flashcard: Flashcard) {
        onView(Matchers.allOf(withId(R.id.flashcardFront), isDescendantOfA(withId(R.id.detailPager)),
                isCompletelyDisplayed()))
                .check(matches(withText(flashcard.front)))
        onView(Matchers.allOf(withId(R.id.flashcardBack), isDescendantOfA(withId(R.id.detailPager)),
                isCompletelyDisplayed()))
                .check(matches(withText(flashcard.back)))
    }


    private fun launchActivityWithFlashcardId(flashcardId: String) {
        testRule.launchActivity(Intent())
        testRule.activity.setFragment(AddEditFlashcardFragment.newInstance(flashcardId))
    }
}
