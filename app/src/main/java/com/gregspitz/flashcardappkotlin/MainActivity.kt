package com.gregspitz.flashcardappkotlin

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.gregspitz.flashcardappkotlin.addeditflashcard.AddEditFlashcardFragment
import com.gregspitz.flashcardappkotlin.flashcardlist.FlashcardListFragment
import com.gregspitz.flashcardappkotlin.randomflashcard.RandomFlashcardFragment
import kotlinx.android.synthetic.main.activity_main.*

/**
 * The only Activity which holds all the Fragment views
 * Serves as the router
 */
class MainActivity : AppCompatActivity(), MainFragmentRouter {

    // Start with a RandomFlashcardFragment (a.k.a. the game)
    private val randomFlashcardFragment = RandomFlashcardFragment.newInstance()
    private lateinit var flashcardListFragment: FlashcardListFragment
    private lateinit var addEditFlashcardFragment: AddEditFlashcardFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp)
        }

        // Start with the game
        if (savedInstanceState == null) {
            setFragment(randomFlashcardFragment)
        }

        navDrawer.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navGame -> {
                    showRandomFlashcard()
                    drawerLayout.closeDrawers()
                    return@setNavigationItemSelectedListener true
                }
                R.id.navList -> {
                    showFlashcardList(FlashcardListFragment.noParticularFlashcardExtra)
                    drawerLayout.closeDrawers()
                    return@setNavigationItemSelectedListener true
                }
                R.id.newFlashcard -> {
                    showAddEditFlashcard(AddEditFlashcardFragment.newFlashcardId)
                    drawerLayout.closeDrawers()
                    return@setNavigationItemSelectedListener true
                }
                else -> false
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when(item?.itemId) {
            android.R.id.home -> {
                drawerLayout.openDrawer(GravityCompat.START)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
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

    /**
     * Change view to AddEditFlashcardFragment
     * @param flashcardId id of Flashcard to be shown
     */
    override fun showAddEditFlashcard(flashcardId: String) {
        addEditFlashcardFragment = AddEditFlashcardFragment.newInstance(flashcardId)
        replaceFragment(addEditFlashcardFragment)
    }

    /**
     * Change view to FlashcardListFragment
     * @param flashcardId id of Flashcard to be shown in detail view
     */
    override fun showFlashcardList(flashcardId: String) {
        flashcardListFragment = FlashcardListFragment.newInstance(flashcardId)
        replaceFragment(flashcardListFragment)
    }

    /**
     * Change view to RandomFlashcardFragment (a.k.a. the game)
     */
    override fun showRandomFlashcard() {
        replaceFragment(randomFlashcardFragment)
    }
}
