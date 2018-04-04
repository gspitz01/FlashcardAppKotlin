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

    private lateinit var presenter: FlashcardListContract.Presenter

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): Holder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.flashcard_list_holder,
                parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder?, position: Int) {
        holder?.setFlashcard(flashcards.get(position))
    }

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
            itemView.flashcard_front.text = flashcard.front
            itemView.setOnClickListener(object: View.OnClickListener {
                override fun onClick(v: View?) {
                    presenter.onFlashcardClick(flashcards[adapterPosition].id)
                }

            })
        }
    }

}
