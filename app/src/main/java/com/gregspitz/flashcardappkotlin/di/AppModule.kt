package com.gregspitz.flashcardappkotlin.di

import android.app.Application
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Application Dagger module
 */
@Module
class AppModule(private val application: Application) {

    @Provides @Singleton
    fun provideApplication() : Application {
        return application
    }
}
