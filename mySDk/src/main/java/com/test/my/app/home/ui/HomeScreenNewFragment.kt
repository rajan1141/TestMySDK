package com.test.my.app.home.ui

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.aktivolabs.aktivocore.data.models.Stats
import com.aktivolabs.aktivocore.data.models.User
import com.aktivolabs.aktivocore.data.models.badges.DailyBadge
import com.aktivolabs.aktivocore.data.models.challenge.Challenge
import com.aktivolabs.aktivocore.data.models.mind.data.MindScore
import com.aktivolabs.aktivocore.data.models.queries.BadgeByDateQuery
import com.aktivolabs.aktivocore.data.models.queries.ChallengeListQuery
import com.aktivolabs.aktivocore.data.models.queries.Query
import com.aktivolabs.aktivocore.data.models.queries.ScoreQuery
import com.aktivolabs.aktivocore.data.models.queries.SleepQuery
import com.aktivolabs.aktivocore.data.models.queries.StepsQuery
import com.aktivolabs.aktivocore.data.models.score.ScoreStats
import com.aktivolabs.aktivocore.data.models.sleep.SleepStats
import com.aktivolabs.aktivocore.data.models.steps.StepStats
import com.aktivolabs.aktivocore.managers.AktivoManager
import com.aktivolabs.aktivocore.network.ResultData
import com.test.my.app.R
import com.test.my.app.common.base.BaseFragment
import com.test.my.app.common.base.BaseViewModel
import com.test.my.app.common.constants.CleverTapConstants
import com.test.my.app.common.constants.Constants
import com.test.my.app.common.constants.NavigationConstants
import com.test.my.app.common.constants.PreferenceConstants
import com.test.my.app.common.extension.openAnotherActivity
import com.test.my.app.common.fitness.FitnessDataManager
import com.test.my.app.common.utils.CalculateParameters
import com.test.my.app.common.utils.CleverTapHelper
import com.test.my.app.common.utils.DateHelper
import com.test.my.app.common.utils.DefaultNotificationDialog
import com.test.my.app.common.utils.PermissionUtil
import com.test.my.app.common.utils.Utilities
import com.test.my.app.databinding.FragmentHomeScreenNewBinding
import com.test.my.app.home.adapter.AktivoParamAdapter
import com.test.my.app.home.adapter.DashboardCalenderAdapter
import com.test.my.app.home.adapter.SlidingDashboardAdapter
import com.test.my.app.home.common.DataHandler
import com.test.my.app.home.common.DataHandler.AktiVoParamModel
import com.test.my.app.home.common.DataHandler.FaceScanDataModel
import com.test.my.app.home.common.HRAObservationModel
import com.test.my.app.home.ui.aktivo.DialogAktivoBadges
import com.test.my.app.home.viewmodel.HomeViewModel
import com.test.my.app.medication_tracker.model.MedCalender
import com.test.my.app.model.sudLifePolicy.PolicyProductsModel
import com.test.my.app.model.waterTracker.GetDailyWaterIntakeModel
import com.test.my.app.repository.utils.Resource
import com.test.my.app.wyh.common.FaceScanSingleton
import com.test.my.app.wyh.faceScan.ui.FaceScanVitalsBottomSheet
import com.test.my.app.wyh.faceScan.viewmodel.WyhFaceScanViewModel
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.CompletableObserver
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.schedulers.Schedulers
import org.threeten.bp.LocalDate
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

