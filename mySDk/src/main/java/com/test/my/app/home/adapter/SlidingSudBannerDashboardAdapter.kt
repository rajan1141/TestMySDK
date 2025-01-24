package com.test.my.app.home.adapter

import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.test.my.app.home.common.OnPolicyBannerListener
import com.test.my.app.home.ui.SlidingSudBannerDashboardFragment
import com.test.my.app.model.sudLifePolicy.PolicyProductsModel

class SlidingSudBannerDashboardAdapter(activity: FragmentActivity,
                                       private val itemsCount: Int,
                                       private val campaignDetailsList: MutableList<PolicyProductsModel.PolicyProducts>,
                                       private val listener: OnPolicyBannerListener) : FragmentStateAdapter(activity) {

    override fun getItemCount() = itemsCount

    override fun createFragment(position: Int) = SlidingSudBannerDashboardFragment.newInstance(position,listener,campaignDetailsList)

}