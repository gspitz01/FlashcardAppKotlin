/*
 * Copyright (C) 2018 Greg Spitz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gregspitz.flashcardappkotlin

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.gregspitz.flashcardappkotlin.R.id.flashcardGameButton
import com.gregspitz.flashcardappkotlin.R.id.flashcardListButton
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
