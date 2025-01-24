package com.test.my.app.home.ui

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.ViewGroup
import android.view.Window
import com.test.my.app.common.utils.Utilities
import com.test.my.app.databinding.DialogAppUpdateBinding
import com.test.my.app.model.entity.AppVersion


class DialogUpdateApp(context: Context, versionDetails: AppVersion) : Dialog(context) {

    private lateinit var binding: DialogAppUpdateBinding

    private var appVersion: AppVersion? = null

    init {
        this.appVersion = versionDetails
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        //binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.dialog_app_update, null, false)
        binding = DialogAppUpdateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setContentView(binding.root)
        window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window!!.currentFocus
        setCancelable(false)
        setCanceledOnTouchOutside(false)

        setClickable()
    }

    private fun setClickable() {

        binding.txtUpdateDescription.text = appVersion!!.description

        binding.btnLeftUpdate.setOnClickListener {
            dismiss()
        }

        binding.btnRightUpdate.setOnClickListener {
            Utilities.goToPlayStore(context)
        }

    }

}