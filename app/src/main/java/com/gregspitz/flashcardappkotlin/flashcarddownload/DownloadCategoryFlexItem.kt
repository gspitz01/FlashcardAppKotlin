package com.gregspitz.flashcardappkotlin.flashcarddownload

import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.View
import com.gregspitz.flashcardappkotlin.R
import com.gregspitz.flashcardappkotlin.data.service.model.DownloadCategory
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.flexibleadapter.utils.DrawableUtils
import eu.davidea.viewholders.FlexibleViewHolder
import kotlinx.android.synthetic.main.download_category_holder.view.*

data class DownloadCategoryFlexItem(val downloadCategory: DownloadCategory)
    : AbstractFlexibleItem<DownloadCategoryFlexItem.DownloadCategoryHolder>() {
    override fun bindViewHolder(adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>?,
                                holder: DownloadCategoryHolder?, position: Int,
                                payloads: MutableList<Any>?) {

        holder?.bind(downloadCategory)
        val context = holder?.itemView?.context
        context?.let {
            val drawable = DrawableUtils.getSelectableBackgroundCompat(
                    Color.WHITE, it.resources.getColor(R.color.colorAccent), Color.WHITE)
            DrawableUtils.setBackgroundCompat(holder.itemView, drawable)
        }

    }

    override fun createViewHolder(
            view: View?,
            adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>?
    ) = DownloadCategoryHolder(view, adapter)

    override fun getLayoutRes(): Int = R.layout.download_category_holder

    inner class DownloadCategoryHolder(view: View?,
                                       adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>?)
        : FlexibleViewHolder(view, adapter) {

        fun bind(downloadCategory: DownloadCategory) {
            itemView.categoryName.text = downloadCategory.name
            itemView.flashcardCount.text = downloadCategory.count.toString()
        }
    }
}
