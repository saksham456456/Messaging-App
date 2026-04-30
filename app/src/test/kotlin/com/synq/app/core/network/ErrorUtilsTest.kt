package com.synq.app.core.network

import org.junit.Assert.assertEquals
import org.junit.Test
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class ErrorUtilsTest {

    @Test
    fun `toUserFriendlyMessage returns correct string for UnknownHostException`() {
        val exception = UnknownHostException()
        val result = exception.toUserFriendlyMessage()
        assertEquals("No internet connection. Please check your network and try again.", result)
    }

    @Test
    fun `toUserFriendlyMessage returns correct string for ConnectException`() {
        val exception = ConnectException()
        val result = exception.toUserFriendlyMessage()
        assertEquals("Unable to connect to the server. Please try again later.", result)
    }

    @Test
    fun `toUserFriendlyMessage returns correct string for SocketTimeoutException`() {
        val exception = SocketTimeoutException()
        val result = exception.toUserFriendlyMessage()
        assertEquals("The connection timed out. Please try again.", result)
    }

    @Test
    fun `toUserFriendlyMessage returns localized message for generic Exception`() {
        val errorMessage = "Something went wrong"
        val exception = Exception(errorMessage)
        val result = exception.toUserFriendlyMessage()
        assertEquals(errorMessage, result)
    }

    @Test
    fun `toUserFriendlyMessage returns fallback string for generic Exception without localized message`() {
        val exception = Exception()
        val result = exception.toUserFriendlyMessage()
        assertEquals("An unexpected error occurred.", result)
    }
}
