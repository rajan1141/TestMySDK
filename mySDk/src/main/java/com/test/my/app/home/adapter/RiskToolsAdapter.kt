package com.test.my.app.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.test.my.app.R
import com.test.my.app.databinding.ItemRiskToolsBinding
import com.test.my.app.home.common.DataHandler

class RiskToolsAdapter(
    private val mContext: Context,
    private val listener: OnRiskToolsListener) : RecyclerView.Adapter<RiskToolsAdapter.RiskToolsViewHolder>() {

    private var featuresList: MutableList<DataHandler.FeatureModel> = mutableListOf()

    override fun getItemCount(): Int = featuresList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RiskToolsViewHolder {
        val v: View = LayoutInflater.from(parent.context).inflate(R.layout.item_risk_tools, parent,false)
        return RiskToolsViewHolder(v)
    }

    override fun onBindViewHolder(holder: RiskToolsViewHolder, position: Int) {
        val feature = featuresList[position]
        holder.txtFeature.text = feature.featureTitle
        holder.imgFeature.setImageResource(feature.imgId)

        holder.layoutRiskTools.setOnClickListener {
            listener.onRiskToolsClick(feature)
        }
    }

    fun updateList(list: List<DataHandler.FeatureModel>) {
        this.featuresList.clear()
        this.featuresList.addAll(list)
        this.notifyDataSetChanged()
    }

    interface OnRiskToolsListener {
        fun onRiskToolsClick(feature: DataHandler.FeatureModel)
    }

    inner class RiskToolsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemRiskToolsBinding.bind(view)
        var layoutRiskTools = binding.layoutRiskTools
        var imgFeature = binding.imgFeature
        var txtFeature = binding.txtFeature
    }

}
