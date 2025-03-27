package com.uniguard.bustracker.core.data.datasource.remote

import com.uniguard.bustracker.core.data.model.User
import retrofit2.http.GET
import retrofit2.http.Path

interface APIService {
    @GET("api/users/{uid}")
    suspend fun getUserByUid(@Path("uid") uid: String): User
}