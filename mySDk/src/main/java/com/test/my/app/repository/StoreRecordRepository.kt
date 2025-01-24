package com.test.my.app.repository

import android.content.Context
import android.net.Uri
import android.util.Base64
import androidx.lifecycle.LiveData
import com.test.my.app.common.constants.ApiConstants
import com.test.my.app.common.constants.Constants
import com.test.my.app.common.utils.DateHelper
import com.test.my.app.common.utils.FileUtils
import com.test.my.app.common.utils.Utilities
import com.test.my.app.local.dao.DataSyncMasterDao
import com.test.my.app.local.dao.StoreRecordsDao
import com.test.my.app.local.dao.VivantUserDao
import com.test.my.app.model.entity.DataSyncMaster
import com.test.my.app.model.entity.DocumentType
import com.test.my.app.model.entity.HealthDocument
import com.test.my.app.model.entity.RecordInSession
import com.test.my.app.model.entity.UserRelatives
import com.test.my.app.model.shr.DeleteDocumentModel
import com.test.my.app.model.shr.DownloadDocumentModel
import com.test.my.app.model.shr.ListDocumentTypesModel
import com.test.my.app.model.shr.ListDocumentsModel
import com.test.my.app.model.shr.ListRelativesModel
import com.test.my.app.model.shr.OCRSaveModel
import com.test.my.app.model.shr.OCRUnitExistModel
import com.test.my.app.model.shr.OcrResponce
import com.test.my.app.model.shr.SaveDocumentModel
import com.test.my.app.remote.ShrDatasource
import com.test.my.app.repository.utils.NetworkBoundResource
import com.test.my.app.repository.utils.NetworkDataBoundResource
import com.test.my.app.repository.utils.Resource
import com.test.my.app.model.BaseResponse
import okhttp3.RequestBody
import java.io.File
import java.io.FileInputStream
import javax.inject.Inject

interface StoreRecordRepository {

    suspend fun fetchDocumentType(
        forceRefresh: Boolean = false,
        data: ListDocumentTypesModel
    ): LiveData<Resource<ListDocumentTypesModel.ListDocumentTypesResponse>>

    suspend fun fetchDocumentList(
        forceRefresh: Boolean = false,
        data: ListDocumentsModel
    ): LiveData<Resource<ListDocumentsModel.ListDocumentsResponse>>

    suspend fun fetchRelativesList(
        forceRefresh: Boolean = false,
        data: ListRelativesModel
    ): LiveData<Resource<ListRelativesModel.ListRelativesResponse>>

    suspend fun saveRecordToServer(
        forceRefresh: Boolean = false,
        data: SaveDocumentModel,
        personName: String,
        personRel: String,
        healthDocumentsList: List<SaveDocumentModel.HealthDoc>
    ): LiveData<Resource<SaveDocumentModel.SaveDocumentsResponse>>

    suspend fun deleteRecordFromServer(
        forceRefresh: Boolean = false,
        data: DeleteDocumentModel,
        deleteRecordIds: List<String>
    ): LiveData<Resource<DeleteDocumentModel.DeleteDocumentResponse>>

    suspend fun downloadDocumentFromServer(
        forceRefresh: Boolean = false,
        data: DownloadDocumentModel
    ): LiveData<Resource<DownloadDocumentModel.DownloadDocumentResponse>>

    suspend fun checkUnitExist(
        forceRefresh: Boolean = false,
        data: OCRUnitExistModel
    ): LiveData<Resource<OCRUnitExistModel.OCRUnitExistResponse>>

    suspend fun ocrSaveDocument(
        forceRefresh: Boolean = false,
        data: OCRSaveModel
    ): LiveData<Resource<OCRSaveModel.OCRSaveResponse>>

    suspend fun ocrDigitizeDocument(
        fileBytes: RequestBody,
        partnerCode: RequestBody,
        fileExtension: RequestBody
    ): LiveData<Resource<OcrResponce>>

    suspend fun saveRecordsInSession(recordsInSession: RecordInSession)
    suspend fun updateHealthRecordPathSync(id: String, path: String, fileUri: String, sync: String)
    suspend fun createUploadList(
        code: String,
        comment: String,
        personID: String,
        relativeId: String,
        relation: String,
        personName: String
    ): MutableList<SaveDocumentModel.HealthDoc>

