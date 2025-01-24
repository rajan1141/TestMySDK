package com.test.my.app.blogs.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.test.my.app.R
import com.test.my.app.blogs.ui.BlogDashboardFragment
import com.test.my.app.blogs.viewmodel.BlogViewModel
import com.test.my.app.blogs.views.BlogsBinding.setHtmlTxt
import com.test.my.app.common.view.CommonBinding.setImgUrl
import com.test.my.app.databinding.ItemBlogNewBinding
import com.test.my.app.model.blogs.BlogItem

class BlogAdapter(
    private val fragment: BlogDashboardFragment,
    private val viewModel: BlogViewModel
) : RecyclerView.Adapter<BlogAdapter.BlogViewHolder>() {

    private val blogList: MutableList<BlogItem> = mutableListOf()
    private var mOnBottomReachedListener: OnBottomReachedListener? = null

    fun setOnBottomReachedListener(onBottomReachedListener: OnBottomReachedListener) {
        this.mOnBottomReachedListener = onBottomReachedListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlogViewHolder =
        BlogViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_blog_new, parent, false)
        )

    override fun onBindViewHolder(holder: BlogViewHolder, position: Int) {
        try {
            val blogItem = blogList[position]

            holder.bindTo(blogItem)

            if (position == blogList.size - 1 && blogList.size > 3) {
                mOnBottomReachedListener!!.onBottomReached(position)
            }

            holder.itemView.setOnClickListener {
                fragment.viewBlog(it, blogItem)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getItemCount(): Int {
        return blogList.size
    }

    fun updateData(blogList: List<BlogItem>?) {
        this.blogList.addAll(blogList!!)
        notifyDataSetChanged()
        //fragment.stopShimmer()
    }

    fun clearAdapterList() {
        blogList.clear()
        notifyDataSetChanged()
    }

    inner class BlogViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        //private val binding = ItemBlogBinding.bind(view)
        private val binding = ItemBlogNewBinding.bind(view)

        fun bindTo(blog: BlogItem) {
            binding.imgBlog.setImgUrl(blog.image!!)
            binding.txtBlogTitle.setHtmlTxt(blog.title!!)
            binding.txtBlogDesciption.text = blog.description
            binding.txtBlogDate.text = blog.date
        }

    }

    interface OnBottomReachedListener {
        fun onBottomReached(position: Int)
    }

}

