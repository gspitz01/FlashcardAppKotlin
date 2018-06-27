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

package com.gregspitz.flashcardappkotlin

import android.content.pm.ActivityInfo
import android.support.test.espresso.Espresso
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.Espresso.pressBack
import android.support.test.espresso.action.ViewActions.*
import android.support.test.espresso.assertion.ViewAssertions.doesNotExist
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.contrib.NavigationViewActions.navigateTo
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.filters.LargeTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.support.v7.widget.RecyclerView
import com.gregspitz.flashcardappkotlin.TestData.FLASHCARD_1
import com.gregspitz.flashcardappkotlin.TestData.FLASHCARD_2
import com.gregspitz.flashcardappkotlin.data.model.Flashcard
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.allOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Large test for whole app
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class MainScreenTest {

    // TODO: fixup UI
    // TODO: eventually create test for Google Sign-In using Firebase Test Lab

    private val database = FlashcardApplication.repoComponent.exposeRepository()

    @Rule @JvmField
    val testRule = ActivityTestRule<MainActivity>(MainActivity::class.java)

    @Before
    fun setup() {
        database.deleteAllFlashcards()
    }

    @Test
    fun navigateThroughWholeAppExceptDownloadView() {
        // App starts with add/edit view
        verifyAddEditViewVisible()

        navigateToGameViewViaNavDrawer()
        verifyGameViewVisible()

        navigateToFlashcardListViaNavDrawer()
        verifyFlashcardListViewVisible()

        navigateToAddEditViaNavDrawer()
        verifyAddEditViewVisible()

        navigateToCategoryListViaNavDrawer()
        verifyCategoryListViewVisible()
    }

    @Test
    fun orientationChangeOnAddEditViewWithPreviouslySaveFlashcard_maintainsCurrentViewWithFlashcardData() {
        // Initial orientation portrait
        requestOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        // Save the flashcard
        saveFlashcard(FLASHCARD_1)
        // Move away from edit view
        navigateToFlashcardListViaNavDrawer()
        // Move back to edit view of that flashcard
        clickOnEditButtonInListView()

        // Check flashcard data shows in EditTexts
        onView(withId(R.id.flashcardEditCategory))
                .check(matches(withText(FLASHCARD_1.category)))
        onView(withId(R.id.flashcardEditFront))
                .check(matches(withText(FLASHCARD_1.front)))
        onView(withId(R.id.flashcardEditBack))
                .check(matches(withText(FLASHCARD_1.back)))

        // Change orientation
        requestOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)

        // Verify same flashcard data still in EditTexts
        onView(withId(R.id.flashcardEditCategory))
                .check(matches(withText(FLASHCARD_1.category)))
        onView(withId(R.id.flashcardEditFront))
                .check(matches(withText(FLASHCARD_1.front)))
        onView(withId(R.id.flashcardEditBack))
                .check(matches(withText(FLASHCARD_1.back)))
    }

    @Test
    fun orientationChangeOnListView_maintainsSameDetail() {
        // Initial orientation portrait
        requestOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)

        // Save Flashcard data
        saveFlashcard(FLASHCARD_1)
        saveFlashcard(FLASHCARD_2)

        navigateToFlashcardListViaNavDrawer()

        // Click first for focus
        onView(withId(R.id.detailPager)).perform(click())
        onView(withId(R.id.detailPager)).perform(swipeLeft())

        // Verify flashcard data shown in detail view
        onView(allOf(isDescendantOfA(withId(R.id.flashcardDetailContent)),
                withText(FLASHCARD_2.back))).check(matches(isDisplayed()))

        // Change orientation
        requestOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)

        // Verify same flashcard data shown in detail view
        onView(allOf(isDescendantOfA(withId(R.id.flashcardDetailContent)),
                withText(FLASHCARD_2.back))).check(matches(isDisplayed()))
    }

    @Test
    fun clickCategoryInCategoryList_showsFlashcardListWithJustFlashcardsFromThatCategory() {
        // Save flashcards
        saveFlashcard(FLASHCARD_1)
        saveFlashcard(FLASHCARD_2)

        navigateToCategoryListViaNavDrawer()
        verifyCategoryListViewVisible()

        onView(withText(FLASHCARD_1.category)).perform(click())

        verifyFlashcardListViewVisible()
        onView(allOf(isDescendantOfA(withClassName(`is`(RecyclerView::class.java.name))),
                withText(FLASHCARD_1.front))).check(matches(isDisplayed()))
        onView(allOf(isDescendantOfA(withClassName(`is`(RecyclerView::class.java.name))),
                withText(FLASHCARD_1.category))).check(matches(isDisplayed()))
        onView(withText(FLASHCARD_2.category)).check(doesNotExist())
        onView(withText(FLASHCARD_2.front)).check(doesNotExist())
    }

    @Test
    fun backFunction_worksProperly() {
        // Save flashcard data
        saveFlashcard(FLASHCARD_1)
        saveFlashcard(FLASHCARD_2)

        // Navigate back to game view after save
        navigateToGameViewViaNavDrawer()
        verifyGameViewVisible()

        // Move to List view
        navigateToFlashcardListViaNavDrawer()
        verifyFlashcardListViewVisible()

        // Press back
        pressBack()
        // Should be back at game view
        verifyGameViewVisible()

        // Move to List view
        navigateToFlashcardListViaNavDrawer()
        verifyFlashcardListViewVisible()

        // Move to Add/Edit view
        clickOnEditButtonInListView()
        verifyAddEditViewVisible()

        // One of the EditTexts gets focus, so close keyboard before pressing back
        Espresso.closeSoftKeyboard()
        pressBack()
        // Should be back at List view
        verifyFlashcardListViewVisible()

        pressBack()
        // Should be back at game view
        verifyGameViewVisible()

        // Move to Add/Edit view
        navigateToAddEditViaNavDrawer()
        verifyAddEditViewVisible()

        pressBack()
        // Should be back at game view
        verifyGameViewVisible()
    }

    private fun saveFlashcard(newFlashcard: Flashcard) {
        navigateToAddEditViaNavDrawer()
        onView(withId(R.id.flashcardEditCategory)).perform(typeText(newFlashcard.category))
        onView(withId(R.id.flashcardEditFront)).perform(typeText(newFlashcard.front))
        onView(withId(R.id.flashcardEditBack)).perform(typeText(newFlashcard.back))
        onView(withId(R.id.saveFlashcardButton)).perform(click())
        Espresso.closeSoftKeyboard()
    }

    private fun requestOrientation(orientation: Int) {
        testRule.activity.requestedOrientation = orientation
    }

    private fun verifyGameViewVisible() {
        onView(withId(R.id.nextFlashcardButton)).check(matches(isDisplayed()))
    }

    private fun verifyFlashcardListViewVisible() {
        onView(withId(R.id.flashcardRecyclerView)).check(matches(isDisplayed()))
    }

    private fun verifyAddEditViewVisible() {
        onView(withId(R.id.flashcardEditCategory)).check(matches(isDisplayed()))
    }

    private fun verifyCategoryListViewVisible() {
        onView(withId(R.id.categoryRecyclerView)).check(matches(isDisplayed()))
    }

    private fun clickOnEditButtonInListView() {
        onView(withId(R.id.editFlashcardButton)).perform(click())
    }

    private fun navigateToFlashcardListViaNavDrawer() {
        onView(withContentDescription(R.string.abc_action_bar_up_description)).perform(click())
        onView(withId(R.id.navDrawer)).perform(navigateTo(R.id.navList))
    }

    private fun navigateToCategoryListViaNavDrawer() {
        onView(withContentDescription(R.string.abc_action_bar_up_description)).perform(click())
        onView(withId(R.id.navDrawer)).perform(navigateTo(R.id.navCategoryList))
    }

    private fun navigateToAddEditViaNavDrawer() {
        onView(withContentDescription(R.string.abc_action_bar_up_description)).perform(click())
        onView(withId(R.id.navDrawer)).perform(navigateTo(R.id.newFlashcard))
    }

    private fun navigateToGameViewViaNavDrawer() {
        onView(withContentDescription(R.string.abc_action_bar_up_description)).perform(click())
        onView(withId(R.id.navDrawer)).perform(navigateTo(R.id.navGame))
    }
}
