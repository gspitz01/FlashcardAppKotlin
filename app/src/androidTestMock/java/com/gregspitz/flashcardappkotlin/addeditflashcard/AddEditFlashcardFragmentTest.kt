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

import android.content.pm.ActivityInfo
import android.support.test.espresso.Espresso
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.NoActivityResumedException
import android.support.test.espresso.action.ViewActions.*
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

    private val newCategory = "New Category"
    private val newFront = "New Front"
    private val newBack = "New Back"

    @Before
    fun setupTests() {
        addFlashcardsToDataSource(FLASHCARD_1)
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
        verifyEditTextsBlank()
    }

    @Test
    fun startWithNewFlashcardIntent_typeInFields_orientationChangeStillShowsText() {
        launchActivity(AddEditFlashcardFragment.newFlashcardId)
        requestOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)

        onView(withId(R.id.flashcardEditCategory)).perform(typeText(newCategory))
        onView(withId(R.id.flashcardEditFront)).perform(typeText(newFront))
        onView(withId(R.id.flashcardEditBack)).perform(typeText(newBack))

        requestOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)

        onView(withId(R.id.flashcardEditCategory)).check(matches(withText(newCategory)))
        onView(withId(R.id.flashcardEditFront)).check(matches(withText(newFront)))
        onView(withId(R.id.flashcardEditBack)).check(matches(withText(newBack)))
    }

    @Test
    fun startWithFlashcardId_replaceText_orientationChangeStillShowsReplacedText() {
        launchActivity(FLASHCARD_1.id)
        requestOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)

        replaceEditTextText(newCategory, R.id.flashcardEditCategory)
        replaceEditTextText(newFront, R.id.flashcardEditFront)
        replaceEditTextText(newBack, R.id.flashcardEditBack)

        requestOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)

        onView(withId(R.id.flashcardEditCategory)).check(matches(withText(newCategory)))
        onView(withId(R.id.flashcardEditFront)).check(matches(withText(newFront)))
        onView(withId(R.id.flashcardEditBack)).check(matches(withText(newBack)))
    }

    @Test
    fun startWithBadFlashcardId_showsFailedToLoad() {
        launchActivity("Bad ID")
        verifyEditTextsBlank()
        checkForSnackbar(R.string.failed_to_load_flashcard_text)
        verifyEditTextsBlank()
    }

    @Test
    fun saveFlashcardButtonClick_savesChangesToExistingFlashcard_movesToFlashcardListOfThatCategoryWithDetailOfThatFlashcard() {
        // TODO: The functionality which is supposed to be test here works, but this test does not
        // pass because the checking for Snackbar doesn't work properly
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

        val newFlashcard = Flashcard(FLASHCARD_1.id, newCategory, newFront, newBack)
        onView(withId(R.id.flashcardEditCategory)).perform(replaceText(newCategory))
        onView(withId(R.id.flashcardEditFront)).perform(replaceText(newFront))
        onView(withId(R.id.flashcardEditBack)).perform(replaceText(newBack))
        clickSaveFlashcardButton()

        // Successful save shows save success message
        checkForSnackbar(R.string.save_successful_message_text)

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
    fun saveFailed_showsSaveFailedMessage() {
        localDataSource.setFailure(true)
        launchActivity(FLASHCARD_1.id)
        clickSaveFlashcardButton()
        checkForSnackbar(R.string.save_failed_message_text)
    }

    @Test
    fun tryToSaveWithoutCategory_showsMustHaveCategoryMessage() {
        launchActivity(AddEditFlashcardFragment.newFlashcardId)
        clickSaveFlashcardButton()
        checkForSnackbar(R.string.flashcard_must_have_category_message_text)
        // Still only one Flashcard in the repo
        assertEquals(numberOfFlashcardsInRepo(), 1)
    }

    @Test
    fun tryToSaveWithoutFront_showsMustHaveFrontMessage() {
        launchActivity(AddEditFlashcardFragment.newFlashcardId)
        onView(withId(R.id.flashcardEditCategory)).perform(replaceText(newCategory))
        clickSaveFlashcardButton()
        checkForSnackbar(R.string.flashcard_must_have_front_message_text)
        // Still only 1 Flashcard in the repo
        assertEquals(numberOfFlashcardsInRepo(), 1)
    }

    @Test
    fun tryToSaveWithoutBack_showsMustHaveBackMessage() {
        launchActivity(AddEditFlashcardFragment.newFlashcardId)
        onView(withId(R.id.flashcardEditCategory)).perform(replaceText(newCategory))
        onView(withId(R.id.flashcardEditFront)).perform(replaceText(newFront))
        clickSaveFlashcardButton()
        checkForSnackbar(R.string.flashcard_must_have_back_message_text)
        // Still only 1 Flashcard in the repo
        assertEquals(numberOfFlashcardsInRepo(), 1)
    }

    @Test
    fun deleteSuccess_deletesFlashcardAndShowsFlashcardListView() {
        launchActivity(FLASHCARD_1.id)

        clickDeleteFlashcardButton()

        // Alert dialog for confirmation
        clickOnDialog(android.R.string.yes)

        assertTrue(verifyFlashcardNotInRepo(FLASHCARD_1))
        verifyListViewShown()
    }

    @Test
    fun deleteFailure_showsDeleteFailedMessage() {
        localDataSource.setDeleteFailure(true)
        launchActivity(FLASHCARD_1.id)

        clickDeleteFlashcardButton()

        // Alert dialog for confirmation
        clickOnDialog(android.R.string.yes)

        checkForSnackbar(R.string.delete_failed_message_text)
    }

    @Test
    fun clickNoOnDeleteDialog_returnsToEditView() {
        launchActivity(FLASHCARD_1.id)
        clickDeleteFlashcardButton()
        clickOnDialog(android.R.string.no)
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
        verifyListViewShown()
    }

    @Test
    fun launchWithNewFlashcard_typeInFront_clickListViewButton_yesOnConfirmation_movesAway() {
        launchChangeEditTextClickListViewAndClickDialog(AddEditFlashcardFragment.newFlashcardId,
                newFront, R.id.flashcardEditFront, android.R.string.yes)
        verifyListViewShown()
    }

    @Test
    fun launchWithNewFlashcard_typeInCategory_clickListViewButton_noOnConfirmation_staysOnView() {
        launchChangeEditTextClickListViewAndClickDialog(AddEditFlashcardFragment.newFlashcardId,
                newCategory, R.id.flashcardEditCategory, android.R.string.no)
        onView(withId(R.id.flashcardEditCategory)).check(matches(withText(newCategory)))
    }

    @Test
    fun launchWithNewFlashcard_typeInFront_clickListViewButton_orientationChange_yesOnConfirmation_movesAway() {
        launchActivity(AddEditFlashcardFragment.newFlashcardId)
        requestOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        replaceEditTextText(newFront, R.id.flashcardEditFront)
        clickShowListButton()
        requestOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
        clickOnDialog(android.R.string.yes)
        verifyListViewShown()
    }

    @Test
    fun launchWithNewFlashcard_typeInCategory_clickListViewButton_orientationChange_noOnConfirmation_staysOnView() {
        launchActivity(AddEditFlashcardFragment.newFlashcardId)
        requestOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        replaceEditTextText(newCategory, R.id.flashcardEditCategory)
        clickShowListButton()
        requestOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
        clickOnDialog(android.R.string.no)
        onView(withId(R.id.flashcardEditCategory)).check(matches(withText(newCategory)))
    }

    @Test
    fun launchWithNewFlashcard_typeInBack_pressBack_orientationChange_yesOnConfirmation_movesAway() {
        launchActivity(AddEditFlashcardFragment.newFlashcardId)
        requestOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        replaceEditTextText(newBack, R.id.flashcardEditBack)
        try {
            Espresso.pressBackUnconditionally()
            requestOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
            clickOnDialog(android.R.string.yes)
        } catch (expectedException: NoActivityResumedException) {
            // This is apparently the best way to verify the activity has been destroyed because of
            // a back press, which is exactly what should happen
        }
    }

    @Test
    fun launchWithNewFlashcard_typeInCategory_pressBack_orientationChange_noOnConfirmation_staysOnView() {
        launchActivity(AddEditFlashcardFragment.newFlashcardId)
        requestOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        replaceEditTextText(newCategory, R.id.flashcardEditCategory)
        Espresso.pressBack()
        requestOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
        clickOnDialog(android.R.string.no)
    }

    @Test
    fun launchWithFlashcardId_replaceCategory_clickListViewButton_orientationChange_yesOnConfirmation_movesAway() {
        launchActivity(FLASHCARD_1.id)
        requestOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        replaceEditTextText(newCategory, R.id.flashcardEditCategory)
        clickShowListButton()
        requestOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
        clickOnDialog(android.R.string.yes)
        verifyListViewShown()
    }

    @Test
    fun launchWithFlashcardId_replaceFront_pressBack_orientationChange_yesOnConfirmation_movesAway() {
        launchActivity(FLASHCARD_1.id)
        requestOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        replaceEditTextText(newFront, R.id.flashcardEditFront)
        try {
            Espresso.pressBackUnconditionally()
            requestOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
            clickOnDialog(android.R.string.yes)
        } catch (expectedException: NoActivityResumedException) {
            // This is apparently the best way to verify the activity has been destroyed because of
            // a back press, which is exactly what should happen
        }
    }

    @Test
    fun launchWithFlashcard_changeBack_clickListViewButton_yesOnConfirmation_movesAway() {
        launchChangeEditTextClickListViewAndClickDialog(FLASHCARD_1.id, newBack,
                R.id.flashcardEditBack, android.R.string.yes)
        verifyListViewShown()
    }

    @Test
    fun launchWithFlashcard_typeInFront_clickListViewButton_noOnConfirmation_staysOnView() {
        launchChangeEditTextClickListViewAndClickDialog(FLASHCARD_1.id,
                newFront, R.id.flashcardEditFront, android.R.string.no)
        onView(withId(R.id.flashcardEditFront)).check(matches(withText(newFront)))
    }

    @Test
    fun launchWithNewFlashcard_typeInCategory_pressBack_yesOnConfirmation_movesAway() {
        launchChangeEditTextPressBackAndClickYesOnDialog(AddEditFlashcardFragment.newFlashcardId,
                newCategory, R.id.flashcardEditCategory)
    }

    @Test
    fun launchWithNewFlashcard_typeInBack_pressBack_noOnConfirmation_staysOnView() {
        launchChangeEditTextPressBackAndClickNoOnDialog(AddEditFlashcardFragment.newFlashcardId,
                newBack, R.id.flashcardEditBack)
    }

    private fun checkDetailViewMatchesFlashcard(flashcard: Flashcard) {
        onView(Matchers.allOf(withId(R.id.flashcardFront), isDescendantOfA(withId(R.id.detailPager)),
                isCompletelyDisplayed()))
                .check(matches(withText(flashcard.front)))
        onView(Matchers.allOf(withId(R.id.flashcardBack), isDescendantOfA(withId(R.id.detailPager)),
                isCompletelyDisplayed()))
                .check(matches(withText(flashcard.back)))
    }

    private fun verifyEditTextsBlank() {
        onView(withId(R.id.flashcardEditCategory)).check(matches(withText("")))
        onView(withId(R.id.flashcardEditFront)).check(matches(withText("")))
        onView(withId(R.id.flashcardEditBack)).check(matches(withText("")))
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

    private fun verifyListViewShown() {
        onView(withId(R.id.detailPager)).check(matches(isDisplayed()))
    }

    private fun launchChangeEditTextClickListViewAndClickDialog(
            launchId: String, textToChange: String, editViewToChange: Int, dialogTextToClick: Int) {
        launchActivity(launchId)
        replaceEditTextText(textToChange, editViewToChange)
        clickShowListButton()
        clickOnDialog(dialogTextToClick)
    }

    private fun launchChangeEditTextPressBackAndClickYesOnDialog(
            launchId: String, textToChange: String, editViewToChange: Int) {
        launchActivity(launchId)
        replaceEditTextText(textToChange, editViewToChange)
        try {
            Espresso.pressBackUnconditionally()
            clickOnDialog(android.R.string.yes)
        } catch (expectedException: NoActivityResumedException) {
            // This is apparently the best way to verify the activity has been destroyed because of
            // a back press, which is exactly what should happen
        }
    }

    private fun launchChangeEditTextPressBackAndClickNoOnDialog(
            launchId: String, textToChange: String, editViewToChange: Int) {
        launchActivity(launchId)
        replaceEditTextText(textToChange, editViewToChange)
        Espresso.pressBack()
        clickOnDialog(android.R.string.no)
        onView(withId(editViewToChange)).check(matches(withText(textToChange)))
    }

    private fun replaceEditTextText(text: String, editTextId: Int) {
        onView(withId(editTextId)).perform(replaceText(text))
    }
}
