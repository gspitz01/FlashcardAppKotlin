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

import com.gregspitz.flashcardappkotlin.addeditflashcard.AddEditFlashcardFragment
import com.gregspitz.flashcardappkotlin.categorylist.CategoryListFragment
import com.gregspitz.flashcardappkotlin.data.source.RepoComponent
import com.gregspitz.flashcardappkotlin.flashcarddownload.FlashcardDownloadFragment
import com.gregspitz.flashcardappkotlin.flashcardlist.FlashcardListFragment
import com.gregspitz.flashcardappkotlin.randomflashcard.RandomFlashcardFragment
import dagger.Component

/**
 * Dagger component for use case injection
 */
@UseCaseScope
@Component(modules = [(UseCaseModule::class)], dependencies = [(RepoComponent::class)])
interface UseCaseComponent {

    fun inject(addEditFlashcardFragment: AddEditFlashcardFragment)

    fun inject(flashcardListFragment: FlashcardListFragment)

    fun inject(randomFlashcardFragment: RandomFlashcardFragment)

    fun inject(flashcardDownloadFragment: FlashcardDownloadFragment)

    fun inject(categoryListFragment: CategoryListFragment)
}
