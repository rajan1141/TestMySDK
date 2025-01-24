package com.test.my.app.medication_tracker

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
import com.test.my.app.databinding.ActivityMedicationHomeBinding
import com.test.my.app.medication_tracker.common.MedicationSingleton
import com.test.my.app.medication_tracker.viewmodel.MedicineTrackerViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MedicationHomeActivity : BaseActivity() {

    private val viewModel: MedicineTrackerViewModel by lazy {
        ViewModelProvider(this)[MedicineTrackerViewModel::class.java]
    }
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private val appColorHelper = AppColorHelper.instance!!
    private lateinit var binding: ActivityMedicationHomeBinding

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreateEvent(savedInstanceState: Bundle?) {
        //setContentView(R.layout.activity_medication_home)
        binding = ActivityMedicationHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolBarView.toolBarMedication)
        // Setting up a back button
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_medication) as NavHostFragment
        navController = navHostFragment.navController
        setupActionBarWithNavController(navController)

        val bundle = Bundle()
        if (intent.hasExtra(Constants.FROM)) {
            when (intent.getStringExtra(Constants.FROM)) {

                Constants.NOTIFICATION_ACTION -> {
                    MedicationSingleton.getInstance()!!.setNotificationIntent(intent)
                    bundle.putString(Constants.FROM, Constants.NOTIFICATION_ACTION)
                    bundle.putString(Constants.DATE, intent.getStringExtra(Constants.DATE))
                }

                Constants.TRACK_PARAMETER -> {
                    bundle.putString(Constants.FROM, Constants.TRACK_PARAMETER)
                }

            }
        }
        navController.setGraph(R.navigation.medication_tracker_nav_graph, bundle)

        navController.addOnDestinationChangedListener { controller, destination, _ ->
            binding.toolBarView.toolbarTitle.text = when (destination.id) {
                R.id.medicineHome -> resources.getString(R.string.TITLE_MEDICINE_TRACKER)
                R.id.addMedicineFragment -> resources.getString(R.string.TITLE_ADD_MEDICATION)
                R.id.scheduleMedicineFragment -> resources.getString(R.string.TITLE_ADD_MEDICATION)
                //R.id.medicineDashboardFragment -> resources.getString(R.string.DASHBOARD)
                //R.id.myMedicationsFragment -> resources.getString(R.string.TITLE_MY_MEDICATIONS)
                else -> resources.getString(R.string.TITLE_MEDICINE_TRACKER)
            }

            /*            if (destination.id == R.id.medicineHome) {
                            toolBarView.visibility = View.GONE
                        } else {
                            toolBarView.visibility = View.VISIBLE
                        }*/

            //toolBarMedication.setBackgroundColor(ContextCompat.getColor(this@MedicationHomeActivity,R.color.white))

            /*            if (destination.id == R.id.addMedicineFragment) {
                            toolBarMedication.setBackgroundColor(ContextCompat.getColor(this@MedicationHomeActivity,R.color.background_color))
                        } else {
                            toolBarMedication.setBackgroundColor(ContextCompat.getColor(this@MedicationHomeActivity,R.color.white))
                        }*/

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
            binding.toolBarView.toolBarMedication.navigationIcon?.colorFilter =
                BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                    appColorHelper.textColor, BlendModeCompat.SRC_ATOP
                )

        }

        binding.toolBarView.imgVivantLogo.setOnClickListener {
            openAnotherActivity(destination = NavigationConstants.HOME, clearTop = true)
        }
        FirebaseHelper.logScreenEvent(FirebaseConstants.MEDICINE_TRACKER_SCREEN)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
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
