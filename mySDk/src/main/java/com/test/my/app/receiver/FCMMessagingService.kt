package com.test.my.app.receiver

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import com.test.my.app.R
import com.test.my.app.common.constants.Constants
import com.test.my.app.common.constants.NavigationConstants
import com.test.my.app.common.constants.PreferenceConstants
import com.test.my.app.common.utils.DateHelper
import com.test.my.app.common.utils.Utilities
import com.test.my.app.home.viewmodel.BackgroundCallViewModel
import com.test.my.app.medication_tracker.common.MedicationTrackerHelper
import com.test.my.app.medication_tracker.model.ReminderNotification
import com.test.my.app.medication_tracker.viewmodel.MedicineTrackerViewModel
import com.clevertap.android.sdk.CleverTapAPI
import com.clevertap.android.sdk.pushnotification.fcm.CTFcmMessageHandler
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class FCMMessagingService : FirebaseMessagingService() {

    private val tag = "FCMMessagingService : "
    private var medNotification = ReminderNotification()
    @Inject
    lateinit var viewModel: MedicineTrackerViewModel
    @Inject
    lateinit var backgroundApiCallViewModel: BackgroundCallViewModel

    override fun onNewToken(token: String) {
        Utilities.printLogError("$tag Refreshed token--->$token")
        sendRegistrationToServer(token)
        // Send the token to CleverTap
        CleverTapAPI.getDefaultInstance(applicationContext)?.pushFcmRegistrationId(token,true)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        try {
            //val notification = remoteMessage.notification
            val data = remoteMessage.data
            Utilities.printData("onMessageReceived(FCMMessagingService): data",data,true)

            val extras = Bundle()
            if (data.isNotEmpty()) {
                for ((key, value) in data.entries) {
                    extras.putString(key, value)
                }
            }
            val info = CleverTapAPI.getNotificationInfo(extras)
            Utilities.printData("info",info)

            if (viewModel.isUserLoggedIn()) {
                if ( data.containsKey("Action") && !Utilities.isNullOrEmpty(data["Action"]!!) && data["Action"]!!.equals("HEALTHTIPS",ignoreCase = true)) {
                    showHealthTipNotification(this, data)
                } else if ( data.containsKey("Screen") && !Utilities.isNullOrEmpty(data["Screen"]!!) ) {
                    when(data["Screen"]!!) {
                        "MEDICATION_REMINDER" -> {
                            showMedicineReminderNotification(data)
                        }
                        "WATER_REMINDER","WATER_REMINDER_21_POSITIVE","WATER_REMINDER_21_NEGATIVE" -> {
                            val personId = data["personid"]
                            if ( personId == backgroundApiCallViewModel.getMainUserPersonID() ) {
                                showWaterReminderNotificationCustom(this,data)
                            } else {
                                Utilities.printLogError("Person Details not Exist for PersonID--->$personId")
                            }
                        }
                        Constants.SCREEN_FEATURE_CAMPAIGN,Constants.SCREEN_INTERNAL_URL_CAMPAIGN,Constants.SCREEN_EXTERNAL_URL_CAMPAIGN -> {
                            showFeatureCampaignNotification(this,data)
                            CleverTapAPI.getDefaultInstance(this)!!.pushNotificationViewedEvent(extras)
                        }
                        "STEPS","STEPS_WEEKLY_SYNOPSIS","STEPS_DAILY_TARGET" -> {
                            Utilities.printLogError("Fitness Tracker Notifications Disabled")
                        }
                        else -> {
                            displayAppNavigationNotification(this,data)
                        }
                    }
                } else if ( data.containsKey(Constants.NOTIFICATION_TYPE) && !Utilities.isNullOrEmpty(data[Constants.NOTIFICATION_TYPE]!!) ) {
                    showAktivoNotification(this,remoteMessage)
                } else if ( data.containsKey(Constants.TYPE) && !Utilities.isNullOrEmpty(data[Constants.TYPE]!!) ) {
                    showAktivoNotification(this,remoteMessage)
                } else {
                    if (data.isNotEmpty()) {
                        if (info.fromCleverTap) {
                            val channelId = Constants.channelIdCleverTap
                            val channelName = Constants.channelNameCleverTap
                            Utilities.createNotificationChannel(this,channelId,channelName)
                            CTFcmMessageHandler().createNotification(applicationContext,remoteMessage)
                            //CleverTapAPI.createNotification(applicationContext,extras)
                        } else {
                            // not from CleverTap handle yourself or pass to another provider
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("MissingPermission")
    private fun showHealthTipNotification(context:Context,data:Map<String,String>) {
        try {
            val action = data["Action"]!!
            val title = data["title"]
            val message = data["text"]
            val imageURL = data["ImageURL"]
            Utilities.printLogError("Action--->$action")

            val notificationId = Utilities.generateNotificationId()
            val channelId = Constants.channelIdYM
            val channelName = Constants.channelNameYM
            val notificationManager = Utilities.createNotificationChannel(context,channelId,channelName)

            // Onclick of Notification Intent
            val onClick = createOnClickIntent(action,action,title!!,message!!,notificationId)
            if (!Utilities.isNullOrEmpty(imageURL)) {
                onClick.putExtra(Constants.NOTIFICATION_URL, imageURL)
            }
            val pendingOnClickIntent: PendingIntent? = TaskStackBuilder.create(this).run {
                // Add the intent, which inflates the back stack
                addNextIntentWithParentStack(onClick)
                // Get the PendingIntent containing the entire back stack
                getPendingIntent(notificationId,PendingIntent.FLAG_UPDATE_CURRENT or
                        // mutability flag required when targeting Android12 or higher
                        PendingIntent.FLAG_IMMUTABLE)
            }

            val builder = NotificationCompat.Builder(this,channelId)
                .setSmallIcon(R.drawable.notification_logo)
                //.setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                //.setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher_round))
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(NotificationCompat.BigTextStyle().bigText(message))
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setTicker(context.resources.getString(R.string.app_name))
                .setContentIntent(pendingOnClickIntent)
                .build()

            Utilities.printLogError("displaying Notification")
            notificationManager.apply {
                // notificationId is a unique int for each notification that you must define
                notify(notificationId,builder)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showMedicineReminderNotification(data:Map<String,String>) {
        try {
            val screen = data["Screen"]
            if (!Utilities.isNullOrEmpty(screen)) {
                Utilities.printLogError("Screen--->$screen")
                val details = JSONObject(data["Body"]!!)
                val personId = details.getString("PersonID")
                medNotification = ReminderNotification()
                medNotification.action = Constants.MEDICATION
                medNotification.personID = personId
                medNotification.medicineName = details.getString("Name")
                medNotification.dosage = details.getString("Dosage")
                medNotification.instruction = details.getString("Instruction")
                medNotification.scheduleTime = details.getString("ScheduleTime")
                medNotification.medicationID = details.getString("MedicationID")
                medNotification.scheduleID = details.getString("ScheduleID")
                medNotification.drugTypeCode = details.getString("DrugTypeCode")
                medNotification.notificationDate = details.getString("NotificationDate").split("T").toTypedArray()[0]
                if ( personId == backgroundApiCallViewModel.getMainUserPersonID() ) {
                    val name = Utilities.preferenceUtils.getPreference(PreferenceConstants.FIRSTNAME, "")
                    MedicationTrackerHelper(this).displayMedicineReminderNotification(this,medNotification,name)
                } else {
                    Utilities.printLogError("Person Details not Exist for PersonID--->$personId")
                }
                //viewModel.checkPersonExistAndShowNotification(this, medNotification)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("MissingPermission")
    private fun showWaterReminderNotificationCustom(context:Context,data:Map<String, String>) {
        try {
            //val personId = data["personid"]
            val action = data["Action"]
            val screen = data["Screen"]
            val title = data["title"]
            val message = data["body"]
            val timeToDisplay: String = DateHelper.getTimeIn12HrFormatAmOrPm(DateHelper.currentTimeAs_hh_mm_ss)
            Utilities.printLogError("Screen--->$screen")

            val notificationId = Utilities.generateNotificationId()
            val channelId = Constants.channelIdYM
            val channelName = Constants.channelNameYM
            val notificationManager = Utilities.createNotificationChannel(context,channelId,channelName)

            // Onclick of Notification Intent
            val onClick = createOnClickIntent(screen!!,action!!,title!!,message!!,notificationId)
            val pendingOnClickIntent: PendingIntent? = TaskStackBuilder.create(this).run {
                // Add the intent, which inflates the back stack
                addNextIntentWithParentStack(onClick)
                // Get the PendingIntent containing the entire back stack
                getPendingIntent(notificationId,PendingIntent.FLAG_UPDATE_CURRENT or
                        // mutability flag required when targeting Android12 or higher
                        PendingIntent.FLAG_IMMUTABLE)
            }

            // Notification's Collapsed layout
            val remoteViewCollapsed = RemoteViews(packageName, R.layout.water_notification_collapsed)
            remoteViewCollapsed.setTextViewText(R.id.water_notification_title, title)
            remoteViewCollapsed.setTextViewText(R.id.water_notification_subtext, message)
            //remoteViewCollapsed.setTextViewText(R.id.txt_time, timeToDisplay)
            if ( screen == "WATER_REMINDER_21_POSITIVE" ) {
                remoteViewCollapsed.setImageViewResource(R.id.img_water,R.drawable.img_water_reminder_achieved)
            } else {
                remoteViewCollapsed.setImageViewResource(R.id.img_water,R.drawable.img_water_reminder)
            }
            remoteViewCollapsed.setOnClickPendingIntent(R.id.layout_water_notification_collapsed, pendingOnClickIntent)

            // Notification's Expanded layout
            val remoteViewExpanded = RemoteViews(packageName, R.layout.water_notification_expanded)
            remoteViewExpanded.setTextViewText(R.id.water_notification_title, title)
            remoteViewExpanded.setTextViewText(R.id.water_notification_subtext, message)
            //remoteViewExpanded.setTextViewText(R.id.txt_time, timeToDisplay)
            if ( screen == "WATER_REMINDER_21_POSITIVE" ) {
                remoteViewExpanded.setImageViewResource(R.id.img_water,R.drawable.img_water_reminder_achieved)
            } else {
                remoteViewExpanded.setImageViewResource(R.id.img_water,R.drawable.img_water_reminder)
            }
            remoteViewExpanded.setOnClickPendingIntent(R.id.layout_water_notification_expanded, pendingOnClickIntent)

            // Apply the layouts to the notification
            val customNotification = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.notification_logo)
                .setStyle(NotificationCompat.DecoratedCustomViewStyle())
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setTicker(resources.getString(R.string.app_name))
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

    @SuppressLint("MissingPermission")
    private fun showFeatureCampaignNotification(context:Context,data:Map<String,String>) {
        try {
            val screen = data["Screen"]!!
            val title = data["nt"]!!
            val message = data["nm"]!!
            val deepLinkUrl = data["wzrk_dl"]!!
            var expandableUrl = ""
            if ( data.containsKey("wzrk_bp") ) {
                expandableUrl = data["wzrk_bp"]!!
            }
            var largeUrl = ""
            if ( data.containsKey("ico") ) {
                largeUrl = data["ico"]!!
            }
            Utilities.printLogError("Screen--->$screen")
            Utilities.printLogError("deepLink--->$deepLinkUrl")

            val notificationId = Utilities.generateNotificationId()
            val channelId = Constants.channelIdCleverTap
            val channelName = Constants.channelNameCleverTap
            val notificationManager = Utilities.createNotificationChannel(context,channelId,channelName)

            // Onclick of Notification Intent
            //val onClick = createOnClickFeatureCampaignNotification(screen,deepLink,title,message,notificationId)
            val onClick = Intent()
            onClick.putExtra(Constants.SCREEN, screen)
            onClick.putExtra(Constants.DEEP_LINK, deepLinkUrl)
            onClick.putExtra(Constants.NOTIFICATION_TITLE, title)
            onClick.putExtra(Constants.NOTIFICATION_MESSAGE, message)
            onClick.putExtra(Constants.NOTIFICATION_ID, notificationId)
            onClick.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            onClick.component = ComponentName(NavigationConstants.APPID,NavigationConstants.SPLASH_SCREEN)

            val pendingOnClickIntent: PendingIntent? = TaskStackBuilder.create(this).run {
                // Add the intent, which inflates the back stack
                addNextIntentWithParentStack(onClick)
                // Get the PendingIntent containing the entire back stack
                getPendingIntent(notificationId,PendingIntent.FLAG_UPDATE_CURRENT or
                        // mutability flag required when targeting Android12 or higher
                        PendingIntent.FLAG_IMMUTABLE)
            }

            val builder = NotificationCompat.Builder(this,channelId)
                .setSmallIcon(R.drawable.notification_logo)
                //.setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                //.setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher_round))
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(NotificationCompat.BigTextStyle().bigText(message))
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setTicker(context.resources.getString(R.string.app_name))
                .setContentIntent(pendingOnClickIntent)
            applyImageUrl(builder,largeUrl,expandableUrl)

            Utilities.printLogError("displaying Notification")
            notificationManager.apply {
                // notificationId is a unique int for each notification that you must define
                notify(notificationId,builder.build())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("MissingPermission")
    private fun showAktivoNotification(context:Context,remoteMessage:RemoteMessage) {
        try {
            val notification = remoteMessage.notification!!
            val data = remoteMessage.data
            var notificationType = ""
            if ( data.containsKey(Constants.NOTIFICATION_TYPE) ) {
                notificationType = data[Constants.NOTIFICATION_TYPE]!!
            }
            if ( Utilities.isNullOrEmpty(notificationType) ) {
                if ( data.containsKey(Constants.TYPE) ) {
                    notificationType = data[Constants.TYPE]!!
                }
            }
            //val notificationType = data[Constants.TYPE]!!
            val message = notification.body
            //val notificationType = data["notificationType"]!!
            //val message = data["template"]
            Utilities.printLogError("Aktivo_Notification_Type--->$notificationType")

            val notificationId = Utilities.generateNotificationId()
            val channelId = Constants.channelIdYM
            val channelName = Constants.channelNameYM
            val notificationManager = Utilities.createNotificationChannel(context,channelId,channelName)

            // Onclick of Notification Intent
            val onClick = createOnClickIntentAktivo(data,notificationType,message!!,notificationId)
            val pendingOnClickIntent: PendingIntent? = TaskStackBuilder.create(this).run {
                // Add the intent, which inflates the back stack
                addNextIntentWithParentStack(onClick)
                // Get the PendingIntent containing the entire back stack
                getPendingIntent(notificationId,PendingIntent.FLAG_UPDATE_CURRENT or
                        // mutability flag required when targeting Android12 or higher
                        PendingIntent.FLAG_IMMUTABLE)
            }

            val builder = NotificationCompat.Builder(this,channelId)
                .setSmallIcon(R.drawable.notification_logo)
                //.setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                //.setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher_round))
                //.setContentTitle(notificationType)
                .setContentText(message)
                .setStyle(NotificationCompat.BigTextStyle().bigText(message))
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setTicker(context.resources.getString(R.string.app_name))
                .setContentIntent(pendingOnClickIntent)
                .build()

            Utilities.printLogError("displaying Notification")
            notificationManager.apply {
                // notificationId is a unique int for each notification that you must define
                notify(notificationId,builder)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("MissingPermission")
    private fun displayAppNavigationNotification(context:Context,data:Map<String,String>) {
        try {
            val action = data["Action"]
            val screen = data["Screen"]
            val title = data["title"]
            val message = data["body"]
            //val message = Html.fromHtml(data["body"])
            Utilities.printLogError("Screen--->$screen")

            val notificationId = Utilities.generateNotificationId()
            val channelId = Constants.channelIdYM
            val channelName = Constants.channelNameYM
            val notificationManager = Utilities.createNotificationChannel(context,channelId,channelName)

            // Onclick of Notification Intent
            val onClick = createOnClickIntent(screen!!,action!!,title!!,message!!,notificationId)
            val pendingOnClickIntent: PendingIntent? = TaskStackBuilder.create(this).run {
                // Add the intent, which inflates the back stack
                addNextIntentWithParentStack(onClick)
                // Get the PendingIntent containing the entire back stack
                getPendingIntent(notificationId,PendingIntent.FLAG_UPDATE_CURRENT or
                        // mutability flag required when targeting Android12 or higher
                        PendingIntent.FLAG_IMMUTABLE)
            }

            val builder = NotificationCompat.Builder(this,channelId)
                .setSmallIcon(R.drawable.notification_logo)
                //.setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                //.setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher_round))
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(NotificationCompat.BigTextStyle().bigText(message))
                .setAutoCancel(true)
                //.setSound(alarmSound)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setTicker(context.resources.getString(R.string.app_name))
                .setContentIntent(pendingOnClickIntent)
                .build()

            Utilities.printLogError("displaying Notification")
            notificationManager.apply {
                // notificationId is a unique int for each notification that you must define
                notify(notificationId,builder)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun applyImageUrl(builder:NotificationCompat.Builder,largeUrl:String,expandableUrl:String) = runBlocking {
        try {
            withContext(Dispatchers.IO) {
                if ( !Utilities.isNullOrEmpty(largeUrl) ) {
                    val url = URL(largeUrl)
                    val input = url.openStream()
                    val bitmap = BitmapFactory.decodeStream(input)
                    Utilities.printLogError("Applying Large ImageUrl")
                    builder.setLargeIcon(bitmap)
                }
                if ( !Utilities.isNullOrEmpty(expandableUrl) ) {
                    val url = URL(expandableUrl)
                    val input = url.openStream()
                    val bitmap = BitmapFactory.decodeStream(input)
                    Utilities.printLogError("Applying Expandable ImageUrl")
                    builder.setStyle(NotificationCompat.BigPictureStyle().bigPicture(bitmap))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

/*    private fun applyImageUrl(builder: NotificationCompat.Builder, imageUrl: String) = runBlocking {
        try {
            val url = URL(imageUrl)
            withContext(Dispatchers.IO) {
                val input = url.openStream()
                val bitmap = BitmapFactory.decodeStream(input)
                Utilities.printLogError("Applying Large ImageUrl")
                builder.setLargeIcon(bitmap)
                *//*                builder.setStyle(NotificationCompat.BigPictureStyle()
                                    .bigPicture(bitmap)
                                    )*//*
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }*/

    private fun sendRegistrationToServer(fcmToken: String) {
        //viewModel2.refreshFcmToken(fcmToken)
        if (viewModel.isUserLoggedIn()) {
            backgroundApiCallViewModel.callSaveCloudMessagingIdApi(fcmToken, true)
        }
    }

    private fun createOnClickIntent(screen:String,action:String,title:String,message:String,notificationId:Int) : Intent {
        val onClick = Intent()
        onClick.putExtra(Constants.SCREEN, screen)
        onClick.putExtra(Constants.NOTIFICATION_ACTION, action)
        onClick.putExtra(Constants.NOTIFICATION_TITLE, title)
        onClick.putExtra(Constants.NOTIFICATION_MESSAGE, message)
        onClick.putExtra(Constants.NOTIFICATION_ID, notificationId)
        onClick.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        onClick.component = ComponentName(NavigationConstants.APPID, NavigationConstants.SPLASH_SCREEN)
        return onClick
    }

    private fun createOnClickIntentAktivo(data:Map<String,String>,notificationType:String,template:String,notificationId:Int) : Intent {
        val onClick = Intent()
        onClick.putExtra(Constants.FROM,Constants.NOTIFICATION)
        onClick.putExtra(Constants.NOTIFICATION_TYPE, notificationType)
        onClick.putExtra(Constants.NOTIFICATION_MESSAGE, template)
        onClick.putExtra(Constants.NOTIFICATION_ID, notificationId)
        if ( notificationType.equals(Constants.CHALLENGE,ignoreCase = true)
            || notificationType.equals(Constants.CHALLENGE_DETAIL,ignoreCase = true) ) {
            if (data.containsKey(Constants.PAYLOAD)) {
                val payload = JSONObject(data[Constants.PAYLOAD]!!)
                if (payload.has(Constants.ID)) {
                    onClick.putExtra(Constants.CHALLENGE_ID, payload.getString(Constants.ID))
                }
            }
        }
        onClick.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        onClick.component = ComponentName(NavigationConstants.APPID, NavigationConstants.SPLASH_SCREEN)
        return onClick
    }

}