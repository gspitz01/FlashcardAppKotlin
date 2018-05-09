package com.gregspitz.flashcardappkotlin.flashcardlist

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gregspitz.flashcardappkotlin.R
import com.gregspitz.flashcardappkotlin.data.model.Flashcard
import kotlinx.android.synthetic.main.fragment_flashcard_detail.*

class FlashcardDetailFragment : Fragment() {

    private var flashcard: Flashcard? = null

    companion object {
        const val flashcardBundleId = "flashcard_bundle_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        flashcard = arguments?.getParcelable(flashcardBundleId)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_flashcard_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        flashcardFront.text = flashcard?.front
        flashcardBack.text = flashcard?.back
        editFlashcardButton.setOnClickListener {
            if (flashcard != null) {
                (activity as FlashcardListActivity).showEditFlashcard(flashcard!!.id)
            }
        }
    }
}
