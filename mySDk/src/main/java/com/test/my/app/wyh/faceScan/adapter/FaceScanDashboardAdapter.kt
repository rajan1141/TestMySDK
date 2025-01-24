package com.test.my.app.wyh.faceScan.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.test.my.app.R
import com.test.my.app.common.utils.Utilities
import com.test.my.app.databinding.ItemWellfieDashboardBinding
import com.test.my.app.home.common.DataHandler
import com.test.my.app.home.common.DataHandler.FaceScanDataModel

class FaceScanDashboardAdapter(val mContext: Context) : RecyclerView.Adapter<FaceScanDashboardAdapter.FaceScanDashboardViewHolder>() {

    private var parametersList: MutableList<FaceScanDataModel> = mutableListOf()

    override fun getItemCount(): Int = parametersList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FaceScanDashboardViewHolder {
        val v: View = LayoutInflater.from(parent.context).inflate(R.layout.item_wellfie_dashboard, parent, false)
        return FaceScanDashboardViewHolder(v)
    }

    override fun onBindViewHolder(holder: FaceScanDashboardViewHolder, position: Int) {
        try {
            val parameter = parametersList[position]
            holder.imgParam.setImageResource(DataHandler(mContext).getWellfieDashboardParameterImageByCode(parameter.paramCode))
            //holder.txtParamName.text = parameter.paramName
            holder.txtParamName.text = DataHandler(mContext).getWellfieParametersConverted(parameter.paramCode)

            when (parameter.paramCode) {
                "HEART_RATE" -> {
                    holder.txtParamValue.text = "${parameter.paramValue} BPM"
                }
                "BREATHING_RATE" -> {
                    holder.txtParamValue.text = "${parameter.paramValue} RPM"
                }
                "BLOOD_OXYGEN" -> {
                    holder.txtParamValue.text = "${parameter.paramValue} %"
                }
                "STRESS_INDEX" -> {
                    //holder.txtParamValue.text = parameter.paramObs
                    holder.txtParamValue.text = Utilities.convertStringToPascalCase(parameter.paramObs)
                }
                else -> {
                    holder.txtParamValue.text = parameter.paramValue
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun updateData(list: List<FaceScanDataModel>) {
        val abc = list.filter { it.paramCode != "BLOOD_OXYGEN" && it.paramCode != "STRESS_INDEX" }
        for (i in abc) {
            when (i.paramCode) {
                "HEART_RATE" -> i.paramId = 1
                "BREATHING_RATE" -> i.paramId = 2
                "BMI" -> i.paramId = 3
                "BP" -> i.paramId = 4
            }
        }
        this.parametersList.clear()
        this.parametersList.addAll(abc.sortedBy { it.paramId })
        Utilities.printData("FaceScanResultList",parametersList.toMutableList())
        this.notifyDataSetChanged()
    }

    inner class FaceScanDashboardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemWellfieDashboardBinding.bind(view)
        var layoutParameter = binding.layoutWellfieParameter
        var imgParam = binding.imgParameter
        var txtParamName = binding.txtParameter
        var txtParamValue = binding.txtParameterValue
    }

}