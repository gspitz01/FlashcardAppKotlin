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

package com.gregspitz.flashcardappkotlin.flashcardlist

import android.content.pm.ActivityInfo
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.IdlingRegistry
import android.support.test.espresso.IdlingResource
import android.support.test.espresso.ViewInteraction
import android.support.test.espresso.action.ViewActions.*
import android.support.test.espresso.assertion.ViewAssertions.doesNotExist
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.contrib.RecyclerViewActions
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.filters.MediumTest
import android.support.test.runner.AndroidJUnit4
import android.support.v4.view.ViewPager
import android.support.v7.widget.RecyclerView
import com.gregspitz.flashcardappkotlin.BaseSingleFragmentTest
import com.gregspitz.flashcardappkotlin.MockTestData.CATEGORY_1
import com.gregspitz.flashcardappkotlin.MockTestData.FLASHCARD_1
import com.gregspitz.flashcardappkotlin.MockTestData.FLASHCARD_2
import com.gregspitz.flashcardappkotlin.R
import com.gregspitz.flashcardappkotlin.R.id.detailPager
import com.gregspitz.flashcardappkotlin.TestUtils.recyclerViewScrollToAndVerifyPosition
import com.gregspitz.flashcardappkotlin.data.model.Flashcard
import org.hamcrest.Matchers.allOf
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Tests for the implementation of {@link FlashcardListFragment}
 */
@RunWith(AndroidJUnit4::class)
@MediumTest
class FlashcardListFragmentTest : BaseSingleFragmentTest() {

    private val flashcard3 = Flashcard("2", "Category2", "Front2", "Back2")
    private val flashcard4 = Flashcard("3", "Category3", "Front3", "Back3")
    private val flashcard5 = Flashcard("4", "Category4", "Front4", "Back4")
    private val flashcard6 = Flashcard("5", "Category5", "Front5", "Back5")
    private val flashcard7 = Flashcard("6", "Category6", "Front6", "Back6")
    private val flashcard8 = Flashcard("7", "Category7", "Front7", "Back7")

    @Test
    fun flashcardRecyclerView_showsFlashcardFronts() {
        addFlashcardsToDataSource(FLASHCARD_1, FLASHCARD_2)
        launchActivity()
        verifyRecyclerViewShownAndNotShown(listOf(FLASHCARD_1.category, FLASHCARD_1.front,
                FLASHCARD_2.category, FLASHCARD_2.front),
                listOf())
    }

    @Test
    fun launchWithCategoryName_showsOnlyThatCategory() {
        addFlashcardsToDataSource(FLASHCARD_1, FLASHCARD_2)
        launchActivity(categoryName = CATEGORY_1.name)
        verifyRecyclerViewShownAndNotShown(listOf(FLASHCARD_1.category, FLASHCARD_1.front),
                listOf(FLASHCARD_2.category, FLASHCARD_2.front))
    }

    @Test
    fun launchWithCategoryNameAndOrientationChange_stillShowsOnlyThatCategory() {
        addFlashcardsToDataSource(FLASHCARD_1, FLASHCARD_2)
        launchActivity(categoryName = CATEGORY_1.name)
        requestOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        verifyRecyclerViewShownAndNotShown(listOf(FLASHCARD_1.category, FLASHCARD_1.front),
                listOf(FLASHCARD_2.category, FLASHCARD_2.front))
        requestOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
        verifyRecyclerViewShownAndNotShown(listOf(FLASHCARD_1.category, FLASHCARD_1.front),
                listOf(FLASHCARD_2.category, FLASHCARD_2.front))
    }

    @Test
    fun launchWithCategory_addFABClick_showsAddEditWithCategoryFilledIn() {
        addFlashcardsToDataSource(FLASHCARD_1, FLASHCARD_2)
        launchActivity(categoryName = CATEGORY_1.name)
        verifyRecyclerViewShownAndNotShown(listOf(FLASHCARD_1.category, FLASHCARD_1.front),
                listOf(FLASHCARD_2.category, FLASHCARD_2.front))
        clickAddFab()
        onView(withId(R.id.flashcardEditCategory)).check(matches(withText(CATEGORY_1.name)))
        onView(withId(R.id.flashcardEditFront)).check(matches(withText("")))
        onView(withId(R.id.flashcardEditBack)).check(matches(withText("")))
    }

