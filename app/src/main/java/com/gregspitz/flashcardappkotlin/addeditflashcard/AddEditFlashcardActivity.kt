package com.gregspitz.flashcardappkotlin.addeditflashcard

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.gregspitz.flashcardappkotlin.R

class AddEditFlashcardActivity : AppCompatActivity() {

    companion object {
        const val flashcardIdExtra = "flashcard_id_extra"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_flashcard)
    }
}
