package com.test.my.app.medication_tracker.common

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import com.test.my.app.R
import com.test.my.app.common.constants.Configuration
import com.test.my.app.common.constants.Constants
import com.test.my.app.common.constants.NavigationConstants
import com.test.my.app.common.constants.PreferenceConstants
import com.test.my.app.common.utils.DateHelper
import com.test.my.app.common.utils.EncryptionUtility
import com.test.my.app.common.utils.LocaleHelper
import com.test.my.app.common.utils.Utilities
import com.test.my.app.common.view.SpinnerModel
import com.test.my.app.medication_tracker.MedNotificationApiService
import com.test.my.app.medication_tracker.model.FrequencyModel
import com.test.my.app.medication_tracker.model.InstructionModel
import com.test.my.app.medication_tracker.model.MedTypeModel
import com.test.my.app.medication_tracker.model.ReminderNotification
import com.test.my.app.medication_tracker.receiver.MedicineReminderBroadcastReceiver
import com.test.my.app.model.medication.AddInTakeModel
import com.test.my.app.model.medication.GetMedicineModel
import com.test.my.app.model.medication.MedicineInTakeModel
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.RequestBody
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit

class MedicationTrackerHelper(val context: Context) {

    private val localResource = LocaleHelper.getLocalizedResources(context!!, Locale(LocaleHelper.getLanguage(context!!)))!!
    private val notificationManager = context!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun getMedTypeList(): ArrayList<MedTypeModel> {
        val localResource = LocaleHelper.getLocalizedResources(context, Locale(LocaleHelper.getLanguage(context)))!!
        val medTypeList: ArrayList<MedTypeModel> = ArrayList()
        medTypeList.add(MedTypeModel(localResource.getString(R.string.TABLET), "TAB", R.drawable.img_pill))
        medTypeList.add(MedTypeModel(localResource.getString(R.string.CAPSULE), "CAP", R.drawable.img_capsul))
        medTypeList.add(MedTypeModel(localResource.getString(R.string.SYRUP), "SYRUP", R.drawable.img_syrup))
        medTypeList.add(MedTypeModel(localResource.getString(R.string.DROPS), "DROPS", R.drawable.img_drop))
        medTypeList.add(MedTypeModel(localResource.getString(R.string.INJECTION), "INJ", R.drawable.img_syringe))
        medTypeList.add(MedTypeModel(localResource.getString(R.string.GEL), "GEL", R.drawable.img_gel))
        medTypeList.add(MedTypeModel(localResource.getString(R.string.TUBE), "TUBE", R.drawable.img_tube))
        medTypeList.add(MedTypeModel(localResource.getString(R.string.SPRAY), "SPRAY", R.drawable.img_spray))
        medTypeList.add(MedTypeModel(localResource.getString(R.string.MOUTHWASH), "MOUTHWASH", R.drawable.img_mouth_wash))
        medTypeList.add(MedTypeModel(localResource.getString(R.string.SOLUTION), "SOL", R.drawable.img_solution))
        medTypeList.add(MedTypeModel(localResource.getString(R.string.OINTMENT), "OINT", R.drawable.img_ointment))
        medTypeList.add(MedTypeModel(localResource.getString(R.string.OTHER), "OTHER", R.drawable.img_other_med))
        return medTypeList
    }