    @Test
    fun detailsFragment_showsFirstFlashcardDetails() {
        addFlashcardsToDataSource(FLASHCARD_1, FLASHCARD_2)
        launchActivity()
        checkDetailViewMatchesFlashcard(FLASHCARD_1)
    }

    @Test
    fun launchWithId_showsThatFlashcardInDetails() {
        addFlashcardsToDataSource(FLASHCARD_1, FLASHCARD_2)
        launchActivity(FLASHCARD_2.id)
        val viewPagerIdlingResource = registerViewPagerIdlingResource()
        // The idling resource seems to not work here, not sure why
        // so I am forced, against my better judgment, to do this:
        Thread.sleep(500)
        checkDetailViewMatchesFlashcard(FLASHCARD_2)
        unregisterViewPagerIdlingResource(viewPagerIdlingResource)
    }

    @Test
    fun noFlashcardsToShow_showsNoFlashcardsMessage() {
        // TODO: The functionality which is supposed to be test here works, but this test does not
        // pass because the checking for Snackbar doesn't work properly
        launchActivity()
        checkForSnackbar(R.string.no_flashcards_to_show_text)
    }

    @Test
    fun failedToLoadFlashcards_showsFailedToLoadMessage() {
        // TODO: The functionality which is supposed to be test here works, but this test does not
        // pass because the checking for Snackbar doesn't work properly
        localDataSource.setFailure(true)
        launchActivity()
        checkForSnackbar(R.string.failed_to_load_flashcard_text)
    }

    @Test
    fun clickFlashcard_showsFlashcardDetails() {
        addFlashcardsToDataSource(FLASHCARD_1, FLASHCARD_2, flashcard3, flashcard4, flashcard5,
                flashcard6, flashcard7, flashcard8)
        launchActivity()
        val viewPagerIdlingResource = registerViewPagerIdlingResource()
        clickRecyclerViewHolderWithText(flashcard8.front)
        checkDetailViewMatchesFlashcard(flashcard8)
        unregisterViewPagerIdlingResource(viewPagerIdlingResource)
    }

    @Test
    fun clickCategory_showsListOfFlashcardsOfOnlyThatCategory() {
        addFlashcardsToDataSource(FLASHCARD_1, FLASHCARD_2)
        launchActivity()
        clickRecyclerViewHolderWithText(FLASHCARD_2.category)
        verifyRecyclerViewShownAndNotShown(listOf(FLASHCARD_2.category, FLASHCARD_2.front),
                listOf(FLASHCARD_1.category, FLASHCARD_1.front))
    }

    @Test
    fun swipeLeftDetailView_showsNextFlashcard() {
        addFlashcardsToDataSource(FLASHCARD_1, FLASHCARD_2)
        launchActivity()
        val viewPagerIdlingResource = registerViewPagerIdlingResource()
        onView(withId(R.id.detailPager)).perform(swipeLeft())
        checkDetailViewMatchesFlashcard(FLASHCARD_2)
        unregisterViewPagerIdlingResource(viewPagerIdlingResource)
    }

    @Test
    fun swipeLeftThenRightDetailView_showsFirstFlashcardAgain() {
        addFlashcardsToDataSource(FLASHCARD_1, FLASHCARD_2)
        launchActivity()
        val viewPagerIdlingResource = registerViewPagerIdlingResource()
        onView(withId(R.id.detailPager))
                .perform(swipeLeft())
        onView(withId(R.id.detailPager))
                .perform(swipeRight())
        checkDetailViewMatchesFlashcard(FLASHCARD_1)
        unregisterViewPagerIdlingResource(viewPagerIdlingResource)
    }

