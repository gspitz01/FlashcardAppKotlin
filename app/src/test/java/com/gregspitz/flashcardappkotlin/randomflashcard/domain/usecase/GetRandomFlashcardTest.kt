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

package com.gregspitz.flashcardappkotlin.randomflashcard.domain.usecase

import com.gregspitz.flashcardappkotlin.TestData.FLASHCARD_1
import com.gregspitz.flashcardappkotlin.TestData.FLASHCARD_2
import com.gregspitz.flashcardappkotlin.TestData.FLASHCARD_LIST
import com.gregspitz.flashcardappkotlin.TestData.FLASHCARD_LIST_ONE_OF_EACH_PRIORITY
import com.gregspitz.flashcardappkotlin.TestData.FLASHCARD_LIST_SAME_IDS
import com.gregspitz.flashcardappkotlin.TestData.FLASHCARD_WITH_PRIORITY_HIGH
import com.gregspitz.flashcardappkotlin.TestData.FLASHCARD_WITH_PRIORITY_LOW
import com.gregspitz.flashcardappkotlin.TestData.FLASHCARD_WITH_PRIORITY_MEDIUM
import com.gregspitz.flashcardappkotlin.TestData.FLASHCARD_WITH_PRIORITY_NEW
import com.gregspitz.flashcardappkotlin.TestData.FLASHCARD_WITH_PRIORITY_URGENT
import com.gregspitz.flashcardappkotlin.TestData.SINGLE_FLASHCARD_LIST
import com.gregspitz.flashcardappkotlin.TestUseCaseScheduler
import com.gregspitz.flashcardappkotlin.UseCase
import com.gregspitz.flashcardappkotlin.UseCaseHandler
import com.gregspitz.flashcardappkotlin.data.model.Flashcard
import com.gregspitz.flashcardappkotlin.data.model.FlashcardPriority
import com.gregspitz.flashcardappkotlin.data.source.FlashcardDataSource
import com.gregspitz.flashcardappkotlin.data.source.FlashcardRepository
import com.gregspitz.flashcardappkotlin.randomflashcard.domain.model.FlashcardPriorityProbabilityDistribution
import com.nhaarman.mockito_kotlin.argumentCaptor
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test

/**
 * Tests for {@link GetRandomFlashcard}
 */
class GetRandomFlashcardTest {

    // Request values represents the id of the previous Flashcard
    // Null means there was no previous Flashcard
    private val requestValuesNull =
            GetRandomFlashcard.RequestValues(null)
    private val requestValuesFlashcard1 =
            GetRandomFlashcard.RequestValues(FLASHCARD_1.id)

    private val flashcardRepository: FlashcardRepository = mock()

    private val useCaseHandler = UseCaseHandler(TestUseCaseScheduler())

    private val repositoryCallbackCaptor =
            argumentCaptor<FlashcardDataSource.GetFlashcardsCallback>()

    private val callback: UseCase.UseCaseCallback<GetRandomFlashcard.ResponseValue> = mock()

    private val responseCaptor = argumentCaptor<GetRandomFlashcard.ResponseValue>()

    private val probabilityDistributionOnlyNew =
            FlashcardPriorityProbabilityDistribution(1.0, 0.0,
                    0.0, 0.0, 0.0)
    private val probabilityDistributionOnlyUrgent =
            FlashcardPriorityProbabilityDistribution(0.0, 1.0,
                    0.0, 0.0, 0.0)
    private val probabilityDistributionOnlyHigh =
            FlashcardPriorityProbabilityDistribution(0.0, 0.0,
                    1.0, 0.0, 0.0)
    private val probabilityDistributionOnlyMedium =
            FlashcardPriorityProbabilityDistribution(0.0, 0.0,
                    0.0, 1.0, 0.0)
    private val probabilityDistributionOnlyLow =
            FlashcardPriorityProbabilityDistribution(0.0, 0.0,
                    0.0, 0.0, 1.0)
    private val probabilityDistributionEven =
            FlashcardPriorityProbabilityDistribution(0.2, 0.2,
                    0.2, 0.2, 0.2)

    private lateinit var getRandomFlashcard: GetRandomFlashcard

    @Before
    fun setup() {
        getRandomFlashcard = GetRandomFlashcard(flashcardRepository, probabilityDistributionEven)
    }

