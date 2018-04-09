package com.gregspitz.flashcardappkotlin

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.gregspitz.flashcardappkotlin.flashcardlist.FlashcardListActivity
import com.gregspitz.flashcardappkotlin.randomflashcard.RandomFlashcardActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        flashcardListButton.setOnClickListener {
            startActivity(Intent(this@MainActivity,
                    FlashcardListActivity::class.java))
        }

        flashcardGameButton.setOnClickListener {
            startActivity(Intent(this@MainActivity,
                    RandomFlashcardActivity::class.java))
        }
    }
}