    @Test
    fun swipingDetailView_recyclerViewFollows() {
        // These flashcards will sort to this order (because of category):
        // flashcard3, flashcard4, flashcard5, flashcard6, flashcard7, flashcard8,
        // FLASHCARD_1, FLASHCARD_2
        addFlashcardsToDataSource(FLASHCARD_1, FLASHCARD_2, flashcard3, flashcard4, flashcard5,
                flashcard6, flashcard7, flashcard8)
        launchActivity()
        val viewPagerIdlingResource = registerViewPagerIdlingResource()
        performMultipleSwipes(onView(withId(R.id.detailPager)), 6)
        checkDetailViewMatchesFlashcard(FLASHCARD_1)
        onView(allOf(isDescendantOfA(withId(R.id.flashcardRecyclerView)),
                withText(FLASHCARD_1.front))).check(matches(isDisplayed()))
        unregisterViewPagerIdlingResource(viewPagerIdlingResource)
    }

    @Test
    fun clickEditFlashcardInDetailView_showsEditFlashcardView() {
        addFlashcardsToDataSource(FLASHCARD_1, FLASHCARD_2)
        launchActivity()
        clickDetailViewEditButton()
        checkForAddEditFlashcardFragment(FLASHCARD_1.front, FLASHCARD_1.back)
    }

    @Test
    fun clickPlayWithAllFlashcardsShown_changesViewToRandomFlashcardWithAllCategories() {
        addFlashcardsToDataSource(FLASHCARD_1, FLASHCARD_2)
        launchActivity()

        clickPlayButton()

        onView(withId(R.id.categorySpinner))
                .check(matches(withSpinnerText(R.string.all_flashcards_spinner_text)))
    }

    @Test
    fun launchWithCategory_clickPlay_changesViewToRandomFlashcardWithThatCategory() {
        addFlashcardsToDataSource(FLASHCARD_1, FLASHCARD_2)
        launchActivity(categoryName = FLASHCARD_2.category)

        clickPlayButton()

        onView(withId(R.id.categorySpinner))
                .check(matches(withSpinnerText(FLASHCARD_2.category)))
    }

    @Test
    fun clickDeleteWithAllFlashcardsShown_yesOnConfirm_deletesAllFlashcards_showsDeleteSuccessMessage_changesToAddFlashcardView() {
        addFlashcardsToDataSource(FLASHCARD_1, FLASHCARD_2)
        launchActivity()

        clickDeleteButton()
        // Click yes on confirmation dialog
        clickOnDialog(android.R.string.yes)

        assertTrue(verifyFlashcardNotInRepo(FLASHCARD_1))
        assertTrue(verifyFlashcardNotInRepo(FLASHCARD_2))
        checkForSnackbar(R.string.delete_succeeded_message_text)
        // Check for add flashcard view
        onView(withId(R.id.flashcardEditCategory)).check(matches(isDisplayed()))
        onView(withId(R.id.flashcardEditFront)).check(matches(isDisplayed()))
        onView(withId(R.id.flashcardEditBack)).check(matches(isDisplayed()))
    }

    @Test
    fun clickDeleteWithAllFlashcardsShown_noOnConfirm_doesNothing() {
        addFlashcardsToDataSource(FLASHCARD_1, FLASHCARD_2)
        launchActivity()

        clickDeleteButton()
        // Click no on confirmation dialog
        clickOnDialog(android.R.string.no)

        assertTrue(verifyFlashcardInInRepo(FLASHCARD_1))
        assertTrue(verifyFlashcardInInRepo(FLASHCARD_2))
        verifyRecyclerViewShownAndNotShown(listOf(FLASHCARD_1.category,
                FLASHCARD_1.front, FLASHCARD_2.category, FLASHCARD_2.front), listOf())
    }

    @Test
    fun clickDeleteWithAllFlashcardsShown_yesOnConfirm_failureFromDataSource_showsDeleteFailedMessage() {
        // TODO: The functionality which is supposed to be test here works, but this test does not
        // pass because the checking for Snackbar doesn't work properly
        addFlashcardsToDataSource(FLASHCARD_1, FLASHCARD_2)
        localDataSource.setDeleteFailure(true)
        launchActivity()

        clickDeleteButton()
        clickOnDialog(android.R.string.yes)

        checkForSnackbar(R.string.delete_failed_message_text)
        verifyRecyclerViewShownAndNotShown(listOf(FLASHCARD_1.category,
                FLASHCARD_1.front, FLASHCARD_2.category, FLASHCARD_2.front), listOf())
        onView(withText(R.string.no_flashcards_to_show_text)).check(doesNotExist())
    }

