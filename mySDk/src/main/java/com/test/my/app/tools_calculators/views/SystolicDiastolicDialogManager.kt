package com.test.my.app.tools_calculators.views

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.Window
import com.test.my.app.R
import com.test.my.app.common.utils.ParameterDataModel
import com.test.my.app.common.utils.Utilities
import com.test.my.app.databinding.DialogInputParameterCombinedBinding
import com.google.android.material.tabs.TabLayout
import java.util.Objects

class SystolicDiastolicDialogManager(
    context: Context, listener: OnDialogValueListener,
    list: MutableList<ParameterDataModel>, pos: Int
) : Dialog(context) {

    private lateinit var binding: DialogInputParameterCombinedBinding

    private var onDialogValueListener: OnDialogValueListener? = null
    private var paramList: MutableList<ParameterDataModel> = mutableListOf()
    private var position = 0

    init {
        this.onDialogValueListener = listener
        this.paramList = list
        this.position = pos
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = DialogInputParameterCombinedBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        init()
    }

    fun init() {
        try {
            if (!Utilities.isNullOrEmpty(paramList[2].finalValue)) {
                binding.editBox1.setText(paramList[2].finalValue)
                Utilities.printLogError("Systolic--->${paramList[2].finalValue}")
            }
            if (!Utilities.isNullOrEmpty(paramList[3].finalValue)) {
                binding.editBox2.setText(paramList[3].finalValue)
                Utilities.printLogError("Diastolic--->${paramList[3].finalValue}")
            }
            if (position == 1) {
                Objects.requireNonNull(binding.layoutTab.getTabAt(0))!!.select()
                binding.systolicBpLayout.visibility = View.VISIBLE
                binding.diastolicBpLayout.visibility = View.GONE
                binding.editBox1.requestFocus()
                Utilities.showKeyboard(binding.editBox1, context)
            } else if (position == 3) {
                Objects.requireNonNull(binding.layoutTab.getTabAt(1))!!.select()
                binding.diastolicBpLayout.visibility = View.VISIBLE
                binding.systolicBpLayout.visibility = View.GONE
                binding.editBox2.requestFocus()
                Utilities.showKeyboard(binding.editBox2, context)
            }

            binding.layoutTab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    if (tab.position == 0) {
                        binding.systolicBpLayout.visibility = View.VISIBLE
                        binding.diastolicBpLayout.visibility = View.GONE
                        binding.editBox1.requestFocus()
                        Utilities.showKeyboard(binding.editBox1, context)
                    } else if (tab.position == 1) {
                        binding.diastolicBpLayout.visibility = View.VISIBLE
                        binding.systolicBpLayout.visibility = View.GONE
                        binding.editBox2.requestFocus()
                        Utilities.showKeyboard(binding.editBox2, context)
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab) {}
                override fun onTabReselected(tab: TabLayout.Tab) {}
            })
            setClickable()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setClickable() {

        binding.btnSaveInput.setOnClickListener {
            var isSystolic = false
            var isDiastolic = false
            var sys = 0.0
            var dia = 0.0

            if (!binding.editBox1.text.toString().equals("", ignoreCase = true)) {
                sys = binding.editBox1.text.toString().toDouble()
                isSystolic = sys in 10.0..350.0
            } else {
                isSystolic = true
            }

            if (!binding.editBox2.text.toString().equals("", ignoreCase = true)) {
                dia = binding.editBox2.text.toString().toDouble()
                isDiastolic = dia in 0.0..150.0
            } else {
                isDiastolic = true
            }

            if (!isSystolic && !isDiastolic) {
                Utilities.toastMessageShort(
                    context,
                    context.getString(R.string.PLEASE_INSERT_SYSTOLIC_BP_VALUE_BETWEEN_10_TO_300)
                )
            } else if (!isSystolic) {
                Utilities.toastMessageShort(
                    context,
                    context.getString(R.string.PLEASE_INSERT_SYSTOLIC_BP_VALUE_BETWEEN_10_TO_300)
                )
            } else if (!isDiastolic) {
                Utilities.toastMessageShort(
                    context,
                    context.getString(R.string.PLEASE_INSERT_DIASTOLIC_BP_VALUE_BETWEEN_0_TO_150)
                )
            }

            if (isSystolic && isDiastolic) {
                if (sys != 0.0 && dia != 0.0 && dia >= sys) {
                    Utilities.toastMessageShort(
                        context,
                        context.getString(R.string.DIASTOLIC_VALUE_SHOULD_BE_LESS_THAN_SYSTOLIC_VALUE)
                    )
                    isDiastolic = false
                }
            }
            if (isSystolic && isDiastolic) {
                onDialogValueListener!!.onDialogValueListener(
                    binding.editBox1.text.toString(),
                    binding.editBox2.text.toString()
                )
                dismiss()
            }

        }

        binding.imgCloseInput.setOnClickListener {
            dismiss()
        }

    }

    interface OnDialogValueListener {
        fun onDialogValueListener(systolic: String, diastolic: String)
    }

}