    fun getMedTypeImageByCode(code: String): Int {
        var image: Int = R.drawable.img_pill
        when {
            code.equals("TAB", ignoreCase = true) -> {
                image = R.drawable.img_pill
            }
            code.equals("CAP", ignoreCase = true) -> {
                image = R.drawable.img_capsul
            }
            code.equals("SYRUP", ignoreCase = true) -> {
                image = R.drawable.img_syrup
            }
            code.equals("DROPS", ignoreCase = true) -> {
                image = R.drawable.img_drop
            }
            code.equals("INJ", ignoreCase = true) -> {
                image = R.drawable.img_syringe
            }
            code.equals("GEL", ignoreCase = true) -> {
                image = R.drawable.img_gel
            }
            code.equals("TUBE", ignoreCase = true) -> {
                image = R.drawable.img_tube
            }
            code.equals("SPRAY", ignoreCase = true) -> {
                image = R.drawable.img_spray
            }
            code.equals("MOUTHWASH", ignoreCase = true) -> {
                image = R.drawable.img_mouth_wash
            }
            code.equals("SOL", ignoreCase = true) -> {
                image = R.drawable.img_solution
            }
            code.equals("OINT", ignoreCase = true) -> {
                image = R.drawable.img_ointment
            }
            code.equals("OTHER", ignoreCase = true) -> {
                image = R.drawable.img_other_med
            }
        }
        return image
    }

    fun getMedTypeByCode(code: String): Int {
        var type: Int = R.string.TABLET
        if (!Utilities.isNullOrEmpty(code)) {
            when {
                code.equals("TAB", ignoreCase = true) -> {
                    type = R.string.TABLET
                }
                code.equals("CAP", ignoreCase = true) -> {
                    type = R.string.CAPSULE
                }
                code.equals("SYRUP", ignoreCase = true) -> {
                    type = R.string.SYRUP
                }
                code.equals("DROPS", ignoreCase = true) -> {
                    type = R.string.DROPS
                }
                code.equals("INJ", ignoreCase = true) -> {
                    type = R.string.INJECTION
                }
                code.equals("GEL", ignoreCase = true) -> {
                    type = R.string.GEL
                }
                code.equals("TUBE", ignoreCase = true) -> {
                    type = R.string.TUBE
                }
                code.equals("SPRAY", ignoreCase = true) -> {
                    type = R.string.SPRAY
                }
                code.equals("MOUTHWASH", ignoreCase = true) -> {
                    type = R.string.MOUTHWASH
                }
                code.equals("SOL", ignoreCase = true) -> {
                    type = R.string.SOLUTION
                }
                code.equals("OINT", ignoreCase = true) -> {
                    type = R.string.OINTMENT
                }
                code.equals("OTHER", ignoreCase = true) -> {
                    type = R.string.OTHER
                }
            }
        }
        return type
    }

    fun getMedInstructionByCode(code: String): String {
        val localResource = LocaleHelper.getLocalizedResources(context, Locale(LocaleHelper.getLanguage(context)))!!
        var instruction = localResource.getString(R.string.BEFORE_MEAL)
        when(code) {
            Constants.BEFORE_MEAL -> {
                instruction = localResource.getString(R.string.BEFORE_MEAL)
            }
            Constants.WITH_MEAL -> {
                instruction = localResource.getString(R.string.WITH_MEAL)
            }
            Constants.AFTER_MEAL -> {
                instruction = localResource.getString(R.string.AFTER_MEAL)
            }
            Constants.ANYTIME -> {
                instruction = localResource.getString(R.string.ANYTIME)
            }
        }
        return instruction
    }

    fun getMedFrequencyByCode(code: String): String {
        val localResource = LocaleHelper.getLocalizedResources(context, Locale(LocaleHelper.getLanguage(context)))!!
        var frequency = localResource.getString(R.string.DAILY)
        when(code) {
            Constants.DAILY -> {
                frequency = localResource.getString(R.string.DAILY)
            }
            Constants.FOR_X_DAYS -> {
                frequency = localResource.getString(R.string.FOR_X_DAYS)
            }
        }
        return frequency
    }

    fun getFrequencyList(): ArrayList<FrequencyModel> {
        val localResource = LocaleHelper.getLocalizedResources(context, Locale(LocaleHelper.getLanguage(context)))!!
        val frequencyList: ArrayList<FrequencyModel> = ArrayList()
        frequencyList.add(FrequencyModel(localResource.getString(R.string.DAILY),Constants.DAILY))
        frequencyList.add(FrequencyModel(localResource.getString(R.string.FOR_X_DAYS),Constants.FOR_X_DAYS))
        return frequencyList
    }

