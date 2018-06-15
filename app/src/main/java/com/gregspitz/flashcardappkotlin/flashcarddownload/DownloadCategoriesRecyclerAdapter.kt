package com.gregspitz.flashcardappkotlin.flashcarddownload

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gregspitz.flashcardappkotlin.R
import com.gregspitz.flashcardappkotlin.data.service.model.DownloadCategory
import kotlinx.android.synthetic.main.download_category_holder.view.*

class DownloadCategoriesRecyclerAdapter(private var downloadCategories: List<DownloadCategory>)
    : RecyclerView.Adapter<DownloadCategoriesRecyclerAdapter.DownloadCategoryHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DownloadCategoryHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.download_category_holder, parent, false)
        return DownloadCategoryHolder(view)
    }

    override fun getItemCount(): Int = downloadCategories.size

    override fun onBindViewHolder(holder: DownloadCategoryHolder, position: Int) {
        holder.bind(downloadCategories[position])
    }

    fun setDownloadCategories(downloadCategories: List<DownloadCategory>) {
        this.downloadCategories = downloadCategories
        notifyDataSetChanged()
    }

    inner class DownloadCategoryHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(category: DownloadCategory) {
            itemView.categoryName.text = category.name
            itemView.flashcardCount.text = category.count.toString()
        }

    }


}
