package com.gregspitz.flashcardappkotlin

import com.gregspitz.flashcardappkotlin.data.model.Category
import com.gregspitz.flashcardappkotlin.data.model.Flashcard
import com.gregspitz.flashcardappkotlin.data.model.FlashcardPriority

object TestData {
    val CATEGORY_1 = Category("Data Structure: Behavioral")
    val CATEGORY_2 = Category("Data Structure: Whatever")
    val CATEGORY_3 = Category("Different Category")
    val FLASHCARD_1 =
            Flashcard("0", CATEGORY_1.name, "Front", "Back", FlashcardPriority.NEW)
    val FLASHCARD_2 =
            Flashcard("1", CATEGORY_2.name, "A different front",
                    "Other Back", FlashcardPriority.NEW)
    val FLASHCARD_SAME_ID_AS_FLASHCARD_1 =
            Flashcard("0", CATEGORY_3.name, "Other front", "Taken aback",
                    FlashcardPriority.NEW)
    val SINGLE_FLASHCARD_LIST = listOf(FLASHCARD_1)
    val FLASHCARD_LIST = listOf(FLASHCARD_1, FLASHCARD_2)
    val FLASHCARD_LIST_OF_CATEGORY_1 = listOf(FLASHCARD_1)
    val FLASHCARD_LIST_SAME_IDS = listOf(FLASHCARD_1, FLASHCARD_SAME_ID_AS_FLASHCARD_1)
    val FLASHCARD_LIST_WITH_CATEGORIES = listOf(CATEGORY_1, FLASHCARD_1, CATEGORY_2, FLASHCARD_2)
}
