package com.test.my.app.home.ui.nimeya

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.test.my.app.common.base.BaseFragment
import com.test.my.app.common.base.BaseViewModel
import com.test.my.app.common.constants.Constants
import com.test.my.app.common.utils.DateHelper
import com.test.my.app.common.utils.Utilities
import com.test.my.app.R
import com.test.my.app.home.common.NimeyaSingleton
import com.test.my.app.databinding.FragmentProtectoMeterResultBinding
import com.test.my.app.home.viewmodel.NimeyaViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProtectoMeterResultFragment : BaseFragment() {

    private val viewModel: NimeyaViewModel by lazy {
        ViewModelProvider(this)[NimeyaViewModel::class.java]
    }
    private lateinit var binding: FragmentProtectoMeterResultBinding

    private var nimeyaSingleton = NimeyaSingleton.getInstance()!!
    private var from = ""
    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            if ( it.containsKey(Constants.FROM) ) {
                from = it.getString(Constants.FROM)!!
            }
            Utilities.printLogError("from--->$from")
        }
        // callback to Handle back button event
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                nimeyaSingleton.clearData()
                requireActivity().finish()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this,callback)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentProtectoMeterResultBinding.inflate(inflater, container, false)
        try {
            initialise()
        } catch ( e : Exception ) {
            e.printStackTrace()
        }
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    private fun initialise() {
        if ( from.equals(Constants.NIMEYA_PROTECTO_METER_RESULT,ignoreCase = true) ) {
            val protectoMeterHistory = nimeyaSingleton.protectoMeterHistory
            Utilities.printData("ProtectoMeterHistory",protectoMeterHistory,true)
            if ( !Utilities.isNullOrEmpty(protectoMeterHistory.dateTime!!) ) {
                binding.txtDate.text = "${resources.getString(R.string.AS_ON)} " + DateHelper.convertDateSourceToDestination(protectoMeterHistory.dateTime!!,DateHelper.DATE_FORMAT_UTC,DateHelper.DATEFORMAT_DDMMMYYYY_NEW)
            } else {
                binding.txtDate.text = "${resources.getString(R.string.AS_ON)} " + DateHelper.currentDateAsStringddMMMyyyyNew
            }
            binding.speedViewLifeInsurance.withTremble = false
            binding.speedViewLifeInsurance.speedTo(protectoMeterHistory.lifeInsuranceScore!!.toFloat(),1500)
            binding.txtScoreLifeInsurance.text = protectoMeterHistory.lifeInsuranceScore!!.toString()
            binding.txtRiskMeterLifeInsurance.text = protectoMeterHistory.lifeInsuranceScoreText
            binding.txtCurrentLifeInsuranceCoverValue.text = " :  ${resources.getString(R.string.INDIAN_RUPEE)} " + Utilities.formatNumberDecimalWithComma(protectoMeterHistory.lifeInsuranceCover!!.toDouble().toInt())
            binding.txtAdditionalLifeInsuranceValue.text = " :  ${resources.getString(R.string.INDIAN_RUPEE)} " + Utilities.formatNumberDecimalWithComma(protectoMeterHistory.lifeInsuranceNeed!!.toDouble().toInt())

            binding.speedViewHealthInsurance.withTremble = false
            binding.speedViewHealthInsurance.speedTo(protectoMeterHistory.healthInsuranceScore!!.toFloat(),1500)
            binding.txtScoreHealthInsurance.text = protectoMeterHistory.healthInsuranceScore!!.toString()
            binding.txtRiskMeterHealthInsurance.text = protectoMeterHistory.healthInsuranceScoreText
            binding.txtCurrentHealthInsuranceCoverValue.text = " :  ${resources.getString(R.string.INDIAN_RUPEE)} " + Utilities.formatNumberDecimalWithComma(protectoMeterHistory.healthInsuranceCover!!.toDouble().toInt())
            binding.txtAdditionalHealthInsuranceValue.text = " :  ${resources.getString(R.string.INDIAN_RUPEE)} " + Utilities.formatNumberDecimalWithComma(protectoMeterHistory.healthInsuranceNeed!!.toDouble().toInt())
        } else {
            val saveProtectoMeter = nimeyaSingleton.saveProtectoMeter
            binding.txtDate.text = "${resources.getString(R.string.AS_ON)} " + DateHelper.currentDateAsStringddMMMyyyyNew
            binding.speedViewLifeInsurance.withTremble = false
            binding.speedViewLifeInsurance.speedTo(saveProtectoMeter.data.lifeInsuranceScore!!.toFloat(),1500)
            binding.txtScoreLifeInsurance.text = saveProtectoMeter.data.lifeInsuranceScore!!.toString()
            binding.txtRiskMeterLifeInsurance.text = saveProtectoMeter.data.lifeInsuranceScoreText
            binding.txtCurrentLifeInsuranceCoverValue.text = " :  ${resources.getString(R.string.INDIAN_RUPEE)} " + Utilities.formatNumberDecimalWithComma(saveProtectoMeter.data.lifeInsuranceCover!!.toDouble().toInt())
            binding.txtAdditionalLifeInsuranceValue.text = " :  ${resources.getString(R.string.INDIAN_RUPEE)} " + Utilities.formatNumberDecimalWithComma(saveProtectoMeter.data.lifeInsuranceNeed!!.toDouble().toInt())

            binding.speedViewHealthInsurance.withTremble = false
            binding.speedViewHealthInsurance.speedTo(saveProtectoMeter.data.healthInsuranceScore!!.toFloat(),1500)
            binding.txtScoreHealthInsurance.text = saveProtectoMeter.data.healthInsuranceScore!!.toString()
            binding.txtRiskMeterHealthInsurance.text = saveProtectoMeter.data.healthInsuranceScoreText
            binding.txtCurrentHealthInsuranceCoverValue.text = " :  ${resources.getString(R.string.INDIAN_RUPEE)} " + Utilities.formatNumberDecimalWithComma(saveProtectoMeter.data.healthInsuranceCover!!.toDouble().toInt())
            binding.txtAdditionalHealthInsuranceValue.text = " :  ${resources.getString(R.string.INDIAN_RUPEE)} " + Utilities.formatNumberDecimalWithComma(saveProtectoMeter.data.healthInsuranceNeed!!.toDouble().toInt())
        }

        binding.btnRestart.setOnClickListener {
            val bundle = Bundle()
            bundle.putString(Constants.FROM,Constants.NIMEYA_PROTECTO_METER_RESULT)
            findNavController().navigate(R.id.action_protectoMeterResultFragment_to_protectoMeterInputFragment,bundle)
        }
    }

}