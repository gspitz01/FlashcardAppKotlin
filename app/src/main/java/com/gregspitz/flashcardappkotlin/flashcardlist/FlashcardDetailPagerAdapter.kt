/*
 * Copyright (C) 2018 Greg Spitz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gregspitz.flashcardappkotlin.flashcardlist

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.PagerAdapter

/**
 * PagerAdapter for FlashcardDetailFragments
 */
class FlashcardDetailPagerAdapter(fragmentManager: FragmentManager,
                                  private var fragments: List<Fragment>)
    : FragmentStatePagerAdapter(fragmentManager) {
    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    override fun getCount(): Int {
        return fragments.size
    }


//    override fun restoreState(state: Parcelable?, loader: ClassLoader?) {
//        // Do nothing to avoid NPE
//        // this solution comes from this stackoverflow response
//        // https://stackoverflow.com/questions/18642890/fragmentstatepageradapter-with-childfragmentmanager-fragmentmanagerimpl-getfra
//    }

    fun setFragments(fragments: List<Fragment>) {
        this.fragments = fragments
        notifyDataSetChanged()
    }

    /**
     * As suggested here:
     * https://stackoverflow.com/questions/7263291/viewpager-pageradapter-not-updating-the-view
     * this is to make sure the ViewPager actually destroys view as needed.
     * If efficiency becomes a problem, change based on other suggestion in that reply
     */
    override fun getItemPosition(`object`: Any): Int {
        return PagerAdapter.POSITION_NONE
    }
}