    @Test
    fun `when only one flashcard and null previous flashcard, gets flashcard and calls success on callback`() {
        // Request value represents the previous Flashcard
        // In this null case, there was no previous Flashcard
        useCaseHandler.execute(getRandomFlashcard, requestValuesNull, callback)
        verify(flashcardRepository).getFlashcards(repositoryCallbackCaptor.capture())
        repositoryCallbackCaptor.firstValue.onFlashcardsLoaded(SINGLE_FLASHCARD_LIST)
        verify(callback).onSuccess(responseCaptor.capture())
        assertEquals(FLASHCARD_1, responseCaptor.firstValue.flashcard)
    }

    @Test
    fun `when only one flashcard and same previous flashcard, gets flashcard and calls success on callback`() {
        // This test is to make sure it doesn't run into an infinite loop of trying to find
        // a different Flashcard from the previous one when there is only one to be had.
        useCaseHandler.execute(getRandomFlashcard, requestValuesFlashcard1, callback)
        verify(flashcardRepository).getFlashcards(repositoryCallbackCaptor.capture())
        repositoryCallbackCaptor.firstValue.onFlashcardsLoaded(SINGLE_FLASHCARD_LIST)
        verify(callback).onSuccess(responseCaptor.capture())
        assertEquals(FLASHCARD_1, responseCaptor.firstValue.flashcard)
    }

    @Test
    fun `when all flashcards have same id, gets first flashcard in list and calls success on callback`() {
        useCaseHandler.execute(getRandomFlashcard, requestValuesFlashcard1, callback)
        verify(flashcardRepository).getFlashcards(repositoryCallbackCaptor.capture())
        repositoryCallbackCaptor.firstValue.onFlashcardsLoaded(FLASHCARD_LIST_SAME_IDS)
        verify(callback).onSuccess(responseCaptor.capture())
        assertEquals(FLASHCARD_1, responseCaptor.firstValue.flashcard)
    }

    @Test
    fun `gets flashcard different from previous and calls success on callback`() {
        useCaseHandler.execute(getRandomFlashcard, requestValuesFlashcard1, callback)
        verify(flashcardRepository).getFlashcards(repositoryCallbackCaptor.capture())
        repositoryCallbackCaptor.firstValue.onFlashcardsLoaded(FLASHCARD_LIST)
        verify(callback).onSuccess(responseCaptor.capture())
        assertEquals(FLASHCARD_2, responseCaptor.firstValue.flashcard)
    }

    @Test
    fun `when data not available, calls failure on callback`() {
        useCaseHandler.execute(getRandomFlashcard, requestValuesNull, callback)
        verify(flashcardRepository).getFlashcards(repositoryCallbackCaptor.capture())
        repositoryCallbackCaptor.firstValue.onDataNotAvailable()
        verify(callback).onError()
    }

    @Test
    fun `when no flashcards from repository, calls failure on callback`() {
        useCaseHandler.execute(getRandomFlashcard, requestValuesNull, callback)
        verify(flashcardRepository).getFlashcards(repositoryCallbackCaptor.capture())
        // Repository replies with empty list
        repositoryCallbackCaptor.firstValue.onFlashcardsLoaded(listOf())
        verify(callback).onError()
    }

    @Test
    fun `one of each priority, only new distribution, null previous id, provides flashcard with new priority`() {
        getRandomFlashcard =
                GetRandomFlashcard(flashcardRepository, probabilityDistributionOnlyNew)
        executeWithRepositoryResponseOfOneOfEachPriorityAndCaptureCallbackResponse(requestValuesNull)
        assertEquals(FLASHCARD_WITH_PRIORITY_NEW, responseCaptor.firstValue.flashcard)
    }

    @Test
    fun `one of each priority, only new distribution, new priority previous id, provides flashcard with new priority`() {
        getRandomFlashcard =
                GetRandomFlashcard(flashcardRepository, probabilityDistributionOnlyNew)
        val requestValues =
                GetRandomFlashcard.RequestValues(FLASHCARD_WITH_PRIORITY_NEW.id)
        executeWithRepositoryResponseOfOneOfEachPriorityAndCaptureCallbackResponse(requestValues)
        assertEquals(FLASHCARD_WITH_PRIORITY_NEW, responseCaptor.firstValue.flashcard)
    }

