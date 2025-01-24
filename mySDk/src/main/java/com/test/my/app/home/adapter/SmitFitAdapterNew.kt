package com.test.my.app.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.test.my.app.R
import com.test.my.app.databinding.ItemSmitFitCategoriesBinding
import com.test.my.app.home.common.DataHandler

class SmitFitAdapterNew(
    private val mContext: Context,
    private val listener: OnSmitFitFeatureListener) : RecyclerView.Adapter<SmitFitAdapterNew.SmitFitViewHolder>() {

    private var featuresList: MutableList<DataHandler.FeatureModel> = mutableListOf()

    override fun getItemCount(): Int = featuresList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SmitFitViewHolder {
        val v: View = LayoutInflater.from(parent.context).inflate(R.layout.item_smit_fit_categories, parent,false)
        return SmitFitViewHolder(v)
    }

    override fun onBindViewHolder(holder: SmitFitViewHolder, position: Int) {
        val feature = featuresList[position]
        holder.txtFeature.text = feature.featureTitle
        holder.imgFeature.setImageResource(feature.imgId)
        //holder.imgFeature.setColorFilter(ContextCompat.getColor(mContext,feature.color))
        //holder.txtFeature.setTextColor(ContextCompat.getColor(mContext, feature.color))

        if ( position % 2 == 0 ) {
            holder.imgFeature.backgroundTintList = ContextCompat.getColorStateList(mContext,R.color.green_light)
            holder.imgFeature.setColorFilter(ContextCompat.getColor(mContext,R.color.color_retirement))
        } else {
            holder.imgFeature.backgroundTintList = ContextCompat.getColorStateList(mContext,R.color.pink_light)
            holder.imgFeature.setColorFilter(ContextCompat.getColor(mContext,R.color.color_meditation))
        }

        holder.layoutSmitFit.setOnClickListener {
            listener.onSmitFitFeatureClick(feature)
        }
    }

    fun updateList(list: List<DataHandler.FeatureModel>) {
        this.featuresList.clear()
        this.featuresList.addAll(list)
        this.notifyDataSetChanged()
    }

    interface OnSmitFitFeatureListener {
        fun onSmitFitFeatureClick(feature: DataHandler.FeatureModel)
    }

    inner class SmitFitViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemSmitFitCategoriesBinding.bind(view)
        var layoutSmitFit = binding.layoutSmitFit
        var imgFeature = binding.imgFeature
        var txtFeature = binding.txtFeature
    }

}
