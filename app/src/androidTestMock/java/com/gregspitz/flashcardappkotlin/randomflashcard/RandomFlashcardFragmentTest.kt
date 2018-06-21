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

package com.gregspitz.flashcardappkotlin.randomflashcard

import android.content.Intent
import android.content.pm.ActivityInfo
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.BoundedMatcher
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.espresso.matcher.ViewMatchers.withText
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.view.View
import android.widget.TextView
import com.gregspitz.flashcardappkotlin.FlashcardApplication
import com.gregspitz.flashcardappkotlin.MockTestData.FLASHCARD_1
import com.gregspitz.flashcardappkotlin.MockTestData.FLASHCARD_2
import com.gregspitz.flashcardappkotlin.R
import com.gregspitz.flashcardappkotlin.SingleFragmentActivity
import com.gregspitz.flashcardappkotlin.data.model.Flashcard
import com.gregspitz.flashcardappkotlin.data.model.FlashcardPriority
import com.gregspitz.flashcardappkotlin.data.source.FlashcardDataSource
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Tests for the implementation of {@link RandomFlashcardFragment}
 */
@RunWith(AndroidJUnit4::class)
class RandomFlashcardFragmentTest {

    private var firstText = ""
    private var firstCategory = ""

    private val dataSource = FlashcardApplication.repoComponent.exposeRepository()

    @Rule @JvmField
    val testRule = ActivityTestRule<SingleFragmentActivity>(
            SingleFragmentActivity::class.java, true, false)

    @Before
    fun setup() {
        dataSource.deleteAllFlashcards()
    }

    @Test
    fun atStart_showsFrontOfOneFlashcardAndClickTurnsFlashcard() {
        addFlashcardsToDataSource(FLASHCARD_1)

        launchActivity()

        onView(withId(R.id.flashcardCategory)).check(matches(withText(FLASHCARD_1.category)))
        onView(withId(R.id.flashcardSide))
                .check(matches(withText(FLASHCARD_1.front)))
                .perform(click())
                .check(matches(withText(FLASHCARD_1.back)))
    }

    @Test
    fun noFlashcardsToLoad_showsNoFlashcards() {
        launchActivity()

        onView(withId(R.id.flashcardSide))
                .check(matches(withText(R.string.failed_to_load_flashcard_text)))
    }

    @Test
    fun nextFlashcardButtonClick_loadsDifferentFlashcard() {
        addFlashcardsToDataSource(FLASHCARD_1, FLASHCARD_2)
        launchActivity()

        verifyFirstCategoryAndText(FLASHCARD_1, FLASHCARD_2)
        onView(withId(R.id.nextFlashcardButton)).perform(click())
        val secondText = getSecondText(FLASHCARD_1.front, FLASHCARD_2.front)
        onView(withId(R.id.flashcardSide)).check(matches(withText(secondText)))
        val secondCategory = getSecondCategory(FLASHCARD_1.category, FLASHCARD_2.category)
        onView(withId(R.id.flashcardCategory)).check(matches(withText(secondCategory)))
    }

    @Test
    fun onScreenRotation_maintainsSameFlashcard() {
        // Make sure emulator or device currently allows screen rotation for this test to work
        addFlashcardsToDataSource(FLASHCARD_1, FLASHCARD_2)
        launchActivity()

        testRule.activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        verifyFirstCategoryAndText(FLASHCARD_1, FLASHCARD_2)
        testRule.activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        onView(withId(R.id.flashcardCategory)).check(matches(withText(firstCategory)))
        onView(withId(R.id.flashcardSide)).check(matches(withText(firstText)))
    }

    @Test
    fun ifOnlyOneFlashcard_newFlashcardButtonClickLoadsSameFlashcard() {
        addFlashcardsToDataSource(FLASHCARD_1)
        launchActivity()

        onView(withId(R.id.flashcardCategory)).check(matches(withText(FLASHCARD_1.category)))
        onView(withId(R.id.flashcardSide)).check(matches(withText(FLASHCARD_1.front)))
        onView(withId(R.id.nextFlashcardButton)).perform(click())
        onView(withId(R.id.flashcardCategory)).check(matches(withText(FLASHCARD_1.category)))
        onView(withId(R.id.flashcardSide)).check(matches(withText(FLASHCARD_1.front)))
    }

