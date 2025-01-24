package com.test.my.app.home.adapter

import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.test.my.app.home.ui.sudLifePolicy.SlidingSudBannerFragment
import com.test.my.app.model.sudLifePolicy.PolicyProductsModel

class SlidingSudBannerAdapter(activity: FragmentActivity,
                              private val itemsCount: Int,
                              private val campaignDetailsList: MutableList<PolicyProductsModel.PolicyProducts>) : FragmentStateAdapter(activity) {

    override fun getItemCount() = itemsCount

    override fun createFragment(position: Int) = SlidingSudBannerFragment.newInstance(position,campaignDetailsList)

}