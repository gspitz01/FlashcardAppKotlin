package com.gregspitz.flashcardappkotlin

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.BoundedMatcher
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.support.v7.widget.RecyclerView
import android.view.View
import com.gregspitz.flashcardappkotlin.data.model.Flashcard
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.junit.Rule
import org.junit.Test
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
