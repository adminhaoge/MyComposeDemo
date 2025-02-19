package com.funny.translation.network.x

data class ApiResponse<T>(
    val success: Boolean,
    val data: T?,
    val error: String?
)
