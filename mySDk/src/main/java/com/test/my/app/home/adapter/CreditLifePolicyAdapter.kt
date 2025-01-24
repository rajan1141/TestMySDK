package com.test.my.app.home.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.ImageViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.test.my.app.R
import com.test.my.app.common.utils.Utilities
import com.test.my.app.databinding.ItemCreditLifePolicyBinding
import com.test.my.app.model.sudLifePolicy.SudPolicyByMobileNumberModel

class CreditLifePolicyAdapter(val context: Context,
                              private val listener:OnCreditLifePolicyListener) : RecyclerView.Adapter<CreditLifePolicyAdapter.CreditLifePolicyHolder>() {

    private val creditLifePolicyList: MutableList<SudPolicyByMobileNumberModel.Record> = mutableListOf()

    override fun getItemCount(): Int = creditLifePolicyList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CreditLifePolicyHolder =
        CreditLifePolicyHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_credit_life_policy,parent,false))

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: CreditLifePolicyHolder, position: Int) {
        val creditLifePolicy = creditLifePolicyList[position]
        //holder.txtPlanName.tag = creditLifePolicy.policyNumber
        holder.txtPlanName.text = creditLifePolicy.planName
        holder.txtPolicyNumber.text = "${context.resources.getString(R.string.POLICY_NO)} ${creditLifePolicy.policyNumber}"
        if ( creditLifePolicy.isSelected ) {
            listener.onCreditLifePolicyClick(position,creditLifePolicy)
            holder.imgCheck.setImageResource(R.drawable.ic_radio_button_checked)
            ImageViewCompat.setImageTintList(holder.imgCheck,
                ColorStateList.valueOf(context.resources.getColor(R.color.colorPrimary)))
        } else {
            holder.imgCheck.setImageResource(R.drawable.ic_radio_button_unchecked)
            ImageViewCompat.setImageTintList(holder.imgCheck,
                ColorStateList.valueOf(context.resources.getColor(R.color.vivant_greyish)))
        }

        holder.layoutOption.setOnClickListener {
            refreshList(creditLifePolicy)
        }
    }

    fun updateList(items: List<SudPolicyByMobileNumberModel.Record>) {
        Utilities.printData("creditLifePolicyList",items)
        creditLifePolicyList.clear()
        creditLifePolicyList.addAll(items)
        notifyDataSetChanged()
    }

    private fun refreshList(policy:SudPolicyByMobileNumberModel.Record) {
        for ( i in creditLifePolicyList ) {
            if ( i.policyNumber == policy.policyNumber ) {
                i.isSelected = true
            } else {
                i.isSelected = false
            }
        }
        notifyDataSetChanged()
    }

    interface OnCreditLifePolicyListener {
        fun onCreditLifePolicyClick(position:Int,record:SudPolicyByMobileNumberModel.Record)
    }

    inner class CreditLifePolicyHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemCreditLifePolicyBinding.bind(view)
        val layoutOption = binding.layoutOption
        val imgCheck = binding.imgCheck
        val txtPlanName = binding.txtPlanName
        val txtPolicyNumber = binding.txtPolicyNumber
    }

}

