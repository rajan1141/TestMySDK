package com.test.my.app.home.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.test.my.app.R
import com.test.my.app.common.base.BaseFragment
import com.test.my.app.common.base.BaseViewModel
import com.test.my.app.common.constants.Constants
import com.test.my.app.common.constants.NavigationConstants
import com.test.my.app.common.extension.openAnotherActivity
import com.test.my.app.common.utils.DateHelper
import com.test.my.app.common.utils.DefaultNotificationDialog
import com.test.my.app.common.utils.FileUtils
import com.test.my.app.common.utils.PermissionUtil
import com.test.my.app.common.utils.UserSingleton
import com.test.my.app.common.utils.Utilities
import com.test.my.app.databinding.DialogEditProfileBinding
import com.test.my.app.databinding.DialogSwitchProfileBinding
import com.test.my.app.databinding.FragmentProfileHomeBinding
import com.test.my.app.home.adapter.RvpFamilyMemberListAdapter
import com.test.my.app.home.adapter.SwitchProfileAdapter
import com.test.my.app.home.ui.ProfileAndFamilyMember.EditProfileActivity
import com.test.my.app.home.viewmodel.BackgroundCallViewModel
import com.test.my.app.home.viewmodel.DashboardViewModel
import com.test.my.app.home.viewmodel.ProfileFamilyMemberViewModel
import com.test.my.app.model.entity.UserRelatives
import com.test.my.app.model.home.Contact
import com.test.my.app.model.home.Person
import com.test.my.app.repository.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import jp.wasabeef.blurry.Blurry
import java.io.File

@AndroidEntryPoint
class FragmentProfile : BaseFragment() {

    lateinit var binding: FragmentProfileHomeBinding

    private val viewModel: DashboardViewModel by lazy {
        ViewModelProvider(this)[DashboardViewModel::class.java]
    }
    private val viewModelProfile: ProfileFamilyMemberViewModel by lazy {
        ViewModelProvider(this)[ProfileFamilyMemberViewModel::class.java]
    }
    private val backGroundCallViewModel: BackgroundCallViewModel by lazy {
        ViewModelProvider(this)[BackgroundCallViewModel::class.java]
    }

    private val permissionUtil = PermissionUtil
    private var strAgeGender = ""
    private var hasProfileImage = false
    private var completeFilePath = ""
    private var needToSet = true
    private var userGender = ""
    private val fileUtils = FileUtils
    private var familyMembersAdapter: RvpFamilyMemberListAdapter? = null
    private var familyList: MutableList<UserRelatives> = mutableListOf()

