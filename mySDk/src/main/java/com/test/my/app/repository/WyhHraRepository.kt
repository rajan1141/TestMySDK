package com.test.my.app.repository

import android.content.Context
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
import com.test.my.app.model.wyh.ira.GetIraHistoryModel
import com.test.my.app.model.wyh.ira.GetIraAnswersModel
import com.test.my.app.model.wyh.ira.SaveIraAnswersModel
import com.test.my.app.remote.WyhDatasource
import com.test.my.app.repository.utils.NetworkDataBoundResource
import com.test.my.app.repository.utils.Resource
import javax.inject.Inject

interface WyhHraRepository {

    suspend fun whyGetWyhAuthorization(mobile: String): LiveData<Resource<WyhAuthorizationModel.WyhAuthorizationResponse>>

    suspend fun whyGetSocketKeys(): LiveData<Resource<GetSocketKeysModel.GetSocketKeysResponse>>

    suspend fun whyAddFaceScanVitals(request: AddFaceScanVitalsModel.AddFaceScanVitalsRequest): LiveData<Resource<AddFaceScanVitalsModel.AddFaceScanVitalsResponse>>

    suspend fun whyGetFaceScanVitals(): LiveData<Resource<GetFaceScanVitalsModel.GetFaceScanVitalsResponse>>

    suspend fun whyGetAllBlogs(request: GetAllBlogsModel.GetAllBlogsRequest): LiveData<Resource<GetAllBlogsModel.GetAllBlogsResponse>>

    suspend fun whyGetAllVideos(request: GetAllVideosModel.GetAllVideosRequest): LiveData<Resource<GetAllVideosModel.GetAllVideosResponse>>

    suspend fun whyGetAllAudios(request: GetAllAudiosModel.GetAllAudiosRequest): LiveData<Resource<GetAllAudiosModel.GetAllAudiosResponse>>

    suspend fun whyGetAllItems(request: GetAllItemsModel.GetAllItemsRequest): LiveData<Resource<GetAllItemsModel.GetAllItemsResponse>>

    suspend fun whyGetBlog(request: GetBlogModel.GetBlogRequest): LiveData<Resource<GetBlogModel.GetBlogResponse>>

    suspend fun whyAddBlogReadingDuration(request: AddBlogReadingDurationModel.AddBlogReadingDurationRequest): LiveData<Resource<AddBlogReadingDurationModel.AddBlogReadingDurationResponse>>

    suspend fun whyAddBookMark(request: AddBookMarkModel.AddBookMarkRequest): LiveData<Resource<AddBookMarkModel.AddBookMarkResponse>>

    suspend fun whyGetIRAHistory(request: GetIraHistoryModel.GetIRAHistoryRequest): LiveData<Resource<GetIraHistoryModel.GetIRAHistoryResponse>>

    suspend fun whyIraCreateConversation(request: CreateIraConversationModel.CreateIraConversationRequest): LiveData<Resource<CreateIraConversationModel.CreateIraConversationResponse>>

    suspend fun whyGetIraAnswers(request: GetIraAnswersModel.GetIraAnswersRequest): LiveData<Resource<GetIraAnswersModel.GetIraAnswersResponse>>

    suspend fun whySaveIraAnswers(request: SaveIraAnswersModel.SaveIraAnswersRequest): LiveData<Resource<SaveIraAnswersModel.SaveIraAnswersResponse>>

    suspend fun whyCreateHraConversation(request: CreateConversationModel.CreateConversationRequest): LiveData<Resource<CreateConversationModel.CreateConversationResponse>>

    suspend fun whyGetHraAnswers(request: GetHraAnswersModel.GetHraAnswersRequest): LiveData<Resource<GetHraAnswersModel.GetHraAnswersResponse>>

    suspend fun whySaveHraAnswers(request: SaveHraAnswersModel.SaveHraAnswersRequest): LiveData<Resource<SaveHraAnswersModel.SaveHraAnswersResponse>>

    suspend fun whySaveHraAnalysis(request: SaveHraAnalysisModel.SaveHraAnalysisRequest): LiveData<Resource<SaveHraAnalysisModel.SaveHraAnalysisResponse>>

    suspend fun whyGetHraAnalysis(request: GetHraAnalysisModel.GetHraAnalysisRequest): LiveData<Resource<GetHraAnalysisModel.GetHraAnalysisResponse>>
}

