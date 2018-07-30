package com.gregspitz.flashcardappkotlin

import android.support.test.espresso.Espresso
import android.support.test.espresso.assertion.ViewAssertions
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.contrib.RecyclerViewActions
import android.support.test.espresso.matcher.BoundedMatcher
import android.support.test.espresso.matcher.ViewMatchers
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.v7.widget.RecyclerView
import android.view.View
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.Description
import org.hamcrest.Matcher

object TestUtils {

    fun checkForSnackbar(stringId: Int) {
        Espresso.onView(allOf(withId(android.support.design.R.id.snackbar_text),
                withText(stringId)))
                .check(matches(isDisplayed()))
    }

    private fun hasTextForPosition(position: Int, text: String)
            : Matcher<in View>? =
            object : BoundedMatcher<View, RecyclerView>(RecyclerView::class.java) {
                override fun describeTo(description: Description?) {
                    description?.appendText("Item has text: $text, at position $position")
                }

                override fun matchesSafely(item: RecyclerView?): Boolean {
                    if (item == null) {
                        return false
                    }

                    val holder = item.findViewHolderForAdapterPosition(position)
                    return holder != null &&
                            ViewMatchers.withChild(ViewMatchers.withText(text)).matches(holder.itemView)
                }

            }

    fun recyclerViewScrollToAndVerifyPosition(recyclerViewId: Int, position: Int, text: String) {
        Espresso.onView(ViewMatchers.withId(recyclerViewId))
                .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(position))
                .check(ViewAssertions.matches(hasTextForPosition(position, text)))
    }
}
