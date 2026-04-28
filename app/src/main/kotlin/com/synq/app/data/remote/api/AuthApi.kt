package com.synq.app.data.remote.api
import com.synq.app.data.remote.dto.OtpRequestDto
import com.synq.app.data.remote.dto.OtpResponseDto
import com.synq.app.data.remote.dto.VerifyRequestDto
import com.synq.app.data.remote.dto.VerifyResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
interface AuthApi {
    @POST("auth/otp/request") suspend fun requestOtp(@Body request: OtpRequestDto): Response<OtpResponseDto>
    @POST("auth/otp/verify") suspend fun verifyOtp(@Body request: VerifyRequestDto): Response<VerifyResponseDto>
}
