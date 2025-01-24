package com.test.my.app.wyh.hra.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.test.my.app.model.wyh.hra.GetHraAnswersModel.Question
import com.test.my.app.wyh.hra.ui.QuestionFragment

class QuestionPagerAdapter(private val questions: List<Question>, fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount() = questions.size

    override fun createFragment(position: Int): Fragment {
        return QuestionFragment.newInstance(questions[position].questionId!!)
    }
}