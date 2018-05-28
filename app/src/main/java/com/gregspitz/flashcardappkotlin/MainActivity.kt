package com.gregspitz.flashcardappkotlin

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.view.MenuItem
import com.gregspitz.flashcardappkotlin.addeditflashcard.AddEditFlashcardFragment
import com.gregspitz.flashcardappkotlin.flashcardlist.FlashcardListFragment
import com.gregspitz.flashcardappkotlin.randomflashcard.RandomFlashcardFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), MainFragmentRouter {

    private val randomFlashcardFragment = RandomFlashcardFragment.newInstance()
    private var flashcardListFragment: FlashcardListFragment? = null
    private var addEditFlashcardFragment: AddEditFlashcardFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp)
        }

        // Start with the game
        setFragment(randomFlashcardFragment)

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
                    showAddEditFlashcard(AddEditFlashcardFragment.newFlashcardExtra)
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
        if (flashcardListFragment == null) {
            flashcardListFragment = FlashcardListFragment.newInstance(flashcardId)
        } else {
            flashcardListFragment?.setDetailView(flashcardId)
        }
        replaceFragment(flashcardListFragment!!)
    }

    override fun showRandomFlashcard() {
        replaceFragment(randomFlashcardFragment)
    }
}
