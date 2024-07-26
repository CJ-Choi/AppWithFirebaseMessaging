package com.example.fcm_broadcast_token_test

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("sendToken")
    suspend fun sendToken(@Body request: TokenRequest): TokenResponse
}

data class TokenRequest(val token: String, val driverNo: Int)
data class TokenResponse(val success: Boolean, val message: String)
