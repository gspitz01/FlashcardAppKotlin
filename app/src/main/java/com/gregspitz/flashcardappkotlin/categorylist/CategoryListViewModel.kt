package com.gregspitz.flashcardappkotlin.categorylist

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.gregspitz.flashcardappkotlin.data.model.Category

class CategoryListViewModel : ViewModel(), CategoryListContract.ViewModel {

    val categories: MutableLiveData<List<Category>> = MutableLiveData()

    override fun setCategories(categories: List<Category>) {
        this.categories.value = categories
    }

    override fun getCategories(): List<Category>? {
        return categories.value
    }

}
