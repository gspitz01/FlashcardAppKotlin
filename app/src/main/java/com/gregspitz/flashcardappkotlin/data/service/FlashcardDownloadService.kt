package com.gregspitz.flashcardappkotlin.data.service

import com.gregspitz.flashcardappkotlin.data.model.Category
import com.gregspitz.flashcardappkotlin.data.model.Flashcard
import com.gregspitz.flashcardappkotlin.data.service.model.DownloadCategory

interface FlashcardDownloadService {

    interface GetDownloadCategoriesCallback {

        fun onCategoriesLoaded(downloadCategories: List<DownloadCategory>)

        fun onDataNotAvailable()
    }

    fun getDownloadCategories(callback: GetDownloadCategoriesCallback)

    interface DownloadFlashcardsCallback {

        fun onFlashcardsDownloaded(flashcards: List<Flashcard>)

        fun onDataNotAvailable()
    }

    fun downloadFlashcardsByCategory(categories: List<Category>,
                                     callback: DownloadFlashcardsCallback)
}
