package com.test.my.app.wyh.faceScan.ui

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.test.my.app.R
import com.test.my.app.common.constants.CleverTapConstants
import com.test.my.app.common.constants.Constants
import com.test.my.app.common.constants.NavigationConstants
import com.test.my.app.common.extension.openAnotherActivity
import com.test.my.app.common.utils.CleverTapHelper
import com.test.my.app.common.utils.DateHelper
import com.test.my.app.common.utils.Utilities
import com.test.my.app.databinding.BottomSheetFaceScanVitalsBinding
import com.test.my.app.wyh.common.FaceScanSingleton
import com.test.my.app.wyh.faceScan.adapter.FaceScanDashboardAdapter
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class FaceScanVitalsBottomSheet : BottomSheetDialogFragment() {

    private lateinit var binding: BottomSheetFaceScanVitalsBinding
    private val faceScanSingleton = FaceScanSingleton.getInstance()!!
    private var faceScanDashboardAdapter: FaceScanDashboardAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = BottomSheetFaceScanVitalsBinding.inflate(inflater, container, false)
        initialise()
        setClickable()
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    private fun initialise() {
        binding.txtLastScannedDate.text = DateHelper.convertDateSourceToDestination(faceScanSingleton.dateTime, DateHelper.DATE_FORMAT_UTC, DateHelper.DATE_TIME_FORMAT_NEW)

        val oxygen = faceScanSingleton.faceScanData.find { it.paramCode == "BLOOD_OXYGEN" }!!.paramValue
        binding.txtOxygen.text = "$oxygen %"
        binding.progressOxygen.progress = oxygen.toInt()

        binding.txtStress.text = Utilities.convertStringToPascalCase(faceScanSingleton.faceScanData.find { it.paramCode == "STRESS_INDEX" }!!.paramObs)

        faceScanDashboardAdapter = FaceScanDashboardAdapter(requireContext())
        binding.rvScanVitals.setExpanded(true)
        binding.rvScanVitals.adapter = faceScanDashboardAdapter
        faceScanDashboardAdapter!!.updateData(faceScanSingleton.faceScanData)
    }

    private fun setClickable() {

        binding.btnRescanVitals.setOnClickListener {
            dismiss()
            val data = HashMap<String, Any>()
            data[CleverTapConstants.FROM] = CleverTapConstants.RESCAN
            CleverTapHelper.pushEventWithProperties(requireContext(), CleverTapConstants.SCAN_YOUR_VITALS, data)
            openAnotherActivity(destination = NavigationConstants.BEGIN_VITALS_ACTIVITY) {
                putString(Constants.FROM, Constants.DASHBOARD)
            }
        }

        binding.imgClose.setOnClickListener {
            dismiss()
        }
    }

    override fun getTheme(): Int {
        //return super.getTheme();
        return R.style.BottomSheetDialog
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(),theme)
    }

    companion object {
        const val TAG = "ModalBottomSheet"
    }

}