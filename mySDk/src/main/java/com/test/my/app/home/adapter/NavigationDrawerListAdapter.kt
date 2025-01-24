package com.test.my.app.home.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.core.content.ContextCompat
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.core.widget.ImageViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.test.my.app.R
import com.test.my.app.common.constants.Constants
import com.test.my.app.common.utils.AppColorHelper
import com.test.my.app.common.utils.DateHelper
import com.test.my.app.common.utils.DefaultNotificationDialog
import com.test.my.app.common.utils.Utilities
import com.test.my.app.databinding.ItemDrawerListBinding
import com.test.my.app.databinding.NavHeaderHomeMainBinding
import com.test.my.app.home.common.DataHandler
import com.test.my.app.home.common.DataHandler.NavDrawerOption
import com.test.my.app.home.ui.HomeMainActivity
import com.test.my.app.home.viewmodel.DashboardViewModel
import com.test.my.app.model.entity.UserRelatives

class NavigationDrawerListAdapter(
    val viewModel: DashboardViewModel,
    val homeMainActivity: HomeMainActivity,
    val context: Context
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var familyProfileSelected = ""
    var mIsSpinnerTouched: Boolean = false
    var isProfileUpdate: Boolean = false
    private val navDrawerList: MutableList<NavDrawerOption> = mutableListOf()
    private var userRelativesList: MutableList<UserRelatives> = mutableListOf()
//    private var drawerClickListener: DrawerClickListener = homeMainActivity
    private val appColorHelper = AppColorHelper.instance!!

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View
        when (viewType) {
            TYPE_HEADER -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.nav_header_home_main, parent, false)
                return HeaderViewHolder(view)
            }

            TYPE_ITEM -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_drawer_list, parent, false)
                return NavDrawerViewHolder(view)
            }
        }
        view = LayoutInflater.from(parent.context).inflate(R.layout.drawer_list_item, parent, false)
        return NavDrawerViewHolder(view)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is NavDrawerViewHolder) {
            val navDrawerItem = getNavDrawerItem(position)
            //holder.view.setBackgroundColor(navDrawerItem.color)
            holder.navIcon.setImageResource(navDrawerItem.imageId)
            holder.navTitle.text = navDrawerItem.title

            if (navDrawerItem.id != DataHandler.NavDrawer.LINK) {
                ImageViewCompat.setImageTintList(
                    holder.navIcon,
                    ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorAccent))
                )
            }

        } else if (holder is HeaderViewHolder) {
            holder.bindTo(viewModel)
        }
    }

    override fun getItemCount(): Int = navDrawerList.size + 1

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) TYPE_HEADER else TYPE_ITEM
    }

    private fun getNavDrawerItem(position: Int): NavDrawerOption {
        return navDrawerList[position - 1]
    }

    fun updateNavDrawerList(items: List<NavDrawerOption>) {
        navDrawerList.clear()
        navDrawerList.addAll(items)
    }

    companion object {
        const val TYPE_HEADER = 0
        const val TYPE_ITEM = 1
    }

    inner class NavDrawerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemDrawerListBinding.bind(view)
        var layoutDrawer = binding.layoutDrawerItem
        var view = binding.viewDrawer
        var navIcon = binding.imgDrawerItem
        var navTitle = binding.txtDrawerItemTitle

        init {
            layoutDrawer.setOnClickListener {
//                drawerClickListener.onDrawerClick(navDrawerList[adapterPosition - 1])
            }
        }
    }

    inner class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = NavHeaderHomeMainBinding.bind(view)

        @SuppressLint("ClickableViewAccessibility")
        fun bindTo(viewModel: DashboardViewModel) {

            if (Constants.strEnableFamilyProfile) {

                binding.llMainUser.visibility = View.GONE
//                binding.layoutSpinner.visibility = View.VISIBLE
                binding.spFamilyprofile.background.colorFilter =
                    BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                        appColorHelper.primaryColor(), BlendModeCompat.SRC_ATOP
                    )
                try {
                    viewModel.userRelativesList.observe(homeMainActivity) {
                        if (!isProfileUpdate) {
                            isProfileUpdate = true
                            if (it != null && it.isNotEmpty()) {
                                Utilities.printLog("RelativesList--->$it")
                                userRelativesList.clear()
                                userRelativesList.addAll(it)

                                try {
                                    userRelativesList = userRelativesList.filter {
                                        DateHelper.isDateAbove18Years(it.dateOfBirth)
                                    }.toMutableList()

                                    val familyProfileAdapter = FamilyProfileAdapter(
                                        viewModel,
                                        userRelativesList,
                                        binding.spFamilyprofile,
                                        context
                                    )
                                    binding.spFamilyprofile.adapter = familyProfileAdapter
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                                keepDefaultFamilyProfileSelected()
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                try {
                    binding.spFamilyprofile.setOnTouchListener { _, _ ->
                        mIsSpinnerTouched = true
                        false
                    }
                    binding.spFamilyprofile.onItemSelectedListener =
                        object : AdapterView.OnItemSelectedListener {

                            override fun onItemSelected(
                                adapterView: AdapterView<*>,
                                view: View,
                                position: Int,
                                l: Long
                            ) {
                                try {
                                    if (mIsSpinnerTouched) {
                                        mIsSpinnerTouched = false
                                        val familyProfileData =
                                            adapterView.selectedItem as UserRelatives
                                        if (familyProfileData != null) {
                                            val relativeID = familyProfileData.relativeID
                                            if (!Utilities.isNullOrEmpty(relativeID) && !relativeID.equals(
                                                    viewModel.personId,
                                                    ignoreCase = true
                                                )
                                            ) {
                                                changeFamilyProfile(familyProfileData, position)
                                            } else {
                                                keepDefaultFamilyProfileSelected()
                                            }
                                        }
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }

                            override fun onNothingSelected(adapterView: AdapterView<*>) {}
                        }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            } else {
                viewModel.getLoggedInPersonDetails()
//                binding.llMainUser.visibility = View.VISIBLE
                binding.layoutSpinner.visibility = View.GONE
                viewModel.userDetails.observe(homeMainActivity) {
                    if (it != null) {
                        if (it.gender.equals("1", ignoreCase = true)) {
                            binding.navigationUserImage.setImageResource(R.drawable.img_father)
                        } else if (it.gender.equals("2", ignoreCase = true)) {
                            binding.navigationUserImage.setImageResource(R.drawable.img_sister)
                        }
                        binding.navigationUserName.text = it.firstName
                        binding.navigationUserEmail.text = it.emailAddress
                    }
                }
            }

            binding.llMainUser.setOnClickListener {
                binding.spFamilyprofile.performClick()
            }

        }

        fun keepDefaultFamilyProfileSelected() {
            var strFamilyProfileSelected =
                getSelectedFamilyProfile(userRelativesList, viewModel.personId)
            if (Utilities.isNullOrEmpty(strFamilyProfileSelected.toString())) {
                strFamilyProfileSelected = 0
            }
            Utilities.printLog("strFamilyProfileSelected--->$strFamilyProfileSelected")
            if (!mIsSpinnerTouched) {
                binding.spFamilyprofile.setSelection(strFamilyProfileSelected)
            }
        }

        private fun getSelectedFamilyProfile(
            userRelativesList: MutableList<UserRelatives>,
            personId: String
        ): Int {
            Utilities.printLog("personId--->$personId")
            for ((index, value) in userRelativesList.withIndex()) {
                if (value.relativeID == personId) {
                    return index
                }
            }
            return 0
        }

        private fun changeFamilyProfile(userRelative: UserRelatives, position: Int) {
            val dialogData = DefaultNotificationDialog.DialogData()
            dialogData.title = context.resources.getString(R.string.SWITCH_PROFILE)
            dialogData.message =
                context.resources.getString(R.string.MSG_SWITCH_PROFILE_CONFIRMATION1) + " " + userRelative.firstName + "." + context.resources.getString(
                    R.string.MSG_SWITCH_PROFILE_CONFIRMATION2
                )
            dialogData.btnLeftName = context.resources.getString(R.string.NO)
            dialogData.btnRightName = context.resources.getString(R.string.CONFIRM)
            val defaultNotificationDialog =
                DefaultNotificationDialog(
                    homeMainActivity,
                    object : DefaultNotificationDialog.OnDialogValueListener {

                        override fun onDialogClickListener(
                            isButtonLeft: Boolean,
                            isButtonRight: Boolean
                        ) {
                            if (isButtonRight) {
                                viewModel.switchProfile(userRelative)
                                familyProfileSelected = position.toString()
//                                homeMainActivity.refreshView()
                            }
                            if (isButtonLeft) {
                                keepDefaultFamilyProfileSelected()
                            }
                        }
                    },
                    dialogData
                )
            defaultNotificationDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            defaultNotificationDialog.show()
        }

    }

    interface DrawerClickListener {
        fun onDrawerClick(item: NavDrawerOption)
    }
}