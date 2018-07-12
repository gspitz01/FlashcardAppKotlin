/*
 * Copyright (C) 2018 Greg Spitz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gregspitz.flashcardappkotlin.di

import com.gregspitz.flashcardappkotlin.UseCaseHandler
import com.gregspitz.flashcardappkotlin.UseCaseScheduler
import com.gregspitz.flashcardappkotlin.UseCaseThreadPoolScheduler
import com.gregspitz.flashcardappkotlin.addeditflashcard.domain.usecase.DeleteFlashcard
import com.gregspitz.flashcardappkotlin.addeditflashcard.domain.usecase.GetFlashcard
import com.gregspitz.flashcardappkotlin.addeditflashcard.domain.usecase.SaveFlashcard
import com.gregspitz.flashcardappkotlin.categorylist.domain.usecase.GetCategories
import com.gregspitz.flashcardappkotlin.data.service.FlashcardDownloadService
import com.gregspitz.flashcardappkotlin.data.source.FlashcardRepository
import com.gregspitz.flashcardappkotlin.flashcarddownload.domain.usecase.DownloadFlashcards
import com.gregspitz.flashcardappkotlin.flashcarddownload.domain.usecase.GetDownloadCategories
import com.gregspitz.flashcardappkotlin.flashcarddownload.domain.usecase.SaveFlashcards
import com.gregspitz.flashcardappkotlin.flashcardlist.domain.usecase.DeleteFlashcards
import com.gregspitz.flashcardappkotlin.flashcardlist.domain.usecase.GetFlashcards
import com.gregspitz.flashcardappkotlin.randomflashcard.domain.model.FlashcardPriorityProbabilityDistribution
import com.gregspitz.flashcardappkotlin.randomflashcard.domain.usecase.GetRandomFlashcard
import dagger.Module
import dagger.Provides

/**
 * Dagger module for use case injection
 */
@Module
class UseCaseModule {

    @Provides
    fun provideUseCaseThreadPoolScheduler() : UseCaseScheduler {
        return UseCaseThreadPoolScheduler()
    }

    @Provides
    fun provideUseCaseHandler(useCaseScheduler: UseCaseScheduler) : UseCaseHandler {
        return UseCaseHandler(useCaseScheduler)
    }

    @Provides
    fun provideGetFlashcard(flashcardRepository: FlashcardRepository) : GetFlashcard {
        return GetFlashcard(flashcardRepository)
    }

    @Provides
    fun provideGetFlashcards(flashcardRepository: FlashcardRepository) : GetFlashcards {
        return GetFlashcards(flashcardRepository)
    }

    @Provides
    fun provideGetCategories(flashcardRepository: FlashcardRepository) : GetCategories {
        return GetCategories(flashcardRepository)
    }

    @Provides
    fun provideSaveFlashcard(flashcardRepository: FlashcardRepository) : SaveFlashcard {
        return SaveFlashcard(flashcardRepository)
    }

    @Provides
    fun provideSaveFlashcards(flashcardRepository: FlashcardRepository) : SaveFlashcards {
        return SaveFlashcards(flashcardRepository)
    }

    @Provides
    fun provideDeleteFlashcard(flashcardRepository: FlashcardRepository): DeleteFlashcard {
        return DeleteFlashcard(flashcardRepository)
    }

    @Provides
    fun provideDeleteFlashcards(flashcardRepository: FlashcardRepository): DeleteFlashcards {
        return DeleteFlashcards(flashcardRepository)
    }

    @Provides
    fun provideGetRandomFlashcard(flashcardRepository: FlashcardRepository) : GetRandomFlashcard {
        return GetRandomFlashcard(flashcardRepository,
                FlashcardPriorityProbabilityDistribution(0.48, 0.23,
                        0.15, 0.09, 0.05))
    }

    @Provides
    fun provideDownloadFlashcards(flashcardDownloadService: FlashcardDownloadService)
            : DownloadFlashcards {
        return DownloadFlashcards(flashcardDownloadService)
    }

    @Provides
    fun provideGetDownloadCategories(flashcardDownloadService: FlashcardDownloadService)
        : GetDownloadCategories {
        return GetDownloadCategories(flashcardDownloadService)
    }
}