    fun getMedInstructionList(): ArrayList<InstructionModel> {
        val localResource = LocaleHelper.getLocalizedResources(context, Locale(LocaleHelper.getLanguage(context)))!!
        val instructionList: ArrayList<InstructionModel> = ArrayList()
        instructionList.add(InstructionModel(localResource.getString(R.string.BEFORE_MEAL),Constants.BEFORE_MEAL, R.drawable.img_before_meal))
        instructionList.add(InstructionModel(localResource.getString(R.string.WITH_MEAL),Constants.WITH_MEAL, R.drawable.img_with_meal))
        instructionList.add(InstructionModel(localResource.getString(R.string.AFTER_MEAL),Constants.AFTER_MEAL, R.drawable.img_after_meal))
        instructionList.add(InstructionModel(localResource.getString(R.string.ANYTIME),Constants.ANYTIME, R.drawable.img_clock))
        return instructionList
    }

    fun getCategoryList(): ArrayList<SpinnerModel> {
        val localResource = LocaleHelper.getLocalizedResources(context, Locale(LocaleHelper.getLanguage(context)))!!
        val list: ArrayList<SpinnerModel> = ArrayList()
        list.add(SpinnerModel(localResource.getString(R.string.ONGOING), "", 0, false))
        list.add(SpinnerModel(localResource.getString(R.string.COMPLETED), "", 1, false))
        list.add(SpinnerModel(localResource.getString(R.string.ALL), "", 2, false))
        return list
    }

