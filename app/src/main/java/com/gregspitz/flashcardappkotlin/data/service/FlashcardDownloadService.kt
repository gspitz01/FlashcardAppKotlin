package com.gregspitz.flashcardappkotlin.data.service

import com.gregspitz.flashcardappkotlin.data.model.Category
import com.gregspitz.flashcardappkotlin.data.model.Flashcard

interface FlashcardDownloadService {

    interface GetDownloadCategoriesCallback {

        fun onCategoriesLoaded(categoryToNumberOfFlashcardsMap: Map<Category, Int>)

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