    @Test
    fun clickLowPriorityButton_savesFlashcardWithLowPriority() {
        verifyPriorityButtonClickSavesWithThatPriority(R.id.flashcardPriorityLowButton,
                FlashcardPriority.LOW)
    }

    @Test
    fun clickMediumPriorityButton_savesFlashcardWithLowPriority() {
        verifyPriorityButtonClickSavesWithThatPriority(R.id.flashcardPriorityMediumButton,
                FlashcardPriority.MEDIUM)
    }

    @Test
    fun clickHighPriorityButton_savesFlashcardWithLowPriority() {
        verifyPriorityButtonClickSavesWithThatPriority(R.id.flashcardPriorityHighButton,
                FlashcardPriority.HIGH)
    }

    @Test
    fun clickUrgentPriorityButton_savesFlashcardWithLowPriority() {
        verifyPriorityButtonClickSavesWithThatPriority(R.id.flashcardPriorityUrgentButton,
                FlashcardPriority.URGENT)
    }

    private fun verifyFirstCategoryAndText(flashcard1: Flashcard, flashcard2: Flashcard) {
        onView(withId(R.id.flashcardCategory))
                .check(matches(withCategoryOfOneOf(flashcard1.category, flashcard2.category)))
        onView(withId(R.id.flashcardSide))
                .check(matches(withTextOfOneOf(flashcard1.front, flashcard2.front)))
    }

    private fun getSecondText(text1: String, text2: String): String {
        if (firstText == text1) {
            return text2
        } else if (firstText == text2) {
            return text1
        }
        throw IllegalStateException("First text did not equal one of these:\n$text1\n$text2")
    }

    private fun getSecondCategory(category1: String, category2: String): String {
        if (firstCategory == category1) {
            return category2
        } else if (firstCategory == category2) {
            return category1
        }
        throw IllegalStateException(
                "First category did not equal one of these:\n$category1\n$category2")
    }

    private fun withCategoryOfOneOf(category1: String, category2: String): Matcher<in View>? {
        return object: BoundedMatcher<View, TextView>(TextView::class.java) {
            override fun describeTo(description: Description?) {
                description?.appendText("with possible text: $category1 -- or -- $category2")
            }

            override fun matchesSafely(item: TextView?): Boolean {
                firstCategory = item?.text.toString()
                return item?.text == category1 || item?.text == category2
            }

        }
    }

    private fun withTextOfOneOf(text1: String, text2: String): Matcher<in View>? {
        return object: BoundedMatcher<View, TextView>(TextView::class.java) {
            override fun describeTo(description: Description?) {
                description?.appendText("with possible text: $text1 -- or -- $text2")
            }

            override fun matchesSafely(item: TextView?): Boolean {
                firstText = item?.text.toString()
                return item?.text == text1 || item?.text == text2
            }

        }
    }

    private fun addFlashcardsToDataSource(vararg flashcards: Flashcard) {
        for (flashcard in flashcards) {
            dataSource.saveFlashcard(flashcard, object : FlashcardDataSource.SaveFlashcardCallback {
                override fun onSaveSuccessful() { /* ignore */}

                override fun onSaveFailed() { /* ignore */ }
            })
        }
    }

    private fun verifyPriorityButtonClickSavesWithThatPriority(buttonId: Int,
                                                               priority: FlashcardPriority) {
        addFlashcardsToDataSource(FLASHCARD_1)
        launchActivity()

        onView(withId(buttonId)).perform(click())

        assertFlashcardSavedWithPriority(priority)
    }

    private fun assertFlashcardSavedWithPriority(priority: FlashcardPriority) {
        var savedFlashcard: Flashcard? = null
        dataSource.getFlashcard(FLASHCARD_1.id, object: FlashcardDataSource.GetFlashcardCallback {
            override fun onFlashcardLoaded(flashcard: Flashcard) {
                savedFlashcard = flashcard
            }

            override fun onDataNotAvailable() { /* ignore */ }
        })

        assertEquals(priority, savedFlashcard!!.priority)
    }

    private fun launchActivity() {
        testRule.launchActivity(Intent())
        testRule.activity.setFragment(RandomFlashcardFragment.newInstance())
    }
}
