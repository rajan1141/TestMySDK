package com.test.my.app.common.base

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import com.test.my.app.R
import com.test.my.app.common.constants.Constants
import com.test.my.app.databinding.CustumProgressBarBinding


class CustomProgressBar(context: Context) : Dialog(context, R.style.TransparentProgressDialog) {

    private lateinit var binding: CustumProgressBarBinding

    init {
        val wlmp = window!!.attributes
        wlmp.gravity = Gravity.CENTER_HORIZONTAL
        window!!.attributes = wlmp
        setTitle(null)
        setCancelable(false)
        setCanceledOnTouchOutside(false)
        //setOnCancelListener(null)
        binding = CustumProgressBarBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //setContentView(R.layout.custum_progress_bar)
    }

    fun setLoaderType(type: String) {

        when (type) {

            Constants.LOADER_DOWNLOAD -> {
                binding.imgCustomAnim.setAnimation(R.raw.download)
            }

            Constants.LOADER_UPLOAD -> {
                binding.imgCustomAnim.setAnimation(R.raw.upload)
            }

            Constants.LOADER_DIGITIZE -> {
                binding.imgCustomAnim.setAnimation(R.raw.scan)
            }

            Constants.LOADER_DELETE -> {
                binding.imgCustomAnim.setAnimation(R.raw.delete)
            }

            Constants.LOADER_BLAST -> {
                binding.imgCustomAnim.setAnimation(R.raw.blast_2)
            }

            else -> {
                binding.imgCustomAnim.setAnimation(R.raw.default_loader)
            }

        }

    }

}
