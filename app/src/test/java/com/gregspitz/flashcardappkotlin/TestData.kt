package com.gregspitz.flashcardappkotlin

import com.gregspitz.flashcardappkotlin.data.model.Flashcard

object TestData {
    val FLASHCARD_1 =
            Flashcard("0", "Data Structure: Behavioral", "Front", "Back")
    val FLASHCARD_2 =
            Flashcard("1", "Data Structure: Whatever", "A different front",
                    "Other Back")
    val FLASHCARD_SAME_ID_AS_FLASHCARD_1 =
            Flashcard("0", "Different Category", "Other front", "Taken aback")
    val SINGLE_FLASHCARD_LIST = listOf(FLASHCARD_1)
    val FLASHCARD_LIST = listOf(FLASHCARD_1, FLASHCARD_2)
    val FLASHCARD_LIST_SAME_IDS = listOf(FLASHCARD_1, FLASHCARD_SAME_ID_AS_FLASHCARD_1)
}
