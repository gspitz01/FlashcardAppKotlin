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

package com.gregspitz.flashcardappkotlin

import android.app.Application
import com.gregspitz.flashcardappkotlin.data.source.DaggerRepoComponent
import com.gregspitz.flashcardappkotlin.data.source.RepoComponent
import com.gregspitz.flashcardappkotlin.data.source.RepoModule
import com.gregspitz.flashcardappkotlin.di.AppModule
import com.gregspitz.flashcardappkotlin.di.DaggerUseCaseComponent
import com.gregspitz.flashcardappkotlin.di.UseCaseComponent
import com.gregspitz.flashcardappkotlin.di.UseCaseModule

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
                .appModule(AppModule(this))
                .repoModule(RepoModule())
                .build()

        useCaseComponent = DaggerUseCaseComponent.builder()
                .repoComponent(repoComponent)
                .useCaseModule(UseCaseModule())
                .build()
    }
}
