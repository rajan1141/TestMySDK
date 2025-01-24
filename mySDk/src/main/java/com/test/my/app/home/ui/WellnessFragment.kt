package com.test.my.app.home.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.test.my.app.R
import com.test.my.app.common.base.BaseFragment
import com.test.my.app.common.base.BaseViewModel
import com.test.my.app.common.constants.CleverTapConstants
import com.test.my.app.common.constants.Constants
import com.test.my.app.common.constants.NavigationConstants
import com.test.my.app.common.extension.openAnotherActivity
import com.test.my.app.common.utils.CleverTapHelper
import com.test.my.app.common.utils.Utilities
import com.test.my.app.databinding.FragmentWellnessBinding
import com.test.my.app.home.adapter.HealthSelfCareAdapter
import com.test.my.app.home.adapter.HealthSelfCareAdapter.OnHealthSelfCareListener
import com.test.my.app.home.adapter.LiveSessionAdapter
import com.test.my.app.home.adapter.RiskToolsAdapter
import com.test.my.app.home.adapter.RiskToolsAdapter.OnRiskToolsListener
import com.test.my.app.home.adapter.SmitFitAdapterNew
import com.test.my.app.home.adapter.SmitFitAdapterNew.OnSmitFitFeatureListener
import com.test.my.app.home.common.DataHandler
import com.test.my.app.home.viewmodel.HomeViewModel
import com.test.my.app.model.home.LiveSessionModel
import com.test.my.app.model.toolscalculators.UserInfoModel
import com.smitfit.media.lib.ui.feature.main.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WellnessFragment : BaseFragment(),OnSmitFitFeatureListener,OnRiskToolsListener,
    OnHealthSelfCareListener,LiveSessionAdapter.OnLiveSessionListener {

    lateinit var binding: FragmentWellnessBinding
    private val viewModel: HomeViewModel by lazy {
        ViewModelProvider(this)[HomeViewModel::class.java]
    }

    private var liveSessionAdapter: LiveSessionAdapter? = null
    private var smitFitAdapter: SmitFitAdapterNew? = null
    private var riskToolsAdapter: RiskToolsAdapter? = null
    private var healthSelfCareAdapter: HealthSelfCareAdapter? = null

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as HomeMainActivity).setToolbarInfo(1, showAppLogo = true, title = resources.getString(R.string.TRACK))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentWellnessBinding.inflate(inflater, container, false)
        initialise()
        setClickable()
        setObserver()
        return binding.root
    }

    private fun initialise() {

        binding.layoutLiveSessions.visibility = View.GONE
        viewModel.callSmitFitEventsApi()

        liveSessionAdapter = LiveSessionAdapter(requireContext(), this)
        binding.rvLiveSessions.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvLiveSessions.adapter = liveSessionAdapter

        smitFitAdapter = SmitFitAdapterNew(requireContext(), this)
        binding.rvSmitFit.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
        binding.rvSmitFit.adapter = smitFitAdapter
        smitFitAdapter!!.updateList(DataHandler(requireContext()).getSmitFitCategoryFeatures())

        riskToolsAdapter = RiskToolsAdapter(requireContext(), this)
        binding.rvRiskTools.layoutManager = GridLayoutManager(requireContext(), 4)
        binding.rvRiskTools.adapter = riskToolsAdapter
        riskToolsAdapter!!.updateList(DataHandler(requireContext()).getRiskToolsFeatures())

        healthSelfCareAdapter = HealthSelfCareAdapter(requireContext(), this)
        binding.rvHealthSelfCare.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
        binding.rvHealthSelfCare.adapter = healthSelfCareAdapter
        healthSelfCareAdapter!!.updateList(DataHandler(requireContext()).getHealthSelfCareFeatures())
    }

    private fun setClickable() {

        binding.layoutFinancialBlogs.setOnClickListener {
            launchNimeyaArticles()
        }

        binding.layoutInvestometer.setOnClickListener {

        }

        binding.layoutLifeGoalCalculators.setOnClickListener {

        }

    }

    private fun setObserver() {

        viewModel.liveSessions.observe(viewLifecycleOwner) {
            if ( it != null ) {
                liveSessionAdapter!!.updateList(it)
                binding.layoutLiveSessions.visibility = View.VISIBLE
                //stopLiveSessionsShimmer()
/*                if (it.isNotEmpty()) {
                    binding.layoutLiveSessions.visibility = View.VISIBLE
                    val campaignFeatureName = Utilities.preferenceUtils.getPreference(Constants.CAMPAIGN_FEATURE_NAME)
                    if( !Utilities.isNullOrEmpty(campaignFeatureName)
                        && campaignFeatureName.equals(Constants.FEATURE_CODE_LIVE_SESSIONS,ignoreCase = true) ) {
                        Utilities.clearCampaignFeatureDetails()
                        redirectToLiveSession(it.toMutableList())
                    }
                } else {
                    binding.layoutLiveSessions.visibility = View.GONE
                    Utilities.clearCampaignFeatureDetails()
                }*/
            }
        }

    }

    private fun launchNimeyaArticles() {
        CleverTapHelper.pushEvent(requireContext(),CleverTapConstants.NIMEYA_FINTALK_FINSTAT)
        openAnotherActivity(destination = NavigationConstants.NIMEYA_WEB_VIEW_SCREEN)
    }

    override fun onSmitFitFeatureClick(feature: DataHandler.FeatureModel) {
        launchSmitFit(feature.featureCode)
    }

    override fun onRiskToolsClick(feature: DataHandler.FeatureModel) {
        when(feature.featureCode) {
            Constants.HRA -> {

            }
            Constants.IRA -> {
                openAnotherActivity(destination = NavigationConstants.WYH_IRA_SUMMARY_ACTIVITY)
            }
            Constants.CODE_HEART_AGE_CALCULATOR,Constants.CODE_DIABETES_CALCULATOR,
            Constants.CODE_HYPERTENSION_CALCULATOR,Constants.CODE_STRESS_ANXIETY_CALCULATOR,
            Constants.CODE_SMART_PHONE_ADDICTION_CALCULATOR,Constants.CODE_DUE_DATE_CALCULATOR -> {
                viewModel.callAddFeatureAccessLogApi(Constants.TOOLS_AND_CALCULATORS, "Tools and Calculators", "VivantCore", "")
                UserInfoModel.getInstance()!!.isDataLoaded = false
                openAnotherActivity(destination = NavigationConstants.TOOLS_CALCULATORS_HOME) {
                    putString(Constants.FROM,Constants.HOME)
                    putString(Constants.TO,feature.featureCode)
                }
            }
        }
    }

    override fun onHealthSelfCareClick(feature: DataHandler.FeatureModel) {
        when(feature.featureCode) {
            Constants.HEALTH_LIBRARY -> {
                CleverTapHelper.pushEvent(requireContext(), CleverTapConstants.HEALTH_LIBRARY)
                //viewModel.callAddFeatureAccessLogApi(Constants.HEALTH_LIBRARY, "Health Library", "VivantCore", "")
                openAnotherActivity(destination = NavigationConstants.HEALTH_CONTENT_DASHBOARD_ACTIVITY)
            }
            Constants.TRACK_PARAMETERS -> {
                CleverTapHelper.pushEvent(requireContext(), CleverTapConstants.TRACK_VITAL_PARAMETERS)
                //viewModel.callAddFeatureAccessLogApi(Constants.TRACK_PARAMETERS, "Track Parameters", "VivantCore", "")
                openAnotherActivity(destination = NavigationConstants.PARAMETER_HOME_ACTIVITY)
            }
            Constants.MENTAL_WELLNESS -> {

            }
        }
    }

    private fun launchSmitFit(featureCode:String) {
        try {
            val data = HashMap<String, Any>()
            data[CleverTapConstants.FROM] = CleverTapConstants.DASHBOARD
            when (featureCode) {
                Constants.MEDITATION -> {
                    CleverTapHelper.pushEventWithProperties(requireContext(), CleverTapConstants.MEDITATION, data)
                    startActivity(MainActivity.getMeditationIntent(requireContext()))

                }
                Constants.YOGA -> {
                    CleverTapHelper.pushEventWithProperties(requireContext(), CleverTapConstants.YOGA, data)
                    startActivity(MainActivity.getYogaIntent(requireContext()))
                }
                Constants.EXERCISE -> {
                    CleverTapHelper.pushEventWithProperties(requireContext(), CleverTapConstants.EXERCISE, data)
                    startActivity(MainActivity.getExerciseIntent(requireContext()))
                }
                Constants.HEALTHY_BITES -> {
                    CleverTapHelper.pushEventWithProperties(requireContext(), CleverTapConstants.HEALTHY_BITES, data)
                    startActivity(MainActivity.getRecipyIntent(requireContext()))
                }
                Constants.WOMEN_HEALTH -> {
                    CleverTapHelper.pushEventWithProperties(requireContext(), CleverTapConstants.WOMEN_HEALTH, data)
                    startActivity(MainActivity.getPcosIntent(requireContext()))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onLiveSessionClick(liveSessionModel: LiveSessionModel) {
        val data = HashMap<String, Any>()
        data[CleverTapConstants.ID] = liveSessionModel.id
        data[CleverTapConstants.DATE] = liveSessionModel.date.split("T").toTypedArray()[0]
        data[CleverTapConstants.FROM] = CleverTapConstants.DASHBOARD
        CleverTapHelper.pushEventWithProperties(requireContext(), CleverTapConstants.JOIN_LIVE_SESSION, data)
        //Utilities.redirectToChrome(liveSessionModel.link, requireContext())
        Utilities.redirectToCustomChromeTab(liveSessionModel.link,requireContext())
    }

    /*    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            return inflater.inflate(R.layout.fragment_wellness, container, false)
        }*/

}