package com.test.my.app.home.viewmodel


import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.widget.ImageView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.test.my.app.R
import com.test.my.app.common.base.BaseViewModel
import com.test.my.app.common.constants.Constants
import com.test.my.app.common.constants.FirebaseConstants
import com.test.my.app.common.constants.PreferenceConstants
import com.test.my.app.common.utils.*
import com.test.my.app.home.common.DataHandler
import com.test.my.app.home.domain.HomeManagementUseCase
import com.test.my.app.home.domain.SudLifePolicyManagementUseCase
import com.test.my.app.home.ui.FragmentProfile
import com.test.my.app.home.ui.ProfileAndFamilyMember.*
import com.test.my.app.model.entity.HealthDocument
import com.test.my.app.model.entity.UserRelatives
import com.test.my.app.model.entity.Users
import com.test.my.app.model.home.*
import com.test.my.app.model.home.AddRelativeModel.Relationship
import com.test.my.app.model.shr.ListRelativesModel
import com.test.my.app.repository.utils.Resource
import com.google.gson.Gson
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import java.io.File
import java.io.FileInputStream
import java.util.*
import javax.inject.Inject

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class ProfileFamilyMemberViewModel @Inject constructor(
    application: Application,
    private val homeManagementUseCase: HomeManagementUseCase,
    private val sudLifePolicyManagementUseCase: SudLifePolicyManagementUseCase,
    private val preferenceUtils: PreferenceUtils,
    private val dataHandler: DataHandler,
    val context: Context?) : BaseViewModel(application) {

    var adminPersonId = preferenceUtils.getPreference(PreferenceConstants.ADMIN_PERSON_ID, "0")
    var personId = preferenceUtils.getPreference(PreferenceConstants.PERSONID, "0")
    var gender = preferenceUtils.getPreference(PreferenceConstants.GENDER, "")
    var relationshipCode = preferenceUtils.getPreference(PreferenceConstants.RELATIONSHIPCODE, "")
    var authToken = preferenceUtils.getPreference(PreferenceConstants.TOKEN, "")

    private val localResource = LocaleHelper.getLocalizedResources(context!!, Locale(LocaleHelper.getLanguage(context)))!!
    private val fileUtils = FileUtils
    private var relativeToRemove: List<UserRelatives> = listOf()

    var userDetails = MutableLiveData<Users>()
    val userRelativesList = MutableLiveData<List<UserRelatives>>()
    val alreadyExistRelatives = MutableLiveData<List<UserRelatives>>()
    var familyRelationList = MutableLiveData<List<DataHandler.FamilyRelationOption>>()
    val allHealthDocuments = MutableLiveData<List<HealthDocument>>()

    private var userProfileDetailsSource: LiveData<Resource<UserDetailsModel.UserDetailsResponse>> = MutableLiveData()
    private val _userProfileDetails = MediatorLiveData<Resource<UserDetailsModel.UserDetailsResponse>>()
    val userProfileDetails: LiveData<Resource<UserDetailsModel.UserDetailsResponse>> get() = _userProfileDetails

    private var userProfileDetailsEditSource: LiveData<Resource<UserDetailsModel.UserDetailsResponse>> = MutableLiveData()
    private val _userProfileDetailsEdit = MediatorLiveData<Resource<UserDetailsModel.UserDetailsResponse>>()
    val userProfileDetailsEdit: LiveData<Resource<UserDetailsModel.UserDetailsResponse>> get() = _userProfileDetailsEdit

    private var updateUserDetailsSource: LiveData<Resource<UpdateUserDetailsModel.UpdateUserDetailsResponse>> = MutableLiveData()
    private val _updateUserDetails = MediatorLiveData<Resource<UpdateUserDetailsModel.UpdateUserDetailsResponse>>()
    val updateUserDetails: LiveData<Resource<UpdateUserDetailsModel.UpdateUserDetailsResponse>> get() = _updateUserDetails

    private var profileImageSource: LiveData<Resource<ProfileImageModel.ProfileImageResponse>> = MutableLiveData()
    private val _profileImage = MediatorLiveData<Resource<ProfileImageModel.ProfileImageResponse>>()
    val profileImage: LiveData<Resource<ProfileImageModel.ProfileImageResponse>> get() = _profileImage

    private var uploadProfileImageSource: LiveData<Resource<UploadProfileImageResponce>> = MutableLiveData()
    private val _uploadProfileImage = MediatorLiveData<Resource<UploadProfileImageResponce>>()
    val uploadProfileImage: LiveData<Resource<UploadProfileImageResponce>> get() = _uploadProfileImage

    private var removeProfileImageSource: LiveData<Resource<RemoveProfileImageModel.RemoveProfileImageResponse>> = MutableLiveData()
    private val _removeProfileImage = MediatorLiveData<Resource<RemoveProfileImageModel.RemoveProfileImageResponse>>()
    val removeProfileImage: LiveData<Resource<RemoveProfileImageModel.RemoveProfileImageResponse>> get() = _removeProfileImage

    private var addRelativeSource: LiveData<Resource<AddRelativeModel.AddRelativeResponse>> = MutableLiveData()
    private val _addRelative = MediatorLiveData<Resource<AddRelativeModel.AddRelativeResponse>>()
    val addRelative: LiveData<Resource<AddRelativeModel.AddRelativeResponse>> get() = _addRelative

    private var updateRelativeSource: LiveData<Resource<UpdateRelativeModel.UpdateRelativeResponse>> = MutableLiveData()
    private val _updateRelative = MediatorLiveData<Resource<UpdateRelativeModel.UpdateRelativeResponse>>()
    val updateRelative: LiveData<Resource<UpdateRelativeModel.UpdateRelativeResponse>> get() = _updateRelative

    private var removeRelativeSource: LiveData<Resource<RemoveRelativeModel.RemoveRelativeResponse>> = MutableLiveData()
    private val _removeRelative = MediatorLiveData<Resource<RemoveRelativeModel.RemoveRelativeResponse>>()
    val removeRelative: LiveData<Resource<RemoveRelativeModel.RemoveRelativeResponse>> get() = _removeRelative

    private var listRelativesSource: LiveData<Resource<ListRelativesModel.ListRelativesResponse>> = MutableLiveData()
    private val _listRelatives = MediatorLiveData<Resource<ListRelativesModel.ListRelativesResponse>>()
    val listRelatives: LiveData<Resource<ListRelativesModel.ListRelativesResponse>> get() = _listRelatives

    fun isSelfUser(): Boolean {
        val personId = preferenceUtils.getPreference(PreferenceConstants.PERSONID, "0")
        val adminPersonId = preferenceUtils.getPreference(PreferenceConstants.ADMIN_PERSON_ID, "0")
        var isSelfUser = false
        if ( !Utilities.isNullOrEmptyOrZero(personId)
            && !Utilities.isNullOrEmptyOrZero(adminPersonId)
            && personId == adminPersonId ) {
            isSelfUser = true
        }
        return isSelfUser
    }

    fun callAddNewRelativeApi(forceRefresh:Boolean,userRelative:UserRelatives,from:String,fragment: AddFamilyMemberFragment) =
        viewModelScope.launch(Dispatchers.Main) {

            val contact = AddRelativeModel.Contact(userRelative.emailAddress, userRelative.contactNo)
            val relationships: ArrayList<Relationship> = ArrayList()
            relationships.add(Relationship(preferenceUtils.getPreference(PreferenceConstants.PERSONID, "0"), userRelative.relationshipCode))
            var gender = ""
            if (userRelative.gender.equals("Male", ignoreCase = true)) {
                gender = "1"
            } else if (userRelative.gender.equals("Female", ignoreCase = true)) {
                gender = "2"
            }

            val requestData = AddRelativeModel(Gson().toJson(AddRelativeModel.JSONDataRequest(
                personID = preferenceUtils.getPreference(PreferenceConstants.PERSONID, "0"),
                person = AddRelativeModel.Person(
                    firstName = userRelative.firstName,
                    relativeID = userRelative.relativeID,
                    dateOfBirth = userRelative.dateOfBirth,
                    gender = gender,
                    isProfileImageChanges = Constants.FALSE,
                    contact = contact,
                    relationships = relationships)),AddRelativeModel.JSONDataRequest::class.java), authToken)

            _progressBar.value = Event("Adding Family Member.....")
            _addRelative.removeSource(addRelativeSource)
            withContext(Dispatchers.IO) {
                addRelativeSource = homeManagementUseCase.invokeaddNewRelative(isForceRefresh = forceRefresh, data = requestData)
            }
            _addRelative.addSource(addRelativeSource) {
                _addRelative.value = it

                if (it.status == Resource.Status.SUCCESS) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    FirebaseHelper.logCustomFirebaseEvent(FirebaseConstants.FAMILY_MEMBER_ADD_EVENT)
                }
                if (it.status == Resource.Status.ERROR) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    if (it.errorNumber.equals("1100014", true)) {
                        _sessionError.value = Event(true)
                    } else {
                        toastMessage(it.errorMessage)
                    }
                }
            }

        }

    fun callListRelativesApi(forceRefresh: Boolean) = viewModelScope.launch(Dispatchers.Main) {

        val requestData = ListRelativesModel(Gson().toJson(ListRelativesModel.JSONDataRequest(
            personID = adminPersonId), ListRelativesModel.JSONDataRequest::class.java), authToken)

        _progressBar.value = Event("Getting Relatives...")
        _listRelatives.removeSource(listRelativesSource)
        withContext(Dispatchers.IO) {
            listRelativesSource = homeManagementUseCase.invokeRelativesList(isForceRefresh = forceRefresh, data = requestData)
        }
        _listRelatives.addSource(listRelativesSource) {
            _listRelatives.value = it

            if (it.status == Resource.Status.SUCCESS) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                if (it.data != null) {
                    val relativesList = it.data!!.relativeList
                    if (relativesList.size > 1) {
                        val userRelatives: MutableList<UserRelatives> = mutableListOf()
                        for (i in relativesList) {
                            userRelatives.add(i)
                            /*                                if (i.relationshipCode != "SELF") {
                                                                userRelatives.add(i)
                                                            }*/
                        }
                        userRelativesList.postValue(userRelatives)
                    } else {
//                            fragment.noDataView()
                    }
                    Utilities.printLog("RelativesList----->${relativesList.size}")
                }
            }
            if (it.status == Resource.Status.ERROR) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                if (it.errorNumber.equals("1100014", true)) {
                    _sessionError.value = Event(true)
                } else {
                    toastMessage(it.errorMessage)
                }
            }
        }
    }

    fun callRemoveRelativesApiNew(forceRefresh: Boolean, relativeId: String, relationshipId: String) =
        viewModelScope.launch(Dispatchers.Main) {

            val relatives: ArrayList<Int> = ArrayList()
            withContext(Dispatchers.IO) {
                relativeToRemove = homeManagementUseCase.invokeGetUserRelativeForRelativeId(relativeId)
            }

            for (i in relativeToRemove) {
                relatives.add(relationshipId.toInt())
            }

            val requestData = RemoveRelativeModel(Gson().toJson(RemoveRelativeModel.JSONDataRequest(
                id = relatives), RemoveRelativeModel.JSONDataRequest::class.java), authToken)

            _progressBar.value = Event(Constants.LOADER_DELETE)
            _removeRelative.removeSource(removeRelativeSource)
            withContext(Dispatchers.IO) {
                removeRelativeSource = homeManagementUseCase.invokeRemoveRelative(isForceRefresh = forceRefresh, data = requestData,relativeId = relativeId)
            }
            _removeRelative.addSource(removeRelativeSource) {
                _removeRelative.value = it

                if (it.status == Resource.Status.SUCCESS) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    toastMessage(localResource.getString(R.string.MEMBER_DELETED))
                }
                if (it.status == Resource.Status.ERROR) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    if (it.errorNumber.equals("1100014", true)) {
                        _sessionError.value = Event(true)
                    } else {
                        toastMessage(it.errorMessage)
                    }
                }
            }
        }

    fun callUpdateRelativesApi(forceRefresh: Boolean, relative: UserRelatives, from: String) =
        viewModelScope.launch(Dispatchers.Main) {

            val requestData = UpdateRelativeModel(Gson().toJson(UpdateRelativeModel.JSONDataRequest(
                personID = preferenceUtils.getPreference(PreferenceConstants.PERSONID, "0"),
                person = UpdateRelativeModel.Person(
                    id = relative.relativeID.toInt(),
                    firstName = relative.firstName,
                    lastName = "",
                    dateOfBirth = relative.dateOfBirth,
                    gender = relative.gender,
                    isProfileImageChanges = Constants.FALSE,
                    contact = UpdateRelativeModel.Contact(
                        emailAddress = relative.emailAddress,
                        primaryContactNo = relative.contactNo))), UpdateRelativeModel.JSONDataRequest::class.java), authToken)

            _progressBar.value = Event("Updating Relative Profile.....")
            _updateRelative.removeSource(updateRelativeSource)
            withContext(Dispatchers.IO) {
                updateRelativeSource = homeManagementUseCase.invokeupdateRelative(isForceRefresh = forceRefresh, data = requestData,relativeId = relative.relativeID)
            }
            _updateRelative.addSource(updateRelativeSource) {
                _updateRelative.value = it

                if (it.status == Resource.Status.SUCCESS) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    if (it != null) {
                        val personDetails = it.data!!.person
                        if (!Utilities.isNullOrEmpty(personDetails.id.toString())) {
                            //toastMessage(context.resources.getString(R.string.PROFILE_UPDATED))
                            //navigate(EditFamilyMemberDetailsFragmentDirections.actionEditFamilyMemberDetailsFragmentToFamilyMembersListFragment())
                        }
                    }
                }
                if (it.status == Resource.Status.ERROR) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    if (it.errorNumber.equals("1100014", true)) {
                        _sessionError.value = Event(true)
                    } else {
                        toastMessage(it.errorMessage)
                        if (from == Constants.RELATIVE) {
                            //navigate(EditFamilyMemberDetailsFragmentDirections.actionEditFamilyMemberDetailsFragmentToFamilyMembersListFragment())
                        }
                    }
                }
            }
        }

    fun callGetUserDetailsEditApi() = viewModelScope.launch(Dispatchers.Main) {

        val requestData = UserDetailsModel(Gson().toJson(UserDetailsModel.JSONDataRequest(
            UserDetailsModel.PersonIdentificationCriteria(
                personId = preferenceUtils.getPreference(PreferenceConstants.PERSONID, "0").toInt())), UserDetailsModel.JSONDataRequest::class.java), authToken)

        _userProfileDetailsEdit.removeSource(userProfileDetailsEditSource)
        withContext(Dispatchers.IO) {
            userProfileDetailsEditSource = homeManagementUseCase.invokeGetUserDetails(isForceRefresh = true, data = requestData)
        }
        _userProfileDetailsEdit.addSource(userProfileDetailsEditSource) {
            _userProfileDetailsEdit.value = it

            if (it.status == Resource.Status.SUCCESS) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                if (it.data != null) {
                    val person = it.data!!.person
                    Utilities.printLog("GetUserDetails----->$person")
                }
            }
            if (it.status == Resource.Status.ERROR) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                if (it.errorNumber.equals("1100014", true)) {
                    _sessionError.value = Event(true)
                } else {
                    toastMessage(it.errorMessage)
                }
            }
        }
    }

    fun callGetUserDetailsApi() = viewModelScope.launch(Dispatchers.Main) {

        val requestData = UserDetailsModel(Gson().toJson(UserDetailsModel.JSONDataRequest(
            UserDetailsModel.PersonIdentificationCriteria(
                personId = preferenceUtils.getPreference(PreferenceConstants.PERSONID, "0").toInt())), UserDetailsModel.JSONDataRequest::class.java), authToken)

        _userProfileDetails.removeSource(userProfileDetailsSource)
        withContext(Dispatchers.IO) {
            userProfileDetailsSource = homeManagementUseCase.invokeGetUserDetails(isForceRefresh = true, data = requestData)
        }
        _userProfileDetails.addSource(userProfileDetailsSource) {
            _userProfileDetails.value = it

            if (it.status == Resource.Status.SUCCESS) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                /*                if (it.data != null) {
                                    val person = it.data!!.person
                                    Utilities.printLog("GetUserDetails----->$person")
                                }*/
            }
            if (it.status == Resource.Status.ERROR) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                if (it.errorNumber.equals("1100014", true)) {
                    _sessionError.value = Event(true)
                } else {
                    toastMessage(it.errorMessage)
                }
            }
        }
    }

    fun callUpdateUserDetailsApi(person: UpdateUserDetailsModel.PersonRequest) = viewModelScope.launch(Dispatchers.Main) {

        val requestData = UpdateUserDetailsModel(Gson().toJson(UpdateUserDetailsModel.JSONDataRequest(
            personID = preferenceUtils.getPreference(PreferenceConstants.PERSONID, "0"),
            person = person), UpdateUserDetailsModel.JSONDataRequest::class.java), authToken)

        _progressBar.value = Event("Updating Profile Details.....")
        _updateUserDetails.removeSource(updateUserDetailsSource)
        withContext(Dispatchers.IO) {
            updateUserDetailsSource = homeManagementUseCase.invokeUpdateUserDetails(isForceRefresh = true, data = requestData)
        }
        _updateUserDetails.addSource(updateUserDetailsSource) {
            _updateUserDetails.value = it

            if (it.status == Resource.Status.SUCCESS) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                if (it != null) {
                    val personDetails = it.data!!.person
                    Utilities.printLog("UpdateUserDetails----->${it.data!!.person}")
                    Utilities.printLog("PersonId-----> ${personDetails.id}")
                    Utilities.printLog("UpdatedName-----> ${personDetails.firstName}")
                    Utilities.printLog("UpdatedDOB-----> ${personDetails.dateOfBirth}")
                    if (!Utilities.isNullOrEmpty(personDetails.id.toString())) {
                        updateUserDetails(personDetails.firstName,personDetails.dateOfBirth, personDetails.id)
                        Utilities.toastMessageShort(context, localResource.getString(R.string.PROFILE_UPDATED))
                    }
                }

            }
            if (it.status == Resource.Status.ERROR) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                if (it.errorNumber.equals("1100014", true)) {
                    _sessionError.value = Event(true)
                } else {
                    toastMessage(it.errorMessage)
                }
            }
        }
    }

    fun callGetProfileImageApiMain(fragment: FragmentProfile, documentID : String ) = viewModelScope.launch(Dispatchers.Main) {

        val requestData = ProfileImageModel(Gson().toJson(
            ProfileImageModel.JSONDataRequest(
                documentID = documentID),
            ProfileImageModel.JSONDataRequest::class.java) , authToken )

        _profileImage.removeSource(profileImageSource)
        withContext(Dispatchers.IO) {
            profileImageSource = homeManagementUseCase.invokeGetProfileImage(isForceRefresh = true, data = requestData)
        }
        _profileImage.addSource(profileImageSource) {
            _profileImage.value = it

            if (it.status == Resource.Status.SUCCESS) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                if (it.data != null ) {
                    /*                    val document = it.data!!.healthRelatedDocument
                                        val fileName = document.fileName
                                        val fileBytes = document.fileBytes
                                        try {
                                            val path = Utilities.getAppFolderLocation(context)
                                            if (!File(path,fileName).exists()) {
                                                if ( !Utilities.isNullOrEmpty(fileBytes) ) {
                                                    val decodedImage = fileUtils.convertBase64ToBitmap(fileBytes)
                                                    if (decodedImage != null) {
                                                        val saveRecordUri = fileUtils.saveBitmapToExternalStorage(context,decodedImage,fileName)
                                                        if ( saveRecordUri != null ) {
                                                            updateUserProfileImgPath(fileName,path)
                                                        }
                                                    }
                                                }
                                            } else {
                                                updateUserProfileImgPath(fileName,path)
                                            }
                                            fragment.completeFilePath = path + "/"  + fileName
                                            fragment.setProfilePic()
                                            fragment.stopImageShimmer()
                                        } catch ( e : Exception ) {
                                            e.printStackTrace()
                                            fragment.stopImageShimmer()
                                        }*/
                }
            }
            if (it.status == Resource.Status.ERROR) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                if(it.errorNumber.equals("1100014",true)){
                    _sessionError.value = Event(true)
                }else {
                    toastMessage(it.errorMessage)
                    fragment.stopImageShimmer()
                }
            }
        }
    }

    fun callGetProfileImageApiInner( activity: EditProfileActivity,documentID : String ) = viewModelScope.launch(Dispatchers.Main) {

        val requestData = ProfileImageModel(Gson().toJson(
            ProfileImageModel.JSONDataRequest(
                documentID = documentID),
            ProfileImageModel.JSONDataRequest::class.java) , authToken )

        _profileImage.removeSource(profileImageSource)
        withContext(Dispatchers.IO) {
            profileImageSource = homeManagementUseCase.invokeGetProfileImage(isForceRefresh = true, data = requestData)
        }
        _profileImage.addSource(profileImageSource) {
            _profileImage.value = it

            if (it.status == Resource.Status.SUCCESS) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                if (it.data != null ) {
                    val document = it.data.healthRelatedDocument
                    val fileName = document.fileName
                    val fileBytes = document.fileBytes
                    try {
                        val path = Utilities.getAppFolderLocation(context!!)
                        if (!File(path,fileName).exists()) {
                            if ( !Utilities.isNullOrEmpty(fileBytes) ) {
                                val decodedImage = fileUtils.convertBase64ToBitmap(fileBytes)
                                if (decodedImage != null) {
                                    val saveRecordUri = fileUtils.saveBitmapToExternalStorage(context,decodedImage,fileName)
                                    if ( saveRecordUri != null ) {
                                        updateUserProfileImgPath(fileName,path)
                                    }
                                    UserSingleton.getInstance()!!.profPicBitmap = decodedImage
                                }
                            }
                        } else {
                            updateUserProfileImgPath(fileName,path)
                        }
                        activity.completeFilePath = "$path/$fileName"
                        activity.setProfilePic()
                        activity.stopImageShimmer()
                    } catch ( e : Exception ) {
                        e.printStackTrace()
                        activity.stopImageShimmer()
                    }
                }
            }
            if (it.status == Resource.Status.ERROR) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                if(it.errorNumber.equals("1100014",true)){
                    _sessionError.value = Event(true)
                }else {
                    toastMessage(it.errorMessage)
                    activity.stopImageShimmer()
                }
            }
        }
    }

    fun callUploadProfileImageApi(activity: EditProfileActivity, name:String, imageFile:File) = viewModelScope.launch(Dispatchers.Main) {
        val destPath: String = Utilities.getAppFolderLocation(context!!)
        var encodedImage =""
        try {
            val bytesFile = ByteArray(imageFile.length().toInt())
            context.contentResolver.openFileDescriptor(Uri.fromFile(imageFile), "r")?.use { parcelFileDescriptor ->
                FileInputStream(parcelFileDescriptor.fileDescriptor).use { inStream ->
                    inStream.read(bytesFile)
                    encodedImage = Base64.encodeToString(bytesFile, Base64.DEFAULT)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val PersonID = RequestBody.create("text/plain".toMediaTypeOrNull(), preferenceUtils.getPreference(PreferenceConstants.PERSONID, "0"))
        val FileName = RequestBody.create("text/plain".toMediaTypeOrNull(), name)
        val DocumentTypeCode = RequestBody.create("text/plain".toMediaTypeOrNull(), "PROFPIC")
        val ByteArray = RequestBody.create("text/plain".toMediaTypeOrNull(), encodedImage)
        val AuthTicket = RequestBody.create("text/plain".toMediaTypeOrNull(), authToken)

        _progressBar.value = Event(Constants.LOADER_UPLOAD)
        _uploadProfileImage.removeSource(uploadProfileImageSource)
        withContext(Dispatchers.IO) {
            uploadProfileImageSource = homeManagementUseCase.invokeUploadProfileImage(PersonID,FileName,DocumentTypeCode,ByteArray,AuthTicket)
        }
        _uploadProfileImage.addSource(uploadProfileImageSource) {
            try {
                _uploadProfileImage.value = it

                when (it.status) {
                    Resource.Status.SUCCESS -> {
                        _progressBar.value = Event(Event.HIDE_PROGRESS)
                        if(it.data!=null){
                            //val profileImageID = it.data.profileImageID?:""
                            val profileImageID = it.data.profileImageID
                            Utilities.printLog("UploadProfileImage----->$profileImageID")
                            if ( !Utilities.isNullOrEmptyOrZero(profileImageID) ) {
                                val oldProfilePic = File(activity.completeFilePath)
                                if ( oldProfilePic.exists() ) {
                                    Utilities.deleteFile(oldProfilePic)
                                }
                                activity.hasProfileImage = true
                                activity.needToSet = true
                                Utilities.toastMessageShort(context,localResource.getString(R.string.PROFILE_PHOTO_UPDATED))
                                updateUserProfileImgPath(name,destPath)
                                activity.completeFilePath = "$destPath/$name"
                                UserSingleton.getInstance()!!.profPicBitmap = BitmapFactory.decodeFile(activity.completeFilePath)
                                activity.isChanged = true
                                activity.setProfilePic()
                            }
                        }
                    }
                    Resource.Status.ERROR -> {
                        _progressBar.value = Event(Event.HIDE_PROGRESS)
                        if(it.errorNumber.equals("1100014",true)){
                            _sessionError.value = Event(true)
                        }else {
                            toastMessage(it.errorMessage)
                        }
                    }

                    else -> {}
                }
            }catch (e:Exception){
                Utilities.printException(e)
            }
        }
    }

    fun callRemoveProfileImageApi(activity: EditProfileActivity, context: Context) =
        viewModelScope.launch(Dispatchers.Main) {

            val requestData = RemoveProfileImageModel(Gson().toJson(RemoveProfileImageModel.JSONDataRequest(
                personID = preferenceUtils.getPreference(PreferenceConstants.PERSONID, "0").toInt()), RemoveProfileImageModel.JSONDataRequest::class.java), authToken)

            _progressBar.value = Event(Constants.LOADER_DELETE)
            _removeProfileImage.removeSource(removeProfileImageSource)
            withContext(Dispatchers.IO) {
                removeProfileImageSource = homeManagementUseCase.invokeRemoveProfileImage(isForceRefresh = true, data = requestData)
            }
            _removeProfileImage.addSource(removeProfileImageSource) {
                _removeProfileImage.value = it

                if (it.status == Resource.Status.SUCCESS) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    if (it.data != null) {
                        val isProcessed = it.data.isProcessed
                        Utilities.printLog("isProcessed----->$isProcessed")
                        if (isProcessed.equals(Constants.TRUE, ignoreCase = true)) {
                            activity.isChanged = true
                            val profilePic = File(activity.completeFilePath)
                            if ( profilePic.exists() ) {
                                Utilities.deleteFile(profilePic)
                            }
                            Utilities.toastMessageShort(context, context.resources.getString(R.string.PROFILE_PHOTO_REMOVED))
                            UserSingleton.getInstance()!!.clearData()
                            activity.removeProfilePic()
                        } else {
                            Utilities.toastMessageShort(context, context.resources.getString(R.string.ERROR_PROFILE_PHOTO))
                        }
                    }
                }
                if (it.status == Resource.Status.ERROR) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    if (it.errorNumber.equals("1100014", true)) {
                        _sessionError.value = Event(true)
                    } else {
                        toastMessage(it.errorMessage)
                    }
                }
            }
        }

    fun getLoggedInPersonDetails() = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            userDetails.postValue(homeManagementUseCase.invokeGetLoggedInPersonDetails())
        }
    }

    fun updateUserDetails(name: String,dob: String,personId: Int) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            homeManagementUseCase.invokeUpdateUserDetails(name,dob,personId)
            preferenceUtils.storePreference(PreferenceConstants.FIRSTNAME,name)
            preferenceUtils.storePreference(PreferenceConstants.DOB,dob.split("T").toTypedArray()[0])
        }
    }

    fun updateUserProfileImgPath(name: String, path: String) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            homeManagementUseCase.invokeUpdateUserProfileImgPath(name, path)
        }
    }

    fun getAllUserRelatives() = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            userRelativesList.postValue(homeManagementUseCase.invokeGetUserRelatives())
        }
    }

    fun getAllHealthRecordsDocuments() = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            val list = homeManagementUseCase.invokeGetAllHealthDocuments().filter { it.PersonId.toString() == preferenceUtils.getPreference(PreferenceConstants.PERSONID, "0") }
            allHealthDocuments.postValue(list)
        }
    }

    fun getRelativesList() = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            userRelativesList.postValue(homeManagementUseCase.invokeGetUserRelativesExceptSelf())
        }
    }

    fun getUserRelativeSpecific(relationShipCode: String) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            alreadyExistRelatives.postValue(homeManagementUseCase.invokeGetUserRelativeSpecific(relationShipCode))
        }
    }

    fun getUserRelativeForRelativeId(relativeId: String) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            alreadyExistRelatives.postValue(homeManagementUseCase.invokeGetUserRelativeForRelativeId(relativeId))
        }
    }


    fun getFamilyRelationshipList() = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            val user = homeManagementUseCase.invokeGetUserRelativeDetailsByRelativeId(preferenceUtils.getPreference(PreferenceConstants.PERSONID, "0"))
            val gender = if ( user == null ) {
                preferenceUtils.getPreference(PreferenceConstants.GENDER, "")
            } else {
                user.gender
            }
            Utilities.printLogError("Gender--->$gender")
            if (gender.contains("1", ignoreCase = true)) {
                familyRelationList.postValue(dataHandler.getFamilyRelationListMale())
            } else {
                familyRelationList.postValue(dataHandler.getFamilyRelationListFemale())
            }
        }
    }

    fun refreshPersonId() {
        personId = preferenceUtils.getPreference(PreferenceConstants.PERSONID, "0")
        gender = preferenceUtils.getPreference(PreferenceConstants.GENDER, "")
        relationshipCode = preferenceUtils.getPreference(PreferenceConstants.RELATIONSHIPCODE, "")
    }

    fun updateFirstName(firstName: String) {
        preferenceUtils.storePreference(PreferenceConstants.FIRSTNAME,firstName)
    }

    fun getUserPreference(key: String): String {
        val userPreference = preferenceUtils.getPreference(key)
        Utilities.printLogError("$key--->$userPreference")
        return userPreference
    }

    fun storeUserPreference(key: String, value: String) {
        Utilities.printLogError("Storing $key--->$value")
        preferenceUtils.storePreference(key, value)
    }

    fun showProgress() {
        _progressBar.value = Event("")
    }

    fun hideProgress() {
        _progressBar.value = Event(Event.HIDE_PROGRESS)
    }

    fun setUrlIntoView(url:String,view:ImageView) = viewModelScope.launch(Dispatchers.Main) {
        showProgress()
        Picasso.get()
            .load(url)
            .placeholder(R.drawable.img_my_profile) // Optional placeholder while loading
            .error(R.drawable.img_my_profile) // Optional error image if loading fails
            .into(view, object : Callback {
                override fun onSuccess() {
                    // Hide the loader when the image is successfully loaded
                    hideProgress()
                }
                override fun onError(e: Exception?) {
                    // Hide the loader even if there's an error
                    hideProgress()
                    e!!.printStackTrace()
                }
            })
    }

}