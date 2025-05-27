package com.games.gamezone.Registration

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("api/v1/register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>
}