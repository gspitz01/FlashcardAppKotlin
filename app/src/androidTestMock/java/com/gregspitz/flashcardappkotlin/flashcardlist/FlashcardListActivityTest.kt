package com.gregspitz.flashcardappkotlin.flashcardlist

import android.content.Intent
import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.intent.Intents.intended
import android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent
import android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra
import android.support.test.espresso.intent.rule.IntentsTestRule
import android.support.test.espresso.matcher.BoundedMatcher
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.runner.AndroidJUnit4
import android.support.v7.widget.RecyclerView
import android.view.View
import com.gregspitz.flashcardappkotlin.FlashcardApplication
import com.gregspitz.flashcardappkotlin.R
import com.gregspitz.flashcardappkotlin.addeditflashcard.AddEditFlashcardActivity
import com.gregspitz.flashcardappkotlin.data.model.Flashcard
import com.gregspitz.flashcardappkotlin.data.source.FakeFlashcardLocalDataSource
import com.gregspitz.flashcardappkotlin.data.source.FlashcardRepository
import com.gregspitz.flashcardappkotlin.flashcarddetail.FlashcardDetailActivity
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Tests for the implementation of {@link FlashcardListActivity}
 */
@RunWith(AndroidJUnit4::class)
class FlashcardListActivityTest {

    private val flashcard1 = Flashcard("0", "A front", "A back")

    private val flashcard2 = Flashcard("1", "A different front", "A different back")

    private val dataSource = FlashcardApplication.repoComponent.getFlashcardLocalDataSource()

    @Rule @JvmField
    val testRule = IntentsTestRule<FlashcardListActivity>(
            FlashcardListActivity::class.java, true, false)

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
        onView(withText(flashcard1.front)).perform(click())
        intended(allOf(hasComponent(FlashcardDetailActivity::class.java.name),
                hasExtra(FlashcardDetailActivity.flashcardIntentId, flashcard1.id)))
    }

    @Test
    fun clickAddFlashcardFab_showsAddEditFlashcardView() {
        addFlashcardsToDataSource(flashcard1, flashcard2)
        launchActivity()
        onView(withId(R.id.addFlashcardFab)).perform(click())
        intended(allOf(hasComponent(AddEditFlashcardActivity::class.java.name),
                hasExtra(AddEditFlashcardActivity.flashcardIdExtra,
                        AddEditFlashcardActivity.newFlashcardExtra)))
    }

    private fun addFlashcardsToDataSource(vararg flashcards: Flashcard) {
        dataSource.addFlashcards(*flashcards)
    }


    private fun launchActivity() {
        testRule.launchActivity(Intent())
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
