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

package com.gregspitz.flashcardappkotlin.addeditflashcard

import android.content.Intent
import android.os.Bundle
import android.support.annotation.VisibleForTesting
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.gregspitz.flashcardappkotlin.FlashcardApplication
import com.gregspitz.flashcardappkotlin.R
import com.gregspitz.flashcardappkotlin.UseCaseHandler
import com.gregspitz.flashcardappkotlin.addeditflashcard.domain.usecase.GetFlashcard
import com.gregspitz.flashcardappkotlin.addeditflashcard.domain.usecase.SaveFlashcard
import com.gregspitz.flashcardappkotlin.data.model.Flashcard
import com.gregspitz.flashcardappkotlin.flashcardlist.FlashcardListActivity
import kotlinx.android.synthetic.main.activity_add_edit_flashcard.*
import javax.inject.Inject

class AddEditFlashcardActivity : AppCompatActivity(), AddEditFlashcardContract.View {

    private lateinit var presenter: AddEditFlashcardContract.Presenter

    @Inject
    lateinit var getFlashcard: GetFlashcard

    @Inject
    lateinit var  saveFlashcard: SaveFlashcard

    @Inject
    lateinit var useCaseHandler: UseCaseHandler

    private var flashcard: Flashcard? = null

    private var active = false

    private var toast: Toast? = null

    init {
        FlashcardApplication.useCaseComponent.inject(this)
    }

    companion object {
        const val flashcardIdExtra = "flashcard_id_extra"
        const val newFlashcardExtra = "-1"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_flashcard)

        AddEditFlashcardPresenter(useCaseHandler, this, getFlashcard, saveFlashcard)
    }

    override fun onResume() {
        super.onResume()
        active = true
        presenter.start()
    }

    override fun onPause() {
        super.onPause()
        active = false
    }

    override fun setPresenter(presenter: AddEditFlashcardContract.Presenter) {
        this.presenter = presenter
        saveFlashcardButton.setOnClickListener {
            val front = flashcardEditFront.text.toString()
            val back = flashcardEditBack.text.toString()
            flashcard = if (flashcard != null) {
                Flashcard(flashcard!!.id, front, back)
            } else {
                Flashcard(front = front, back = back)
            }
            this@AddEditFlashcardActivity.presenter.saveFlashcard(flashcard!!)
        }

        showFlashcardListButton.setOnClickListener { this@AddEditFlashcardActivity.presenter.showList() }
    }

    override fun setLoadingIndicator(active: Boolean) {
        // TODO: fill this in
    }

    override fun getIdFromIntent(): String {
        return intent.getStringExtra(flashcardIdExtra) ?: newFlashcardExtra
    }

    override fun showFlashcard(flashcard: Flashcard) {
        this.flashcard = flashcard
        flashcardEditFront.setText(flashcard.front)
        flashcardEditBack.setText(flashcard.back)
    }

    override fun showFlashcardList(flashcardId: String) {
        val intent = Intent(this, FlashcardListActivity::class.java)
        var extraId = flashcardId
        if (extraId == newFlashcardExtra) {
            extraId = FlashcardListActivity.noParticularFlashcardExtra
        }
        intent.putExtra(FlashcardListActivity.flashcardIdExtra, extraId)
        startActivity(intent)
    }

    override fun showFailedToLoadFlashcard() {
        failedToLoadFlashcard.visibility = View.VISIBLE
    }

    override fun showNewFlashcard() {
        flashcardEditFront.setText("")
        flashcardEditBack.setText("")
    }

    override fun showSaveSuccessful() {
        toast = Toast.makeText(this, R.string.save_successful_toast_text, Toast.LENGTH_LONG)
        toast?.show()
    }

    override fun showSaveFailed() {
        toast = Toast.makeText(this, R.string.save_failed_toast_text, Toast.LENGTH_LONG)
        toast?.show()
    }

    override fun isActive(): Boolean {
        return active
    }

    @VisibleForTesting
    fun getToast(): Toast? {
        return toast
    }
}
