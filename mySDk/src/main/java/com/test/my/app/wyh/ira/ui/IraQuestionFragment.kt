package com.test.my.app.wyh.ira.ui

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RadioButton
import androidx.lifecycle.ViewModelProvider
import com.test.my.app.R
import com.test.my.app.common.base.BaseFragment
import com.test.my.app.common.base.BaseViewModel
import com.test.my.app.common.constants.Constants
import com.test.my.app.common.utils.Utilities
import com.test.my.app.common.utils.ViewUtils
import com.test.my.app.databinding.FragmentIraQuestionBinding
import com.test.my.app.model.wyh.ira.GetIraAnswersModel
import com.test.my.app.wyh.ira.viewmodel.WyhIraViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class IraQuestionFragment : BaseFragment() {

    private lateinit var binding: FragmentIraQuestionBinding
    private val wyhIraViewModel: WyhIraViewModel by lazy {
        ViewModelProvider(this)[WyhIraViewModel::class.java]
    }

    private var questionPagerActivity: IraQuestionPagerActivity? = null
    private lateinit var question : GetIraAnswersModel.Question
    private var questionId : String = ""
    var selectedAnswer = ""

    override fun getViewModel(): BaseViewModel = wyhIraViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentIraQuestionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        try {
            questionPagerActivity = (activity as IraQuestionPagerActivity)
            arguments?.let {
                questionId = it.getString(Constants.QUESTION_ID,"")
                Utilities.printLogError("questionId--->$questionId")
                if ( !Utilities.isNullOrEmpty(questionId) ) {
                    question = questionPagerActivity!!.questions.find { ques -> ques.questionId == questionId }!!
                    initialise()
                    setClickable()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("InflateParams")
    private fun initialise() {
        if (questionPagerActivity!!.viewPager.currentItem > 0) {
            binding.layoutPrevious.visibility = View.VISIBLE
        } else {
            binding.layoutPrevious.visibility = View.INVISIBLE
        }

        questionPagerActivity!!.updateProgress(questionId)
        binding.questionText.text = question.question

        Utilities.printData("userAnswers",questionPagerActivity!!.userAnswers)
        // Dynamically create options based on the question type
        when (question.questionType) {
            "singleselectbutton" -> {
                try {
                    binding.rgIraSelection.removeAllViews()
                    val inflater = requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                    val par = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                    par.setMargins(10, 20, 10, 10)
                    for ( i in question.options.indices ) {
                        val radioButton = inflater.inflate(R.layout.item_radio_button,null) as RadioButton
                        val option = question.options[i]
                        var checked = false
                        for (( key,value ) in questionPagerActivity!!.userAnswers) {
                            if ( key == questionId && option == value) {
                                checked = true
                                if ( questionPagerActivity!!.viewPager.currentItem == questionPagerActivity!!.questions.size-1 ) {
                                    questionPagerActivity!!.updateButtonStates(true)
                                } else {
                                    questionPagerActivity!!.updateButtonStates(false)
                                }
                            }
                        }
                        radioButton.apply {
                            id = i
                            tag = option
                            text = option
                            textSize = 15f
                            isChecked = checked
                            layoutParams = par
                        }
                        Utilities.printLogError("Option ::$option")
                        radioButton.setOnClickListener(listener)
                        binding.rgIraSelection.addView(radioButton)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun setClickable() {

        binding.layoutPrevious.setOnClickListener {
            questionPagerActivity!!.onQuestionAnswered(question.questionId!!,selectedAnswer)
            questionPagerActivity!!.goToPrevious()
        }

    }

    var listener = View.OnClickListener { _ ->
        val ansCode = ViewUtils.getRadioSelectedValueTag(binding.rgIraSelection)
        selectedAnswer = ansCode
        questionPagerActivity!!.onQuestionAnswered(question.questionId!!,selectedAnswer)
        questionPagerActivity!!.goToNext()
    }

    companion object {
        @JvmStatic
        fun newInstance(questionId:String) = IraQuestionFragment().apply {
            //Utilities.printLogError("questionIdInit--->$questionId")
            arguments = Bundle().apply {
                putString(Constants.QUESTION_ID,questionId)
            }
        }
    }

}