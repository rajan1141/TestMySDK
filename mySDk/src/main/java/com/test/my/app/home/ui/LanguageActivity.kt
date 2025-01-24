package com.test.my.app.home.ui

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.lifecycle.ViewModelProvider
import com.test.my.app.R
import com.test.my.app.common.base.BaseActivity
import com.test.my.app.common.base.BaseViewModel
import com.test.my.app.common.constants.Constants
import com.test.my.app.common.constants.NavigationConstants
import com.test.my.app.common.extension.openAnotherActivity
import com.test.my.app.common.utils.AppColorHelper
import com.test.my.app.common.utils.Utilities
import com.test.my.app.databinding.ActivityLanguageBinding
import com.test.my.app.home.adapter.LanguageAdapter
import com.test.my.app.home.viewmodel.BackgroundCallViewModel
import com.test.my.app.home.viewmodel.SettingsViewModel
import com.test.my.app.model.home.LanguageModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LanguageActivity : BaseActivity(), LanguageAdapter.OnItemClickListener {

    private val viewModel: SettingsViewModel by lazy {
        ViewModelProvider(this)[SettingsViewModel::class.java]
    }
    private val backGroundCallViewModel: BackgroundCallViewModel by lazy {
        ViewModelProvider(this)[BackgroundCallViewModel::class.java]
    }
    private lateinit var binding: ActivityLanguageBinding

    private val appColorHelper = AppColorHelper.instance!!

    private var from = ""
    private var languageAdapter: LanguageAdapter? = null
    private var languageDataSet: LanguageModel = LanguageModel("English", Constants.LANGUAGE_CODE_ENGLISH, R.drawable.img_english, R.color.color_english)

    override fun getViewModel(): BaseViewModel = viewModel

    private val onBackPressedCallBack = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            finish()
        }

    }


    override fun onCreateEvent(savedInstanceState: Bundle?) {
        binding = ActivityLanguageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        from = intent.getStringExtra(Constants.FROM)!!
        Utilities.printLogError("from----->$from")
        setupToolbar()
        initialise()
    }

    private fun initialise() {
        onBackPressedDispatcher.addCallback(this, onBackPressedCallBack)
        languageAdapter = LanguageAdapter(this, this)
        binding.rvLanguageList.adapter = languageAdapter
        languageAdapter!!.updateList(viewModel.getLanguageList(this))

        binding.btnSave.setOnClickListener {
            Utilities.changeLanguage(languageDataSet.languageCode, this@LanguageActivity)
            Utilities.logCleverTapChangeLanguage(languageDataSet.languageCode, this@LanguageActivity)
            //recreate()
            openAnotherActivity(destination = NavigationConstants.HOME ,clearTop = true)
        }
    }

    override fun onItemSelection(position: Int, data: LanguageModel) {
        Utilities.printLogError("LanguageCode---> " + data.language + " :: " + data.languageCode)
        languageDataSet = data
    }


    private fun setupToolbar() {
       //binding.backIV.setOnClickListener { finish() }
        setSupportActionBar(binding.toolBarView.toolbarCommon)
        //binding.toolBarView.toolbarTitle.text = resources.getString(R.string.SELECT_LANGUAGE)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)

        binding.toolBarView.toolbarCommon.navigationIcon?.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
            appColorHelper.textColor, BlendModeCompat.SRC_ATOP)

        binding.toolBarView.toolbarCommon.setNavigationOnClickListener {
            onBackPressed()
        }
    }



}