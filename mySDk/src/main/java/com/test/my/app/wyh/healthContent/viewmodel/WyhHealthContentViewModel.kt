package com.test.my.app.wyh.healthContent.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.test.my.app.R
import com.test.my.app.common.base.BaseViewModel
import com.test.my.app.common.constants.PreferenceConstants
import com.test.my.app.common.utils.Event
import com.test.my.app.common.utils.PreferenceUtils
import com.test.my.app.common.utils.Utilities
import com.test.my.app.model.wyh.healthContent.AddBlogReadingDurationModel
import com.test.my.app.model.wyh.healthContent.AddBookMarkModel
import com.test.my.app.model.wyh.healthContent.GetAllAudiosModel
import com.test.my.app.model.wyh.healthContent.GetAllBlogsModel
import com.test.my.app.model.wyh.healthContent.GetAllItemsModel
import com.test.my.app.model.wyh.healthContent.GetAllVideosModel
import com.test.my.app.model.wyh.healthContent.GetBlogModel
import com.test.my.app.repository.utils.Resource
import com.test.my.app.wyh.common.HealthContent
import com.test.my.app.wyh.domain.WyhManagementUseCase
import com.test.my.app.wyh.healthContent.ui.HealthContentDashboardActivity
import com.test.my.app.wyh.healthContent.ui.HealthContentDetailsActivity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
@SuppressLint("StaticFieldLeak")
class WyhHealthContentViewModel@Inject constructor(
    application: Application,
    private val wyhManagementUseCase: WyhManagementUseCase,
    private val preferenceUtils: PreferenceUtils,
    val context: Context?) : BaseViewModel(application) {

    private val visibleThreshold = 10
    var phone = preferenceUtils.getPreference(PreferenceConstants.PHONE, "")

    private var getAllBlogsSource: LiveData<Resource<GetAllBlogsModel.GetAllBlogsResponse>> = MutableLiveData()
    private val _getAllBlogs = MediatorLiveData<Resource<GetAllBlogsModel.GetAllBlogsResponse>>()
    val getAllBlogs: LiveData<Resource<GetAllBlogsModel.GetAllBlogsResponse>> get() = _getAllBlogs

    private var getAllVideosSource: LiveData<Resource<GetAllVideosModel.GetAllVideosResponse>> = MutableLiveData()
    private val _getAllVideos = MediatorLiveData<Resource<GetAllVideosModel.GetAllVideosResponse>>()
    val getAllVideos: LiveData<Resource<GetAllVideosModel.GetAllVideosResponse>> get() = _getAllVideos

    private var getAllAudiosSource: LiveData<Resource<GetAllAudiosModel.GetAllAudiosResponse>> = MutableLiveData()
    private val _getAllAudios = MediatorLiveData<Resource<GetAllAudiosModel.GetAllAudiosResponse>>()
    val getAllAudios: LiveData<Resource<GetAllAudiosModel.GetAllAudiosResponse>> get() = _getAllAudios

    private var getAllItemsSource: LiveData<Resource<GetAllItemsModel.GetAllItemsResponse>> = MutableLiveData()
    private val _getAllItems = MediatorLiveData<Resource<GetAllItemsModel.GetAllItemsResponse>>()
    val getAllItems: LiveData<Resource<GetAllItemsModel.GetAllItemsResponse>> get() = _getAllItems

    private var getWyhBlogSource: LiveData<Resource<GetBlogModel.GetBlogResponse>> = MutableLiveData()
    private val _getWyhBlog = MediatorLiveData<Resource<GetBlogModel.GetBlogResponse>>()
    val getWyhBlog: LiveData<Resource<GetBlogModel.GetBlogResponse>> get() = _getWyhBlog

    private var addBlogReadingDurationSource: LiveData<Resource<AddBlogReadingDurationModel.AddBlogReadingDurationResponse>> = MutableLiveData()
    private val _addBlogReadingDuration = MediatorLiveData<Resource<AddBlogReadingDurationModel.AddBlogReadingDurationResponse>>()
    val addBlogReadingDuration: LiveData<Resource<AddBlogReadingDurationModel.AddBlogReadingDurationResponse>> get() = _addBlogReadingDuration

    private var addBookMarkSource: LiveData<Resource<AddBookMarkModel.AddBookMarkResponse>> = MutableLiveData()
    private val _addBookMark = MediatorLiveData<Resource<AddBookMarkModel.AddBookMarkResponse>>()
    val addBookMark: LiveData<Resource<AddBookMarkModel.AddBookMarkResponse>> get() = _addBookMark

    fun callGetAllBlogsApi( page: Int, activity:HealthContentDashboardActivity,searchKey:String = "" ) = viewModelScope.launch(Dispatchers.Main) {

        val request = GetAllBlogsModel.GetAllBlogsRequest(
            startRow = page,
            pageSize = visibleThreshold,
            searchKey = searchKey
        )

        //_progressBar.value = Event("")
        _getAllBlogs.removeSource(getAllBlogsSource)
        withContext(Dispatchers.IO) {
            getAllBlogsSource = wyhManagementUseCase.invokeGetAllBlogs( request = request )
        }
        _getAllBlogs.addSource(getAllBlogsSource) {
            _getAllBlogs.value = it

            if (it.status == Resource.Status.SUCCESS) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                if ( it.data != null ) {
                    if ( it.data.success!! && it.data.data != null ) {
                        val list = it.data.data.data
                        //Utilities.printData("ResponseList",list)
                        val blogList: MutableList<HealthContent> = mutableListOf()
                        if ( list != null && list.isNotEmpty() ) {
                            for ( content in list ) {
                                val blogItem = HealthContent(
                                    id = content.id,
                                    contentImgUrl = content.imgPath,
                                    contentName = content.articleName,
                                    //contentDesc = content.articleName,
                                    contentUrl = content.articlePath,
                                    htmlContent = content.htmlContent,
                                    tags = content.tags,
                                    articleCode = content.articleCode,
                                    createdBy = content.createdBy,
                                    createdOn = content.createdOn,
                                    updatedBy = content.updatedBy,
                                    updatedOn = content.updatedOn,
                                    isActive = content.active
                                )
                                if (!blogList.contains(blogItem)) {
                                    blogList.add(blogItem)
                                }
                            }
                            activity.updateContentList(blogList)
                        }
                        activity.stopShimmer()
                    }
                }
            }
            if (it.status == Resource.Status.ERROR) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                toastMessage(it.errorMessage)
                activity.stopShimmer()
            }
        }

    }

    fun callGetAllVideosApi( page: Int, activity:HealthContentDashboardActivity,searchKey:String = "" ) = viewModelScope.launch(Dispatchers.Main) {

        val request = GetAllVideosModel.GetAllVideosRequest(
            startRow = page,
            pageSize = visibleThreshold,
            searchKey = searchKey
        )

        //_progressBar.value = Event("")
        _getAllVideos.removeSource(getAllVideosSource)
        withContext(Dispatchers.IO) {
            getAllVideosSource = wyhManagementUseCase.invokeGetAllVideos( request = request )
        }
        _getAllVideos.addSource(getAllVideosSource) {
            _getAllVideos.value = it

            if (it.status == Resource.Status.SUCCESS) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                if ( it.data != null ) {
                    if ( it.data.success!! && it.data.data != null ) {
                        val list = it.data.data.data
                        val blogList: MutableList<HealthContent> = mutableListOf()
                        if ( list != null && list.isNotEmpty() ) {
                            for ( content in list ) {
                                val blogItem = HealthContent(
                                    id = content.id,
                                    contentImgUrl = content.thumbnailImage,
                                    contentName = content.videoName,
                                    contentDesc = content.description,
                                    contentUrl = content.videoUrl,
                                    //htmlContent = content.htmlContent,
                                    productUrl = content.productUrl,
                                    createdBy = content.createdBy,
                                    createdOn = content.createdOn,
                                    updatedBy = content.modifiedBy,
                                    updatedOn = content.modifiedOn,
                                    isActive = content.isActive
                                )
                                if (!blogList.contains(blogItem)) {
                                    blogList.add(blogItem)
                                }
                            }
                            activity.updateContentList(blogList)
                        }
                        activity.stopShimmer()
                    }
                }
            }
            if (it.status == Resource.Status.ERROR) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                toastMessage(it.errorMessage)
                activity.stopShimmer()
            }
        }

    }

    fun callGetAllAudiosApi( page: Int, activity:HealthContentDashboardActivity,searchKey:String = "" ) = viewModelScope.launch(Dispatchers.Main) {

        val request = GetAllAudiosModel.GetAllAudiosRequest(
            startRow = page,
            pageSize = visibleThreshold,
            searchKey = searchKey
        )

        //_progressBar.value = Event("")
        _getAllAudios.removeSource(getAllAudiosSource)
        withContext(Dispatchers.IO) {
            getAllAudiosSource = wyhManagementUseCase.invokeGetAllAudios( request = request )
        }
        _getAllAudios.addSource(getAllAudiosSource) {
            _getAllAudios.value = it

            if (it.status == Resource.Status.SUCCESS) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                if ( it.data != null ) {
                    if ( it.data.success!! && it.data.data != null ) {
                        val list = it.data.data.data
                        //Utilities.printData("ResponseList",list)
                        val blogList: MutableList<HealthContent> = mutableListOf()
                        if ( list != null && list.isNotEmpty() ) {
                            for ( content in list ) {
                                val blogItem = HealthContent(
                                    id = content.id,
                                    contentImgUrl = content.thumbnailImage,
                                    contentName = content.audioName,
                                    contentDesc = content.description,
                                    contentUrl = content.audioUrl,
                                    //htmlContent = content.htmlContent,
                                    createdBy = content.createdBy,
                                    createdOn = content.createdOn,
                                    //updatedBy = content.updatedBy,
                                    //updatedOn = content.updatedOn,
                                    isActive = content.isActive
                                )
                                if (!blogList.contains(blogItem)) {
                                    blogList.add(blogItem)
                                }
                            }
                            activity.updateContentList(blogList)
                        }
                        activity.stopShimmer()
                    }
                }
            }
            if (it.status == Resource.Status.ERROR) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                toastMessage(it.errorMessage)
                activity.stopShimmer()
            }
        }

    }

    fun callGetAllItemsApi( page: Int, activity:HealthContentDashboardActivity ) = viewModelScope.launch(Dispatchers.Main) {

        val request = GetAllItemsModel.GetAllItemsRequest(
            itemType = "Quick Reads",
            pagenumber = 0
        )

        //_progressBar.value = Event("")
        _getAllItems.removeSource(getAllItemsSource)
        withContext(Dispatchers.IO) {
            getAllItemsSource = wyhManagementUseCase.invokeGetAllItems( request = request )
        }
        _getAllItems.addSource(getAllItemsSource) {
            _getAllItems.value = it

            if (it.status == Resource.Status.SUCCESS) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                if ( it.data != null ) {
                    if ( it.data.success!! && it.data.quickReadData != null ) {
                        val quickReadData = it.data.quickReadData
                        //Utilities.printData("AllItemsResponse",quickReadData)
                        activity.renderQuickReadSection(quickReadData)
                    }
                }
            }
            if (it.status == Resource.Status.ERROR) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                toastMessage(it.errorMessage)
            }
        }

    }

    fun callGetBlogApi( articleCode:String = "",
                        activity:HealthContentDashboardActivity ) = viewModelScope.launch(Dispatchers.Main) {

        val request = GetBlogModel.GetBlogRequest(
            articleCode = articleCode
        )

        _progressBar.value = Event("")
        _getWyhBlog.removeSource(getWyhBlogSource)
        withContext(Dispatchers.IO) {
            getWyhBlogSource = wyhManagementUseCase.invokeGetBlog( request = request )
        }
        _getWyhBlog.addSource(getWyhBlogSource) {
            _getWyhBlog.value = it

            if (it.status == Resource.Status.SUCCESS) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                if ( it.data != null ) {
                    if ( it.data.success!! && it.data.data != null ) {
                        val article = it.data.data
                        //Utilities.printData("ResponseList",list)
                        val blog = HealthContent(
                            id = article.articleID,
                            contentImgUrl = article.imgPath,
                            contentName = article.articleName,
                            //contentDesc = article.description,
                            contentUrl = article.articlePath,
                            htmlContent = article.htmlContent,
                            tags = article.tags,
                            articleCode = article.articleCode,
                            isBookMarked = article.isBookMarked!!,
                            isActive = if ( 1 == article.active ) true else false )

                        activity.viewHealthContentDetail(blog)
                    }
                }
            }
            if (it.status == Resource.Status.ERROR) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                toastMessage(it.errorMessage)
                activity.stopShimmer()
            }
        }

    }

    fun callAddBlogReadingDurationApi( readingDuration:Int,healthContent:HealthContent,
                                       activity: HealthContentDetailsActivity ) = viewModelScope.launch(Dispatchers.Main) {

        val request = AddBlogReadingDurationModel.AddBlogReadingDurationRequest(
            readingmins = readingDuration,
            articleCode = healthContent.articleCode,
            blogId = healthContent.id
        )

        //_progressBar.value = Event("")
        _addBlogReadingDuration.removeSource(addBlogReadingDurationSource)
        withContext(Dispatchers.IO) {
            addBlogReadingDurationSource = wyhManagementUseCase.invokeAddBlogReadingDuration( request = request )
        }
        _addBlogReadingDuration.addSource(addBlogReadingDurationSource) {
            _addBlogReadingDuration.value = it

            if (it.status == Resource.Status.SUCCESS) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                if ( it.data != null ) {
                    if ( it.data.success!! && it.data.data != null ) {
                        Utilities.printLogError("Duration Added")
                        //Utilities.toastMessageShort(context,"Duration Added")
                        //activity.finish()
                    }
                }
            }
            if (it.status == Resource.Status.ERROR) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                toastMessage(it.errorMessage)
                activity.finish()
            }
        }

    }

    fun callAddBookMarkApi( healthContent:HealthContent,isBookMarked:Boolean,
                            activity: HealthContentDetailsActivity ) = viewModelScope.launch(Dispatchers.Main) {

        val request = AddBookMarkModel.AddBookMarkRequest(
            articleCode = healthContent.articleCode,
            isBookMarked = isBookMarked
        )

        _progressBar.value = Event("")
        _addBookMark.removeSource(addBookMarkSource)
        withContext(Dispatchers.IO) {
            addBookMarkSource = wyhManagementUseCase.invokeWhyAddBookMark( request = request )
        }
        _addBookMark.addSource(addBookMarkSource) {
            _addBookMark.value = it

            if (it.status == Resource.Status.SUCCESS) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                if ( it.data != null ) {
                    if ( it.data.success!! ) {
                        activity.isBookMarkChanged = true
                        Utilities.printLogError("BookMarked--->${healthContent.articleCode}")
                        if ( isBookMarked ) {
                            HealthContentDetailsActivity.healthContent.isBookMarked = true
                            Utilities.toastMessageShort(context,"BookMarked Successfully")
                            activity.binding.imgBookMark.setImageResource(R.drawable.ic_bookmark_selected)
                        } else {
                            HealthContentDetailsActivity.healthContent.isBookMarked = false
                            Utilities.toastMessageShort(context,"BookMark Removed")
                            activity.binding.imgBookMark.setImageResource(R.drawable.ic_bookmark_unselected)
                        }
                    }
                }
            }
            if (it.status == Resource.Status.ERROR) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                toastMessage(it.errorMessage)
            }
        }

    }

    fun showProgress() {
        _progressBar.value = Event("")
    }

    fun hideProgress() {
        _progressBar.value = Event(Event.HIDE_PROGRESS)
    }

}