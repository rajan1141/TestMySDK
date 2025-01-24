package com.test.my.app.wyh.ira.ui

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.test.my.app.R
import com.test.my.app.common.base.BaseActivity
import com.test.my.app.common.base.BaseViewModel
import com.test.my.app.common.constants.Constants
import com.test.my.app.common.constants.NavigationConstants
import com.test.my.app.common.extension.openAnotherActivity
import com.test.my.app.common.utils.AppColorHelper
import com.test.my.app.common.utils.DefaultNotificationDialog
import com.test.my.app.common.utils.Utilities
import com.test.my.app.databinding.ActivityIraQuestionPagerBinding
import com.test.my.app.model.wyh.ira.GetIraAnswersModel
import com.test.my.app.repository.utils.Resource
import com.test.my.app.wyh.common.VerticalViewPager2Transformer
import com.test.my.app.wyh.ira.adapter.IraQuestionPagerAdapter
import com.test.my.app.wyh.ira.viewmodel.WyhIraViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class IraQuestionPagerActivity : BaseActivity() {

    private val appColorHelper = AppColorHelper.instance!!
    private lateinit var binding: ActivityIraQuestionPagerBinding
    private val wyhIraViewModel: WyhIraViewModel by lazy {
        ViewModelProvider(this)[WyhIraViewModel::class.java]
    }

    lateinit var viewPager: ViewPager2
    private var conversationId = ""
    val userAnswers = mutableMapOf<String,String>()
    private var questionPagerAdapter: IraQuestionPagerAdapter? = null
    var questions : MutableList<GetIraAnswersModel.Question> = mutableListOf()

    private val onBackPressedCallBack = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if ( getCurrentScreen() > 0 ) {
                val currentQuestionId = questions[getCurrentScreen()].questionId
                val currentFragment = getFragmentAtPosition( getCurrentScreen() ) as? IraQuestionFragment
                onQuestionAnswered(currentQuestionId!!,currentFragment!!.selectedAnswer)
                goToPrevious()
                //goToPrevious(questions[viewPager.currentItem].questionId!!)
            } else {
                routeToHomeScreen()
            }
        }
    }

    override fun getViewModel(): BaseViewModel = wyhIraViewModel

    override fun onCreateEvent(savedInstanceState: Bundle?) {
        binding = ActivityIraQuestionPagerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        try {
            onBackPressedDispatcher.addCallback(this,onBackPressedCallBack)
            if ( intent.hasExtra(Constants.CONVERSATION_ID)   ) {
                conversationId = intent.getStringExtra(Constants.CONVERSATION_ID)!!
            }
            Utilities.printLogError("conversationId--->$conversationId")
            setUpToolbar()
            initialise()
            setClickable()
            registerObserver()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initialise() {
        viewPager = binding.viewPager
        binding.indicatorProgress.visibility = View.GONE
        binding.btnSubmit.visibility = View.GONE
        wyhIraViewModel.callGetIraAnswersApi(conversationId)
        binding.indicatorProgress.setOnTouchListener { _: View?, _: MotionEvent? -> true }
    }

    private fun registerObserver() {

        wyhIraViewModel.getIraAnswers.observe(this) {
            if (it.status == Resource.Status.SUCCESS) {
                val listType = object : TypeToken<List<GetIraAnswersModel.Question>>() {}.type
                val questionsList : List<GetIraAnswersModel.Question> = Gson().fromJson(it.data!!.data!!.questionsList,listType)
                questions.clear()
                questions.addAll(questionsList)
                binding.indicatorProgress.visibility = View.VISIBLE
                setupQuestionsAdapter()
                //wyhHraViewModel.callCreateHraConversationApi()
            }
        }

        wyhIraViewModel.saveIraAnswers.observe(this) {
            if (it.status == Resource.Status.SUCCESS) {
                if ( it.data!!.success!! ) {
                    openAnotherActivity(destination = NavigationConstants.WYH_IRA_SUMMARY_ACTIVITY, clearTop = true) {
                        putString(Constants.CONVERSATION_ID,conversationId)
                    }
                }
            }
        }

        /*        wyhIraViewModel.iraCreateConversation.observe(this) {
                    if (it.status == Resource.Status.SUCCESS) {
                        setupQuestionsAdapter()
                    }
                }*/
    }

    private fun setupQuestionsAdapter() {
        Utilities.printData("questions",questions)
        questionPagerAdapter = IraQuestionPagerAdapter(questions,this)
        binding.viewPager.adapter = questionPagerAdapter
        //binding.viewPager.setScrollDuration(600)
        binding.viewPager.setPageTransformer(VerticalViewPager2Transformer())
        binding.viewPager.isUserInputEnabled = false
    }

    private fun setClickable() {
        binding.btnSubmit.setOnClickListener {
            submitAssessment()
        }
    }

    fun onQuestionAnswered( questionId:String,answer:String ) {
        val answeredQuesNo = questions.indexOf(questions.find { it.questionId == questionId })
        Utilities.printLogError("answeredQuesNo--->$answeredQuesNo")
        Utilities.printData("selectedAnswers",answer)
        if ( answer.isNotEmpty() ) {
            userAnswers[questionId] = answer
        }
        Utilities.printData("userAnswers",userAnswers)
    }

    fun goToPrevious() {
        if ( getCurrentScreen() > 0 ) {
            setCurrentScreen(getCurrentScreen() - 1)
            getCurrentScreen()
        }
        updateButtonStates(false)
    }

    fun goToNext() {
        if ( getCurrentScreen() < questions.size - 1) {
            setCurrentScreen(getCurrentScreen() + 1)
            updateButtonStates(false)
        } else {
            // Handle completion
            updateButtonStates(true)
        }
    }

    private fun submitAssessment() {
        Utilities.printData("FinalData",userAnswers)
        wyhIraViewModel.callSaveIraAnswersApi(conversationId,userAnswers,questions)
    }

    fun updateButtonStates(isEnable:Boolean) {
        if ( isEnable ) {
            Utilities.setProgressWithAnimation(binding.indicatorProgress,100,500)
            binding.btnSubmit.visibility = View.VISIBLE
        } else {
            binding.btnSubmit.visibility = View.GONE
        }
    }

    @SuppressLint("SetTextI18n")
    fun updateProgress(quesId : String) {
        val current = questions.indexOf(questions.find { it.questionId == quesId })
        val progress = (current * 100) / (questions.size)
        Utilities.printLogError("current--->$current")
        binding.txtProgress.text = "Question ${current+1}/${questions.size}"
        Utilities.setProgressWithAnimation(binding.indicatorProgress,progress,700)
    }

    private fun setCurrentScreen(i: Int) {
        binding.viewPager.setCurrentItem(i, true)
    }

    fun getCurrentScreen(): Int {
        return binding.viewPager.currentItem
    }

    fun getFragmentAtPosition(position: Int): Fragment? {
        val tag = "f${position}" // ViewPager2 tags fragments with "f<position>"
        return supportFragmentManager.findFragmentByTag(tag)
    }

    private fun routeToHomeScreen() {
        openAnotherActivity(destination = NavigationConstants.HOME, clearTop = true)
    }

    @SuppressLint("SetTextI18n")
    private fun setUpToolbar() {
        setSupportActionBar(binding.toolBarView.toolBarHra)
        binding.toolBarView.toolbarTitle.text = "Immunity Risk Assessment"
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
        binding.toolBarView.toolBarHra.navigationIcon?.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(appColorHelper.textColor, BlendModeCompat.SRC_ATOP)
        binding.toolBarView.imgCancel.visibility = View.VISIBLE
        binding.toolBarView.toolBarHra.setNavigationOnClickListener {
            onBackPressedCallBack.handleOnBackPressed()
        }
        binding.toolBarView.imgCancel.setOnClickListener {
            showExitAssessmentDialog()
        }
    }

    private fun showExitAssessmentDialog() {
        val dialogData = DefaultNotificationDialog.DialogData()
        dialogData.title = "Exit Assessment?"
        dialogData.message = "Are you sure that you want to exit? \n Your current assessment progress will be lost."
        dialogData.btnLeftName = "Cancel"
        dialogData.btnRightName = "Exit"
        val defaultNotificationDialog = DefaultNotificationDialog(this@IraQuestionPagerActivity,object: DefaultNotificationDialog.OnDialogValueListener {
            override fun onDialogClickListener(isButtonLeft: Boolean, isButtonRight: Boolean) {
                if (isButtonRight) {
                    routeToHomeScreen()
                }
            }
        },dialogData)
        defaultNotificationDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        defaultNotificationDialog.show()
    }

    /*    override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            enableEdgeToEdge()
            setContentView(R.layout.activity_ira_question_pager)
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
        }*/

}