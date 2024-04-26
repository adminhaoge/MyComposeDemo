package com.funny.translation.network.demo.normal


interface Monitor<T> {
    fun onResult(newResult: T, isLast: Boolean)
    fun onFailure(t: Throwable?)
    fun onCancel()
    fun onProgress(progress: Long, total: Long)
}