    @Test
    fun `one of each priority, only urgent distribution, null previous id, provides flashcard with urgent priority`() {
        getRandomFlashcard =
                GetRandomFlashcard(flashcardRepository, probabilityDistributionOnlyUrgent)
        executeWithRepositoryResponseOfOneOfEachPriorityAndCaptureCallbackResponse(requestValuesNull)
        assertEquals(FLASHCARD_WITH_PRIORITY_URGENT, responseCaptor.firstValue.flashcard)
    }

    @Test
    fun `one of each priority, only urgent distribution, urgent priority previous id, provides flashcard with urgent priority`() {
        getRandomFlashcard =
                GetRandomFlashcard(flashcardRepository, probabilityDistributionOnlyUrgent)
        val requestValues =
                GetRandomFlashcard.RequestValues(FLASHCARD_WITH_PRIORITY_URGENT.id)
        executeWithRepositoryResponseOfOneOfEachPriorityAndCaptureCallbackResponse(requestValues)
        assertEquals(FLASHCARD_WITH_PRIORITY_URGENT, responseCaptor.firstValue.flashcard)
    }

    @Test
    fun `one of each priority, only high distribution, null previous id, provides flashcard with high priority`() {
        getRandomFlashcard =
                GetRandomFlashcard(flashcardRepository, probabilityDistributionOnlyHigh)
        executeWithRepositoryResponseOfOneOfEachPriorityAndCaptureCallbackResponse(requestValuesNull)
        assertEquals(FLASHCARD_WITH_PRIORITY_HIGH, responseCaptor.firstValue.flashcard)
    }

    @Test
    fun `one of each priority, only high distribution, high priority previous id, provides flashcard with high priority`() {
        getRandomFlashcard =
                GetRandomFlashcard(flashcardRepository, probabilityDistributionOnlyHigh)
        val requestValues =
                GetRandomFlashcard.RequestValues(FLASHCARD_WITH_PRIORITY_HIGH.id)
        executeWithRepositoryResponseOfOneOfEachPriorityAndCaptureCallbackResponse(requestValues)
        assertEquals(FLASHCARD_WITH_PRIORITY_HIGH, responseCaptor.firstValue.flashcard)
    }

    @Test
    fun `one of each priority, only medium distribution, null previous id, provides flashcard with medium priority`() {
        getRandomFlashcard =
                GetRandomFlashcard(flashcardRepository, probabilityDistributionOnlyMedium)
        executeWithRepositoryResponseOfOneOfEachPriorityAndCaptureCallbackResponse(requestValuesNull)
        assertEquals(FLASHCARD_WITH_PRIORITY_MEDIUM, responseCaptor.firstValue.flashcard)
    }

    @Test
    fun `one of each priority, only high distribution, medium priority previous id, provides flashcard with high priority`() {
        getRandomFlashcard =
                GetRandomFlashcard(flashcardRepository, probabilityDistributionOnlyMedium)
        val requestValues =
                GetRandomFlashcard.RequestValues(FLASHCARD_WITH_PRIORITY_MEDIUM.id)
        executeWithRepositoryResponseOfOneOfEachPriorityAndCaptureCallbackResponse(requestValues)
        assertEquals(FLASHCARD_WITH_PRIORITY_MEDIUM, responseCaptor.firstValue.flashcard)
    }

    @Test
    fun `one of each priority, only low distribution, null previous id, provides flashcard with low priority`() {
        getRandomFlashcard =
                GetRandomFlashcard(flashcardRepository, probabilityDistributionOnlyLow)
        executeWithRepositoryResponseOfOneOfEachPriorityAndCaptureCallbackResponse(requestValuesNull)
        assertEquals(FLASHCARD_WITH_PRIORITY_LOW, responseCaptor.firstValue.flashcard)
    }

    @Test
    fun `one of each priority, only high distribution, low priority previous id, provides flashcard with high priority`() {
        getRandomFlashcard =
                GetRandomFlashcard(flashcardRepository, probabilityDistributionOnlyLow)
        val requestValues =
                GetRandomFlashcard.RequestValues(FLASHCARD_WITH_PRIORITY_LOW.id)
        executeWithRepositoryResponseOfOneOfEachPriorityAndCaptureCallbackResponse(requestValues)
        assertEquals(FLASHCARD_WITH_PRIORITY_LOW, responseCaptor.firstValue.flashcard)
    }

