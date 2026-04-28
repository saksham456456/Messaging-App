package com.synq.app.data.remote.api

import com.synq.app.data.remote.dto.UpdateProfileRequestDto
import com.synq.app.data.remote.dto.UserProfileDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface UserApi {
    @POST("users/profile")
    suspend fun updateProfile(@Body request: UpdateProfileRequestDto): Response<UserProfileDto>

    @GET("users/profile")
    suspend fun getProfile(): Response<UserProfileDto>
}
