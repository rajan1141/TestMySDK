package com.test.my.app.wyh.faceScan.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Html
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import com.test.my.app.databinding.ActivityFaceScanBinding
import com.test.my.app.home.ui.wellfie.PermissionManager

import com.test.my.app.R
import com.test.my.app.common.base.BaseActivity
import com.test.my.app.common.base.BaseViewModel
import com.test.my.app.common.constants.Constants
import com.test.my.app.common.constants.NavigationConstants
import com.test.my.app.common.extension.openAnotherActivity
import com.test.my.app.common.utils.CalculateParameters
import com.test.my.app.common.utils.DefaultNotificationDialog
import com.test.my.app.common.utils.Utilities
import com.test.my.app.common.utils.showDialog
import com.test.my.app.home.common.DataHandler.FaceScanDataModel
import com.test.my.app.model.wyh.faceScan.AddFaceScanVitalsModel
import com.test.my.app.repository.utils.Resource
import com.test.my.app.wyh.common.FaceScanData
import com.test.my.app.wyh.common.FaceScanHrvResponse
import com.test.my.app.wyh.common.FaceScanResponse
import com.test.my.app.wyh.common.FaceScanSingleton
import com.test.my.app.wyh.common.MeasurementStatusResponse
import com.test.my.app.wyh.faceScan.viewmodel.WyhFaceScanViewModel
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale
import kotlin.math.roundToInt

@AndroidEntryPoint
class FaceScanActivity : BaseActivity() , DefaultNotificationDialog.OnDialogValueListener {

    private lateinit var binding: ActivityFaceScanBinding
    private val wyhFaceScanViewModel: WyhFaceScanViewModel by lazy {
        ViewModelProvider(this)[WyhFaceScanViewModel::class.java]
    }

    private var isRecording = false
  /*  private lateinit var camera: RppgCameraView
    private val socketManager = RppgSocketManager()
    private lateinit var cameraManager: RppgCameraManager*/
    private val permissionManager: PermissionManager by lazy { PermissionManager() }

    var age = 0
    var gender = ""
    private var height = ""
    private var weight = ""
    private var socketUrl = ""
    private var socketToken = ""

    private var hrv = 0
    private var systolic = 0
    private var diastolic = 0
    private var faceScanData = FaceScanData()
    private var resultParametersList : MutableList<FaceScanDataModel> = mutableListOf()
    private val faceScanSingleton = FaceScanSingleton.getInstance()!!