    @SuppressLint("MissingPermission")
    fun displayMedicineReminderNotification(context: Context, data: ReminderNotification, fullName: String) {
        try {
            val localResource = LocaleHelper.getLocalizedResources(context, Locale(LocaleHelper.getLanguage(context)))!!
            val instruction = getMedInstructionByCode(data.instruction)
            val strMessage: String = data.dosage + " ${localResource.getString(R.string.DOSE)}" + " , " + instruction
            val timeToDisplay: String = DateHelper.getTimeIn12HrFormatAmOrPm(data.scheduleTime)
            val firstName: String = fullName.split(" ")[0]
            val personName = "${localResource.getString(R.string.FOR)} $firstName"

            val notificationId = Utilities.generateNotificationId()
            val channelId = Constants.channelIdMedication
            val channelName = Constants.channelNameMedication
            val notificationManager = Utilities.createNotificationChannel(context,channelId,channelName)

            // Onclick of Notification Intent
            val onClick = generateOnClickIntent(data, data.action, notificationId)
            onClick.putExtra(Constants.SUB_TITLE, strMessage)
            onClick.putExtra(Constants.TIME, timeToDisplay)
            //val pendingOnClickIntent = PendingIntent.getBroadcast(context, notificationId, onClick, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            val pendingOnClickIntent: PendingIntent? = TaskStackBuilder.create(context).run {
                // Add the intent, which inflates the back stack
                addNextIntentWithParentStack(onClick)
                // Get the PendingIntent containing the entire back stack
                getPendingIntent(notificationId,PendingIntent.FLAG_UPDATE_CURRENT or
                        // mutability flag required when targeting Android12 or higher
                        PendingIntent.FLAG_IMMUTABLE)
            }

            // Taken Action button Intent
            val takenIntent = generateIntent(context, data, Constants.TAKEN, notificationId)
            takenIntent.putExtra(Constants.SUB_TITLE, strMessage)
            takenIntent.putExtra(Constants.TIME, timeToDisplay)
            val pendingTakenIntent = PendingIntent.getBroadcast(context, notificationId, takenIntent, PendingIntent.FLAG_IMMUTABLE)

            // Skip Action button Intent
            val skipIntent = generateIntent(context, data, Constants.SKIPPED, notificationId)
            skipIntent.putExtra(Constants.SUB_TITLE, strMessage)
            skipIntent.putExtra(Constants.TIME, timeToDisplay)
            val pendingSkipIntent = PendingIntent.getBroadcast(context, notificationId, skipIntent, PendingIntent.FLAG_IMMUTABLE)

            //Custom Notification Layout(Remote View) only supports android component,
            // not support custom view, androidx, android support library or any library
            // Do not change components of Remote views to androidx

            // Notification's Collapsed layout
            val remoteViewCollapsed = RemoteViews(context.packageName, R.layout.med_notification_collapsed)
            remoteViewCollapsed.setTextViewText(R.id.med_notification_title, data.medicineName)
            remoteViewCollapsed.setTextViewText(R.id.med_notification_subtext, strMessage)
            remoteViewCollapsed.setTextViewText(R.id.med_schedule_time, timeToDisplay)
            remoteViewCollapsed.setTextViewText(R.id.med_person_name, personName)
            remoteViewCollapsed.setTextViewText(R.id.lbl_med_reminder,localResource.getString(R.string.MEDICINE_REMINDER))
            remoteViewCollapsed.setOnClickPendingIntent(R.id.med_reminder_collapsed, pendingOnClickIntent)

            // Notification's Expanded layout
            val remoteViewExpanded = RemoteViews(context.packageName, R.layout.med_notification_expanded)
            remoteViewExpanded.setTextViewText(R.id.med_notification_title, data.medicineName)
            remoteViewExpanded.setTextViewText(R.id.med_notification_subtext, strMessage)
            remoteViewExpanded.setTextViewText(R.id.med_schedule_time, timeToDisplay)
            remoteViewExpanded.setTextViewText(R.id.med_person_name, personName)
            remoteViewExpanded.setTextViewText(R.id.lbl_med_reminder,localResource.getString(R.string.MEDICINE_REMINDER))
            remoteViewExpanded.setTextViewText(R.id.txt_taken,localResource.getString(R.string.TAKEN))
            remoteViewExpanded.setTextViewText(R.id.txt_skip,localResource.getString(R.string.SKIP))
            remoteViewExpanded.setOnClickPendingIntent(R.id.layout_taken, pendingTakenIntent)
            remoteViewExpanded.setOnClickPendingIntent(R.id.layout_skip, pendingSkipIntent)
            remoteViewExpanded.setOnClickPendingIntent(R.id.med_reminder_expanded, pendingOnClickIntent)

            val customNotification = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.notification_logo)
                .setStyle(NotificationCompat.DecoratedCustomViewStyle())
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setTicker(context.resources.getString(R.string.app_name))
                .setContentIntent(pendingOnClickIntent)
                .setContent(remoteViewExpanded)
                .setContent(remoteViewCollapsed)
                .setCustomContentView(remoteViewCollapsed)
                .setCustomBigContentView(remoteViewExpanded)
                .build()

