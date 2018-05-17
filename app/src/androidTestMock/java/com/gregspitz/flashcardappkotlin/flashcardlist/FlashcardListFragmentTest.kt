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
import android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import android.support.test.espresso.matcher.BoundedMatcher
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.support.v4.view.ViewPager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.gregspitz.flashcardappkotlin.FlashcardApplication
import com.gregspitz.flashcardappkotlin.R
import com.gregspitz.flashcardappkotlin.R.id.detailContent
import com.gregspitz.flashcardappkotlin.SingleFragmentActivity
import com.gregspitz.flashcardappkotlin.data.model.Flashcard
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

    private val flashcard1 = Flashcard("0", "A front", "A back")

    private val flashcard2 = Flashcard("1", "A different front", "A different back")

    private val dataSource = FlashcardApplication.repoComponent.exposeLocalDataSource()

    @Rule @JvmField
    val testRule = ActivityTestRule<SingleFragmentActivity>(
            SingleFragmentActivity::class.java, true, false)

    @Before
    fun setup() {
        dataSource.deleteAllFlashcards()
    }

    @Test
    fun flashcardRecyclerView_showsFlashcardFronts() {
        addFlashcardsToDataSource(flashcard1, flashcard2)
        launchActivity()
        onView(withId(R.id.flashcardRecyclerView))
                .check(matches(hasFlashcardFrontForPosition(0, flashcard1)))
        onView(withId(R.id.flashcardRecyclerView))
                .check(matches(hasFlashcardFrontForPosition(1, flashcard2)))
        onView(withId(R.id.flashcardListMessages)).check(matches(not(isDisplayed())))
    }

    @Test
    fun detailsFragment_showsFirstFlashcardDetails() {
        addFlashcardsToDataSource(flashcard1, flashcard2)
        launchActivity()
        checkDetailViewMatchesFlashcard(flashcard1)
    }

    @Test
    fun launchWithId_showsThatFlashcardInDetails() {
        addFlashcardsToDataSource(flashcard1, flashcard2)
        launchActivity(flashcard2.id)
        val viewPagerIdlingResource = registerViewPagerIdlingResource()
        // The idling resource seems to not work here, not sure why
        // so I am forced, against my better judgment, to do this:
        Thread.sleep(500)
        checkDetailViewMatchesFlashcard(flashcard2)
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
        dataSource.setFailure(true)
        launchActivity()
        onView(withId(R.id.flashcardListMessages))
                .check(matches(withText(R.string.failed_to_load_flashcard_text)))
    }

    @Test
    fun clickFlashcard_showsFlashcardDetails() {
        addFlashcardsToDataSource(flashcard1, flashcard2)
        launchActivity()
        onView(withId(R.id.flashcardRecyclerView))
                .perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(1, click()))
        checkDetailViewMatchesFlashcard(flashcard2)
    }

    @Test
    fun swipeLeftDetailView_showsNextFlashcard() {
        addFlashcardsToDataSource(flashcard1, flashcard2)
        launchActivity()
        val viewPagerIdlingResource = registerViewPagerIdlingResource()
        onView(withId(R.id.detailContent)).perform(swipeLeft())
        checkDetailViewMatchesFlashcard(flashcard2)
        unregisterViewPagerIdlingResource(viewPagerIdlingResource)
    }

    @Test
    fun swipeLeftThenRightDetailView_showsFirstFlashcardAgain() {
        addFlashcardsToDataSource(flashcard1, flashcard2)
        launchActivity()
        val viewPagerIdlingResource = registerViewPagerIdlingResource()
        onView(withId(R.id.detailContent))
                .perform(swipeLeft())
        onView(withId(R.id.detailContent))
                .perform(swipeRight())
        checkDetailViewMatchesFlashcard(flashcard1)
        unregisterViewPagerIdlingResource(viewPagerIdlingResource)
    }

    @Test
    fun swipingDetailView_recyclerViewFollows() {
        val flashcard3 = Flashcard("2", "Front2", "Back2")
        val flashcard4 = Flashcard("3", "Front3", "Back3")
        val flashcard5 = Flashcard("4", "Front4", "Back4")
        val flashcard6 = Flashcard("5", "Front5", "Back5")
        val flashcard7 = Flashcard("6", "Front6", "Back6")
        val flashcard8 = Flashcard("7", "Front7", "Back7")
        addFlashcardsToDataSource(flashcard1, flashcard2, flashcard3, flashcard4, flashcard5,
                flashcard6, flashcard7, flashcard8)
        launchActivity()
        val viewPagerIdlingResource = registerViewPagerIdlingResource()
        performMultipleSwipes(onView(withId(R.id.detailContent)), 6)
        checkDetailViewMatchesFlashcard(flashcard7)
        onView(allOf(isDescendantOfA(withId(R.id.flashcardRecyclerView)),
                withText(flashcard7.front))).check(matches(isDisplayed()))
        unregisterViewPagerIdlingResource(viewPagerIdlingResource)
    }

    @Test
    fun clickEditFlashcardInDetailView_showsEditFlashcardView() {
        addFlashcardsToDataSource(flashcard1, flashcard2)
        launchActivity()
        clickDetailViewEditButton()
        checkForAddEditFlashcardFragment(flashcard1.front, flashcard1.back)
    }

    @Test
    fun orientationChangeAndThenClickEditFlashcardInDetailView_showsEditFlashcardView() {
        addFlashcardsToDataSource(flashcard1, flashcard2)
        launchActivity()
        testRule.activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        testRule.activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        clickDetailViewEditButton()
        checkForAddEditFlashcardFragment(flashcard1.front, flashcard1.back)
    }

    @Test
    fun detailViewSwipeThenClickEditFlashcard_showsEditFlashcardView() {
        addFlashcardsToDataSource(flashcard1, flashcard2)
        launchActivity()
        val viewPagerIdlingResource = registerViewPagerIdlingResource()
        onView(withId(R.id.detailContent)).perform(swipeLeft())
        clickDetailViewEditButton()
        checkForAddEditFlashcardFragment(flashcard2.front, flashcard2.back)
        unregisterViewPagerIdlingResource(viewPagerIdlingResource)
    }


    @Test
    fun clickAddFlashcardFab_showsAddEditFlashcardView() {
        addFlashcardsToDataSource(flashcard1, flashcard2)
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
        onView(allOf(withId(R.id.editFlashcardButton), isDescendantOfA(withId(R.id.detailContent)),
                isCompletelyDisplayed())).perform(click())
    }

    private fun checkDetailViewMatchesFlashcard(flashcard: Flashcard) {
        onView(allOf(withId(R.id.flashcardFront), isDescendantOfA(withId(R.id.detailContent)),
                isCompletelyDisplayed()))
                .check(matches(withText(flashcard.front)))
        onView(allOf(withId(R.id.flashcardBack), isDescendantOfA(withId(R.id.detailContent)),
                isCompletelyDisplayed()))
                .check(matches(withText(flashcard.back)))
    }

    private fun registerViewPagerIdlingResource() : IdlingResource {
        // Force Espresso to wait until fragment is loaded.
        onView(withId(R.id.flashcardRecyclerView)).check(matches(isDisplayed()))
        val detailPager = testRule.activity.supportFragmentManager
                .fragments[0].view!!.findViewById<ViewPager>(detailContent)
        val viewPagerIdlingResource = ViewPagerIdlingResource(detailPager, "PagerIdlingResource")
        IdlingRegistry.getInstance().register(viewPagerIdlingResource)
        return viewPagerIdlingResource
    }

    private fun unregisterViewPagerIdlingResource(idlingResource: IdlingResource) {
        IdlingRegistry.getInstance().unregister(idlingResource)
    }

    private fun addFlashcardsToDataSource(vararg flashcards: Flashcard) {
        dataSource.addFlashcards(*flashcards)
    }


    private fun launchActivity(flashcardId: String =
                                       FlashcardListFragment.noParticularFlashcardExtra) {
        testRule.launchActivity(Intent())
        testRule.activity.setFragment(FlashcardListFragment.newInstance(flashcardId))
    }

    private fun hasFlashcardFrontForPosition(
            position: Int, flashcard: Flashcard): Matcher<in View>? =
        object : BoundedMatcher<View, RecyclerView>(RecyclerView::class.java) {
            override fun matchesSafely(item: RecyclerView?): Boolean {
                if (item == null) {
                    return false
                }

                val holder = item.findViewHolderForAdapterPosition(position)

                return holder != null &&
                        withChild(withText(flashcard.front)).matches(holder.itemView)
            }

            override fun describeTo(description: Description?) {
                description?.appendText("Item has flashcard data at position $position")
            }
        }

}
