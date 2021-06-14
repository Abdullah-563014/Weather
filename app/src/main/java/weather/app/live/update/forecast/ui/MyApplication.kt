package weather.app.live.update.forecast.ui

import android.app.Application
import android.content.Intent
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.initialization.InitializationStatus
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.ktx.Firebase
import com.onesignal.OSNotificationReceivedEvent
import com.onesignal.OneSignal
import weather.app.live.update.forecast.ui.splash.SplashActivity
import weather.app.live.update.forecast.utils.Constants


class MyApplication: Application() {


    lateinit var firebaseAnalytics: FirebaseAnalytics


    override fun onCreate() {
        super.onCreate()


        FirebaseApp.initializeApp(this)
        firebaseAnalytics = Firebase.analytics
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)


        MobileAds.initialize(this) {

        }

        initOneSignal()






    }


    private fun initOneSignal() {
        OneSignal.initWithContext(this)
        OneSignal.setAppId(Constants.onesignalApplicationId)
        OneSignal.setNotificationWillShowInForegroundHandler { notificationReceivedEvent: OSNotificationReceivedEvent ->
            val notification = notificationReceivedEvent.notification
            val data = notification.additionalData

            notificationReceivedEvent.complete(notification)
        }
        OneSignal.setNotificationOpenedHandler { result -> result?.let {
            val launchUrl: String = it.notification.launchURL
            val notificationIntent: Intent =
                Intent(applicationContext, SplashActivity::class.java)
//            notificationIntent.putExtra(Constants.targetUrl, launchUrl)
            notificationIntent.flags =
                Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(notificationIntent)
        }
        }
        OneSignal.unsubscribeWhenNotificationsAreDisabled(true)
        OneSignal.pauseInAppMessages(true)
        OneSignal.setLocationShared(false)
    }


}