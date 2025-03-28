package com.uniguard.bustracker.core.data.model.request

data class UserLocationRequest(
    val uid: String,
    val latitude: Double,
    val longitude: Double
)