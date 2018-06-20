package com.gregspitz.flashcardappkotlin.randomflashcard.domain.model

import com.gregspitz.flashcardappkotlin.data.model.FlashcardPriority

data class FlashcardPriorityProbabilityDistribution(
        val newProbability: Double, val urgentProbability: Double, val highProbability: Double,
        val mediumProbability: Double, val lowProbability: Double) {

    fun getDistributionMap(): Map<FlashcardPriority, Double> {
        return mapOf(FlashcardPriority.NEW to newProbability,
                FlashcardPriority.URGENT to urgentProbability,
                FlashcardPriority.HIGH to highProbability,
                FlashcardPriority.MEDIUM to mediumProbability,
                FlashcardPriority.LOW to lowProbability)
    }
}
