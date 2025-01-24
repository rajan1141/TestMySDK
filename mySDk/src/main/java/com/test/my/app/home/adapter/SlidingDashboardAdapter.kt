package com.test.my.app.home.adapter

import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.test.my.app.common.utils.Utilities
import com.test.my.app.home.common.DataHandler
import com.test.my.app.home.ui.HomeScreenNewFragment
import com.test.my.app.home.ui.SlidingDashboardFragment

/*class SlidingDashboardAdapter(fragmentActivity: FragmentActivity,
    private val models: List<DataHandler.DashboardBannerModel>) : FragmentStateAdapter(fragmentActivity) {*/
class SlidingDashboardAdapter(fragmentActivity: FragmentActivity,
                              val fragment: HomeScreenNewFragment) : FragmentStateAdapter(fragmentActivity) {

    private var models: MutableList<DataHandler.DashboardBannerModel> = mutableListOf()

    override fun getItemCount() = models.size

    override fun createFragment(position: Int): Fragment {
        return SlidingDashboardFragment.newInstance(models[position])
    }

    fun updatePolicyBannerList() {
        val dataA = fragment.policyBannerList.map { DataHandler.DashboardBannerModel.TypePolicy(it) }
        val dataB = fragment.challengesList.distinct().map { DataHandler.DashboardBannerModel.TypeChallenge(it) }
        val final = dataA + dataB
        models.clear()
        models.addAll(final)
        Utilities.printData("CombinedBannerList",models)
        //fragment.binding.slidingViewPagerChallenges.removeAllViews()
        if ( models.isNotEmpty() ) {
            fragment.binding.layoutChallenges.visibility = View.VISIBLE
            if ( models.size > 1 ) {
                fragment.showSlidingDots(models)
            }
        } else {
            fragment.binding.layoutChallenges.visibility = View.GONE
        }
        notifyDataSetChanged()
    }

}