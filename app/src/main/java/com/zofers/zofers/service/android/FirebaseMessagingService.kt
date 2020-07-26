package com.zofers.zofers.service.android

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.Person
import androidx.core.graphics.drawable.IconCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.zofers.zofers.R
import com.zofers.zofers.App
import com.zofers.zofers.firebase.FirebaseService
import com.zofers.zofers.model.Message
import com.zofers.zofers.ui.home.HomeActivity
import java.io.IOException
import java.net.URL


class FirebaseMessagingService : FirebaseMessagingService() {

	private val firebaseService = FirebaseService()
	/**
	 * Called when message is received.
	 *
	 * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
	 */
	// [START receive_message]
	override fun onMessageReceived(remoteMessage: RemoteMessage) {
		// [START_EXCLUDE]
		// There are two types of messages data messages and notification messages. Data messages are handled
		// here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
		// traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
		// is in the foreground. When the app is in the background an automatically generated notification is displayed.
		// When the user taps on the notification they are returned to the app. Messages containing both notification
		// and data payloads are treated as notification messages. The Firebase console always sends notification
		// messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
		// [END_EXCLUDE]

		// Not getting messages here? See why this may be: https://goo.gl/39bRNJ
		Log.d(TAG, "From: ${remoteMessage.from}")

		// Check if message contains a data payload.
		Log.d(TAG, "Message data payload: " + remoteMessage.data)

		if (/* Check if data needs to be processed by long running job */ false) {
			// For long-running tasks (10 seconds or more) use WorkManager.
			scheduleJob()
		} else {
			try {
				val notificationMessage = remoteMessage.data["message"]
				val title = remoteMessage.data["title"]
				val imageUrl = remoteMessage.data["imageUrl"]
				val data = remoteMessage.data["data"]
//				val message = Gson().fromJson(data, Message::class.java) // date syntax error
				if (!(application as App).isMessengerActive) {
					sendNotification(notificationMessage.orEmpty(), title, imageUrl, imageUrl.hashCode() + title.hashCode())
				}

			} catch (e: NullPointerException) {
				Log.e(TAG, "onMessageReceived: NullPointerException: " + e.message)
			}
			// Handle message within 10 seconds
		}


		// Check if message contains a notification payload.
//		remoteMessage.notification?.body?.let {
//			Log.d(TAG, "Message Notification Body: ${it}")
//			sendNotification(it)
//		}

	}
	// [END receive_message]

	// [START on_new_token]
	/**
	 * Called if InstanceID token is updated. This may occur if the security of
	 * the previous token had been compromised. Note that this is called when the InstanceID token
	 * is initially generated so this is where you would retrieve the token.
	 */
	override fun onNewToken(token: String) {
		Log.d(TAG, "Refreshed token: $token")

		// If you want to send messages to this application instance or
		// manage this apps subscriptions on the server side, send the
		// Instance ID token to your app server.
		sendRegistrationToServer(token)
	}
	// [END on_new_token]

	/**
	 * Schedule async work using WorkManager.
	 */
	private fun scheduleJob() {

	}

	/**
	 * Persist token to third-party servers.
	 *
	 * Modify this method to associate the user's FCM InstanceID token with any server-side account
	 * maintained by your application.
	 *
	 * @param token The new token.
	 */
	private fun sendRegistrationToServer(token: String?) {
		firebaseService.saveDeviceToken(token)
		Log.d(TAG, "sendRegistrationTokenToServer($token)")
	}

	/**
	 * Create and show a simple notification containing the received FCM message.
	 *
	 * @param messageBody FCM message body received.
	 */
	private fun sendNotification(messageBody: String, title: String?, iconUri: String?, notifID: Int) {
		val intent = Intent(this, HomeActivity::class.java)
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
		intent.putExtra(HomeActivity.EXTRA_OPENING_TAB, HomeActivity.OPENING_TAB_NOTIFICATION)
		val pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
				PendingIntent.FLAG_ONE_SHOT)

		val icon = try {
			val url = URL(iconUri)
			val input = url.openStream()
			IconCompat.createWithBitmap(BitmapFactory.decodeStream(input))
		} catch (e: IOException) {
			null
		}

		val style: NotificationCompat.MessagingStyle =
				restoreMessagingStyle(this, notifID)
						?: run {
							val user: Person = Person.Builder().setIcon(icon).setName(title).build()
							NotificationCompat.MessagingStyle(user)
						}

		style
				.addMessage(messageBody, System.currentTimeMillis(), style.user)
				.isGroupConversation = false

		val channelId = "fuckin_channel_id_45"
		val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
		val notificationBuilder = NotificationCompat.Builder(this, channelId)
				.setContentText(messageBody)
				.setSmallIcon(R.mipmap.ic_notification)
				.setAutoCancel(true)
				.setSound(defaultSoundUri)
				.setContentIntent(pendingIntent)
				.setStyle(style)
		if (!title.isNullOrEmpty()) {
			notificationBuilder.setContentTitle(title)
		}

		val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

		// Since android Oreo notification channel is needed.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			val channel = NotificationChannel(channelId,
					"Channel human readable title",
					NotificationManager.IMPORTANCE_DEFAULT)
			notificationManager.createNotificationChannel(channel)
		}

		notificationManager.notify(notifID /* ID of notification */, notificationBuilder.build())
	}


	private fun restoreMessagingStyle(context: Context, notificationId: Int): NotificationCompat.MessagingStyle? {
		return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			(context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
					.activeNotifications
					.find { it.id == notificationId }
					?.notification
					?.let { NotificationCompat.MessagingStyle.extractMessagingStyleFromNotification(it) }
		} else {
			return null
		}
	}

	companion object {

		private const val TAG = "MyFirebaseMsgService"
	}
}