package com.gregspitz.flashcardappkotlin

import com.gregspitz.flashcardappkotlin.data.model.Category
import com.gregspitz.flashcardappkotlin.data.model.Flashcard

object TestData {
    val CATEGORY_1 = Category("Different Category")
    val CATEGORY_2 = Category("Data Structure: Whatever")
    val CATEGORY_3 = Category("Data Structure: Behavioral")
    val CATEGORY_LIST = listOf(CATEGORY_1, CATEGORY_2, CATEGORY_3)
    val CATEGORY_LIST_SORTED = listOf(CATEGORY_3, CATEGORY_2, CATEGORY_1)
    val FLASHCARD_1 =
            Flashcard("0", CATEGORY_1.name, "Front", "Back")
    val FLASHCARD_2 =
            Flashcard("1", CATEGORY_2.name, "A different front",
                    "Other Back")
    val FLASHCARD_SAME_ID_AS_FLASHCARD_1 =
            Flashcard("0", CATEGORY_3.name, "Other front", "Taken aback")
    val FLASHCARD_WITH_PRIORITY_NEW = Flashcard("2", CATEGORY_1.name, "NEW Front",
            "NEW Back")
    val FLASHCARD_WITH_PRIORITY_URGENT = Flashcard("3", CATEGORY_3.name, "URGENT Front",
            "URGENT Back", 2.6f)
    val FLASHCARD_WITH_PRIORITY_HIGH = Flashcard("4", CATEGORY_2.name, "HIGH Front",
            "HIGH Back", 2.8f)
    val FLASHCARD_WITH_PRIORITY_MEDIUM = Flashcard("5", CATEGORY_1.name, "MEDIUM Front",
            "MEDIUM Back", 3.1f)
    val FLASHCARD_WITH_PRIORITY_LOW = Flashcard("6", CATEGORY_2.name, "LOW Front",
            "LOW Back", 3.6f)
    val SINGLE_FLASHCARD_LIST = listOf(FLASHCARD_1)
    val FLASHCARD_LIST = listOf(FLASHCARD_1, FLASHCARD_2)
    val FLASHCARD_LIST_OF_CATEGORY_1 = listOf(FLASHCARD_1)
    val FLASHCARD_LIST_SAME_IDS = listOf(FLASHCARD_1, FLASHCARD_SAME_ID_AS_FLASHCARD_1)
    val FLASHCARD_LIST_WITH_CATEGORIES =
            listOf(CATEGORY_1, FLASHCARD_1, CATEGORY_2, FLASHCARD_2)
    val FLASHCARD_LIST_ONE_OF_EACH_PRIORITY = listOf(FLASHCARD_WITH_PRIORITY_NEW,
            FLASHCARD_WITH_PRIORITY_URGENT, FLASHCARD_WITH_PRIORITY_HIGH,
            FLASHCARD_WITH_PRIORITY_MEDIUM, FLASHCARD_WITH_PRIORITY_LOW)
}
