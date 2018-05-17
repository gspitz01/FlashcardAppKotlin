package com.gregspitz.flashcardappkotlin

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.RestrictTo
import android.support.v4.app.Fragment
import android.view.ViewGroup
import android.widget.FrameLayout

/**
 * Container for testing fragments
 */
@RestrictTo(RestrictTo.Scope.TESTS)
class SingleFragmentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val content = FrameLayout(this)
        content.layoutParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT)
        content.id = R.id.contentFrame
        setContentView(content)
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
}