    suspend fun getRecordsInSession(): List<RecordInSession>
    suspend fun getDocumentTypes(): List<DocumentType>
    suspend fun getUserRelatives(): List<UserRelatives>
    suspend fun getHealthDocumentsWherePersonId(personId: String): List<HealthDocument>
    suspend fun getHealthDocumentsWhereCode(code: String): List<HealthDocument>
    suspend fun getAllHealthDocuments(): List<HealthDocument>

    suspend fun deleteRecordInSession(record: RecordInSession)
    suspend fun deleteRecordInSessionTable()
    suspend fun logoutUser()
}

class ShrRepositoryImpl @Inject constructor(
    private val datasource: ShrDatasource,
    private val shrDao: StoreRecordsDao,
    private val userDao: VivantUserDao,
    private val dataSyncMasterDao: DataSyncMasterDao,
    val context: Context
) : StoreRecordRepository {

    override suspend fun fetchDocumentType(
        forceRefresh: Boolean,
        data: ListDocumentTypesModel
    ): LiveData<Resource<ListDocumentTypesModel.ListDocumentTypesResponse>> {

        return object :
            NetworkBoundResource<ListDocumentTypesModel.ListDocumentTypesResponse, BaseResponse<ListDocumentTypesModel.ListDocumentTypesResponse>>(
                context
            ) {

            override fun shouldStoreInDb(): Boolean = true

            override suspend fun loadFromDb(): ListDocumentTypesModel.ListDocumentTypesResponse {
                val dbData = ListDocumentTypesModel.ListDocumentTypesResponse()
                dbData.documentTypes = shrDao.getDocumentTypes()
                return dbData
            }

            override suspend fun saveCallResults(items: ListDocumentTypesModel.ListDocumentTypesResponse) {
                if (items.documentTypes.isNotEmpty()) {
                    shrDao.deleteDocumentTypesTable()
                    for (documentType in items.documentTypes) {
                        shrDao.insertDocumentTypes(documentType)
                    }
                }
                val dataSyc = DataSyncMaster(
                    apiName = ApiConstants.DOC_TYPE_MASTER,
                    syncDate = DateHelper.currentDateAsStringyyyyMMdd
                )
//                if(dataSyncMasterDao.getLastSyncDataList().find { it.apiName == ApiConstants.DOC_TYPE_MASTER } == null)
                dataSyncMasterDao.insertApiSyncData(dataSyc)
//                else
//                    dataSyncMasterDao.updateRecord(dataSyc)
            }

            override suspend fun createCallAsync(): BaseResponse<ListDocumentTypesModel.ListDocumentTypesResponse> {
                return datasource.getDocumentTypeResponse(data)
            }

            override fun processResponse(response: BaseResponse<ListDocumentTypesModel.ListDocumentTypesResponse>): ListDocumentTypesModel.ListDocumentTypesResponse {
                return response.jSONData
            }

            override fun shouldFetch(data: ListDocumentTypesModel.ListDocumentTypesResponse?): Boolean {
                return true
            }

        }.build().asLiveData()
    }

    override suspend fun fetchRelativesList(
        forceRefresh: Boolean,
        data: ListRelativesModel
    ): LiveData<Resource<ListRelativesModel.ListRelativesResponse>> {

        return object :
            NetworkBoundResource<ListRelativesModel.ListRelativesResponse, BaseResponse<ListRelativesModel.ListRelativesResponse>>(
                context
            ) {

            override fun shouldStoreInDb(): Boolean = false

            override suspend fun loadFromDb(): ListRelativesModel.ListRelativesResponse {
                return ListRelativesModel.ListRelativesResponse()
            }

            override suspend fun createCallAsync(): BaseResponse<ListRelativesModel.ListRelativesResponse> {
                return datasource.getRelativesListResponse(data)
            }

            override fun processResponse(response: BaseResponse<ListRelativesModel.ListRelativesResponse>): ListRelativesModel.ListRelativesResponse {
                return response.jSONData
            }

            override suspend fun saveCallResults(items: ListRelativesModel.ListRelativesResponse) {
            }

            override fun shouldFetch(data: ListRelativesModel.ListRelativesResponse?): Boolean {
                return true
            }

        }.build().asLiveData()
    }

    override suspend fun fetchDocumentList(
        forceRefresh: Boolean,
        data: ListDocumentsModel
    ): LiveData<Resource<ListDocumentsModel.ListDocumentsResponse>> {

        return object :
            NetworkBoundResource<ListDocumentsModel.ListDocumentsResponse, BaseResponse<ListDocumentsModel.ListDocumentsResponse>>(
                context
            ) {

            override fun shouldStoreInDb(): Boolean = true

            override suspend fun loadFromDb(): ListDocumentsModel.ListDocumentsResponse {
                val resp = ListDocumentsModel.ListDocumentsResponse()
                resp.documents = shrDao.getHealthDocuments()
                return resp
            }

            override suspend fun saveCallResults(items: ListDocumentsModel.ListDocumentsResponse) {
                val documentList = items.documents
                //shrDao.deleteHealthDocumentTableTable()
                var no = 1
                for (document in documentList) {
                    val record = shrDao.getHealthDocumentById(document.Id)
                    Utilities.printLogError("$no)Record_fetched----->$record")
                    if (record == null) {
                        if (document.PersonId != null) {
                            document.Relation = shrDao.getRelationShip(document.PersonId.toString())
                        }
                        document.Path = ""
                        if (!Utilities.isNullOrEmpty(document.Name)) {
                            document.Type = Utilities.findTypeOfDocument(document.Name!!)
                        }
                        //document.RecordDate = DateHelper.getDateTimeAs_ddMMMyyyy(document.RecordDate)
                        document.RecordDate = document.RecordDate!!.split("T").toTypedArray()[0]
                        shrDao.insertDocument(document)
                        Utilities.printLogError("$no)Inserting_record_id----->${document.Id}")
                    } else {
                        shrDao.updateHealthDocument(
                            document.Id.toString(),
                            "N",
                            document.RecordDate!!.split("T").toTypedArray()[0],
                            document.PersonName!!
                        )
                        Utilities.printLogError("$no)Updating_record_id----->${document.Id}")
                    }
                    no++
                }
            }

            override suspend fun createCallAsync(): BaseResponse<ListDocumentsModel.ListDocumentsResponse> {
                return datasource.getDocumentListResponse(data)
            }

            override fun processResponse(response: BaseResponse<ListDocumentsModel.ListDocumentsResponse>): ListDocumentsModel.ListDocumentsResponse {
                return response.jSONData
            }

            override fun shouldFetch(data: ListDocumentsModel.ListDocumentsResponse?): Boolean {
                return true
            }

        }.build().asLiveData()
    }

    override suspend fun deleteRecordFromServer(
        forceRefresh: Boolean,
        data: DeleteDocumentModel,
        deleteRecordIds: List<String>
    ):
            LiveData<Resource<DeleteDocumentModel.DeleteDocumentResponse>> {

        return object :
            NetworkBoundResource<DeleteDocumentModel.DeleteDocumentResponse, BaseResponse<DeleteDocumentModel.DeleteDocumentResponse>>(
                context
            ) {

            var isProcessed = ""

            override fun shouldStoreInDb(): Boolean = true

            override suspend fun loadFromDb(): DeleteDocumentModel.DeleteDocumentResponse {
                val resp = DeleteDocumentModel.DeleteDocumentResponse()
                resp.isProcessed = isProcessed
                return resp
            }

            override suspend fun saveCallResults(items: DeleteDocumentModel.DeleteDocumentResponse) {
                isProcessed = items.isProcessed
                Utilities.printLog("isProcessed----->$isProcessed")
                if (isProcessed.equals(Constants.TRUE, ignoreCase = true)) {
                    for (i in deleteRecordIds) {
                        val record = shrDao.getHealthDocumentById(i.toInt())
                        Utilities.deleteFile(File(record.Path!!, record.Name!!))
                        shrDao.deleteHealthDocument(i)
                        Utilities.printLog("Deleted RecordId----->$i")
                    }
                }
            }

            override suspend fun createCallAsync(): BaseResponse<DeleteDocumentModel.DeleteDocumentResponse> {
                return datasource.deleteRecordsFromServerResponse(data)
            }

            override fun processResponse(response: BaseResponse<DeleteDocumentModel.DeleteDocumentResponse>): DeleteDocumentModel.DeleteDocumentResponse {
                return response.jSONData
            }

            override fun shouldFetch(data: DeleteDocumentModel.DeleteDocumentResponse?): Boolean {
                return true
            }

        }.build().asLiveData()
    }

    override suspend fun downloadDocumentFromServer(
        forceRefresh: Boolean,
        data: DownloadDocumentModel
    ): LiveData<Resource<DownloadDocumentModel.DownloadDocumentResponse>> {

        return object :
            NetworkBoundResource<DownloadDocumentModel.DownloadDocumentResponse, BaseResponse<DownloadDocumentModel.DownloadDocumentResponse>>(
                context
            ) {

            override fun shouldStoreInDb(): Boolean = false

            override suspend fun loadFromDb(): DownloadDocumentModel.DownloadDocumentResponse {
                return DownloadDocumentModel.DownloadDocumentResponse()
            }

            override fun processResponse(response: BaseResponse<DownloadDocumentModel.DownloadDocumentResponse>): DownloadDocumentModel.DownloadDocumentResponse {
                return response.jSONData
            }

            override suspend fun createCallAsync(): BaseResponse<DownloadDocumentModel.DownloadDocumentResponse> {
                return datasource.downloadDocumentFromServerResponse(data)
            }

            override suspend fun saveCallResults(items: DownloadDocumentModel.DownloadDocumentResponse) {

            }

            override fun shouldFetch(data: DownloadDocumentModel.DownloadDocumentResponse?): Boolean {
                return true
            }

        }.build().asLiveData()

    }

    override suspend fun saveRecordToServer(
        forceRefresh: Boolean,
        data: SaveDocumentModel,
        personName: String,
        personRel: String,
        healthDocumentsList: List<SaveDocumentModel.HealthDoc>
    ): LiveData<Resource<SaveDocumentModel.SaveDocumentsResponse>> {

        return object :
            NetworkBoundResource<SaveDocumentModel.SaveDocumentsResponse, BaseResponse<SaveDocumentModel.SaveDocumentsResponse>>(
                context
            ) {

            var resp = SaveDocumentModel.SaveDocumentsResponse()

            override fun shouldStoreInDb(): Boolean = true

            override suspend fun loadFromDb(): SaveDocumentModel.SaveDocumentsResponse {
                // return SaveDocumentModel.SaveDocumentsResponse()
                return resp
            }

            override suspend fun saveCallResults(items: SaveDocumentModel.SaveDocumentsResponse) {
                resp = items
                val uploadedList = items.healthDocuments
                if (uploadedList.isNotEmpty()) {
                    for (document in uploadedList) {
                        document.Type = Utilities.findTypeOfDocument(document.Name!!)
                        for (reqDoc in healthDocumentsList) {
                            if (document.Name.equals(reqDoc.fileName, ignoreCase = true)) {
                                document.Path = reqDoc.Path
                            }
                        }
                        document.PersonName = personName
                        document.Relation = personRel
                        document.RecordDate = document.RecordDate!!.split("T").toTypedArray()[0]
                        document.FileUri = shrDao.getRecordInSessionWhere(document.Name!!).FileUri
                        shrDao.insertDocument(document)
                    }
                    shrDao.deleteRecordsInSessionTable()
                }
            }

            override fun processResponse(response: BaseResponse<SaveDocumentModel.SaveDocumentsResponse>): SaveDocumentModel.SaveDocumentsResponse {
                return response.jSONData
            }

            override suspend fun createCallAsync(): BaseResponse<SaveDocumentModel.SaveDocumentsResponse> {
                return datasource.saveRecordToServerResponse(data)
            }

            override fun shouldFetch(data: SaveDocumentModel.SaveDocumentsResponse?): Boolean {
                return true
            }

        }.build().asLiveData()
    }

    override suspend fun ocrDigitizeDocument(
        fileBytes: RequestBody,
        partnerCode: RequestBody,
        fileExtension: RequestBody
    ): LiveData<Resource<OcrResponce>> {

        return object : NetworkDataBoundResource<OcrResponce, BaseResponse<OcrResponce>>(context) {

            override fun processResponse(response: BaseResponse<OcrResponce>): OcrResponce {
                return response.jSONData
            }

            override suspend fun createCallAsync(): BaseResponse<OcrResponce> {
                return datasource.ocrDigitizeDocument(fileBytes, partnerCode, fileExtension)
            }

        }.build().asLiveData()
    }

    override suspend fun checkUnitExist(
        forceRefresh: Boolean,
        data: OCRUnitExistModel
    ): LiveData<Resource<OCRUnitExistModel.OCRUnitExistResponse>> {

        return object :
            NetworkBoundResource<OCRUnitExistModel.OCRUnitExistResponse, BaseResponse<OCRUnitExistModel.OCRUnitExistResponse>>(
                context
            ) {

            override fun shouldStoreInDb(): Boolean = false

            override suspend fun loadFromDb(): OCRUnitExistModel.OCRUnitExistResponse {
                return OCRUnitExistModel.OCRUnitExistResponse()
            }

            override suspend fun createCallAsync(): BaseResponse<OCRUnitExistModel.OCRUnitExistResponse> {
                return datasource.getUnitExistResponse(data)
            }

            override fun processResponse(response: BaseResponse<OCRUnitExistModel.OCRUnitExistResponse>): OCRUnitExistModel.OCRUnitExistResponse {
                return response.jSONData
            }

            override suspend fun saveCallResults(items: OCRUnitExistModel.OCRUnitExistResponse) {
            }

            override fun shouldFetch(data: OCRUnitExistModel.OCRUnitExistResponse?): Boolean {
                return true
            }
        }.build().asLiveData()
    }

    override suspend fun ocrSaveDocument(
        forceRefresh: Boolean,
        data: OCRSaveModel
    ): LiveData<Resource<OCRSaveModel.OCRSaveResponse>> {

        return object :
            NetworkBoundResource<OCRSaveModel.OCRSaveResponse, BaseResponse<OCRSaveModel.OCRSaveResponse>>(
                context
            ) {

            override fun shouldStoreInDb(): Boolean = false

            override suspend fun loadFromDb(): OCRSaveModel.OCRSaveResponse {
                return OCRSaveModel.OCRSaveResponse()
            }

            override suspend fun createCallAsync(): BaseResponse<OCRSaveModel.OCRSaveResponse> {
                return datasource.ocrSaveDocumentResponse(data)
            }

            override fun processResponse(response: BaseResponse<OCRSaveModel.OCRSaveResponse>): OCRSaveModel.OCRSaveResponse {
                return response.jSONData
            }

            override suspend fun saveCallResults(items: OCRSaveModel.OCRSaveResponse) {
            }

            override fun shouldFetch(data: OCRSaveModel.OCRSaveResponse?): Boolean {
                return true
            }
        }.build().asLiveData()
    }

    override suspend fun createUploadList(
        code: String,
        comment: String,
        personID: String,
        relativeId: String,
        relation: String,
        personName: String
    ): MutableList<SaveDocumentModel.HealthDoc> {

        val recordToUploadRequestList: MutableList<SaveDocumentModel.HealthDoc> = mutableListOf()
        try {
            val recordsToUploadList: ArrayList<HealthDocument> = ArrayList()
            val uploadRecordsInSession = shrDao.getRecordsInSession()
            Utilities.printLog("UploadRecordsInSession----->${uploadRecordsInSession.size}")
            if (uploadRecordsInSession.isNotEmpty()) {
                for (recordInSession in uploadRecordsInSession) {
                    val newHealthRecord = HealthDocument(
                        Id = 0,
                        Title = recordInSession.OriginalFileName,
                        Name = recordInSession.Name,
                        Code = code,
                        Type = Utilities.findTypeOfDocument(recordInSession.Name),
                        Comment = comment,
                        PersonId = relativeId.toInt(),
                        PersonName = personName,
                        Relation = relation,
                        Path = recordInSession.Path,
                        FileUri = recordInSession.FileUri,
                        Sync = "Y",
                        RecordDate = DateHelper.currentDateAsStringddMMMyyyy
                    )
                    recordsToUploadList.add(newHealthRecord)
                }
                Utilities.printLog("New Records----->${recordsToUploadList}")
            }
            if (recordsToUploadList.size > 0) {
                for (newRecord in recordsToUploadList) {
                    val doc = prepareRecordToUpload(newRecord)
                    recordToUploadRequestList.add(doc)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return recordToUploadRequestList
    }

    private fun prepareRecordToUpload(newRecord: HealthDocument): SaveDocumentModel.HealthDoc {
        var encodedFile = ""

        val file = File(newRecord.Path, newRecord.Name!!)
        //val file = DocumentFile.fromTreeUri(context, newRecord.FileUri.toUri())!!
        if (file.exists()) {
            try {
                val bytesFile = ByteArray(file.length().toInt())
                context.contentResolver.openFileDescriptor(Uri.fromFile(file), "r")
                    ?.use { parcelFileDescriptor ->
                        FileInputStream(parcelFileDescriptor.fileDescriptor).use { inStream ->
                            inStream.read(bytesFile)
                            encodedFile = Base64.encodeToString(bytesFile, Base64.DEFAULT)
                        }
                    }
            } catch (e: Exception) {
                Utilities.printLog("Error retrieving document to upload")
                e.printStackTrace()
            }
        } else {
            Utilities.printLog("${newRecord.Name} : File not found")
        }

        var documentTitle = newRecord.Title
        if (!Utilities.isNullOrEmpty(documentTitle)) {
            //documentTitle = RealPathUtil.removeFileExt(documentTitle!!)
            documentTitle = FileUtils.getNameWithoutExtension(documentTitle!!)
        }

        val healthDoc = SaveDocumentModel.HealthDoc(
            personID = newRecord.PersonId!!.toString(),
            documentTypeCode = newRecord.Code!!,
            comments = newRecord.Comment!!,
            fileName = newRecord.Name!!,
            title = documentTitle!!,
            relation = newRecord.Relation!!,
            fileBytes = encodedFile,
            personName = newRecord.PersonName!!,
            type = newRecord.Type!!,
            Path = newRecord.Path!!
        )
        return healthDoc
    }

    override suspend fun updateHealthRecordPathSync(
        id: String,
        path: String,
        fileUri: String,
        sync: String
    ) {
        shrDao.updateHealthDocumentPathSync(id, path, fileUri, sync)
    }

    override suspend fun saveRecordsInSession(recordsInSession: RecordInSession) {
        shrDao.insertRecordInSession(recordsInSession)
        Utilities.printLogError("Record inserted in RecordsInSessionTable.....")
    }

    override suspend fun getHealthDocumentsWhereCode(code: String): List<HealthDocument> {
        if (code.equals("OTR", ignoreCase = true)) {
            return shrDao.getHealthDocumentsWhereCodeWithOther(code)
        } else {
            return shrDao.getHealthDocumentsWhereCode(code)
        }
    }

    // getAllHealthDocuments
    override suspend fun getAllHealthDocuments(): List<HealthDocument> {
        return shrDao.getHealthDocuments()
    }

    override suspend fun getHealthDocumentsWherePersonId(personId: String): List<HealthDocument> {
        return shrDao.getHealthDocumentsWherePersonId(personId)
    }

    override suspend fun getRecordsInSession(): List<RecordInSession> {
        return shrDao.getRecordsInSession()
    }

    override suspend fun getDocumentTypes(): List<DocumentType> {
        return shrDao.getDocumentTypes()
    }

    override suspend fun getUserRelatives(): List<UserRelatives> {
        return shrDao.getUserRelatives()
    }

    override suspend fun deleteRecordInSession(record: RecordInSession) {
        shrDao.deleteRecordInSession(record.Name, record.Path)
        Utilities.deleteFileFromLocalSystem(record.Path + "/" + record.Name)
        Utilities.printLogError("Deleted Record from RecordsInSessionTable...")
    }

    override suspend fun deleteRecordInSessionTable() {
        try {
            val list = shrDao.getRecordsInSession()
            if (!list.isNullOrEmpty()) {
                for (record in list) {
                    val file = File(record.Path, record.Name)
                    if (file.exists()) {
                        Utilities.deleteFile(file)
                    }
                    Utilities.printLogError("Deleted ${record.Name} from RecordsInSessionTable...")
                }
            }
            shrDao.deleteRecordsInSessionTable()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun logoutUser() {
        shrDao.deleteDocumentTypesTable()
        shrDao.deleteHealthDocumentTableTable()
        shrDao.deleteRecordsInSessionTable()
        shrDao.deleteUserRelativesTable()
    }
}