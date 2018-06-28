package com.gregspitz.flashcardappkotlin

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.*
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.contrib.DrawerActions
import android.support.test.espresso.contrib.NavigationViewActions.navigateTo
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import com.gregspitz.flashcardappkotlin.data.source.FlashcardDataSource
import org.hamcrest.CoreMatchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MainScreenTest {

    @Rule @JvmField
    val activityRule = ActivityTestRule<MainActivity>(MainActivity::class.java)

    @Before
    fun setup() {
        FlashcardApplication.repoComponent.exposeRepository().deleteAllFlashcards(
                object: FlashcardDataSource.DeleteAllFlashcardsCallback {
                    override fun onDeleteSuccessful() { /* ignore */ }

                    override fun onDeleteFailed() { /* ignore */ }
                }
        )
    }

    @Test
    fun moveToList_pressEdit_backToList_pressAdd_fieldsShouldBeEmpty() {
        // Move to add
        onView(withId(R.id.drawerLayout)).perform(DrawerActions.open())
        onView(withId(R.id.navDrawer)).perform(navigateTo(R.id.newFlashcard))

        // Type in some data
        onView(withId(R.id.flashcardEditCategory))
                .perform(typeText(MockTestData.FLASHCARD_1.category))
        onView(withId(R.id.flashcardEditFront)).perform(typeText(MockTestData.FLASHCARD_1.front))
        onView(withId(R.id.flashcardEditBack))
                .perform(typeText(MockTestData.FLASHCARD_1.back), closeSoftKeyboard())

        // Save flashcard
        onView(withId(R.id.saveFlashcardButton)).perform(click())

        // Move to list view
        onView(withId(R.id.drawerLayout)).perform(DrawerActions.open())
        onView(withId(R.id.navDrawer)).perform(navigateTo(R.id.navList))

        // Click edit on first view pager item (should be first item from initial data)
        // Need double click to set focus first
        onView(withId(R.id.editFlashcardButton)).perform(doubleClick())

        // Edit view shows correct fields
        onView(withId(R.id.flashcardEditCategory))
                .check(matches(withText(MockTestData.FLASHCARD_1.category)))
        onView(withId(R.id.flashcardEditFront))
                .check(matches(withText(MockTestData.FLASHCARD_1.front)))
        onView(withId(R.id.flashcardEditBack))
                .check(matches(withText(MockTestData.FLASHCARD_1.back)))
        onView(withId(R.id.failedToLoadFlashcard)).check(matches(not(isDisplayed())))

        // Move back to list
        onView(withId(R.id.showFlashcardListButton)).perform(click())

        // Move to add Flashcard view
        onView(withId(R.id.addFlashcardFab)).perform(click())

        // Fields should be empty
        onView(withId(R.id.flashcardEditCategory)).check(matches(withText("")))
        onView(withId(R.id.flashcardEditFront)).check(matches(withText("")))
        onView(withId(R.id.flashcardEditBack)).check(matches(withText("")))
        onView(withId(R.id.failedToLoadFlashcard)).check(matches(not(isDisplayed())))
    }
}