            Utilities.printLogError("displaying Notification")
            notificationManager.apply {
                // notificationId is a unique int for each notification that you must define
                notify(notificationId,customNotification)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun generateIntent(context: Context, data: ReminderNotification, action: String, notificationId: Int): Intent {
        val intent = Intent(context, MedicineReminderBroadcastReceiver::class.java)
        intent.action = action
        intent.putExtra(Constants.NOTIFICATION_ID, notificationId)
        intent.putExtra(Constants.NOTIFICATION_ACTION, action)
        intent.putExtra(Constants.PERSON_ID, data.personID)
        intent.putExtra(Constants.MEDICINE_NAME, data.medicineName)
        intent.putExtra(Constants.DOSAGE, data.dosage)
        intent.putExtra(Constants.INSTRUCTION, data.instruction)
        intent.putExtra(Constants.SCHEDULE_TIME, data.scheduleTime)
        intent.putExtra(Constants.MEDICATION_ID, data.medicationID)
        intent.putExtra(Constants.MEDICINE_IN_TAKE_ID, 0)
        intent.putExtra(Constants.SERVER_SCHEDULE_ID, data.scheduleID)
        intent.putExtra(Constants.DATE, data.notificationDate)
        intent.putExtra(Constants.DRUG_TYPE_CODE, data.drugTypeCode)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        return intent
    }

    private fun generateOnClickIntent(data: ReminderNotification, action: String, notificationId: Int): Intent {
        val intent = Intent()
        intent.action = action
        intent.putExtra(Constants.FROM, Constants.NOTIFICATION_ACTION)
        intent.putExtra(Constants.NOTIFICATION_ID, notificationId)
        intent.putExtra(Constants.NOTIFICATION_ACTION, action)
        intent.putExtra(Constants.PERSON_ID, data.personID)
        intent.putExtra(Constants.MEDICINE_NAME, data.medicineName)
        intent.putExtra(Constants.DOSAGE, data.dosage)
        intent.putExtra(Constants.INSTRUCTION, data.instruction)
        intent.putExtra(Constants.SCHEDULE_TIME, data.scheduleTime)
        intent.putExtra(Constants.MEDICATION_ID, data.medicationID)
        intent.putExtra(Constants.MEDICINE_IN_TAKE_ID, 0)
        intent.putExtra(Constants.SERVER_SCHEDULE_ID, data.scheduleID)
        intent.putExtra(Constants.DATE, data.notificationDate)
        intent.putExtra(Constants.DRUG_TYPE_CODE, data.drugTypeCode)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        intent.component = ComponentName(NavigationConstants.APPID, NavigationConstants.SPLASH_SCREEN)
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        return intent
    }

    // Api calls
    fun getMedDetailsByMedicationIdApi(medicationID: String) {
        val jsonObjectJSONData = JSONObject()
        jsonObjectJSONData.put("MedicationID", medicationID)
        jsonObjectJSONData.put("Message", "Get medicine....")
        getResponseFromServer(jsonObjectJSONData, "1")
    }

    private fun getResponseFromServer(jsonRequest: JSONObject, from: String): String {
        var jsonData = JSONObject()
        try {
            val jsonObject = JSONObject()

            val jsonObjectHeader = JSONObject()
            jsonObjectHeader.put("RequestID", Configuration.RequestID)
            jsonObjectHeader.put("DateTime", DateHelper.currentDateAsStringddMMMyyyy)
            jsonObjectHeader.put("ApplicationCode", Configuration.ApplicationCode)
            jsonObjectHeader.put("AuthTicket", Utilities.getUserPreference(PreferenceConstants.TOKEN))
            jsonObjectHeader.put("PartnerCode", Constants.PartnerCode)
            jsonObjectHeader.put("EntityType", Configuration.EntityType)
            jsonObjectHeader.put("HandShake", Configuration.Handshake)
            jsonObjectHeader.put("UTMSource", Configuration.UTMSource)
            jsonObjectHeader.put("UTMMedium", Configuration.UTMMedium)
            jsonObjectHeader.put("LanguageCode", Configuration.LanguageCode)
            jsonObjectHeader.put("EntityID", Configuration.EntityID)

            jsonObject.put("Header", jsonObjectHeader)
            jsonObject.put("JSONData", jsonRequest.toString())

            val logging = HttpLoggingInterceptor {
                Utilities.printLog("HttpLogging--> $it")
            }
            logging.level = HttpLoggingInterceptor.Level.BODY
            // Create Retrofit
            val retrofit = Retrofit.Builder()
                .client(
                    OkHttpClient.Builder()
                        .retryOnConnectionFailure(true)
                        .protocols(Arrays.asList(Protocol.HTTP_1_1))
                        .addInterceptor(logging)
                        .connectTimeout(3, TimeUnit.MINUTES)
                        .writeTimeout(3, TimeUnit.MINUTES)
                        .readTimeout(3, TimeUnit.MINUTES)
                        .build()
                )
                .baseUrl(Constants.strAPIUrl)
                .addConverterFactory(GsonConverterFactory.create())
                //.addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
                //.addConverterFactory(ScalarsConverterFactory.create())
                //.addCallAdapterFactory(CoroutineCallAdapterFactory())
                .build()

            // Create Service
            val service = retrofit.create(MedNotificationApiService::class.java)
            //Utilities.printLogError("Request--->$jsonObject")
            val body = getEncryptedRequestBody(jsonObject.toString())

            CoroutineScope(Dispatchers.Main).launch {
                var response: Response<ResponseBody>? = null
                when (from) {
                    "1" -> response = service.getMedicineDetailsApi(body)
                    "2" -> response = service.getMedicationInTakeApi(body)
                    "3" -> response = service.addIntakeApi(body)
                }
                //val response = service.getMedicineDetailsApi(body)
                withContext(Dispatchers.IO) {
                    if (response!!.isSuccessful) {
                        val resp = getResponse(response.body()!!.string())
                        Utilities.printLogError("from--->$from")
                        Utilities.printLogError("Resp--->$resp")
                        val jsonObjectResponse = JSONObject(resp)
                        if (!jsonObjectResponse.isNull("JSONData")) {
                            jsonData = JSONObject(jsonObjectResponse.optString("JSONData"))

                            when (from) {
                                "1" -> {
                                    getMedDetailsByMedicationIdApiResp(jsonObjectResponse.optString("JSONData"))
                                }
                                "2" -> {
                                    getMedInTakeFromNotificationByScheduleIDResp(jsonObjectResponse.optString("JSONData"))
                                }
                                "3" -> {
                                    addMedIntakeResp(jsonObjectResponse.optString("JSONData"))
                                }
                            }
                        }
                    } else {
                        Utilities.printLogError("RETROFIT_ERROR--->${response.code()}")
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return jsonData.toString()
    }

    private fun getMedDetailsByMedicationIdApiResp(response: String) {
        if (!Utilities.isNullOrEmpty(response)) {
            val gson = GsonBuilder().setPrettyPrinting().create()
            val jsonData: GetMedicineModel.GetMedicineResponse = gson.fromJson(response,
                object : TypeToken<GetMedicineModel.GetMedicineResponse?>() {}.type)
            val medication = jsonData.medication
            Utilities.printLogError("MedicationID--->${medication.medicationId}")
            try {
                val notificationData = MedicationSingleton.getInstance()!!.getNotificationIntent()
                val notificationID = notificationData.getIntExtra(Constants.NOTIFICATION_ID, -1)
                val medName = notificationData.getStringExtra(Constants.MEDICINE_NAME)
                val scheduleTime = notificationData.getStringExtra(Constants.SCHEDULE_TIME)?.substring(0, 5)
                val time = notificationData.getStringExtra(Constants.TIME)

                if (!Utilities.isNullOrEmptyOrZero(medication.medicationId.toString())) {
                    var isExist = false
                    for (i in medication.scheduleList) {
                        Utilities.printLogError("scheduleTimeResp--->${i.scheduleTime!!.substring(0, 5)}")
                        Utilities.printLogError("scheduleTimeNoti--->$scheduleTime")
                        if (i.scheduleTime!!.substring(0, 5) == scheduleTime) {
                            isExist = true
                            break
                        }
                    }

                    if (isExist) {
                        getMedInTakeFromNotificationByScheduleIDApi()
                    } else {
                        closeNotificationDrawer(context!!)
                        cancelNotification(notificationManager, notificationID)
                        Utilities.toastMessageShort(context!!,localResource.getString(R.string.YOU_HAVE_REMOVED_OR_RESCHEDULED_TIME) + " $time" + " " + localResource.getString(R.string.FOR) + " " + medName)
                    }

                } else {
                    closeNotificationDrawer(context!!)
                    cancelNotification(notificationManager, notificationID)
                    Utilities.toastMessageShort(context!!, localResource.getString(R.string.YOU_HAVE_ALREADY_DELETED) +" $medName")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            Utilities.printLogError("Error while fetching Response from server")
        }
    }

    private fun getMedInTakeFromNotificationByScheduleIDResp(response: String) {
        if (!Utilities.isNullOrEmpty(response)) {
            val gson = GsonBuilder().setPrettyPrinting().create()
            val jsonData: MedicineInTakeModel.MedicineDetailsResponse = gson.fromJson(response,
                object : TypeToken<MedicineInTakeModel.MedicineDetailsResponse?>() {}.type)
            val medicationInTakes = jsonData.medicationInTakes
            val medicationInTakeID = if (medicationInTakes.isNotEmpty()) {
                medicationInTakes[0].medicineInTakeId
            } else {
                0
            }
            Utilities.printLogError("MedicationInTakeID--->$medicationInTakeID")
            addMedIntakeApi(medicationInTakeID)
        } else {
            Utilities.printLogError("Error while fetching Response from server")
        }
    }

    private fun addMedIntakeApi(medicationInTakeID: Int) {
        try{
            val notificationData = MedicationSingleton.getInstance()!!.getNotificationIntent()
            val medicationInTakes = JSONArray()
            val intake = JSONObject()
            intake.put("MedicationID", notificationData.getStringExtra(Constants.MEDICATION_ID)!!.toInt())
            intake.put("ID", medicationInTakeID)
            intake.put("ScheduleID", notificationData.getStringExtra(Constants.SERVER_SCHEDULE_ID)!!.toInt())
            intake.put("Status", notificationData.getStringExtra(Constants.NOTIFICATION_ACTION)!!)
            intake.put("FeelStatus", "")
            intake.put("MedDate", notificationData.getStringExtra(Constants.DATE)!!)
            intake.put("MedTime", notificationData.getStringExtra(Constants.SCHEDULE_TIME)!!)
            intake.put("Dosage", notificationData.getStringExtra(Constants.DOSAGE)!!)
            intake.put("Description", "")
            intake.put("CreatedDate", notificationData.getStringExtra(Constants.DATE)!!)
            medicationInTakes.put(intake)

            val jsonObjectJSONData = JSONObject()
            jsonObjectJSONData.put("MedicationInTake", medicationInTakes)
            getResponseFromServer(jsonObjectJSONData, "3")
        }catch (e: Exception){e.printStackTrace()}

    }

    private fun addMedIntakeResp(response: String) {
        if (!Utilities.isNullOrEmpty(response)) {
            val jsonObjectResponse = JSONObject(response)
            val intakeArray = jsonObjectResponse.get("MedicationInTake")
            Utilities.printLogError("MedicationInTake--->$response")
            if (!Utilities.isNullOrEmpty(intakeArray.toString())) {
                val gson = GsonBuilder().setPrettyPrinting().create()
                val jsonData: AddInTakeModel.AddInTakeResponse = gson.fromJson(
                    response,
                    object : TypeToken<AddInTakeModel.AddInTakeResponse?>() {}.type
                )
                val medicationInTake = jsonData.medicationInTake
                Utilities.printLogError("MedicationInTake--->$medicationInTake")

                if (medicationInTake.isNotEmpty()) {
                    val medicineDetailsList =
                        MedicationSingleton.getInstance()!!.geMedicineListByDay()
                    if (medicineDetailsList.size > 0) {
                        for (inTakeObject in medicationInTake) {
                            for (medicineDetails in medicineDetailsList) {
                                if (inTakeObject.medicationID == medicineDetails.medicationId) {
                                    for (scheduleDetails in medicineDetails.medicationScheduleList) {
                                        if (inTakeObject.scheduleID == scheduleDetails.scheduleId) {
                                            scheduleDetails.status = inTakeObject.status
                                            scheduleDetails.medicationInTakeID =
                                                inTakeObject.medicationInTakeID
                                            Utilities.printLogError("Intake Updated.....")
                                        }
                                    }
                                }
                            }
                        }
                    }
                    updateAndCancelNotification(medicationInTake[0].status)
                    //meddashboardFragment!!.updateMedicatinesList()
                }
            }
        } else {
            Utilities.printLogError("Error while fetching Response from server")
        }
    }

    private fun getMedInTakeFromNotificationByScheduleIDApi() {
        val notificationData = MedicationSingleton.getInstance()!!.getNotificationIntent()
        val scheduleID = notificationData.getStringExtra(Constants.SERVER_SCHEDULE_ID)!!
        val recordDate = notificationData.getStringExtra(Constants.DATE)!!
        val jsonObjectJSONData = JSONObject()
        jsonObjectJSONData.put("ScheduleID", scheduleID)
        jsonObjectJSONData.put("RecordDate", recordDate)
        getResponseFromServer(jsonObjectJSONData, "2")
    }

    private fun updateAndCancelNotification(takeStatus: String) {
        try {
            val notificationData = MedicationSingleton.getInstance()!!.getNotificationIntent()
            val notificationID = notificationData.getIntExtra(Constants.NOTIFICATION_ID, -1)
            val medName = notificationData.getStringExtra(Constants.MEDICINE_NAME)
            Utilities.printLog("notificationData=>$notificationData")
            if (notificationID != -1 && !Utilities.isNullOrEmpty(medName)) {
                var text = "You have $takeStatus : $medName"
                when(takeStatus) {
                    "Taken" -> {
                        text = "${localResource.getString(R.string.YOU_HAVE_TAKEN)} : $medName"
                    }
                    "Skipped" -> {
                        text = "${localResource.getString(R.string.YOU_HAVE_SKIPPED)} : $medName"
                    }
                }

                //Cancel notification
                cancelNotification(notificationManager, notificationID)
                val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                //Update the Notification to show that the Status has been Updated.
                val repliedNotification = NotificationCompat.Builder(context!!, "fcm_medication_channel")
                    .setSmallIcon(R.drawable.notification_logo)
                    //.setColor(ContextCompat.getColor(context!!, R.color.colorPrimary))
                    .setSound(alarmSound)
                    .setTicker(context!!.resources.getString(R.string.app_name))
                    .setContentTitle(text)
                notificationManager.notify(notificationID, repliedNotification.build())
                /*                Handler(Looper.getMainLooper()).postDelayed({
                                    cancelNotification(notificationManager, notificationID)
                                }, 2000)*/
                /*                Timer().schedule(2000){
                                    cancelNotification(notificationManager, notificationID)
                                }*/
                //cancelNotification(notificationManager, notificationID)
                //MedicationSingleton.getInstance()!!.setNotificationIntent(Intent())
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getEncryptedRequestBody(request: String): RequestBody {
        val encryptedReq = EncryptionUtility.encrypt(request)
        return RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), encryptedReq)
        //return encryptedReq.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
    }

    private fun getResponse(response: String): String {
        val decrypted = EncryptionUtility.decrypt(response)
        val decryptedResponse: String = decrypted
            .replace("\\r\\n", "")
            .replace("\\\"", "\"")
            .replace("\\\\\"", "\"")
            .replace("\"{", "{")
            .replace("\"[", "[")
            .replace("}\"", "}")
            .replace("]\"", "]")
        return decryptedResponse
    }

    private fun cancelNotification(manager: NotificationManager, notificationId: Int) {
        manager.cancel(notificationId)
    }

    private fun closeNotificationDrawer(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            context!!.sendBroadcast(Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS))
        }
    }

}