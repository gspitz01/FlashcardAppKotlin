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

import android.support.test.espresso.matcher.BoundedMatcher
import android.support.test.espresso.matcher.ViewMatchers.withChild
import android.support.test.espresso.matcher.ViewMatchers.withText
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.support.v7.widget.RecyclerView
import android.view.View
import com.gregspitz.flashcardappkotlin.data.model.Flashcard
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.junit.Rule
import org.junit.runner.RunWith

/**
 * Large test for whole app
 */
@RunWith(AndroidJUnit4::class)
class MainScreenTest {

    // TODO: make these tests - keeping in mind that this one test failed in travis build #7
    // for the reason of not having a matching view, whereas on this computer it fails for matching
    // more than one view.

    @Rule @JvmField
    val testRule = ActivityTestRule<MainActivity>(MainActivity::class.java)

//    @Test
//    fun moveToFlashcardList_containsFlashcards() {
//        onView(withId(R.id.flashcardListButton)).perform(click())
//
//        onView(withText(R.string.app_name)).check(matches(isDisplayed()))
//        onView(withText(InitialData.flashcards[0].front)).perform(click())
//
//        onView(withText(InitialData.flashcards[0].front)).check(matches(isDisplayed()))
//        onView(withText(InitialData.flashcards[0].back)).check(matches(isDisplayed()))
//    }

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
