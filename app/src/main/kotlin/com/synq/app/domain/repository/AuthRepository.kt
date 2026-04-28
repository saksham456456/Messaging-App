package com.synq.app.domain.repository
import com.synq.app.core.result.AppResult
interface AuthRepository {
    suspend fun requestOtp(phoneNumber: String): AppResult<Boolean>
    suspend fun verifyOtp(phoneNumber: String, otp: String): AppResult<Unit>
    suspend fun updateProfile(name: String): AppResult<Unit>
    suspend fun logout()
    fun isLoggedIn(): Boolean
    fun isProfileComplete(): Boolean
}
