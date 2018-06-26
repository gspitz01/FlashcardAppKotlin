package com.gregspitz.flashcardappkotlin

import com.gregspitz.flashcardappkotlin.data.model.Category
import com.gregspitz.flashcardappkotlin.data.model.Flashcard
import com.gregspitz.flashcardappkotlin.data.model.FlashcardPriority

object TestData {
    val CATEGORY_1 = Category("Data Structure: Behavioral")
    val CATEGORY_2 = Category("Data Structure: Whatever")
    val CATEGORY_3 = Category("Different Category")
    val CATEGORY_LIST = listOf(CATEGORY_1, CATEGORY_2, CATEGORY_3)
    val FLASHCARD_1 =
            Flashcard("0", CATEGORY_1.name, "Front", "Back", FlashcardPriority.NEW)
    val FLASHCARD_2 =
            Flashcard("1", CATEGORY_2.name, "A different front",
                    "Other Back", FlashcardPriority.NEW)
    val FLASHCARD_SAME_ID_AS_FLASHCARD_1 =
            Flashcard("0", CATEGORY_3.name, "Other front", "Taken aback",
                    FlashcardPriority.NEW)
    val FLASHCARD_WITH_PRIORITY_NEW = Flashcard("2", CATEGORY_1.name, "NEW Front",
            "NEW Back", FlashcardPriority.NEW)
    val FLASHCARD_WITH_PRIORITY_URGENT = Flashcard("3", CATEGORY_3.name, "URGENT Front",
            "URGENT Back", FlashcardPriority.URGENT)
    val FLASHCARD_WITH_PRIORITY_HIGH = Flashcard("4", CATEGORY_2.name, "HIGH Front",
            "HIGH Back", FlashcardPriority.HIGH)
    val FLASHCARD_WITH_PRIORITY_MEDIUM = Flashcard("5", CATEGORY_1.name, "MEDIUM Front",
            "MEDIUM Back", FlashcardPriority.MEDIUM)
    val FLASHCARD_WITH_PRIORITY_LOW = Flashcard("6", CATEGORY_2.name, "LOW Front",
            "LOW Back", FlashcardPriority.LOW)
    val SINGLE_FLASHCARD_LIST = listOf(FLASHCARD_1)
    val FLASHCARD_LIST = listOf(FLASHCARD_1, FLASHCARD_2)
    val FLASHCARD_LIST_SAME_IDS = listOf(FLASHCARD_1, FLASHCARD_SAME_ID_AS_FLASHCARD_1)
    val FLASHCARD_LIST_WITH_CATEGORIES =
            listOf(CATEGORY_1, FLASHCARD_1, CATEGORY_2, FLASHCARD_2)
    val FLASHCARD_LIST_ONE_OF_EACH_PRIORITY = listOf(FLASHCARD_WITH_PRIORITY_NEW,
            FLASHCARD_WITH_PRIORITY_URGENT, FLASHCARD_WITH_PRIORITY_HIGH,
            FLASHCARD_WITH_PRIORITY_MEDIUM, FLASHCARD_WITH_PRIORITY_LOW)
}
