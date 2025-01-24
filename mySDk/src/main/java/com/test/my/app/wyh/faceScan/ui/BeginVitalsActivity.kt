package com.test.my.app.wyh.faceScan.ui

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.lifecycle.ViewModelProvider
import com.test.my.app.R
import com.test.my.app.common.base.BaseActivity
import com.test.my.app.common.base.BaseViewModel
import com.test.my.app.common.constants.Constants
import com.test.my.app.common.constants.NavigationConstants
import com.test.my.app.common.extension.openAnotherActivity
import com.test.my.app.common.utils.AppColorHelper
import com.test.my.app.common.utils.HeightWeightDialog
import com.test.my.app.common.utils.ParameterDataModel
import com.test.my.app.common.utils.Utilities
import com.test.my.app.databinding.ActivityBeginVitalsBinding
import com.test.my.app.repository.utils.Resource
import com.test.my.app.wyh.faceScan.viewmodel.WyhFaceScanViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class BeginVitalsActivity : BaseActivity(), HeightWeightDialog.OnDialogValueListener {

    private lateinit var binding: ActivityBeginVitalsBinding
    private val appColorHelper = AppColorHelper.instance!!
    private var capturedHeight: Double = 0.0
    private var capturedWeight: Double = 0.0
    private var isChanged = false

    private val wyhFaceScanViewModel: WyhFaceScanViewModel by lazy {
        ViewModelProvider(this)[WyhFaceScanViewModel::class.java]
    }

    private val onBackPressedCallBack = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            finish()
        }
    }

