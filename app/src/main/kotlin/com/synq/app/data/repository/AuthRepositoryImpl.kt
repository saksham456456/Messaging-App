package com.synq.app.data.repository

import com.synq.app.core.network.TokenManager
import com.synq.app.core.network.toUserFriendlyMessage
import com.synq.app.core.result.AppResult
import com.synq.app.data.remote.api.AuthApi
import com.synq.app.data.remote.api.UserApi
import com.synq.app.data.remote.dto.OtpRequestDto
import com.synq.app.data.remote.dto.UpdateProfileRequestDto
import com.synq.app.data.remote.dto.VerifyRequestDto
import com.synq.app.domain.repository.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authApi: AuthApi,
    private val userApi: UserApi,
    private val tokenManager: TokenManager
) : AuthRepository {

    override suspend fun requestOtp(phoneNumber: String): AppResult<Boolean> = withContext(Dispatchers.IO) {
        try {
            val response = authApi.requestOtp(OtpRequestDto(phoneNumber))
            if (response.isSuccessful) {
                AppResult.Success(response.body()?.success ?: false)
            } else {
                AppResult.Error("Failed to send OTP.")
            }
        } catch (e: Exception) {
            AppResult.Error(e.toUserFriendlyMessage(), e)
        }
    }

    override suspend fun verifyOtp(phoneNumber: String, otp: String): AppResult<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = authApi.verifyOtp(VerifyRequestDto(phoneNumber, otp))
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                tokenManager.saveToken(body.accessToken)
                tokenManager.saveUserId(body.userId)
                tokenManager.setProfileComplete(false)
                AppResult.Success(Unit)
            } else {
                AppResult.Error("Failed to verify OTP.")
            }
        } catch (e: Exception) {
            AppResult.Error(e.toUserFriendlyMessage(), e)
        }
    }

    override suspend fun updateProfile(name: String): AppResult<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = userApi.updateProfile(UpdateProfileRequestDto(name))
            if (response.isSuccessful) {
                tokenManager.setProfileComplete(true)
                AppResult.Success(Unit)
            } else {
                AppResult.Error("Failed to save profile.")
            }
        } catch (e: Exception) {
            AppResult.Error(e.toUserFriendlyMessage(), e)
        }
    }

    override suspend fun logout() {
        tokenManager.saveToken(null)
        tokenManager.saveUserId(null)
        tokenManager.setProfileComplete(false)
    }

    override fun isLoggedIn(): Boolean = tokenManager.getToken() != null
    override fun isProfileComplete(): Boolean = tokenManager.isProfileComplete()
}
