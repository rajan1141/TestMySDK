package com.test.my.app.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.test.my.app.R
import com.test.my.app.common.constants.Constants
import com.test.my.app.databinding.ItemPolicyOptionBinding
import com.test.my.app.home.common.DataHandler

class PolicyOptionsAdapter(val context: Context,
                           private val listener: OnPolicyOptionClickListener) : RecyclerView.Adapter<PolicyOptionsAdapter.PolicyOptionsAdapterViewHolder>() {

    private val policyOptionsList: MutableList<DataHandler.Option> = mutableListOf()
    override fun getItemCount(): Int = policyOptionsList.size
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PolicyOptionsAdapterViewHolder {
        return PolicyOptionsAdapterViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_policy_option, parent, false))
    }

    override fun onBindViewHolder(holder: PolicyOptionsAdapter.PolicyOptionsAdapterViewHolder, position: Int) {
        val policy = policyOptionsList[position]
        holder.txtPolicyOption.text = policy.title
        holder.imgPolicyOption.setImageResource(policy.imageId)
        //holder.imgPolicyOption.backgroundTintList = ContextCompat.getColorStateList(context,policy.color)

        when(policy.code) {
            Constants.OPTION_WHATS_APP_BOT,Constants.OPTION_CALL_CENTRE -> {
                holder.imgPolicyOption.setColorFilter(ContextCompat.getColor(context,policy.color))
            }
        }

        holder.layoutPolicyOption.setOnClickListener {
            listener.onPolicyOptionClick(policy)
        }
    }

    fun updateList(items: MutableList<DataHandler.Option>) {
        policyOptionsList.clear()
        policyOptionsList.addAll(items)
        notifyDataSetChanged()
    }

    inner class PolicyOptionsAdapterViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemPolicyOptionBinding.bind(view)
        val layoutPolicyOption = binding.layoutPolicyOption
        val txtPolicyOption = binding.txtPolicyOption
        val imgPolicyOption = binding.imgPolicyOption
    }

    interface OnPolicyOptionClickListener {
        fun onPolicyOptionClick(policy: DataHandler.Option)
    }

}