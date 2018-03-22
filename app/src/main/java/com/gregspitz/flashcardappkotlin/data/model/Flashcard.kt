package com.gregspitz.flashcardappkotlin.data.model

import java.util.*

/**
 * An immutable text flashcard with a front and a back
 */
data class Flashcard(val id: String = UUID.randomUUID().toString(),
                     val front: String, val back: String)
