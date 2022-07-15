package com.journalmetro.app.services

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.navigation.NavDeepLinkBuilder
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.journalmetro.app.R
import com.journalmetro.app.common.preferences.AppPreferences
import com.journalmetro.app.notifications.NotificationUtility
import com.journalmetro.app.post.convertEmptyPost
import com.journalmetro.app.ui.MainActivity


class AppFirebaseMessagingService : FirebaseMessagingService(), LifecycleObserver {

    lateinit var prefs: SharedPreferences

    private var mNotificationManager: NotificationManager? = null

    lateinit var notificationUtility: NotificationUtility

    private var isAppInForeground = false

    override fun onCreate() {
        super.onCreate()
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        ProcessLifecycleOwner.get().lifecycle.removeObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onForegroundStart() {
        isAppInForeground = true
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onForegroundStop() {
        isAppInForeground = false
    }


    override fun onNewToken(token: String) {
        prefs = this.getSharedPreferences("shared_prefs", Context.MODE_PRIVATE)
        notificationUtility = NotificationUtility(this, prefs)
        savePushNotificationRefreshedToken(token)
    }


    @SuppressLint("ApplySharedPref")
    private fun savePushNotificationRefreshedToken(refreshedToken: String) {
        if (refreshedToken.isNotEmpty()) {
            prefs.edit().putString(
                AppPreferences.PUSH_NOTIFICATIONS_REFRESHED_TOKEN,
                refreshedToken
            ).commit()
        }
    }


    @ExperimentalUnsignedTypes
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.e("remote message", "--${remoteMessage.data}")
        val params: Map<String, String> = remoteMessage.data

        if (params.isNotEmpty()) {
            val type = params[PushNotificationFields.type_Key.stringValue]
            var title = ""
            var content = ""
            var id = ""
            val arguments: Map<String, Any?>
            when(type){

                PushNotificationFields.TypeBreakingNews.stringValue -> {
                    id = params[PushNotificationFields.postID_Key.stringValue]!!
                    title = params[PushNotificationFields.title_key.stringValue]!!
                    content = params[PushNotificationFields.contentKey.stringValue]!!

                    arguments = mapOf<String, Any?>(
                        Pair(PushNotificationFields.title_key.stringValue, title),
                        Pair(PushNotificationFields.contentKey.stringValue, content),
                        Pair(PushNotificationFields.type_Key.stringValue, type),
                        Pair(PushNotificationFields.postID_Key.stringValue, id)
                    )
                    sendNotification(arguments)
                }

                PushNotificationFields.TypeDailyRecap.stringValue -> {
                    title = params[PushNotificationFields.title_key.stringValue]!!
                    content = params[PushNotificationFields.contentKey.stringValue]!!

                    arguments = mapOf<String, Any?>(
                        Pair(PushNotificationFields.title_key.stringValue, title),
                        Pair(PushNotificationFields.contentKey.stringValue, content),
                        Pair(PushNotificationFields.type_Key.stringValue, type)
                    )
                    sendNotification(arguments)
                }
            }

        }
    }

    enum class PushNotificationFields (val stringValue: String) {
        postID_Key("id"),
        type_Key("type"),
        title_key("title"),
        contentKey("body"),
        TypeBreakingNews("BreakingNews"),
        TypeDailyRecap("DailyRecap"),
    }

    private fun areNotificationsEnabled(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (!manager.areNotificationsEnabled()) {
                return false
            }
            val channels = manager.notificationChannels
            for (channel in channels) {
                if (channel.importance == NotificationManager.IMPORTANCE_NONE) {
                    return false
                }
            }
            true
        } else {
            NotificationManagerCompat.from(this).areNotificationsEnabled()
        }
    }

    @ExperimentalUnsignedTypes
    override fun onDeletedMessages() {
        super.onDeletedMessages()
        //sendNotification("Deleted messages on server.", null)
    }

    @ExperimentalUnsignedTypes
    private fun sendNotification(arguments: Map<String, Any?>) {
        if (areNotificationsEnabled()) {
            prefs = this.getSharedPreferences("shared_prefs", Context.MODE_PRIVATE)

            notificationUtility = NotificationUtility(this, prefs)
            mNotificationManager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?

            var destination: Int = 0
            var bundle = Bundle()

            val type = arguments[PushNotificationFields.type_Key.stringValue] as String
            if (type == PushNotificationFields.TypeBreakingNews.stringValue) {

                destination = R.id.articleDetailFragment // Open normal detail page.

                val postIdFromMap = arguments[PushNotificationFields.postID_Key.stringValue]
                var idLong = 0L
                if (postIdFromMap is String) {
                    idLong = postIdFromMap.toLong()
                } else if (postIdFromMap is Long) {
                    idLong = postIdFromMap
                }

                bundle = bundleOf(
                    "post" to idLong.convertEmptyPost(),
                    "isBreakingNews" to true
                ) // Create empty post and set breaking news to true.
            }
            else if(type == PushNotificationFields.TypeDailyRecap.stringValue){
               destination = R.id.notificationsFragment
            }

            val notificationMessage = arguments[PushNotificationFields.contentKey.stringValue].toString()
            val contentIntent = NavDeepLinkBuilder(this)
                .setComponentName(MainActivity::class.java) // your destination activity
                .setGraph(R.navigation.nav_graph_main)
                .setDestination(destination)
                .setArguments(bundle)
                .createPendingIntent()

            val notificationLargeIconBitmap: Bitmap? = BitmapFactory.decodeResource(resources, R.drawable.ic_logo_metro)
            val notificationSmallIconBitmap: Int = R.drawable.ic_logo_metro

            //Notification
            val channelId: String = getString(R.string.default_notifications_channel_id)
            val mBuilder = NotificationCompat.Builder(this, channelId)
//                .setPriority(NotificationManager.IMPORTANCE_HIGH)
                .setAutoCancel(true)
                .setSmallIcon(notificationSmallIconBitmap)
                .setColor(ContextCompat.getColor(this, R.color.colorAccent))
                .setLargeIcon(notificationLargeIconBitmap)
                .setContentTitle(arguments[PushNotificationFields.title_key.stringValue] as String)
                .setContentText(notificationMessage)
                .setStyle(NotificationCompat.BigTextStyle().bigText(notificationMessage))
                .setContentIntent(contentIntent)

            //Light
            mBuilder.setLights(ContextCompat.getColor(this, R.color.colorTextWhite_1), 600, 300)

            //Vibration
            mBuilder.setVibrate(VIBRATION_PATTERN)

            //Notification ID
            val notificationID: Int = notificationUtility.generateNotificationID(this)

            // Since android Oreo notification channel is needed.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    channelId,
                    getString(R.string.default_notifications_channel_id),
                    NotificationManager.IMPORTANCE_HIGH
                )
                mNotificationManager!!.createNotificationChannel(channel)
            }

            //Proceed with notification
            mNotificationManager!!.notify(notificationID, mBuilder.build())
        }
    }
    companion object {
        private val VIBRATION_PATTERN = longArrayOf(1000, 600, 1000)
    }

}