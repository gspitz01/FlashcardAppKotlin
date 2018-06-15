package com.gregspitz.flashcardappkotlin

import android.app.Activity
import android.support.test.espresso.Espresso
import android.support.test.espresso.assertion.ViewAssertions
import android.support.test.espresso.matcher.RootMatchers
import android.support.test.espresso.matcher.ViewMatchers
import android.support.test.rule.ActivityTestRule
import org.hamcrest.Matchers

object TestUtils {
    fun checkForToast(testRule: ActivityTestRule<out Activity>, stringId: Int) {
        Espresso.onView(ViewMatchers.withText(stringId))
                .inRoot(RootMatchers.withDecorView(Matchers.not(testRule.activity.window.decorView)))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }
}
