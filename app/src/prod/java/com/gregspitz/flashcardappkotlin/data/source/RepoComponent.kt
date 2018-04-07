package com.gregspitz.flashcardappkotlin.data.source

import com.gregspitz.flashcardappkotlin.Injection
import com.gregspitz.flashcardappkotlin.di.AppModule
import dagger.Component
import javax.inject.Singleton

/**
 * Dagger component for the FlashcardRepository
 */
@Singleton
@Component(modules = [(AppModule::class), (RepoModule::class)])
interface RepoComponent {

    fun inject(injection: Injection)

}