    private val onBackPressedCallBack = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            //stopRecording()
            routeToHomeScreen()
        }
    }

    private fun routeToHomeScreen() {
        openAnotherActivity(destination = NavigationConstants.HOME, clearTop = true)
    }

    override fun getViewModel(): BaseViewModel = wyhFaceScanViewModel

    override fun onCreateEvent(savedInstanceState: Bundle?) {
        binding = ActivityFaceScanBinding.inflate(layoutInflater)
        setContentView(binding.root)
        try {
            onBackPressedDispatcher.addCallback(this, onBackPressedCallBack)
            if (intent.hasExtra(Constants.HEIGHT)) {
                height = intent.getStringExtra(Constants.HEIGHT)!!.toDouble().toInt().toString()
            }
            if (intent.hasExtra(Constants.WEIGHT)) {
                weight = intent.getStringExtra(Constants.WEIGHT)!!.toDouble().toInt().toString()
            }
            if (intent.hasExtra(Constants.SOCKET_URL)) {
                socketUrl = intent.getStringExtra(Constants.SOCKET_URL)!!
            }
            if (intent.hasExtra(Constants.SOCKET_TOKEN)) {
                socketToken = intent.getStringExtra(Constants.SOCKET_TOKEN)!!
            }
            age = wyhFaceScanViewModel.age.split(" ").toTypedArray()[0].toInt()
            gender = Utilities.getDisplayGender(wyhFaceScanViewModel.gender).lowercase()
            Utilities.printLogError("height--->$height")
            Utilities.printLogError("weight--->$weight")
            Utilities.printLogError("socketUrl--->$socketUrl")
            Utilities.printLogError("socketToken--->$socketToken")
//            initSocket()
            setClickable()
            setObserver()
            showTipsForFaceScan()
        } catch ( e:Exception ) {
            e.printStackTrace()
        }
    }

   /* private fun initSocket() {
        showBegin()
        camera = binding.camera
        val finalSocketUrl = "$socketUrl?authToken=$socketToken&fps=&age=$age&sex=$gender&height=$height&weight=$weight"
        *//*        if (!SharedPref.getUserHeight().equals("")) {
                url = url + "?authToken=$token&fps=&age=${SharedPref.getUserAge()}&sex=${
                    SharedPref.getUserGender().lowercase(Locale.getDefault())
                }&height=${SharedPref.getUserHeight()}&weight=${SharedPref.getUserWeight()}"
            }*//*
        Utilities.printLogError("RppgCommon : FinalSocketUrl--->$finalSocketUrl")

        socketManager.init(this@FaceScanActivity, finalSocketUrl, object : SocketCallback {
            override fun onEvent(message: String) {
                Utilities.printLogError("onEventData--->$message")
                sendEvent(message)
            }
            override fun onError(error: String) {
                // socket errors here
                Utilities.printLogError("RppgCommon : Socket Started: $error")
            }
            override fun onAuthError() {
                // auth token expired
                Utilities.printLogError("RppgCommon : Socket Started: Auth Error")
            }
        })
        return permissionManager.checkPermission(this@FaceScanActivity) {
            initCameraManager()
        }
    }*/

    private fun setClickable() {
        val strLearnMore = "<u><a><B><font color='#00cfff'>" + resources.getString(R.string.LEARN_MORE) + "</font></B></a></u>"
        binding.txtDisclaimer.text = Html.fromHtml(resources.getString(R.string.WELLFIE_DISCLAIMER_LABEL) + " " + strLearnMore,Html.FROM_HTML_MODE_LEGACY)

        //button Start/Stop on UI
        /*binding.control.setOnClickListener {
            isRecording = !isRecording
            if (isRecording) {
                startRecording()
            } else {
                stopRecording()
            }
        }*/

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

        binding.imgInfo.setOnClickListener {
            showTipsForFaceScan()
        }

    }

    private fun setObserver() {

        wyhFaceScanViewModel.addFaceScanVitals.observe(this) {
            if (it.status == Resource.Status.SUCCESS) {
                if ( it.data!!.success!! ) {
                    faceScanSingleton.dateTime = it.data.data.createdOn!!
                    faceScanSingleton.faceScanData = resultParametersList
                    navigateToResultScreen()
                }
            }
        }

    }

    private fun showBegin() {
        binding.layoutBegin.visibility = View.VISIBLE
        binding.llCalibrating.visibility = View.INVISIBLE
    }

    private fun showProgress() {
        binding.layoutBegin.visibility = View.INVISIBLE
        binding.llCalibrating.visibility = View.VISIBLE
    }

