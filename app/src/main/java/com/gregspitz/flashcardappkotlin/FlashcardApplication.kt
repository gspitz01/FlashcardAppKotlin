package com.gregspitz.flashcardappkotlin

import android.app.Application
import com.gregspitz.flashcardappkotlin.data.source.DaggerRepoComponent
import com.gregspitz.flashcardappkotlin.data.source.RepoModule
import com.gregspitz.flashcardappkotlin.di.AppModule
import com.gregspitz.flashcardappkotlin.data.source.RepoComponent

/**
 * The main application class
 */
class FlashcardApplication : Application() {

    companion object {
        @JvmStatic lateinit var repoComponent: RepoComponent
    }

    override fun onCreate() {
        super.onCreate()

        repoComponent = DaggerRepoComponent.builder()
                .appModule(AppModule(this))
                .repoModule(RepoModule())
                .build()
    }
}
