package com.test.my.app.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.test.my.app.R
import com.test.my.app.databinding.ItemHealthSelfCareBinding
import com.test.my.app.home.common.DataHandler

class HealthSelfCareAdapter(
    private val mContext: Context,
    private val listener: OnHealthSelfCareListener) : RecyclerView.Adapter<HealthSelfCareAdapter.HealthSelfCareViewHolder>() {

    private var featuresList: MutableList<DataHandler.FeatureModel> = mutableListOf()

    override fun getItemCount(): Int = featuresList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HealthSelfCareViewHolder {
        val v: View = LayoutInflater.from(parent.context).inflate(R.layout.item_health_self_care, parent,false)
        return HealthSelfCareViewHolder(v)
    }

    override fun onBindViewHolder(holder: HealthSelfCareViewHolder, position: Int) {
        val feature = featuresList[position]
        holder.txtFeature.text = feature.featureTitle
        holder.imgFeature.setImageResource(feature.imgId)
        holder.imgFeature.setColorFilter(ContextCompat.getColor(mContext,feature.color))
        //holder.txtFeature.setTextColor(ContextCompat.getColor(mContext, feature.color))

        holder.layoutHealthSelfCare.setOnClickListener {
            listener.onHealthSelfCareClick(feature)
        }
    }

    fun updateList(list: List<DataHandler.FeatureModel>) {
        this.featuresList.clear()
        this.featuresList.addAll(list)
        this.notifyDataSetChanged()
    }

    interface OnHealthSelfCareListener {
        fun onHealthSelfCareClick(feature: DataHandler.FeatureModel)
    }

    inner class HealthSelfCareViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemHealthSelfCareBinding.bind(view)
        var layoutHealthSelfCare = binding.layoutBlog
        var imgFeature = binding.imgBlog
        var txtFeature = binding.txtBlogTitle
    }

}
