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

package com.gregspitz.flashcardappkotlin.randomflashcard

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.gregspitz.flashcardappkotlin.FlashcardApplication
import com.gregspitz.flashcardappkotlin.R
import com.gregspitz.flashcardappkotlin.R.id.flashcardSide
import com.gregspitz.flashcardappkotlin.R.id.nextFlashcardButton
import com.gregspitz.flashcardappkotlin.UseCaseHandler
import com.gregspitz.flashcardappkotlin.data.model.Flashcard
import com.gregspitz.flashcardappkotlin.data.model.FlashcardSide
import com.gregspitz.flashcardappkotlin.randomflashcard.domain.usecase.GetRandomFlashcard
import kotlinx.android.synthetic.main.activity_random_flashcard.*
import javax.inject.Inject

class RandomFlashcardActivity : AppCompatActivity(), RandomFlashcardContract.View {

    private lateinit var presenter: RandomFlashcardContract.Presenter

    @Inject
    lateinit var getRandomFlashcard: GetRandomFlashcard

    @Inject
    lateinit var useCaseHandler: UseCaseHandler

    private lateinit var viewModel: RandomFlashcardViewModel

    private var active = false

    init {
        FlashcardApplication.useCaseComponent.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_random_flashcard)

        viewModel = ViewModelProviders.of(this).get(RandomFlashcardViewModel::class.java)

        val randomFlashcardObserver = Observer<Flashcard> {
            when(viewModel.flashcardSide.value) {
                FlashcardSide.FRONT -> flashcardSide.text = it?.front
                FlashcardSide.BACK -> flashcardSide.text = it?.back
            }
        }

        val flashcardSideObserver = Observer<FlashcardSide> {
            when(it) {
                FlashcardSide.FRONT -> flashcardSide.text = viewModel.randomFlashcard.value?.front
                FlashcardSide.BACK -> flashcardSide.text = viewModel.randomFlashcard.value?.back
            }
        }

        viewModel.randomFlashcard.observe(this, randomFlashcardObserver)
        viewModel.flashcardSide.observe(this, flashcardSideObserver)

        RandomFlashcardPresenter(useCaseHandler, this, viewModel, getRandomFlashcard)
    }

    override fun onResume() {
        super.onResume()
        if (viewModel.randomFlashcard.value == null) {
            presenter.start()
        }
        active = true
    }

    override fun onPause() {
        super.onPause()
        active = false
    }

    override fun setPresenter(presenter: RandomFlashcardContract.Presenter) {
        this.presenter = presenter
        flashcardSide.setOnClickListener {
            this@RandomFlashcardActivity.presenter.turnFlashcard() }

        nextFlashcardButton.setOnClickListener { this@RandomFlashcardActivity.presenter.loadNewFlashcard() }
    }

    override fun setLoadingIndicator(active: Boolean) {
        // TODO: implement
    }

    override fun showFailedToLoadFlashcard() {
        flashcardSide.setText(R.string.failed_to_load_flashcard_text)
    }

    override fun isActive(): Boolean = active
}
