package com.funny.translation.network.demo

import okhttp3.Request
import okhttp3.Response

class HttpCacheInterceptor: Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        println("HttpCacheInterceptor = start")

        val response = chain.proceed(chain.request())

        println("HttpCacheInterceptor = end")

        return response
    }
}