/*    private fun startRecording() {
        socketManager.startSocket(socketToken)
        cameraManager.startRecording()
        runOnUiThread {
            showProgress()
            faceScanData = FaceScanData()
            binding.llCalibrating.visibility = View.VISIBLE
            binding.control.text = resources.getString(R.string.STOP)
        }
    }

    private fun stopRecording() {
        cameraManager.stopRecording()
        socketManager.stopSocket()
        runOnUiThread {
            showBegin()
            binding.txtProgress.text = "0 %"
            binding.llCalibrating.visibility = View.INVISIBLE
            binding.control.text = resources.getString(R.string.START)
            binding.indicatorProgress.setValue(0f)
        }
    }*/

    @SuppressLint("SetTextI18n")
    private fun setProgressForProgressBar(faceScanData: FaceScanData) {
        var progress = 0
        if ( faceScanData.bpm > 0 ) {
            progress += 20
        }
        if ( faceScanData.rr > 0 ) {
            progress += 20
        }
        if ( faceScanData.oxygen > 0 ) {
            progress += 20
        }
        if ( systolic > 0 && diastolic > 0 ) {
            progress += 20
        }
        if ( faceScanData.stressStatus != null && faceScanData.stressStatus != "NO_DATA"
            && faceScanData.bloodPressureStatus != null && faceScanData.bloodPressureStatus != "NO_DATA"
            && hrv > 0
            ) {
            progress += 20
        }
        runOnUiThread {
            binding.txtProgress.text = "$progress %"
            binding.progress.setProgress(progress, true)
            Utilities.printLogError("Progress Changed to--->$progress")
            Utilities.printData("faceScanData at $progress %",faceScanData,true)
        }
    }

    private fun setApiMsg(code: String, msg: String) {
        runOnUiThread {
            // Stuff that updates the UI
            binding.txtError.text = wyhFaceScanViewModel.getFaceScanStatusMsg(code, msg)
        }
    }

    private fun sendEvent(message: String) {
        // socket results here
        val gson = Gson()
        var faceScanData = FaceScanData()

        if (message.contains("MEASUREMENT_STATUS")) {
            val measurementStatusResponse = gson.fromJson(message,MeasurementStatusResponse::class.java)
            if (measurementStatusResponse.messageType == "MEASUREMENT_STATUS") {
                setApiMsg(measurementStatusResponse.data.statusCode!!,measurementStatusResponse.data.statusMessage!!)
            }
            if (measurementStatusResponse.messageType == "MOVING_WARNING") {
                setApiMsg("MOVING_WARNING", "")
            }
        }

        if (message.contains("HRV_METRICS")) {
            val faceScanResponseHRV = gson.fromJson(message,FaceScanHrvResponse::class.java)
            faceScanData.hrv = faceScanResponseHRV.data.hrv.roundToInt()
            hrv = faceScanResponseHRV.data.hrv.roundToInt()
        }

        val faceScanResponse = gson.fromJson(message,FaceScanResponse::class.java)

        if (faceScanResponse.messageType == "MEASUREMENT_MEAN_DATA") {
            faceScanData = faceScanResponse.faceScanData
            setProgressForProgressBar(faceScanData)
        }
        if (faceScanResponse.messageType == "BLOOD_PRESSURE") {
            val faceScanResponseNew = gson.fromJson(message,FaceScanResponse::class.java)
            if (faceScanResponseNew.faceScanData.systolic > 0)
                systolic = faceScanResponseNew.faceScanData.systolic
            if (faceScanResponseNew.faceScanData.diastolic > 0)
                diastolic = faceScanResponseNew.faceScanData.diastolic
        }

        if (faceScanData.rr > 0 && faceScanData.bpm > 0 && faceScanData.oxygen > 0 && diastolic > 0 && systolic > 0
            && faceScanData.stressStatus != null && faceScanData.bloodPressureStatus != null &&
            !faceScanData.stressStatus.equals("NO_DATA") && !faceScanData.bloodPressureStatus.equals("NO_DATA")
            && hrv > 0
            ) {
            faceScanData.systolic = systolic
            faceScanData.diastolic = diastolic
            faceScanData.hrv = hrv
//            stopRecording()
            Utilities.printLogError("FaceScanData : Condition Successful from mean")
            sendDataToServer(faceScanData)
        }
    }

    private fun sendDataToServer(faceScanData : FaceScanData) {
        try {
            val request = AddFaceScanVitalsModel.AddFaceScanVitalsRequest(
                heartRate = faceScanData.bpm,
                respiratoryRate = faceScanData.rr.toInt(),
                oxygen = faceScanData.oxygen.toInt(),
                stressLevel = faceScanData.stressStatus,
                bloodPressureStatus = faceScanData.bloodPressureStatus,
                systolic = faceScanData.systolic,
                diastolic = faceScanData.diastolic,
                hrv = hrv,
                gender = gender,
                age = age,
                height = height.toInt(),
                weight = weight.toInt())
            Utilities.printData("Request",request,true)

            resultParametersList.clear()
            resultParametersList.add(FaceScanDataModel(1, "BMI","BMI", String.format(Locale.ENGLISH,"%.1f", CalculateParameters.roundOffPrecision(CalculateParameters.getBMI(height.toDouble(), weight.toDouble()), 1)),""))
            resultParametersList.add(FaceScanDataModel(2, "BP", "Blood Pressure", "${faceScanData.systolic} / ${faceScanData.diastolic}",faceScanData.bloodPressureStatus!!))
            resultParametersList.add(FaceScanDataModel(3, "HEART_RATE","Heart Rate", faceScanData.bpm.toString(),""))
            resultParametersList.add(FaceScanDataModel(4, "BREATHING_RATE","Breathing Rate", faceScanData.rr.toInt().toString(),""))
            resultParametersList.add(FaceScanDataModel(5, "BLOOD_OXYGEN","Blood Oxygen", faceScanData.oxygen.toInt().toString(),""))
            resultParametersList.add(FaceScanDataModel(6, "STRESS_INDEX","Stress Index", faceScanData.stressIndex.toString(),faceScanData.stressStatus!!))

            wyhFaceScanViewModel.callAddFaceScanVitalsApi(request)
        } catch ( e: Exception ) {
            e.printStackTrace()
        }
    }

   /* private fun initCameraManager() {
        cameraManager = RppgCameraManager(lifecycleOwner = this, camera = camera, cameraConfig = CameraConfig(isDebug = true, fps = 30))
        val manager = RppgCoreManager().apply {
            init()
        }
        Utilities.printLogError("RppgCommon : version ${manager.getVersion()}")
        // camera
        cameraManager.setListener { data ->
            handleCameraManagerData(data, manager)
        }
    }

    private fun handleCameraManagerData(data: FaceData, manager: RppgCoreManager) {
        with(data) {
            val result = manager.track(width, height, byteArray, timestamp, floatArray)
            socketManager.update(result, timestamp)
        }
    }*/

