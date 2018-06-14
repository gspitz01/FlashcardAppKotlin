package com.gregspitz.flashcardappkotlin.data.source

import com.gregspitz.flashcardappkotlin.data.model.Category
import com.gregspitz.flashcardappkotlin.data.model.Flashcard
import com.gregspitz.flashcardappkotlin.data.service.FlashcardDownloadService
import com.gregspitz.flashcardappkotlin.data.service.model.DownloadCategory

class FakeFlashcardDownloadService : FlashcardDownloadService {

    private val downloadCategories = mutableListOf<DownloadCategory>()
    var categoriesFailure = false
    private val flashcards = mutableListOf<Flashcard>()
    var flashcardFailure = false

    override fun getDownloadCategories(callback: FlashcardDownloadService.GetDownloadCategoriesCallback) {
        if (categoriesFailure) {
            callback.onDataNotAvailable()
            return
        }
        callback.onCategoriesLoaded(downloadCategories)
    }

    override fun downloadFlashcardsByCategory(categories: List<Category>, callback: FlashcardDownloadService.DownloadFlashcardsCallback) {
        if (flashcardFailure) {
            callback.onDataNotAvailable()
            return
        }
        callback.onFlashcardsDownloaded(flashcards)
    }

    fun deleteAll() {
        downloadCategories.clear()
        flashcards.clear()
        categoriesFailure = false
        flashcardFailure = false
    }

    fun addDownloadCategories(downloadCategories: List<DownloadCategory>) {
        this.downloadCategories.addAll(downloadCategories)
    }

}
