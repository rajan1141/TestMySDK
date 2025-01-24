package com.test.my.app.records_tracker.views

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.test.my.app.R
import com.test.my.app.common.utils.CustomCounter
import com.test.my.app.common.utils.Utilities
import com.test.my.app.model.entity.RecordInSession
import com.test.my.app.model.entity.UserRelatives
import com.test.my.app.model.shr.HealthDataParameter
import com.test.my.app.records_tracker.adapter.SelectRelationshipAdapter
import com.test.my.app.records_tracker.adapter.SelectedRecordToUploadAdapter
import com.test.my.app.records_tracker.adapter.UploadRecordAdapter
import com.test.my.app.records_tracker.common.DataHandler
import com.test.my.app.records_tracker.ui.UploadRecordFragment

object ShrBinding {

    @BindingAdapter("app:recordInSessionImg")
    @JvmStatic
    fun AppCompatImageView.setRecordInSessionImg(recordInSession: RecordInSession?) {
        try {
            if (recordInSession != null) {
                val filePath = recordInSession.Path
                val fileName = recordInSession.Name
                val type = recordInSession.Type
                val completeFilePath = "$filePath/$fileName"
                if (type.equals("IMAGE", ignoreCase = true)) {
                    if (!completeFilePath.equals("", ignoreCase = true)) {

                        var bitmap: Bitmap? = null
                        try {
                            //bitmap = RealPathUtil.decodeFile(completeFilePath)
                            bitmap = BitmapFactory.decodeFile(completeFilePath)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        if (bitmap == null) {
                            setImageResource(R.drawable.icon_file_unknown)
                        } else {
                            setImageBitmap(bitmap)
                            setOnClickListener {
                                Utilities.showFullImageWithBitmap(bitmap, context, true)
                            }
                        }
                    }
                } else if (type.equals("PDF", ignoreCase = true)) {
                    setImageDrawable(ContextCompat.getDrawable(context, R.drawable.img_pdf))
                    setOnClickListener {
                        DataHandler(context).viewRecord(recordInSession)
                    }
                } else if (type.equals("DOC", ignoreCase = true)) {
                    setImageDrawable(ContextCompat.getDrawable(context, R.drawable.img_doc))
                    setOnClickListener {
                        DataHandler(context).viewRecord(recordInSession)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @BindingAdapter("android:loadImageView")
    @JvmStatic
    fun AppCompatImageView.setImageViewResource(resource: Int) {
        setImageResource(resource)
    }

    @BindingAdapter("app:relativesList")
    @JvmStatic
    fun RecyclerView.setRelativesList(list: List<UserRelatives>?) {
        with(this.adapter as SelectRelationshipAdapter) {
            layoutManager = androidx.recyclerview.widget.GridLayoutManager(context, 3)
            list?.let { updateRelativesList(it) }
        }
    }

    //@BindingAdapter("app:uploadRecordList")
    @BindingAdapter(
        value = ["app:uploadRecordList", "app:uploadRecordFragment"],
        requireAll = false
    )
    @JvmStatic
    fun RecyclerView.setUploadRecordList(
        list: List<RecordInSession>?,
        fragment: UploadRecordFragment
    ) {
        with(this.adapter as UploadRecordAdapter) {
            layoutManager = androidx.recyclerview.widget.GridLayoutManager(context, 3)
            list?.let {
                if (!it.isNullOrEmpty()) {
                    fragment.populateList(it.toMutableList())
                }
            }
        }
    }

    @BindingAdapter("app:recordToUploadList")
    @JvmStatic
    fun RecyclerView.setRecordToUploadList(list: List<RecordInSession>?) {
        with(this.adapter as SelectedRecordToUploadAdapter) {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            list?.let { updateData(it.toMutableList()) }
        }
    }

    @BindingAdapter("app:setVisible")
    @JvmStatic
    fun View.setVisible(isExist: Boolean) {
        visibility = if (isExist)
            View.GONE
        else
            View.VISIBLE
    }

    @BindingAdapter("app:setCounter")
    @JvmStatic
    fun CustomCounter.setCounter(parameter: HealthDataParameter) {
        setValue(parameter.observation)
        setAsNonDecimal(true)
        if (!Utilities.isNullOrEmpty(parameter.minPermissibleValue)
            && !Utilities.isNullOrEmpty(parameter.maxPermissibleValue)
        ) {
            setInputRange(
                parameter.minPermissibleValue.toDouble(),
                parameter.maxPermissibleValue.toDouble()
            )
        }
    }

    @BindingAdapter("app:setError")
    @JvmStatic
    fun AppCompatEditText.setError(isExist: Boolean) {
        context.resources.getString(R.string.ERROR_UNIT_NOT_SUPPORTED)
        error = if (isExist)
            "Unit is not Supported"
        else
            null
    }

    /*    @BindingAdapter("app:digitizedList")
        @JvmStatic fun setDigitizedParametetsList(recyclerView: RecyclerView, list: List<HealthDataParameter>?) {
            with(recyclerView.adapter as DigitizeRecordParametersAdapter) {
                recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(recyclerView.context)
                list?.let { updateHealthParameterList(it) }
            }
        }*/
}