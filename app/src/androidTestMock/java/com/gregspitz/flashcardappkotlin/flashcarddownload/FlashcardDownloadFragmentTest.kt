package com.gregspitz.flashcardappkotlin.flashcarddownload

import android.content.Intent
import android.content.pm.ActivityInfo
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions
import android.support.test.espresso.assertion.ViewAssertions.doesNotExist
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.contrib.RecyclerViewActions
import android.support.test.espresso.matcher.BoundedMatcher
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import android.support.v7.widget.RecyclerView
import android.view.View
import com.gregspitz.flashcardappkotlin.FlashcardApplication
import com.gregspitz.flashcardappkotlin.MockTestData.CATEGORY_1
import com.gregspitz.flashcardappkotlin.MockTestData.CATEGORY_2
import com.gregspitz.flashcardappkotlin.R
import com.gregspitz.flashcardappkotlin.SingleFragmentActivity
import com.gregspitz.flashcardappkotlin.TestUtils
import com.gregspitz.flashcardappkotlin.data.model.Category
import com.gregspitz.flashcardappkotlin.data.service.model.DownloadCategory
import com.gregspitz.flashcardappkotlin.data.source.FakeFlashcardDownloadService
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.not
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class FlashcardDownloadFragmentTest {

    private val downloadCategories = listOf(DownloadCategory(CATEGORY_1.name, 3),
            DownloadCategory(CATEGORY_2.name, 4))

    private val downloadService =
            FlashcardApplication.repoComponent.exposeFlashcardDownloadService() as FakeFlashcardDownloadService

    @Rule
    @JvmField
    val activityRule = ActivityTestRule<SingleFragmentActivity>(SingleFragmentActivity::class.java,
            true, false)

    @Before
    fun setup() {
        downloadService.deleteAll()
    }

    @Test
    fun atStart_showsListOfDownloadCategoriesAndDownloadButtonDisabled() {
        addCategoriesToDownloadService(downloadCategories)
        launchActivity()
        scrollToAndVerifyPosition(0, CATEGORY_1, 3)
        scrollToAndVerifyPosition(1, CATEGORY_2, 4)

        onView(withId(R.id.downloadFlashcardsButton)).check(doesNotExist())
    }

    @Test
    fun noDownloadCategories_showsFailedToGetDownloadCategories() {
        downloadService.categoriesFailure = true
        launchActivity()
        onView(withId(R.id.downloadCategoriesMessage))
                .check(matches(withText(R.string.failed_to_load_download_categories_text)))
    }

    @Test
    fun selectCategory_enablesActionModeMenuWithSingleCategorySelectedTitle() {
        addCategoriesToDownloadService(downloadCategories)
        launchActivity()
        scrollToAndClickPosition(1)
        verifyDownloadFlashcardButtonDisplayedAndEnabled()
        val title = "1 ${activityRule.activity.getString(R.string.action_one_selected)}"
        onView(withText(title)).check(matches(isDisplayed()))
    }

    @Test
    fun selectTwoCategories_enablesActionModeMenuWith2CategoriesSelectedTitle() {
        addCategoriesToDownloadService(downloadCategories)
        launchActivity()
        scrollToAndClickPosition(0)
        scrollToAndClickPosition(1)
        verifyDownloadFlashcardButtonDisplayedAndEnabled()
        verifyMultipleCountActionTitleDisplayed(2)
    }

    @Test
    fun onRotation_maintainsSelection() {
        addCategoriesToDownloadService(downloadCategories)
        launchActivity()
        activityRule.activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        scrollToAndClickPosition(0)
        scrollToAndClickPosition(1)
        verifyDownloadFlashcardButtonDisplayedAndEnabled()
        verifyMultipleCountActionTitleDisplayed(2)
        activityRule.activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        verifyDownloadFlashcardButtonDisplayedAndEnabled()
        verifyMultipleCountActionTitleDisplayed(2)
    }

    @Test
    fun selectSingleCategoryAndClickDownloadButton_showsSuccessToast() {
        addCategoriesToDownloadService(downloadCategories)
        launchActivity()
        scrollToAndClickPosition(1)
        clickDownloadFlashcardButton()
        assertEquals(listOf(downloadCategories[1]), downloadService.attemptedDownloadCategories)
        TestUtils.checkForToast(activityRule, R.string.download_flashcards_successful)
    }

    @Test
    fun selectTwoCategoriesAndClickDownloadButton_showsSuccessToast() {
        addCategoriesToDownloadService(downloadCategories)
        launchActivity()
        scrollToAndClickPosition(1)
        scrollToAndClickPosition(0)
        clickDownloadFlashcardButton()
        assertEquals(downloadCategories, downloadService.attemptedDownloadCategories)
        TestUtils.checkForToast(activityRule, R.string.download_flashcards_successful)
    }

    @Test
    fun failedDownloadFromService_showsFailedToast() {
        downloadService.flashcardFailure = true
        addCategoriesToDownloadService(downloadCategories)
        launchActivity()
        scrollToAndClickPosition(1)
        clickDownloadFlashcardButton()
        assertEquals(listOf(downloadCategories[1]), downloadService.attemptedDownloadCategories)
        TestUtils.checkForToast(activityRule, R.string.download_flashcards_failed)
    }

    private fun launchActivity() {
        activityRule.launchActivity(Intent())
        activityRule.activity.setFragment(FlashcardDownloadFragment.newInstance())
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

    private fun verifyMultipleCountActionTitleDisplayed(count: Int) {
        val title = "$count ${activityRule.activity.getString(R.string.action_many_selected)}"
        onView(withText(title)).check(matches(isDisplayed()))
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
                            withChild(withText(category.name)).matches(holder.itemView) &&
                            withChild(withText(count.toString())).matches(holder.itemView)
                }

            }
}
