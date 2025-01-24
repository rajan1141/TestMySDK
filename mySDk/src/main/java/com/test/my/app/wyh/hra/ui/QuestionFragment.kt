package com.test.my.app.wyh.hra.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.lifecycle.ViewModelProvider
import com.test.my.app.common.base.BaseFragment
import com.test.my.app.common.base.BaseViewModel
import com.test.my.app.common.constants.Constants
import com.test.my.app.common.utils.Utilities
import com.test.my.app.databinding.FragmentQuestionBinding
import com.test.my.app.model.wyh.hra.GetHraAnswersModel.Question
import com.test.my.app.wyh.hra.viewmodel.WyhHraViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class QuestionFragment : BaseFragment() {

    private lateinit var binding: FragmentQuestionBinding
    private val wyhHraViewModel: WyhHraViewModel by lazy {
        ViewModelProvider(this)[WyhHraViewModel::class.java]
    }

    private var questionPagerActivity: QuestionPagerActivity? = null

    private var questionId : String = ""
    private lateinit var question : Question
    private var selectedAnswers = mutableListOf<String>()

    override fun getViewModel(): BaseViewModel = wyhHraViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentQuestionBinding.inflate(inflater, container, false)
        try {
            arguments?.let {
                questionId = it.getString(Constants.QUESTION_ID,"")
                Utilities.printLogError("questionId--->$questionId")
                /*            if (VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                //intent.getParcelableExtra("DATA", User::class.java)
                                question = it.getParcelable(Constants.QUESTION,Question::class.java)!!
                            } else {
                                question = it.getParcelable("key")!!
                            }*/
                if ( !Utilities.isNullOrEmpty(questionId) ) {
                    questionPagerActivity = (activity as QuestionPagerActivity)
                    question = questionPagerActivity!!.questions.find { ques -> ques.questionId == questionId }!!
                    //question = questionPagerActivity!!.questions.filter { it.questionId == questionId }.first() }
                    initialise()
                    setClickable()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return binding.root
    }

    private fun initialise() {
        selectedAnswers.clear()
        questionPagerActivity!!.updateProgress(questionId)
        binding.questionText.text = question.question

        // Dynamically create options based on the question type
        when (question.questionType) {
            "singleChoice" -> {
                val radioGroup = RadioGroup(context)
                question.options.forEach { option ->
                    val radioButton = RadioButton(context).apply {
                        text = option
                        setOnClickListener {
                            selectedAnswers.clear()
                            selectedAnswers.add(option)
                        }
                    }
                    radioGroup.addView(radioButton)
                }
                binding.optionsContainer.addView(radioGroup)
            }
            "multiSelect" -> {
                question.options.forEach { option ->
                    val checkBox = CheckBox(context).apply {
                        text = option
                        setOnCheckedChangeListener { _, isChecked ->
                            if (isChecked) selectedAnswers.add(option)
                            else selectedAnswers.remove(option)
                        }
                    }
                    binding.optionsContainer.addView(checkBox)
                }
            }
        }
    }

    private fun setClickable() {

        binding.previousButton.setOnClickListener {
            questionPagerActivity!!.onQuestionAnswered(question.questionId!!,selectedAnswers)
            questionPagerActivity!!.goToPrevious(questionId)
            updateButtonStates()
        }
        binding.nextButton.setOnClickListener {
            questionPagerActivity!!.onQuestionAnswered(question.questionId!!,selectedAnswers)
            questionPagerActivity!!.goToNext(questionId)
            updateButtonStates()
        }

    }

    private fun updateButtonStates() {
        binding.previousButton.isEnabled = questionPagerActivity!!.viewPager.currentItem > 0
        binding.nextButton.text = if (questionPagerActivity!!.viewPager.currentItem == questionPagerActivity!!.questions.size - 1) "Finish" else "Next"
    }

    fun getSelectedAnswers(): List<String> = selectedAnswers

    companion object {
        @JvmStatic
        fun newInstance(questionId:String) = QuestionFragment().apply {
            //question: Question
            Utilities.printLogError("questionIdInit--->$questionId")
                arguments = Bundle().apply {
                    putString(Constants.QUESTION_ID,questionId)
                    //putParcelable(Constants.QUESTION,question)
                }
            }
    }
}