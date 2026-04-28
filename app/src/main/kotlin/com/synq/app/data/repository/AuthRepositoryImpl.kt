package com.synq.app.data.repository
import com.synq.app.core.network.TokenManager
import com.synq.app.core.result.AppResult
import com.synq.app.data.remote.api.AuthApi
import com.synq.app.data.remote.dto.OtpRequestDto
import com.synq.app.data.remote.dto.VerifyRequestDto
import com.synq.app.domain.repository.AuthRepository
import javax.inject.Inject
class AuthRepositoryImpl @Inject constructor(private val authApi: AuthApi, private val tokenManager: TokenManager) : AuthRepository {
    override suspend fun requestOtp(phoneNumber: String): AppResult<Boolean> {
        return try { val response = authApi.requestOtp(OtpRequestDto(phoneNumber)); if (response.isSuccessful) AppResult.Success(response.body()?.success ?: false) else AppResult.Error("Failed: ${response.message()}") } catch (e: Exception) { AppResult.Error(e.message ?: "Network error", e) }
    }
    override suspend fun verifyOtp(phoneNumber: String, otp: String): AppResult<Unit> {
        return try { val response = authApi.verifyOtp(VerifyRequestDto(phoneNumber, otp)); if (response.isSuccessful && response.body() != null) { val body = response.body()!!; tokenManager.saveToken(body.accessToken); tokenManager.saveUserId(body.userId); AppResult.Success(Unit) } else AppResult.Error("Failed: ${response.message()}") } catch (e: Exception) { AppResult.Error(e.message ?: "Network error", e) }
    }
    override suspend fun logout() { tokenManager.saveToken(null); tokenManager.saveUserId(null) }
    override fun isLoggedIn(): Boolean = tokenManager.getToken() != null
}
