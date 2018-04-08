package com.gregspitz.flashcardappkotlin.data.source

import android.support.annotation.VisibleForTesting
import com.gregspitz.flashcardappkotlin.di.AppModule
import dagger.Component
import javax.inject.Singleton

/**
 * Dagger component for FlashcardRepository
 */
@Singleton
@Component(modules = [(AppModule::class), (RepoModule::class)])
interface RepoComponent {

    fun exposeRepository() : FlashcardRepository

    @VisibleForTesting
    fun exposeLocalDataSource() : FakeFlashcardLocalDataSource
}
