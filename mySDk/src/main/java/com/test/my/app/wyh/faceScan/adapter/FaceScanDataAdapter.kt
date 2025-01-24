package com.test.my.app.wyh.faceScan.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.test.my.app.R
import com.test.my.app.common.utils.Utilities
import com.test.my.app.databinding.ItemFaceScanDataBinding
import com.test.my.app.home.common.DataHandler
import com.test.my.app.home.common.DataHandler.FaceScanDataModel

class FaceScanDataAdapter(val mContext: Context) : RecyclerView.Adapter<FaceScanDataAdapter.FaceScanDataViewHolder>() {

    private var parametersList: MutableList<FaceScanDataModel> = mutableListOf()

    override fun getItemCount(): Int = parametersList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FaceScanDataViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_face_scan_data, parent, false)
        return FaceScanDataViewHolder(v)
    }

    @SuppressLint("ClickableViewAccessibility", "SetTextI18n", "Range")
    override fun onBindViewHolder(holder: FaceScanDataViewHolder, position: Int) {
        try {
            val parameter: FaceScanDataModel = parametersList[position]
            holder.imgParam.setImageResource(Utilities.getWellfieParameterImageByCode(parameter.paramCode))
            holder.txtParamName.text = DataHandler(mContext).getWellfieParametersConverted(parameter.paramCode)
            when (parameter.paramCode) {
                "STRESS_INDEX" -> {
                    //holder.txtParamValue.text = parameter.paramObs
                    holder.txtParamValue.text = Utilities.convertStringToPascalCase(parameter.paramObs)
                }
                "HEART_RATE" -> {
                    holder.txtParamValue.text = "${parameter.paramValue} BPM"
                }
                "BREATHING_RATE" -> {
                    holder.txtParamValue.text = "${parameter.paramValue} RPM"
                }
                "BLOOD_OXYGEN" -> {
                    holder.txtParamValue.text = "${parameter.paramValue} %"
                }
                else -> {
                    holder.txtParamValue.text = parameter.paramValue
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun updateData(list: MutableList<FaceScanDataModel>) {
        for (i in list) {
            when (i.paramCode) {
                "BMI" -> i.paramId = 1
                "BP" -> i.paramId = 2
                "HEART_RATE" -> i.paramId = 3
                "BREATHING_RATE" -> i.paramId = 4
                "BLOOD_OXYGEN" -> i.paramId = 5
                "STRESS_INDEX" -> i.paramId = 6
            }
        }
        this.parametersList.clear()
        this.parametersList.addAll(list.sortedBy { it.paramId })
        Utilities.printData("FaceScanDataList",parametersList.toMutableList())
        this.notifyDataSetChanged()
    }

    inner class FaceScanDataViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemFaceScanDataBinding.bind(view)
        var layoutParameter = binding.layoutParameter
        var imgInfo = binding.imgInfo
        var imgParam = binding.imgParam
        var txtParamName = binding.txtParamName
        var txtParamValue = binding.txtParamValue
        //var txtParamObs = binding.txtParamObs
    }

}