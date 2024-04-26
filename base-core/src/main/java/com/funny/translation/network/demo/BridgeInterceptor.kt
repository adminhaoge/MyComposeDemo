package com.funny.translation.network.demo

import okhttp3.Response

class BridgeInterceptor: Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        println("BridgeInterceptor = start")

        val response = chain.proceed(chain.request())

        println("BridgeInterceptor = end")

        return response
    }
}