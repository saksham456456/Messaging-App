package com.synq.app.data.repository

import com.synq.app.core.network.TokenManager
import com.synq.app.core.result.AppResult
import com.synq.app.data.remote.api.AuthApi
import com.synq.app.data.remote.api.UserApi
import com.synq.app.data.remote.dto.OtpRequestDto
import com.synq.app.data.remote.dto.OtpResponseDto
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class AuthRepositoryImplTest {

    private lateinit var authApi: AuthApi
    private lateinit var userApi: UserApi
    private lateinit var tokenManager: TokenManager
    private lateinit var authRepository: AuthRepositoryImpl

    @Before
    fun setup() {
        authApi = mockk()
        userApi = mockk()
        tokenManager = mockk()
        authRepository = AuthRepositoryImpl(authApi, userApi, tokenManager)
    }

    @Test
    fun `requestOtp should return Success when api response is successful`() = runTest {
        val phoneNumber = "1234567890"
        val expectedResponse = OtpResponseDto(success = true)
        coEvery { authApi.requestOtp(OtpRequestDto(phoneNumber)) } returns Response.success(expectedResponse)

        val result = authRepository.requestOtp(phoneNumber)

        assertTrue(result is AppResult.Success)
        assertEquals(true, (result as AppResult.Success).data)
    }

    @Test
    fun `requestOtp should return Error when api response is not successful`() = runTest {
        val phoneNumber = "1234567890"
        val errorResponseBody = "{\"error\": \"Bad Request\"}".toResponseBody("application/json".toMediaTypeOrNull())
        coEvery { authApi.requestOtp(OtpRequestDto(phoneNumber)) } returns Response.error(400, errorResponseBody)

        val result = authRepository.requestOtp(phoneNumber)

        assertTrue(result is AppResult.Error)
        assertTrue((result as AppResult.Error).message.contains("Failed:"))
    }

    @Test
    fun `requestOtp should return Error when api throws exception`() = runTest {
        val phoneNumber = "1234567890"
        val exception = RuntimeException("Network Error")
        coEvery { authApi.requestOtp(OtpRequestDto(phoneNumber)) } throws exception

        val result = authRepository.requestOtp(phoneNumber)

        assertTrue(result is AppResult.Error)
        // Check for common fallback message, or at least that it's an error containing info.
        // It uses `e.toUserFriendlyMessage()`. Let's just assert it is an Error and the exception is returned.
        assertEquals(exception, (result as AppResult.Error).cause)
    }
}
