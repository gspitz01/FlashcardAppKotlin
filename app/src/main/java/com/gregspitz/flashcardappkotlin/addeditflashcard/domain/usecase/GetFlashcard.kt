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

package com.gregspitz.flashcardappkotlin.addeditflashcard.domain.usecase

import com.gregspitz.flashcardappkotlin.UseCase
import com.gregspitz.flashcardappkotlin.data.model.Flashcard
import com.gregspitz.flashcardappkotlin.data.source.FlashcardDataSource
import com.gregspitz.flashcardappkotlin.data.source.FlashcardRepository
import javax.inject.Inject

/**
 * UseCase for getting a single Flashcard
 */
class GetFlashcard @Inject constructor(private val flashcardRepository: FlashcardRepository)
    : UseCase<GetFlashcard.RequestValues, GetFlashcard.ResponseValue>() {

    override fun executeUseCase(requestValues: RequestValues) {

        flashcardRepository.getFlashcard(requestValues.flashcardId,
                object: FlashcardDataSource.GetFlashcardCallback {
                    override fun onFlashcardLoaded(flashcard: Flashcard) {
                        getUseCaseCallback().onSuccess(ResponseValue(flashcard))
                    }

                    override fun onDataNotAvailable() {
                        getUseCaseCallback().onError()
                    }

                })
    }

    class RequestValues(val flashcardId: String) : UseCase.RequestValues

    class ResponseValue(val flashcard: Flashcard) : UseCase.ResponseValue
}
