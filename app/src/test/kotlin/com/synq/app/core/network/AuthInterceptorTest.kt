package com.synq.app.core.network

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test

class AuthInterceptorTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var tokenManager: TokenManager
    private lateinit var authInterceptor: AuthInterceptor
    private lateinit var okHttpClient: OkHttpClient

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        tokenManager = mockk(relaxed = true)
        authInterceptor = AuthInterceptor(tokenManager)

        okHttpClient = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .build()
    }

    @After
    fun teardown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `intercept adds Authorization header when token is available`() {
        // Arrange
        val testToken = "test_token_123"
        every { tokenManager.getToken() } returns testToken

        mockWebServer.enqueue(MockResponse().setResponseCode(200))

        val request = Request.Builder()
            .url(mockWebServer.url("/"))
            .build()

        // Act
        okHttpClient.newCall(request).execute()

        // Assert
        val recordedRequest = mockWebServer.takeRequest()
        val authHeader = recordedRequest.getHeader("Authorization")

        assertThat(authHeader).isNotNull()
        assertThat(authHeader).isEqualTo("Bearer $testToken")
    }

    @Test
    fun `intercept does not add Authorization header when token is null`() {
        // Arrange
        every { tokenManager.getToken() } returns null

        mockWebServer.enqueue(MockResponse().setResponseCode(200))

        val request = Request.Builder()
            .url(mockWebServer.url("/"))
            .build()

        // Act
        okHttpClient.newCall(request).execute()

        // Assert
        val recordedRequest = mockWebServer.takeRequest()
        val authHeader = recordedRequest.getHeader("Authorization")

        assertThat(authHeader).isNull()
    }

    @Test
    fun `intercept preserves existing headers`() {
        // Arrange
        val testToken = "test_token_123"
        every { tokenManager.getToken() } returns testToken

        mockWebServer.enqueue(MockResponse().setResponseCode(200))

        val request = Request.Builder()
            .url(mockWebServer.url("/"))
            .addHeader("Custom-Header", "CustomValue")
            .build()

        // Act
        okHttpClient.newCall(request).execute()

        // Assert
        val recordedRequest = mockWebServer.takeRequest()

        val authHeader = recordedRequest.getHeader("Authorization")
        assertThat(authHeader).isEqualTo("Bearer $testToken")

        val customHeader = recordedRequest.getHeader("Custom-Header")
        assertThat(customHeader).isEqualTo("CustomValue")
    }
}
