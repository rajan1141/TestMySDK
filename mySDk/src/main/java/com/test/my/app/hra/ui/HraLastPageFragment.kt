package com.test.my.app.hra.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import com.test.my.app.common.base.BaseFragment
import com.test.my.app.common.base.BaseViewModel
import com.test.my.app.common.constants.CleverTapConstants
import com.test.my.app.common.constants.Constants
import com.test.my.app.common.constants.FirebaseConstants
import com.test.my.app.common.constants.NavigationConstants
import com.test.my.app.common.extension.openAnotherActivity
import com.test.my.app.common.utils.CleverTapHelper
import com.test.my.app.common.utils.FirebaseHelper
import com.test.my.app.common.utils.Utilities
import com.test.my.app.databinding.FragmentHraLastPageBinding
import com.test.my.app.hra.common.HraDataSingleton
import com.test.my.app.hra.viewmodel.HraViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HraLastPageFragment(val qCode: String) : BaseFragment() {

    private lateinit var binding: FragmentHraLastPageBinding
    private val viewModel: HraViewModel by lazy {
        ViewModelProvider(this)[HraViewModel::class.java]
    }

    private var viewPagerActivity: HraQuestionsActivity? = null
    private val hraDataSingleton = HraDataSingleton.getInstance()!!

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Callback to Handle back button event
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                viewPagerActivity!!.navigateToHomeScreen()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHraLastPageBinding.inflate(inflater, container, false)
        if (userVisibleHint) {
            initialise()
            setClickable()
        }
        FirebaseHelper.logCustomFirebaseEvent(FirebaseConstants.HRA_COMPLETED_EVENT)
        return binding.root
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser && isResumed) {
            initialise()
            setClickable()
        }
    }

    private fun initialise() {
        viewPagerActivity = (activity as HraQuestionsActivity)
        Utilities.printLogError("qCode----->$qCode")
        Utilities.printLogError("CurrentPageNo--->" + viewPagerActivity!!.getCurrentScreen())
        val prevAnsList =
            hraDataSingleton.getPrevAnsList(viewPagerActivity!!.getCurrentScreen() - 1)
        Utilities.printLogError("prevAnsList---> $prevAnsList")
    }

    private fun setClickable() {

        binding.btnViewReport.setOnClickListener {
            val data = HashMap<String, Any>()
            data[CleverTapConstants.FROM] = CleverTapConstants.HRA_SUBMIT
            CleverTapHelper.pushEventWithProperties(
                requireContext(),
                CleverTapConstants.HRA_SUMMARY_SCREEN,
                data
            )
            openAnotherActivity(destination = NavigationConstants.HRA_SUMMARY, clearTop = true) {
                putString(Constants.PERSON_ID, viewPagerActivity!!.personId)
            }
        }

    }

}