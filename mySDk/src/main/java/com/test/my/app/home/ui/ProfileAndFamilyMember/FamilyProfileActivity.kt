package com.test.my.app.home.ui.ProfileAndFamilyMember

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MenuItem
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
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
import com.test.my.app.databinding.ActivityFamilyProfileBinding
import com.test.my.app.home.viewmodel.ProfileFamilyMemberViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FamilyProfileActivity : BaseActivity() {

    private lateinit var navController: NavController
    private val appColorHelper = AppColorHelper.instance!!
    private val viewModel: ProfileFamilyMemberViewModel by lazy {
        ViewModelProvider(this)[ProfileFamilyMemberViewModel::class.java]
    }
    private lateinit var binding: ActivityFamilyProfileBinding

    override fun getViewModel(): BaseViewModel = viewModel


    override fun onCreateEvent(savedInstanceState: Bundle?) {
        //binding = DataBindingUtil.setContentView(this, R.layout.activity_family_profile)
        binding = ActivityFamilyProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initialise()
        setClickable()
    }

    private fun setClickable() {

    }

    private fun initialise() {

        setSupportActionBar(binding.toolBarView.toolbarCommon)
        // Setting up a back button
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_family_profile) as NavHostFragment
        navController = navHostFragment.navController
        setupActionBarWithNavController(navController)

        val bundle = Bundle()
        if (intent.hasExtra(Constants.FROM) && intent.getStringExtra(Constants.FROM)
                .equals(Constants.HRA, ignoreCase = true)
        ) {
            bundle.putString(Constants.FROM, Constants.HRA)
        }

        if (intent.hasExtra(Constants.FROM) && intent.getStringExtra(Constants.FROM).equals(
                NavigationConstants.HOME, ignoreCase = true
            )
        ) {
            if (intent.hasExtra(Constants.DATA)) {
                Utilities.printLog("IntentDATA=> " + intent.getStringExtra(Constants.DATA))
            }
        }

        if (intent.hasExtra(Constants.SCREEN)) {
            bundle.putString(Constants.SCREEN, intent.getStringExtra(Constants.SCREEN))
        }

        navController.setGraph(R.navigation.nav_graph_family_profile, bundle)

        navController.addOnDestinationChangedListener { controller, destination, _ ->
            binding.toolBarView.toolbarTitle.text = when (destination.id) {
                //R.id.familyMembersListFragment2 -> resources.getString(R.string.TITLE_FAMILY_MEMBERS)
                //R.id.editFamilyMemberDetailsFragment -> resources.getString(R.string.TITLE_EDIT_DETAILS)
                R.id.selectRelationshipFragment2 -> resources.getString(R.string.TITLE_SELECT_RELATION)
                R.id.addFamilyMemberFragment2 -> resources.getString(R.string.TITLE_ADD_FAMILY_MEMBER)
                else -> ""
            }

            /*            if (destination.id == controller.graph.startDestination) {
                            supportActionBar!!.setDisplayShowTitleEnabled(false)
                            supportActionBar?.setDisplayHomeAsUpEnabled(true)
                            supportActionBar?.setHomeButtonEnabled(true)
                            supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
                        } else {
                            supportActionBar!!.setDisplayShowTitleEnabled(false)
                            supportActionBar?.setDisplayHomeAsUpEnabled(true)
                            supportActionBar?.setHomeButtonEnabled(true)
                            supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
                        }*/

            supportActionBar!!.setDisplayShowTitleEnabled(false)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setHomeButtonEnabled(true)
            supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)

            binding.toolBarView.toolbarCommon.navigationIcon?.colorFilter =
                BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                    appColorHelper.textColor, BlendModeCompat.SRC_ATOP
                )
        }
    }

    /*override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_family_profile)

        setSupportActionBar(toolbar_home)
        // Setting up a back button
        navController = nav_host_fragment_family_profile.findNavController()
        setupActionBarWithNavController(navController)

        val bundle = Bundle()
        if (intent.hasExtra(Constants.FROM) && intent.getStringExtra(Constants.FROM).equals(Constants.HRA, ignoreCase = true)) {
            bundle.putString(Constants.FROM, Constants.HRA)
        }
        navController.setGraph(R.navigation.nav_graph_family_profile, bundle)

        navController.addOnDestinationChangedListener { controller, destination, _ ->
            toolbar_title.text = when (destination.id) {
                R.id.familyMembersListFragment2 -> resources.getString(R.string.TITLE_FAMILY_MEMBERS)
                R.id.editFamilyMemberDetailsFragment2 -> resources.getString(R.string.TITLE_EDIT_DETAILS)
                R.id.selectRelationshipFragment2 -> resources.getString(R.string.TITLE_ADD_FAMILY_MEMBER)
                R.id.addFamilyMemberFragment2 -> resources.getString(R.string.TITLE_ADD_FAMILY_MEMBER)
                else -> ""
            }

//            if (destination.id == controller.graph.startDestination) {
//                supportActionBar!!.setDisplayShowTitleEnabled(false)
//                supportActionBar?.setDisplayHomeAsUpEnabled(true)
//                supportActionBar?.setHomeButtonEnabled(true)
//                supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
//            } else {
//                supportActionBar!!.setDisplayShowTitleEnabled(false)
//                supportActionBar?.setDisplayHomeAsUpEnabled(true)
//                supportActionBar?.setHomeButtonEnabled(true)
//                supportActionBar?.setHomeAsUpIndicator(R.drawable.back_arrow_white)
//            }

        }
        toolbar_home.navigationIcon?.colorFilter =
            BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                appColorHelper.primaryColor(), BlendModeCompat.SRC_ATOP
            )
        //NavigationUI.setupActionBarWithNavController(this, navController)
        img_vivant_logo.setOnClickListener {
            val intentToPass = Intent()
            intentToPass.component =
                ComponentName(NavigationConstants.APPID, NavigationConstants.HOME)
            intentToPass.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intentToPass)
        }
    }*/

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

    /*    override fun onSupportNavigateUp(): Boolean {
            return navController.navigateUp()
        }*/

    @SuppressLint("SetTextI18n")
    fun setToolbarTitle(name: String) {
        binding.toolBarView.toolbarTitle.text = resources.getString(R.string.ADD) + " " + name
    }

    override fun onSupportNavigateUp(): Boolean {
        return Navigation.findNavController(this, R.id.nav_host_fragment_family_profile)
            .navigateUp()
    }

}
