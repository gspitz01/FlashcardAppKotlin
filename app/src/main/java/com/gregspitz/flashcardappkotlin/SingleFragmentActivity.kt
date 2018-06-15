package com.gregspitz.flashcardappkotlin

import android.os.Bundle
import android.support.annotation.RestrictTo
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import com.gregspitz.flashcardappkotlin.addeditflashcard.AddEditFlashcardFragment
import com.gregspitz.flashcardappkotlin.flashcarddownload.FlashcardDownloadFragment
import com.gregspitz.flashcardappkotlin.flashcardlist.FlashcardListFragment
import com.gregspitz.flashcardappkotlin.randomflashcard.RandomFlashcardFragment
import kotlinx.android.synthetic.main.activity_single_fragment.*

/**
 * Container for testing fragments
 */
@RestrictTo(RestrictTo.Scope.TESTS)
class SingleFragmentActivity : AppCompatActivity(), MainFragmentRouter {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_fragment)
        setSupportActionBar(toolbar)
    }

    fun setFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
                .add(R.id.contentFrame, fragment, "TEST")
                .commit()
    }

    fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
                .replace(R.id.contentFrame, fragment)
                .commit()
    }

    override fun showFlashcardList(flashcardId: String) {
        replaceFragment(FlashcardListFragment.newInstance(flashcardId))
    }

    override fun showAddEditFlashcard(flashcardId: String) {
        replaceFragment(AddEditFlashcardFragment.newInstance(flashcardId))
    }

    override fun showRandomFlashcard() {
        replaceFragment(RandomFlashcardFragment.newInstance())
    }

    override fun showFlashcardDownload() {
        replaceFragment(FlashcardDownloadFragment.newInstance())
    }
}
