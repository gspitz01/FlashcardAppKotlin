package com.gregspitz.flashcardappkotlin.flashcarddetail

import android.content.Intent
import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.intent.rule.IntentsTestRule
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.espresso.matcher.ViewMatchers.withText
import android.support.test.runner.AndroidJUnit4
import com.gregspitz.flashcardappkotlin.R
import com.gregspitz.flashcardappkotlin.data.model.Flashcard
import com.gregspitz.flashcardappkotlin.data.source.FakeFlashcardLocalDataSource
import com.gregspitz.flashcardappkotlin.data.source.FlashcardRepository
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Tests for the implementation of {@link FlashcardDetailActivity}
 */
@RunWith(AndroidJUnit4::class)
class FlashcardDetailActivityTest {

    // TODO: figure this out

    private val flashcard = Flashcard("0", "Front", "Back")

    @Rule @JvmField
    val testRule = IntentsTestRule<FlashcardDetailActivity>(
            FlashcardDetailActivity::class.java, true, false
    )

    @Before
    fun setup() {
        FlashcardRepository.destroyInstance()
        FakeFlashcardLocalDataSource.getInstance(
                InstrumentationRegistry.getTargetContext()
        ).addFlashcards(flashcard)
    }

    @Test
    fun startUp_showsFlashcard() {
        createIntentAndStartActivity()
        onView(withId(R.id.flashcard_front)).check(matches(withText(flashcard.front)))
        onView(withId(R.id.flashcard_back)).check(matches(withText(flashcard.back)))
    }

    private fun createIntentAndStartActivity() {
        val intent = Intent()
        intent.putExtra(FlashcardDetailActivity.flashcardIntentId, flashcard.id)
        testRule.launchActivity(intent)
    }
}
