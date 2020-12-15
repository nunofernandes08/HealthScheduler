package healthscheduler.example.healthscheduler.helpers

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class Notification : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        remoteMessage.notification?.let{
            Log.d("Message", "Notification: ${it.body}")
        }
    }

}