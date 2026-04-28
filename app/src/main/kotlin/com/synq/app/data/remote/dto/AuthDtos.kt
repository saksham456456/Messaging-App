package com.synq.app.data.remote.dto
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
@JsonClass(generateAdapter = true) data class OtpRequestDto(@Json(name = "phoneNumber") val phoneNumber: String)
@JsonClass(generateAdapter = true) data class OtpResponseDto(@Json(name = "success") val success: Boolean)
@JsonClass(generateAdapter = true) data class VerifyRequestDto(@Json(name = "phoneNumber") val phoneNumber: String, @Json(name = "otp") val otp: String)
@JsonClass(generateAdapter = true) data class VerifyResponseDto(@Json(name = "accessToken") val accessToken: String, @Json(name = "refreshToken") val refreshToken: String, @Json(name = "userId") val userId: String)
