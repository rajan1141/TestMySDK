package com.test.my.app.wyh.ira.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.test.my.app.model.wyh.ira.GetIraAnswersModel
import com.test.my.app.wyh.ira.ui.IraQuestionFragment

class IraQuestionPagerAdapter(private val questions: List<GetIraAnswersModel.Question>,
                              fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount() = questions.size

    override fun createFragment(position: Int): Fragment {
        return IraQuestionFragment.newInstance(questions[position].questionId!!)
    }

}