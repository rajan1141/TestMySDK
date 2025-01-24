package com.test.my.app.remote

import com.test.my.app.di.DIModule.WYH
import com.test.my.app.model.wyh.faceScan.AddFaceScanVitalsModel
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
import javax.inject.Inject
import javax.inject.Named

class WyhDatasource@Inject constructor(@Named(WYH)  private val wyhService: ApiService) {

    suspend fun getWyhAuthorization(mobile: String) = wyhService.getWyhAuthorization(mobile=mobile)

    suspend fun whyGetSocketKeys() = wyhService.getSocketKeys()

    suspend fun whyAddFaceScanVitals(request: AddFaceScanVitalsModel.AddFaceScanVitalsRequest) = wyhService.whyAddFaceScanVitals( request=request )

    suspend fun whyGetFaceScanVitals() = wyhService.whyGetFaceScanVitals()

    suspend fun whyGetAllBlogs(request: GetAllBlogsModel.GetAllBlogsRequest) = wyhService.whyGetAllBlogs( request=request )

    suspend fun whyGetAllVideos(request: GetAllVideosModel.GetAllVideosRequest) = wyhService.whyGetAllVideos( request=request )

    suspend fun whyGetAllAudios(request: GetAllAudiosModel.GetAllAudiosRequest) = wyhService.whyGetAllAudios( request=request )

    suspend fun whyGetAllItems(request: GetAllItemsModel.GetAllItemsRequest) = wyhService.whyGetAllItems( request=request )

    suspend fun whyGetBlog(request: GetBlogModel.GetBlogRequest) = wyhService.whyGetBlog( request=request )

    suspend fun whyAddBlogReadingDuration(request: AddBlogReadingDurationModel.AddBlogReadingDurationRequest) = wyhService.whyAddBlogReadingDuration( request=request )

    suspend fun whyAddBookMark(request: AddBookMarkModel.AddBookMarkRequest) = wyhService.whyAddBookMark( request=request )

    suspend fun whyGetIRAHistory(request: GetIraHistoryModel.GetIRAHistoryRequest) = wyhService.whyGetIRAHistory( request=request )

    suspend fun whyIraCreateConversation(request: CreateIraConversationModel.CreateIraConversationRequest) = wyhService.whyIraCreateConversation( request=request )

    suspend fun whyGetIraAnswers(request: GetIraAnswersModel.GetIraAnswersRequest) = wyhService.whyGetIraAnswers( request=request )

    suspend fun whySaveIraAnswers(request: SaveIraAnswersModel.SaveIraAnswersRequest) = wyhService.whySaveIraAnswers( request=request )


    suspend fun whyCreateHraConversation(request: CreateConversationModel.CreateConversationRequest) = wyhService.whyCreateHraConversation(request=request)

    suspend fun whyGetHraAnswers(request: GetHraAnswersModel.GetHraAnswersRequest) = wyhService.whyGetHraAnswers(request=request)

    suspend fun whySaveHraAnswers(request: SaveHraAnswersModel.SaveHraAnswersRequest) = wyhService.whySaveHraAnswers(request=request)

    suspend fun whySaveHraAnalysis(request: SaveHraAnalysisModel.SaveHraAnalysisRequest) = wyhService.whySaveHraAnalysis(request=request)

    suspend fun whyGetHraAnalysis(request: GetHraAnalysisModel.GetHraAnalysisRequest) = wyhService.whyGetHraAnalysis(request=request)
}