package FCM

import android.content.Intent
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.example.fcm_broadcast_token_test.InternalDB.FirebaseMessageStorageHelper

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        // 메시지 수신 로직 처리
        Log.d("MyFirebaseMessagingService", "onMessageReceived: ${remoteMessage.messageId}")
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("MyFirebaseMessagingService", "Refreshed token: $token")

        // 토큰 저장
        val dbHelper = FirebaseMessageStorageHelper(applicationContext)
        dbHelper.setTokenData(token, 12345) // 예제 driverNo

        // 브로드캐스트 송신
        val broadcastIntent = Intent().also { intent ->
            intent.action = "com.example.broadcast.TOKEN_BROADCAST"
            intent.putExtra("data", token)
            intent.putExtra("driverNo", 12345) // 예제 driverNo
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent)
    }
}