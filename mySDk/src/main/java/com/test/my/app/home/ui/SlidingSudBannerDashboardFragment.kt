package com.test.my.app.home.ui

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.test.my.app.R
import com.test.my.app.common.constants.CleverTapConstants
import com.test.my.app.common.constants.Constants
import com.test.my.app.common.constants.NavigationConstants
import com.test.my.app.common.extension.changeColor
import com.test.my.app.common.extension.checkString
import com.test.my.app.common.extension.openAnotherActivity
import com.test.my.app.common.extension.setSpanString
import com.test.my.app.common.utils.CleverTapHelper
import com.test.my.app.common.utils.Utilities
import com.test.my.app.databinding.FragmentSlidingSudBannerDashboardBinding
import com.test.my.app.home.common.OnPolicyBannerListener
import com.test.my.app.home.viewmodel.DashboardViewModel
import com.test.my.app.model.sudLifePolicy.PolicyProductsModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SlidingSudBannerDashboardFragment : Fragment() {

    private lateinit var binding: FragmentSlidingSudBannerDashboardBinding
    private val viewModel: DashboardViewModel by lazy {
        ViewModelProvider(this)[DashboardViewModel::class.java]
    }
    private var banner = PolicyProductsModel.PolicyProducts()

    companion object {
        var position = 0
        var campaignDetailsList: MutableList<PolicyProductsModel.PolicyProducts> = mutableListOf()
        lateinit var listener: OnPolicyBannerListener

        fun newInstance(pos: Int,listener1:OnPolicyBannerListener,
                        list: MutableList<PolicyProductsModel.PolicyProducts>) : SlidingSudBannerDashboardFragment {
            val fragment = SlidingSudBannerDashboardFragment()
            position = pos
            campaignDetailsList = list
            listener = listener1
            val args = Bundle()
            args.putInt(Constants.POSITION,pos)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentSlidingSudBannerDashboardBinding.inflate(inflater, container, false)
        if ( campaignDetailsList.isNotEmpty() ) {
            initialise()
            setClickable()
        }
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    fun initialise() {
        banner = campaignDetailsList[position]
        Utilities.printData("banner", banner, true)
        if (!Utilities.isNullOrEmpty(banner.productImageURL)) {
            Utilities.loadImageUrl(banner.productImageURL, binding.imgBanner)
        }

        if ( !Utilities.isNullOrEmpty(banner.disclaimerText) ) {
            binding.disclaimerTV.text = banner.disclaimerText
        }

        if ( !Utilities.isNullOrEmpty(banner.disclaimerText) && !Utilities.isNullOrEmpty(banner.disclaimerUrl) ) {
            binding.disclaimerTV.text = "${banner.disclaimerText} Disclaimer"
            binding.disclaimerTV.setSpanString(
                binding.disclaimerTV.checkString(),
                banner.disclaimerText.length,
                showBold = true,
                isUnderlineText = true,
                color = R.color.blue
            ) {
                listener.onPolicyBannerClick(Constants.CLOSE_DIALOG)
                openAnotherActivity(destination = NavigationConstants.FEED_UPDATE_SCREEN) {
                    putString(Constants.TITLE, "Disclaimer")
                    putString(Constants.WEB_URL, banner.disclaimerUrl)
                }
            }
        }

        if (!Utilities.isNullOrEmpty(banner.productShareURL)) {
            binding.shareBanner.visibility = View.VISIBLE
        } else {
            binding.shareBanner.visibility = View.GONE
        }

        viewModel.addFeatureAccessLog.observe(viewLifecycleOwner) {}
    }

    fun setClickable() {

        binding.imgBanner.setOnClickListener {
            Utilities.showFullImageWithImgUrl(campaignDetailsList[position].productImageURL,requireContext(),true)
        }

        binding.disclaimerCB.setOnCheckedChangeListener { _, _ ->
            binding.clickHereToKnowTV.changeColor(binding.disclaimerCB.isChecked)
        }

        binding.clickHereToKnowTV.setOnClickListener {
            if (binding.disclaimerCB.isChecked) {
                viewModel.callAddFeatureAccessLogApi(
                    campaignDetailsList[position].productCode,
                    campaignDetailsList[position].productName,
                    "SudBanner",
                    campaignDetailsList[position].productRedirectionURL?:"")
                dashboardBannerClick()
                listener.onPolicyBannerClick(Constants.CLICK_TO_KNOW_MORE)
            } else {
                Utilities.toastMessageShort(requireContext(),resources.getString(R.string.ERROR_CONSENT))
            }
        }

        binding.shareBanner.setOnClickListener {
            Utilities.printData("PolicyProduct", banner, true)
            dashboardBannerShare()
            listener.onPolicyBannerClick(Constants.SHARE_BANNER)
        }

    }

    private fun dashboardBannerClick() {
        Utilities.printData("PolicyProduct", banner, true)
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
        data[CleverTapConstants.FROM] = "DASHBOARD"
        CleverTapHelper.pushEventWithProperties(requireContext(), CleverTapConstants.POLICY_BANNER, data)
        Utilities.printLogError("RedirectionUrl--->${url}")
        if (!Utilities.isNullOrEmpty(url)) {
            Utilities.redirectToChrome(url!!, requireContext())
        }
    }

    private fun dashboardBannerShare() {
/*        when (banner.productCode) {
            Constants.CENTURION -> CleverTapHelper.pushEvent(requireContext(), CleverTapConstants.SHARE_CENTURION_BANNER)
            Constants.SMART_HEALTH_PRODUCT -> CleverTapHelper.pushEvent(requireContext(), CleverTapConstants.SHARE_SMART_HEALTH_PRODUCT_BANNER)
        }*/
        if (!Utilities.isNullOrEmpty(banner.productShareURL)) {
            viewModel.shareBannerWithFriends(requireContext(), banner)
        }
    }

}