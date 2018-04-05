package com.gregspitz.flashcardappkotlin.randomflashcard

import android.content.Intent
import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.intent.rule.IntentsTestRule
import android.support.test.espresso.matcher.BoundedMatcher
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.espresso.matcher.ViewMatchers.withText
import android.support.test.runner.AndroidJUnit4
import android.view.View
import android.widget.TextView
import com.gregspitz.flashcardappkotlin.R
import com.gregspitz.flashcardappkotlin.data.model.Flashcard
import com.gregspitz.flashcardappkotlin.data.source.FakeFlashcardLocalDataSource
import com.gregspitz.flashcardappkotlin.data.source.FlashcardRepository
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Tests for the implementation of {@link RandomFlashcardActivity}
 */
@RunWith(AndroidJUnit4::class)
class RandomFlashcardActivityTest {

    private val flashcard1 = Flashcard("0", "Front", "Back")

    private val flashcard2 = Flashcard("1", "Tag Team", "Back Again")

    private var firstText = ""

    @Rule @JvmField
    val testRule = IntentsTestRule<RandomFlashcardActivity>(
            RandomFlashcardActivity::class.java, true, false)

    @Before
    fun setup() {
        FlashcardRepository.destroyInstance()
        FakeFlashcardLocalDataSource.getInstance(InstrumentationRegistry.getTargetContext())
                .clearFlashcards()
    }

    @Test
    fun atStart_showsFrontOfOneFlashcardAndClickTurnsFlashcard() {
        addFlashcardsToDataSource(flashcard1)

        launchActivity()

        onView(withId(R.id.flashcardSide)).check(matches(withText(flashcard1.front)))
                .perform(click())
                .check(matches(withText(flashcard1.back)))
    }

    @Test
    fun noFlashcardsToLoad_showsNoFlashcards() {
        launchActivity()

        onView(withId(R.id.flashcardSide))
                .check(matches(withText(R.string.failed_to_load_flashcard_text)))
    }

    @Test
    fun nextFlashcardButtonClick_loadsDifferentFlashcard() {
        addFlashcardsToDataSource(flashcard1, flashcard2)
        launchActivity()

        onView(withId(R.id.flashcardSide))
                .check(matches(withTextOfOneOf(flashcard1.front, flashcard2.front)))
        onView(withId(R.id.nextFlashcardButton)).perform(click())
        val secondText = getSecondText(flashcard1.front, flashcard2.front)
        onView(withId(R.id.flashcardSide)).check(matches(withText(secondText)))
    }

    @Test
    fun ifOnlyOneFlashcard_newFlashcardButtonClickLoadsSameFlashcard() {
        addFlashcardsToDataSource(flashcard1)
        launchActivity()

        onView(withId(R.id.flashcardSide)).check(matches(withText(flashcard1.front)))
        onView(withId(R.id.nextFlashcardButton)).perform(click())
        onView(withId(R.id.flashcardSide)).check(matches(withText(flashcard1.front)))
    }

    private fun getSecondText(text1: String, text2: String): String {
        if (firstText == text1) {
            return text2
        } else if (firstText == text2) {
            return text1
        }
        throw IllegalStateException("First text did not equal one of these texts:\n$text1\n$text2")
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
        FakeFlashcardLocalDataSource.getInstance(
                InstrumentationRegistry.getTargetContext())
                .addFlashcards(*flashcards)
    }

    private fun launchActivity() {
        testRule.launchActivity(Intent())
    }
}