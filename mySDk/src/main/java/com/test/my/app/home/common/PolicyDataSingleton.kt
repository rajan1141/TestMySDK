package com.test.my.app.home.common

import com.test.my.app.common.utils.Utilities
import com.test.my.app.model.sudLifePolicy.PolicyProductsModel
import com.test.my.app.model.sudLifePolicy.SudFundDetailsModel.FundDetail
import com.test.my.app.model.sudLifePolicy.SudKYPModel.KYP
import com.test.my.app.model.sudLifePolicy.SudPolicyByMobileNumberModel
import com.test.my.app.model.sudLifePolicy.SudPolicyDetailsByPolicyNumberModel.PolicyDetails


class PolicyDataSingleton private constructor() {

    var productsList : MutableList<PolicyProductsModel.PolicyProducts> = mutableListOf()
    var completePolicyList : MutableList<SudPolicyByMobileNumberModel.Record> = mutableListOf()
    var policyList : MutableList<KYP> = mutableListOf()
    var kypDetails = KYP()
    var policyDetails = PolicyDetails()
    var fundDetails : MutableList<FundDetail> = mutableListOf()

    fun clearData() {
        instance = null
        productsList.clear()
        completePolicyList.clear()
        policyList.clear()
        kypDetails = KYP()
        policyDetails = PolicyDetails()
        fundDetails.clear()
        Utilities.printLogError("Cleared Policy Data")
    }

    fun clearPolicyDetails() {
        policyDetails = PolicyDetails()
        Utilities.printLogError("Cleared Policy Details")
    }
    fun clearFundDetails() {
        fundDetails.clear()
        Utilities.printLogError("Cleared Fund Details")
    }

    companion object {
        private var instance: PolicyDataSingleton? = null
        fun getInstance(): PolicyDataSingleton? {
            if (instance == null) {
                instance = PolicyDataSingleton()
            }
            return instance
        }
    }

}