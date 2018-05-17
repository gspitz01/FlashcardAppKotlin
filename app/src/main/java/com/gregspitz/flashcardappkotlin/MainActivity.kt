package com.gregspitz.flashcardappkotlin

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import com.gregspitz.flashcardappkotlin.addeditflashcard.AddEditFlashcardFragment
import com.gregspitz.flashcardappkotlin.flashcardlist.FlashcardListFragment
import com.gregspitz.flashcardappkotlin.randomflashcard.RandomFlashcardFragment

class MainActivity : AppCompatActivity(), MainFragmentRouter {

    private val randomFlashcardFragment = RandomFlashcardFragment.newInstance()
    private var flashcardListFragment: FlashcardListFragment? = null
    private var addEditFlashcardFragment: AddEditFlashcardFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Start with the game
        setFragment(randomFlashcardFragment)
    }

    private fun setFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
                .add(R.id.contentFrame, fragment, "Main Content")
                .commit()
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
                .replace(R.id.contentFrame, fragment)
                .commit()
    }

    override fun showAddEditFlashcard(flashcardId: String) {
        var wasNull = false
        if (addEditFlashcardFragment == null) {
            addEditFlashcardFragment = AddEditFlashcardFragment.newInstance(flashcardId)
            wasNull = true
        }
        replaceFragment(addEditFlashcardFragment!!)
        if (!wasNull) {
            addEditFlashcardFragment?.setFlashcard(flashcardId)
        }
    }

    override fun showFlashcardList(flashcardId: String) {
        var wasNull = false
        if (flashcardListFragment == null) {
            flashcardListFragment = FlashcardListFragment.newInstance(flashcardId)
            wasNull = true
        }
        replaceFragment(flashcardListFragment!!)
        if (!wasNull) {
            flashcardListFragment?.setDetailView(flashcardId)
        }
    }

    override fun showRandomFlashcard() {
        replaceFragment(randomFlashcardFragment)
    }
}
