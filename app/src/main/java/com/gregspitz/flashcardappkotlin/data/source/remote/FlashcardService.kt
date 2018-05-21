package com.gregspitz.flashcardappkotlin.data.source.remote

import com.gregspitz.flashcardappkotlin.data.model.Flashcard
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.POST

interface FlashcardService {

    @GET("flashcard/{id}")
    fun getFlashcardById(id: String): Call<Flashcard>

    @GET("flashcards")
    fun getFlashcards(): Call<List<Flashcard>>

    @POST("flashcard")
    fun saveFlashcard(flashcard: Flashcard): Call<Flashcard>

    companion object {
        private const val BASE_URL = "http://10.0.2.2:8080/"

        fun create(): FlashcardService {
            val retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .build()
            return retrofit.create(FlashcardService::class.java)
        }
    }
}
