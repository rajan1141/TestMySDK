package com.test.my.app.home.ui

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.aktivolabs.aktivocore.data.models.challenge.Challenge
import com.test.my.app.R
import com.test.my.app.common.constants.CleverTapConstants
import com.test.my.app.common.constants.Constants
import com.test.my.app.common.constants.NavigationConstants
import com.test.my.app.common.extension.checkString
import com.test.my.app.common.extension.openAnotherActivity
import com.test.my.app.common.extension.setSpanString
import com.test.my.app.common.utils.CleverTapHelper
import com.test.my.app.common.utils.DateHelper
import com.test.my.app.common.utils.Utilities
import com.test.my.app.databinding.FragmentSlidingDashboardBinding
import com.test.my.app.home.common.DataHandler
import com.test.my.app.home.viewmodel.HomeViewModel
import com.test.my.app.model.sudLifePolicy.PolicyProductsModel
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SlidingDashboardFragment : Fragment() {

    private lateinit var binding: FragmentSlidingDashboardBinding
    private val viewModel: HomeViewModel by lazy {
        ViewModelProvider(this)[HomeViewModel::class.java]
    }
    private val dateHelper = DateHelper

    companion object {
        private const val ARG_MODEL = "arg_model"
        //var campaignDetailsList: DataHandler.DashboardBannerModel = DataHandler.DashboardBannerModel()
        @JvmStatic
        fun newInstance(model: DataHandler.DashboardBannerModel): SlidingDashboardFragment {
            val fragment = SlidingDashboardFragment()
            val args = Bundle().apply {
                //putSerializable(ARG_MODEL, model)
                putParcelable(ARG_MODEL, model)
            }
            //args.putParcelable(ARG_MODEL,model)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentSlidingDashboardBinding.inflate(inflater, container, false)
        initialise()
        return binding.root
    }

    @Suppress("DEPRECATION")
    @SuppressLint("SetTextI18n")
    fun initialise() {
        //val model = arguments?.getSerializable(ARG_MODEL) as? DataHandler.DashboardBannerModel
/*        val model = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getSerializable(ARG_MODEL,DataHandler.DashboardBannerModel::class.java)
        } else {
            arguments?.getSerializable(ARG_MODEL) as? DataHandler.DashboardBannerModel
        }*/

        val model = arguments?.getParcelable<DataHandler.DashboardBannerModel>(ARG_MODEL)

        Utilities.printData("model", model!!)
        when {
            model is DataHandler.DashboardBannerModel.TypePolicy -> {
                Utilities.printLogError("Type--->TypePolicy")
                //textView.text = "Type A: ${model.data.id}"
                binding.layoutPolicy.visibility = View.VISIBLE
                binding.layoutChallenge.visibility = View.GONE
                setPolicyBanner(model.data)
            }
            model is DataHandler.DashboardBannerModel.TypeChallenge -> {
                //textView.text = "Type B: ${model.data.description}"
                Utilities.printLogError("Type--->TypeChallenge")
                binding.layoutPolicy.visibility = View.GONE
                binding.layoutChallenge.visibility = View.VISIBLE
                setChallengeBanner(model.data)
            }
        }
/*        model?.let {
            view.findViewById<TextView>(R.id.textView).text = "Title: ${it.title}"
        }*/

/*        val model = arguments?.getParcelable<MyModel>(ARG_MODEL)
        model?.let {
            // Use the model (e.g., update UI)
            view.findViewById<TextView>(R.id.textView).text = "ID: ${it.id}, Name: ${it.name}"
        }*/
    }

    @SuppressLint("SetTextI18n")
    private fun setPolicyBanner(banner: PolicyProductsModel.PolicyProducts) {
        //banner = dashboardBannerList[SlidingSudBannerDashboardFragment.position]
        //Utilities.printData("banner", banner, true)
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
                color = R.color.blue) {
                openAnotherActivity(destination = NavigationConstants.FEED_UPDATE_SCREEN) {
                    putString(Constants.TITLE, "Disclaimer")
                    putString(Constants.WEB_URL, banner.disclaimerUrl)
                }
            }
        }

        binding.layoutBanner.setOnClickListener {
            if (binding.disclaimerCB.isChecked) {
/*                viewModel.callAddFeatureAccessLogApi(
                    campaignDetailsList[SlidingSudBannerDashboardFragment.position].productCode,
                    campaignDetailsList[SlidingSudBannerDashboardFragment.position].productName,
                    "SudBanner",
                    campaignDetailsList[SlidingSudBannerDashboardFragment.position].productRedirectionURL?:"")*/
                dashboardBannerClick(banner)
                //listener.onPolicyBannerClick(Constants.CLICK_TO_KNOW_MORE)
            } else {
                Utilities.toastMessageShort(requireContext(),resources.getString(R.string.ERROR_CONSENT))
            }
        }

        binding.disclaimerCB.setOnCheckedChangeListener { _, _ ->
            //binding.disclaimerTV.changeColor(binding.disclaimerCB.isChecked)
            if ( binding.disclaimerCB.isChecked ) {
                binding.disclaimerTV.setTextColor(ContextCompat.getColor(requireContext(),R.color.dark_gray))
            } else {
                binding.disclaimerTV.setTextColor(ContextCompat.getColor(requireContext(),R.color.placeholder))
            }
        }
    }

    private fun dashboardBannerClick(banner: PolicyProductsModel.PolicyProducts) {
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

    @SuppressLint("SetTextI18n")
    private fun setChallengeBanner(challenge:Challenge) {
        if (!Utilities.isNullOrEmpty(challenge.imageUrl)) {
            //Glide.with(mContext).load(challenge.imageUrl).apply(RequestOptions.circleCropTransform()).into(holder.imgChallenge)
            Picasso.get()
                .load(challenge.imageUrl)
                .placeholder(R.drawable.bg_disabled)
                //.resize(6000, 3000)
                //.onlyScaleDown()
                .error(R.drawable.bg_disabled)
                .into(binding.imgBanner)
        } else {
            binding.imgBanner.setImageResource(R.drawable.img_placeholder)
        }

        //binding.btnChallengeType.text = challenge.challengeType
        binding.txtChallengeTitle.text = challenge.title
        binding.txtChallengeDuration.text = "${dateHelper.convertDateSourceToDestination(challenge.startDate, dateHelper.SERVER_DATE_YYYYMMDD, dateHelper.DATEFORMAT_DDMMMYYYY_NEW)
        } - ${
            dateHelper.convertDateSourceToDestination(challenge.endDate, dateHelper.SERVER_DATE_YYYYMMDD, dateHelper.DATEFORMAT_DDMMMYYYY_NEW)
        }"

        //binding.txtDaysLeft.text = "${Days.daysBetween(LocalDate.now(),LocalDate.parse(challenge.endDate)).days} ${context.resources.getString(R.string.DAYS_LEFT)}"
        binding.txtParticipantsCount.text = challenge.numberOfParticipants.toString()
        if (challenge.enrolled) {
            binding.txtStatus.text =resources.getString(R.string.ENROLLED)
            binding.txtStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.state_success))
        } else {
            binding.txtStatus.text = resources.getString(R.string.ENROLL_NOW)
            binding.txtStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.textViewColor))
        }

        binding.layoutBanner.setOnClickListener {
            CleverTapHelper.pushEvent(requireContext(), CleverTapConstants.AKTIVO_CHALLENGES)
            openAnotherActivity(destination = NavigationConstants.AKTIVO_PERMISSION_SCREEN) {
                putString(Constants.CODE, Constants.AKTIVO_CHALLENGES_CODE)
                putString(Constants.CHALLENGE_ID, challenge.id)
            }
        }
    }

/*    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SlidingDashboardFragment().apply {
                arguments = Bundle().apply {
                    //putString(ARG_PARAM1, param1)
                    //putString(ARG_PARAM2, param2)
                }
            }
    }*/
}