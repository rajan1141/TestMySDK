package com.test.my.app.home.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.test.my.app.common.base.BaseViewModel
import com.test.my.app.home.common.DataHandler
import com.test.my.app.home.domain.HomeManagementUseCase
import com.test.my.app.model.entity.Users
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.File
import javax.inject.Inject

@HiltViewModel
@SuppressLint("StaticFieldLeak")
class WebViewViewModel @Inject constructor(
    application: Application,
    private val homeManagementUseCase: HomeManagementUseCase,
    private val dataHandler: DataHandler,
    val context: Context?
) : BaseViewModel(application) {

    var userDetails = MutableLiveData<Users>()

    private fun openDownloadedFile(file: File) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.ms-excel")
        intent.flags =
            Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
        try {
            context!!.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            //AlertDialogHelper( this , "No Application Available to View Excel." ,"You need Excel viewer to open this file.")
        }
    }

}