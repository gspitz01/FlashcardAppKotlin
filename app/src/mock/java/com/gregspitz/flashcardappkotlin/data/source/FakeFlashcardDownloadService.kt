package com.gregspitz.flashcardappkotlin.data.source

import com.gregspitz.flashcardappkotlin.data.model.Category
import com.gregspitz.flashcardappkotlin.data.model.Flashcard
import com.gregspitz.flashcardappkotlin.data.service.FlashcardDownloadService
import com.gregspitz.flashcardappkotlin.data.service.model.DownloadCategory

class FakeFlashcardDownloadService : FlashcardDownloadService {

    private val downloadCategories =
            mutableListOf<DownloadCategory>(DownloadCategory("Barf", 4),
            DownloadCategory("Junk", 5), DownloadCategory("Argument", 9))
    var categoriesFailure = false
    private val flashcards = mutableListOf<Flashcard>()
    var flashcardFailure = false
    // Categories that were asked to be downloaded
    val attemptedDownloadCategories = mutableListOf<DownloadCategory>()

    override fun getDownloadCategories(callback: FlashcardDownloadService.GetDownloadCategoriesCallback) {
        if (categoriesFailure) {
            callback.onDataNotAvailable()
            return
        }
        callback.onCategoriesLoaded(downloadCategories)
    }

    override fun downloadFlashcardsByCategory(
            categories: List<DownloadCategory>,
            callback: FlashcardDownloadService.DownloadFlashcardsCallback) {

        attemptedDownloadCategories.addAll(categories)
        if (flashcardFailure) {
            callback.onDataNotAvailable()
            return
        }
        callback.onFlashcardsDownloaded(flashcards)
    }

    fun deleteAll() {
        downloadCategories.clear()
        flashcards.clear()
        attemptedDownloadCategories.clear()
        categoriesFailure = false
        flashcardFailure = false
    }

    fun addDownloadCategories(downloadCategories: List<DownloadCategory>) {
        this.downloadCategories.addAll(downloadCategories)
    }
}
