package weather.app.live.update.forecast.services

import android.content.Context
import androidx.core.app.NotificationCompat
import com.onesignal.OSMutableNotification
import com.onesignal.OSNotification
import com.onesignal.OSNotificationReceivedEvent
import com.onesignal.OneSignal

class MyNotificationExtenderService: OneSignal.OSRemoteNotificationReceivedHandler {

//    OS_7a44f2be-483e-4c2a-a8f9-4a66f674350a==========for general
//    OS_b0c78f9f-0923-4a8d-af71-6ac97d39b990==========for silent

    private var mutableNotification: OSMutableNotification?=null
    private var title: String?=null
    private var description: String?=null
    private var sendingTime: String?=null


    override fun remoteNotificationReceived(context: Context?, notificationReceivedEvent: OSNotificationReceivedEvent?) {
        if (context!=null && notificationReceivedEvent!=null) {
            val osNotification: OSNotification =notificationReceivedEvent.notification
            mutableNotification=osNotification.mutableCopy()


            title=mutableNotification?.title
            description=mutableNotification?.body
            val launchUrl: String?=mutableNotification?.launchURL


            mutableNotification?.setExtender(object : NotificationCompat.Extender {
                override fun extend(builder: NotificationCompat.Builder): NotificationCompat.Builder {
                    return builder
                }
            })
        }
        if (description!=null && description!!.contains("... Click here",true)) {
            notificationReceivedEvent?.complete(mutableNotification)
        }else {
            notificationReceivedEvent?.complete(null)
        }
    }


}