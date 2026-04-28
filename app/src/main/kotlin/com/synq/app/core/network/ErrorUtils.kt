package com.synq.app.core.network

import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

fun Throwable.toUserFriendlyMessage(): String {
    return when (this) {
        is UnknownHostException -> "No internet connection. Please check your network and try again."
        is ConnectException -> "Unable to connect to the server. Please try again later."
        is SocketTimeoutException -> "The connection timed out. Please try again."
        else -> this.localizedMessage ?: "An unexpected error occurred."
    }
}
