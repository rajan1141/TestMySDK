package com.test.my.app.home.ui.aktivo

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import com.test.my.app.R
import com.test.my.app.common.constants.Constants
import com.test.my.app.common.utils.DateHelper
import com.test.my.app.common.utils.Utilities
import com.test.my.app.databinding.DialogAktivoBadgesBinding
import com.test.my.app.home.ui.HomeScreenNewFragment

class DialogAktivoBadges(val mContext: Context,val badge:String,
                         val selectedDate:String, val fragment : HomeScreenNewFragment) : Dialog(mContext) {

    private lateinit var binding: DialogAktivoBadgesBinding
    private var animation: Animation? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = DialogAktivoBadgesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setContentView(binding.root)
        window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window!!.currentFocus
        setCancelable(false)
        setCanceledOnTouchOutside(false)

        animation = AnimationUtils.loadAnimation(mContext,R.anim.anim_pulse_once)
        initialise()
    }

    @SuppressLint("SetTextI18n")
    private fun initialise() {
        if ( selectedDate == DateHelper.currentDateAsStringyyyyMMdd ) {
            binding.txtBadgeDate.text = "${mContext.resources.getString(R.string.TODAY)}, ${DateHelper.convertDateSourceToDestination(selectedDate,DateHelper.SERVER_DATE_YYYYMMDD,DateHelper.DATEFORMAT_DDMMMYYYY_NEW)}"
        } else {
            binding.txtBadgeDate.text = " ${DateHelper.convertDateSourceToDestination(selectedDate,DateHelper.SERVER_DATE_YYYYMMDD,DateHelper.DATEFORMAT_DDMMMYYYY_NEW)}"
        }
        binding.imgTickContender.visibility = View.INVISIBLE
        binding.imgTickChallenger.visibility = View.INVISIBLE
        binding.imgTickAchiever.visibility = View.INVISIBLE
        when (badge.uppercase()) {
            "CONTENDER" -> {
                binding.progressBadge.progress = 0
                binding.imgTickContender.visibility = View.VISIBLE
                binding.imgTickChallenger.visibility = View.INVISIBLE
                binding.imgTickAchiever.visibility = View.INVISIBLE
                animateBadgeView(binding.imgBadgeContender)
            }

            "CHALLENGER" -> {
                Utilities.setProgressWithAnimation(binding.progressBadge, 50, Constants.PROGRESS_DURATION)
                Handler(Looper.getMainLooper()).postDelayed({
                    binding.imgTickContender.visibility = View.VISIBLE
                    binding.imgTickChallenger.visibility = View.VISIBLE
                    binding.imgTickAchiever.visibility = View.INVISIBLE
                    animateBadgeView(binding.imgBadgeChallenger)
                }, (Constants.PROGRESS_DURATION).toLong())
            }

            "ACHIEVER" -> {
                Utilities.setProgressWithAnimation(binding.progressBadge, 100, Constants.PROGRESS_DURATION)
                Handler(Looper.getMainLooper()).postDelayed({
                    binding.imgTickContender.visibility = View.VISIBLE
                    binding.imgTickChallenger.visibility = View.VISIBLE
                    binding.imgTickAchiever.visibility = View.VISIBLE
                    animateBadgeView(binding.imgBadgeAchiever)
                }, (Constants.PROGRESS_DURATION).toLong())
            }
        }

        binding.btnViewDetails.setOnClickListener {
            dismiss()
            fragment.navigateToAktivo(Constants.AKTIVO_BADGES_CODE)
        }

        binding.imgClose.setOnClickListener {
            dismiss()
        }
    }

    private fun animateBadgeView(badge: ImageView) {
        badge.startAnimation(animation)
        //badge.clearAnimation()
    }

}