package com.test.my.app.wyh.hra.ui

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.test.my.app.R
import com.test.my.app.common.base.BaseActivity
import com.test.my.app.common.base.BaseViewModel
import com.test.my.app.common.constants.NavigationConstants
import com.test.my.app.common.extension.openAnotherActivity
import com.test.my.app.common.utils.AppColorHelper
import com.test.my.app.common.utils.Utilities
import com.test.my.app.databinding.ActivityQuestionPagerBinding
import com.test.my.app.model.wyh.hra.GetHraAnswersModel.Question
import com.test.my.app.repository.utils.Resource
import com.test.my.app.wyh.common.VerticalViewPager2Transformer
import com.test.my.app.wyh.hra.adapter.QuestionPagerAdapter
import com.test.my.app.wyh.hra.viewmodel.WyhHraViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class QuestionPagerActivity : BaseActivity() {

    private val appColorHelper = AppColorHelper.instance!!
    private lateinit var binding: ActivityQuestionPagerBinding
    private val wyhHraViewModel: WyhHraViewModel by lazy {
        ViewModelProvider(this)[WyhHraViewModel::class.java]
    }

    var questions : MutableList<Question> = mutableListOf(
        Question("1","singleChoice" ,"Do you smoke?", listOf("Yes","No"), listOf()),
        Question("2","multiSelect" ,"Do you suffer from?", listOf("Blood pressure","Diabetes","Cholesterol","None"), listOf()),
    )
    //private var selectedAnswers = mutableListOf<String>()

    lateinit var viewPager: ViewPager2
    private val userAnswers = mutableMapOf<String,List<String>>()
    private var questionPagerAdapter: QuestionPagerAdapter? = null

    private val onBackPressedCallBack = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            //finish()
            routeToHomeScreen()
        }
    }

    override fun getViewModel(): BaseViewModel = wyhHraViewModel

    override fun onCreateEvent(savedInstanceState: Bundle?) {
        binding = ActivityQuestionPagerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        try {
            onBackPressedDispatcher.addCallback(this, onBackPressedCallBack)
            setUpToolbar()
            initialise()
            setClickable()
            registerObserver()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun initialise() {
        wyhHraViewModel.callGetHraAnswersApi(this)
    }

    private fun registerObserver() {
        wyhHraViewModel.getHraAnswers.observe(this) {
            if (it.status == Resource.Status.SUCCESS) {
                val listType = object : TypeToken<List<Question>>() {}.type
                val questionsList : List<Question> = Gson().fromJson(it.data!!.data.questionsList,listType)
                questions.clear()
                questions.addAll(questionsList)
                questions = questions.filter { question ->
                    question.questionType != "date" && question.questionType != "weight"
                            && question.questionType != "height" && question.questionType != "waist"
                            && question.questionType != "water" && question.questionType != "sleep"
                }.toMutableList()
                setupQuestionsAdapter()
                //wyhHraViewModel.callCreateHraConversationApi()
            }
        }

/*        wyhHraViewModel.createHraConversation.observe(this) {
            if (it.status == Resource.Status.SUCCESS) {
                setupQuestionsAdapter()
            }
        }*/
    }

    fun setupQuestionsAdapter() {
        Utilities.printData("questions",questions)
        questionPagerAdapter = QuestionPagerAdapter(questions,this)
        binding.viewPager.setPageTransformer(VerticalViewPager2Transformer())
        binding.viewPager.adapter = questionPagerAdapter
    }

    private fun setClickable() {

    }

    fun onQuestionAnswered( questionId:String,answers:List<String> ) {
        userAnswers[questionId] = answers
        // Move to the next question or finish the assessment
/*        if (viewPager.currentItem + 1 < questions.size) {
            viewPager.currentItem += 1
        } else {
            // Handle completion
            //showResults()
        }*/
    }

    fun goToNext(questionId: String) {
        if (viewPager.currentItem < questions.size - 1) {
            viewPager.currentItem += 1
        } else {
            // Handle completion
            //showResults()
            //openAnotherActivity(destination = NavigationConstants.WYH_HRA_SUMMARY_ACTIVITY, clearTop = true)
            Utilities.printData("FinalData",userAnswers)
        }
        updateButtonStates()
    }

    fun goToPrevious(questionId: String) {
        if (viewPager.currentItem > 0) {
            viewPager.currentItem -= 1
        }
        updateButtonStates()
    }

    private fun updateButtonStates() {
        //binding.prevButton.isEnabled = viewPager.currentItem > 0
        //binding.nextButton.text = if (viewPager.currentItem == questions.size - 1) "Finish" else "Next"
    }

    private fun showResults() {
        //Toast.makeText(this, "Assessment Complete: $userAnswers", Toast.LENGTH_LONG).show()
        finish()
    }

    fun updateProgress(quesId : String) {
        val current = questions.indexOf(questions.find { it.questionId == quesId }) + 1
        val progress = (current * 100) / (questions.size)
        //binding.indicatorProgress.progress = progress
        Utilities.printLogError("current--->$current")
        binding.txtProgress.text = "$progress%"
    }

    private fun routeToHomeScreen() {
        openAnotherActivity(destination = NavigationConstants.HOME, clearTop = true)
    }

    private fun setUpToolbar() {
        setSupportActionBar(binding.toolBarView.toolBarHra)
        binding.toolBarView.toolbarTitle.text = "Health Risk Assessment"
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
        binding.toolBarView.toolBarHra.navigationIcon?.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(appColorHelper.textColor, BlendModeCompat.SRC_ATOP)

        binding.toolBarView.toolBarHra.setNavigationOnClickListener {
            onBackPressedCallBack.handleOnBackPressed()
        }
    }

/*    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_question_pager)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }*/
}