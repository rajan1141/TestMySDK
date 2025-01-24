package com.test.my.app.hra.common

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.core.content.FileProvider
import com.test.my.app.R
import com.test.my.app.common.constants.Configuration
import com.test.my.app.common.utils.FileUtils
import com.test.my.app.common.utils.HeightWeightDialog
import com.test.my.app.common.utils.LocaleHelper
import com.test.my.app.common.utils.ParameterDataModel
import com.test.my.app.common.utils.Utilities
import com.test.my.app.common.view.CustomEditTextHra
import com.test.my.app.common.view.FlowLayout
import com.test.my.app.hra.ui.HraQuesBmiFragment
import com.test.my.app.model.hra.LabRecordsModel.LabRecordDetails
import com.test.my.app.model.hra.Option
import okhttp3.ResponseBody
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.Locale

object HraHelper {


    private val fileUtils = FileUtils

    fun showHeightDialog(
        height: Int,
        layHeight: CustomEditTextHra,
        listener: HraQuesBmiFragment,
        context: Context,
        savedUnit: String
    ) {
        val localResource =
            LocaleHelper.getLocalizedResources(context, Locale(LocaleHelper.getLanguage(context)))!!
        val data = ParameterDataModel()
        data.title = localResource.getString(R.string.HEIGHT)
        data.value = " - - "
        data.finalValue = height.toString()
        if (savedUnit.equals("cm", true)) {
            data.unit = localResource.getString(R.string.CM)
        } else {
            data.unit = localResource.getString(R.string.FEET_INCH)
        }
        data.code = "HEIGHT"
        val heightWeightDialog = HeightWeightDialog(context, listener, "Height", data)
        heightWeightDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        heightWeightDialog.show()
    }

    fun showWeightDialog(
        weight: Double,
        layWeight: CustomEditTextHra,
        listener: HraQuesBmiFragment,
        context: Context,
        savedUnit: String = "kg"
    ) {
        val localResource =
            LocaleHelper.getLocalizedResources(context, Locale(LocaleHelper.getLanguage(context)))!!
        val data = ParameterDataModel()
        data.title = localResource.getString(R.string.WEIGHT)
        data.value = " - - "
        if (Utilities.isNullOrEmptyOrZero(weight.toString())) {
            data.finalValue = "50.0"
        } else {
            data.finalValue = weight.toString()
        }

        if (!savedUnit.equals("kg", true)) {
            data.unit = localResource.getString(R.string.LBS)
        } else {
            data.unit = localResource.getString(R.string.KG)
        }
        data.code = "WEIGHT"
        val heightWeightDialog = HeightWeightDialog(context, listener, "Weight", data)
        heightWeightDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        heightWeightDialog.show()
    }

    fun filterLabRecords(labRecords: List<LabRecordDetails>): List<LabRecordDetails> {

        return labRecords.filter {
            it.ParameterCode.equals("DIAB_RA", ignoreCase = true)
                    || it.ParameterCode.equals("DIAB_FS", ignoreCase = true)
                    || it.ParameterCode.equals("DIAB_PM", ignoreCase = true)
                    || it.ParameterCode.equals("DIAB_HBA1C", ignoreCase = true)
                    || it.ParameterCode.equals("CHOL_TOTAL", ignoreCase = true)
                    || it.ParameterCode.equals("CHOL_HDL", ignoreCase = true)
                    || it.ParameterCode.equals("CHOL_LDL", ignoreCase = true)
                    || it.ParameterCode.equals("CHOL_TRY", ignoreCase = true)
                    || it.ParameterCode.equals("CHOL_VLDL", ignoreCase = true)
        }

    }

    fun getParamData(key: String?): ParameterDataModel {
        val param = ParameterDataModel()
        when (key) {
            "CHOL_TOTAL" -> {
                param.code = key
                param.minRange = 50.0
                param.maxRange = 1000.0
            }

            "CHOL_LDL" -> {
                param.code = key
                param.minRange = 10.0
                param.maxRange = 300.0
            }

            "CHOL_HDL" -> {
                param.code = key
                param.minRange = 10.0
                param.maxRange = 100.0
            }

            "CHOL_TRY" -> {
                param.code = key
                param.minRange = 20.0
                param.maxRange = 700.0
            }

            "CHOL_VLDL" -> {
                param.code = key
                param.minRange = 1.0
                param.maxRange = 200.0
            }

            "DIAB_FS" -> {
                param.code = key
                param.minRange = 1.0
                param.maxRange = 999.0
            }

            "DIAB_PM" -> {
                param.code = key
                param.minRange = 61.0
                param.maxRange = 999.0
            }

            "DIAB_RA" -> {
                param.code = key
                param.minRange = 61.0
                param.maxRange = 999.0
            }

            "DIAB_HBA1C" -> {
                param.code = key
                param.minRange = 1.0
                param.maxRange = 100.0
            }
        }
        return param
    }

