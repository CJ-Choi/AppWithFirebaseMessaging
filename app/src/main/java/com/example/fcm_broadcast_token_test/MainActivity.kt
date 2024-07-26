package com.example.fcm_broadcast_token_test

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.google.firebase.messaging.FirebaseMessaging
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.fcm_broadcast_token_test.InternalDB.FirebaseMessageStorageHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private val tokenReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val token = intent?.getStringExtra("data") ?: return
            val driverNo = intent.getIntExtra("driverNo", -1)

            Log.d("MainActivity", "FCM 토큰 수신: $token, Driver No: $driverNo")

            // 네트워크 작업 코루틴으로 처리
            lifecycleScope.launch {
                sendTokenToServer(token, driverNo)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Firebase 초기화 및 토큰 요청
        setupFirebaseMessaging()

        // 브로드캐스트 리시버 등록
        LocalBroadcastManager.getInstance(this).registerReceiver(
            tokenReceiver, IntentFilter("com.example.broadcast.TOKEN_BROADCAST")
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(tokenReceiver)
    }

    private fun setupFirebaseMessaging() {
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("MainActivity", "Fetching FCM registration token failed", task.exception)
                    return@addOnCompleteListener
                }

                val token = task.result
                Log.d("MainActivity", "FCM registration token: $token")

                // 브로드캐스트 송신
                Intent().also { intent ->
                    intent.action = "com.example.broadcast.TOKEN_BROADCAST"
                    intent.putExtra("data", token)
                    intent.putExtra("driverNo", 12345) // 예제 driverNo 설정
                    LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
                }
            }
    }

    private suspend fun sendTokenToServer(token: String, driverNo: Int) {
        val retrofit = RetrofitInstance.api
        val tokenRequest = TokenRequest(token, driverNo)

        try {
            val response = retrofit.sendToken(tokenRequest)
            Log.d("MainActivity", "Token sent successfully: ${response.message}")
        } catch (e: Exception) {
            Log.e("MainActivity", "Exception in sending token: $e")
        }
    }
}