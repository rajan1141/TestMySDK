package com.test.my.app.wyh.faceScan.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Html
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.test.my.app.R
import com.test.my.app.common.base.BaseActivity
import com.test.my.app.common.base.BaseViewModel
import com.test.my.app.common.constants.CleverTapConstants
import com.test.my.app.common.constants.Constants
import com.test.my.app.common.constants.NavigationConstants
import com.test.my.app.common.extension.openAnotherActivity
import com.test.my.app.common.utils.AppColorHelper
import com.test.my.app.common.utils.CleverTapHelper
import com.test.my.app.common.utils.DateHelper
import com.test.my.app.common.utils.DefaultNotificationDialog
import com.test.my.app.common.utils.Utilities
import com.test.my.app.common.utils.showDialog
import com.test.my.app.databinding.ActivityFaceScanResultBinding
import com.test.my.app.home.adapter.WellfieResultAdapter
import com.test.my.app.home.common.DataHandler
import com.test.my.app.wyh.common.FaceScanSingleton
import com.test.my.app.wyh.faceScan.adapter.FaceScanDataAdapter
import com.test.my.app.wyh.faceScan.viewmodel.WyhFaceScanViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FaceScanResultActivity : BaseActivity(),WellfieResultAdapter.OnWellfieParameterClickListener,
    DefaultNotificationDialog.OnDialogValueListener {

    private lateinit var binding: ActivityFaceScanResultBinding
    private val appColorHelper = AppColorHelper.instance!!
    private val wyhFaceScanViewModel: WyhFaceScanViewModel by lazy {
        ViewModelProvider(this)[WyhFaceScanViewModel::class.java]
    }
    private var from = ""
    private var faceScanDataAdapter: FaceScanDataAdapter? = null
    private val faceScanSingleton = FaceScanSingleton.getInstance()!!

    private val onBackPressedCallBack = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            //finish()
            routeToHomeScreen()
        }
    }

    override fun getViewModel(): BaseViewModel = wyhFaceScanViewModel

    override fun onCreateEvent(savedInstanceState: Bundle?) {
        binding = ActivityFaceScanResultBinding.inflate(layoutInflater)
        setContentView(binding.root)
        onBackPressedDispatcher.addCallback(this, onBackPressedCallBack)
        try {
            if (intent.hasExtra(Constants.FROM)) {
                from = intent.getStringExtra(Constants.FROM)!!
            }
            Utilities.printLogError("from--->$from")
            CleverTapHelper.pushEvent(this, CleverTapConstants.SCAN_VITALS_RESULT)
            setupToolbar()
            initialise()
            setClickable()
            //setObserver()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initialise() {
        if (from.equals(Constants.DASHBOARD, ignoreCase = true)) {
            binding.toolBarView.toolbarTitle.text = resources.getString(R.string.PAST_VITALS)
            binding.btnRescanVitals.visibility = View.VISIBLE
            binding.txtLastUpdatedDateTime.visibility = View.VISIBLE
        } else {
            binding.toolBarView.toolbarTitle.text = resources.getString(R.string.CURRENT_VITALS)
            binding.btnRescanVitals.visibility = View.GONE
            binding.txtLastUpdatedDateTime.visibility = View.GONE
        }

        //binding.txtLastUpdatedDateTime.text = "${resources.getString(R.string.LAST_UPDATED)} : ${DateHelper.currentDateAsStringddMMMyyyyNew} - ${DateHelper.currentTimeAs_am_pm()}"
        binding.txtLastUpdatedDateTime.text = "${resources.getString(R.string.LAST_UPDATED)} : ${DateHelper.convertDateSourceToDestination(faceScanSingleton.dateTime, DateHelper.DATE_FORMAT_UTC, DateHelper.DATE_TIME_FORMAT_NEW)}"

        faceScanDataAdapter = FaceScanDataAdapter(this)
        binding.rvFaceScanResult.layoutManager = GridLayoutManager(this, 2)
        binding.rvFaceScanResult.adapter = faceScanDataAdapter
        faceScanDataAdapter!!.updateData(faceScanSingleton.faceScanData)

        val strLearnMore = "<u><a><B><font color='#00cfff'>" + resources.getString(R.string.LEARN_MORE) + "</font></B></a></u>"
        binding.txtDisclaimer.text = Html.fromHtml(resources.getString(R.string.WELLFIE_DISCLAIMER_LABEL) + " " + strLearnMore,Html.FROM_HTML_MODE_LEGACY)
    }

    private fun setClickable() {

        binding.btnRescanVitals.setOnClickListener {
            openAnotherActivity(destination = NavigationConstants.BEGIN_VITALS_ACTIVITY)
        }

        binding.txtDisclaimer.setOnClickListener {
            showDialog(
                listener = this,
                title = resources.getString(R.string.DISCLAIMER),
                message = "<a><font color='#3A393B'>" + resources.getString(R.string.WELLFIE_DISCLAIMER1) + "</font></a>" + "<a><B><font color='#000000'>" + resources.getString(
                    R.string.WELLFIE_DISCLAIMER2
                ) + "</font></B></a>",
                showLeftBtn = false
            )
        }

    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolBarView.toolbarCommon)
        //binding.toolBarView.toolbarTitle.text = ""
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)

        binding.toolBarView.toolbarCommon.navigationIcon?.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(appColorHelper.textColor, BlendModeCompat.SRC_ATOP)

        binding.toolBarView.toolbarCommon.setNavigationOnClickListener {
            onBackPressedCallBack.handleOnBackPressed()
        }
    }

    private fun routeToHomeScreen() {
        openAnotherActivity(destination = NavigationConstants.HOME, clearTop = true)
    }

    override fun onWellfieParameterClick(wellfieResultModel: DataHandler.WellfieResultModel) { }

    override fun onDialogClickListener(isButtonLeft: Boolean, isButtonRight: Boolean) { }

