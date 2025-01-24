package com.test.my.app.home.ui.sudLifePolicy

import android.os.Bundle
import android.view.MenuItem
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.test.my.app.R
import com.test.my.app.common.base.BaseActivity
import com.test.my.app.common.base.BaseViewModel
import com.test.my.app.common.constants.Constants
import com.test.my.app.common.constants.NavigationConstants
import com.test.my.app.common.extension.openAnotherActivity
import com.test.my.app.common.utils.AppColorHelper
import com.test.my.app.common.utils.Utilities
import com.test.my.app.databinding.ActivitySudLifePolicyBinding
import com.test.my.app.home.viewmodel.SudLifePolicyViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SudLifePolicyActivity : BaseActivity() {

    private lateinit var navController: NavController
    private val appColorHelper = AppColorHelper.instance!!
    private val viewModel: SudLifePolicyViewModel by lazy {
        ViewModelProvider(this)[SudLifePolicyViewModel::class.java]
    }
    private lateinit var binding: ActivitySudLifePolicyBinding

    private var onHelpClickListener: OnHelpClickListener? = null

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreateEvent(savedInstanceState: Bundle?) {
        binding = ActivitySudLifePolicyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        try {
            initialise()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun initialise() {

        setSupportActionBar(binding.toolBarView.toolbarCommon)
        // Setting up a back button
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_sud_life_policy) as NavHostFragment
        navController = navHostFragment.navController
        setupActionBarWithNavController(navController)

        val bundle = Bundle()
        if (intent.hasExtra(Constants.PHONE_NUMBER)) {
            bundle.putString(Constants.PHONE_NUMBER, intent.getStringExtra(Constants.PHONE_NUMBER))
            Utilities.printLog("PHONE_NUMBER--->" + intent.getStringExtra(Constants.PHONE_NUMBER))
        }

        if (intent.hasExtra(Constants.FROM) && !Utilities.isNullOrEmpty(
                intent.getStringExtra(
                    Constants.FROM
                )
            )
        ) {
            when (intent.getStringExtra(Constants.FROM)) {
                Constants.PROFILE -> bundle.putString(Constants.FROM, Constants.PROFILE)
            }
        }

        navController.setGraph(R.navigation.nav_graph_sud_life_policy, bundle)

       /* navController.addOnDestinationChangedListener { controller, destination, _ ->
            binding.toolBarView.toolbarTitle.text = when (destination.id) {
                R.id.sudLifePolicyDashboardFragment -> resources.getString(R.string.TITLE_POLICY_DASHBOARD)
                R.id.sudLifePolicyDetailsFragment -> resources.getString(R.string.TITLE_POLICY_DETAILS)
                R.id.viewReceiptFragment -> resources.getString(R.string.TITLE_PREMIUM_RECEIPT)
                else -> ""
            }

            binding.toolBarView.imgHelp.visibility = when (destination.id) {
                R.id.sudLifePolicyDashboardFragment -> View.VISIBLE
                else -> View.GONE
            }

            *//*            if (destination.id == controller.graph.startDestination) {
                            supportActionBar!!.setDisplayShowTitleEnabled(false)
                            supportActionBar?.setDisplayHomeAsUpEnabled(true)
                            supportActionBar?.setHomeButtonEnabled(true)
                            supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
                        } else {
                            supportActionBar!!.setDisplayShowTitleEnabled(false)
                            supportActionBar?.setDisplayHomeAsUpEnabled(true)
                            supportActionBar?.setHomeButtonEnabled(true)
                            supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
                        }*//*

            supportActionBar!!.setDisplayShowTitleEnabled(false)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setHomeButtonEnabled(true)
            supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)

            binding.toolBarView.toolbarCommon.navigationIcon?.colorFilter =
                BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                    appColorHelper.textColor, BlendModeCompat.SRC_ATOP
                )

            binding.toolBarView.imgHelp.setOnClickListener {
                if (onHelpClickListener != null) {
                    onHelpClickListener!!.onHelpClick()
                }
            }
        }*/
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        routeToHomeScreen()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                // API 5+ solution
                onBackPressed()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    fun routeToHomeScreen() {
        openAnotherActivity(destination = NavigationConstants.HOME, clearTop = true)
        finish()
    }

    interface OnHelpClickListener {
        fun onHelpClick()
    }

    fun setOnHelpClickListener(listener: OnHelpClickListener) {
        this.onHelpClickListener = listener
    }

}