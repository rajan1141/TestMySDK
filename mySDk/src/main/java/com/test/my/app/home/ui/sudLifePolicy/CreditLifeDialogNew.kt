package com.test.my.app.home.ui.sudLifePolicy

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.ViewGroup
import android.view.Window
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import com.test.my.app.R
import com.test.my.app.common.utils.Utilities
import com.test.my.app.databinding.DialogCreditLifeNewBinding
import com.test.my.app.home.adapter.CreditLifePolicyAdapter
import com.test.my.app.model.sudLifePolicy.SudPolicyByMobileNumberModel

class CreditLifeDialogNew(private val mContext: Context,
                          private val fragment:SudLifePolicyDashboardFragment,
                          private val policyList : MutableList<SudPolicyByMobileNumberModel.Record>) : Dialog(mContext) , CreditLifePolicyAdapter.OnCreditLifePolicyListener {

    private lateinit var binding: DialogCreditLifeNewBinding

    private var creditLifePolicyAdapter: CreditLifePolicyAdapter? = null
    var policy = SudPolicyByMobileNumberModel.Record()
    private var animation: LayoutAnimationController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = DialogCreditLifeNewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window!!.currentFocus
        setCancelable(false)
        setCanceledOnTouchOutside(false)
        try {
            init()
            setClickable()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun init() {
        animation = AnimationUtils.loadLayoutAnimation(mContext,R.anim.layout_animation_slide_from_bottom)
        creditLifePolicyAdapter = CreditLifePolicyAdapter(mContext,this)
        binding.rvRiskoMeterOptions.layoutAnimation = animation
        binding.rvRiskoMeterOptions.setExpanded(true)
        binding.rvRiskoMeterOptions.adapter = creditLifePolicyAdapter
        creditLifePolicyAdapter!!.updateList(policyList)
    }

    private fun setClickable() {
        binding.btnDonePicker.setOnClickListener {
            if ( !Utilities.isNullOrEmpty(policy.policyNumber) ) {
                dismiss()
                fragment.showCreditLifePasswordDialog(policy)
            } else {
                Utilities.toastMessageShort(mContext,mContext.resources.getString(R.string.ERROR_SELECT_POLICY))
            }
        }

        binding.imgClose.setOnClickListener {
            dismiss()
        }
    }

    override fun onCreditLifePolicyClick(position: Int,record:SudPolicyByMobileNumberModel.Record) {
        Utilities.printData("record",record)
        policy = record
    }

}