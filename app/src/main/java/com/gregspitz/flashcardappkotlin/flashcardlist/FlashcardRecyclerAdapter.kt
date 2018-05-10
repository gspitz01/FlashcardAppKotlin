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

package com.gregspitz.flashcardappkotlin.flashcardlist

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gregspitz.flashcardappkotlin.R
import com.gregspitz.flashcardappkotlin.data.model.Flashcard
import kotlinx.android.synthetic.main.flashcard_list_holder.view.*

/**
 * Recycler adapter to hold a list of Flashcards
 */
class FlashcardRecyclerAdapter(private var flashcards: List<Flashcard>)
    : RecyclerView.Adapter<FlashcardRecyclerAdapter.Holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.flashcard_list_holder,
                parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.setFlashcard(flashcards[position])
    }

    private lateinit var presenter: FlashcardListContract.Presenter

    override fun getItemCount(): Int = flashcards.size

    fun updateFlashcards(flashcards: List<Flashcard>) {
        this.flashcards = flashcards
        notifyDataSetChanged()
    }

    fun setPresenter(presenter: FlashcardListContract.Presenter) {
        this.presenter = presenter
    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun setFlashcard(flashcard: Flashcard) {
            itemView.flashcardFront.text = flashcard.front
            itemView.setOnClickListener {
                presenter.onFlashcardClick(adapterPosition)
            }
        }
    }

}