    //private val resultLauncher = registerForActivityResult<Intent,ActivityResult>(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
    private val editProfileLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        Utilities.printData("editProfileLauncher",result)
        if (result.resultCode == Activity.RESULT_OK) {
            // Call your refresh method
            initialise()
        }
    }

    override fun getViewModel(): BaseViewModel = viewModelProfile

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentProfileHomeBinding.inflate(inflater, container, false)
        initialise()
        setObserver()
        setClickable()
        setReferSection()
        //viewModelProfile.callListRelativesApi(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as HomeMainActivity).setToolbarInfo(4, true, showToolBar = false, title = resources.getString(R.string.PROFILE))
    }

    override fun onResume() {
        super.onResume()
        requireActivity().window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        Utilities.printLogError("Inside_Profile_onResume=")
    }

    private fun initialise() {
        binding.rvFamilyMember.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        familyMembersAdapter = RvpFamilyMemberListAdapter(object : RvpFamilyMemberListAdapter.OnItemClickListener {
            override fun onItemClick(position: Int, item: UserRelatives) {
                if (item.relationshipCode == "ADD") {
                    openAnotherActivity(destination = NavigationConstants.FAMILY_PROFILE)
                } else if (item.relationshipCode != "SELF" && item.relativeID != viewModelProfile.personId) {
                    showEditProfileDialog(position, item)
                } else if (item.relationshipCode == "SELF" && item.relativeID != viewModelProfile.personId) {
                    changeFamilyProfile(item)
                }
            } },requireContext(),viewModelProfile)
        binding.rvFamilyMember.adapter = familyMembersAdapter

        val profPicBitmap = UserSingleton.getInstance()!!.profPicBitmap
        if (viewModelProfile.personId == viewModelProfile.adminPersonId && profPicBitmap != null) {
            binding.imgUserPic.setImageBitmap(profPicBitmap)
            binding.imgUserPicBanner.setImageBitmap(profPicBitmap)
            blurBanner(profPicBitmap)
        } else if (!Utilities.isNullOrEmpty(userGender)) {
            binding.imgUserPic.setImageResource(Utilities.getRelativeImgIdWithGender(viewModelProfile.relationshipCode, userGender))
            binding.imgUserPicBanner.setImageResource(R.drawable.btn_fill_round)
        }
        startImageShimmer()
        //viewModelProfile.getAllUserRelatives()
        viewModelProfile.callGetUserDetailsApi()
    }

    private fun setObserver() {

        viewModelProfile.userProfileDetails.observe(viewLifecycleOwner) {
            if (it.status == Resource.Status.SUCCESS) {
                if (!Utilities.isNullOrEmptyOrZero(it.data!!.person.profileImageID.toString())) {
                    hasProfileImage = true
                    viewModelProfile.callGetProfileImageApiMain(this@FragmentProfile, it.data.person.profileImageID.toString())
                } else {
                    stopImageShimmer()
                    binding.imgUserPic.setImageResource(Utilities.getRelativeImgIdWithGender("", it.data.person.gender.toString()))
                    if (it.data.person.id.toString() != viewModelProfile.adminPersonId) {
                        binding.imgUserPic.setImageResource(Utilities.getRelativeImgIdWithGender(viewModelProfile.relationshipCode, viewModelProfile.gender))
                    }
                    binding.imgUserPicBanner.setImageResource(R.drawable.btn_fill_round)
                }
                setUserDetails(it.data.person)
            }
        }

/*        viewModelProfile.userProfileDetails.observe(viewLifecycleOwner) {
            if (it.status == Resource.Status.SUCCESS) {
                //val person = it.data!!.person
                if (!Utilities.isNullOrEmptyOrZero(it.data!!.person.profileImageID.toString())) {
                    hasProfileImage = true
                    val permissionResult = permissionUtil.checkStoragePermission(object : PermissionUtil.AppPermissionListener {
                        override fun isPermissionGranted(isGranted: Boolean) {
                            Utilities.printLogError("$isGranted")
                            if (isGranted) {
                                viewModelProfile.callGetProfileImageApiMain(this@FragmentProfile, it.data.person.profileImageID.toString())
                            } else {
                                stopImageShimmer()
                                binding.imgUserPic.setImageResource(Utilities.getRelativeImgIdWithGender("", it.data.person.gender.toString()))
                                binding.imgUserPicBanner.setImageResource(R.drawable.btn_fill_round)
                            }
                        }
                    }, requireContext())
                    if (permissionResult) {
                        viewModelProfile.callGetProfileImageApiMain(this@FragmentProfile, it.data.person.profileImageID.toString())
                    }
                } else {
                    stopImageShimmer()
                    binding.imgUserPic.setImageResource(Utilities.getRelativeImgIdWithGender("", it.data.person.gender.toString()))
                    if (it.data.person.id.toString() != viewModelProfile.adminPersonId) {
                        binding.imgUserPic.setImageResource(Utilities.getRelativeImgIdWithGender(viewModelProfile.relationshipCode, viewModelProfile.gender))
                    }
                    binding.imgUserPicBanner.setImageResource(R.drawable.btn_fill_round)
                }
                setUserDetails(it.data.person)
            }
        }*/

        viewModelProfile.profileImage.observe(viewLifecycleOwner) {
            if (it.status == Resource.Status.SUCCESS) {
                val document = it.data!!.healthRelatedDocument
                val fileName = document.fileName
                val fileBytes = document.fileBytes
                try {
                    val path = Utilities.getAppFolderLocation(requireContext())
                    if (!File(path, fileName).exists()) {
                        if (!Utilities.isNullOrEmpty(fileBytes)) {
                            val decodedImage = fileUtils.convertBase64ToBitmap(fileBytes)
                            if (decodedImage != null) {
                                val saveRecordUri = fileUtils.saveBitmapToExternalStorage(requireContext(), decodedImage, fileName)
                                if (saveRecordUri != null) {
                                    viewModelProfile.updateUserProfileImgPath(fileName, path)
                                }
                                UserSingleton.getInstance()!!.profPicBitmap = decodedImage
                            }
                        }
                    } else {
                        viewModelProfile.updateUserProfileImgPath(fileName, path)
                    }
                    completeFilePath = "$path/$fileName"
                    setProfilePic()
                    stopImageShimmer()
                    familyMembersAdapter!!.notifyDataSetChanged()
                } catch (e: Exception) {
                    e.printStackTrace()
                    stopImageShimmer()
                }
            }
        }

        viewModelProfile.removeRelative.observe(viewLifecycleOwner) {
            if (it.status == Resource.Status.SUCCESS) {
                viewModelProfile.getAllUserRelatives()
            }
        }

        /*        viewModelProfile.listRelatives.observe(viewLifecycleOwner) {
                    Utilities.printLog("RelativeData--->$it")
                    if (it != null && !it.data!!.relativeList.isNullOrEmpty()) {
                        familyList.clear()
                        familyList.addAll(it.data!!.relativeList.toMutableList())
                        familyMembersAdapter!!.updateRelationshipCode(viewModel.relationshipCode)
                    }
                }*/

        viewModelProfile.userRelativesList.observe(viewLifecycleOwner) {
            it?.let {
                if (familyMembersAdapter != null) {
                    familyMembersAdapter!!.updateFamilyMembersList(it)
                }
            }

        }

    }

    private fun setClickable() {

        binding.imgEditProfile.setOnClickListener {
            navigateToEditProfile()
        }

        binding.imgUserPic.setOnClickListener {
            //navigateToEditProfile()
            viewProfilePhoto()
        }

/*        binding.imgEditPic.setOnClickListener {
            navigateToEditProfile()
        }*/

/*        binding.imgSwitchProfile.setOnClickListener {
            Utilities.printLog("SwitchProfile=> ")
            showSwitchProfileDialog(familyList.toMutableList())
        }*/
    }

    private fun navigateToEditProfile() {
        if (viewModel.isSelfUser()) {
            //viewModelProfile.navigate(FragmentHomeMainDirections.actionDashboardFragmentToEditProfileActivity())
            //openAnotherActivity(destination = NavigationConstants.EDIT_PROFILE_ACTIVITY)
            val intent = Intent(activity,EditProfileActivity::class.java)
            //intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            editProfileLauncher.launch(intent)
        } else {
            val relative = getRelativeObject(viewModel.personId)
            navigateToEditFamilyProfileActivity(
                relative.relativeID,
                relative.relationShipID,
                relative.relationshipCode,
                relative.relationship)
        }
    }

    private fun setUserDetails(user: Person) {
        try {
            if (!Utilities.isNullOrEmptyOrZero(user.id.toString())) {
                val firstName = user.firstName
                val email = user.contact.emailAddress
                var dateOfBirth = user.dateOfBirth
                //val age = user.age
                val gender = user.gender

                if (!Utilities.isNullOrEmpty(firstName)) {
                    binding.txtName.text = firstName
                }

                if (!Utilities.isNullOrEmpty(email)) {
                    binding.txtEmail.text = email
                }

                if (!dateOfBirth.equals("", ignoreCase = true)) {
                    dateOfBirth =
                        DateHelper.formatDateValue(DateHelper.DISPLAY_DATE_DDMMMYYYY, dateOfBirth)!!
                    Utilities.printLogError("DOB--->$dateOfBirth")
                    if (!Utilities.isNullOrEmpty(dateOfBirth)) {
                        val calculatedAge: String =
                            DateHelper.calculatePersonAge(dateOfBirth, requireContext())
                        Utilities.printLogError("calculatedAge--->$calculatedAge")
                        strAgeGender = calculatedAge/*                        strAgeGender = if (!Utilities.isNullOrEmptyOrZero(age.toString())) {
                                                    "$age Yrs"
                                                } else {
                                                    calculatedAge
                                                }*/
                        binding.txtDOB.text = DateHelper.formatDateValue(
                            DateHelper.DATEFORMAT_DDMMMYYYY_NEW, dateOfBirth
                        )!!
                        binding.txtAge.text = strAgeGender
                    }
                } else {
                    binding.txtDOB.text = " -- "
                    binding.txtAge.text = " -- "
                }

                when (gender) {
                    1 -> {
                        binding.txtGender.text = resources.getString(R.string.MALE)
                    }

                    2 -> {
                        binding.txtGender.text = resources.getString(R.string.FEMALE)
                    }
                }
                userGender = gender.toString()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setProfilePic() {
        try {
            Utilities.printLog("completeFilePath----->$completeFilePath")
            Utilities.printLog("needToSet----->$needToSet")
            if (!Utilities.isNullOrEmpty(completeFilePath)) {
                val bitmap = BitmapFactory.decodeFile(completeFilePath)
                if (bitmap != null) {
                    binding.imgUserPic.setImageBitmap(bitmap)
                    binding.imgUserPicBanner.setImageBitmap(bitmap)
                    blurBanner(bitmap)
                    //profPicBitmap = bitmap
                    UserSingleton.getInstance()!!.profPicBitmap = bitmap
                    //needToSet = false
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun startImageShimmer() {
        binding.layoutImgShimmer.startShimmer()
        binding.layoutImgShimmer.visibility = View.VISIBLE
        binding.layoutUserPic.visibility = View.GONE
        binding.imgUserPicBanner.setImageResource(R.drawable.btn_fill_round)
    }

    fun stopImageShimmer() {
        binding.layoutImgShimmer.stopShimmer()
        binding.layoutImgShimmer.visibility = View.GONE
        binding.layoutUserPic.visibility = View.VISIBLE
    }

    fun blurBanner(bitmap: Bitmap) {
        Blurry.with(context).from(bitmap).into(binding.imgUserPicBanner)
    }

    private fun setReferSection() {
        viewModel.generateReferLink()
        var referLink = ""
        viewModel.referLink.observe(viewLifecycleOwner) {
            if (!Utilities.isNullOrEmpty(it)) {
                referLink = it
                binding.imgQrCode.setImageBitmap(Utilities.generateQrCode(referLink))
                binding.layoutRefer.visibility = View.VISIBLE
            }
        }

        binding.btnInvite.setOnClickListener {
            viewModel.shareAppReferralMessage(requireContext(),referLink)
        }

        binding.imgInvite.setOnClickListener {
            viewModel.shareAppReferralMessage(requireContext(),referLink)
        }
    }

    private fun navigateToEditFamilyProfileActivity(relativeId: String, relationShipId: String, relationCode: String, relation: String) {
        openAnotherActivity(destination = NavigationConstants.WELLFIE_SCREEN) {
            putString(Constants.RELATIVE_ID, relativeId)
            putString(Constants.RELATION_SHIP_ID, relationShipId)
            putString(Constants.RELATION_CODE, relationCode)
            putString(Constants.RELATION, relation)
        }
    }

    private fun getRelativeObject(personId: String): UserRelatives {
        for (item in familyList) {
            if (item.relativeID.equals(personId, true)) {
                return item
            }
        }
        return UserRelatives()
    }

    @SuppressLint("SetTextI18n")
    private fun showEditProfileDialog(position: Int, item: UserRelatives) {
        try {
            val dialog = Dialog(requireContext())
            val dialogBinding = DialogEditProfileBinding.inflate(layoutInflater)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(dialogBinding.root)
            //dialog.setContentView(R.layout.dialog_edit_profile)
            dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            val relativeImgId = Utilities.getRelativeImgIdWithGender(item.relationshipCode, item.gender)
            dialogBinding.imgUser.setImageResource(relativeImgId)
            dialogBinding.txtName.text = item.firstName
            dialogBinding.txtDOB.text = item.dateOfBirth
            dialogBinding.txtEmail.text = item.emailAddress
            dialogBinding.txtRelation.text = item.relationship
            dialogBinding.txtPhone.text = item.contactNo
            dialogBinding.dialogBtnSwitch.text =
                "${resources.getString(R.string.SWITCH_TO)} ${item.firstName}"

            if (DateHelper.isDateAbove18Years(item.dateOfBirth)) {
                dialogBinding.dialogBtnSwitch.visibility = View.VISIBLE
            } else {
                dialogBinding.dialogBtnSwitch.visibility = View.GONE
            }

            dialog.show()

            dialogBinding.imgClose.setOnClickListener {
                dialog.dismiss()
            }

            dialogBinding.dialogBtnEdit.setOnClickListener {
                dialog.dismiss()
                navigateToEditFamilyProfileActivity(item.relativeID, item.relationShipID, item.relationshipCode, item.relationship)
            }

            dialogBinding.dialogBtnDelete.setOnClickListener {
                dialog.dismiss()
                deleteFamilyMember(item)
            }

            dialogBinding.dialogBtnSwitch.setOnClickListener {
                dialog.dismiss()
                changeFamilyProfile(item)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showSwitchProfileDialog(list: MutableList<UserRelatives>) {
        try {
            var relative = UserRelatives()
            val dialog = Dialog(requireContext())
            val dialogBinding = DialogSwitchProfileBinding.inflate(layoutInflater)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(dialogBinding.root)
            //dialog.setContentView(R.layout.dialog_switch_profile)
            dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

            var selectedPos = -1
            for (i in list.indices) {
                if (list[i].relativeID == viewModelProfile.personId) {
                    selectedPos = i
                }
            }

            val adapter = SwitchProfileAdapter(
                selectedPos, object : SwitchProfileAdapter.OnItemClickListener {
                    override fun onItemClick(user: UserRelatives) {
                        relative = user
                        dialogBinding.dialogBtnSwitch.isEnabled =
                            relative.relativeID != viewModelProfile.personId
                    }
                },requireContext(), viewModelProfile)
            dialogBinding.recyclerView.adapter = adapter
            adapter.updateFamilyMembersList(list)
            dialogBinding.dialogBtnSwitch.isEnabled = false
            dialog.show()

            dialogBinding.imgClose.setOnClickListener {
                dialog.dismiss()
            }

            dialogBinding.dialogBtnSwitch.setOnClickListener {
                dialog.dismiss()
                Utilities.printData("relative", relative, true)
                changeFamilyProfile(relative)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun deleteFamilyMember(userRelative: UserRelatives) {
        val dialogData = DefaultNotificationDialog.DialogData()
        dialogData.title = resources.getString(R.string.DELETE_FAMILY_MEMBER)
        dialogData.message = resources.getString(R.string.MSG_DELETE_MEMBER_CONFIRMATION)
        dialogData.btnLeftName = resources.getString(R.string.CANCEL)
        dialogData.btnRightName = resources.getString(R.string.CONFIRM)
        dialogData.hasErrorBtn = true
        val defaultNotificationDialog = DefaultNotificationDialog(
            activity, object : DefaultNotificationDialog.OnDialogValueListener {

                override fun onDialogClickListener(isButtonLeft: Boolean, isButtonRight: Boolean) {
                    if (isButtonRight) {
                        viewModelProfile.callRemoveRelativesApiNew(
                            true, userRelative.relativeID, userRelative.relationShipID
                        )
                    }
                }
            },dialogData)
        defaultNotificationDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        defaultNotificationDialog.show()
    }

    private fun changeFamilyProfile(userRelative: UserRelatives) {
        val dialogData = DefaultNotificationDialog.DialogData()
        dialogData.title = resources.getString(R.string.SWITCH_PROFILE)
        dialogData.message =
            resources.getString(R.string.MSG_SWITCH_PROFILE_CONFIRMATION1) + " " + userRelative.firstName + "." + resources.getString(
                R.string.MSG_SWITCH_PROFILE_CONFIRMATION2
            )
        dialogData.btnLeftName = resources.getString(R.string.NO)
        dialogData.btnRightName = resources.getString(R.string.CONFIRM)
        val defaultNotificationDialog = DefaultNotificationDialog(
            activity, object : DefaultNotificationDialog.OnDialogValueListener {

                override fun onDialogClickListener(isButtonLeft: Boolean, isButtonRight: Boolean) {
                    if (isButtonRight) {
                        val person = Person(
                            id = userRelative.relativeID.toInt(),
                            firstName = userRelative.firstName,
                            dateOfBirth = userRelative.dateOfBirth,
                            age = userRelative.age.toInt(),
                            gender = userRelative.gender.toInt(),
                            contact = Contact(emailAddress = userRelative.emailAddress)
                        )
                        setUserDetails(person)

                        binding.imgUserPic.setImageResource(Utilities.getRelativeImgIdWithGender(userRelative.relationshipCode, userRelative.gender))
                        binding.imgUserPicBanner.setImageResource(R.drawable.btn_fill_round)
                        val bitmap = UserSingleton.getInstance()!!.profPicBitmap
                        if (userRelative.relativeID == viewModelProfile.adminPersonId && bitmap != null) {
                            binding.imgUserPic.setImageBitmap(bitmap)
                            binding.imgUserPicBanner.setImageBitmap(bitmap)
                            blurBanner(bitmap)

                        }
                        // RefreshView
                        viewModel.switchProfile(userRelative)
                        backGroundCallViewModel.refreshPersonId()
                        viewModel.refreshPersonId()
                        viewModelProfile.refreshPersonId()
                        backGroundCallViewModel.isBackgroundApiCall = false
                        backGroundCallViewModel.profileSwitched = true
                        backGroundCallViewModel.callBackgroundApiCall(true)
                        viewModelProfile.callListRelativesApi(true)
                        //viewModelProfile.callListDocumentsApi(true,Constants.SWITCH_PROFILE)
                    }
                }
            },dialogData)
        defaultNotificationDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        defaultNotificationDialog.show()
    }

    private fun viewProfilePhoto() {
        try {
            if (!Utilities.isNullOrEmpty(completeFilePath)) {
                val file = File(completeFilePath)
                if (file.exists()) {
                    val type = "image/*"
                    val intent = Intent(Intent.ACTION_VIEW)
                    val uri = Uri.fromFile(file)
                    intent.setDataAndType(uri, type)
                    //intent.setDataAndType(FileProvider.getUriForFile(this, getPackageName().toString() + ".provider", file), type)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    try {
                        startActivity(intent)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Utilities.toastMessageShort(requireContext(), resources.getString(R.string.ERROR_UNABLE_TO_OPEN_FILE))
                    }
                } else {
                    Utilities.toastMessageShort(requireContext(), resources.getString(R.string.ERROR_FILE_DOES_NOT_EXIST))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}