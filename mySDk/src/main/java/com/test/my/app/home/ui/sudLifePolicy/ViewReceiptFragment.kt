package com.test.my.app.home.ui.sudLifePolicy

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.test.my.app.R
import com.test.my.app.common.base.BaseFragment
import com.test.my.app.common.base.BaseViewModel
import com.test.my.app.common.constants.CleverTapConstants
import com.test.my.app.common.utils.CleverTapHelper
import com.test.my.app.common.utils.DateHelper
import com.test.my.app.common.utils.PermissionUtil
import com.test.my.app.common.utils.Utilities
import com.test.my.app.databinding.FragmentViewReceiptBinding
import com.test.my.app.home.common.PolicyDataSingleton
import com.test.my.app.home.ui.HomeMainActivity
import com.test.my.app.home.viewmodel.SudLifePolicyViewModel
import com.test.my.app.repository.utils.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ViewReceiptFragment : BaseFragment() {

    private val viewModel: SudLifePolicyViewModel by lazy {
        ViewModelProvider(this)[SudLifePolicyViewModel::class.java]
    }
    private lateinit var binding: FragmentViewReceiptBinding

    private val permissionUtil = PermissionUtil
    private var policyDataSingleton: PolicyDataSingleton? = null

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentViewReceiptBinding.inflate(inflater, container, false)
        try {
            initialise()
            registerObserver()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as HomeMainActivity).setToolbarInfo(5, showAppLogo = false, title = resources.getString(R.string.TITLE_PREMIUM_RECEIPT))
    }
    private fun initialise() {
        CleverTapHelper.pushEvent(requireContext(), CleverTapConstants.POLICY_PREMIUM_RECEIPT_SCREEN)
        policyDataSingleton = PolicyDataSingleton.getInstance()!!
        viewModel.callSudReceiptDetails(policyDataSingleton!!.kypDetails.policyNumber!!)
        binding.txtPolicyName.text = policyDataSingleton!!.kypDetails.contractTypeDesc!!
        binding.txtPolicyStatus.text = policyDataSingleton!!.policyDetails.policyStatus
    }

    @SuppressLint("SetTextI18n")
    private fun registerObserver() {
/*        viewModel.sudReceiptDetails.observe(viewLifecycleOwner) {
            if (it.status == Resource.Status.SUCCESS) {
                if (!it.data!!.result.isNullOrEmpty()) {
                    val resp = it.data!!.result
                    binding.txtReceiptId.text = resp[0].receiptID
                    binding.txtDateTime.text =
                        DateHelper.formatDateValueInReadableFormat(resp[0].receiptDate!!)
                    if (!Utilities.isNullOrEmpty(resp[0].bnfyName)) {
                        binding.txtBnfyName.text = Utilities.convertStringToPascalCase(
                            resp[0].bnfyName!!.replace(".", "").replace(",", " , "))
                        //binding.txtBnfyName.text = resp[0].bnfyName!!.replace(".","")
                    }
                    binding.txtProductUinCode.text = resp[0].productUINCode!!.toString()
                    binding.txtApplicationNumber.text = resp[0].applicationNo
                    binding.txtCurrentBillChannelDesc.text = resp[0].currentBillChannelDesc
                    binding.txtBasePremiumCgstAmount.text = "${resources.getString(R.string.INDIAN_RUPEE)} ${Utilities.formatNumberDecimalWithComma(resp[0].basePremiumCGSTAmount!!)}"
                    binding.txtBasePremiumSgstAmount.text = "${resources.getString(R.string.INDIAN_RUPEE)} ${Utilities.formatNumberDecimalWithComma(resp[0].basePremiumSGSTAmount!!)}"
                    binding.txtBasePremiumIgstAmount.text = "${resources.getString(R.string.INDIAN_RUPEE)} ${Utilities.formatNumberDecimalWithComma(resp[0].basePremiumIGSTAmount!!)}"
                    binding.txtCovrSi.text = "${resources.getString(R.string.INDIAN_RUPEE)} ${Utilities.formatNumberDecimalWithComma(resp[0].covrSI!!)}"
                    binding.txtTotalPremium.text = "${resources.getString(R.string.INDIAN_RUPEE)} ${Utilities.formatNumberDecimalWithComma(resp[0].totPrem!!)}"
                    binding.txtContractTypeDesc.text = resp[0].contractTypeDesc!!.toString()
                    binding.txtCurrentBillFrequency.text = resp[0].currentBillFreqDesc
                    binding.txtCoverPremiumTerm.text = resp[0].covrPremTerm!!.toString()
                }
            }
        }*/

        viewModel.sudReceiptDetails.observe(viewLifecycleOwner) {
            if (it.status == Resource.Status.SUCCESS) {
                if ( it.data!!.result.status == "1" ) {
                    if (!it.data!!.result.instantIssuanceResponse.isNullOrEmpty()) {
                        val resp = it.data!!.result.instantIssuanceResponse
                        binding.txtReceiptId.text = resp[0].receiptID
                        binding.txtDateTime.text = DateHelper.formatDateValueInReadableFormat(resp[0].receiptDate!!)
                        if (!Utilities.isNullOrEmpty(resp[0].bnfyName)) {
                            binding.txtBnfyName.text = Utilities.convertStringToPascalCase(
                                resp[0].bnfyName!!.replace(".", "").replace(",", " , "))
                            //binding.txtBnfyName.text = resp[0].bnfyName!!.replace(".","")
                        }
                        binding.txtProductUinCode.text = resp[0].productUINCode!!.toString()
                        binding.txtApplicationNumber.text = resp[0].applicationNo
                        binding.txtCurrentBillChannelDesc.text = resp[0].currentBillChannelDesc
                        binding.txtBasePremiumCgstAmount.text = "${resources.getString(R.string.INDIAN_RUPEE)} ${Utilities.formatNumberDecimalWithComma(resp[0].basePremiumCGSTAmount!!.toDouble())}"
                        binding.txtBasePremiumSgstAmount.text = "${resources.getString(R.string.INDIAN_RUPEE)} ${Utilities.formatNumberDecimalWithComma(resp[0].basePremiumSGSTAmount!!.toDouble())}"
                        binding.txtBasePremiumIgstAmount.text = "${resources.getString(R.string.INDIAN_RUPEE)} ${Utilities.formatNumberDecimalWithComma(resp[0].basePremiumIGSTAmount!!.toDouble())}"
                        binding.txtCovrSi.text = "${resources.getString(R.string.INDIAN_RUPEE)} ${Utilities.formatNumberDecimalWithComma(resp[0].covrSI!!.toDouble())}"
                        binding.txtTotalPremium.text = "${resources.getString(R.string.INDIAN_RUPEE)} ${Utilities.formatNumberDecimalWithComma(resp[0].totPrem!!.toDouble())}"
                        binding.txtContractTypeDesc.text = resp[0].contractTypeDesc!!.toString()
                        binding.txtCurrentBillFrequency.text = resp[0].currentBillFreqDesc
                        binding.txtCoverPremiumTerm.text = resp[0].covrPremTerm!!.toString()
                    }
                }
            }
        }
    }

}