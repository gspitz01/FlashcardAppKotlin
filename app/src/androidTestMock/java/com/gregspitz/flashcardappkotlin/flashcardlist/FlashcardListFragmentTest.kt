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

import android.content.Intent
import android.content.pm.ActivityInfo
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.IdlingRegistry
import android.support.test.espresso.IdlingResource
import android.support.test.espresso.ViewInteraction
import android.support.test.espresso.action.ViewActions.*
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.contrib.RecyclerViewActions
import android.support.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import android.support.test.espresso.matcher.BoundedMatcher
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.support.v4.view.ViewPager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.gregspitz.flashcardappkotlin.FlashcardApplication
import com.gregspitz.flashcardappkotlin.MockTestData.FLASHCARD_1
import com.gregspitz.flashcardappkotlin.MockTestData.FLASHCARD_2
import com.gregspitz.flashcardappkotlin.R
import com.gregspitz.flashcardappkotlin.R.id.detailPager
import com.gregspitz.flashcardappkotlin.SingleFragmentActivity
import com.gregspitz.flashcardappkotlin.data.model.Flashcard
import com.gregspitz.flashcardappkotlin.data.source.FlashcardDataSource
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Tests for the implementation of {@link FlashcardListFragment}
 */
@RunWith(AndroidJUnit4::class)
class FlashcardListFragmentTest {

    private val flashcard3 = Flashcard("2", "Category2", "Front2", "Back2")
    private val flashcard4 = Flashcard("3", "Category3", "Front3", "Back3")
    private val flashcard5 = Flashcard("4", "Category4", "Front4", "Back4")
    private val flashcard6 = Flashcard("5", "Category5", "Front5", "Back5")
    private val flashcard7 = Flashcard("6", "Category6", "Front6", "Back6")
    private val flashcard8 = Flashcard("7", "Category7", "Front7", "Back7")

    private val dataSource = FlashcardApplication.repoComponent.exposeRepository()
    private val localDataSource =
            FlashcardApplication.repoComponent.exposeLocalDataSource()

    @Rule @JvmField
    val testRule = ActivityTestRule<SingleFragmentActivity>(
            SingleFragmentActivity::class.java, true, false)

    @Before
    fun setup() {
        dataSource.deleteAllFlashcards()
    }

    @Test
    fun flashcardRecyclerView_showsFlashcardFronts() {
        addFlashcardsToDataSource(FLASHCARD_1, FLASHCARD_2)
        launchActivity()
        scrollToAndVerifyPosition(0, FLASHCARD_1.category)
        scrollToAndVerifyPosition(1, FLASHCARD_1.front)
        scrollToAndVerifyPosition(2, FLASHCARD_2.category)
        scrollToAndVerifyPosition(3, FLASHCARD_2.front)
        onView(withId(R.id.flashcardListMessages)).check(matches(not(isDisplayed())))
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
        launchActivity()
        onView(withId(R.id.flashcardListMessages))
                .check(matches(withText(R.string.no_flashcards_to_show_text)))
    }

    @Test
    fun failedToLoadFlashcards_showsFailedToLoadMessage() {
        localDataSource.setFailure(true)
        launchActivity()
        onView(withId(R.id.flashcardListMessages))
                .check(matches(withText(R.string.failed_to_load_flashcard_text)))
    }

    @Test
    fun clickFlashcard_showsFlashcardDetails() {
        addFlashcardsToDataSource(FLASHCARD_1, FLASHCARD_2, flashcard3, flashcard4, flashcard5,
                flashcard6, flashcard7, flashcard8)
        launchActivity()
        val viewPagerIdlingResource = registerViewPagerIdlingResource()
        onView(withId(R.id.flashcardRecyclerView))
                .perform(RecyclerViewActions
                        .scrollTo<RecyclerView.ViewHolder>(hasDescendant(
                                withText(flashcard8.front))))
                .perform(RecyclerViewActions
                        .actionOnItem<RecyclerView.ViewHolder>(
                                hasDescendant(withText(flashcard8.front)),
                                click()))
        checkDetailViewMatchesFlashcard(flashcard8)
        unregisterViewPagerIdlingResource(viewPagerIdlingResource)
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
        addFlashcardsToDataSource(FLASHCARD_1, FLASHCARD_2, flashcard3, flashcard4, flashcard5,
                flashcard6, flashcard7, flashcard8)
        launchActivity()
        val viewPagerIdlingResource = registerViewPagerIdlingResource()
        performMultipleSwipes(onView(withId(R.id.detailPager)), 6)
        checkDetailViewMatchesFlashcard(flashcard7)
        onView(allOf(isDescendantOfA(withId(R.id.flashcardRecyclerView)),
                withText(flashcard7.front))).check(matches(isDisplayed()))
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
    fun orientationChangeAndThenClickEditFlashcardInDetailView_showsEditFlashcardView() {
        addFlashcardsToDataSource(FLASHCARD_1, FLASHCARD_2)
        launchActivity()
        testRule.activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        testRule.activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
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
        onView(withId(R.id.addFlashcardFab)).perform(click())
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

    private fun registerViewPagerIdlingResource() : IdlingResource {
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

    private fun addFlashcardsToDataSource(vararg flashcards: Flashcard) {
        for (flashcard in flashcards) {
            dataSource.saveFlashcard(flashcard, object: FlashcardDataSource.SaveFlashcardCallback {
                override fun onSaveSuccessful() { /* ignore */ }

                override fun onSaveFailed() { /* ignore */ }
            })
        }
    }


    private fun launchActivity(flashcardId: String =
                                       FlashcardListFragment.noParticularFlashcardExtra) {
        testRule.launchActivity(Intent())
        testRule.activity.setFragment(FlashcardListFragment.newInstance(flashcardId))
    }

    private fun scrollToAndVerifyPosition(position: Int, text: String) {
        onView(withId(R.id.flashcardRecyclerView))
                .perform(scrollToPosition<RecyclerView.ViewHolder>(position))
                .check(matches(hasFlashcardTextForPosition(position, text)))
    }

    private fun hasFlashcardTextForPosition(position: Int, text: String)
            : Matcher<in View>? =
            object : BoundedMatcher<View, RecyclerView>(RecyclerView::class.java) {
                override fun describeTo(description: Description?) {
                    description?.appendText("Item has flashcard category at position $position")
                }

                override fun matchesSafely(item: RecyclerView?): Boolean {
                    if (item == null) {
                        return false
                    }

                    val holder = item.findViewHolderForAdapterPosition(position)
                    return holder != null &&
                            withChild(withText(text)).matches(holder.itemView)
                }

            }

}
