package com.uniguard.bustracker.core.domain.repository

import com.uniguard.bustracker.core.data.model.User
import com.uniguard.bustracker.core.data.model.request.UserLocationRequest
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun getUserByUid(params: UserLocationRequest): Flow<User>
}