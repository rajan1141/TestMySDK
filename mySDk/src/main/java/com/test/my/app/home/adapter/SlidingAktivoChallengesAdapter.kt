package com.test.my.app.home.adapter

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
//import com.aktivolabs.aktivocore.data.models.challenge.Challenge
import com.test.my.app.home.ui.aktivo.SlidingAktivoChallengesFragment

class SlidingAktivoChallengesAdapter(
    private val context: Context,
    activity: FragmentActivity,
    private val itemsCount: Int,
    /*private val challengesList: MutableList<Challenge>*/) : FragmentStateAdapter(activity) {

    override fun getItemCount() = itemsCount

    override fun createFragment(position: Int): Fragment  {
        /*= SlidingAktivoChallengesFragment.newInstance(position,challengesList)*/
        return Fragment()
    }

}