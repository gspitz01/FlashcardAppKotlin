package com.gregspitz.flashcardappkotlin.data.service

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.gregspitz.flashcardappkotlin.data.service.model.DownloadCategory
import com.gregspitz.flashcardappkotlin.data.service.model.DownloadFlashcard

class FirebaseFlashcardDownloadService : FlashcardDownloadService {

    // TODO: figure out how to test this

    private val database = FirebaseDatabase.getInstance()
    private val flashcardsRef = database.getReference("flashcards")
    private val categoriesRef = database.getReference("categories")

    override fun getDownloadCategories(callback: FlashcardDownloadService.GetDownloadCategoriesCallback) {
        categoriesRef.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                callback.onDataNotAvailable()
            }

            override fun onDataChange(p0: DataSnapshot) {
                val downloadCategories = mutableListOf<DownloadCategory>()
                for (category in p0.children) {
                    val cat = category.getValue(DownloadCategory::class.java)
                    cat?.let {
                        if (category.key != null) {
                            downloadCategories.add(DownloadCategory(it.name, it.count,
                                    category.key!!))
                        }
                    }
                }
                if (downloadCategories.size > 0) {
                    callback.onCategoriesLoaded(downloadCategories)
                } else {
                    callback.onDataNotAvailable()
                }
            }
        })
    }

    override fun downloadFlashcardsByCategory(
            category: DownloadCategory,
            callback: FlashcardDownloadService.DownloadFlashcardsCallback) {

        flashcardsRef.orderByChild("category_id").equalTo(category.id)
                .addListenerForSingleValueEvent(object: ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                        callback.onDataNotAvailable()
                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        val flashcards = mutableListOf<DownloadFlashcard>()
                        for (flashcard in p0.children) {
                            val card = flashcard.getValue(DownloadFlashcard::class.java)
                            card?.let {
                                if (flashcard.key != null) {
                                    flashcards.add(DownloadFlashcard(flashcard.key!!,
                                            card.category_name, card.category_id, card.front,
                                            card.back))
                                }
                            }
                        }
                        if (flashcards.size > 0) {
                            callback.onFlashcardsDownloaded(flashcards)
                        } else {
                            callback.onDataNotAvailable()
                        }
                    }
        })
    }
}