    @Test
    fun `no urgent priority, only new distribution, null previous id, provides flashcard with new priority`() {
        removePrioritiesAndTestForDifferentOne(listOf(FlashcardPriority.URGENT),
                probabilityDistributionOnlyNew, FLASHCARD_WITH_PRIORITY_NEW)
    }

    @Test
    fun `no high priority, only new distribution, null previous id, provides flashcard with new priority`() {
        removePrioritiesAndTestForDifferentOne(listOf(FlashcardPriority.HIGH),
                probabilityDistributionOnlyNew, FLASHCARD_WITH_PRIORITY_NEW)
    }

    @Test
    fun `no medium priority, only new distribution, null previous id, provides flashcard with new priority`() {
        removePrioritiesAndTestForDifferentOne(listOf(FlashcardPriority.MEDIUM),
                probabilityDistributionOnlyNew, FLASHCARD_WITH_PRIORITY_NEW)
    }

    @Test
    fun `no low priority, only new distribution, null previous id, provides flashcard with new priority`() {
        removePrioritiesAndTestForDifferentOne(listOf(FlashcardPriority.LOW),
                probabilityDistributionOnlyNew, FLASHCARD_WITH_PRIORITY_NEW)
    }

    @Test
    fun `no new priority, only urgent distribution, null previous id, provides flashcard with urgent priority`() {
        removePrioritiesAndTestForDifferentOne(listOf(FlashcardPriority.NEW),
                probabilityDistributionOnlyUrgent, FLASHCARD_WITH_PRIORITY_URGENT)
    }

    @Test
    fun `no high priority, only urgent distribution, null previous id, provides flashcard with urgent priority`() {
        removePrioritiesAndTestForDifferentOne(listOf(FlashcardPriority.HIGH),
                probabilityDistributionOnlyUrgent, FLASHCARD_WITH_PRIORITY_URGENT)
    }

    @Test
    fun `no medium priority, only urgent distribution, null previous id, provides flashcard with urgent priority`() {
        removePrioritiesAndTestForDifferentOne(listOf(FlashcardPriority.MEDIUM),
                probabilityDistributionOnlyUrgent, FLASHCARD_WITH_PRIORITY_URGENT)
    }

    @Test
    fun `no low priority, only urgent distribution, null previous id, provides flashcard with urgent priority`() {
        removePrioritiesAndTestForDifferentOne(listOf(FlashcardPriority.LOW),
                probabilityDistributionOnlyUrgent, FLASHCARD_WITH_PRIORITY_URGENT)
    }

    @Test
    fun `no new priority, only high distribution, null previous id, provides flashcard with high priority`() {
        removePrioritiesAndTestForDifferentOne(listOf(FlashcardPriority.NEW),
                probabilityDistributionOnlyHigh, FLASHCARD_WITH_PRIORITY_HIGH)
    }

    @Test
    fun `no urgent priority, only high distribution, null previous id, provides flashcard with high priority`() {
        removePrioritiesAndTestForDifferentOne(listOf(FlashcardPriority.URGENT),
                probabilityDistributionOnlyHigh, FLASHCARD_WITH_PRIORITY_HIGH)
    }

    @Test
    fun `no medium priority, only high distribution, null previous id, provides flashcard with high priority`() {
        removePrioritiesAndTestForDifferentOne(listOf(FlashcardPriority.MEDIUM),
                probabilityDistributionOnlyHigh, FLASHCARD_WITH_PRIORITY_HIGH)
    }

    @Test
    fun `no low priority, only high distribution, null previous id, provides flashcard with high priority`() {
        removePrioritiesAndTestForDifferentOne(listOf(FlashcardPriority.LOW),
                probabilityDistributionOnlyHigh, FLASHCARD_WITH_PRIORITY_HIGH)
    }

    @Test
    fun `no new priority, only medium distribution, null previous id, provides flashcard with medium priority`() {
        removePrioritiesAndTestForDifferentOne(listOf(FlashcardPriority.NEW),
                probabilityDistributionOnlyMedium, FLASHCARD_WITH_PRIORITY_MEDIUM)
    }

