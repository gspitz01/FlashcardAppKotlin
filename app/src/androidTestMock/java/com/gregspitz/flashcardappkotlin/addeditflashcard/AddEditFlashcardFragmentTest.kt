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

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.action.ViewActions.replaceText
import android.support.test.espresso.assertion.ViewAssertions.doesNotExist
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.filters.MediumTest
import android.support.test.runner.AndroidJUnit4
import com.gregspitz.flashcardappkotlin.BaseSingleFragmentTest
import com.gregspitz.flashcardappkotlin.MockTestData.FLASHCARD_1
import com.gregspitz.flashcardappkotlin.MockTestData.FLASHCARD_2
import com.gregspitz.flashcardappkotlin.R
import com.gregspitz.flashcardappkotlin.data.model.Flashcard
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Tests for the implementation of {@link AddEditFlashcardFragment}
 */
@RunWith(AndroidJUnit4::class)
@MediumTest
class AddEditFlashcardFragmentTest : BaseSingleFragmentTest() {

    @Before
    fun setupTests() {
        addFlashcardsToDataSource(FLASHCARD_1)
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
        launchActivity(FLASHCARD_1.id)
        onView(withId(R.id.flashcardEditCategory)).check(matches(withText(FLASHCARD_1.category)))
        onView(withId(R.id.flashcardEditFront)).check(matches(withText(FLASHCARD_1.front)))
        onView(withId(R.id.flashcardEditBack)).check(matches(withText(FLASHCARD_1.back)))
    }

    @Test
    fun startWithNewFlashcardIntent_showsBlankFields() {
        launchActivity(AddEditFlashcardFragment.newFlashcardId)
        onView(withId(R.id.flashcardEditCategory)).check(matches(withText("")))
        onView(withId(R.id.flashcardEditFront)).check(matches(withText("")))
        onView(withId(R.id.flashcardEditBack)).check(matches(withText("")))
    }

    @Test
    fun startWithBadFlashcardId_showsFailedToLoad() {
        launchActivity("Bad ID")
        onView(withId(R.id.failedToLoadFlashcard)).check(matches(isDisplayed()))
    }

    @Test
    fun saveFlashcardButtonClick_savesChangesToExistingFlashcard_movesToFlashcardListOfThatCategoryWithDetailOfThatFlashcard() {
        val newCategory = "Category1"
        val sortBeforeFront = "Mew Front"
        val someOtherBack = "Some other back"
        val flashcardSortsBeforeNewFlashcard = Flashcard("whatever", newCategory, sortBeforeFront,
                someOtherBack)
        // Add to data source a flashcard that sorts before the new flashcard so that the detail
        // view in CategoryFlashcardList is actually checked that it moves to newFlashcard correctly
        // Also add FLASHCARD_2, which is in a different category to prove that only the correct
        // category is shown when it moves to FlashcardList
        addFlashcardsToDataSource(flashcardSortsBeforeNewFlashcard, FLASHCARD_2)

        launchActivity(FLASHCARD_1.id)

        val newFront = "New Front"
        val newBack = "New Back"
        val newFlashcard = Flashcard(FLASHCARD_1.id, newCategory, newFront, newBack)
        onView(withId(R.id.flashcardEditCategory)).perform(replaceText(newCategory))
        onView(withId(R.id.flashcardEditFront)).perform(replaceText(newFront))
        onView(withId(R.id.flashcardEditBack)).perform(replaceText(newBack))
        clickSaveFlashcardButton()

        // Successful save shows save success toast
        checkForToast(R.string.save_successful_toast_text)

        val savedFlashcard = getFlashcardFromRepoById(newFlashcard.id)
        assertEquals(newCategory, savedFlashcard?.category)
        assertEquals(newFront, savedFlashcard?.front)
        assertEquals(newBack, savedFlashcard?.back)

        // Check FlashcardList shows with correct detail view
        checkDetailViewMatchesFlashcard(newFlashcard)

        // Also check that there's no view shown for FLASHCARD_2 or its category because it's
        // in a different category
        onView(withText(FLASHCARD_2.category)).check(doesNotExist())
        onView(withText(FLASHCARD_2.front)).check(doesNotExist())
    }

    @Test
    fun saveFailed_showsSaveFailedToast() {
        localDataSource.setFailure(true)
        launchActivity(FLASHCARD_1.id)
        clickSaveFlashcardButton()
        checkForToast(R.string.save_failed_toast_text)
    }

    @Test
    fun deleteSuccess_deletesFlashcardAndShowsFlashcardListView() {
        launchActivity(FLASHCARD_1.id)

        clickDeleteFlashcardButton()

        // Alert dialog for confirmation
        clickOnDeleteDialog(android.R.string.yes)

        assertTrue(verifyFlashcardNotInRepo(FLASHCARD_1))
        onView(withId(R.id.detailPager)).check(matches(isDisplayed()))
    }

    @Test
    fun deleteFailure_showsDeleteFailedToast() {
        localDataSource.setDeleteFailure(true)
        launchActivity(FLASHCARD_1.id)

        clickDeleteFlashcardButton()

        // Alert dialog for confirmation
        clickOnDeleteDialog(android.R.string.yes)

        checkForToast(R.string.delete_failed_toast_text)
    }

    @Test
    fun clickNoOnDeleteDialog_returnsToEditView() {
        launchActivity(FLASHCARD_1.id)
        clickDeleteFlashcardButton()
        clickOnDeleteDialog(android.R.string.no)
        onView(withId(R.id.flashcardEditCategory)).check(matches(isDisplayed()))
    }

    @Test
    fun showListButtonClickIntentWithId_showsFlashcardListViewWithThatFlashcardInDetailView() {
        launchActivity(FLASHCARD_1.id)
        clickShowListButton()
        checkDetailViewMatchesFlashcard(FLASHCARD_1)
    }

    @Test
    fun showListButtonClickNewFlashcard_showsFlashcardListViewWithNoParticularFlashcard() {
        launchActivity(AddEditFlashcardFragment.newFlashcardId)
        clickShowListButton()
        onView(withId(R.id.detailPager)).check(matches(isDisplayed()))
    }

    private fun checkDetailViewMatchesFlashcard(flashcard: Flashcard) {
        onView(Matchers.allOf(withId(R.id.flashcardFront), isDescendantOfA(withId(R.id.detailPager)),
                isCompletelyDisplayed()))
                .check(matches(withText(flashcard.front)))
        onView(Matchers.allOf(withId(R.id.flashcardBack), isDescendantOfA(withId(R.id.detailPager)),
                isCompletelyDisplayed()))
                .check(matches(withText(flashcard.back)))
    }


    private fun launchActivity(flashcardId: String) {
        launchActivity(AddEditFlashcardFragment.newInstance(flashcardId))
    }

    private fun clickSaveFlashcardButton() {
        onView(withId(R.id.saveFlashcardButton)).perform(click())
    }

    private fun clickDeleteFlashcardButton() {
        onView(withId(R.id.deleteFlashcardButton)).perform(click())
    }

    private fun clickShowListButton() {
        onView(withId(R.id.showFlashcardListButton)).perform(click())
    }
}
