package com.gregspitz.flashcardappkotlin

/**
 * An interface for all views
 */
interface BaseView<in T : BasePresenter> {
    fun setPresenter(presenter: T)
}
