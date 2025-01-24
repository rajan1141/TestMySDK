package com.test.my.app.remote

import com.test.my.app.di.DIModule
import com.test.my.app.model.shr.DeleteDocumentModel
import com.test.my.app.model.shr.DownloadDocumentModel
import com.test.my.app.model.shr.ListDocumentTypesModel
import com.test.my.app.model.shr.ListDocumentsModel
import com.test.my.app.model.shr.ListRelativesModel
import com.test.my.app.model.shr.OCRSaveModel
import com.test.my.app.model.shr.OCRUnitExistModel
import com.test.my.app.model.shr.SaveDocumentModel
import okhttp3.RequestBody
import javax.inject.Inject
import javax.inject.Named

class ShrDatasource @Inject constructor(
    @Named(DIModule.DEFAULT) private val defaultService: ApiService,
    @Named(DIModule.ENCRYPTED) private val encryptedService: ApiService
) {

    suspend fun getDocumentTypeResponse(data: ListDocumentTypesModel) =
        encryptedService.fetchDocumentTypeApi(data)

    suspend fun getDocumentListResponse(data: ListDocumentsModel) =
        encryptedService.fetchDocumentListApi(data)

    suspend fun getRelativesListResponse(data: ListRelativesModel) =
        encryptedService.fetchRelativesListApi(data)

    suspend fun saveRecordToServerResponse(data: SaveDocumentModel) =
        encryptedService.saveRecordToServerApi(data)

    suspend fun deleteRecordsFromServerResponse(data: DeleteDocumentModel) =
        encryptedService.deleteRecordsFromServerApi(data)

    suspend fun downloadDocumentFromServerResponse(data: DownloadDocumentModel) =
        encryptedService.downloadDocumentFromServerApi(data)

    suspend fun getUnitExistResponse(data: OCRUnitExistModel) =
        encryptedService.checkUnitExist(data)

    suspend fun ocrSaveDocumentResponse(data: OCRSaveModel) = encryptedService.ocrSaveDocument(data)

    suspend fun ocrDigitizeDocument(
        fileBytes: RequestBody,
        partnerCode: RequestBody,
        fileExtension: RequestBody
    ) = defaultService.ocrDigitizeDocument(fileBytes, partnerCode, fileExtension)
}