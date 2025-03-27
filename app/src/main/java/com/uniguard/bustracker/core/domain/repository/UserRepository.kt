package com.uniguard.bustracker.core.domain.repository

import com.uniguard.bustracker.core.data.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun getUserByUid(uid: String): Flow<User>
}