@AndroidEntryPoint
class HomeScreenNewFragment : BaseFragment() , HomeMainActivity.OnAktivoListener ,
    HomeMainActivity.OnHelpClickListener,DashboardCalenderAdapter.OnDashboardDateListener {

    lateinit var binding: FragmentHomeScreenNewBinding
    private val viewModel: HomeViewModel by lazy {
        ViewModelProvider(this)[HomeViewModel::class.java]
    }
    private val wyhFaceScanViewModel: WyhFaceScanViewModel by lazy {
        ViewModelProvider(this)[WyhFaceScanViewModel::class.java]
    }

    private val calenderDateList : MutableList<MedCalender> = mutableListOf()
    private val permissionUtil = PermissionUtil
    private var fitnessDataManager: FitnessDataManager? = null
    //private val todayDate = DateHelper.currentDateAsStringyyyyMMdd
    var selectedDate = DateHelper.currentDateAsStringyyyyMMdd
    private var dashboardCalenderAdapter: DashboardCalenderAdapter? = null
    private var aktivoParamAdapter: AktivoParamAdapter? = null
    private var selectedMonthIndex = 0
    private var todayIntakePercentage = 0

    private var aktivoScore = 0
    private var aktivoMindScore = 0
    private var aktivoBadge = "CONTENDER"
    private var aktivoManager: AktivoManager? = null
    private var compositeDisposable: CompositeDisposable? = null
    private var dialogAktivoBadges: DialogAktivoBadges? = null
    private val faceScanSingleton = FaceScanSingleton.getInstance()!!

    var handler : Handler = Handler(Looper.getMainLooper())
    private var currentPage = 0
    var slidingDashboardAdapter : SlidingDashboardAdapter? = null
    val challengesList: MutableList<Challenge> = mutableListOf()
    val policyBannerList: MutableList<PolicyProductsModel.PolicyProducts> = mutableListOf()
    //val dashboardBannerList: MutableList<DataHandler.DashboardBannerModel> = mutableListOf()

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utilities.printLogError("Inside=> onCreate")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as HomeMainActivity).setOnAktivoListener(this)
        (activity as HomeMainActivity).setOnHelpClickListener(this)
        (activity as HomeMainActivity).setToolbarInfo(0, showAppLogo = true, title = "", showBg = true)
        Utilities.printLog("Inside=> onViewCreated")
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onDashboardDateClick(date: MedCalender, newDayPosition: Int ) {
        Utilities.printData("SelectedDate",date)
        selectedDate = date.Date
        selectedMonthIndex = newDayPosition
        if ( date.IsToday ) {
            binding.txtSelectedDate.text = "${resources.getString(R.string.TODAY)}, ${DateHelper.convertDateSourceToDestination(selectedDate,DateHelper.SERVER_DATE_YYYYMMDD,DateHelper.DATEFORMAT_DDMMMYYYY_NEW)}"
        } else {
            binding.txtSelectedDate.text = " ${DateHelper.convertDateSourceToDestination(selectedDate,DateHelper.SERVER_DATE_YYYYMMDD,DateHelper.DATEFORMAT_DDMMMYYYY_NEW)}"
        }
        refreshDashboard(true)

        when (selectedDate) {
            calenderDateList[0].Date -> {
                binding.imgBack.visibility = View.INVISIBLE
                binding.imgNext.visibility = View.VISIBLE
            }
            calenderDateList.last().Date -> {
                binding.imgNext.visibility = View.INVISIBLE
                binding.imgBack.visibility = View.VISIBLE
            }
            else -> {
                binding.imgBack.visibility = View.VISIBLE
                binding.imgNext.visibility = View.VISIBLE
            }
        }
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentHomeScreenNewBinding.inflate(inflater, container, false)
        try {
            CleverTapHelper.pushEvent(requireContext(), CleverTapConstants.DASHBOARD_SCREEN)
            aktivoManager = AktivoManager.getInstance(requireContext())
            compositeDisposable = CompositeDisposable()
            fitnessDataManager = FitnessDataManager(requireContext())

            binding.txtUserName.text = "${resources.getString(R.string.HI)} " + viewModel.preferenceUtil.getPreference(PreferenceConstants.FIRSTNAME,"")
            checkNotificationPermission()
            setUpCalender()
            setUpSlidingDashboardViewPager()
            viewModel.callPolicyProductsApi(this)

            refreshDashboard(false)
            setObserver()
            setClickable()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        Utilities.printLogError("Inside=> onCreateView")
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun refreshDashboard(showProgress:Boolean) {
        checkWyhToken()
        getAktivoScore(showProgress)
        viewModel.callGetMedicalProfileSummary()
        viewModel.callGetDailyWaterIntakeApi(selectedDate)
        viewModel.callMedicineListByDayApi(selectedDate)
    }

    @SuppressLint("SetTextI18n")
    private fun setUpCalender() {
        calenderDateList.clear()
        calenderDateList.addAll(Utilities.getCurrentWeekDates())
        binding.txtSelectedDate.text = "${resources.getString(R.string.TODAY)}, ${DateHelper.convertDateSourceToDestination(selectedDate,DateHelper.SERVER_DATE_YYYYMMDD,DateHelper.DATEFORMAT_DDMMMYYYY_NEW)}"

        dashboardCalenderAdapter = DashboardCalenderAdapter(requireContext(),this)
        binding.rvDashboardCalender.adapter = dashboardCalenderAdapter
        dashboardCalenderAdapter!!.updateList(calenderDateList)
        selectedMonthIndex = calenderDateList.indexOf(calenderDateList.find { it.Date == DateHelper.currentDateAsStringyyyyMMdd })

        aktivoParamAdapter = AktivoParamAdapter(requireContext(),this@HomeScreenNewFragment)
        binding.rvAktivoParam.adapter = aktivoParamAdapter
    }

    @SuppressLint("SetTextI18n")
    private fun setObserver() {

        viewModel.aktivoCreateUser.observe(viewLifecycleOwner) {
            if (it.status == Resource.Status.SUCCESS) {
                if (!Utilities.isNullOrEmpty(it.data!!.resultData.member.id)) {
                    viewModel.storeUserPreference(PreferenceConstants.AKTIVO_MEMBER_ID, it.data.resultData.member.id!!)
                    viewModel.callAktivoGetUserTokenApi(it.data.resultData.member.id)
                }
            }
            if (it.status == Resource.Status.ERROR) {
                //stopAllAktivoShimmers()
            }
        }

        viewModel.aktivoGetUserToken.observe(viewLifecycleOwner) {
            if (it.status == Resource.Status.SUCCESS) {
                if (!Utilities.isNullOrEmpty(it.data!!.accessToken) && !Utilities.isNullOrEmpty(it.data.refreshToken)) {
                    viewModel.storeUserPreference(PreferenceConstants.AKTIVO_ACCESS_TOKEN, it.data.accessToken!!)
                    viewModel.storeUserPreference(PreferenceConstants.AKTIVO_REFRESH_TOKEN, it.data.refreshToken!!)
                    authenticateUserUsingToken()
                }
            }
            if (it.status == Resource.Status.ERROR) {
                //stopAllAktivoShimmers()
            }
        }

        viewModel.medicalProfileSummary.observe(viewLifecycleOwner) {
            if (it.status == Resource.Status.SUCCESS) {
                if (it.data!!.MedicalProfileSummary != null) {
                    viewModel.setHraSummaryDetails(it.data.MedicalProfileSummary!!)
                    viewModel.setupHRAWidgetData(it.data.MedicalProfileSummary)
                }
            }
        }

        viewModel.hraObservationLiveData.observe(viewLifecycleOwner) {
            HRAData.data = it
            setupHRAWidgetUI(it)
        }

        viewModel.getDailyWaterIntake.observe(viewLifecycleOwner) {
            if (it.status == Resource.Status.SUCCESS) {
                if (it.data!!.result.result.isNotEmpty()) {
                    val result = it.data.result.result[0]
                    Utilities.printData("result", result, true)
                    loadData(result)
                }
            }
        }

        wyhFaceScanViewModel.getWyhAuthorization.observe(viewLifecycleOwner) {
            if (it.status == Resource.Status.SUCCESS) {
                viewModel.hideProgress()
                if ( !Utilities.isNullOrEmpty(it.data!!.data) ) {
                    wyhFaceScanViewModel.storeUserPreference(PreferenceConstants.WYH_ACCESS_TOKEN,it.data.data!!)
                    wyhFaceScanViewModel.storeUserPreference(PreferenceConstants.WYH_TOKEN_LAST_SYNC,DateHelper.currentUTCDatetimeInMillisecAsString)
                    //wyhFaceScanViewModel.callGetFaceScanVitalsApi()
                }
            }
            if (it.status == Resource.Status.ERROR) {
                viewModel.hideProgress()
                //stopWellfieShimmer()
            }
        }

        viewModel.medicineListByDay.observe(viewLifecycleOwner) {
            if (it.status == Resource.Status.SUCCESS) {
                val medicineListByDay = it.data?.medications?.toMutableList()
                Utilities.printLogError("medicineListByDay---> ${medicineListByDay?.size}")
                val scheduleList = mutableListOf<String>()
                it?.data?.let { data->
                    if (data.medications.isNotEmpty()) {
                        medicineListByDay?.sortByDescending { med -> med.notification!!.setAlert }
                        //MedicationSingleton.getInstance()!!.setMedicineListByDay(medicineListByDay)
                        val dateFormat = SimpleDateFormat("HH:mm", Locale.ENGLISH)
                        val currentTime = dateFormat.parse(dateFormat.format(Date()))
                        for ( i in medicineListByDay!! ) {
                            if ( i.medicationScheduleList.isNotEmpty() ) {
                                for ( j in i.medicationScheduleList ) {
                                    if ( dateFormat.parse(j.scheduleTime)!!.after(currentTime) ) {
                                        scheduleList.add(j.scheduleTime)
                                    }
                                }
                            }
                        }
                    }

                    Utilities.printData("scheduleList",scheduleList)
                    if ( scheduleList.isNotEmpty() ) {
                        binding.txtNextDose.text = "Next Dose at"
                        binding.txtDose.text = DateHelper.getTimeIn12HrFormatAmPm(scheduleList.minByOrNull { it }!!)
                        binding.txtDose.visibility = View.VISIBLE
                    } else {
                        binding.txtNextDose.text = "No Due Dose"
                        binding.txtDose.visibility = View.GONE
                    }
                }
            }
            if (it.status == Resource.Status.ERROR) {
                binding.txtNextDose.text = "No Due Dose"
                binding.txtDose.visibility = View.GONE
            }
        }

        wyhFaceScanViewModel.getFaceScanVitals.observe(viewLifecycleOwner) {
            if (it.status == Resource.Status.SUCCESS) {
                viewModel.hideProgress()
                val faceScanData = it.data!!.data
                if ( faceScanData != null && !Utilities.isNullOrEmpty(faceScanData.createdOn) ) {
                    val parametersList : MutableList<FaceScanDataModel> = mutableListOf()
                    parametersList.add(FaceScanDataModel(1, "BMI","BMI", String.format(Locale.ENGLISH,"%.1f", CalculateParameters.roundOffPrecision(CalculateParameters.getBMI(faceScanData.height!!.toDouble(), faceScanData.weight!!.toDouble()), 1)),""))
                    parametersList.add(FaceScanDataModel(2, "BP", "Blood Pressure", "${faceScanData.systolic} / ${faceScanData.diastolic}",faceScanData.bloodPressureStatus!!))
                    parametersList.add(FaceScanDataModel(3, "HEART_RATE","Heart Rate", faceScanData.heartRate.toString(),""))
                    parametersList.add(FaceScanDataModel(4, "BREATHING_RATE","Breathing Rate", faceScanData.respiratoryRate!!.toInt().toString(),""))
                    parametersList.add(FaceScanDataModel(5, "BLOOD_OXYGEN","Blood Oxygen", faceScanData.oxygen!!.toInt().toString(),""))
                    parametersList.add(FaceScanDataModel(6, "STRESS_INDEX","Stress Index", "",faceScanData.stressStatus!!))
                    faceScanSingleton.dateTime = faceScanData.createdOn!!
                    faceScanSingleton.faceScanData = parametersList

                    val bottomSheet = FaceScanVitalsBottomSheet()
                    bottomSheet.show(requireActivity().supportFragmentManager,FaceScanVitalsBottomSheet.TAG)
                } else {
                    faceScanSingleton.clearData()
                    faceScanSingleton.faceScanData = mutableListOf()
                    navigateToFaceScanWithPermission(CleverTapConstants.SCAN)
                }
            }
            if (it.status == Resource.Status.ERROR) {
                viewModel.hideProgress()
            }
        }

        viewModel.policyProducts.observe(viewLifecycleOwner) { }
        viewModel.addFeatureAccessLog.observe(viewLifecycleOwner) { }
    }

    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
    private fun setClickable() {

        binding.layoutBadgeProgress.setOnClickListener {
            dialogAktivoBadges = DialogAktivoBadges(requireContext(),aktivoBadge,selectedDate,this)
            dialogAktivoBadges!!.show()
        }

        binding.imgBack.setOnClickListener {
            Utilities.printLogError("selectedMonthIndex--->$selectedMonthIndex")
            if ((selectedMonthIndex-1) >= 0 && (selectedMonthIndex-1) < calenderDateList.size) {
                val date = calenderDateList[selectedMonthIndex-1]
                Utilities.printData("SelectedDate",date)
                if ( date.IsClickable ) {
                    selectedMonthIndex -= 1
                    selectedDate = date.Date
                    dashboardCalenderAdapter!!.updateSelectedDate(selectedDate)
                    if ( date.IsToday ) {
                        binding.txtSelectedDate.text = "${resources.getString(R.string.TODAY)}, ${DateHelper.convertDateSourceToDestination(selectedDate,DateHelper.SERVER_DATE_YYYYMMDD,DateHelper.DATEFORMAT_DDMMMYYYY_NEW)}"
                    } else {
                        binding.txtSelectedDate.text = " ${DateHelper.convertDateSourceToDestination(selectedDate,DateHelper.SERVER_DATE_YYYYMMDD,DateHelper.DATEFORMAT_DDMMMYYYY_NEW)}"
                    }

                    if ( selectedDate == calenderDateList[0].Date ) {
                        binding.imgBack.visibility = View.INVISIBLE
                        binding.imgNext.visibility = View.VISIBLE
                    } else {
                        binding.imgBack.visibility = View.VISIBLE
                        binding.imgNext.visibility = View.VISIBLE
                    }
                }
            } else {
                selectedMonthIndex += 1
            }
        }

        binding.imgNext.setOnClickListener {
            Utilities.printLogError("selectedMonthIndex--->$selectedMonthIndex")
            if ((selectedMonthIndex+1) < calenderDateList.size) {
                val date = calenderDateList[selectedMonthIndex+1]
                Utilities.printData("SelectedDate",date)
                if ( date.IsClickable ) {
                    selectedMonthIndex += 1
                    selectedDate = date.Date
                    dashboardCalenderAdapter!!.updateSelectedDate(selectedDate)
                    if ( date.IsToday ) {
                        binding.txtSelectedDate.text = "${resources.getString(R.string.TODAY)}, ${DateHelper.convertDateSourceToDestination(selectedDate,DateHelper.SERVER_DATE_YYYYMMDD,DateHelper.DATEFORMAT_DDMMMYYYY_NEW)}"
                    } else {
                        binding.txtSelectedDate.text = " ${DateHelper.convertDateSourceToDestination(selectedDate,DateHelper.SERVER_DATE_YYYYMMDD,DateHelper.DATEFORMAT_DDMMMYYYY_NEW)}"
                    }

                    if ( selectedDate == calenderDateList.last().Date ) {
                        binding.imgNext.visibility = View.INVISIBLE
                        binding.imgBack.visibility = View.VISIBLE
                    } else {
                        binding.imgNext.visibility = View.VISIBLE
                        binding.imgBack.visibility = View.VISIBLE
                    }
                }
            } else {
                selectedMonthIndex -= 1
            }
        }

        binding.cardAktivoScore.setOnClickListener {
            navigateToAktivo(Constants.AKTIVO_SCORE_CODE)
        }

        binding.cardFaceScan.setOnClickListener {
            checkCameraPermissionAndProceed()
        }

        binding.cardMindScore.setOnClickListener {
            navigateToAktivo(Constants.AKTIVO_MIND_SCORE_CODE)
        }

        binding.cardHra.setOnClickListener {
            goToHRA()
        }

        binding.cardHydrationTracker.setOnClickListener {
            CleverTapHelper.pushEvent(requireContext(), CleverTapConstants.HYDRATION_TRACKER)
            viewModel.callAddFeatureAccessLogApi(Constants.HYDRATION_TRACKER, "Hydration Tracker", "VivantCore", "")
            openAnotherActivity(destination = NavigationConstants.WATER_TRACKER)
        }

        binding.cardMedicationTracker.setOnClickListener {
            CleverTapHelper.pushEvent(requireContext(), CleverTapConstants.MEDITATION_TRACKER)
            viewModel.callAddFeatureAccessLogApi(Constants.MEDICATION_TRACKER, "Medication Tracker", "VivantCore", "")
            openAnotherActivity(destination = NavigationConstants.MEDICINE_TRACKER)
        }

        binding.cardFoodTracker.setOnClickListener {
            launchFitrofySdp()
        }

        binding.progressAktivoScore.isClickable = false
        binding.progressAktivoScore.setOnTouchListener { _, _ -> true }
        binding.progressMindScore.isClickable = false
        binding.progressMindScore.setOnTouchListener { _, _ -> true }
        binding.hraProgressBar.isClickable = false
        binding.hraProgressBar.setOnTouchListener { _, _ -> true }
    }

    @SuppressLint("SetTextI18n")
    private fun setupHRAWidgetUI(model: HRAObservationModel) {
        if (model.obaservation.contains(resources.getString(R.string.TAKE), true)) {
            binding.layoutHraGiven.visibility = View.GONE
            binding.layoutHraStart.visibility = View.VISIBLE
            //playTutorialHraNotGiven()
        } else {
            binding.layoutHraGiven.visibility = View.VISIBLE
            binding.layoutHraStart.visibility = View.GONE
            //binding.txtHraObservation.text = model.obaservation
            binding.txtHraScore.text = "${model.hraScore}"
            binding.hraProgressBar.setValueAnimated(model.hraScore.toFloat(), Constants.ANIMATION_DURATION.toLong())
            //binding.hraProgressBar.setBackgroundColor(resources.getColor(model.color))
            binding.hraProgressBar.setBarColor(ContextCompat.getColor(requireContext(), model.color))
            //playTutorialHraGiven()
        }
    }

    private fun checkCameraPermissionAndProceed() {
        val permissionResult = permissionUtil.checkCameraPermissionFaceScan(object : PermissionUtil.AppPermissionListener {
            override fun isPermissionGranted(isGranted: Boolean) {
                Utilities.printLogError("$isGranted")
                if (isGranted) {
                    viewModel.showProgress()
                    wyhFaceScanViewModel.callGetFaceScanVitalsApi()
                }
            }
        }, requireContext())
        if ( permissionResult ) {
            viewModel.showProgress()
            wyhFaceScanViewModel.callGetFaceScanVitalsApi()
        }
    }

    private fun navigateToFaceScanWithPermission(from: String) {
        val permissionResult = permissionUtil.checkCameraPermissionFaceScan(object : PermissionUtil.AppPermissionListener {
            override fun isPermissionGranted(isGranted: Boolean) {
                Utilities.printLogError("$isGranted")
                if (isGranted) {
                    navigateToFaceScanBegin(from)
                }
            }
        }, requireContext())
        if ( permissionResult ) {
            navigateToFaceScanBegin(from)
        }
    }

    private fun navigateToFaceScanBegin(from: String) {
        val data = HashMap<String, Any>()
        data[CleverTapConstants.FROM] = from
        CleverTapHelper.pushEventWithProperties(requireContext(), CleverTapConstants.SCAN_YOUR_VITALS, data)
        openAnotherActivity(destination = NavigationConstants.BEGIN_VITALS_ACTIVITY) {
            putString(Constants.FROM, Constants.DASHBOARD)
        }
    }

    fun navigateToAktivo(screenCode: String) {
        when(screenCode) {
            Constants.AKTIVO_DASHBOARD_CODE -> CleverTapHelper.pushEvent(requireContext(),CleverTapConstants.AKTIVO_DASHBOARD)
            Constants.AKTIVO_CHALLENGES_CODE -> CleverTapHelper.pushEvent(requireContext(),CleverTapConstants.AKTIVO_CHALLENGES)
            Constants.AKTIVO_BADGES_CODE -> {
                val aktivoData = HashMap<String, Any>()
                aktivoData[CleverTapConstants.BADGE] = aktivoBadge
                CleverTapHelper.pushEventWithProperties(requireContext(),CleverTapConstants.AKTIVO_BADGES,aktivoData)
            }
            Constants.AKTIVO_SCORE_CODE -> {
                val aktivoData = HashMap<String, Any>()
                aktivoData[CleverTapConstants.SCORE] = aktivoScore
                CleverTapHelper.pushEventWithProperties(requireContext(),CleverTapConstants.AKTIVO_SCORE,aktivoData)
            }
            Constants.AKTIVO_MIND_SCORE_CODE -> {
                val aktivoData = HashMap<String, Any>()
                aktivoData[CleverTapConstants.SCORE] = aktivoMindScore
                CleverTapHelper.pushEventWithProperties(requireContext(),CleverTapConstants.MIND_SCORE,aktivoData)
            }
        }
        openAnotherActivity(destination = NavigationConstants.AKTIVO_PERMISSION_SCREEN) {
            putString(Constants.CODE, screenCode)
        }
    }

    private fun goToHRA() {
        CleverTapHelper.pushEvent(requireContext(), CleverTapConstants.HEALTH_RISK_ASSESSMENT)
        viewModel.callAddFeatureAccessLogApi(Constants.HRA, "HRA", "VivantCore", "")
        try {
            if (viewModel.hraDetails.value != null) {
                val hraSummary = viewModel.hraDetails.value
                val currentHRAHistoryID = hraSummary?.currentHRAHistoryID.toString()
                val wellnessScore = hraSummary?.scorePercentile.toString()
                val hraCutOff = hraSummary?.hraCutOff
                if (!Utilities.isNullOrEmpty(currentHRAHistoryID) && currentHRAHistoryID != "0") {
                    if (hraCutOff.equals("0")) {
                        navigateToHraStart()
                    } else if (!Utilities.isNullOrEmpty(wellnessScore)) {
                        navigateToHraSummary()
                    } else {
                        navigateToHraStart()
                    }
                } else {
                    navigateToHraStart()
                }
            } else {
                navigateToHraStart()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun navigateToHraStart() {
        openAnotherActivity(destination = NavigationConstants.HRA_INFO)
        //openAnotherActivity(destination = NavigationConstants.HRA_HOME)
    }

    private fun navigateToHraSummary() {
        val data = HashMap<String, Any>()
        data[CleverTapConstants.FROM] = CleverTapConstants.DASHBOARD
        CleverTapHelper.pushEventWithProperties(requireContext(), CleverTapConstants.HRA_SUMMARY_SCREEN, data)
        openAnotherActivity(destination = NavigationConstants.HRA_SUMMARY)
    }

    private fun launchFitrofySdp() {
        CleverTapHelper.pushEvent(requireContext(),CleverTapConstants.FITROFY_SDP)
        openAnotherActivity(destination = NavigationConstants.FITROFY_SDP_SCREEN)
    }

    private fun getAktivoScore(showProgress:Boolean) {
        val aktivoMemberId = viewModel.getUserPreference(PreferenceConstants.AKTIVO_MEMBER_ID)
        val aktivoAccessToken = viewModel.getUserPreference(PreferenceConstants.AKTIVO_ACCESS_TOKEN)
        val aktivoRefreshToken = viewModel.getUserPreference(PreferenceConstants.AKTIVO_REFRESH_TOKEN)
        if ( Utilities.isNullOrEmpty(aktivoMemberId) ) {

            try{
                if(FirebaseApp.getApps(requireContext()).isNotEmpty()){

                    FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                        // Get new FCM registration token
                        val token = task.result!!
                        Utilities.printLogError("\nDeviceToken--->$token")
                        viewModel.callAktivoCreateUserApi(token)
                    }
                }
            }catch (e:Exception){
                e.printStackTrace()
            }


        } else if(Utilities.isNullOrEmpty(aktivoAccessToken) || Utilities.isNullOrEmpty(aktivoRefreshToken) ) {
            viewModel.callAktivoGetUserTokenApi(aktivoMemberId)
        } else {
            authenticateUserUsingToken()
        }
    }

    private fun authenticateUserUsingToken() {
        val userId = viewModel.getUserPreference(PreferenceConstants.AKTIVO_MEMBER_ID)
        val token = viewModel.getUserPreference(PreferenceConstants.AKTIVO_ACCESS_TOKEN)
        val refreshToken = viewModel.getUserPreference(PreferenceConstants.AKTIVO_REFRESH_TOKEN)
        aktivoManager!!.setClientId(Constants.strAktivoClientId)
        aktivoManager!!.setUserTokens(token, refreshToken)
        authenticateUser(userId)
    }

    private fun authenticateUser(userId: String) {
        try {
            aktivoManager!!.authenticateUser(User(userId), requireActivity())
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : CompletableObserver {
                    override fun onSubscribe(d: Disposable) {}
                    override fun onComplete() {
                        Utilities.printLogError("User Authenticated")
                        checkFitnessPermissionsAndProceed()
                    }

                    override fun onError(e: Throwable) {
                        //stopAllAktivoShimmers()
                        e.printStackTrace()
                    }
                })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun checkFitnessPermissionsAndProceed() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (fitnessDataManager!!.aktivoAuthPermissionsApproved() && permissionUtil.isActivityRecognitionPermission(binding.txtUserName.context)) {
                syncFitnessData()
            } else {
                showAktivoPermissionsDialog()
            }
        } else {
            if (fitnessDataManager!!.aktivoAuthPermissionsApproved()) {
                syncFitnessData()
            } else {
                showAktivoPermissionsDialog()
            }
        }
    }

    private fun showAktivoPermissionsDialog() {
        if (this.isVisible) {
            val dialogData = DefaultNotificationDialog.DialogData()
            dialogData.title = resources.getString(R.string.PERMISSIONS_REQUIRED)
            //dialogData.imgResource = R.drawable.app_logo_white
            dialogData.message =
                "<a>" + "${resources.getString(R.string.NEED_HEART_RATE_PERMISSION)} <br/><br/> ${
                    resources.getString(R.string.NEED_SLEEP_PERMISSION)
                } <br/><br/> ${resources.getString(R.string.NEED_PHYSICAL_PERMISSION)}" + "</a>"
            dialogData.showLeftButton = false
            dialogData.btnRightName = resources.getString(R.string.ALLOW_PERMISSIONS)
            dialogData.showDismiss = false
            val defaultNotificationDialog =
                DefaultNotificationDialog(
                    (activity as HomeMainActivity),
                    object : DefaultNotificationDialog.OnDialogValueListener {
                        override fun onDialogClickListener(isButtonLeft: Boolean, isButtonRight: Boolean) {
                            if (isButtonRight) {
                                checkAllGoogleFitPermission()
                            }
                        }
                    },dialogData)
            defaultNotificationDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            defaultNotificationDialog.show()
        }
    }

    private fun syncFitnessData() {
        try {
            //viewModel.showProgress()
            compositeDisposable!!.add(
                aktivoManager!!.syncFitnessData().subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableCompletableObserver() {
                        override fun onComplete() {
                            //viewModel.hideProgress()
                            Utilities.printLogError("Data Synced")
                            getLatestAktivoScore()
                            getMindScore()
                            getAktivoParameters()
                            getBadgeByDate()
                            getOngoingChallenges()
                        }
                        override fun onError(e: Throwable) {
                            //viewModel.hideProgress()
                            //Utilities.toastMessageShort(requireContext(), "Data Sync error: " + e.message)
                            e.printStackTrace()
                            //stopAllAktivoShimmers()
                            getLatestAktivoScore()
                            getMindScore()
                            getAktivoParameters()
                            getBadgeByDate()
                            getOngoingChallenges()
                        }
                    })
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getLatestAktivoScore() {
        try {
            var score = "0"
            Utilities.printLogError("TodaysDate--->$selectedDate")
            val date = DateHelper.getDateBeforeOrAfterGivenDaysNew(selectedDate, -1)
            Utilities.printLogError("Getting Aktivo Score for Date--->$date")
            aktivoManager!!.query(ScoreQuery(date, date)).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map { localDateScoreStatsMap: Map<LocalDate, ScoreStats> -> localDateScoreStatsMap }
                .subscribe(object : SingleObserver<Map<LocalDate, ScoreStats>> {
                    override fun onSubscribe(d: Disposable) {}
                    override fun onSuccess(localDateScoreStatsMap: Map<LocalDate, ScoreStats>) {
                        val keySet = localDateScoreStatsMap.keys
                        for (localDate in keySet) {
                            Utilities.printLogError("***********************************")
                            score = localDateScoreStatsMap[localDate]!!.score.toString()
                            Utilities.printLogError("Date : $localDate , Aktivo Score: $score")
                            Utilities.printLogError("***********************************")
                            if (!Utilities.isNullOrEmptyOrZero(score)) {
                                binding.progressAktivoScore.setProgressWithAnimation(score.toDouble(),Constants.ANIMATION_DURATION)
                                binding.txtAktivoScore.text = score
                                aktivoScore = score.toInt()
                            }
                        }
                        val aktivoData = HashMap<String, Any>()
                        aktivoData[CleverTapConstants.AKTIVO_SCORE2] = aktivoScore
                        CleverTapHelper.pushEventWithProperties(requireContext(),CleverTapConstants.AKTIVO_HEALTH_DATA_INFO,aktivoData)
                        //stopAktivoScoreShimmer()
                    }
                    override fun onError(e: Throwable) {
                        Utilities.printLogError("Error in getScoreStats: " + e.message)
                        //stopAktivoScoreShimmer()
                    }
                })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getAktivoParameters() {
        val paramList: MutableList<AktiVoParamModel> = mutableListOf()
        val queryList = ArrayList<Query>()
        queryList.add(ScoreQuery(selectedDate,selectedDate))
        queryList.add(StepsQuery(selectedDate,selectedDate))
        queryList.add(SleepQuery(selectedDate,selectedDate))
        aktivoManager!!.query(queryList).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).map { maps -> maps }
            .subscribe(object : SingleObserver<List<Map<LocalDate, Stats>>> {
                override fun onSubscribe(d: Disposable) {}
                override fun onSuccess(mapList: List<Map<LocalDate, Stats>>) {
                    Utilities.printData("MapList", mapList, true)
                    Utilities.printLogError("MapSize--->" + mapList.size)
                    var steps = 0
                    var sleepInMinitues = 0
                    var exerciseInMinitues = 0
                    var sedentaryInMinitues = 0
                    var lightActivityInMinitues = 0
                    for (i in mapList.indices) {
                        val keySet = mapList[i].keys
                        if (queryList[i] is ScoreQuery) {
                            var exercise = 0
                            var lightActivity = 0
                            var sedentary = 0
                            for (`object` in keySet) {
                                //Utilities.printLogError("Date: " + `object` + " value: " + (mapList[i][`object`] as ScoreStats?)!!.score)
                                exercise = (mapList[i][`object`] as ScoreStats?)!!.scoreMvpa.value
                                lightActivity = (mapList[i][`object`] as ScoreStats?)!!.scoreLipa.value
                                sedentary = (mapList[i][`object`] as ScoreStats?)!!.scoreSb.value
                                Utilities.printLogError("Date: $`object` Exercise: $exercise")
                                Utilities.printLogError("Date: $`object` LightActivity: $lightActivity")
                                Utilities.printLogError("Date: $`object` Sedentary: $sedentary")
                                exerciseInMinitues = DateHelper.convertSecToMin(exercise)
                                sedentaryInMinitues = DateHelper.convertSecToMin(sedentary)
                                lightActivityInMinitues = DateHelper.convertSecToMin(lightActivity)
                                //paramList.add(AktiVoParamModel(2,Constants.EXERCISE,resources.getString(R.string.EXERCISE),DateHelper.getHourMinFromSecondsAktivo1(exercise),R.drawable.img_aktivo_exercise,R.color.colorPrimary,R.color.ab2 ))
                                //binding.viewLightActivity.setParamValue(DateHelper.getHourMinFromSecondsAktivo(lightActivity))
                                //binding.viewSedentary.setParamValue(DateHelper.getHourMinFromSecondsAktivo(sedentary))
                            }
                            if ( exercise >= 0 ) {
                                paramList.add(AktiVoParamModel(2,Constants.EXERCISE,resources.getString(R.string.EXERCISE),DateHelper.getHourMinFromSecondsAktivo1(exercise),R.drawable.img_aktivo_exercise,R.color.colorPrimary,R.color.ab2 ))
                            } else {
                                paramList.add(AktiVoParamModel(2,Constants.EXERCISE,resources.getString(R.string.EXERCISE),"0",R.drawable.img_aktivo_exercise,R.color.colorPrimary,R.color.ab2 ))
                            }
                        } else if (queryList[i] is StepsQuery) {
                            for (`object` in keySet) {
                                steps = (mapList[i][`object`] as StepStats?)!!.value
                                Utilities.printLogError("Date: $`object` Steps: $steps")
                            }
                            paramList.add(AktiVoParamModel(1,Constants.STEPS,resources.getString(R.string.STEPS),steps.toString(),R.drawable.img_aktivo_steps,R.color.color_meditation,R.color.ab1 ))
                        } else if (queryList[i] is SleepQuery) {
                            var sleep = 0
                            for (`object` in keySet) {
                                sleep = (mapList[i][`object`] as SleepStats?)!!.value
                                Utilities.printLogError("Date: $`object` Sleep: $sleep")
                                sleepInMinitues = DateHelper.convertSecToMin(sleep)
                                //paramList.add(AktiVoParamModel(3,Constants.SLEEP,resources.getString(R.string.SLEEP),DateHelper.getHourMinFromSecondsAktivo1(sleep),R.drawable.img_aktivo_sleep,R.color.vivant_teal_blue,R.color.ab3 ))
                            }
                            if ( sleep >= 0 ) {
                                paramList.add(AktiVoParamModel(3,Constants.SLEEP,resources.getString(R.string.SLEEP),DateHelper.getHourMinFromSecondsAktivo1(sleep),R.drawable.img_aktivo_sleep,R.color.vivant_teal_blue,R.color.ab3 ))
                            } else {
                                paramList.add(AktiVoParamModel(3,Constants.SLEEP,resources.getString(R.string.SLEEP),"0",R.drawable.img_aktivo_sleep,R.color.vivant_teal_blue,R.color.ab3 ))
                            }
                        }
                    }
                    aktivoParamAdapter!!.updateList(paramList)

                    //stopAktivoParameterShimmer()
                    val aktivoData = HashMap<String, Any>()
                    aktivoData[CleverTapConstants.AKTIVO_STEPS] = steps
                    aktivoData[CleverTapConstants.AKTIVO_SLEEP] = sleepInMinitues
                    aktivoData[CleverTapConstants.AKTIVO_EXERCISE] = exerciseInMinitues
                    aktivoData[CleverTapConstants.AKTIVO_SEDENTARY] = sedentaryInMinitues
                    aktivoData[CleverTapConstants.AKTIVO_LIGHT_ACTIVITY] = lightActivityInMinitues
                    CleverTapHelper.pushEventWithProperties(requireContext(),CleverTapConstants.AKTIVO_HEALTH_DATA_INFO,aktivoData)
                }
                override fun onError(e: Throwable) {
                    //stopAktivoParameterShimmer()
                    Utilities.printLogError("Aktivo stats error: " + e.localizedMessage + "---" + e.message)
                }
            })
    }

    private fun getMindScore() {
        aktivoManager!!
        try {
            val resultData: ResultData<MindScore> = aktivoManager!!.getLatestMindScore(viewModel.getUserPreference(PreferenceConstants.AKTIVO_MEMBER_ID))
            when (resultData) {
                is ResultData.Success -> {
                    val mindScore = resultData.data.score
                    Utilities.printLogError("MindScore : $mindScore")
                    if (mindScore >= 0) {
                        aktivoMindScore = mindScore
                        binding.txtMindScore.text = "$aktivoMindScore"
                        binding.progressMindScore.setValueAnimated(aktivoMindScore.toFloat(),Constants.ANIMATION_DURATION.toLong())
                    }
                    val aktivoData = HashMap<String, Any>()
                    aktivoData[CleverTapConstants.AKTIVO_MIND_SCORE] = aktivoMindScore
                    CleverTapHelper.pushEventWithProperties(requireContext(),CleverTapConstants.AKTIVO_HEALTH_DATA_INFO,aktivoData)
                    //stopMindScoreShimmer()
                }

                is ResultData.Error -> {
                    Utilities.printLogError("Error in getMindScore: " + resultData.errorData.message)
                    //stopMindScoreShimmer()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getBadgeByDate() {
        try {
            val date = DateHelper.getDateBeforeOrAfterGivenDaysNew(selectedDate, -1)
            val badgeByDateQuery = BadgeByDateQuery(date)

            aktivoManager!!.queryBadgeByDate(badgeByDateQuery).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : SingleObserver<DailyBadge> {
                    override fun onSubscribe(d: Disposable) {}
                    override fun onSuccess(dailyBadge: DailyBadge) {
                        if (dailyBadge.badgeType != null) {
                            //Utilities.printData("DailyBadge", dailyBadge, true)
                            binding.layoutBadgeProgress.visibility = View.VISIBLE
                            if (this@HomeScreenNewFragment.isVisible) {
                                aktivoBadge = dailyBadge.badgeType.badgeTypeEnum.toString()
                                binding.imgBadge.setImageResource(Utilities.getBadgeImageByCode(aktivoBadge))
                                binding.txtBadge.text = Utilities.getBadgeNameByCode(aktivoBadge,requireContext())
                            }
                        }
                    }
                    override fun onError(e: Throwable) {
                        //val message = "BadgesByDate, error: " + e.message
                        binding.layoutBadgeProgress.visibility = View.GONE
                    }
                })

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getOngoingChallenges() {
        try {
            Utilities.printLogError("Fetching ongoing challenges")
            aktivoManager!!.getOngoingChallenges(ChallengeListQuery(viewModel.getUserPreference(PreferenceConstants.AKTIVO_MEMBER_ID), false)
            ).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : SingleObserver<List<Challenge>> {
                    override fun onSubscribe(d: Disposable) {}
                    override fun onSuccess(challenges: List<Challenge>) {
                        Utilities.printLogError("Ongoing_Challenges_Count--->${challenges.size}")
                        Utilities.printData("Ongoing_Challenges", challenges, true)
                        challengesList.clear()
                        challengesList.addAll(challenges.toMutableList().distinct())
                        slidingDashboardAdapter!!.updatePolicyBannerList()
/*                        if (challenges.isNotEmpty()) {
                            binding.layoutChallenges.visibility = View.VISIBLE
                            challengesList.clear()
                            challengesList.addAll(challenges.toMutableList())
                            slidingDashboardAdapter!!.updatePolicyBannerList()
                            //setUpSlidingChallengesViewPager(challenges.toMutableList())
                        } else {
                            binding.layoutChallenges.visibility = View.GONE
                        }*/
                        //checkToPlayTutorial()
                    }
                    override fun onError(e: Throwable) {
                        Utilities.printLogError("getOngoingChallenges error:$e")
                        binding.layoutChallenges.visibility = View.GONE
                        //checkToPlayTutorial()
                    }
                })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setUpSlidingDashboardViewPager() {
        try {
            slidingDashboardAdapter = SlidingDashboardAdapter( requireActivity(),this )
            binding.slidingViewPagerChallenges.apply {
                post {
                    adapter = slidingDashboardAdapter
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun showSlidingDots(models:MutableList<DataHandler.DashboardBannerModel>) {
        try {
            binding.shiftingIndicator.attachTo(binding.slidingViewPagerChallenges)
            handler = Handler(Looper.getMainLooper())
            val runnable = object : Runnable {
                override fun run() {
                    if ( currentPage == models.size ) {
                        currentPage = 0
                    }
                    binding.slidingViewPagerChallenges.setCurrentItem(currentPage++,true)
                    handler.postDelayed(this,Constants.POLICY_BANNER_DELAY_IN_MS.toLong())
                }
            }
            handler.postDelayed(runnable,Constants.POLICY_BANNER_DELAY_IN_MS.toLong())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacksAndMessages(null)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacksAndMessages(null)
    }


    private fun checkAllGoogleFitPermission() {
        try {
            aktivoManager!!.isGoogleFitPermissionGranted.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : SingleObserver<Boolean> {
                    override fun onSubscribe(d: Disposable) {}
                    @RequiresApi(Build.VERSION_CODES.Q)
                    override fun onSuccess(aBoolean: Boolean) {
                        if (aBoolean) {
                            Utilities.printLogError("All Google fit permissions granted")
                            checkPhysicalActivityPermission()
                        } else {
                            requestAllGoogleFitPermission()
                        }
                    }

                    override fun onError(e: Throwable) {}
                })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun requestAllGoogleFitPermission() {
        try {
            Utilities.printLogError("Requesting All Google fit permissions")
            aktivoManager!!.requestGoogleFitPermissions(requireActivity(),Constants.REQ_CODE_AKTIVO_GOOGLE_FIT_PERMISSIONS
            ).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(object : CompletableObserver {
                    override fun onSubscribe(d: Disposable) {}
                    override fun onError(e: Throwable) {}
                    override fun onComplete() {
                        Utilities.printLogError("Permission requested")
                    }
                })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun checkPhysicalActivityPermission() {
        try {
            Utilities.printLogError("Checking Physical Activity permissions")
            aktivoManager!!.isActivityRecognitionPermissionGranted(requireActivity())
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : SingleObserver<Boolean> {
                    override fun onSubscribe(d: Disposable) {}
                    override fun onSuccess(aBoolean: Boolean) {
                        if (aBoolean) {
                            Utilities.printLogError("Physical Activity permissions granted")
                            syncFitnessData()
                        } else {
                            requestPhysicalActivityPermission()
                        }
                    }

                    override fun onError(e: Throwable) {}
                })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private fun requestPhysicalActivityPermission() {
        try {
            Utilities.printLogError("Requesting Physical Activity permissions")
            aktivoManager!!.requestActivityRecognitionPermission(requireActivity(),Constants.REQ_PHYSICAL_ACTIVITY_PERMISSIONS
            ).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : CompletableObserver {
                    override fun onSubscribe(d: Disposable) {}
                    override fun onError(e: Throwable) {}
                    override fun onComplete() {
                        Utilities.printLogError("Permission requested")
                    }
                })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun loadData(data: GetDailyWaterIntakeModel.ResultData) {
        try {
            if (!Utilities.isNullOrEmpty(data.totalWaterIntake)) {
                binding.txtWaterConsumed.text = "${data.totalWaterIntake!!.toDouble().roundToInt()}"
                if (!Utilities.isNullOrEmpty(data.waterGoal)) {
                    todayIntakePercentage = ((data.totalWaterIntake.toDouble() * 100) / data.waterGoal!!.toDouble()).roundToInt()
                    //binding.imgWaterTracker.setImageResource(Utilities.getWaterDropImageByValue(todayIntakePercentage.toDouble()))
                    /*                    if (todayIntakePercentage > 99.99) {
                                            binding.txtIntakePercent.text = "100 %"
                                        } else {
                                            binding.txtIntakePercent.text = "$todayIntakePercentage %"
                                        }*/
                }
            } else {
                binding.txtWaterConsumed.text = "0"
                todayIntakePercentage = 0
                //binding.imgWaterTracker.setImageResource(Utilities.getWaterDropImageByValue(0.0))
            }

            if (!Utilities.isNullOrEmpty(data.waterGoal)) {
                binding.txtWaterGoal.text = " / ${data.waterGoal!!.toDouble().toInt()} ${resources.getString(R.string.ML)}"
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun checkWyhToken() {
        val wyhTokenLastSync = viewModel.getUserPreference(PreferenceConstants.WYH_TOKEN_LAST_SYNC)
        if ( !Utilities.isNullOrEmpty(wyhTokenLastSync) ) {
            val current = DateHelper.stringDateToDate(DateHelper.DATE_FORMAT_UTC,DateHelper.currentUTCDatetimeInMillisecAsString)
            val lastSync = DateHelper.stringDateToDate(DateHelper.DATE_FORMAT_UTC,wyhTokenLastSync)
            val diff = TimeUnit.MILLISECONDS.toHours(current.time - lastSync.time)
            Utilities.printLogError("DifferenceIn_WYH_Token_Last_Sync--->$diff")
            if ( diff >= Constants.WYH_TOKEN_EXPIRY_HR ) {
                viewModel.showProgress()
                wyhFaceScanViewModel.callGetWyhAuthorizationApi()
            }
            /*else {
                wyhFaceScanViewModel.callGetFaceScanVitalsApi()
            }*/
        } else {
            viewModel.showProgress()
            wyhFaceScanViewModel.callGetWyhAuthorizationApi()
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onAktivoSelection(from: String) {
        Utilities.printLogError("from---> $from")
        when (from) {
            Constants.PHYSICAL_ACTIVITY_PERMISSION -> {
                checkPhysicalActivityPermission()
            }
            Constants.SYNC_FITNESS_DATA -> {
                syncFitnessData()
            }
            Constants.DENIED -> {
                //stopAllAktivoShimmers()
                //checkToPlayTutorial()
            }
        }
    }

    override fun onHelpClick() { }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permissionResult = permissionUtil.checkNotificationPermission(object :
                PermissionUtil.AppPermissionListener {
                override fun isPermissionGranted(isGranted: Boolean) {
                    Utilities.printLogError("$isGranted")
                    if (isGranted) {
                        Utilities.toastMessageShort(requireContext(), resources.getString(R.string.MSG_NOTIFICATION_PERMISSION))
                    }
                }
            }, requireContext())
            //if (permissionResult) { }
        }
    }

/*    private fun setUpSlidingDashboardViewPager1() {
        try {
            slidingDashboardAdapter = SlidingDashboardAdapter( requireActivity(),this )

            binding.slidingViewPagerChallenges.apply {
                post {
                    adapter = slidingDashboardAdapter
                }
                registerOnPageChangeCallback( object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        for (i in 0 until slidingDotsCount) {
                            slidingImageDots[i]?.setImageDrawable(ContextCompat.getDrawable(binding.slidingViewPagerChallenges.context, R.drawable.dot_non_active))
                        }
                        slidingImageDots[position]?.setImageDrawable(ContextCompat.getDrawable(binding.slidingViewPagerChallenges.context, R.drawable.dot_active))
                    }
                })
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun showSlidingDots1(models:MutableList<DataHandler.DashboardBannerModel>) {
        try {
            slidingDotsCount = models.size
            slidingImageDots = arrayOfNulls(slidingDotsCount)
            binding.sliderDotsChallenges.removeAllViews()
            if (slidingDotsCount > 1) {
                for (i in 0 until slidingDotsCount) {
                    slidingImageDots[i] = ImageView(binding.slidingViewPagerChallenges.context)
                    slidingImageDots[i]?.setImageDrawable(ContextCompat.getDrawable(binding.slidingViewPagerChallenges.context,R.drawable.dot_non_active))
                    val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                    params.setMargins(8, 0, 8, 0)
                    binding.sliderDotsChallenges.addView(slidingImageDots[i], params)
                }
                slidingImageDots[0]?.setImageDrawable(ContextCompat.getDrawable(binding.slidingViewPagerChallenges.context, R.drawable.dot_active))

                val handler = Handler(Looper.getMainLooper())
                val update = Runnable {
                    if (currentPage1 == slidingDotsCount) {
                        currentPage1 = 0
                    }
                    binding.slidingViewPagerChallenges.setCurrentItem(currentPage1++, true)
                }
                Timer().schedule(object : TimerTask() {
                    override fun run() {
                        handler.post(update)
                    }
                },0,Constants.POLICY_BANNER_DELAY_IN_MS.toLong())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setUpSlidingChallengesViewPager(challengesList: MutableList<Challenge>) {
        try {
            slidingDotsCountChallenges = challengesList.size
            slidingImageDotsChallenges = arrayOfNulls(slidingDotsCountChallenges)
            val landingImagesAdapter = SlidingAktivoChallengesAdapter(requireContext(), requireActivity(), slidingDotsCountChallenges, challengesList)

            binding.slidingViewPagerChallenges.apply {
                adapter = landingImagesAdapter
                registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        for (i in 0 until slidingDotsCountChallenges) {
                            slidingImageDotsChallenges[i]?.setImageDrawable(ContextCompat.getDrawable(binding.slidingViewPagerChallenges.context, R.drawable.dot_non_active))
                        }
                        slidingImageDotsChallenges[position]?.setImageDrawable(ContextCompat.getDrawable(binding.slidingViewPagerChallenges.context, R.drawable.dot_active))
                    }
                })
            }

            if (slidingDotsCountChallenges > 1) {
                for (i in 0 until slidingDotsCountChallenges) {
                    slidingImageDotsChallenges[i] = ImageView(binding.slidingViewPagerChallenges.context)
                    slidingImageDotsChallenges[i]?.setImageDrawable(ContextCompat.getDrawable(binding.slidingViewPagerChallenges.context, R.drawable.dot_non_active))
                    val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                    params.setMargins(8, 0, 8, 0)
                    binding.sliderDotsChallenges.addView(slidingImageDotsChallenges[i], params)
                }
                slidingImageDotsChallenges[0]?.setImageDrawable(ContextCompat.getDrawable(binding.slidingViewPagerChallenges.context, R.drawable.dot_active))

                val handler = Handler(Looper.getMainLooper())
                val update = Runnable {
                    if (currentPage1 == slidingDotsCountChallenges) {
                        currentPage1 = 0
                    }
                    binding.slidingViewPagerChallenges.setCurrentItem(currentPage1++, true)
                }
                Timer().schedule(object : TimerTask() {
                    override fun run() {
                        handler.post(update)
                    }
                }, 0,Constants.POLICY_BANNER_DELAY_IN_MS.toLong())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }*/

}