/*    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        initCameraManager()
    }*/

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Utilities.printLogError("requestCode--->$requestCode")
        Utilities.printLogError("grantResults--->${grantResults}")
        when (requestCode) {
            Constants.CAMERA_SELECT_CODE -> {
                if (permissionManager.hasPermissionsGranted(this,permissions)) {
//                    initCameraManager()
                } else {
                    Utilities.toastMessageShort(this, resources.getString(R.string.ERROR_CAMERA_PERMISSION))
                }
            }
        }
    }

    private fun showTipsForFaceScan() {
        showDialog(
            listener = this,
            imgResource = R.drawable.img_face_scan_help,
            title = "",
            message = "<a><B><font color='#3A393B'>${resources.getString(R.string.FACE_SCAN_TIPS_TITLE)} </font></B><br/><br/> ${resources.getString(R.string.FACE_SCAN_TIP_1)} <br/> ${resources.getString(R.string.FACE_SCAN_TIP_2)} <br/> ${resources.getString(R.string.FACE_SCAN_TIP_3)} <br/> ${resources.getString(R.string.FACE_SCAN_TIP_4)} <br/> ${resources.getString(R.string.FACE_SCAN_TIP_5)} <br/> ${resources.getString(R.string.FACE_SCAN_TIP_6)} </a>",
            rightText = resources.getString(R.string.OK),
            showLeftBtn = false
        )
    }

    private fun navigateToResultScreen() {
        openAnotherActivity(destination = NavigationConstants.FACE_SCAN_RESULT_ACTIVITY) {
            putString(Constants.FROM, "")
        }
    }

    override fun onDialogClickListener(isButtonLeft: Boolean, isButtonRight: Boolean) {}