/*    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_begin_vitals)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }*/

    override fun getViewModel(): BaseViewModel = wyhFaceScanViewModel

    override fun onCreateEvent(savedInstanceState: Bundle?) {
        binding = ActivityBeginVitalsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        onBackPressedDispatcher.addCallback(this, onBackPressedCallBack)
        try {
            setupToolbar()
            initialise()
            setClickable()
            setObserver()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun initialise() {
        wyhFaceScanViewModel.callIsBMIExist()
        binding.layHeight.setTitle(resources.getString(R.string.HEIGHT))
        binding.layWeight.setTitle(resources.getString(R.string.WEIGHT))
        binding.layHeight.setUnit(resources.getString(R.string.CM))
        binding.layWeight.setUnit(resources.getString(R.string.KG))
    }

    private fun setClickable() {

        binding.layHeight.setOnClickListener {
            showHeightDialog(capturedHeight.toInt(), "cm")
        }

        binding.layHeight.editText!!.setOnClickListener {
            showHeightDialog(capturedHeight.toInt(), "cm")
        }

        binding.layHeight.editText!!.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                showHeightDialog(capturedHeight.toInt(), "cm")
            }
        }

        binding.layWeight.setOnClickListener {
            showWeightDialog(capturedWeight)
        }

        binding.layWeight.editText!!.setOnClickListener {
            showWeightDialog(capturedWeight)
        }

        binding.layWeight.editText!!.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                showWeightDialog(capturedWeight)
            }
        }

        binding.btnNext.setOnClickListener {
            if (Utilities.isNullOrEmptyOrZero(capturedHeight.toString())) {
                Utilities.toastMessageShort(this, resources.getString(R.string.PLEASE_SELECT_HEIGHT))
            } else if (Utilities.isNullOrEmptyOrZero(capturedWeight.toString())) {
                Utilities.toastMessageShort(this, resources.getString(R.string.PLEASE_SELECT_WEIGHT))
            } else {
                wyhFaceScanViewModel.callGetSocketKeysApi()
            }
        }

    }

    private fun setObserver() {
        wyhFaceScanViewModel.bmiDetails.observe(this) {
            if (it.status == Resource.Status.SUCCESS) {
                if ( !Utilities.isNullOrEmpty(it.data!!.BMI.Height) ) {
                    capturedHeight = it.data.BMI.Height!!.toDouble()
                    binding.layHeight.setValue(capturedHeight.toInt().toString())
                }
                if ( !Utilities.isNullOrEmpty(it.data.BMI.Weight) ) {
                    capturedWeight = it.data.BMI.Weight!!.toDouble()
                    binding.layWeight.setValue(capturedWeight.toInt().toString())
                }
            }
        }

        wyhFaceScanViewModel.getSocketKeys.observe(this) {
            if (it.status == Resource.Status.SUCCESS) {
                val sockets = it.data!!.data
                if ( sockets.isNotEmpty() && !Utilities.isNullOrEmpty(sockets[0].value) ) {
                    if (isChanged) {
                        wyhFaceScanViewModel.saveParameter(this,
                            capturedHeight.toString(),
                            capturedWeight.toString(),
                            sockets[0].value!!
                        )
                    } else {
                        navigateToFaceScan(sockets[0].value!!)
                    }
                }
            }
        }

        wyhFaceScanViewModel.saveParam.observe(this) {}
    }

    fun navigateToFaceScan(socketToken:String) {
        openAnotherActivity(destination = NavigationConstants.FACE_SCAN_ACTIVITY) {
            putString(Constants.HEIGHT,capturedHeight.toString())
            putString(Constants.WEIGHT,capturedWeight.toString())
            putString(Constants.SOCKET_URL, Constants.WYH_FACE_SCAN_URL)
            putString(Constants.SOCKET_TOKEN, socketToken)
        }
    }

    private fun showHeightDialog(height: Int, savedUnit: String) {
        val data = ParameterDataModel()
        data.title = resources.getString(R.string.HEIGHT)
        data.value = " - - "
        data.finalValue = height.toString()
        if (savedUnit.equals("cm", true)) {
            data.unit = resources.getString(R.string.CM)
        } else {
            data.unit = resources.getString(R.string.FEET_INCH)
        }
        data.code = "HEIGHT"
        val heightWeightDialog = HeightWeightDialog(this, this, "Height", data)
        heightWeightDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        heightWeightDialog.show()
    }

    private fun showWeightDialog(weight: Double, savedUnit: String = "kg") {
        val data = ParameterDataModel()
        data.title = resources.getString(R.string.WEIGHT)
        data.value = " - - "
        if (Utilities.isNullOrEmptyOrZero(weight.toString())) {
            data.finalValue = "50.0"
        } else {
            data.finalValue = weight.toString()
        }

        if (!savedUnit.equals("kg", true)) {
            data.unit = resources.getString(R.string.LBS)
        } else {
            data.unit = resources.getString(R.string.KG)
        }
        data.code = "WEIGHT"
        val heightWeightDialog = HeightWeightDialog(this, this, "Weight", data)
        heightWeightDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        heightWeightDialog.show()
    }

    override fun onDialogValueListener(dialogType: String, height: String, weight: String, unit: String, visibleValue: String) {
        if (dialogType.equals("Height", ignoreCase = true)) {
            if (capturedHeight != height.toDouble()) {
                isChanged = true
            }
            this.capturedHeight = height.toDouble()
            binding.layHeight.setValue(visibleValue)
            binding.layHeight.setUnit(unit.lowercase(Locale.ROOT))
            Utilities.printLog("Height::visibleValue----> $height , $visibleValue")
        } else {
            if (capturedWeight != weight.toDouble()) {
                isChanged = true
            }
            this.capturedWeight = weight.toDouble()
            binding.layWeight.setValue(visibleValue)
            binding.layWeight.setUnit(unit.lowercase(Locale.ROOT))
            Utilities.printLog("Weight::visibleValue----> $weight , $visibleValue")
        }
        Utilities.printLogError("isChanged---->$isChanged")
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolBarView.toolbarCommon)
        binding.toolBarView.toolbarTitle.text = ""
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)

        binding.toolBarView.toolbarCommon.navigationIcon?.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(appColorHelper.textColor, BlendModeCompat.SRC_ATOP)

        binding.toolBarView.toolbarCommon.setNavigationOnClickListener {
            onBackPressedCallBack.handleOnBackPressed()
        }
    }

}