    @Test
    fun launchWithCategory_clickDelete_yesOnConfirm_deletesThatCategory_showsDeleteSuccessMessage_changesViewToCategoryList() {
        addFlashcardsToDataSource(FLASHCARD_1, FLASHCARD_2)
        launchActivity(categoryName = FLASHCARD_1.category)

        // Only showing FLASHCARD_1
        verifyRecyclerViewShownAndNotShown(listOf(FLASHCARD_1.category, FLASHCARD_1.front),
                listOf(FLASHCARD_2.category, FLASHCARD_2.front))

        clickDeleteButton()
        clickOnDialog(android.R.string.yes)

        assertTrue(verifyFlashcardNotInRepo(FLASHCARD_1))
        assertTrue(verifyFlashcardInInRepo(FLASHCARD_2))
        checkForSnackbar(R.string.delete_succeeded_message_text)

        // Moves to category list view
        onView(withId(R.id.categoryRecyclerView)).check(matches(isDisplayed()))
    }

    @Test
    fun launchWithCategory_clickDelete_noOnConfirm_doesNothing() {
        addFlashcardsToDataSource(FLASHCARD_1, FLASHCARD_2)
        launchActivity(categoryName = FLASHCARD_1.category)

        // Only showing FLASHCARD_1
        verifyRecyclerViewShownAndNotShown(listOf(FLASHCARD_1.category, FLASHCARD_1.front),
                listOf(FLASHCARD_2.category, FLASHCARD_2.front))

        clickDeleteButton()
        clickOnDialog(android.R.string.no)

        assertTrue(verifyFlashcardInInRepo(FLASHCARD_1))
        assertTrue(verifyFlashcardInInRepo(FLASHCARD_2))
        verifyRecyclerViewShownAndNotShown(listOf(FLASHCARD_1.category, FLASHCARD_1.front),
                listOf(FLASHCARD_2.category, FLASHCARD_2.front))
    }

    @Test
    fun launchWithCategory_clickDelete_yesOnConfirm_failureFromRepo_showsDeleteFailedMessage() {
        // TODO: The functionality which is supposed to be test here works, but this test does not
        // pass because the checking for Snackbar doesn't work properly
        addFlashcardsToDataSource(FLASHCARD_1, FLASHCARD_2)
        localDataSource.setDeleteFailure(true)
        launchActivity(categoryName = FLASHCARD_1.category)

        // Only showing FLASHCARD_1
        verifyRecyclerViewShownAndNotShown(listOf(FLASHCARD_1.category, FLASHCARD_1.front),
                listOf(FLASHCARD_2.category, FLASHCARD_2.front))

        clickDeleteButton()
        clickOnDialog(android.R.string.yes)

        assertTrue(verifyFlashcardInInRepo(FLASHCARD_1))
        assertTrue(verifyFlashcardInInRepo(FLASHCARD_2))
        checkForSnackbar(R.string.delete_failed_message_text)
        verifyRecyclerViewShownAndNotShown(listOf(FLASHCARD_1.category, FLASHCARD_1.front),
                listOf(FLASHCARD_2.category, FLASHCARD_2.front))
    }

    @Test
    fun orientationChangeAndThenClickEditFlashcardInDetailView_showsEditFlashcardView() {
        addFlashcardsToDataSource(FLASHCARD_1, FLASHCARD_2)
        launchActivity()
        requestOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        requestOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
        clickDetailViewEditButton()
        checkForAddEditFlashcardFragment(FLASHCARD_1.front, FLASHCARD_1.back)
    }

