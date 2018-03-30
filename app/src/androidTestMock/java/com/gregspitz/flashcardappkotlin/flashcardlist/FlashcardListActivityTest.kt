package com.gregspitz.flashcardappkotlin.flashcardlist

import android.content.Intent
import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.intent.rule.IntentsTestRule
import android.support.test.espresso.matcher.BoundedMatcher
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.runner.AndroidJUnit4
import android.support.v7.widget.RecyclerView
import android.view.View
import com.gregspitz.flashcardappkotlin.R
import com.gregspitz.flashcardappkotlin.data.model.Flashcard
import com.gregspitz.flashcardappkotlin.data.source.FakeFlashcardLocalDataSource
import com.gregspitz.flashcardappkotlin.data.source.FlashcardRepository
import org.hamcrest.Description
import org.hamcrest.Matcher
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

    // TODO: continue making these tests

    private val flashcard1 = Flashcard("0", "A front", "A back")

    private val flashcard2 = Flashcard("1", "A different front", "A different back")

    @Rule @JvmField
    val testRule = IntentsTestRule<FlashcardListActivity>(
            FlashcardListActivity::class.java, true, false)

    @Before
    fun setup() {
        FlashcardRepository.destroyInstance()
        FakeFlashcardLocalDataSource.getInstance(
                InstrumentationRegistry.getTargetContext()
        ).addFlashcards(flashcard1, flashcard2)
    }

    @Test
    fun flashcardRecyclerView_showsFlashcardFronts() {
        createIntentAndLaunchActivity()
        onView(withId(R.id.flashcard_recycler_view))
                .check(matches(hasFlashcardFrontForPosition(0, flashcard1)))
        onView(withId(R.id.flashcard_recycler_view))
                .check(matches(hasFlashcardFrontForPosition(1, flashcard2)))
        onView(withId(R.id.no_flashcards_to_show)).check(matches(not(isDisplayed())))
    }


    private fun createIntentAndLaunchActivity() {
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