    @Test
    fun `no urgent priority, only medium distribution, null previous id, provides flashcard with medium priority`() {
        removePrioritiesAndTestForDifferentOne(listOf(FlashcardPriority.URGENT),
                probabilityDistributionOnlyMedium, FLASHCARD_WITH_PRIORITY_MEDIUM)
    }

    @Test
    fun `no high priority, only medium distribution, null previous id, provides flashcard with medium priority`() {
        removePrioritiesAndTestForDifferentOne(listOf(FlashcardPriority.HIGH),
                probabilityDistributionOnlyMedium, FLASHCARD_WITH_PRIORITY_MEDIUM)
    }

    @Test
    fun `no low priority, only medium distribution, null previous id, provides flashcard with medium priority`() {
        removePrioritiesAndTestForDifferentOne(listOf(FlashcardPriority.LOW),
                probabilityDistributionOnlyMedium, FLASHCARD_WITH_PRIORITY_MEDIUM)
    }

    @Test
    fun `no new priority, only low distribution, null previous id, provides flashcard with low priority`() {
        removePrioritiesAndTestForDifferentOne(listOf(FlashcardPriority.NEW),
                probabilityDistributionOnlyLow, FLASHCARD_WITH_PRIORITY_LOW)
    }

    @Test
    fun `no urgent priority, only low distribution, null previous id, provides flashcard with low priority`() {
        removePrioritiesAndTestForDifferentOne(listOf(FlashcardPriority.URGENT),
                probabilityDistributionOnlyLow, FLASHCARD_WITH_PRIORITY_LOW)
    }

    @Test
    fun `no high priority, only low distribution, null previous id, provides flashcard with low priority`() {
        removePrioritiesAndTestForDifferentOne(listOf(FlashcardPriority.HIGH),
                probabilityDistributionOnlyLow, FLASHCARD_WITH_PRIORITY_LOW)
    }

    @Test
    fun `no medium priority, only low distribution, null previous id, provides flashcard with low priority`() {
        removePrioritiesAndTestForDifferentOne(listOf(FlashcardPriority.MEDIUM),
                probabilityDistributionOnlyLow, FLASHCARD_WITH_PRIORITY_LOW)
    }

    @Test
    fun `no urgent or high, only new distribution, null previous id, provides flashcard with new priority`() {
        removePrioritiesAndTestForDifferentOne(
                listOf(FlashcardPriority.HIGH, FlashcardPriority.URGENT),
                probabilityDistributionOnlyNew, FLASHCARD_WITH_PRIORITY_NEW)
    }

    @Test
    fun `no high or low, only new distribution, null previous id, provides flashcard with new priority`() {
        removePrioritiesAndTestForDifferentOne(
                listOf(FlashcardPriority.HIGH, FlashcardPriority.LOW),
                probabilityDistributionOnlyNew, FLASHCARD_WITH_PRIORITY_NEW)
    }

    @Test
    fun `no urgent or medium, only new distribution, null previous id, provides flashcard with new priority`() {
        removePrioritiesAndTestForDifferentOne(
                listOf(FlashcardPriority.URGENT, FlashcardPriority.MEDIUM),
                probabilityDistributionOnlyNew, FLASHCARD_WITH_PRIORITY_NEW)
    }

    @Test
    fun `no new or urgent, only high distribution, null previous id, provides flashcard with high priority`() {
        removePrioritiesAndTestForDifferentOne(
                listOf(FlashcardPriority.NEW, FlashcardPriority.URGENT),
                probabilityDistributionOnlyHigh, FLASHCARD_WITH_PRIORITY_HIGH)
    }

    @Test
    fun `no new or low, only high distribution, null previous id, provides flashcard with high priority`() {
        removePrioritiesAndTestForDifferentOne(
                listOf(FlashcardPriority.NEW, FlashcardPriority.LOW),
                probabilityDistributionOnlyHigh, FLASHCARD_WITH_PRIORITY_HIGH)
    }

    @Test
    fun `no urgent or medium, only high distribution, null previous id, provides flashcard with high priority`() {
        removePrioritiesAndTestForDifferentOne(
                listOf(FlashcardPriority.URGENT, FlashcardPriority.MEDIUM),
                probabilityDistributionOnlyHigh, FLASHCARD_WITH_PRIORITY_HIGH)
    }

