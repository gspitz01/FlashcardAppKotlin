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
import com.gregspitz.flashcardappkotlin.data.model.Category
import com.gregspitz.flashcardappkotlin.data.model.Flashcard
import com.gregspitz.flashcardappkotlin.data.model.FlashcardListItem
import kotlinx.android.synthetic.main.category_list_holder.view.*
import kotlinx.android.synthetic.main.flashcard_list_holder.view.*

/**
 * Recycler adapter to hold a list of FlashcardListItems
 */
class FlashcardRecyclerAdapter(private var flashcards: List<FlashcardListItem>)
    : RecyclerView.Adapter<FlashcardRecyclerAdapter.Holder>() {

    private val VIEW_TYPE_CATEGORY = 0
    private val VIEW_TYPE_FLASHCARD = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return when (viewType) {
            VIEW_TYPE_FLASHCARD -> {
                // An actual Flashcard
                val view = LayoutInflater.from(parent.context).inflate(R.layout.flashcard_list_holder,
                        parent, false)
                FlashcardHolder(view)
            }
            else -> {
                // A Category header
                val view = LayoutInflater.from(parent.context).inflate(R.layout.category_list_holder,
                        parent, false)
                CategoryHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        when (holder.itemViewType) {
            VIEW_TYPE_FLASHCARD -> (holder as FlashcardHolder)
                    .setFlashcard(flashcards[position] as Flashcard)
            else -> (holder as CategoryHolder).setCategory(flashcards[position] as Category)
        }

    }

    private lateinit var presenter: FlashcardListContract.Presenter

    override fun getItemCount(): Int = flashcards.size

    override fun getItemViewType(position: Int): Int {
        return when (flashcards[position]) {
            is Flashcard -> VIEW_TYPE_FLASHCARD
            else -> VIEW_TYPE_CATEGORY
        }
    }

    fun updateFlashcards(flashcards: List<FlashcardListItem>) {
        this.flashcards = flashcards
        notifyDataSetChanged()
    }

    fun setPresenter(presenter: FlashcardListContract.Presenter) {
        this.presenter = presenter
    }

    open inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView)

    /**
     * ViewHolder for an actual Flashcard
     */
    inner class FlashcardHolder(itemView: View) : Holder(itemView) {
        fun setFlashcard(flashcard: Flashcard) {
            itemView.flashcardFront.text = flashcard.front
            itemView.setOnClickListener {
                presenter.onFlashcardClick(adapterPosition)
            }
        }
    }

    /**
     * ViewHolder for a CategoryHeader
     */
    inner class CategoryHolder(itemView: View) : Holder(itemView) {
        fun setCategory(category: Category) {
            itemView.categoryName.text = category.name
            itemView.setOnClickListener {
                presenter.onCategoryClick(adapterPosition)
            }
        }
    }

}