    fun deselectExceptNone(flowLayout: FlowLayout) {
        for (i in 0 until flowLayout.childCount) {
            if (i != 0) {
                val chk = flowLayout.getChildAt(i) as CheckBox
                if (chk.isChecked) {
                    chk.isChecked = false
                    Utilities.printLogError("${chk.text} is Unchecked")
                }
            }
        }
    }

    /*    fun addButtonsMultiSelection(optionList: ArrayList<Option>, flowLayout: FlowLayout, listener: CompoundButton.OnCheckedChangeListener, noneListener: View.OnClickListener, context: Context) {
            try {
                flowLayout.removeAllViews()
                val par = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                par.setMargins(10, 10, 10, 10)
                //if (mQuestion.getOptions().size() <= 5) btnViewMore.setVisibility(View.GONE)
                for (i in 0 until optionList.size) {
                    val chk = CheckBox(context)
                    val option = optionList[i]
                    chk.apply {
                        id = i
                        tag = option.answerCode
                        text = option.description
                        layoutParams = par
                        buttonDrawable = null
                        setPadding(40, 25, 40, 25)
                        background = ViewUtils.getArcRbSelectorBgHra(true)
                        setTextColor(ViewUtils.getArcRbSelectorTxtColorHra(true))

                        //background = ContextCompat.getDrawable(context, R.drawable.hra_option_selector_bg)
                        //setTextColor(ContextCompat.getColorStateList(context, R.color.hra_option_selector_color))
                    }
                    Utilities.printLogError("Option ::%s", option.description)
                    //if (i >= 5) { chk.visibility = View.GONE }
                    chk.setOnCheckedChangeListener(listener)
                    if (i == 0) {
                        chk.setOnClickListener(noneListener)
                    }
                    flowLayout.addView(chk)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }*/

