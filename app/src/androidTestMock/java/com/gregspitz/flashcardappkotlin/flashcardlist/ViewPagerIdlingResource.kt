package com.gregspitz.flashcardappkotlin.flashcardlist

import android.support.test.espresso.IdlingResource
import android.support.v4.view.ViewPager

/**
 * Idling resource to make sure swipes on a ViewPager finish completely
 * adapted from https://stackoverflow.com/questions/31056918/wait-for-view-pager-animations-with-espresso
 */
class ViewPagerIdlingResource(viewPager: ViewPager, private val name: String)
    : IdlingResource {

    private var idle = true

    private var resourceCallback: IdlingResource.ResourceCallback? = null

    init {
        viewPager.addOnPageChangeListener(ViewPageListener())
    }

    inner class ViewPageListener : ViewPager.SimpleOnPageChangeListener() {
        override fun onPageScrollStateChanged(state: Int) {
            idle = (state == ViewPager.SCROLL_STATE_IDLE ||
                    state == ViewPager.SCROLL_STATE_DRAGGING)
            if (idle && resourceCallback != null) {
                resourceCallback!!.onTransitionToIdle()
            }
        }
    }

    override fun getName(): String {
        return name
    }

    override fun isIdleNow(): Boolean {
        return idle
    }

    override fun registerIdleTransitionCallback(callback: IdlingResource.ResourceCallback?) {
        resourceCallback = callback
    }

}
