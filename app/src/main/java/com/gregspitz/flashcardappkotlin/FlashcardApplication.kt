package com.gregspitz.flashcardappkotlin

import android.app.Application
import com.gregspitz.flashcardappkotlin.data.source.DaggerRepoComponent
import com.gregspitz.flashcardappkotlin.data.source.RepoComponent
import com.gregspitz.flashcardappkotlin.data.source.RepoModule
import com.gregspitz.flashcardappkotlin.di.*

/**
 * The main application class
 */
class FlashcardApplication : Application() {

    companion object {
        @JvmStatic lateinit var repoComponent: RepoComponent

        @JvmStatic lateinit var useCaseComponent: UseCaseComponent
    }

    override fun onCreate() {
        super.onCreate()

        repoComponent = DaggerRepoComponent.builder()
                .repoModule(RepoModule())
                .build()

        useCaseComponent = DaggerUseCaseComponent.builder()
                .repoComponent(repoComponent)
                .useCaseModule(UseCaseModule())
                .build()
    }
}
