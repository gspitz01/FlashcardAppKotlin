package com.gregspitz.flashcardappkotlin.categorylist.domain.usecase

import com.gregspitz.flashcardappkotlin.UseCase
import com.gregspitz.flashcardappkotlin.data.model.Category
import com.gregspitz.flashcardappkotlin.data.source.FlashcardDataSource
import com.gregspitz.flashcardappkotlin.data.source.FlashcardRepository

class GetCategories(private val repository: FlashcardRepository)
    : UseCase<GetCategories.RequestValues, GetCategories.ResponseValue>() {

    override fun executeUseCase(requestValues: RequestValues) {
        repository.getCategories(object : FlashcardDataSource.GetCategoriesCallback {
            override fun onCategoriesLoaded(categories: List<Category>) {
                getUseCaseCallback().onSuccess(ResponseValue(sortCategories(categories)))
            }

            override fun onDataNotAvailable() {
                getUseCaseCallback().onError()
            }
        })
    }

    fun sortCategories(categories: List<Category>): List<Category> =
            categories.sortedWith(compareBy { it.name })

    class RequestValues : UseCase.RequestValues

    class ResponseValue(val categories: List<Category>) : UseCase.ResponseValue
}