    fun addButtonsMultiSelection(
        optionList: ArrayList<Option>,
        flowLayout: FlowLayout,
        listener: CompoundButton.OnCheckedChangeListener,
        noneListener: View.OnClickListener,
        context: Context
    ) {
        try {
            flowLayout.removeAllViews()
            val inflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val par = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            par.setMargins(10, 20, 10, 10)
            //if (mQuestion.getOptions().size() <= 5) btnViewMore.setVisibility(View.GONE)
            for (i in 0 until optionList.size) {
                //val chk = CheckBox(context)
                val chk = inflater.inflate(R.layout.item_check_box, null) as CheckBox
                val option = optionList[i]
                chk.apply {
                    id = i
                    tag = option.answerCode
                    text = option.description
                    layoutParams = par
                    //buttonDrawable = null
                    //setPadding(40, 25, 40, 25)
                    //background = ViewUtils.getArcRbSelectorBgHra(true)
                    //setTextColor(ViewUtils.getArcRbSelectorTxtColorHra(true))

                    //background = ContextCompat.getDrawable(context, R.drawable.hra_option_selector_bg)
                    //setTextColor(ContextCompat.getColorStateList(context, R.color.hra_option_selector_color))
                }
                //Utilities.printLogError("Option ::%s", option.description)
                //if (i >= 5) { chk.visibility = View.GONE }
                chk.setOnCheckedChangeListener(listener)
                if (i == 0) {
                    chk.setOnClickListener(noneListener)
                }
                flowLayout.addView(chk)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun addRadioButtonsSingleSelection(
        optionList: ArrayList<Option>,
        radioGroup: RadioGroup,
        listener: View.OnClickListener,
        context: Context
    ) {
        try {
            radioGroup.removeAllViews()
            val inflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val par = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            par.setMargins(10, 20, 10, 10)
            for (i in optionList.indices) {
                val radioButton = inflater.inflate(R.layout.item_radio_button, null) as RadioButton
                val option = optionList[i]
                radioButton.apply {
                    id = i
                    tag = option.answerCode
                    text = option.description
                    layoutParams = par
                    //gravity = Gravity.CENTER
                    //buttonDrawable = null
                    //setPadding(40, 25, 40, 25)
                    //background = ViewUtils.getArcRbSelectorBgHra(true)
                }
                //Utilities.printLogError("Option ::%s", option.description)
                radioButton.setOnClickListener(listener)
                radioGroup.addView(radioButton)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /*    fun addRadioButtonsSingleSelection(optionList: ArrayList<Option>, radioGroup: RadioGroup, listener: View.OnClickListener, context: Context) {
            try {
                radioGroup.removeAllViews()
                val par = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                par.setMargins(10, 10, 10, 10)
                for (i in optionList.indices) {
                    val radioButton = RadioButton(context)
                    val option = optionList[i]
                    radioButton.apply {
                        id = i
                        tag = option.answerCode
                        text = option.description
                        layoutParams = par
                        gravity = Gravity.CENTER
                        buttonDrawable = null
                        setPadding(40, 25, 40, 25)
                        background = ViewUtils.getArcRbSelectorBgHra(true)
                        setTextColor(ViewUtils.getArcRbSelectorTxtColorHra(true))

                        //background = ContextCompat.getDrawable(context, R.drawable.hra_option_selector_bg)
                        //setTextColor(ContextCompat.getColorStateList(context, R.color.hra_option_selector_color))
                    }
                    Utilities.printLogError("Option ::%s", option.description)
                    radioButton.setOnClickListener(listener)
                    radioGroup.addView(radioButton)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }*/

    fun saveHRAReportAppDirectory(body: ResponseBody, context: Context): Boolean {
        var save = false
        try {
            if (fileUtils.isExternalStorageAvailable && fileUtils.isExternalStorageWritable) {

                val folderName = Utilities.getAppFolderLocation(context)
                val fileName = fileUtils.generateUniqueFileName(
                    Configuration.strAppIdentifier + "_HRA",
                    ".pdf"
                )

                val myDirectory = File(folderName)
                if (!myDirectory.exists()) {
                    myDirectory.mkdirs()
                }

                val hraReportFile = File(folderName, fileName)
                Utilities.printLogError("downloadDocPath: ----->$hraReportFile")

                val inputStream: InputStream = body.byteStream()
                val fileReader = ByteArray(4096)

                context.contentResolver.openFileDescriptor(Uri.fromFile(hraReportFile), "w")
                    ?.use { parcelFileDescriptor ->
                        FileOutputStream(parcelFileDescriptor.fileDescriptor).use { outStream ->
                            while (true) {
                                val read = inputStream.read(fileReader)
                                if (read == -1) {
                                    break
                                }
                                outStream.write(fileReader, 0, read)
                            }
                            save = true
                            openDownloadedDocumentFile(hraReportFile, context)
                        }
                    }
                inputStream.close()
            } else {
                save = false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            save = false
        }
        return save
    }

    private fun openDownloadedDocumentFile(file: File, context: Context) {
        try {
            val uri = FileProvider.getUriForFile(context, context.packageName + ".provider", file)
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(uri, "application/pdf")
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
            //val openIntent = Intent.createChooser(intent,"Open using")
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
            Utilities.toastMessageShort(
                context,
                context.resources.getString(R.string.ERROR_NO_APPLICATION_TO_VIEW_PDF)
            )
        } catch (e: Exception) {
            e.printStackTrace()
            Utilities.toastMessageShort(
                context,
                context.resources.getString(R.string.ERROR_UNABLE_TO_OPEN_FILE)
            )
        }
    }

    /*    private fun openDownloadedFile(downloadedFilePath: String, context: Context) {
            val builder = StrictMode.VmPolicy.Builder()
            StrictMode.setVmPolicy(builder.build())
            val file = File(downloadedFilePath)
            val uri = Uri.fromFile(file)

            val intent = Intent(Intent.ACTION_VIEW)
            if (uri.toString().contains(".pdf")) {
                intent.setDataAndType(uri, "application/pdf")
            }
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION

            try {
                context.startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                e.printStackTrace()
                Utilities.toastMessageShort(context, context.resources.getString(R.string.ERROR_NO_APPLICATION_TO_VIEW_PDF))
            } catch (e: Exception) {
                e.printStackTrace()
                Utilities.toastMessageShort(context, context.resources.getString(R.string.ERROR_UNABLE_TO_OPEN_FILE))
            }
        }*/

}