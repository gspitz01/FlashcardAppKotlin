package com.gregspitz.flashcardappkotlin.categorylist

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gregspitz.flashcardappkotlin.R
import com.gregspitz.flashcardappkotlin.data.model.Category
import kotlinx.android.synthetic.main.download_category_holder.view.*

class CategoryRecyclerAdapter(private var categories: List<Category>)
    : RecyclerView.Adapter<CategoryRecyclerAdapter.CategoryHolder>() {

    private lateinit var presenter: CategoryListContract.Presenter

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.category_list_holder, parent, false)
        return CategoryHolder(view)
    }

    override fun getItemCount() = categories.size

    override fun onBindViewHolder(holder: CategoryHolder, position: Int) {
        holder.bind(categories[position])
    }

    fun setPresenter(presenter: CategoryListContract.Presenter) {
        this.presenter = presenter
    }

    fun updateCategories(categories: List<Category>) {
        this.categories = categories
        notifyDataSetChanged()
    }

    inner class CategoryHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(category: Category) {
            itemView.categoryName.text = category.name
            itemView.setOnClickListener {
                presenter.onCategoryClick(adapterPosition)
            }
        }
    }
}