    @Test
    fun detailViewSwipeThenClickEditFlashcard_showsEditFlashcardView() {
        addFlashcardsToDataSource(FLASHCARD_1, FLASHCARD_2)
        launchActivity()
        val viewPagerIdlingResource = registerViewPagerIdlingResource()
        onView(withId(R.id.detailPager)).perform(swipeLeft())
        clickDetailViewEditButton()
        checkForAddEditFlashcardFragment(FLASHCARD_2.front, FLASHCARD_2.back)
        unregisterViewPagerIdlingResource(viewPagerIdlingResource)
    }


    @Test
    fun clickAddFlashcardFab_showsAddEditFlashcardView() {
        addFlashcardsToDataSource(FLASHCARD_1, FLASHCARD_2)
        launchActivity()
        clickAddFab()
        checkForAddEditFlashcardFragment("", "")
    }

    private fun checkForAddEditFlashcardFragment(front: String, back: String) {
        onView(withId(R.id.saveFlashcardButton)).check(matches(isDisplayed()))
        onView(withId(R.id.flashcardEditFront)).check(matches(withText(front)))
        onView(withId(R.id.flashcardEditBack)).check(matches(withText(back)))
    }

    private fun performMultipleSwipes(view: ViewInteraction?, number: Int) {
        for (i in 1..number) {
            view?.perform(swipeLeft())
        }
    }

    private fun clickDetailViewEditButton() {
        onView(withId(R.id.editFlashcardButton)).perform(click())
    }

    private fun clickDeleteButton() {
        onView(withId(R.id.deleteFlashcardsButton)).perform(click())
    }

    private fun clickPlayButton() {
        onView(withId(R.id.playButton)).perform(click())
    }

    private fun clickAddFab() {
        onView(withId(R.id.addFlashcardFab)).perform(click())
    }

    private fun clickRecyclerViewHolderWithText(text: String) {
        onView(withId(R.id.flashcardRecyclerView))
                .perform(RecyclerViewActions
                        .scrollTo<RecyclerView.ViewHolder>(hasDescendant(
                                withText(text))))
                .perform(RecyclerViewActions
                        .actionOnItem<RecyclerView.ViewHolder>(
                                hasDescendant(withText(text)),
                                click()))
    }

    private fun verifyRecyclerViewShownAndNotShown(shownTexts: List<String>,
                                                   notShownTexts: List<String>) {
        for ((index, text) in shownTexts.withIndex()) {
            recyclerViewScrollToAndVerifyPosition(R.id.flashcardRecyclerView, index,
                    text)
        }
        for (text in notShownTexts) {
            onView(withText(text)).check(doesNotExist())
        }
    }

    private fun checkDetailViewMatchesFlashcard(flashcard: Flashcard) {
        onView(allOf(withId(R.id.flashcardCategory), isDescendantOfA(withId(R.id.detailPager)),
                isCompletelyDisplayed()))
                .check(matches(withText(flashcard.category)))
        onView(allOf(withId(R.id.flashcardFront), isDescendantOfA(withId(R.id.detailPager)),
                isCompletelyDisplayed()))
                .check(matches(withText(flashcard.front)))
        onView(allOf(withId(R.id.flashcardBack), isDescendantOfA(withId(R.id.detailPager)),
                isCompletelyDisplayed()))
                .check(matches(withText(flashcard.back)))
    }

    private fun registerViewPagerIdlingResource(): IdlingResource {
        // Force Espresso to wait until fragment is loaded.
        onView(withId(R.id.flashcardRecyclerView)).check(matches(isDisplayed()))
        val detailPager = testRule.activity.supportFragmentManager
                .fragments[0].view!!.findViewById<ViewPager>(detailPager)
        val viewPagerIdlingResource = ViewPagerIdlingResource(detailPager, "PagerIdlingResource")
        IdlingRegistry.getInstance().register(viewPagerIdlingResource)
        return viewPagerIdlingResource
    }

    private fun unregisterViewPagerIdlingResource(idlingResource: IdlingResource) {
        IdlingRegistry.getInstance().unregister(idlingResource)
    }

    private fun launchActivity(flashcardId: String =
                                       FlashcardListFragment.noParticularFlashcardExtra,
                               categoryName: String? = null) {
        launchActivity(FlashcardListFragment.newInstance(flashcardId, categoryName))
    }
}
