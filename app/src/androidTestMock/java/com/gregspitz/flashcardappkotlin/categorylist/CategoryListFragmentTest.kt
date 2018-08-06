package com.gregspitz.flashcardappkotlin.categorylist

import android.content.pm.ActivityInfo
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.doesNotExist
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.contrib.RecyclerViewActions
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.filters.MediumTest
import android.support.test.runner.AndroidJUnit4
import android.support.v7.widget.RecyclerView
import com.gregspitz.flashcardappkotlin.BaseSingleFragmentTest
import com.gregspitz.flashcardappkotlin.MockTestData.CATEGORY_1
import com.gregspitz.flashcardappkotlin.MockTestData.CATEGORY_2
import com.gregspitz.flashcardappkotlin.MockTestData.FLASHCARD_1
import com.gregspitz.flashcardappkotlin.MockTestData.FLASHCARD_2
import com.gregspitz.flashcardappkotlin.R
import com.gregspitz.flashcardappkotlin.TestUtils.recyclerViewScrollToAndVerifyPosition
import com.gregspitz.flashcardappkotlin.data.model.Category
import com.gregspitz.flashcardappkotlin.data.model.Flashcard
import com.gregspitz.flashcardappkotlin.data.model.FlashcardListItem
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@MediumTest
class CategoryListFragmentTest : BaseSingleFragmentTest() {

    @Test
    fun recyclerView_showsListOfCategories() {
        addFlashcardsToDataSource(FLASHCARD_1, FLASHCARD_2)
        launchActivity()

        recyclerViewScrollToAndVerifyPosition(R.id.categoryRecyclerView, 0, CATEGORY_1.name)
        recyclerViewScrollToAndVerifyPosition(R.id.categoryRecyclerView, 1, CATEGORY_2.name)
    }

    @Test
    fun noCategoriesToShow_showsNoCategoriesMessage() {
        launchActivity()
        onView(withId(R.id.categoryRecyclerView)).check(matches(isDisplayed()))
        checkForSnackbar(R.string.no_categories_to_show_text)
        onView(withId(R.id.categoryRecyclerView)).check(matches(isDisplayed()))
    }

    @Test
    fun failedToLoadCategories_showsFailedToLoadMessage() {
        localDataSource.setFailure(true)
        launchActivity()
        onView(withId(R.id.categoryRecyclerView)).check(matches(isDisplayed()))
        checkForSnackbar(R.string.failed_to_load_categories_text)
        onView(withId(R.id.categoryRecyclerView)).check(matches(isDisplayed()))
    }

    @Test
    fun clickCategory_showsThatCategoryInFlashcardListView() {
        addFlashcardsToDataSource(FLASHCARD_1, FLASHCARD_2)
        launchActivity()
        clickCategoryOfFlashcard(FLASHCARD_2)
        checkForFlashcardListViewWithCategory(listOf(FLASHCARD_2), listOf(FLASHCARD_1))
    }

    @Test
    fun orientationChangeThenClickCategory_showsThatCategoryInFlashcardListView() {
        addFlashcardsToDataSource(FLASHCARD_1, FLASHCARD_2)
        launchActivity()
        requestOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        requestOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
        clickCategoryOfFlashcard(FLASHCARD_2)
        checkForFlashcardListViewWithCategory(listOf(FLASHCARD_2), listOf(FLASHCARD_1))
    }

    private fun launchActivity() {
        launchActivity(CategoryListFragment.newInstance())
    }

    private fun clickCategoryOfFlashcard(flashcard: Flashcard) {
        onView(withId(R.id.categoryRecyclerView))
                .perform(RecyclerViewActions
                        .scrollTo<RecyclerView.ViewHolder>(hasDescendant(
                                withText(flashcard.category))))
                .perform(RecyclerViewActions
                        .actionOnItem<RecyclerView.ViewHolder>(
                                hasDescendant(withText(flashcard.category)), click()))
    }

    private fun checkForFlashcardListViewWithCategory(displayedFlashcards: List<Flashcard>,
                                                      notDisplayedFlashcards: List<Flashcard>) {
        for ((index, flashcard) in createListWithCategories(displayedFlashcards)
                .withIndex()) {
            val text = (flashcard as? Flashcard)?.front ?: (flashcard as Category).name
            recyclerViewScrollToAndVerifyPosition(R.id.flashcardRecyclerView, index,
                    text)
        }
        for (flashcard in notDisplayedFlashcards) {
            onView(withText(flashcard.front)).check(doesNotExist())
        }
    }

    private fun createListWithCategories(flashcards: List<Flashcard>): List<FlashcardListItem> {
        val listWithCategories = mutableListOf<FlashcardListItem>()
        for (flashcard in flashcards) {
            if (!listWithCategories.contains(Category(flashcard.category))) {
                listWithCategories.add(Category(flashcard.category))
            }
            listWithCategories.add(flashcard)
        }
        return listWithCategories
    }
}
