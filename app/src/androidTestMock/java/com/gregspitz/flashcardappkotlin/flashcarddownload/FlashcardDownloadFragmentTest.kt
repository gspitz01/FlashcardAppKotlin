package com.gregspitz.flashcardappkotlin.flashcarddownload

import android.content.pm.ActivityInfo
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.contrib.RecyclerViewActions
import android.support.test.espresso.matcher.BoundedMatcher
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.filters.MediumTest
import android.support.test.runner.AndroidJUnit4
import android.support.v7.widget.RecyclerView
import android.view.View
import com.gregspitz.flashcardappkotlin.BaseSingleFragmentTest
import com.gregspitz.flashcardappkotlin.FlashcardApplication
import com.gregspitz.flashcardappkotlin.MockTestData.CATEGORY_1
import com.gregspitz.flashcardappkotlin.MockTestData.CATEGORY_2
import com.gregspitz.flashcardappkotlin.R
import com.gregspitz.flashcardappkotlin.data.model.Category
import com.gregspitz.flashcardappkotlin.data.service.model.DownloadCategory
import com.gregspitz.flashcardappkotlin.data.source.FakeFlashcardDownloadService
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.not
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@MediumTest
class FlashcardDownloadFragmentTest : BaseSingleFragmentTest() {

    private val downloadCategories = listOf(DownloadCategory(CATEGORY_1.name, 3),
            DownloadCategory(CATEGORY_2.name, 4))

    private val downloadService =
            FlashcardApplication.repoComponent.exposeFlashcardDownloadService() as FakeFlashcardDownloadService

    @Before
    fun setup_test() {
        downloadService.deleteAll()
    }

    @Test
    fun atStart_showsListOfDownloadCategoriesAndDownloadButtonDisabled() {
        addCategoriesToDownloadService(downloadCategories)
        launchActivity()
        scrollToAndVerifyPosition(0, CATEGORY_1, 3)
        scrollToAndVerifyPosition(1, CATEGORY_2, 4)

        onView(withId(R.id.downloadFlashcardsButton))
                .check(matches(allOf(isDisplayed(), not(isEnabled()))))
    }

    @Test
    fun noDownloadCategories_showsFailedToGetDownloadCategories() {
        downloadService.categoriesFailure = true
        launchActivity()
        checkForSnackbar(R.string.failed_to_load_download_categories_text)
    }

    @Test
    fun selectCategory_enablesDownloadFlashcardButton() {
        addCategoriesToDownloadService(downloadCategories)
        launchActivity()
        scrollToAndClickPosition(1)
        verifyDownloadFlashcardButtonDisplayedAndEnabled()
    }

    @Test
    fun onRotation_maintainsSelection() {
        addCategoriesToDownloadService(downloadCategories)
        launchActivity()
        requestOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        scrollToAndClickPosition(0)
        verifyDownloadFlashcardButtonDisplayedAndEnabled()
        requestOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
        verifyDownloadFlashcardButtonDisplayedAndEnabled()
    }

    @Test
    fun selectCategoryAndClickDownloadButton_showsSuccessMessage() {
        addCategoriesToDownloadService(downloadCategories)
        launchActivity()
        scrollToAndClickPosition(1)
        clickDownloadFlashcardButton()
        assertEquals(downloadCategories[1], downloadService.attemptedDownloadCategory)
        checkForSnackbar(R.string.download_flashcards_successful)
    }

    @Test
    fun failedDownloadFromService_showsFailedMessage() {
        downloadService.flashcardFailure = true
        addCategoriesToDownloadService(downloadCategories)
        launchActivity()
        scrollToAndClickPosition(1)
        clickDownloadFlashcardButton()
        assertEquals(downloadCategories[1], downloadService.attemptedDownloadCategory)
        checkForSnackbar(R.string.download_flashcards_failed)
    }

    private fun launchActivity() {
        launchActivity(FlashcardDownloadFragment.newInstance())
    }

    private fun addCategoriesToDownloadService(downloadCategories: List<DownloadCategory>) {
        downloadService.addDownloadCategories(downloadCategories)
    }

    private fun verifyDownloadFlashcardButtonDisplayedAndEnabled() {
        onView(withId(R.id.downloadFlashcardsButton))
                .check(matches(allOf(isDisplayed(), isEnabled())))
    }

    private fun clickDownloadFlashcardButton() {
        onView(withId(R.id.downloadFlashcardsButton)).perform(click())
    }

    private fun scrollToAndVerifyPosition(position: Int, category: Category, count: Int) {
        onView(withId(R.id.categoriesRecyclerView))
                .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(position))
                .check(ViewAssertions.matches(
                        hasCategoryAndCountForPosition(position, category, count)))
    }

    private fun scrollToAndClickPosition(position: Int) {
        onView(withId(R.id.categoriesRecyclerView))
                .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(position))
                .perform(RecyclerViewActions
                        .actionOnItemAtPosition<RecyclerView.ViewHolder>(position, click()))
    }

    private fun hasCategoryAndCountForPosition(position: Int, category: Category, count: Int)
            : Matcher<in View>? =
            object : BoundedMatcher<View, RecyclerView>(RecyclerView::class.java) {
                override fun describeTo(description: Description?) {
                    description?.appendText("Item has flashcard category at position $position")
                }

                override fun matchesSafely(item: RecyclerView?): Boolean {
                    if (item == null) {
                        return false
                    }

                    val holder = item.findViewHolderForAdapterPosition(position)
                    return holder != null &&
                            hasDescendant(withText(category.name)).matches(holder.itemView) &&
                            hasDescendant(withText(count.toString())).matches(holder.itemView)
                }

            }
}
