package com.uniguard.bustracker.core.data.datasource.remote

import com.uniguard.bustracker.core.data.model.User
import com.uniguard.bustracker.core.data.model.request.UserLocationRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface APIService {
    @POST("api/user-location")
    suspend fun getUserByUid(@Body request: UserLocationRequest): User
}
