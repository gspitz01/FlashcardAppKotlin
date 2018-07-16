package com.gregspitz.flashcardappkotlin

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.gregspitz.flashcardappkotlin.addeditflashcard.AddEditFlashcardFragment
import com.gregspitz.flashcardappkotlin.categorylist.CategoryListFragment
import com.gregspitz.flashcardappkotlin.flashcarddownload.FlashcardDownloadFragment
import com.gregspitz.flashcardappkotlin.flashcardlist.FlashcardListFragment
import com.gregspitz.flashcardappkotlin.randomflashcard.RandomFlashcardFragment
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.CropCircleTransformation
import kotlinx.android.synthetic.main.activity_main.*

private const val RC_SIGN_IN = 1

/**
 * The only Activity which holds all the Fragment views
 * Serves as the router
 */
class MainActivity : AppCompatActivity(), MainFragmentRouter {

    // Start with a AddEditFlashcardFragment
    private var addEditFlashcardFragment =
            AddEditFlashcardFragment.newInstance(AddEditFlashcardFragment.newFlashcardId)
    private lateinit var randomFlashcardFragment: RandomFlashcardFragment
    private lateinit var flashcardListFragment: FlashcardListFragment
    private lateinit var categoryListFragment: CategoryListFragment
    private var flashcardDownloadFragment: FlashcardDownloadFragment? = null

    // Specially initialize the nav header views
    private lateinit var navHeaderText: TextView
    private lateinit var userProfileImage: ImageView
    private lateinit var signOutButton: Button

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
                    showRandomFlashcard(null)
                    drawerLayout.closeDrawers()
                    return@setNavigationItemSelectedListener true
                }
                R.id.navList -> {
                    showFlashcardList(FlashcardListFragment.noParticularFlashcardExtra)
                    drawerLayout.closeDrawers()
                    return@setNavigationItemSelectedListener true
                }
                R.id.navCategoryList -> {
                    showCategoryList()
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

        // Force nav header to instantiate
        val navHeader = navDrawer.getHeaderView(0)
        navHeaderText = navHeader.findViewById(R.id.navHeaderText)
        userProfileImage = navHeader.findViewById(R.id.userProfileImg)
        signOutButton = navHeader.findViewById(R.id.signOutButton)
        signOutButton.setOnClickListener {
            signOut()
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
        if (googleSignInAccount != null) {
            updateNavViewForSignedIn()
        }
    }

    override fun onBackPressed() {
        // If nav drawer is open, close it
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers()
        } else {
            super.onBackPressed()
        }
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
            // Successful sign in, update nav view and show FlashcardDownload
            updateNavViewForSignedIn()
            showFlashcardDownload()
        } catch (ex: ApiException) {
            // Failed sign in, show AddEditFlashcard
            showAddEditFlashcard(AddEditFlashcardFragment.newFlashcardId)
            Toast.makeText(this, R.string.sign_in_failed_text, Toast.LENGTH_LONG).show()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            android.R.id.home -> {
                closeSoftKeyboard()
                drawerLayout.openDrawer(GravityCompat.START)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
                .add(R.id.contentFrame, fragment)
                .commit()
    }

    private fun replaceFragment(fragment: Fragment) {
        closeSoftKeyboard()
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
     * Change view to FlashcardListFragment for a particular Category
     * @param categoryName the name of the Category
     */
    override fun showCategoryFlashcardList(categoryName: String) {
        flashcardListFragment = FlashcardListFragment
                .newInstance(FlashcardListFragment.noParticularFlashcardExtra, categoryName)
        replaceFragment(flashcardListFragment)
    }

    /**
     * Change view to CategoryListFragment
     */
    override fun showCategoryList() {
        categoryListFragment = CategoryListFragment.newInstance()
        replaceFragment(categoryListFragment)
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
            replaceFragment(flashcardDownloadFragment!!)
        }
    }

    /**
     * Change view to RandomFlashcardFragment (a.k.a. the game)
     */
    override fun showRandomFlashcard(categoryName: String?) {
        randomFlashcardFragment = RandomFlashcardFragment.newInstance(categoryName)
        replaceFragment(randomFlashcardFragment)
    }

    private fun closeSoftKeyboard() {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE)
                as InputMethodManager
        var currentView = currentFocus
        if (currentView == null) {
            // In case there is no currently focused view, create one
            currentView = View(this)
        }
        inputMethodManager.hideSoftInputFromWindow(currentView.windowToken, 0)
    }

    /**
     * Google Sign-In
     */
    private fun signIn() {
        startActivityForResult(googleSignInClient.signInIntent, RC_SIGN_IN)
    }

    private fun signOut() {
        googleSignInClient.signOut()
                .addOnCompleteListener(this) {
                    googleSignInAccount = null
                    updateNavViewForSignedOut()
                }
    }

    private fun updateNavViewForSignedIn() {
        googleSignInAccount?.let {
            it.displayName?.let {
                navHeaderText.text = it
            }
            it.photoUrl?.let {
                userProfileImage.visibility = View.VISIBLE
                Picasso.get().load(it)
                        .resize(resources.getDimensionPixelSize(R.dimen.user_profile_img_nav_width),
                                resources.getDimensionPixelSize(R.dimen.user_profile_img_nav_height))
                        .transform(CropCircleTransformation())
                        .into(userProfileImage)
            }
        }
        signOutButton.visibility = View.VISIBLE
    }

    private fun updateNavViewForSignedOut() {
        navHeaderText.text = getString(R.string.app_name)
        userProfileImage.setImageBitmap(null)
        userProfileImage.setImageDrawable(null)
        userProfileImage.visibility = View.GONE
        signOutButton.visibility = View.GONE

        // If current fragment is FlashcardDownload, move to AddEditFlashcardFragment
        if (supportFragmentManager.findFragmentById(R.id.contentFrame)
                        is FlashcardDownloadFragment) {
            showAddEditFlashcard(AddEditFlashcardFragment.newFlashcardId)
        }
    }
}