class WyhHraRepositoryImpl @Inject constructor(private val datasource: WyhDatasource,
                                               val context: Context) : WyhHraRepository {

    override suspend fun whyGetWyhAuthorization(mobile: String): LiveData<Resource<WyhAuthorizationModel.WyhAuthorizationResponse>> {

        return object : NetworkDataBoundResource<WyhAuthorizationModel.WyhAuthorizationResponse, WyhAuthorizationModel.WyhAuthorizationResponse>(context) {

            override fun processResponse(response: WyhAuthorizationModel.WyhAuthorizationResponse): WyhAuthorizationModel.WyhAuthorizationResponse {
                return response
            }

            override suspend fun createCallAsync(): WyhAuthorizationModel.WyhAuthorizationResponse {
                return datasource.getWyhAuthorization(mobile)
            }

        }.build().asLiveData()

    }

    override suspend fun whyGetSocketKeys(): LiveData<Resource<GetSocketKeysModel.GetSocketKeysResponse>> {

        return object : NetworkDataBoundResource<GetSocketKeysModel.GetSocketKeysResponse,GetSocketKeysModel.GetSocketKeysResponse>(context) {

            override fun processResponse(response:GetSocketKeysModel.GetSocketKeysResponse): GetSocketKeysModel.GetSocketKeysResponse {
                return response
            }

            override suspend fun createCallAsync(): GetSocketKeysModel.GetSocketKeysResponse {
                return datasource.whyGetSocketKeys()
            }

        }.build().asLiveData()

    }

    override suspend fun whyAddFaceScanVitals(request: AddFaceScanVitalsModel.AddFaceScanVitalsRequest): LiveData<Resource<AddFaceScanVitalsModel.AddFaceScanVitalsResponse>> {

        return object : NetworkDataBoundResource<AddFaceScanVitalsModel.AddFaceScanVitalsResponse,AddFaceScanVitalsModel.AddFaceScanVitalsResponse>(context) {

            override fun processResponse(response: AddFaceScanVitalsModel.AddFaceScanVitalsResponse): AddFaceScanVitalsModel.AddFaceScanVitalsResponse {
                return response
            }

            override suspend fun createCallAsync(): AddFaceScanVitalsModel.AddFaceScanVitalsResponse {
                return datasource.whyAddFaceScanVitals(request)
            }

        }.build().asLiveData()

    }

    override suspend fun whyGetFaceScanVitals(): LiveData<Resource<GetFaceScanVitalsModel.GetFaceScanVitalsResponse>> {

        return object : NetworkDataBoundResource<GetFaceScanVitalsModel.GetFaceScanVitalsResponse,GetFaceScanVitalsModel.GetFaceScanVitalsResponse>(context) {

            override fun processResponse(response: GetFaceScanVitalsModel.GetFaceScanVitalsResponse): GetFaceScanVitalsModel.GetFaceScanVitalsResponse {
                return response
            }

            override suspend fun createCallAsync(): GetFaceScanVitalsModel.GetFaceScanVitalsResponse {
                return datasource.whyGetFaceScanVitals()
            }

        }.build().asLiveData()

    }

    override suspend fun whyGetAllBlogs(request: GetAllBlogsModel.GetAllBlogsRequest): LiveData<Resource<GetAllBlogsModel.GetAllBlogsResponse>> {

        return object : NetworkDataBoundResource<GetAllBlogsModel.GetAllBlogsResponse,GetAllBlogsModel.GetAllBlogsResponse>(context) {

            override fun processResponse(response: GetAllBlogsModel.GetAllBlogsResponse): GetAllBlogsModel.GetAllBlogsResponse {
                return response
            }

            override suspend fun createCallAsync(): GetAllBlogsModel.GetAllBlogsResponse {
                return datasource.whyGetAllBlogs(request)
            }

        }.build().asLiveData()
    }

    override suspend fun whyGetAllVideos(request: GetAllVideosModel.GetAllVideosRequest): LiveData<Resource<GetAllVideosModel.GetAllVideosResponse>> {

        return object : NetworkDataBoundResource<GetAllVideosModel.GetAllVideosResponse,GetAllVideosModel.GetAllVideosResponse>(context) {

            override fun processResponse(response: GetAllVideosModel.GetAllVideosResponse): GetAllVideosModel.GetAllVideosResponse {
                return response
            }

            override suspend fun createCallAsync(): GetAllVideosModel.GetAllVideosResponse {
                return datasource.whyGetAllVideos(request)
            }

        }.build().asLiveData()
    }

    override suspend fun whyGetAllAudios(request: GetAllAudiosModel.GetAllAudiosRequest): LiveData<Resource<GetAllAudiosModel.GetAllAudiosResponse>> {

        return object : NetworkDataBoundResource<GetAllAudiosModel.GetAllAudiosResponse,GetAllAudiosModel.GetAllAudiosResponse>(context) {

            override fun processResponse(response: GetAllAudiosModel.GetAllAudiosResponse): GetAllAudiosModel.GetAllAudiosResponse {
                return response
            }

            override suspend fun createCallAsync(): GetAllAudiosModel.GetAllAudiosResponse {
                return datasource.whyGetAllAudios(request)
            }

        }.build().asLiveData()
    }

    override suspend fun whyGetAllItems(request: GetAllItemsModel.GetAllItemsRequest): LiveData<Resource<GetAllItemsModel.GetAllItemsResponse>> {

        return object : NetworkDataBoundResource<GetAllItemsModel.GetAllItemsResponse,GetAllItemsModel.GetAllItemsResponse>(context) {

            override fun processResponse(response: GetAllItemsModel.GetAllItemsResponse): GetAllItemsModel.GetAllItemsResponse {
                return response
            }

            override suspend fun createCallAsync(): GetAllItemsModel.GetAllItemsResponse {
                return datasource.whyGetAllItems(request)
            }

        }.build().asLiveData()

    }

    override suspend fun whyGetBlog(request: GetBlogModel.GetBlogRequest): LiveData<Resource<GetBlogModel.GetBlogResponse>> {

        return object : NetworkDataBoundResource<GetBlogModel.GetBlogResponse,GetBlogModel.GetBlogResponse>(context) {

            override fun processResponse(response: GetBlogModel.GetBlogResponse): GetBlogModel.GetBlogResponse {
                return response
            }

            override suspend fun createCallAsync(): GetBlogModel.GetBlogResponse {
                return datasource.whyGetBlog(request)
            }

        }.build().asLiveData()

    }

    override suspend fun whyAddBlogReadingDuration(request: AddBlogReadingDurationModel.AddBlogReadingDurationRequest): LiveData<Resource<AddBlogReadingDurationModel.AddBlogReadingDurationResponse>> {

        return object : NetworkDataBoundResource<AddBlogReadingDurationModel.AddBlogReadingDurationResponse,AddBlogReadingDurationModel.AddBlogReadingDurationResponse>(context) {

            override fun processResponse(response: AddBlogReadingDurationModel.AddBlogReadingDurationResponse): AddBlogReadingDurationModel.AddBlogReadingDurationResponse {
                return response
            }

            override suspend fun createCallAsync(): AddBlogReadingDurationModel.AddBlogReadingDurationResponse {
                return datasource.whyAddBlogReadingDuration(request)
            }

        }.build().asLiveData()

    }

    override suspend fun whyAddBookMark(request: AddBookMarkModel.AddBookMarkRequest): LiveData<Resource<AddBookMarkModel.AddBookMarkResponse>> {

        return object : NetworkDataBoundResource<AddBookMarkModel.AddBookMarkResponse,AddBookMarkModel.AddBookMarkResponse>(context) {

            override fun processResponse(response: AddBookMarkModel.AddBookMarkResponse): AddBookMarkModel.AddBookMarkResponse {
                return response
            }

            override suspend fun createCallAsync(): AddBookMarkModel.AddBookMarkResponse {
                return datasource.whyAddBookMark(request)
            }

        }.build().asLiveData()

    }

    override suspend fun whyGetIRAHistory(request: GetIraHistoryModel.GetIRAHistoryRequest): LiveData<Resource<GetIraHistoryModel.GetIRAHistoryResponse>> {

        return object : NetworkDataBoundResource<GetIraHistoryModel.GetIRAHistoryResponse,GetIraHistoryModel.GetIRAHistoryResponse>(context) {

            override fun processResponse(response: GetIraHistoryModel.GetIRAHistoryResponse): GetIraHistoryModel.GetIRAHistoryResponse {
                return response
            }

            override suspend fun createCallAsync(): GetIraHistoryModel.GetIRAHistoryResponse {
                return datasource.whyGetIRAHistory(request)
            }

        }.build().asLiveData()

    }

    override suspend fun whyIraCreateConversation(request: CreateIraConversationModel.CreateIraConversationRequest): LiveData<Resource<CreateIraConversationModel.CreateIraConversationResponse>> {

        return object : NetworkDataBoundResource<CreateIraConversationModel.CreateIraConversationResponse,CreateIraConversationModel.CreateIraConversationResponse>(context) {

            override fun processResponse(response: CreateIraConversationModel.CreateIraConversationResponse): CreateIraConversationModel.CreateIraConversationResponse {
                return response
            }

            override suspend fun createCallAsync(): CreateIraConversationModel.CreateIraConversationResponse {
                return datasource.whyIraCreateConversation(request)
            }

        }.build().asLiveData()

    }

    override suspend fun whyGetIraAnswers(request: GetIraAnswersModel.GetIraAnswersRequest): LiveData<Resource<GetIraAnswersModel.GetIraAnswersResponse>> {

        return object : NetworkDataBoundResource<GetIraAnswersModel.GetIraAnswersResponse,GetIraAnswersModel.GetIraAnswersResponse>(context) {

            override fun processResponse(response: GetIraAnswersModel.GetIraAnswersResponse): GetIraAnswersModel.GetIraAnswersResponse {
                return response
            }

            override suspend fun createCallAsync(): GetIraAnswersModel.GetIraAnswersResponse {
                return datasource.whyGetIraAnswers(request)
            }

        }.build().asLiveData()

    }

    override suspend fun whySaveIraAnswers(request: SaveIraAnswersModel.SaveIraAnswersRequest): LiveData<Resource<SaveIraAnswersModel.SaveIraAnswersResponse>> {

        return object : NetworkDataBoundResource<SaveIraAnswersModel.SaveIraAnswersResponse,SaveIraAnswersModel.SaveIraAnswersResponse>(context) {

            override fun processResponse(response: SaveIraAnswersModel.SaveIraAnswersResponse): SaveIraAnswersModel.SaveIraAnswersResponse {
                return response
            }

            override suspend fun createCallAsync(): SaveIraAnswersModel.SaveIraAnswersResponse {
                return datasource.whySaveIraAnswers(request)
            }

        }.build().asLiveData()

    }

    override suspend fun whyCreateHraConversation(request: CreateConversationModel.CreateConversationRequest): LiveData<Resource<CreateConversationModel.CreateConversationResponse>> {

        return object : NetworkDataBoundResource<CreateConversationModel.CreateConversationResponse,CreateConversationModel.CreateConversationResponse>(context) {

            override fun processResponse(response: CreateConversationModel.CreateConversationResponse): CreateConversationModel.CreateConversationResponse {
                return response
            }

            override suspend fun createCallAsync(): CreateConversationModel.CreateConversationResponse {
                return datasource.whyCreateHraConversation(request)
            }

        }.build().asLiveData()

    }

    override suspend fun whyGetHraAnswers(request: GetHraAnswersModel.GetHraAnswersRequest): LiveData<Resource<GetHraAnswersModel.GetHraAnswersResponse>> {

        return object : NetworkDataBoundResource<GetHraAnswersModel.GetHraAnswersResponse,GetHraAnswersModel.GetHraAnswersResponse>(context) {

            override fun processResponse(response: GetHraAnswersModel.GetHraAnswersResponse): GetHraAnswersModel.GetHraAnswersResponse {
                return response
            }

            override suspend fun createCallAsync(): GetHraAnswersModel.GetHraAnswersResponse {
                return datasource.whyGetHraAnswers(request)
            }

        }.build().asLiveData()

    }

    override suspend fun whySaveHraAnswers(request: SaveHraAnswersModel.SaveHraAnswersRequest): LiveData<Resource<SaveHraAnswersModel.SaveHraAnswersResponse>> {

        return object : NetworkDataBoundResource<SaveHraAnswersModel.SaveHraAnswersResponse,SaveHraAnswersModel.SaveHraAnswersResponse>(context) {

            override fun processResponse(response: SaveHraAnswersModel.SaveHraAnswersResponse): SaveHraAnswersModel.SaveHraAnswersResponse {
                return response
            }

            override suspend fun createCallAsync(): SaveHraAnswersModel.SaveHraAnswersResponse {
                return datasource.whySaveHraAnswers(request)
            }

        }.build().asLiveData()

    }

    override suspend fun whySaveHraAnalysis(request: SaveHraAnalysisModel.SaveHraAnalysisRequest): LiveData<Resource<SaveHraAnalysisModel.SaveHraAnalysisResponse>> {

        return object : NetworkDataBoundResource<SaveHraAnalysisModel.SaveHraAnalysisResponse,SaveHraAnalysisModel.SaveHraAnalysisResponse>(context) {

            override fun processResponse(response: SaveHraAnalysisModel.SaveHraAnalysisResponse): SaveHraAnalysisModel.SaveHraAnalysisResponse {
                return response
            }

            override suspend fun createCallAsync(): SaveHraAnalysisModel.SaveHraAnalysisResponse {
                return datasource.whySaveHraAnalysis(request)
            }

        }.build().asLiveData()

    }

    override suspend fun whyGetHraAnalysis(request: GetHraAnalysisModel.GetHraAnalysisRequest): LiveData<Resource<GetHraAnalysisModel.GetHraAnalysisResponse>> {

        return object : NetworkDataBoundResource<GetHraAnalysisModel.GetHraAnalysisResponse,GetHraAnalysisModel.GetHraAnalysisResponse>(context) {

            override fun processResponse(response: GetHraAnalysisModel.GetHraAnalysisResponse): GetHraAnalysisModel.GetHraAnalysisResponse {
                return response
            }

            override suspend fun createCallAsync(): GetHraAnalysisModel.GetHraAnalysisResponse {
                return datasource.whyGetHraAnalysis(request)
            }

        }.build().asLiveData()

    }

}