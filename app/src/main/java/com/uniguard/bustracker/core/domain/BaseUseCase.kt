package com.uniguard.bustracker.core.domain

abstract class BaseUseCase<in Params, out T> {
    abstract suspend fun execute(params: Params): T
}