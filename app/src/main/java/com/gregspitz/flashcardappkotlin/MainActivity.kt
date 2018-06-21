package com.gregspitz.flashcardappkotlin

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.gregspitz.flashcardappkotlin.addeditflashcard.AddEditFlashcardFragment
import com.gregspitz.flashcardappkotlin.flashcarddownload.FlashcardDownloadFragment
import com.gregspitz.flashcardappkotlin.flashcardlist.FlashcardListFragment
import com.gregspitz.flashcardappkotlin.randomflashcard.RandomFlashcardFragment
import kotlinx.android.synthetic.main.activity_main.*

private const val RC_SIGN_IN = 1

/**
 * The only Activity which holds all the Fragment views
 * Serves as the router
 */
class MainActivity : AppCompatActivity(), MainFragmentRouter {

    // TODO: Add tests for login
    // TODO: show something in display when user is logged in

    // Start with a AddEditFlashcardFragment
    private var addEditFlashcardFragment =
            AddEditFlashcardFragment.newInstance(AddEditFlashcardFragment.newFlashcardId)
    private val randomFlashcardFragment = RandomFlashcardFragment.newInstance()
    private lateinit var flashcardListFragment: FlashcardListFragment
    private lateinit var flashcardDownloadFragment: FlashcardDownloadFragment

    private lateinit var googleSignInClient: GoogleSignInClient
    private var googleSignInAccount: GoogleSignInAccount? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        // Switch from launcher theme to main theme
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp)
        }

        // Start with the game
        if (savedInstanceState == null) {
            setFragment(addEditFlashcardFragment)
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
                R.id.downloadFlashcardsList -> {
                    showFlashcardDownload()
                    drawerLayout.closeDrawers()
                    return@setNavigationItemSelectedListener true
                }
                else -> false
            }
        }

        // Google Sign-In
        val googleSignInOptions = GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)
    }

    override fun onStart() {
        super.onStart()
        googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>?) {
        try {
            googleSignInAccount = completedTask?.getResult(ApiException::class.java)
            // Successful sign in, show FlashcardDownload
            showFlashcardDownload()
        } catch (ex: ApiException) {
            // Failed sign in, show AddEditFlashcard
            showAddEditFlashcard(AddEditFlashcardFragment.newFlashcardId)
            Toast.makeText(this, R.string.sign_in_failed_text, Toast.LENGTH_LONG).show()
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
                .addToBackStack(null)
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
     * Change view to FlashcardDownloadFragment
     */
    override fun showFlashcardDownload() {
        // Download requires sign-in so check before changing fragments
        if (googleSignInAccount == null) {
            AlertDialog.Builder(this)
                    .setMessage(R.string.download_requires_sign_in_message)
                    .setTitle(R.string.sign_in_dialog_title)
                    .setPositiveButton(R.string.ok) { _, _ ->
                        signIn()
                    }
                    .setNegativeButton(R.string.no) { _, _ ->
                        showAddEditFlashcard(AddEditFlashcardFragment.newFlashcardId)
                    }
                    .create().show()
        } else {
            flashcardDownloadFragment = FlashcardDownloadFragment.newInstance()
            replaceFragment(flashcardDownloadFragment)
        }
    }

    /**
     * Change view to RandomFlashcardFragment (a.k.a. the game)
     */
    override fun showRandomFlashcard() {
        replaceFragment(randomFlashcardFragment)
    }

    /**
     * Google Sign-In
     */
    private fun signIn() {
        startActivityForResult(googleSignInClient.signInIntent, RC_SIGN_IN)
    }
}
