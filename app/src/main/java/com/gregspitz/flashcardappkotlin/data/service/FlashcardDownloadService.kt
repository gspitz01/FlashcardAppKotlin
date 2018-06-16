package com.gregspitz.flashcardappkotlin.data.service

import com.gregspitz.flashcardappkotlin.data.service.model.DownloadCategory
import com.gregspitz.flashcardappkotlin.data.service.model.DownloadFlashcard

interface FlashcardDownloadService {

    interface GetDownloadCategoriesCallback {

        fun onCategoriesLoaded(downloadCategories: List<DownloadCategory>)

        fun onDataNotAvailable()
    }

    fun getDownloadCategories(callback: GetDownloadCategoriesCallback)

    interface DownloadFlashcardsCallback {

        fun onFlashcardsDownloaded(downloadFlashcards: List<DownloadFlashcard>)

        fun onDataNotAvailable()
    }

    fun downloadFlashcardsByCategory(category: DownloadCategory,
                                     callback: DownloadFlashcardsCallback)
}
