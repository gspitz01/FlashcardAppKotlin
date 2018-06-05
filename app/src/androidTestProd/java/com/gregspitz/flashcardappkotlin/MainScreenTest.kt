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
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.Espresso.pressBack
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.action.ViewActions.doubleClick
import android.support.test.espresso.assertion.ViewAssertions.doesNotExist
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.contrib.DrawerActions
import android.support.test.espresso.contrib.NavigationViewActions.navigateTo
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.filters.LargeTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.widget.ScrollView
import org.hamcrest.CoreMatchers.*
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

    @Rule @JvmField
    val testRule = ActivityTestRule<MainActivity>(MainActivity::class.java)

    @Test
    fun moveThroughWholeApp() {
        verifyGameViewVisible()
        navigateToListViaNavDrawer()
        verifyListViewVisible()
        onView(withId(R.id.drawerLayout)).perform(DrawerActions.open())
        onView(withId(R.id.navDrawer)).perform(navigateTo(R.id.newFlashcard))
        verifyAddEditViewVisible()
    }

    @Test
    fun orientationChangeOnAddEditView_maintainsCurrentViewWithFlashcardData() {
        testRule.activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        navigateToListViaNavDrawer()
        clickOnEditButtonOfFirstInDetailPager()

        onView(withId(R.id.flashcardEditCategory))
                .check(matches(withText(InitialData.flashcards[0].category)))
        onView(withId(R.id.flashcardEditFront))
                .check(matches(withText(InitialData.flashcards[0].front)))
        onView(withId(R.id.flashcardEditBack))
                .check(matches(withText(InitialData.flashcards[0].back)))

        testRule.activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        onView(withId(R.id.flashcardEditCategory))
                .check(matches(withText(InitialData.flashcards[0].category)))
        onView(withId(R.id.flashcardEditFront))
                .check(matches(withText(InitialData.flashcards[0].front)))
        onView(withId(R.id.flashcardEditBack))
                .check(matches(withText(InitialData.flashcards[0].back)))
        onView(withId(R.id.nextFlashcardButton)).check(doesNotExist())
    }

    @Test
    fun backFunction_worksProperly() {
        // Start on game view
        verifyGameViewVisible()

        // Move to List view
        navigateToListViaNavDrawer()
        verifyListViewVisible()

        // Press back
        pressBack()
        // Should be back at game view
        verifyGameViewVisible()

        // Move to List view
        navigateToListViaNavDrawer()
        verifyListViewVisible()

        // Move to Add/Edit view
        clickOnEditButtonOfFirstInDetailPager()
        verifyAddEditViewVisible()

        pressBack()
        // Should be back at List view
        verifyListViewVisible()

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

    private fun verifyGameViewVisible() {
        onView(withId(R.id.nextFlashcardButton)).check(matches(isDisplayed()))
    }

    private fun verifyListViewVisible() {
        onView(withId(R.id.flashcardRecyclerView)).check(matches(isDisplayed()))
    }

    private fun verifyAddEditViewVisible() {
        onView(withId(R.id.flashcardEditCategory)).check(matches(isDisplayed()))
    }

    private fun clickOnEditButtonOfFirstInDetailPager() {
        onView(allOf(isDescendantOfA(allOf(hasDescendant(withText(InitialData.flashcards[0].front)),
                instanceOf(ScrollView::class.java))),
                withId(R.id.editFlashcardButton))).perform(doubleClick())
    }

    private fun navigateToListViaNavDrawer() {
        onView(withContentDescription(R.string.abc_action_bar_up_description)).perform(click())
        onView(withId(R.id.navDrawer)).perform(navigateTo(R.id.navList))
    }

    private fun navigateToAddEditViaNavDrawer() {
        onView(withContentDescription(R.string.abc_action_bar_up_description)).perform(click())
        onView(withId(R.id.navDrawer)).perform(navigateTo(R.id.newFlashcard))
    }
}
