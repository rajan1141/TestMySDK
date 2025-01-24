package com.test.my.app.wyh.healthContent.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.test.my.app.R
import com.test.my.app.common.constants.Constants
import com.test.my.app.common.utils.Utilities
import com.test.my.app.common.view.CommonBinding.setImgUrl
import com.test.my.app.databinding.ItemHealthContentBinding
import com.test.my.app.wyh.common.HealthContent
import com.test.my.app.wyh.common.WyhHelper
import com.test.my.app.wyh.healthContent.ui.HealthContentDashboardActivity

class HealthBlogsAdapter(private val context: Context,
                         private val activity: HealthContentDashboardActivity,
                         private val mOnBottomReachedListener: OnBlogsBottomReachedListener) : RecyclerView.Adapter<HealthBlogsAdapter.HealthBlogsViewHolder>() {

    val blogList: MutableList<HealthContent> = mutableListOf()

    override fun getItemCount(): Int = blogList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HealthBlogsViewHolder =
        HealthBlogsViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_health_content, parent,false))

    override fun onBindViewHolder(holder: HealthBlogsViewHolder, position: Int) {
        try {
            val blogItem = blogList[position]
            if ( activity.category == Constants.VIDEOS ) {
                val videoId = Utilities.getYouTubeVideoId(blogItem.contentUrl!!)
                holder.imgBlog.setImgUrl("https://img.youtube.com/vi/$videoId/0.jpg")
            } else {
                holder.imgBlog.setImgUrl(WyhHelper.getWyhUrlToLoad(blogItem.contentImgUrl!!))
            }

            holder.txtBlogTitle.text = blogItem.contentName
            //holder.txtBlogDate.text = DateHelper.getDateTimeAs_ddMMMyyyyNew(blogItem.createdOn!!.split("T").toTypedArray()[0])

            if ( !Utilities.isNullOrEmpty(blogItem.contentDesc) ) {
                holder.txtBlogDesciption.text = blogItem.contentDesc
                holder.txtBlogDesciption.visibility = View.VISIBLE
            } else {
                holder.txtBlogDesciption.text = ""
                holder.txtBlogDesciption.visibility = View.GONE
            }

            if (position == blogList.size - 1 && blogList.size > 2) {
                mOnBottomReachedListener.onBottomReached(position)
            }

            holder.layoutBlog.setOnClickListener {
                when(activity.category) {
                    Constants.BLOGS,Constants.QUICK_READS -> {
                        activity.getBlogFromArticleCode(blogItem.articleCode!!)
                    }
                    else -> activity.viewHealthContentDetail(blogItem)
                }
            }
        } catch ( e:Exception ) {
            e.printStackTrace()
        }
    }

    fun updateData( list : MutableList<HealthContent> ) {
        for ( item in list ) {
            if ( !blogList.contains(item) ) {
                blogList.add(item)
            }
        }
        //this.blogList.addAll(list)
        //blogList.sortByDescending { it.createdOn }
        notifyDataSetChanged()
        //fragment.stopShimmer()
    }

    fun clearAdapterList() {
        blogList.clear()
        notifyDataSetChanged()
    }

    inner class HealthBlogsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = ItemHealthContentBinding.bind(view)
        val layoutBlog = binding.layoutBlog
        val imgBlog = binding.imgBlog
        val txtBlogTitle = binding.txtBlogTitle
        val txtBlogDesciption = binding.txtBlogDesciption
        val txtBlogDate = binding.txtBlogDate
    }

    interface OnBlogsBottomReachedListener {
        fun onBottomReached(position: Int)
    }

}