package com.gregspitz.flashcardappkotlin

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.gregspitz.flashcardappkotlin.flashcardlist.FlashcardListActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        flashcardListButton.setOnClickListener(object: View.OnClickListener {
            override fun onClick(v: View?) {
                startActivity(Intent(this@MainActivity,
                        FlashcardListActivity::class.java))
            }
        })
    }
}
