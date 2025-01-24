package com.test.my.app.wyh.healthContent.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.test.my.app.R
import com.test.my.app.databinding.ItemQuickReadsBinding
import com.test.my.app.wyh.common.Section
import com.test.my.app.wyh.common.WyhHelper
import com.test.my.app.wyh.healthContent.ui.HealthContentDashboardActivity

class QuickReadsAdapter(private val context: Context,
                        private val activity: HealthContentDashboardActivity) : RecyclerView.Adapter<QuickReadsAdapter.QuickReadsViewHolder>() {

    val blogList: MutableList<Section> = mutableListOf()
    var subQuickReadsAdapter: SubQuickReadsAdapter? = null

    override fun getItemCount(): Int = blogList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuickReadsViewHolder =
        QuickReadsViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_quick_reads, parent,false))

    override fun onBindViewHolder(holder: QuickReadsViewHolder, position: Int) {
        val blogItem = blogList[position]

        holder.txtQuickReads.text = WyhHelper.getQuickReadsTitleByCode(blogItem.sectionCode!!,context)

        subQuickReadsAdapter = SubQuickReadsAdapter(context,activity)
        holder.rvSubQuickReads.layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
        holder.rvSubQuickReads.adapter = subQuickReadsAdapter
        subQuickReadsAdapter!!.updateData(blogItem.sectionList.toMutableList())
    }

    fun updateData( list : MutableList<Section> ) {
        this.blogList.clear()
        this.blogList.addAll(list)
        //blogList.sortByDescending { it.createdOn }
        notifyDataSetChanged()
    }

    fun clearAdapterList() {
        blogList.clear()
        notifyDataSetChanged()
    }

    inner class QuickReadsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = ItemQuickReadsBinding.bind(view)
        //val layoutBlog = binding.layoutBlog
        val txtQuickReads = binding.txtQuickReads
        val rvSubQuickReads = binding.rvSubQuickReads
    }

}