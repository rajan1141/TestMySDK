package com.test.my.app.home.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.test.my.app.R
import com.test.my.app.common.constants.Constants
import com.test.my.app.databinding.ItemAktivoParamBinding
import com.test.my.app.home.common.DataHandler.AktiVoParamModel
import com.test.my.app.home.ui.HomeScreenNewFragment

class AktivoParamAdapter(val context: Context,
                         val fragment : HomeScreenNewFragment) : RecyclerView.Adapter<AktivoParamAdapter.AktivoParamViewHolder>() {

    private var aktivoParamList: MutableList<AktiVoParamModel> = mutableListOf()

    override fun getItemCount(): Int = aktivoParamList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AktivoParamViewHolder {
        return AktivoParamViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_aktivo_param,parent, false))
    }

    override fun onBindViewHolder(holder: AktivoParamViewHolder, position: Int) {
        val aktivoParam = aktivoParamList[position]
        holder.lblAktivoParam.text = aktivoParam.paramName
        holder.txtAktivoParam.text = aktivoParam.paramValue
        holder.imgAktivoParam.setImageResource(aktivoParam.paramImgId)
        holder.layoutAktivoParam.backgroundTintList = ContextCompat.getColorStateList(context,aktivoParam.bgColor)
        holder.imgAktivoParam.setColorFilter(ContextCompat.getColor(this.context,aktivoParam.paramColor))
        holder.lblAktivoParam.setTextColor(ContextCompat.getColor(this.context,aktivoParam.paramColor))
        holder.txtAktivoParam.setTextColor(ContextCompat.getColor(this.context,aktivoParam.paramColor))

        holder.layoutAktivoParam.setOnClickListener {
            fragment.navigateToAktivo(Constants.AKTIVO_DASHBOARD_CODE)
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(list: MutableList<AktiVoParamModel>) {
        aktivoParamList.clear()
        aktivoParamList.addAll(list)
        aktivoParamList.sortBy { it.paramId }
        notifyDataSetChanged()
    }

    inner class AktivoParamViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = ItemAktivoParamBinding.bind(view)
        val layoutAktivoParam = binding.layoutAktivoParam
        val imgAktivoParam = binding.imgAktivoParam
        val lblAktivoParam = binding.lblAktivoParam
        val txtAktivoParam = binding.txtAktivoParam
    }

}