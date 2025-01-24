package com.test.my.app.home.ui.ProfileAndFamilyMember

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.core.net.toFile
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.aktivolabs.aktivocore.data.models.userprofile.UserProfile
import com.aktivolabs.aktivocore.data.models.userprofile.enums.Gender
import com.aktivolabs.aktivocore.data.models.userprofile.height.Height
import com.aktivolabs.aktivocore.data.models.userprofile.height.HeightCm
import com.aktivolabs.aktivocore.data.models.userprofile.weight.Weight
import com.aktivolabs.aktivocore.data.models.userprofile.weight.WeightKg
import com.aktivolabs.aktivocore.managers.AktivoManager
import com.test.my.app.R
import com.test.my.app.common.base.BaseActivity
import com.test.my.app.common.base.BaseViewModel
import com.test.my.app.common.constants.Configuration
import com.test.my.app.common.constants.Constants
import com.test.my.app.common.utils.AppColorHelper
import com.test.my.app.common.utils.DateHelper
import com.test.my.app.common.utils.DefaultNotificationDialog
import com.test.my.app.common.utils.DialogHelper
import com.test.my.app.common.utils.FileUtils
import com.test.my.app.common.utils.PermissionUtil
import com.test.my.app.common.utils.UserSingleton
import com.test.my.app.common.utils.Utilities
import com.test.my.app.common.utils.Validation
import com.test.my.app.databinding.ActivityEditProfileBinding
import com.test.my.app.home.common.DataHandler
import com.test.my.app.home.common.UrlBuilder
import com.test.my.app.home.common.UrlConfig
import com.test.my.app.home.common.WebViewInterface
import com.test.my.app.home.ui.AvatarWebViewActivity
import com.test.my.app.home.viewmodel.ProfileFamilyMemberViewModel
import com.test.my.app.model.home.Person
import com.test.my.app.model.home.UpdateUserDetailsModel
import com.test.my.app.repository.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class EditProfileActivity : BaseActivity(), EditProfileBottomSheet.OnOptionClickListener,
    AvatarWebViewActivity.WebViewCallback {

    private val viewModel: ProfileFamilyMemberViewModel by lazy {
        ViewModelProvider(this)[ProfileFamilyMemberViewModel::class.java]
    }
    lateinit var binding: ActivityEditProfileBinding

    private val appColorHelper = AppColorHelper.instance!!
    private val permissionUtil = PermissionUtil
    private val fileUtils = FileUtils

    private val cal = Calendar.getInstance()
    private var selectedDate = ""
    private val df1 = SimpleDateFormat(DateHelper.SERVER_DATE_YYYYMMDD, Locale.ENGLISH)

    private var strAgeGender = ""
    private var dateOfBirth = ""
    var completeFilePath = ""
    var hasProfileImage = false
    var needToSet = true
    var user: Person = Person()
    var gender = ""
    var isChanged = false

    //private var profPicBitmap : Bitmap? = null
    private var aktivoManager: AktivoManager? = null

    private var urlConfig: UrlConfig = UrlConfig()

    private val onBackPressedCallBack = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            Utilities.printLogError("isChanged--->$isChanged")
            if (isChanged) {
                finishWithResult()
            } else {
                finish()
            }
        }
    }

    private val takePictureLauncher  = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        Utilities.printData("result",result)
        if (result.resultCode == Activity.RESULT_OK) {
            if (result.data != null) {
                processTakePictureFile(result.data!!.extras!!)
            }
        }
    }

    private val photoPickerLauncher = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        // Callback is invoked after the user selects a media item or closes the photo picker.
        if (uri != null) {
            //imageView.setImageURI(uri)
            processPhotoPickerFile(uri)
        } else {
            Utilities.printLogError("PhotoPicker : No media selected")
        }
    }

    private val cropPictureLauncher : ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) { result ->
        try {
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                if (data != null) {
                    if ( data.hasExtra(Constants.BITMAP) ) {
                        val imageUriString  = data.getStringExtra(Constants.BITMAP)
                        val file = Uri.parse(imageUriString).toFile()
                        if ( file.exists() ) {
                            //val path = fileUtils.getFilePath(this,imageUriString!!.toUri())
                            Utilities.printLogError("path--->${file.absolutePath}")
                            val fileSize = fileUtils.calculateFileSize(file,"MB")
                            viewModel.callUploadProfileImageApi(this,file.name,file)
                        } else {
                            Utilities.toastMessageLong(this,resources.getString(R.string.ERROR_FILE_DOES_NOT_EXIST))
                        }
                    }
                }
            }
        } catch ( e:Exception ) {
            e.printStackTrace()
        }
    }

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreateEvent(savedInstanceState: Bundle?) {
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AvatarWebViewActivity.setWebViewCallback(this)
        onBackPressedDispatcher.addCallback(this,onBackPressedCallBack)
        setUpToolbar()
        initialise()
        registerObservers()
        setClickable()
    }
    private fun initialise() {
        aktivoManager = AktivoManager.getInstance(this)
        startImageShimmer()
        if(viewModel.isSelfUser()){
            //binding.layoutChangeProfilePic.visibility = View.VISIBLE
            binding.imgEditPic.visibility = View.VISIBLE
        }else{
            //binding.layoutChangeProfilePic.visibility = View.GONE
            binding.imgEditPic.visibility = View.GONE
        }
        viewModel.callGetUserDetailsEditApi()

        binding.edtUsername.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun afterTextChanged(editable: Editable) {
                if (!editable.toString().equals("", ignoreCase = true)) {
                    binding.tilEdtUsername.error = null
                    binding.tilEdtUsername.isErrorEnabled = false
                    //binding.txtUsername.text = editable.toString()
                } else {
                    //binding.txtUsername.text = user.firstName
                }
            }
        })

        binding.layoutBtnProfile.visibility = View.VISIBLE
        binding.layoutEditDetails.visibility = View.VISIBLE
    }

    private fun registerObservers() {

        viewModel.userProfileDetailsEdit.observe(this) {
            if (it.status == Resource.Status.SUCCESS) {
                val person = it.data!!.person
                if (!Utilities.isNullOrEmptyOrZero(person.profileImageID.toString())) {
                    hasProfileImage = true
                    //viewModel.callGetProfileImageApi(this, person.profileImageID.toString())
                    viewModel.callGetProfileImageApiInner(this, person.profileImageID.toString())
                } else {
                    stopImageShimmer()
                    binding.imgUserPic.setImageResource(Utilities.getRelativeImgIdWithGender("", person.gender.toString()))
                }
                user = person
                setUserDetails(person)
            }
        }

        viewModel.updateUserDetails.observe(this) {
            viewModel.hideProgressBar()
            lifecycleScope.launch(Dispatchers.Main){
                delay(600)
                if (it.status == Resource.Status.SUCCESS) {
                    val person = it.data!!.person
                    user = person
                    setUserDetails(person)
                    //updateAktivoUserProfile(person)
                    isChanged = true
                    onBackPressedCallBack.handleOnBackPressed()
                }
            }
        }
        viewModel.profileImage.observe(this) {}
        viewModel.uploadProfileImage.observe(this) {

        }
        viewModel.removeProfileImage.observe(this) {}
    }

    fun setClickable() {

        binding.tilEdtBirthdate.setOnClickListener {
            showDatePicker()
        }
        binding.edtBirthdate.setOnClickListener {
            showDatePicker()
        }

        binding.imgUserPic.setOnClickListener {
            viewBottomSheet()
        }

        binding.imgEditPic.setOnClickListener {
            viewBottomSheet()
        }

        binding.btnUpdateProfile.setOnClickListener {
            validateAndUpdate()
        }

    }

    private fun showDatePicker() {
        try {
            val mCalendar = Calendar.getInstance()
            mCalendar.add(Calendar.YEAR, -18)
            if ( !Utilities.isNullOrEmpty(selectedDate) ) {
                cal.time = df1.parse(selectedDate)!!
            }
            DialogHelper().showDatePickerDialog(resources.getString(R.string.DATE_OF_BIRTH), this,cal, null, mCalendar,
                object : DialogHelper.DateListener {
                    override fun onDateSet(date: String, year: String, month: String, dayOfMonth: String) {
                        selectedDate = DateHelper.convertDateSourceToDestination(date, DateHelper.DISPLAY_DATE_DDMMMYYYY, DateHelper.SERVER_DATE_YYYYMMDD)
//                        val date = year + "-" + (month + 1) + "-" + dayOfMonth
                        if (!Utilities.isNullOrEmpty(date)) {
                            dateOfBirth = date
                            binding.edtBirthdate.setText(DateHelper.formatDateValue(DateHelper.DATEFORMAT_DDMMMYYYY_NEW, date))
                        }
                    }
                })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun validateAndUpdate() {
        val username = binding.edtUsername.text.toString().trim { it <= ' ' }
        val dob = binding.edtBirthdate.text.toString().trim { it <= ' ' }

        var isValid = true

        if ( Validation.isEmpty(username) || !Validation.isValidName(username)) {
            isValid = false
            viewModel.toastMessage(binding.edtBirthdate.context.resources.getString(R.string.VALIDATE_USERNAME))
        }

        if( Utilities.isNullOrEmpty(binding.edtBirthdate.text.toString()) ){
            isValid = false
            viewModel.toastMessage(binding.edtBirthdate.context.resources.getString(R.string.VALIDATE_DATE_OF_BIRTH))
        }

        dateOfBirth = DateHelper.convertDateSourceToDestination(dob,DateHelper.DATEFORMAT_DDMMMYYYY_NEW,DateHelper.SERVER_DATE_YYYYMMDD)

        if ( isValid ) {
            //Helper.showMessage(getContext(),"Details Updated");
            val newUserDetails = UpdateUserDetailsModel.PersonRequest(
                id = user.id,
                firstName = username,
                dateOfBirth = dateOfBirth,
                gender = user.gender.toString(),
                contact = UpdateUserDetailsModel.Contact(
                    emailAddress = user.contact.emailAddress,
                    primaryContactNo = user.contact.primaryContactNo,
                    alternateEmailAddress = "",
                    alternateContactNo = "",),
                address = UpdateUserDetailsModel.Address(
                    addressLine1 = ""))
            viewModel.callUpdateUserDetailsApi(newUserDetails)
        }
    }

    private fun viewBottomSheet() {
        try {
            val bottomSheet = EditProfileBottomSheet( this,hasProfileImage )
            bottomSheet.show(supportFragmentManager, EditProfileBottomSheet.TAG)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onOptionClick( code: String ) {
        if (code == DataHandler.ProfileImgOption.View) {
            viewProfilePhoto()
        }
        if (code == DataHandler.ProfileImgOption.Avatar) {
            proceedWithCameraPermissionToAvatar()
        }
        if (code == DataHandler.ProfileImgOption.Gallery) {
            showImageChooser()
        }
        if (code == DataHandler.ProfileImgOption.Photo) {
            proceedWithCameraPermission()
        }
        if (code == DataHandler.ProfileImgOption.Remove) {
            val dialogData = DefaultNotificationDialog.DialogData()
            dialogData.title = resources.getString(R.string.REMOVE_PROFILE_PHOTO)
            dialogData.message = resources.getString(R.string.MSG_REMOVE_PROFILE_PHOTO_CONFORMATION)
            dialogData.btnLeftName = resources.getString(R.string.NO)
            dialogData.btnRightName = resources.getString(R.string.YES)
            dialogData.hasErrorBtn = true
            val defaultNotificationDialog =
                DefaultNotificationDialog(
                    this,
                    object : DefaultNotificationDialog.OnDialogValueListener {
                        override fun onDialogClickListener(isButtonLeft: Boolean, isButtonRight: Boolean) {
                            if (isButtonRight) {
                                viewModel.callRemoveProfileImageApi(this@EditProfileActivity, this@EditProfileActivity)
                            }
                        }
                    },dialogData)
            defaultNotificationDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            defaultNotificationDialog.show()
        }
    }

    private fun proceedWithCameraPermissionToAvatar() {
        val permissionResult: Boolean = permissionUtil.checkCameraPermission(object :
            PermissionUtil.AppPermissionListener {
            override fun isPermissionGranted(isGranted: Boolean) {
                Utilities.printLogError("$isGranted")
                if (isGranted) {
                    launchAvatarWebView()
                }
            }
        }, this)
        if (permissionResult) {
            launchAvatarWebView()
        }
    }

    private fun launchAvatarWebView() {
        openAvatarWebView()
    }

    private fun viewProfilePhoto() {
        try {
            //Utilities.showFullImageWithBitmap(bitmap,this,true)
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
                        Utilities.toastMessageShort(this, resources.getString(R.string.ERROR_UNABLE_TO_OPEN_FILE))
                    }
                } else {
                    Utilities.toastMessageShort(this, resources.getString(R.string.ERROR_FILE_DOES_NOT_EXIST))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun proceedWithCameraPermission() {
        val permissionResult: Boolean = permissionUtil.checkCameraPermission(object :
            PermissionUtil.AppPermissionListener {
            override fun isPermissionGranted(isGranted: Boolean) {
                Utilities.printLogError("$isGranted")
                if (isGranted) {
                    dispatchTakePictureIntent()
                }
            }
        }, this)
        if (permissionResult) {
            dispatchTakePictureIntent()
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
/*        if (takePictureIntent.resolveActivity(Objects.requireNonNull(this).packageManager) != null) {
            startActivityForResult(takePictureIntent, Constants.CAMERA_SELECT_CODE)
        }*/
        takePictureLauncher.launch(takePictureIntent)
    }

    private fun showImageChooser() {
        photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun showImageCropper(uriImage:Uri) {
        val intent = Intent(this,CropPictureActivity::class.java)
        intent.putExtra(Constants.URI,uriImage.toString())
        cropPictureLauncher.launch(intent)
        //CropImage.activity(uriImage).start(this)
    }

//    private fun showImageChooser() {
//        val pickIntent = Intent(Intent.ACTION_PICK)
//        pickIntent.type = "image/*"
//        startActivityForResult(pickIntent,Constants.GALLERY_SELECT_CODE)
//    }

    private fun setUserDetails(user: Person) {
        try {
            if (!Utilities.isNullOrEmptyOrZero(user.id.toString())) {
                val email = user.contact.emailAddress
                val number = user.contact.primaryContactNo
                gender = user.gender.toString()

                if (!Utilities.isNullOrEmpty(user.firstName)) {
                    //binding.txtUsername.text = user.firstName
                    viewModel.updateFirstName(user.firstName)
                    binding.edtUsername.setText(user.firstName)
                }

                if (!Utilities.isNullOrEmpty(email)) {
                    binding.edtEmail.setText(email)
                }

                if (!Utilities.isNullOrEmpty(number)) {
                    binding.tilEdtNumber.visibility = View.VISIBLE
                    binding.edtNumber.setText(number)
                } else {
                    binding.tilEdtNumber.visibility = View.GONE
                }
                if ( !Utilities.isNullOrEmpty(user.dateOfBirth) ) {
                    selectedDate = user.dateOfBirth.split("T").toTypedArray()[0]
                    var dateOfBirth = user.dateOfBirth
                    dateOfBirth = DateHelper.formatDateValue(DateHelper.DISPLAY_DATE_DDMMMYYYY, dateOfBirth)!!
                    val viewDob = DateHelper.formatDateValue(DateHelper.DATEFORMAT_DDMMMYYYY_NEW, dateOfBirth)!!
                    Utilities.printLog("DOB--->$dateOfBirth")
                    if (!Utilities.isNullOrEmpty(dateOfBirth)) {
                        val age: String = DateHelper.calculatePersonAge(dateOfBirth,this)
                        strAgeGender = if (!Utilities.isNullOrEmptyOrZero(user.age.toString())) {
                            user.age.toString() + " Yrs"
                        } else {
                            age
                        }
                        binding.edtBirthdate.setText(viewDob)
                    }
                } else {
                    binding.edtBirthdate.setText("")
                }

                if(!Utilities.isNullOrEmpty(gender)){
                    if(gender.equals("1",true)){
                        binding.edtGender.setText(resources.getString(R.string.MALE))
                    }else {
                        binding.edtGender.setText(resources.getString(R.string.FEMALE))
                    }

                }else{
                    binding.edtGender.setText("")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setProfilePic() {
        try {
            Utilities.printLogError("completeFilePath----->$completeFilePath")
            Utilities.printLogError("needToSet----->$needToSet")
            if ( needToSet  ) {
                if (!Utilities.isNullOrEmpty(completeFilePath)) {
                    val bitmap = BitmapFactory.decodeFile(completeFilePath)
                    if (bitmap != null) {
                        binding.imgUserPic.setImageBitmap(bitmap)
                        //profPicBitmap = bitmap
                        UserSingleton.getInstance()!!.profPicBitmap = bitmap
                        needToSet = false
                    }
                }
            }
        } catch ( e :Exception ) {
            e.printStackTrace()
        }
    }

    fun removeProfilePic() {
        hasProfileImage = false
        when(gender) {
            "1" -> {
                binding.imgUserPic.setImageResource(Utilities.getRelativeImgIdWithGender("",gender))
            }
            "2" -> {
                binding.imgUserPic.setImageResource(Utilities.getRelativeImgIdWithGender("",gender))
            }
            else -> {
                binding.imgUserPic.setImageResource(R.drawable.img_my_profile)
            }
        }

    }

/*    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Utilities.printLogError("requestCode,resultCode,data----->$requestCode,$resultCode,$data")
        try {
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                val result = CropImage.getActivityResult(data)
                if (resultCode == RESULT_OK) {
                    val imageUri = result.uri
                    val imagePath = fileUtils.getFilePath(this, imageUri!!)!!
                    val fileSize = fileUtils.calculateFileSize(imagePath,"MB")
                    if (fileSize <= 5.0) {
                        val extension = fileUtils.getFileExt(imagePath)
                        if ( Utilities.isAcceptableDocumentType(extension) ) {
                            val fileName = fileUtils.generateUniqueFileName(Configuration.strAppIdentifier + "_PROFPIC",imagePath)
                            Utilities.printLogError("File Path---> $imagePath")
                            val saveImage = fileUtils.saveRecordToExternalStorage(this,imagePath,imageUri,fileName)
                            if ( saveImage != null ) {
                                Utilities.deleteFileFromLocalSystem(imagePath)
                                viewModel.callUploadProfileImageApi(this,fileName,saveImage)
                            }
                        } else {
                            Utilities.toastMessageLong(this, extension + " " + resources.getString(R.string.ERROR_FILES_NOT_ACCEPTED))
                        }
                    } else {
                        Utilities.toastMessageLong(this,resources.getString(R.string.ERROR_FILE_SIZE_LESS_THEN_5MB))
                    }
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    val error = result.error
                    Utilities.printLogError("ImageCropperError--->$error")
                }
            }
            super.onActivityResult(requestCode, resultCode, data)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }*/

    private fun processTakePictureFile(extras:Bundle) {
        val photo = extras.get("data") as Bitmap
        val uriImage = fileUtils.getImageUri(this,photo)
        val cameraImgPath = fileUtils.getFilePath(this, uriImage)!!
        val fileSize = fileUtils.calculateFileSize(cameraImgPath,"MB")
        if (fileSize <= 5.0) {
            showImageCropper(uriImage)
        } else {
            Utilities.toastMessageShort(this, resources.getString(R.string.ERROR_FILE_SIZE_LESS_THEN_5MB))
        }
    }

    private fun processPhotoPickerFile(uriImage:Uri) {
        val imagePath = fileUtils.getFilePath(this,uriImage)!!
        val fileSize = fileUtils.calculateFileSize(imagePath,"MB")
        if (fileSize <= 5.0) {
            val extension = fileUtils.getFileExt(imagePath)
            if ( Utilities.isAcceptableDocumentType(extension) ) {
                showImageCropper(uriImage)
            } else {
                Utilities.toastMessageLong(this, extension + " " + resources.getString(R.string.ERROR_FILES_NOT_ACCEPTED))
            }
        } else {
            Utilities.toastMessageShort(this, resources.getString(R.string.ERROR_FILE_SIZE_LESS_THEN_5MB))
        }
    }

    private fun startImageShimmer() {
        binding.layoutImgShimmer.startShimmer()
        binding.layoutImgShimmer.visibility = View.VISIBLE
        binding.layoutImgDetails.visibility = View.GONE
    }


    fun stopImageShimmer() {
        binding.layoutImgShimmer.stopShimmer()
        binding.layoutImgShimmer.visibility = View.GONE
        binding.layoutImgDetails.visibility = View.VISIBLE
    }

    private fun setUpToolbar() {
        setSupportActionBar(binding.toolBarView.toolbarCommon)
        binding.toolBarView.toolbarTitle.text = resources.getString(R.string.TITLE_EDIT_PROFILE)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
        binding.toolBarView.toolbarCommon.navigationIcon?.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(appColorHelper.textColor, BlendModeCompat.SRC_ATOP)

        binding.toolBarView.toolbarCommon.setNavigationOnClickListener {
            onBackPressedCallBack.handleOnBackPressed()
        }
    }

    private fun updateAktivoUserProfile(user: Person) {
        try {
            val date = DateHelper.convertDateSourceToDestination(user.dateOfBirth.split("T").toTypedArray()[0],DateHelper.SERVER_DATE_YYYYMMDD,"yyyy/MM/dd")
            val dateOfBirth = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy/MM/dd"))
            var gender = Gender.Male
            when(user.gender.toString()) {
                "1" -> gender = Gender.Male
                "2" -> gender = Gender.Female
            }
            val userProfile = UserProfile(
                nickName = user.firstName,
                dateOfBirth = dateOfBirth,
                gender = gender,
                height = Height(HeightCm(172)), weight = Weight(WeightKg(64))
            )
            Utilities.printData("UserProfile",userProfile,true)
            aktivoManager!!.updateUserProfile(userProfile).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : SingleObserver<Boolean> {
                    override fun onSubscribe(d: Disposable) {}
                    override fun onSuccess(aBoolean: Boolean) {
                        Utilities.printLogError("Response for update profile: $aBoolean")
                    }

                    override fun onError(e: Throwable) {
                        Utilities.printLogError("Response error for update profile: " + e.message)
                    }
                })
        } catch ( e:Exception ) {
            e.printStackTrace()
        }
    }

    fun finishWithResult() {
        setResult(Activity.RESULT_OK,Intent())
        finish()
    }

    private fun openAvatarWebView() {
        if( !Utilities.isNullOrEmpty(gender) ) {
            when(gender) {
                "1" ->   urlConfig.gender = com.test.my.app.home.common.Gender.MALE
                "2" ->   urlConfig.gender = com.test.my.app.home.common.Gender.FEMALE
            }
        }
        urlConfig.bodyType = com.test.my.app.home.common.BodyType.FULLBODY
        val intent = Intent(this,AvatarWebViewActivity::class.java)
        intent.putExtra(AvatarWebViewActivity.CLEAR_BROWSER_CACHE, true)
        intent.putExtra(AvatarWebViewActivity.URL_KEY,UrlBuilder(urlConfig).buildUrl())
        webViewActivityResultLauncher.launch(intent)
    }

    private val webViewActivityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            Utilities.printLogError("RPM : Result activity run.")
        }
    }

    override fun onAvatarExported(avatarUrl: String) {
        try {
            Utilities.printLogError("RPM : Avatar Exported - Avatar URL: $avatarUrl")
            val avatarImg = avatarUrl.replace(".glb", ".png")
            getAvatar(avatarImg)
        } catch ( e:Exception ) {
            e.printStackTrace()
        }
    }

    private fun getAvatar(avatarUrl: String) {
        if( !Utilities.isNullOrEmpty(avatarUrl) ) {
            try {
                val fileName = fileUtils.generateUniqueFileName(Configuration.strAppIdentifier + "_PROFPIC", ".png")
                val file = fileUtils.saveImageUrlToExternalStorage(this,avatarUrl,fileName)!!
                val fileSize = fileUtils.calculateFileSize(file.absolutePath, "MB")
                if ( file.exists() ) {
                    viewModel.callUploadProfileImageApi(this,file.name,file)
                } else {
                    Utilities.toastMessageLong(this,resources.getString(R.string.ERROR_FILE_DOES_NOT_EXIST))
                }
            } catch ( e:Exception ) {
                e.printStackTrace()
            }
        }
    }

    override fun onOnUserSet(userId: String) {
        Utilities.printLogError("RPM : User Set - User ID: $userId")
    }

    override fun onOnUserUpdated(userId: String) {
        Utilities.printLogError("RPM : User Updated - User ID: $userId")
    }

    override fun onOnUserAuthorized(userId: String) {
        Utilities.printLogError("RPM : User Authorized - User ID: $userId")
    }

    override fun onAssetUnlock(assetRecord: WebViewInterface.AssetRecord) {
        Utilities.printLogError("RPM : Asset Unlock - Asset Record: $assetRecord")
    }

    override fun onUserLogout() {
        Utilities.printLogError("RPM : User Logout")
    }

}