package com.uniguard.bustracker.core.data.repository

import com.uniguard.bustracker.core.data.datasource.remote.APIService
import com.uniguard.bustracker.core.data.model.User
import com.uniguard.bustracker.core.data.model.request.UserLocationRequest
import com.uniguard.bustracker.core.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val apiService: APIService
) : UserRepository {
    override suspend fun getUserByUid(params: UserLocationRequest): Flow<User> {
        return flowOf(apiService.getUserByUid(params))
    }
} 