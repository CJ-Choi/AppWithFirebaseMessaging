package com.example.fcm_broadcast_token_test

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.fcm_broadcast_token_test.InternalDB.FirebaseMessageStorageHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TokenBroadcastReceiver : BroadcastReceiver() {
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    override fun onReceive(context: Context?, intent: Intent?) {
        val token = intent?.getStringExtra("data")
        val driverNo = intent?.getIntExtra("driverNo", -1) ?: -1

        if (context != null && token != null && driverNo != -1) {
            coroutineScope.launch {
                sendTokenToServer(token, driverNo)
            }

            // Optional: Save the token to local database
            val dbHelper = FirebaseMessageStorageHelper(context)
            dbHelper.setTokenData(token, driverNo)
        }
    }

    private suspend fun sendTokenToServer(token: String, driverNo: Int) {
        val retrofit = RetrofitInstance.api
        val tokenRequest = TokenRequest(token, driverNo)

        try {
            val response = retrofit.sendToken(tokenRequest)
            Log.d("TokenBroadcastReceiver", "Token sent successfully: ${response.message}")
        } catch (e: Exception) {
            Log.e("TokenBroadcastReceiver", "Exception in sending token: $e")
        }
    }
}