/*    private fun setObserver() {
        wyhFaceScanViewModel.getFaceScanVitals.observe(this) {
            if (it.status == Resource.Status.SUCCESS) {
                //wellfieResultAdapter!!.updateData(wellfieSingleton.getWellfieResultList())
                //faceScanDataAdapter!!.updateData(it.data!!.data!!)
                //Utilities.printData("Response",request,true)
                val faceScanData = it.data!!.data!!
                val parametersList : MutableList<FaceScanDataModel> = mutableListOf()

                binding.txtLastUpdatedDateTime.text = "${resources.getString(R.string.LAST_UPDATED)} : ${DateHelper.convertDateSourceToDestination(faceScanData.CreatedOn, DateHelper.DATE_FORMAT_UTC, DateHelper.DATE_TIME_FORMAT_NEW)}"

                parametersList.add(FaceScanDataModel(1, "BP", "Blood Pressure", "${faceScanData.systolic} / ${faceScanData.diastolic}"))
                parametersList.add(FaceScanDataModel(2, "STRESS_INDEX","Stress Index", faceScanData.stressStatus!!))
                parametersList.add(FaceScanDataModel(3, "HEART_RATE","Heart Rate", faceScanData.heartRate.toString()))
                parametersList.add(FaceScanDataModel(4, "BREATHING_RATE","Breathing Rate", faceScanData.respiratoryRate!!.toInt().toString()))
                parametersList.add(FaceScanDataModel(5, "BLOOD_OXYGEN","Blood Oxygen", faceScanData.oxygen!!.toInt().toString()))
                parametersList.add(FaceScanDataModel(6, "BMI","BMI", String.format(Locale.ENGLISH,"%.1f", CalculateParameters.roundOffPrecision(CalculateParameters.getBMI(faceScanData.height!!.toDouble(), faceScanData.weight!!.toDouble()), 1))))
                faceScanDataAdapter!!.updateData(parametersList)
            }
        }
    }*/

}