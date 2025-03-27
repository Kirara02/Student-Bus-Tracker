package com.uniguard.bustracker.core.domain.usecase

import com.uniguard.bustracker.core.data.model.User
import com.uniguard.bustracker.core.domain.BaseUseCase
import com.uniguard.bustracker.core.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserByUidUseCase @Inject constructor(
    private val userRepository: UserRepository
) : BaseUseCase<String, Flow<User>>() {
    override suspend fun execute(params: String): Flow<User> {
        return userRepository.getUserByUid(params)
    }
} 