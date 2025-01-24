package com.test.my.app.home.ui.sudLifePolicy

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.test.my.app.R
import com.test.my.app.common.constants.CleverTapConstants
import com.test.my.app.common.constants.Constants
import com.test.my.app.common.utils.CleverTapHelper
import com.test.my.app.common.utils.DefaultNotificationDialog
import com.test.my.app.common.utils.Utilities
import com.test.my.app.databinding.FragmentSlidingSudBannerBinding
import com.test.my.app.home.viewmodel.DashboardViewModel
import com.test.my.app.model.sudLifePolicy.PolicyProductsModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SlidingSudBannerFragment : Fragment() {

    private lateinit var binding: FragmentSlidingSudBannerBinding
    private val viewModel: DashboardViewModel by lazy {
        ViewModelProvider(this)[DashboardViewModel::class.java]
    }

    companion object {
        var position = 0
        var campaignDetailsList: MutableList<PolicyProductsModel.PolicyProducts> = mutableListOf()

        fun newInstance(pos: Int,list: MutableList<PolicyProductsModel.PolicyProducts>) : SlidingSudBannerFragment {
            val fragment = SlidingSudBannerFragment()
            position = pos
            campaignDetailsList = list
            val args = Bundle()
            args.putInt(Constants.POSITION,pos)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentSlidingSudBannerBinding.inflate(inflater, container, false)
        if ( campaignDetailsList.isNotEmpty() ) {
            initialise()
        }
        return binding.root
    }

    fun initialise() {
        if ( !Utilities.isNullOrEmpty(campaignDetailsList[position].productImageURL) ) {
            Utilities.loadImageUrl(campaignDetailsList[position].productImageURL,binding.slidingImage)
        }

        binding.slidingImage.setOnClickListener {
            showConsentDialog()
        }

        viewModel.addFeatureAccessLog.observe(viewLifecycleOwner) {}
    }

    private fun showConsentDialog() {
        val dialogData = DefaultNotificationDialog.DialogData()
        dialogData.message = resources.getString(R.string.POLICY_BANNER_CONSENT_MSG)
        dialogData.btnRightName = resources.getString(R.string.YES)
        dialogData.btnLeftName = resources.getString(R.string.NO)
        dialogData.showDismiss = false
        val defaultNotificationDialog = DefaultNotificationDialog(context,
            object : DefaultNotificationDialog.OnDialogValueListener {
                override fun onDialogClickListener(isButtonLeft: Boolean, isButtonRight: Boolean) {
                    if (isButtonRight) {
                        viewModel.callAddFeatureAccessLogApi(
                            campaignDetailsList[position].productCode,
                            campaignDetailsList[position].productName,
                            "SudBanner",
                            campaignDetailsList[position].productRedirectionURL?:"")
                        handleBannerClick(campaignDetailsList[position])
                    }
                }
            }, dialogData)
        defaultNotificationDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        defaultNotificationDialog.show()
    }

    private fun handleBannerClick(banner:PolicyProductsModel.PolicyProducts) {
        val url = banner.productRedirectionURL
        val data = HashMap<String, Any>()
        data[CleverTapConstants.PRODUCT_CODE] = banner.productCode
        data[CleverTapConstants.USER_TYPE] = Utilities.getEmployeeType()
        data[CleverTapConstants.PHONE] = Utilities.getUserPhoneNumber()
        when (Utilities.getEmployeeType()) {
            Constants.SUD_LIFE -> {
                data[CleverTapConstants.EMPLOYEE_ID] = Utilities.getEmployeeID()
            }
        }
        data[CleverTapConstants.FROM] = "POLICY_SECTION"
        CleverTapHelper.pushEventWithProperties(requireContext(), CleverTapConstants.POLICY_BANNER, data)
        Utilities.printLogError("RedirectionUrl--->${url}")
        if (!Utilities.isNullOrEmpty(url)) {
            Utilities.redirectToChrome(url!!, requireContext())
        }
    }

/*    private fun getEventByBannerCode(bannerCode:String) : String {
        var event = ""
        when(bannerCode) {
            Constants.CENTURY_ROYALE -> event = CleverTapConstants.POLICY_CENTURY_ROYALE_BANNER
            Constants.CENTURY_GOLD -> event = CleverTapConstants.POLICY_CENTURY_GOLD_BANNER
            Constants.PROTECT_SHIELD_PLUS -> event = CleverTapConstants.POLICY_PROTECT_SHIELD_PLUS_BANNER
        }
        return event
    }*/

}
