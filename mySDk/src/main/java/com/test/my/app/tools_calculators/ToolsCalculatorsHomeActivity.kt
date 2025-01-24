package com.test.my.app.tools_calculators

import android.os.Bundle
import android.view.MenuItem
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.test.my.app.R
import com.test.my.app.common.base.BaseActivity
import com.test.my.app.common.base.BaseViewModel
import com.test.my.app.common.constants.Constants
import com.test.my.app.common.constants.FirebaseConstants
import com.test.my.app.common.constants.NavigationConstants
import com.test.my.app.common.extension.openAnotherActivity
import com.test.my.app.common.utils.AppColorHelper
import com.test.my.app.common.utils.FirebaseHelper
import com.test.my.app.databinding.ActivityToolsCalculatorsHomeBinding
import com.test.my.app.model.toolscalculators.UserInfoModel
import com.test.my.app.tools_calculators.viewmodel.ToolsCalculatorsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ToolsCalculatorsHomeActivity : BaseActivity() {

    private val viewModel: ToolsCalculatorsViewModel by lazy {
        ViewModelProvider(this)[ToolsCalculatorsViewModel::class.java]
    }
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private val appColorHelper = AppColorHelper.instance!!
    private lateinit var binding: ActivityToolsCalculatorsHomeBinding

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreateEvent(savedInstanceState: Bundle?) {
        //setContentView(R.layout.activity_tools_calculators_home)
        binding = ActivityToolsCalculatorsHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        UserInfoModel.getInstance()!!.isDataLoaded = false

        binding.toolBarView.toolbarTitle
        setSupportActionBar(binding.toolBarView.toolBarToolsCalculator)

        // Setting up a back button
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_tools_calculators) as NavHostFragment
        navController = navHostFragment.navController
        setupActionBarWithNavController(navController)

        val bundle = Bundle()
        if (intent.hasExtra(Constants.SCREEN)) {
            bundle.putString(Constants.SCREEN, intent.getStringExtra(Constants.SCREEN))
        }
        navController.setGraph(R.navigation.nav_graph_tools_calculators, bundle)

        navController.addOnDestinationChangedListener { controller, destination, _ ->
            binding.toolBarView.toolbarTitle.text = when (destination.id) {
                R.id.toolsCalculatorsDashboardFragment -> resources.getString(R.string.TITLE_TOOLS_CALCULATORS)
                R.id.toolsCalculatorsNavigationFragment -> resources.getString(R.string.TITLE_TOOLS_CALCULATORS)

                R.id.heartAgeFragment -> resources.getString(R.string.HEART_AGE_CALCULATOR)
                R.id.heartSummaryFragment -> resources.getString(R.string.HEART_AGE_CALCULATOR)
                R.id.heartReportFragment -> resources.getString(R.string.HEART_AGE_CALCULATOR)
                R.id.heartAgeRecalculateFragment -> resources.getString(R.string.HEART_AGE_CALCULATOR)

                R.id.diabetesCalculatorFragment -> resources.getString(R.string.DIABETES_CALCULATOR)
                R.id.diabetesSummaryFragment -> resources.getString(R.string.DIABETES_CALCULATOR)
                R.id.diabetesReportFragment -> resources.getString(R.string.DIABETES_CALCULATOR)

                R.id.hypertensionInputFragment -> resources.getString(R.string.HYPERTENSION_CALCULATOR)
                R.id.hypertensionSummeryFragment -> resources.getString(R.string.HYPERTENSION_CALCULATOR)
                R.id.hypertensionReportFragment -> resources.getString(R.string.HYPERTENSION_CALCULATOR)
                R.id.hypertensionRecalculateFragment -> resources.getString(R.string.HYPERTENSION_CALCULATOR)

                R.id.stressAndAnxietyInputFragment -> resources.getString(R.string.STRESS_ANXIETY_CALCULATOR)
                R.id.stressAndAnxietySummeryFragment -> resources.getString(R.string.STRESS_ANXIETY_CALCULATOR)

                R.id.smartPhoneInputFragment -> resources.getString(R.string.SMART_PHONE_CALCULATOR)
                R.id.smartPhoneAddictionSummaryFragment -> resources.getString(R.string.SMART_PHONE_CALCULATOR)

                R.id.dueDateInputFragment -> resources.getString(R.string.DUE_DATE_CALCULATOR)
                R.id.dueDateCalculatorReportFragment -> resources.getString(R.string.DUE_DATE_CALCULATOR)
                else -> resources.getString(R.string.TITLE_TOOLS_CALCULATORS)
            }
            if (destination.id == controller.graph.getStartDestination()) {
                supportActionBar!!.setDisplayShowTitleEnabled(false)
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
                supportActionBar?.setHomeButtonEnabled(true)
                supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
            } else {
                supportActionBar!!.setDisplayShowTitleEnabled(false)
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
                supportActionBar?.setHomeButtonEnabled(true)
                supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
            }

            /*            when(destination.id) {
                            R.id.heartAgeFragment,R.id.stressAndAnxietySummeryFragment,R.id.heartSummaryFragment,
                            R.id.heartAgeFragment,R.id.hypertensionInputFragment,R.id.diabetesCalculatorFragment -> {
                                toolBarToolsCalculator.setBackgroundColor(ContextCompat.getColor(this,R.color.white))
                            }
                            else -> {
                                toolBarToolsCalculator.setBackgroundColor(ContextCompat.getColor(this,R.color.white))
                            }
                        }*/

            binding.toolBarView.toolBarToolsCalculator.navigationIcon?.colorFilter =
                BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                    appColorHelper.textColor, BlendModeCompat.SRC_ATOP
                )
        }
        FirebaseHelper.logScreenEvent(FirebaseConstants.TOOLS_CALCULATORS_SCREEN)

    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            android.R.id.home -> {
                // API 5+ solution
                onBackPressed()
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }

    fun routeToHomeScreen() {
        openAnotherActivity(destination = NavigationConstants.HOME, clearTop = true)
    }

}