/*    private fun sendEvent1(message: String) {
        // socket results here
        val gson = Gson()
        //faceScanData = FaceScanData()
        val faceScanResponse = gson.fromJson(message,FaceScanResponse::class.java)
        Utilities.printData("faceScanResponse",faceScanResponse)

        if (faceScanResponse.messageType == "MEASUREMENT_STATUS") {
            setApiMsg(faceScanResponse.faceScanData.statusCode!!, faceScanResponse.faceScanData.statusMessage!!)
        }
        if (faceScanResponse.messageType == "MOVING_WARNING") {
            setApiMsg("MOVING_WARNING", "")
        }

        //.printLogError("RppgCommon : ${message.contains("HRV_METRICS")} res: $message")
        if (message.contains("HRV_METRICS")) {
            val faceScanResponseHRV = gson.fromJson(message,FaceScanHrvResponse::class.java)
            faceScanData.stressIndex = faceScanResponseHRV.data.stressIndex
            faceScanData.hrv = faceScanResponseHRV.data.hrv.roundToInt()
            hrv = faceScanResponseHRV.data.hrv.roundToInt()
            Utilities.printLogError("FaceScanData : HRV: $hrv")
        }

        if (faceScanResponse.messageType == "MEASUREMENT_MEAN_DATA") {
*//*            binding.progress.isVisible = true
            if (faceScanResponse.faceScanData.bpm > 0) {
                binding.tvBPM.text = faceScanResponse.faceScanData.bpm.toString()
            }*//*
            faceScanData = faceScanResponse.faceScanData
            //Log.d("FaceScanData", gson.toJson(faceScanData))
            Utilities.printData("faceScanData",faceScanData,true)
            //setProgressForProgressBar()
        }

        if (faceScanResponse.messageType == "BLOOD_PRESSURE") {
            val faceScanResponseNew = gson.fromJson(message, FaceScanResponse::class.java)
            Utilities.printLogError("FaceScanData : ${gson.toJson(faceScanResponseNew.faceScanData)}")
            if (faceScanResponseNew.faceScanData.systolic > 0) {
                systolic = faceScanResponseNew.faceScanData.systolic
                faceScanData.systolic = systolic
            }
            if (faceScanResponseNew.faceScanData.diastolic > 0) {
                diastolic = faceScanResponseNew.faceScanData.diastolic
                faceScanData.diastolic = diastolic
            }
        }

        setProgressForProgressBar()

        if (faceScanData.rr > 0 && faceScanData.bpm > 0 && faceScanData.oxygen > 0
            && faceScanData.systolic > 0 && faceScanData.diastolic > 0
            && faceScanData.stressStatus != null && faceScanData.bloodPressureStatus != null
            && !faceScanData.stressStatus.equals("NO_DATA")
            && !faceScanData.bloodPressureStatus.equals("NO_DATA")
            && hrv > 0
            ) {
            faceScanData.hrv = hrv
            //cameraManager.stopRecording()
            //socketManager.stopSocket()
            stopRecording()
            Utilities.printLogError("FaceScanData : Condition Successful from mean")
            //sendDataToServer()
        }
    }*/

    /*    private fun sendEvent1(message: String) {
        try {
            obj = JSONObject(message)
            when (obj.getString("messageType")) {

                "MEASUREMENT_STATUS" -> {
                    setApiMsg(
                        obj.getJSONObject("data").getString("statusCode"),
                        obj.getJSONObject("data").getString("statusMessage")
                    )
                }

                "MOVING_WARNING" -> {
                    setApiMsg("MOVING_WARNING", "")
                }
                *//*               "UNSTABLE_CONDITIONS_WARNING" -> {
                                   setApiMsg( "UNSTABLE_CONDITIONS_WARNING","" )
                               }*//*
                "BLOOD_PRESSURE" -> {
                    bpSystolic = obj.getJSONObject("data").getString("systolic")
                    bpDiastolic = obj.getJSONObject("data").getString("diastolic")
                }

                "SIGNAL_QUALITY" -> {
                    snr = obj.getJSONObject("data").getString("snr")
                }

                "STRESS_INDEX" -> {
                    stress = obj.getJSONObject("data").getString("stress")
                }

                "MEASUREMENT_MEAN_DATA" -> {
                    bpm = obj.getJSONObject("data").getString("bpm")
                    rr = obj.getJSONObject("data").getString("rr")
                    oxygen = obj.getJSONObject("data").getString("oxygen")
                    stressStatus = obj.getJSONObject("data").getString("stressStatus")
                    bloodPressureStatus = obj.getJSONObject("data").getString("bloodPressureStatus")
                }

                "HRV_METRICS" -> {
                    ibi = obj.getJSONObject("data").getString("ibi")
                    rmssd = obj.getJSONObject("data").getString("rmssd")
                    sdnn = obj.getJSONObject("data").getString("sdnn")
                    hrv = obj.getJSONObject("data").getString("hrv").toInt()
                }
            }

            setProgressForProgressBar()

            if (rr.toInt() > 0 && bpm.toInt() > 0 && oxygen.toInt() > 0 && diastolic > 0 && systolic > 0
                && stressStatus != null && bloodPressureStatus != null
                && hrv > 0 && stressStatus != "NO_DATA" && bloodPressureStatus != "NO_DATA") {
                Utilities.printLogError("FaceScanData : Condition Successful from mean")
                //faceScanData.systolic = systolic
                //faceScanData.diastolic = diastolic
                //faceScanData.hrv = hrv
                //SharedPref.putUserVitals(gson.toJson(faceScanData))
                socketManager.stopSocket()
                cameraManager.stopRecording()
                sendDataToServer()
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }*/

/*    @SuppressLint("SetTextI18n")
    private fun setProgressForProgressBar() {
        var progress = 0
        if ( faceScanData.bpm > 0 ) {
            progress += 20
        }
        if ( faceScanData.rr.toInt() > 0 ) {
            progress += 20
        }
        if ( faceScanData.oxygen.toInt() > 0 ) {
            progress += 20
        }
        if ( faceScanData.systolic > 0 && faceScanData.diastolic > 0 ) {
            progress += 20
        }
        if ( faceScanData.stressStatus != null  && faceScanData.stressStatus != "NO_DATA"
            && faceScanData.bloodPressureStatus != null && faceScanData.bloodPressureStatus != "NO_DATA"
            && hrv > 0
        ) {
            progress += 20
        }
        Utilities.printData("faceScanData",faceScanData,true)
        runOnUiThread {
            binding.txtProgress.text = "$progress %"
            binding.progress.setProgress(progress, true)
            Utilities.printLogError("Progress Changed to--->$progress")
            Utilities.printData("faceScanData at $progress %",faceScanData,true)
        }
    }*/

}