    @Test
    fun `no urgent high or medium, only new distribution, null previous id, provides flashcard with new priority`() {
        removePrioritiesAndTestForDifferentOne(
                listOf(FlashcardPriority.URGENT, FlashcardPriority.HIGH, FlashcardPriority.MEDIUM),
                probabilityDistributionOnlyNew, FLASHCARD_WITH_PRIORITY_NEW)
    }

    @Test
    fun `no urgent high or medium, only low distribution, null previous id, provides flashcard with low priority`() {
        removePrioritiesAndTestForDifferentOne(
                listOf(FlashcardPriority.URGENT, FlashcardPriority.HIGH, FlashcardPriority.MEDIUM),
                probabilityDistributionOnlyLow, FLASHCARD_WITH_PRIORITY_LOW)
    }

    @Test
    fun `no new high or low, only medium distribution, null previous id, provides flashcard with medium priority`() {
        removePrioritiesAndTestForDifferentOne(
                listOf(FlashcardPriority.NEW, FlashcardPriority.HIGH, FlashcardPriority.LOW),
                probabilityDistributionOnlyMedium, FLASHCARD_WITH_PRIORITY_MEDIUM)
    }

    @Test
    fun `no new high or low, only urgent distribution, null previous id, provides flashcard with urgent priority`() {
        removePrioritiesAndTestForDifferentOne(
                listOf(FlashcardPriority.NEW, FlashcardPriority.HIGH, FlashcardPriority.LOW),
                probabilityDistributionOnlyUrgent, FLASHCARD_WITH_PRIORITY_URGENT)
    }

    @Test
    fun `all new priority except one, won't repeat that one`() {
        val flashcards = listOf(FLASHCARD_1, FLASHCARD_2, FLASHCARD_WITH_PRIORITY_NEW,
                FLASHCARD_WITH_PRIORITY_URGENT)
        val request =
                GetRandomFlashcard.RequestValues(FLASHCARD_WITH_PRIORITY_URGENT.id)
        // Run several times to avoid accidental success from probability
        for (num in 0..10) {
            useCaseHandler.execute(getRandomFlashcard, request, callback)
            verify(flashcardRepository, times(num + 1))
                    .getFlashcards(repositoryCallbackCaptor.capture())
            repositoryCallbackCaptor.firstValue.onFlashcardsLoaded(flashcards)
            verify(callback, times(num + 1)).onSuccess(responseCaptor.capture())
            assertNotEquals(FLASHCARD_WITH_PRIORITY_URGENT, responseCaptor.allValues[num].flashcard)
        }
    }

    private fun executeWithRepositoryResponseOfOneOfEachPriorityAndCaptureCallbackResponse(
            requestValues: GetRandomFlashcard.RequestValues,
            flashcardList: List<Flashcard> = FLASHCARD_LIST_ONE_OF_EACH_PRIORITY) {
        useCaseHandler.execute(getRandomFlashcard, requestValues, callback)
        verify(flashcardRepository).getFlashcards(repositoryCallbackCaptor.capture())
        repositoryCallbackCaptor.firstValue.onFlashcardsLoaded(flashcardList)
        verify(callback).onSuccess(responseCaptor.capture())
    }

    private fun filterBasedOnPriority(priorities: List<FlashcardPriority>): List<Flashcard> {
        return FLASHCARD_LIST_ONE_OF_EACH_PRIORITY.filter { !priorities.contains(it.priority) }
    }

    private fun removePrioritiesAndTestForDifferentOne(
            prioritiesToRemove: List<FlashcardPriority>,
            priorityDistribution: FlashcardPriorityProbabilityDistribution,
            flashcardToAssert: Flashcard) {

        getRandomFlashcard =
                GetRandomFlashcard(flashcardRepository, priorityDistribution)
        val flashcardsWithoutHighPriority =
                filterBasedOnPriority(prioritiesToRemove)
        executeWithRepositoryResponseOfOneOfEachPriorityAndCaptureCallbackResponse(requestValuesNull,
                flashcardsWithoutHighPriority)
        assertEquals(flashcardToAssert, responseCaptor.firstValue.flashcard)
    }
}
