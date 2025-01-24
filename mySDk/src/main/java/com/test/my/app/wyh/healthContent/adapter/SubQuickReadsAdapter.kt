package com.test.my.app.wyh.healthContent.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.test.my.app.R
import com.test.my.app.common.view.CommonBinding.setImgUrl
import com.test.my.app.databinding.ItemSubQuickReadsBinding
import com.test.my.app.model.wyh.healthContent.GetAllItemsModel
import com.test.my.app.wyh.common.WyhHelper
import com.test.my.app.wyh.healthContent.ui.HealthContentDashboardActivity

class SubQuickReadsAdapter(private val context: Context,
                           private val activity: HealthContentDashboardActivity) : RecyclerView.Adapter<SubQuickReadsAdapter.SubQuickReadsViewHolder>() {

    val blogList: MutableList<GetAllItemsModel.Article> = mutableListOf()

    override fun getItemCount(): Int = blogList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubQuickReadsViewHolder =
        SubQuickReadsViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_sub_quick_reads, parent,false))

    override fun onBindViewHolder(holder: SubQuickReadsViewHolder, position: Int) {
        val blogItem = blogList[position]

        holder.txtBlogTitle.text = blogItem.articleName

        holder.imgBlog.setImgUrl(WyhHelper.getWyhUrlToLoad(blogItem.imgPath!!))
        holder.txtBlogTitle.text = blogItem.articleName

        holder.layoutBlog.setOnClickListener {
            //mOnSubQuickReadListener.onSubQuickReadClicked(position,blogItem)
            activity.getBlogFromArticleCode(blogItem.articleCode!!)
        }
    }

    fun updateData( list : MutableList<GetAllItemsModel.Article> ) {
        this.blogList.addAll(list)
        //blogList.sortByDescending { it.createdOn }
        notifyDataSetChanged()
    }

    fun clearAdapterList() {
        blogList.clear()
        notifyDataSetChanged()
    }

    inner class SubQuickReadsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = ItemSubQuickReadsBinding.bind(view)
        val layoutBlog = binding.layoutBlog
        val imgBlog = binding.imgBlog
        val txtBlogTitle = binding.txtBlogTitle
    }

    interface OnSubQuickReadListener {
        fun onSubQuickReadClicked( position: Int , article:GetAllItemsModel.Article )
    }
}