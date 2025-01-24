package com.test.my.app.wyh.domain

import androidx.lifecycle.LiveData
import com.test.my.app.model.wyh.WyhAuthorizationModel
import com.test.my.app.model.wyh.faceScan.AddFaceScanVitalsModel
import com.test.my.app.model.wyh.faceScan.GetFaceScanVitalsModel
import com.test.my.app.model.wyh.faceScan.GetSocketKeysModel
import com.test.my.app.model.wyh.healthContent.AddBlogReadingDurationModel
import com.test.my.app.model.wyh.healthContent.AddBookMarkModel
import com.test.my.app.model.wyh.healthContent.GetAllAudiosModel
import com.test.my.app.model.wyh.healthContent.GetAllBlogsModel
import com.test.my.app.model.wyh.healthContent.GetAllItemsModel
import com.test.my.app.model.wyh.healthContent.GetAllVideosModel
import com.test.my.app.model.wyh.healthContent.GetBlogModel
import com.test.my.app.model.wyh.hra.CreateConversationModel
import com.test.my.app.model.wyh.hra.GetHraAnalysisModel
import com.test.my.app.model.wyh.hra.GetHraAnswersModel
import com.test.my.app.model.wyh.hra.SaveHraAnalysisModel
import com.test.my.app.model.wyh.hra.SaveHraAnswersModel
import com.test.my.app.model.wyh.ira.CreateIraConversationModel
import com.test.my.app.model.wyh.ira.GetIraAnswersModel
import com.test.my.app.model.wyh.ira.GetIraHistoryModel
import com.test.my.app.model.wyh.ira.SaveIraAnswersModel
import com.test.my.app.repository.WyhHraRepository
import com.test.my.app.repository.utils.Resource
import javax.inject.Inject

class WyhManagementUseCase@Inject constructor(private val wyhHraRepository: WyhHraRepository) {

    suspend fun invokeGetWyhAuthorization(mobile: String): LiveData<Resource<WyhAuthorizationModel.WyhAuthorizationResponse>> {

        return wyhHraRepository.whyGetWyhAuthorization(mobile)
    }

    suspend fun invokeGetSocketKeys(): LiveData<Resource<GetSocketKeysModel.GetSocketKeysResponse>> {

        return wyhHraRepository.whyGetSocketKeys()
    }

    suspend fun invokeAddFaceScanVitals(request: AddFaceScanVitalsModel.AddFaceScanVitalsRequest): LiveData<Resource<AddFaceScanVitalsModel.AddFaceScanVitalsResponse>> {

        return wyhHraRepository.whyAddFaceScanVitals(request)
    }

    suspend fun invokeGetFaceScanVitals(): LiveData<Resource<GetFaceScanVitalsModel.GetFaceScanVitalsResponse>> {

        return wyhHraRepository.whyGetFaceScanVitals()
    }

    suspend fun invokeGetAllBlogs(request: GetAllBlogsModel.GetAllBlogsRequest): LiveData<Resource<GetAllBlogsModel.GetAllBlogsResponse>> {

        return wyhHraRepository.whyGetAllBlogs(request)
    }

    suspend fun invokeGetAllVideos(request: GetAllVideosModel.GetAllVideosRequest): LiveData<Resource<GetAllVideosModel.GetAllVideosResponse>> {

        return wyhHraRepository.whyGetAllVideos(request)
    }

    suspend fun invokeGetAllAudios(request: GetAllAudiosModel.GetAllAudiosRequest): LiveData<Resource<GetAllAudiosModel.GetAllAudiosResponse>> {

        return wyhHraRepository.whyGetAllAudios(request)
    }

    suspend fun invokeGetAllItems(request: GetAllItemsModel.GetAllItemsRequest): LiveData<Resource<GetAllItemsModel.GetAllItemsResponse>> {

        return wyhHraRepository.whyGetAllItems(request)
    }

    suspend fun invokeGetBlog(request: GetBlogModel.GetBlogRequest): LiveData<Resource<GetBlogModel.GetBlogResponse>> {

        return wyhHraRepository.whyGetBlog(request)
    }

    suspend fun invokeAddBlogReadingDuration(request: AddBlogReadingDurationModel.AddBlogReadingDurationRequest): LiveData<Resource<AddBlogReadingDurationModel.AddBlogReadingDurationResponse>> {

        return wyhHraRepository.whyAddBlogReadingDuration(request)
    }

    suspend fun invokeWhyAddBookMark(request: AddBookMarkModel.AddBookMarkRequest): LiveData<Resource<AddBookMarkModel.AddBookMarkResponse>> {

        return wyhHraRepository.whyAddBookMark(request)
    }

    suspend fun invokeWhyGetIRAHistory(request: GetIraHistoryModel.GetIRAHistoryRequest): LiveData<Resource<GetIraHistoryModel.GetIRAHistoryResponse>> {

        return wyhHraRepository.whyGetIRAHistory(request)
    }

    suspend fun invokeWhyIraCreateConversation(request: CreateIraConversationModel.CreateIraConversationRequest): LiveData<Resource<CreateIraConversationModel.CreateIraConversationResponse>> {

        return wyhHraRepository.whyIraCreateConversation(request)
    }

    suspend fun invokeWhyGetIraAnswers(request: GetIraAnswersModel.GetIraAnswersRequest): LiveData<Resource<GetIraAnswersModel.GetIraAnswersResponse>> {

        return wyhHraRepository.whyGetIraAnswers(request)
    }

    suspend fun invokeWhySaveIraAnswers(request: SaveIraAnswersModel.SaveIraAnswersRequest): LiveData<Resource<SaveIraAnswersModel.SaveIraAnswersResponse>> {

        return wyhHraRepository.whySaveIraAnswers(request)
    }



    suspend fun invokeWhyCreateHraConversation(request: CreateConversationModel.CreateConversationRequest): LiveData<Resource<CreateConversationModel.CreateConversationResponse>> {

        return wyhHraRepository.whyCreateHraConversation(request)
    }

    suspend fun invokeWhyGetHraAnswers(request: GetHraAnswersModel.GetHraAnswersRequest): LiveData<Resource<GetHraAnswersModel.GetHraAnswersResponse>> {

        return wyhHraRepository.whyGetHraAnswers(request)
    }

    suspend fun invokeWhySaveHraAnswers(request: SaveHraAnswersModel.SaveHraAnswersRequest): LiveData<Resource<SaveHraAnswersModel.SaveHraAnswersResponse>> {

        return wyhHraRepository.whySaveHraAnswers(request)
    }

    suspend fun invokeWhySaveHraAnalysis(request: SaveHraAnalysisModel.SaveHraAnalysisRequest): LiveData<Resource<SaveHraAnalysisModel.SaveHraAnalysisResponse>> {

        return wyhHraRepository.whySaveHraAnalysis(request)
    }

    suspend fun invokeWhyGetHraAnalysis(request: GetHraAnalysisModel.GetHraAnalysisRequest): LiveData<Resource<GetHraAnalysisModel.GetHraAnalysisResponse>> {

        return wyhHraRepository.whyGetHraAnalysis(request)
    }

}